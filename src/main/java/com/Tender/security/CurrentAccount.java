package com.Tender.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.Tender.client.dto.AccountDTO;

/** Gives services access to the account resolved by IdentityAuthFilter for the current request. */
@Component
public class CurrentAccount {

	static final String ATTRIBUTE = CurrentAccount.class.getName() + ".ACCOUNT";

	public AccountDTO get() {
		RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
		Object account = attrs == null ? null : attrs.getAttribute(ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
		if (account instanceof AccountDTO dto) {
			return dto;
		}
		throw new IllegalStateException("No authenticated account on this request");
	}
}
