package com.Tender.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.Tender.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.Tender.Exception.TenderDocumentServiceException;
import com.Tender.dto.StatusDocumentCountDTO;
import com.Tender.dto.TenderRequestDTO;
import com.Tender.dto.TenderResponseDTO;
import com.Tender.entity.Tender;
import com.Tender.entity.TenderDocument;
import com.Tender.entity.TenderStatus;
import com.Tender.repository.TenderRepository;
import com.Tender.repository.TenderStatusRepository;
import com.Tender.security.CurrentAccount;
import com.Tender.security.TenderVisibility;
import com.Tender.service.TenderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenderServiceImpl implements TenderService{
	
	private static final Logger logger = LoggerFactory.getLogger(TenderServiceImpl.class);
	
	@Autowired
	private TenderRepository tenderRepository;
	
	@Autowired
	TenderStatusRepository tenderStatusRepository;
	
	@Autowired
	private CurrentAccount currentAccount;
	
	@Autowired
	private TenderVisibility visibility;
	
	@Override
	public TenderResponseDTO createTender(TenderRequestDTO requestDTO) {
	    try {
	        logger.info("Creating a new Tender with Reference: {}", requestDTO.getReference());

	        if (requestDTO == null) {
	            throw new IllegalArgumentException("Request body cannot be null");
	        }

	        if (requestDTO.getStatusLabel() == null || requestDTO.getStatusLabel().trim().isEmpty()) {
	            throw new IllegalArgumentException("Status label must not be null or empty");
	        }

	        TenderStatus status = tenderStatusRepository.findByName(requestDTO.getStatusLabel())
	                .orElseThrow(() -> new ResourceNotFoundException(
	                        "Tender status not found with label: " + requestDTO.getStatusLabel()));

	        Tender tender = Tender.builder()
	                .reference(requestDTO.getReference())
	                .title(requestDTO.getTitle())
	                .description(requestDTO.getDescription())
	                .publicationDate(requestDTO.getPublicationDate())
	                .deadlineDate(requestDTO.getDeadlineDate()) // ⚠️ Attention à l’orthographe de la propriété dans ton DTO
	                .createdBy(currentAccount.get().getEmail()) // author = authenticated account, never trusted from the client
	                .status(status)
	                .build();

	        Tender saved = tenderRepository.save(tender);

	        logger.info("Tender created successfully with ID: {}", saved.getId());

	        return mapToResponse(saved);

	    } catch (Exception e) {
	        logger.error("Error creating tender", e);
	        throw new TenderDocumentServiceException("Unable to create tender. Please try again.", e);
	    }
	}
	
	private TenderResponseDTO mapToResponse(Tender tender) {
		return TenderResponseDTO.builder()
				.id(tender.getId())
				.reference(tender.getReference())
				.title(tender.getTitle())
				.description(tender.getDescription())
				.publicationDate(tender.getPublicationDate())
				.deadlineDate(tender.getDeadlineDate())
				.createdBy(tender.getCreatedBy())
				.statusLabel(tender.getStatus() != null ? tender.getStatus().getName() : null)
				.documentNames(
						tender.getDocuments() != null ?
								tender.getDocuments().stream()
								.map(TenderDocument :: getFileName)
								.toList(): null)
				.build();
	}

	@Override
	public List<TenderResponseDTO> getAllTenders() {
		try {
			logger.info("Fetching all tenders ....");
			List<Tender> tenders = visibility.filter(tenderRepository.findAll());
			
			if(tenders.isEmpty()) {
				logger.warn("No tenders found in the system");
			}
			return tenders.stream().map(this::mapToResponse).toList();
		} catch (Exception e) {
			logger.error("Error fetching all tenders" , e);
			throw new TenderDocumentServiceException("Unable to fetch tenders at this time.", e);
		}
	}

	@Override
	public TenderResponseDTO getById(Long id) {
		try {
			logger.info("Fetching tender with ID: {} ", id);
			
			if(id == null || id <= 0) {
				throw new IllegalArgumentException("Invalid ID provided");
			}
			Tender tender = tenderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tender not found with id " + id));
			if (!visibility.isVisible(tender)) {
				// same answer as "does not exist": do not reveal closed tenders to restricted accounts
				throw new ResourceNotFoundException("Tender not found with id " + id);
			}
			return mapToResponse(tender);
		} catch (Exception e) {
			logger.error("Error fetching tender with ID: {}" , id, e);
			throw new TenderDocumentServiceException("Unable to retrieve tender with ID " + id, e);
		}
	}
	
	
	
	@Override
	public TenderResponseDTO updateTender(Long id, TenderRequestDTO requestDTO) {
		
		try {
			logger.info("Updating tender with ID: {} ", id);
			if(id == null || id <= 0) {
				throw new IllegalArgumentException("Invalid tender ID");
			}
			if(requestDTO == null) {
				throw new IllegalArgumentException("Request body cannot be null");
			}
			Tender tender = tenderRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Tender not found with id" + id));
			
			TenderStatus status = tenderStatusRepository.findByName(requestDTO.getStatusLabel())
	                .orElseThrow(() -> new ResourceNotFoundException(
	                        "Tender status not found with label: " + requestDTO.getStatusLabel()));
			
			tender.setReference(requestDTO.getReference());
			tender.setTitle(requestDTO.getTitle());
			tender.setDescription(requestDTO.getDescription());
			tender.setPublicationDate(requestDTO.getPublicationDate());
			tender.setDeadlineDate(requestDTO.getDeadlineDate());
			tender.setStatus(status);
			
			Tender updetedTender = tenderRepository.save(tender);
			
			logger.info("Tender updated successfully with ID: {}", updetedTender.getId());
			
			return mapToResponse(updetedTender);
		} catch (Exception e) {
			logger.error("Error updating tender with ID: {}" , id, e);
			throw new TenderDocumentServiceException("Unable to update tender with ID " + id, e);
		}
	}

	@Override
	public void deleteTender(Long id) {
		try {
			logger.info("Deleting tender with ID: {}" , id);
			if(id == null || id <= 0) {
				throw new IllegalArgumentException("Invalid ID provider for deletion");
			}
			Tender tender = tenderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tender not found with id" + id));
			tenderRepository.delete(tender);
			
			logger.info("Tender deleted successfully with ID: {} ", id);
		} catch (Exception e) {
			logger.error("Error deleting tender with ID: {}", id , e);
			throw new TenderDocumentServiceException("Unable to delete tender with ID " + id, e);
		}
	}

	@Override
	public List<TenderResponseDTO> searchTenders(String title, String statusName) {
	
		try {
			logger.info("search de tender with title='{}' and status='{}'", title, statusName);
			if(title == null && statusName == null) {
				throw new IllegalArgumentException("invalid title or status");
			}
			List<Tender> tenders = visibility.filter(tenderRepository.searchByTitleAndStatus(title, statusName));
			
			List<TenderResponseDTO> response = tenders.stream().map(this::mapToResponse).toList();
			 
			logger.info("Tender searching with successfully : {} result find", response.size());
			
			return response;
		} catch (Exception e) {
			logger.error("error searching tender with title = '{}' and status= '{}' ", title, statusName, e);
			throw new TenderDocumentServiceException("Unable to fetching with title and status " , e );
		}
	}
	
	@Override
	public List<TenderResponseDTO> searchTendersByDateRange(LocalDate start, LocalDate end) {
		
		try {
			logger.info("search tender with start= '{}' and end='{}' " , start, end);
			if(start == null || end == null) {
				throw new IllegalArgumentException("invalid start date or end date ");
			}
													
			List<Tender> tenders = visibility.filter(tenderRepository.findByPublicationDateBetween(start, end));
			List<TenderResponseDTO> response = tenders.stream().map(this::mapToResponse).toList();
			
			logger.info("Tender searching with successfully : {} result find", response.size());
			return response;
		} catch (Exception e) {
			logger.error("error searching tender with start date = '{}' and end date= '{}' ", start, end, e);
			throw new TenderDocumentServiceException("Unable to fetching with start date and end date " , e );
		}
	}

	@Override
	public List<TenderResponseDTO> searchTendersByCreatedBy(String createdBy) {
	
		try {
			logger.info("search tender with createdBy = '{}'", createdBy);
			if(createdBy == null || createdBy.isEmpty()) {
				throw new IllegalArgumentException("created by must nut null or blank");
			}
			List<Tender> tenders = visibility.filter(tenderRepository.findByCreatedBy(createdBy));
			List<TenderResponseDTO> response = tenders.stream().map(this::mapToResponse).toList();
			
			logger.info("Tender searching with successfyly :{} result find" , response.size());
			return response;
		} catch (Exception e) {
			logger.error("error searching tender with createdBy  = '{}' ", createdBy,  e);
			throw new TenderDocumentServiceException("Unable to fetching with createdBy " , e );
		}	
	}

	@Override
	public List<TenderResponseDTO> searchTenderWithdocumentAnbdStatus(String statusName) {
		try {
			logger.info("search tender with statusName = '{}'", statusName);
			
			List<Tender> tenders = visibility.filter(tenderRepository.findTendersWithDocumentsByStatus(statusName));
			List<TenderResponseDTO> response = tenders.stream().map(this::mapToResponse).toList();
			
			logger.info("Tender searching with successfyly :{} result find" , response.size());
			return response;
			
		} catch (Exception e) {
			logger.error("error searching tender with statusName  = '{}' ", statusName,  e);
			throw new TenderDocumentServiceException("Unable to fetching with statusName " , e );
		}
	}

	@Override
	public List<TenderResponseDTO> searchTenderWithStatus(String statusName) {
		try {
			logger.info("search tender with statusName = '{}' ", statusName);
			
			List<Tender> tenders = visibility.filter(tenderRepository.findTendersByStatus(statusName));
			List<TenderResponseDTO> response = tenders.stream().map(this::mapToResponse).toList();
			
			logger.info("Tender searching with successfuly : {} result find ", response.size());
			return response;
		} catch (Exception e) {
			logger.info("error searching with statusName = '{}' ", statusName, e);
			throw new TenderDocumentServiceException("unable to fetching with statusName", e);
		}
	}

	@Override
	public List<StatusDocumentCountDTO> countDocumentByStatus() {
		List<StatusDocumentCountDTO> counts = tenderRepository.countDucumentByStatus();
		if (visibility.canReadAll()) {
			return counts;
		}
		return counts.stream().filter(c -> visibility.isOpenStatusName(c.getStatusName())).toList();
	}

}
