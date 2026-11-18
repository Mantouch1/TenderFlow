package com.Tender.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenderResponseDTO {
	
	private Long id;
	private String reference;
	private String title;
	private String description;
	private String statusLabel;
	private LocalDate publicationDate;
	private LocalDate deadlineDate;
	private String createdBy;
	private List<String> documentNames;

}
