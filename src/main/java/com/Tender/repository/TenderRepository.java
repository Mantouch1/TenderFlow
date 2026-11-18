package com.Tender.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Tender.dto.StatusDocumentCountDTO;
import com.Tender.entity.Tender;
import com.Tender.entity.TenderStatus;

@Repository
public interface TenderRepository extends JpaRepository<Tender, Long>{
	
	boolean existsByStatus(TenderStatus status);
	
	
	@Query("SELECT t FROM Tender t " +
		       "WHERE (:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
		       "AND (:statusName IS NULL OR LOWER(t.status.name) = LOWER(:statusName))")
	List<Tender> searchByTitleAndStatus(@Param("title") String title, @Param("statusName") String statusName);
	
	List<Tender> findByPublicationDateBetween(LocalDate start, LocalDate end);
	
	List<Tender> findByCreatedBy(String createdBy);
	
	@Query("SELECT t FROM Tender t JOIN FETCH t.documents d JOIN t.status s WHERE LOWER(s.name) = LOWER(:statusName)")
	List<Tender> findTendersWithDocumentsByStatus(@Param("statusName") String statusName);
	
	@Query("SELECT t FROM Tender t JOIN t.status s WHERE LOWER(s.name) = LOWER(:statusName)")
	List<Tender> findTendersByStatus(@Param("statusName") String statusName);
	
	
	@Query("SELECT new com.Tender.dto.StatusDocumentCountDTO(s.name, COUNT(d)) " +
	           "FROM Tender t " +
	           "JOIN t.status s " +
	           "LEFT JOIN t.documents d " +
	           "GROUP BY s.name")
	List<StatusDocumentCountDTO> countDucumentByStatus();

}
