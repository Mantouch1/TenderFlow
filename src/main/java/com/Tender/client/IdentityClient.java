package com.Tender.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.Tender.client.dto.AccountDTO;

/**
 * Declarative HTTP client: Feign generates the implementation at startup.
 * getCurrentAccount(...) sends  GET {identity.service.url}/api/accounts/me
 * with the user's JWT and maps the JSON answer to AccountDTO.
 */
@FeignClient(name = "identity-service", url = "${identity.service.url}")
public interface IdentityClient {

	@GetMapping("/api/accounts/me")
	AccountDTO getCurrentAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization);
}
