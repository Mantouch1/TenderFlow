package com.Tender.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.Tender.entity.Tender;

/**
 * Which tenders can the current account see?
 * - permission TENDER_READ_ALL (ADMIN, MANAGER, EDITOR, AUDITOR...): every tender
 * - otherwise (USER): only tenders whose status is the "open" status
 */
@Component
public class TenderVisibility {

	public static final String READ_ALL = "TENDER_READ_ALL";

	@Autowired
	private CurrentAccount currentAccount;

	@Value("${tenderflow.open-status-name:OPEN}")
	private String openStatusName;

	public boolean canReadAll() {
		return currentAccount.get().hasPermission(READ_ALL);
	}

	public boolean isOpenStatusName(String statusName) {
		return statusName != null && openStatusName.equalsIgnoreCase(statusName);
	}

	public boolean isVisible(Tender tender) {
		if (canReadAll()) {
			return true;
		}
		return tender != null && tender.getStatus() != null && isOpenStatusName(tender.getStatus().getName());
	}

	public List<Tender> filter(List<Tender> tenders) {
		if (canReadAll()) {
			return tenders;
		}
		return tenders.stream().filter(this::isVisible).toList();
	}
}
