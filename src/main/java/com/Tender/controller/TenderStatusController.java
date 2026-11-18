package com.Tender.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.Tender.Exception.ResourceNotFoundException;
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
import org.springframework.web.bind.annotation.RestController;

import com.Tender.dto.TenderResponseDTO;
import com.Tender.dto.TenderStatusRequestDTO;
import com.Tender.dto.TenderStatusResponseDTO;
import com.Tender.service.TenderService;
import com.Tender.service.TenderStatusService;
import com.Tender.status.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/statuses")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class TenderStatusController {
	
	private static final Logger logger = LoggerFactory.getLogger(TenderStatusController.class);
	
	@Autowired
	private TenderStatusService tenderStatusService;
	
	@PostMapping
    public ResponseEntity<ApiResponse<TenderStatusResponseDTO>> createStatus(
            @RequestBody TenderStatusRequestDTO requestDTO) {
        try {
            logger.info("Received request to create TenderStatus: {}", requestDTO);
            if (requestDTO == null || requestDTO.getName() == null || requestDTO.getName().trim().isEmpty()) {
                ApiResponse<TenderStatusResponseDTO> badRequest = ApiResponse.<TenderStatusResponseDTO>builder()
                        .status(HttpStatus.BAD_REQUEST.name())
                        .message("Invalid TenderStatus data")
                        .payload(null)
                        .build();
                return ResponseEntity.badRequest().body(badRequest);
            }
            TenderStatusResponseDTO created = tenderStatusService.createStatus(requestDTO);
            ApiResponse<TenderStatusResponseDTO> response = ApiResponse.<TenderStatusResponseDTO>builder()
                    .status(HttpStatus.CREATED.name())
                    .message("Tender Status created successfully")
                    .payload(created)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Error while creating TenderStatus", e);

            ApiResponse<TenderStatusResponseDTO> errorResponse = ApiResponse.<TenderStatusResponseDTO>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                    .message("Failed to create TenderStatus. Please contact support.")
                    .payload(null)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
	
	@GetMapping
	public ResponseEntity<ApiResponse<List<TenderStatusResponseDTO>>> getAllStatus(){
		try {
			List<TenderStatusResponseDTO> status = tenderStatusService.getAllStatuses();
			return ResponseEntity.ok(
					ApiResponse.<List<TenderStatusResponseDTO>> builder()
					.status(HttpStatus.OK.name())
					.message("list of all status")
					.payload(status)
					.build()
					);
		}catch (Exception e) {
			logger.error("Error retrieving status ",  e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				ApiResponse.<List<TenderStatusResponseDTO>>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message("Failed to retrieve status.")
					.payload(null)
					.build()
					);
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<TenderStatusResponseDTO>> getTenderByID(@PathVariable("id") Long id) {
		try {
			TenderStatusResponseDTO status = tenderStatusService.getStatusById(id);
			return ResponseEntity.ok(
					ApiResponse.<TenderStatusResponseDTO> builder()
					.status(HttpStatus.OK.name())
					.message("Status")
					.payload(status)
					.build()
					);	
		} catch (Exception e) {
			logger.error("Error fetching status by id: {} ", id,  e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				ApiResponse.<TenderStatusResponseDTO>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message("Status not found with ID " + id)
					.payload(null)
					.build()
					);
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<TenderStatusResponseDTO>> updateStatus(@PathVariable("id") long id, @RequestBody TenderStatusRequestDTO requestDTO){
		try {
			TenderStatusResponseDTO updated = tenderStatusService.updateStatus(id, requestDTO);
			return ResponseEntity.ok(
					ApiResponse.<TenderStatusResponseDTO> builder()
						.status(HttpStatus.OK.name())
						.message("Tender updated succeefully")
						.payload(updated)
						.build()
						);	
		} catch (Exception e) {
			logger.error("Error fetching tender by id: {} ", id,  e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					ApiResponse.<TenderStatusResponseDTO>builder()
					.status(HttpStatus.INTERNAL_SERVER_ERROR.name())
					.message("Failed to update status with ID " + id)
					.payload(null)
					.build()
					);		
		}
	}
	
	

	
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> deletingStatus(@PathVariable("id") Long id) {
	    try {
	        tenderStatusService.deleteStatus(id);

	        return ResponseEntity.ok(
	                ApiResponse.<String>builder()
	                        .status(HttpStatus.OK.name())
	                        .message("Tender status deleted successfully")
	                        .payload(null)
	                        .build()
	        );

	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(
	                ApiResponse.<String>builder()
	                        .status(HttpStatus.BAD_REQUEST.name())
	                        .message(e.getMessage())
	                        .payload(null)
	                        .build()
	        );
	    } catch (IllegalStateException e) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(
	                ApiResponse.<String>builder()
	                        .status(HttpStatus.CONFLICT.name())
	                        .message(e.getMessage())
	                        .payload(null)
	                        .build()
	        );
	    } catch (ResourceNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
	                ApiResponse.<String>builder()
	                        .status(HttpStatus.NOT_FOUND.name())
	                        .message(e.getMessage())
	                        .payload(null)
	                        .build()
	        );
	    } catch (Exception e) {
	        logger.error("Error deleting tender status with ID: {}", id, e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	                ApiResponse.<String>builder()
	                        .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
	                        .message("Failed to delete status with ID " + id)
	                        .payload(null)
	                        .build()
	        );
	    }
	}

}
