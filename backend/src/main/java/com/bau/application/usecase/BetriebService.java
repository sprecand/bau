package com.bau.application.usecase;

import com.bau.application.domain.betrieb.Betrieb;
import com.bau.application.domain.betrieb.BetriebStatus;
import com.bau.application.port.in.BetriebUseCase;
import com.bau.application.port.out.BetriebRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of Betrieb use cases.
 * Contains the business logic for Betrieb operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BetriebService implements BetriebUseCase {
    
    private final BetriebRepository betriebRepository;
    
    @Override
    public Betrieb createBetrieb(Betrieb betrieb) {
        log.debug("Creating new betrieb: {}", betrieb.getName());
        validateBetrieb(betrieb);
        
        if (betriebRepository.existsByEmail(betrieb.getEmail())) {
            log.warn("Attempted to create betrieb with existing email: {}", betrieb.getEmail());
            throw new IllegalArgumentException("Betrieb with this email already exists");
        }
        
        return betriebRepository.save(betrieb);
    }
    
    @Override
    public Optional<Betrieb> updateBetrieb(UUID id, Betrieb betrieb) {
        log.debug("Updating betrieb with id: {}", id);
        return betriebRepository.findById(id)
                .map(existingBetrieb -> {
                    betrieb.setId(id);
                    
                    // Check if email is being changed and if it's already taken
                    if (!existingBetrieb.getEmail().equals(betrieb.getEmail()) &&
                        betriebRepository.existsByEmail(betrieb.getEmail())) {
                        log.warn("Attempted to update betrieb with existing email: {}", betrieb.getEmail());
                        throw new IllegalArgumentException("Betrieb with this email already exists");
                    }
                    
                    validateBetrieb(betrieb);
                    return betriebRepository.save(betrieb);
                });
    }
    
    @Override
    public Optional<Betrieb> updateBetriebStatus(UUID id, BetriebStatus status) {
        log.debug("Updating betrieb status to {} for id: {}", status, id);
        return betriebRepository.findById(id)
                .map(betrieb -> {
                    betrieb.setStatus(status);
                    return betriebRepository.save(betrieb);
                });
    }
    
    @Override
    public Optional<Betrieb> getBetriebById(UUID id) {
        log.debug("Retrieving betrieb by id: {}", id);
        return betriebRepository.findById(id);
    }
    
    @Override
    public Optional<Betrieb> getBetriebByEmail(String email) {
        log.debug("Retrieving betrieb by email: {}", email);
        return betriebRepository.findByEmail(email);
    }
    
    @Override
    public List<Betrieb> getActiveBetriebs() {
        log.debug("Retrieving all active betriebs");
        return betriebRepository.findByStatus(BetriebStatus.AKTIV);
    }
    
    @Override
    public BetriebPageResult getBetriebs(int page, int size, BetriebStatus status) {
        log.debug("Retrieving betriebs with pagination - page: {}, size: {}, status: {}", 
                page, size, status);
        BetriebRepository.BetriebPageResult result = betriebRepository.findWithPagination(
                page, size, status);
        
        return new BetriebPageResult(
                result.getContent(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getCurrentPage(),
                result.getPageSize()
        );
    }
    
    @Override
    public boolean deleteBetrieb(UUID id) {
        log.debug("Deleting betrieb with id: {}", id);
        if (!betriebRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent betrieb with id: {}", id);
            return false;
        }
        return betriebRepository.deleteById(id);
    }
    
    /**
     * Validates the betrieb according to business rules.
     * @param betrieb the betrieb to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateBetrieb(Betrieb betrieb) {
        if (betrieb.getName() == null || betrieb.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Company name is required");
        }
        if (betrieb.getAdresse() == null || betrieb.getAdresse().trim().isEmpty()) {
            throw new IllegalArgumentException("Company address is required");
        }
        if (betrieb.getEmail() == null || betrieb.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Company email is required");
        }
        if (!isValidEmail(betrieb.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
    
    /**
     * Basic email validation.
     * @param email email to validate
     * @return true if email format is valid
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
} 