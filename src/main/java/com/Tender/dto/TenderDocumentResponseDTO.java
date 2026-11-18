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
public class TenderDocumentResponseDTO {
	
	private Long id;
	private String fileName;
	private String fileType;
	private String fileUrl;

}
