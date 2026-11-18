package com.Tender.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.Tender.dto.TenderResponseDTO;
import com.Tender.dto.TenderStatusResponseDTO;
import com.Tender.service.TenderStatusService;
import com.Tender.status.ApiResponse;

public class TenderStatusControllerTest {
	
	@Mock
	private TenderStatusService tenderStatusService;
	
	@InjectMocks
	private TenderStatusController tenderStatusController;
	
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void testGetAllStatus_success(){
		TenderStatusResponseDTO status1 = new TenderStatusResponseDTO();
		status1.setId(1L);
		status1.setName("Publié");
		
		TenderStatusResponseDTO status2 = new TenderStatusResponseDTO();
		status2.setId(2L);
		status2.setName("Annulé");
		
		
		List<TenderStatusResponseDTO> mockStatus = List.of(status1, status2);
		
		
		when(tenderStatusService.getAllStatuses()).thenReturn(mockStatus);
		
		ResponseEntity<ApiResponse<List<TenderStatusResponseDTO>>> response = tenderStatusController.getAllStatus();
		
		assertEquals(200, response.getStatusCodeValue());
		assertEquals(2, response.getBody().getPayload().size());
		assertEquals("Publié", response.getBody().getPayload().get(0).getName());
		
	}
	
	@Test
	void testGeAllStatus_emptyList() {
		when(tenderStatusService.getAllStatuses()).thenReturn(List.of());
		
		ResponseEntity<ApiResponse<List<TenderStatusResponseDTO>>> response = tenderStatusController.getAllStatus();
		
		assertEquals(200, response.getStatusCodeValue());
		assertEquals(0, response.getBody().getPayload().size());
		assertEquals("list of all status", response.getBody().getMessage());
	}
	
	@Test
	void testGetStatusById_success() {
		
		Long id = 1L;
		
		TenderStatusResponseDTO mockStatus = new TenderStatusResponseDTO();
		mockStatus.setId(id);
		mockStatus.setName("Publié");
		
		when(tenderStatusService.getStatusById(id)).thenReturn(mockStatus);
		
		ResponseEntity<ApiResponse<TenderStatusResponseDTO>> response = tenderStatusController.getTenderByID(id);
		
		assertEquals(200, response.getStatusCodeValue());
		assertEquals("Status", response.getBody().getMessage());
		assertEquals(id, response.getBody().getPayload().getId());
		assertEquals("Publié", response.getBody().getPayload().getName());
		
	}
	
	@Test
	void testGetStatusById_exception() {
		
		Long id = 99L;
		
		when(tenderStatusService.getStatusById(id)).thenThrow(new RuntimeException("Not Found"));
		
		ResponseEntity<ApiResponse<TenderStatusResponseDTO>> response = tenderStatusController.getTenderByID(id);
		
		assertEquals(500, response.getStatusCodeValue());
		assertEquals("Status not found with ID " + id , response.getBody().getMessage());
		assertEquals(null, response.getBody().getPayload());
	}

}
