package com.Tender.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import com.Tender.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.Tender.Exception.TenderDocumentServiceException;
import com.Tender.dto.TenderDocumentRequestDTO;
import com.Tender.dto.TenderDocumentResponseDTO;
import com.Tender.entity.Tender;
import com.Tender.entity.TenderDocument;
import com.Tender.repository.TenderDocumentRepository;
import com.Tender.repository.TenderRepository;
import com.Tender.security.TenderVisibility;
import com.Tender.service.TenderDocumentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenderDocumentServiceImpl implements TenderDocumentService{
	
	private static final Logger logger = LoggerFactory.getLogger(TenderDocumentServiceImpl.class);
	
	@Autowired
	private TenderDocumentRepository tenderDocumentRepository;
	
	@Autowired
	private TenderRepository tenderRepository;
	
	@Autowired
	private TenderVisibility visibility;
	
	@Override
	public TenderDocumentResponseDTO createTenderDocument(TenderDocumentRequestDTO requestDto) {
		
		try {
			logger.info("creating a new document : {} ", requestDto.getTenderId() );
			if(requestDto.getTenderId() == null) {
				throw new IllegalArgumentException("Tender ID must not be null");
			}
			
			Tender tender = tenderRepository.findById(requestDto.getTenderId())
	                .orElseThrow(() -> new ResourceNotFoundException("Tender not found with ID: " + requestDto.getTenderId()));

	        TenderDocument document = TenderDocument.builder()
	                .fileName(requestDto.getFileName())
	                .fileType(requestDto.getFileType())
	                .fileUrl(requestDto.getFileUrl())
	                .tender(tender)
	                .build();

	        TenderDocument savedDocument = tenderDocumentRepository.save(document);
	        logger.info("Tender document created seccessfully" , savedDocument.getId());

	        return mapToResponse(savedDocument);
		} catch (Exception e) {
			
			logger.error("Error creating document", e);
			throw new TenderDocumentServiceException("Unable to create document, please try again",e);
		} 
    }
	
	
	private TenderDocumentResponseDTO mapToResponse(TenderDocument document) {
		return TenderDocumentResponseDTO.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .fileType(document.getFileType())
                .fileUrl(document.getFileUrl())
                .build();
	}

	
	@Override
	public List<TenderDocumentResponseDTO> getAllDocuments() {
		try {
			logger.info("Fetching all documents ....");
			List<TenderDocument> documents = tenderDocumentRepository.findAll().stream()
					.filter(d -> visibility.isVisible(d.getTender())).toList();
			
			if(documents.isEmpty()) {
				logger.warn("No documents found in the system");
			}
			return documents.stream().map(this::mapToResponse).toList();
		} catch (ResourceNotFoundException e) {
	        // Si un "not found" remonte, on la relance telle quelle (spécifique)
	        throw e;

	    } catch (DataAccessException e) {
	        // Si erreur BD
	        logger.error("Database error while fetching documents", e);
	        throw new TenderDocumentServiceException("Database error while fetching documents", e);

	    } catch (Exception e) {
	        logger.error("Unexpected error fetching all documents", e);
	        throw new TenderDocumentServiceException("Unexpected error while fetching documents", e);
	    }
	}


	@Override
	public TenderDocumentResponseDTO getDocumentById(Long id) {
		try {
			logger.info("FEtching document ");
			if(id == null || id <=0 ) {
				throw new IllegalArgumentException("Invalid ID provided");
			}
			TenderDocument document = tenderDocumentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Documents not found with id" + id));
			if (!visibility.isVisible(document.getTender())) {
				throw new ResourceNotFoundException("Documents not found with id" + id);
			}
			return mapToResponse(document);
		} catch (Exception e) {
			logger.error("Error fetching documents with ID: {} ", id , e);
			throw new TenderDocumentServiceException("Unable to retrieve documents with ID " +id, e);
		}
	}


	@Override
	public TenderDocumentResponseDTO updateDocument(Long id, TenderDocumentRequestDTO requestDTO) {
		try {
			logger.info("Updating documents with ID: {}", id);
			
			if(id == null || id <= 0) {
				throw new IllegalArgumentException("Invalid document ID");
			}
			
			if(requestDTO == null) {
				throw new IllegalArgumentException("Request body cannot  be null"); 
			}
			
			Tender tender = tenderRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Tender not found with id" + id));
			
			TenderDocument document = tenderDocumentRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Tender document not found with ID: " + requestDTO.getTenderId() ));
			
			document.setFileName(requestDTO.getFileName());
			document.setFileType(requestDTO.getFileType());
			document.setFileUrl(requestDTO.getFileUrl());
			document.setTender(tender);
			
			TenderDocument uddatedDocument = tenderDocumentRepository.save(document);
			
			logger.info("Tender document updated with successfully with ID: {}", uddatedDocument.getId());
			
			return mapToResponse(uddatedDocument);
		} catch (Exception e) {
			logger.error("Error updating tender document with ID: {}" , id, e);
			throw new TenderDocumentServiceException("Unable to update tender document with ID " + id, e);
		}
	}


	@Override
	public void deleteDocument(Long id) {
		try {
			logger.info("deleting document with id: {}" , id);
			if(id == null || id <= 0) {
				throw new IllegalArgumentException("Invalid ID provider for deletion");
			}
			
			TenderDocument document = tenderDocumentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("document not found with id" + id));
			tenderDocumentRepository.delete(document);
			
			logger.info("document deleted successfullu with ID: {} ", id);
		} catch (Exception e) {
			logger.error("Error deleting document with ID: {}", id , e);
			throw new TenderDocumentServiceException("Unable to delete document with ID " + id , e);
		}
		
	}
}
