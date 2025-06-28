package com.bau.adapter.out.persistence.betrieb;

import com.bau.application.domain.betrieb.BetriebStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository for BetriebEntity.
 * Provides database operations for betrieb table.
 */
@Repository
public interface BetriebJpaRepository extends JpaRepository<BetriebEntity, UUID> {
    
    /**
     * Finds a betrieb by email.
     * @param email the email to search for
     * @return the betrieb if found
     */
    Optional<BetriebEntity> findByEmail(String email);
    
    /**
     * Finds all betriebs by status.
     * @param status the status to filter by
     * @return list of betriebs
     */
    List<BetriebEntity> findByStatus(BetriebStatus status);
    
    /**
     * Finds betriebs with pagination and filtering.
     * @param status filter by status (optional)
     * @param pageable pagination parameters
     * @return paginated list of betriebs
     */
    @Query("SELECT b FROM BetriebEntity b WHERE " +
           "(:status IS NULL OR b.status = :status) " +
           "ORDER BY b.name ASC")
    Page<BetriebEntity> findWithFilters(
            @Param("status") BetriebStatus status,
            Pageable pageable
    );
    
    /**
     * Checks if a betrieb exists by email.
     * @param email the email to check
     * @return true if exists
     */
    boolean existsByEmail(String email);
} 