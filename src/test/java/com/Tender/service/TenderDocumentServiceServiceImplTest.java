package com.Tender.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Tender.dto.TenderDocumentRequestDTO;
import com.Tender.dto.TenderDocumentResponseDTO;
import com.Tender.entity.Tender;
import com.Tender.entity.TenderDocument;
import com.Tender.repository.TenderDocumentRepository;
import com.Tender.repository.TenderRepository;
import com.Tender.service.impl.TenderDocumentServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TenderDocumentServiceServiceImplTest {
	
	@InjectMocks
	private TenderDocumentServiceImpl service;
	
	@Mock
	private TenderDocumentRepository tenderDocumentRepository;
	
	@Mock
	private TenderRepository tenderRepository;
	
	private Tender tender;
	
	@BeforeEach
	void setup() {
		tender = Tender.builder().id(1L).title("Test Tender").build();
	}
	
	@Test
	void TestCreateTenderDocument_success() {
		TenderDocumentRequestDTO dto = TenderDocumentRequestDTO.builder()
				.tenderId(1L)
				.fileName("doc1.pdf")
				.fileType("application/pdf")
				.fileUrl("http://files.com/doc1.pdf")
				.build();
		
		TenderDocument tenderDocument = TenderDocument.builder()
				.id(10L)
				.fileName(dto.getFileName())
				.fileType(dto.getFileType())
				.fileUrl(dto.getFileUrl())
				.tender(tender)
				.build();
		
		//When
		when(tenderRepository.findById(1L)).thenReturn(Optional.of(tender));
		when(tenderDocumentRepository.save(any(TenderDocument.class))).thenReturn(tenderDocument);
		
		//Then
		TenderDocumentResponseDTO response = service.createTenderDocument(dto);
		
		assertNotNull(response);
		assertEquals(dto.getFileName(), response.getFileName());
		assertEquals(dto.getFileType(), response.getFileType());
		assertEquals(dto.getFileUrl(), response.getFileUrl());
		
		verify(tenderDocumentRepository, times(1)).save(any(TenderDocument.class));
	}
	


	
	

}
