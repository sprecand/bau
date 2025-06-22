package com.bau.application.usecase;

import com.bau.application.domain.bedarf.Bedarf;
import com.bau.application.domain.bedarf.BedarfStatus;
import com.bau.application.port.in.BedarfUseCase;
import com.bau.application.port.out.BedarfRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of Bedarf use cases.
 * Contains the business logic for Bedarf operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BedarfService implements BedarfUseCase {
    
    private final BedarfRepository bedarfRepository;
    
    @Override
    public Bedarf createBedarf(Bedarf bedarf) {
        log.debug("Creating new bedarf for betrieb: {}", bedarf.getBetriebId());
        validateBedarf(bedarf);
        
        // Set default status if not provided
        if (bedarf.getStatus() == null) {
            bedarf.setStatus(BedarfStatus.AKTIV);
        }
        
        return bedarfRepository.save(bedarf);
    }
    
    @Override
    public Optional<Bedarf> updateBedarf(UUID id, Bedarf bedarf) {
        log.debug("Updating bedarf with id: {}", id);
        return bedarfRepository.findById(id)
                .map(existingBedarf -> {
                    bedarf.setId(id);
                    bedarf.setBetriebId(existingBedarf.getBetriebId());
                    validateBedarf(bedarf);
                    return bedarfRepository.save(bedarf);
                });
    }
    
    @Override
    public Optional<Bedarf> updateBedarfStatus(UUID id, BedarfStatus status) {
        log.debug("Updating bedarf status to {} for id: {}", status, id);
        return bedarfRepository.findById(id)
                .map(bedarf -> {
                    bedarf.setStatus(status);
                    return bedarfRepository.save(bedarf);
                });
    }
    
    @Override
    public Optional<Bedarf> getBedarfById(UUID id) {
        log.debug("Retrieving bedarf by id: {}", id);
        return bedarfRepository.findById(id);
    }
    
    @Override
    public List<Bedarf> getBedarfsByBetriebId(UUID betriebId) {
        log.debug("Retrieving bedarfs for betrieb: {}", betriebId);
        return bedarfRepository.findByBetriebId(betriebId);
    }
    
    @Override
    public List<Bedarf> getActiveBedarfs() {
        log.debug("Retrieving all active bedarfs");
        return bedarfRepository.findByStatus(BedarfStatus.AKTIV);
    }
    
    @Override
    public BedarfPageResult getBedarfs(int page, int size, BedarfStatus status, 
                                     LocalDate datumVon, LocalDate datumBis) {
        log.debug("Retrieving bedarfs with pagination - page: {}, size: {}, status: {}", 
                page, size, status);
        BedarfRepository.BedarfPageResult result = bedarfRepository.findWithPagination(
                page, size, status, datumVon, datumBis);
        
        return new BedarfPageResult(
                result.getContent(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getCurrentPage(),
                result.getPageSize()
        );
    }
    
    @Override
    public boolean deleteBedarf(UUID id) {
        log.debug("Deleting bedarf with id: {}", id);
        if (!bedarfRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent bedarf with id: {}", id);
            return false;
        }
        return bedarfRepository.deleteById(id);
    }
    
    /**
     * Validates the bedarf according to business rules.
     * @param bedarf the bedarf to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateBedarf(Bedarf bedarf) {
        if (bedarf.getBetriebId() == null) {
            throw new IllegalArgumentException("Betrieb ID is required");
        }
        if (bedarf.getHolzbauAnzahl() == null || bedarf.getHolzbauAnzahl() < 0) {
            throw new IllegalArgumentException("Holzbau Anzahl must be non-negative");
        }
        if (bedarf.getZimmermannAnzahl() == null || bedarf.getZimmermannAnzahl() < 0) {
            throw new IllegalArgumentException("Zimmermann Anzahl must be non-negative");
        }
        if (bedarf.getHolzbauAnzahl() == 0 && bedarf.getZimmermannAnzahl() == 0) {
            throw new IllegalArgumentException("At least one worker type must be specified");
        }
        if (bedarf.getDatumVon() == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (bedarf.getDatumBis() == null) {
            throw new IllegalArgumentException("End date is required");
        }
        if (bedarf.getDatumBis().isBefore(bedarf.getDatumVon())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        if (bedarf.getAdresse() == null || bedarf.getAdresse().trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }
    }
} 