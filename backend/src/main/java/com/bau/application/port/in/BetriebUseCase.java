package com.bau.application.port.in;

import com.bau.application.domain.betrieb.Betrieb;
import com.bau.application.domain.betrieb.BetriebStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Inbound port for Betrieb use cases.
 * Defines the business operations that can be performed on Betrieb objects.
 */
public interface BetriebUseCase {
    
    /**
     * Creates a new betrieb.
     * @param betrieb the betrieb to create
     * @return the created betrieb with ID
     */
    Betrieb createBetrieb(Betrieb betrieb);
    
    /**
     * Updates an existing betrieb.
     * @param id the betrieb ID
     * @param betrieb the updated betrieb data
     * @return the updated betrieb
     */
    Optional<Betrieb> updateBetrieb(UUID id, Betrieb betrieb);
    
    /**
     * Updates the status of a betrieb.
     * @param id the betrieb ID
     * @param status the new status
     * @return the updated betrieb
     */
    Optional<Betrieb> updateBetriebStatus(UUID id, BetriebStatus status);
    
    /**
     * Retrieves a betrieb by ID.
     * @param id the betrieb ID
     * @return the betrieb if found
     */
    Optional<Betrieb> getBetriebById(UUID id);
    
    /**
     * Retrieves a betrieb by email.
     * @param email the betrieb email
     * @return the betrieb if found
     */
    Optional<Betrieb> getBetriebByEmail(String email);
    
    /**
     * Retrieves all active betriebs.
     * @return list of active betriebs
     */
    List<Betrieb> getActiveBetriebs();
    
    /**
     * Retrieves betriebs with pagination.
     * @param page page number (0-based)
     * @param size page size
     * @param status filter by status
     * @return paginated list of betriebs
     */
    BetriebPageResult getBetriebs(int page, int size, BetriebStatus status);
    
    /**
     * Deletes a betrieb.
     * @param id the betrieb ID
     * @return true if deleted successfully
     */
    boolean deleteBetrieb(UUID id);
    
    /**
     * Result class for paginated betrieb queries.
     */
    class BetriebPageResult {
        private final List<Betrieb> content;
        private final int totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;
        private final boolean hasNext;
        private final boolean hasPrevious;
        
        public BetriebPageResult(List<Betrieb> content, int totalElements, 
                               int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
            this.hasNext = currentPage < totalPages - 1;
            this.hasPrevious = currentPage > 0;
        }
        
        // Getters
        public List<Betrieb> getContent() { return content; }
        public int getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
        public boolean isHasNext() { return hasNext; }
        public boolean isHasPrevious() { return hasPrevious; }
    }
} 