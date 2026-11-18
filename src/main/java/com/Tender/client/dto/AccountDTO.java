package com.Tender.client.dto;

import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

/** Subset of IdentityService's AccountResponseDTO that TenderFlow needs. */
@Data
@NoArgsConstructor
public class AccountDTO {

	private Long id;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String status;
	private Set<String> roles;
	private Set<String> permissions;

	public boolean hasPermission(String permission) {
		return permissions != null && permissions.contains(permission);
	}

	public boolean hasAnyPermission(String... wanted) {
		for (String p : wanted) {
			if (hasPermission(p)) {
				return true;
			}
		}
		return false;
	}
}
