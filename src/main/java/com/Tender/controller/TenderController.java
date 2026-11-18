package com.Tender.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Tender.dto.StatusDocumentCountDTO;
import com.Tender.dto.TenderRequestDTO;
import com.Tender.dto.TenderResponseDTO;
import com.Tender.service.TenderService;
import com.Tender.status.ApiResponse;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/tenders")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class TenderController {

	@Autowired
	private TenderService tenderService;
	private static final Logger logger = LoggerFactory.getLogger(TenderController.class);
	
	private static final String ERROR_RETRIEVING_TENDERS = "Error retrieving tenders ";
    private static final String FAILED_RETRIEVE_MESSAGE = "Failed to retrieve tenders.";
    private static final String RESULT_SEARCH = "result search";
    private static final String STATUS_OK = "OK";
	
	
	@PostMapping("/tender")
	public ResponseEntity<ApiResponse<TenderResponseDTO>> createTender(@RequestBody TenderRequestDTO requestDTO){
		try {
			if (requestDTO == null) {
	            ApiResponse<TenderResponseDTO> badRequest = ApiResponse.<TenderResponseDTO>builder()
	                    .status(HttpStatus.BAD_REQUEST.name())
	                    .message("Invalid tender request data.")
	                    .payload(null)
	                    .build();
	            return ResponseEntity.badRequest().body(badRequest);
	        }
			TenderResponseDTO created = tenderService.createTender(requestDTO);
			ApiResponse<TenderResponseDTO> response = ApiResponse.<TenderResponseDTO>builder()
					.status(HttpStatus.CREATED.name())
					.message("Tender created successfully")
					.payload(created)
					.build();
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}catch (Exception e) {
			logger.error("Error creating tender", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<TenderResponseDTO>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message("Failed to create Tender. Please contact support.")
					.payload(null)
					.build()
					);
		}
	}
	
	@GetMapping("/tenders")
	public ResponseEntity<ApiResponse<List<TenderResponseDTO>>> getAllTenders(){
		try {
			List<TenderResponseDTO> tenders = tenderService.getAllTenders();
			return ResponseEntity.ok(
					ApiResponse.<List<TenderResponseDTO>> builder()
					.status(HttpStatus.OK.name())
					.message("list of all tenders")
					.payload(tenders)
					.build()
					);
		}catch (Exception e) {
			logger.error(ERROR_RETRIEVING_TENDERS,  e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				ApiResponse.<List<TenderResponseDTO>>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message(FAILED_RETRIEVE_MESSAGE)
					.payload(null)
					.build()
					);
		}
	}
	
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<TenderResponseDTO>>> searchTenders(
	        @RequestParam(name = "title", required = false) String title,
	        @RequestParam(name= "status", required = false) String status) {
	    
		try {
			List<TenderResponseDTO> results = tenderService.searchTenders(title, status);
		    
		    return ResponseEntity.ok(
		        ApiResponse.<List<TenderResponseDTO>> builder()
		            .status(STATUS_OK)
		            .message(RESULT_SEARCH)
		            .payload(results)
		            .build()
		    ); 
		} catch (Exception e) {
			logger.error(ERROR_RETRIEVING_TENDERS,  e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				ApiResponse.<List<TenderResponseDTO>>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message(FAILED_RETRIEVE_MESSAGE)
					.payload(null)
					.build()
					);
		} 
	}
	
	@GetMapping("/search-by-date")
	public ResponseEntity<ApiResponse<List<TenderResponseDTO>>> searchByDate(
			@RequestParam(name = "start", required =  false) LocalDate start,
			@RequestParam(name = "end", required =  false) LocalDate end
			){
		
		try {
			List<TenderResponseDTO> results = tenderService.searchTendersByDateRange(start, end);
			
			return ResponseEntity.ok(
			        ApiResponse.<List<TenderResponseDTO>> builder()
			            .status(STATUS_OK)
			            .message(RESULT_SEARCH)
			            .payload(results)
			            .build()
			    ); 
		} catch (Exception e) {
			logger.error(ERROR_RETRIEVING_TENDERS,  e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				ApiResponse.<List<TenderResponseDTO>>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message(FAILED_RETRIEVE_MESSAGE)
					.payload(null)
					.build()
					);
		}
	}
	
	@GetMapping("/ceraed-By")
	public ResponseEntity<ApiResponse<List<TenderResponseDTO>>> searchByCreatedBy(
			@RequestParam(name = "createdBy", required =  false) String createdBy){
		
		try {
			List<TenderResponseDTO> results = tenderService.searchTendersByCreatedBy(createdBy);
			return ResponseEntity.ok(
			        ApiResponse.<List<TenderResponseDTO>> builder()
			            .status(STATUS_OK)
			            .message(RESULT_SEARCH)
			            .payload(results)
			            .build()
			    ); 
		} catch (Exception e) {
			logger.error(ERROR_RETRIEVING_TENDERS,  e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				ApiResponse.<List<TenderResponseDTO>>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message(FAILED_RETRIEVE_MESSAGE)
					.payload(null)
					.build()
					);
		}
	}
	
	@GetMapping("/status-name")
	public ResponseEntity<ApiResponse<List<TenderResponseDTO>>> searchByStatusName(
			@RequestParam(name = "statusName", required =  false) String statusName){
		
		try {
			List<TenderResponseDTO> results = tenderService.searchTenderWithdocumentAnbdStatus(statusName);
			return ResponseEntity.ok(
			        ApiResponse.<List<TenderResponseDTO>> builder()
			            .status(STATUS_OK)
			            .message(RESULT_SEARCH)
			            .payload(results)
			            .build()
			    ); 
		} catch (Exception e) {
			logger.error(ERROR_RETRIEVING_TENDERS,  e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				ApiResponse.<List<TenderResponseDTO>>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message(FAILED_RETRIEVE_MESSAGE)
					.payload(null)
					.build()
					);
		}
	}
	
	@GetMapping("/status")
	public ResponseEntity<ApiResponse<List<TenderResponseDTO>>> searchByStatus(@RequestParam(name="statusName" , required = false) String statusName){
		try {
			List<TenderResponseDTO> results = tenderService.searchTenderWithStatus(statusName);
			return ResponseEntity.ok(
			        ApiResponse.<List<TenderResponseDTO>> builder()
		            .status(STATUS_OK)
		            .message(RESULT_SEARCH)
		            .payload(results)
		            .build()
		    ); 
		} catch (Exception e) {
			logger.error(ERROR_RETRIEVING_TENDERS,  e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				ApiResponse.<List<TenderResponseDTO>>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message(FAILED_RETRIEVE_MESSAGE)
					.payload(null)
					.build()
					);
		}
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<TenderResponseDTO>> getTenderByID(@PathVariable("id") Long id) {
		try {
			TenderResponseDTO tender = tenderService.getById(id);
			return ResponseEntity.ok(
					ApiResponse.<TenderResponseDTO> builder()
					.status(HttpStatus.OK.name())
					.message("Tender")
					.payload(tender)
					.build()
					);	
		} catch (Exception e) {
			logger.error("Error fetching tender by id: {} ", id,  e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				ApiResponse.<TenderResponseDTO>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message("Tender not found with ID " + id)
					.payload(null)
					.build()
					);
		}
	}
	
	
	
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<TenderResponseDTO>> updateTender(@PathVariable("id") Long id, @RequestBody TenderRequestDTO requestDTO) {
		try {
			TenderResponseDTO updated = tenderService.updateTender(id, requestDTO);
			return ResponseEntity.ok(
				ApiResponse.<TenderResponseDTO> builder()
					.status(HttpStatus.OK.name())
					.message("Tender updated succeefully")
					.payload(updated)
					.build()
					);	
		} catch (Exception e) {
			logger.error("Error fetching tender by id: {} ", id,  e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					ApiResponse.<TenderResponseDTO>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message("Failed to update Tender with ID " + id)
					.payload(null)
					.build()
					);	
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> deleteTender(@PathVariable("id") Long id){
		try {
			tenderService.deleteTender(id);
			return ResponseEntity.ok(
					ApiResponse.<String> builder()
						.status(HttpStatus.OK.name())
						.message("Tender deleted succeefully")
						.payload(null)
						.build()
						);	
		} catch (Exception e) {
			logger.error("Error deleting tender with ID: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					ApiResponse.<String>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message("Failed to delete Tender with ID " + id)
					.payload(null)
					.build()
			);	
		}
	}
	
	@GetMapping("/status_docuemnts_count")
	public ResponseEntity<ApiResponse<List<StatusDocumentCountDTO>>> countDocumentByStatus(){
		try {
			List<StatusDocumentCountDTO> results =  tenderService.countDocumentByStatus();
			return ResponseEntity.ok(
			        ApiResponse.<List<StatusDocumentCountDTO>> builder()
			            .status(STATUS_OK)
			            .message(RESULT_SEARCH)
			            .payload(results)
			            .build()
			    ); 
		} catch (Exception e) {
			logger.error(ERROR_RETRIEVING_TENDERS,  e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				ApiResponse.<List<StatusDocumentCountDTO>>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message(FAILED_RETRIEVE_MESSAGE)
					.payload(null)
					.build()
					);
		}
	}
	
	

	
	
}
