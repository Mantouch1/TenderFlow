package com.Tender.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import com.Tender.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.Tender.Exception.TenderDocumentServiceException;
import com.Tender.dto.TenderStatusRequestDTO;
import com.Tender.dto.TenderStatusResponseDTO;
import com.Tender.entity.TenderStatus;
import com.Tender.repository.TenderRepository;
import com.Tender.repository.TenderStatusRepository;
import com.Tender.service.TenderStatusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenderStatusServiceImpl implements TenderStatusService{
	
	private static final Logger logger = LoggerFactory.getLogger(TenderStatusServiceImpl.class);
	
	@Autowired
	private TenderStatusRepository tenderStatusRepository;
	
	@Autowired
	private TenderRepository tenderRepository;

	@Override
	public TenderStatusResponseDTO createStatus(TenderStatusRequestDTO requestDTO) {
		try {
			logger.info("Creating a new Tender with Reference: {}" , requestDTO.getName());
			
			if(requestDTO.getName() == null || requestDTO.getName().isEmpty()) {
				throw new IllegalArgumentException("name  must not be null");
			}
			
			
			TenderStatus status = TenderStatus.builder()
					.name(requestDTO.getName())
					.build();
			TenderStatus saved = tenderStatusRepository.save(status);
			
			logger.info("Tender created successfully with ID: {} ", saved.getId());
			return mapToResponse(saved);
		} catch (Exception e) {
			logger.error("Error creating tender" , e);
			throw new TenderDocumentServiceException("Unable to create tender. please try again ", e);
		}
	}
	
	private TenderStatusResponseDTO mapToResponse(TenderStatus status) {
		return TenderStatusResponseDTO.builder()
				.id(status.getId())
				.name(status.getName())
				.build();
	}

	@Override
	public List<TenderStatusResponseDTO> getAllStatuses() {
		try {
			logger.info("Fetching all status ....");
			List<TenderStatus> status = tenderStatusRepository.findAll();
			
			if(status.isEmpty()) {
				logger.warn("No status found in the system");
			}
			return status.stream().map(this::mapToResponse).toList();
		} catch (Exception e) {
			logger.error("Error fetching all status" , e);
			throw new TenderDocumentServiceException("Unable to fetch status at this time.", e);
		}
	}

	@Override
	public TenderStatusResponseDTO getStatusById(Long id) {
		try {
			logger.info("Fetching status with ID: {} ", id);
			
			if(id == null || id <= 0) {
				throw new IllegalArgumentException("Invalid ID provided");
			}
			TenderStatus status = tenderStatusRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("status not found with id " + id));
			return mapToResponse(status);
		} catch (Exception e) {
			logger.error("Error fetching status with ID: {}" , id, e);
			throw new TenderDocumentServiceException("Unable to retrieve status with ID " + id, e);
		}
	}

	@Override
	public TenderStatusResponseDTO updateStatus(Long id, TenderStatusRequestDTO requestDto) {
		try {
			logger.info("Updating status with ID: {} ", id);
			if(id == null || id <= 0 ) {
				throw new IllegalArgumentException("Invalid Status ID");
			}
			if(requestDto == null) {
				throw new IllegalArgumentException("Request body cannot be null");
			}
			
			TenderStatus status = tenderStatusRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("status not found with ID: " + id));
			
			status.setName(requestDto.getName());
			
			TenderStatus updated = tenderStatusRepository.save(status);
			
			logger.info("Status updated successfully with ID: {}", updated.getId());
			return mapToResponse(updated);
		} catch (Exception e) {
			logger.info("Error updating Status with ID: {} ", id , e);
			throw new TenderDocumentServiceException("Unable to update status with ID " + id, e);
		}
	}
	
	@Override
	public void deleteStatus(Long id) {
	    try {
	        logger.info("Deleting status with ID: {}", id);

	        if (id == null || id <= 0) {
	            throw new IllegalArgumentException("Invalid ID provided for deletion");
	        }

	        TenderStatus status = tenderStatusRepository.findById(id)
	                .orElseThrow(() -> new ResourceNotFoundException("Status not found with ID: " + id));

	        // Vérification si le status est utilisé par des tenders
	        boolean isUsed = tenderRepository.existsByStatus(status);
	        if (isUsed) {
	            throw new IllegalStateException("Cannot delete status because it is still in use by one or more tenders.");
	        }

	        tenderStatusRepository.delete(status);
	        logger.info("Status deleted successfully with ID: {}", id);

	    } catch (IllegalStateException | IllegalArgumentException e) {
	        throw e; // Laisse le controller gérer proprement
	    } catch (DataIntegrityViolationException e) {
	        logger.error("Foreign key constraint violation: status ID {} is in use", id, e);
	        throw new IllegalStateException("Cannot delete status because it is still referenced.");
	    } catch (Exception e) {
	        logger.error("Unexpected error while deleting status with ID: {}", id, e);
	        throw new TenderDocumentServiceException("Unable to delete status with ID " + id, e);
	    }
	}

}
