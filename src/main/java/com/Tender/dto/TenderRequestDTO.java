package com.Tender.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenderRequestDTO {
	
	private String Reference;
	private String title;
	private String description;
	private String statusLabel;
	private LocalDate publicationDate;
	private LocalDate deadlineDate;
	private String createdBy;
	

}
