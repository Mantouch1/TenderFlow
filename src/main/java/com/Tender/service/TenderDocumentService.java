package com.Tender.service;

import java.util.List;

import com.Tender.dto.TenderDocumentRequestDTO;
import com.Tender.dto.TenderDocumentResponseDTO;

public interface TenderDocumentService {
	
	TenderDocumentResponseDTO createTenderDocument(TenderDocumentRequestDTO requestDto);
	List<TenderDocumentResponseDTO> getAllDocuments();
	TenderDocumentResponseDTO getDocumentById(Long id);
	TenderDocumentResponseDTO updateDocument(Long id, TenderDocumentRequestDTO requestDTO);
	void deleteDocument(Long id);

}
