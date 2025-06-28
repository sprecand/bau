package com.bau.application.port.out;

import com.bau.application.domain.bedarf.Bedarf;
import com.bau.application.domain.bedarf.BedarfStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for Bedarf persistence operations.
 * Defines the contract for storing and retrieving Bedarf objects.
 */
public interface BedarfRepository {
    
    /**
     * Saves a bedarf.
     * @param bedarf the bedarf to save
     * @return the saved bedarf with ID
     */
    Bedarf save(Bedarf bedarf);
    
    /**
     * Finds a bedarf by ID.
     * @param id the bedarf ID
     * @return the bedarf if found
     */
    Optional<Bedarf> findById(UUID id);
    
    /**
     * Finds all bedarfs for a specific betrieb.
     * @param betriebId the betrieb ID
     * @return list of bedarfs
     */
    List<Bedarf> findByBetriebId(UUID betriebId);
    
    /**
     * Finds all bedarfs with the given status.
     * @param status the status to filter by
     * @return list of bedarfs
     */
    List<Bedarf> findByStatus(BedarfStatus status);
    
    /**
     * Finds bedarfs with pagination and filtering.
     * @param page page number (0-based)
     * @param size page size
     * @param status filter by status
     * @param datumVon filter by start date
     * @param datumBis filter by end date
     * @return paginated list of bedarfs
     */
    BedarfPageResult findWithPagination(int page, int size, BedarfStatus status, 
                                      LocalDate datumVon, LocalDate datumBis);
    
    /**
     * Deletes a bedarf by ID.
     * @param id the bedarf ID
     * @return true if deleted successfully
     */
    boolean deleteById(UUID id);
    
    /**
     * Checks if a bedarf exists by ID.
     * @param id the bedarf ID
     * @return true if exists
     */
    boolean existsById(UUID id);
    
    /**
     * Result class for paginated bedarf queries.
     */
    class BedarfPageResult {
        private final List<Bedarf> content;
        private final int totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;
        
        public BedarfPageResult(List<Bedarf> content, int totalElements, 
                              int totalPages, int currentPage, int pageSize) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }
        
        // Getters
        public List<Bedarf> getContent() { return content; }
        public int getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
    }
} 