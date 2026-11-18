# TenderFlow <-> IdentityService (Feign) and access rules

Every request needs `Authorization: Bearer <accessToken>` issued by IdentityService (`POST /api/auth/login`).
`IdentityAuthFilter` calls IdentityService through the Feign client `IdentityClient`
(`GET {identity.service.url}/api/accounts/me`) and applies the rules below.

| Action | Required permission |
|---|---|
| GET (tenders, documents, statuses) | TENDER_READ or TENDER_READ_ALL |
| POST / PUT tenders and documents | TENDER_WRITE |
| DELETE tenders and documents | TENDER_DELETE |
| POST / PUT / DELETE `/api/statuses` | TENDER_STATUS_MANAGE |

Visibility: with `TENDER_READ_ALL` (ADMIN, MANAGER, EDITOR, AUDITOR) all tenders are visible; otherwise (USER) only tenders
whose status equals `tenderflow.open-status-name` (default `OPEN`, check table `tender_status`).
The tender author (`createdBy`) is the authenticated account's email, not a value sent by the client.

Errors: 401 no/invalid token, 403 disabled account or missing permission, 503 IdentityService unreachable.

Configuration: `IDENTITY_SERVICE_URL` (default http://localhost:8082), `TENDERFLOW_OPEN_STATUS_NAME`, `SPRING_DATASOURCE_PASSWORD` (required).
