package com.bau.application.port.in;

import com.bau.application.domain.bedarf.Bedarf;
import com.bau.application.domain.bedarf.BedarfStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Inbound port for Bedarf use cases.
 * Defines the business operations that can be performed on Bedarf objects.
 */
public interface BedarfUseCase {
    
    /**
     * Creates a new bedarf.
     * @param bedarf the bedarf to create
     * @return the created bedarf with ID
     */
    Bedarf createBedarf(Bedarf bedarf);
    
    /**
     * Updates an existing bedarf.
     * @param id the bedarf ID
     * @param bedarf the updated bedarf data
     * @return the updated bedarf
     */
    Optional<Bedarf> updateBedarf(UUID id, Bedarf bedarf);
    
    /**
     * Updates the status of a bedarf.
     * @param id the bedarf ID
     * @param status the new status
     * @return the updated bedarf
     */
    Optional<Bedarf> updateBedarfStatus(UUID id, BedarfStatus status);
    
    /**
     * Retrieves a bedarf by ID.
     * @param id the bedarf ID
     * @return the bedarf if found
     */
    Optional<Bedarf> getBedarfById(UUID id);
    
    /**
     * Retrieves all bedarfs for a specific betrieb.
     * @param betriebId the betrieb ID
     * @return list of bedarfs
     */
    List<Bedarf> getBedarfsByBetriebId(UUID betriebId);
    
    /**
     * Retrieves all active bedarfs.
     * @return list of active bedarfs
     */
    List<Bedarf> getActiveBedarfs();
    
    /**
     * Retrieves bedarfs with pagination and filtering.
     * @param page page number (0-based)
     * @param size page size
     * @param status filter by status
     * @param datumVon filter by start date
     * @param datumBis filter by end date
     * @return paginated list of bedarfs
     */
    BedarfPageResult getBedarfs(int page, int size, BedarfStatus status, 
                               LocalDate datumVon, LocalDate datumBis);
    
    /**
     * Deletes a bedarf.
     * @param id the bedarf ID
     * @return true if deleted successfully
     */
    boolean deleteBedarf(UUID id);
    
    /**
     * Result class for paginated bedarf queries.
     */
    class BedarfPageResult {
        private final List<Bedarf> content;
        private final int totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;
        private final boolean hasNext;
        private final boolean hasPrevious;
        
        public BedarfPageResult(List<Bedarf> content, int totalElements, 
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
        public List<Bedarf> getContent() { return content; }
        public int getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
        public boolean isHasNext() { return hasNext; }
        public boolean isHasPrevious() { return hasPrevious; }
    }
} 