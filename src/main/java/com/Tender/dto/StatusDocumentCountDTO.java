package com.Tender.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusDocumentCountDTO {
	
	private String statusName;
	private Long documentCount;

}
