package com.Tender.service;

import java.time.LocalDate;
import java.util.List;

import com.Tender.dto.StatusDocumentCountDTO;
import com.Tender.dto.TenderRequestDTO;
import com.Tender.dto.TenderResponseDTO;

public interface TenderService {
	
	TenderResponseDTO createTender(TenderRequestDTO requestDTO);
	List<TenderResponseDTO> getAllTenders();
	TenderResponseDTO getById(Long id);
	TenderResponseDTO updateTender(Long id, TenderRequestDTO requestDTO);
	void deleteTender(Long id);
	
	
	List<TenderResponseDTO> searchTenders(String title, String statusName);
	List<TenderResponseDTO> searchTendersByDateRange(LocalDate start, LocalDate end);
	List<TenderResponseDTO> searchTendersByCreatedBy(String createdBy);
	List<TenderResponseDTO> searchTenderWithdocumentAnbdStatus(String statusName);
	List<TenderResponseDTO> searchTenderWithStatus(String statusName);
	List<StatusDocumentCountDTO> countDocumentByStatus();
	
	

}
