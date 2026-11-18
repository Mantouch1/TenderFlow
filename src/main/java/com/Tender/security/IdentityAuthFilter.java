package com.Tender.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Tender.client.IdentityClient;
import com.Tender.client.dto.AccountDTO;
import com.Tender.status.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * For every request: ask IdentityService (through Feign) who the caller is, then check that the
 * account may perform this HTTP method on this path.
 * Missing / invalid token -> 401, disabled account or missing permission -> 403,
 * IdentityService down -> 503.
 */
@Component
@RequiredArgsConstructor
public class IdentityAuthFilter extends OncePerRequestFilter {

	private final IdentityClient identityClient;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			reject(response, 401, "Missing bearer token");
			return;
		}
		AccountDTO account;
		try {
			account = identityClient.getCurrentAccount(authorization);
		} catch (FeignException e) {
			if (e.status() == 401 || e.status() == 403) {
				reject(response, 401, "Invalid or expired token");
			} else {
				logger.error("IdentityService call failed (status " + e.status() + ")", e);
				reject(response, 503, "Identity service unavailable");
			}
			return;
		}
		if (!"ACTIVE".equals(account.getStatus())) {
			reject(response, 403, "Account is not active");
			return;
		}
		if (!isAllowed(account, request.getMethod(), request.getRequestURI())) {
			reject(response, 403, "Access denied: missing permission");
			return;
		}
		request.setAttribute(CurrentAccount.ATTRIBUTE, account);
		chain.doFilter(request, response);
	}

	/**
	 * Permission matrix.
	 *  - read (GET/HEAD/OPTIONS) anywhere: TENDER_READ or TENDER_READ_ALL (what is visible is decided in the services)
	 *  - /api/statuses  create/update/delete: TENDER_STATUS_MANAGE
	 *  - other create/update (POST, PUT, PATCH): TENDER_WRITE
	 *  - other DELETE: TENDER_DELETE
	 */
	private boolean isAllowed(AccountDTO account, String method, String path) {
		if (HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method) || HttpMethod.OPTIONS.matches(method)) {
			return account.hasAnyPermission("TENDER_READ", "TENDER_READ_ALL");
		}
		if (path.startsWith("/api/statuses")) {
			return account.hasPermission("TENDER_STATUS_MANAGE");
		}
		if (HttpMethod.DELETE.matches(method)) {
			return account.hasPermission("TENDER_DELETE");
		}
		return account.hasPermission("TENDER_WRITE");
	}

	private void reject(HttpServletResponse response, int status, String message) throws IOException {
		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getWriter(),
				ApiResponse.builder().status("error").message(message).build());
	}
}
