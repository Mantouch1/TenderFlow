package com.Tender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Tender.entity.TenderDocument;

@Repository
public interface TenderDocumentRepository extends JpaRepository<TenderDocument, Long>{

}
