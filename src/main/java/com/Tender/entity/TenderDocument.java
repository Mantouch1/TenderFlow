package com.Tender.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tender_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
  public class TenderDocument {
  	
  	@Id
  	@GeneratedValue(strategy = GenerationType.IDENTITY)
  	private Long id;
  	
  	private String fileName;
  	private String  fileType;
  	private String fileUrl;
  	
  	@ManyToOne
  	@JoinColumn(name = "Tender_id")
  	private Tender tender;
  	
  
  }
