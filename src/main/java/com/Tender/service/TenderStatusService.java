package com.Tender.service;

import java.util.List;

import com.Tender.dto.TenderStatusRequestDTO;
import com.Tender.dto.TenderStatusResponseDTO;

public interface TenderStatusService {
	
	TenderStatusResponseDTO createStatus(TenderStatusRequestDTO requestDTO);
	List<TenderStatusResponseDTO> getAllStatuses();
	TenderStatusResponseDTO getStatusById(Long id);
	TenderStatusResponseDTO updateStatus(Long id, TenderStatusRequestDTO requestDto);
	void deleteStatus(Long id);
	

}
