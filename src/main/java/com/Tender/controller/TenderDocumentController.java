package com.Tender.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Tender.dto.TenderDocumentRequestDTO;
import com.Tender.dto.TenderDocumentResponseDTO;
import com.Tender.dto.TenderResponseDTO;
import com.Tender.service.TenderDocumentService;
import com.Tender.status.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class TenderDocumentController {
	
	@Autowired
	private TenderDocumentService tenderDocumentService;
	private static final Logger logger = LoggerFactory.getLogger(TenderDocumentController.class);
	
	@PostMapping
    public ResponseEntity<ApiResponse<TenderDocumentResponseDTO>> createTenderDocument(
            @RequestBody TenderDocumentRequestDTO requestDTO) {
		
		try {
			if(requestDTO == null) {
				ApiResponse<TenderDocumentResponseDTO> badRequest = ApiResponse.<TenderDocumentResponseDTO>builder()
						.status(HttpStatus.BAD_REQUEST.name())
	                    .message("Invalid tender document request data.")
	                    .payload(null)
	                    .build();
				return ResponseEntity.badRequest().body(badRequest);
			}
			
			TenderDocumentResponseDTO responseDTO = tenderDocumentService.createTenderDocument(requestDTO);
			ApiResponse<TenderDocumentResponseDTO> response = ApiResponse.<TenderDocumentResponseDTO>builder()
					.status(HttpStatus.CREATED.name())
                    .message("tender document created successfully.")
                    .payload(responseDTO)
                    .build();
	        return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			logger.error("Error creating tender document", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<TenderDocumentResponseDTO>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message("Failed to create Tender document. Please contact support.")
					.payload(null)
					.build()
					);
		}
    }
	
	
	@GetMapping
	public ResponseEntity<ApiResponse<List<TenderDocumentResponseDTO>>> getAllDocuments(){
		
		try {
			List<TenderDocumentResponseDTO> documents = tenderDocumentService.getAllDocuments();
			return ResponseEntity.ok(
					ApiResponse.<List<TenderDocumentResponseDTO>> builder()
					.status(HttpStatus.OK.name())
					.message("list of all documents")
					.payload(documents)
					.build()
					);
		} catch (Exception e) {
			logger.error("ERROR_RETRIEVING_TENDERS_DOCUMENTS " , e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					ApiResponse.<List<TenderDocumentResponseDTO>> builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message("list of all documents")
					.payload(null)
					.build()
					);
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<TenderDocumentResponseDTO>>  getDocumentsbyId (@PathVariable("id") Long id) {
		
		try {
			TenderDocumentResponseDTO documents = tenderDocumentService.getDocumentById(id);
			return ResponseEntity.ok(
					ApiResponse.<TenderDocumentResponseDTO> builder()
					.status(HttpStatus.OK.name())
					.message("documents")
					.payload(documents)
					.build()
					);
		} catch (Exception e) {
			logger.error("Error fetching documents " , e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					ApiResponse.<TenderDocumentResponseDTO> builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message("document not found")
					.payload(null)
					.build()
					);
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<TenderDocumentResponseDTO>> updatedocument(@PathVariable("id") Long id , @RequestBody TenderDocumentRequestDTO requestDTO){
		
		try {
			TenderDocumentResponseDTO updated = tenderDocumentService.updateDocument(id, requestDTO);
			
			return ResponseEntity.ok(
					ApiResponse.<TenderDocumentResponseDTO> builder()
						.status(HttpStatus.OK.name())
						.message("Tender document updated succeefully")
						.payload(updated)
						.build()
						);	
		} catch (Exception e) {
			logger.error("Error fetching document by id: {} ", id,  e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					ApiResponse.<TenderDocumentResponseDTO>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message("Failed to update document with ID " + id)
					.payload(null)
					.build()
					);	
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> deleteDocument(@PathVariable("id") Long id){
		try {
			tenderDocumentService.deleteDocument(id);
			return ResponseEntity.ok(
					ApiResponse.<String> builder()
						.status(HttpStatus.OK.name())
						.message("Tender deleted succeefully")
						.payload(null)
						.build()
						);	
		} catch (Exception e) {
			logger.error("Error deleting document with ID: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					ApiResponse.<String>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message("Failed to delete document with ID " + id)
					.payload(null)
					.build()
			);	
		}
	}
	
	

}
