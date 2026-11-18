package com.Tender.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.Tender.dto.TenderResponseDTO;
import com.Tender.service.TenderService;
import com.Tender.status.ApiResponse;

public class TenderControllerTest {
	
	@Mock
	private TenderService tenderService;
	
	@InjectMocks
	private TenderController tenderController;
	
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void testGetAllTenders_success() {
		TenderResponseDTO tender1 = new TenderResponseDTO();
		
		tender1.setId(1L);
		tender1.setTitle("Fourniture de matériel informatique");
		tender1.setReference("ADO-2025-002");
		tender1.setDescription("Achat dìrodinateur portables pour les équipes");
		tender1.setStatusLabel("Cloturé");
		tender1.setPublicationDate(LocalDate.of(2025, 7, 12));
		tender1.setDeadlineDate(LocalDate.of(2025, 8, 1));
		tender1.setCreatedBy("techbuyer@example.com");
		tender1.setDocumentNames(List.of("FormulaireOffre-002.docx", "DevisMateriek.xlsx"));
		
		
		TenderResponseDTO tender2 = new TenderResponseDTO();
		
		tender2.setId(2L);
		tender2.setTitle("Recrutement consultant en cybersécurité");
		
		List<TenderResponseDTO> mockTenders = List.of(tender1, tender2);
		
		//Simuler le service
		when(tenderService.getAllTenders()).thenReturn(mockTenders);
		
		//Appel du controller
		ResponseEntity<ApiResponse<List<TenderResponseDTO>>> response = tenderController.getAllTenders();
		
		//Vérifications
		assertEquals(200, response.getStatusCodeValue());
		assertEquals(2, response.getBody().getPayload().size());
		assertEquals("Fourniture de matériel informatique", response.getBody().getPayload().get(0).getTitle());
		
	}
	
	@Test
	void testGetAllTenders_emptyList() {
		
		//service retourne une liste vide
		when(tenderService.getAllTenders()).thenReturn(List.of());
		
		ResponseEntity<ApiResponse<List<TenderResponseDTO>>> response = tenderController.getAllTenders();
		
		assertEquals(200, response.getStatusCodeValue());
		assertEquals(0, response.getBody().getPayload().size());
		assertEquals("list of all tenders", response.getBody().getMessage());
	}
	
	
	@Test
	void testGetTenderById_success() {
	    // given
	    Long id = 1L;

	    TenderResponseDTO mockTender = new TenderResponseDTO();
	    mockTender.setId(id);
	    mockTender.setTitle("Achat matériel informatique");

	    when(tenderService.getById(id)).thenReturn(mockTender);

	    // when
	    ResponseEntity<ApiResponse<TenderResponseDTO>> response = tenderController.getTenderByID(id);

	    // then
	    assertEquals(200, response.getStatusCodeValue());
	    assertEquals("Tender", response.getBody().getMessage());
	    assertEquals(id, response.getBody().getPayload().getId());
	    assertEquals("Achat matériel informatique", response.getBody().getPayload().getTitle());
	}
	
	
	@Test
	void testGetTenderById_exception() {
	    // given
	    Long id = 99L;

	    when(tenderService.getById(id)).thenThrow(new RuntimeException("Not found"));

	    // when
	    ResponseEntity<ApiResponse<TenderResponseDTO>> response = tenderController.getTenderByID(id);

	    // then
	    assertEquals(500, response.getStatusCodeValue());
	    assertEquals("Tender not found with ID " + id, response.getBody().getMessage());
	    assertEquals(null, response.getBody().getPayload());
	}
	
	

}
