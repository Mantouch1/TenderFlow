package com.Tender.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Tender.entity.TenderStatus;

@Repository
public interface TenderStatusRepository extends JpaRepository<TenderStatus, Long> {
	
	Optional<TenderStatus> findByName(String name);
}
