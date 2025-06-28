package com.bau.application.port.out;

import com.bau.application.domain.betrieb.Betrieb;
import com.bau.application.domain.betrieb.BetriebStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for Betrieb persistence operations.
 * Defines the contract for storing and retrieving Betrieb objects.
 */
public interface BetriebRepository {
    
    /**
     * Saves a betrieb.
     * @param betrieb the betrieb to save
     * @return the saved betrieb with ID
     */
    Betrieb save(Betrieb betrieb);
    
    /**
     * Finds a betrieb by ID.
     * @param id the betrieb ID
     * @return the betrieb if found
     */
    Optional<Betrieb> findById(UUID id);
    
    /**
     * Finds a betrieb by email.
     * @param email the betrieb email
     * @return the betrieb if found
     */
    Optional<Betrieb> findByEmail(String email);
    
    /**
     * Finds all betriebs with the given status.
     * @param status the status to filter by
     * @return list of betriebs
     */
    List<Betrieb> findByStatus(BetriebStatus status);
    
    /**
     * Finds betriebs with pagination.
     * @param page page number (0-based)
     * @param size page size
     * @param status filter by status
     * @return paginated list of betriebs
     */
    BetriebPageResult findWithPagination(int page, int size, BetriebStatus status);
    
    /**
     * Deletes a betrieb by ID.
     * @param id the betrieb ID
     * @return true if deleted successfully
     */
    boolean deleteById(UUID id);
    
    /**
     * Checks if a betrieb exists by ID.
     * @param id the betrieb ID
     * @return true if exists
     */
    boolean existsById(UUID id);
    
    /**
     * Checks if a betrieb exists by email.
     * @param email the betrieb email
     * @return true if exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Result class for paginated betrieb queries.
     */
    class BetriebPageResult {
        private final List<Betrieb> content;
        private final int totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;
        
        public BetriebPageResult(List<Betrieb> content, int totalElements, 
                               int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }
        
        // Getters
        public List<Betrieb> getContent() { return content; }
        public int getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
    }
} 