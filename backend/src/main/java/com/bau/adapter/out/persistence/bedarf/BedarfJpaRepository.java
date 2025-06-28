package com.bau.adapter.out.persistence.bedarf;

import com.bau.application.domain.bedarf.BedarfStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for BedarfEntity.
 * Provides database operations for bedarf table.
 */
@Repository
public interface BedarfJpaRepository extends JpaRepository<BedarfEntity, UUID> {
    
    /**
     * Finds all bedarfs by betrieb ID.
     * @param betriebId the betrieb ID
     * @return list of bedarfs
     */
    List<BedarfEntity> findByBetriebId(UUID betriebId);
    
    /**
     * Finds all bedarfs by status.
     * @param status the status to filter by
     * @return list of bedarfs
     */
    List<BedarfEntity> findByStatus(BedarfStatus status);
    
    /**
     * Finds bedarfs with pagination and filtering.
     * @param status filter by status (optional)
     * @param datumVon filter by start date (optional)
     * @param datumBis filter by end date (optional)
     * @param pageable pagination parameters
     * @return paginated list of bedarfs
     */
    @Query("SELECT b FROM BedarfEntity b WHERE " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:datumVon IS NULL OR b.datumVon >= :datumVon) AND " +
           "(:datumBis IS NULL OR b.datumBis <= :datumBis) " +
           "ORDER BY b.createdAt DESC")
    Page<BedarfEntity> findWithFilters(
            @Param("status") BedarfStatus status,
            @Param("datumVon") LocalDate datumVon,
            @Param("datumBis") LocalDate datumBis,
            Pageable pageable
    );
} 