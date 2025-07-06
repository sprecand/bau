package com.bau.adapter.in.web.betrieb;

import com.bau.adapter.in.web.api.BetriebApi;
import com.bau.adapter.in.web.dto.*;
import com.bau.adapter.in.web.betrieb.mapper.BetriebWebMapper;
import com.bau.application.domain.betrieb.Betrieb;
import com.bau.application.domain.betrieb.BetriebStatus;
import com.bau.application.port.in.BetriebUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for Betrieb API endpoints.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class BetriebApiController implements BetriebApi {
    
    private final BetriebUseCase betriebUseCase;
    private final BetriebWebMapper mapper;
    
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BetriebResponse> createBetrieb(@Valid @RequestBody CreateBetriebRequest createBetriebRequest) {
        log.info("Creating new betrieb");
        Betrieb betrieb = mapper.toDomain(createBetriebRequest);
        Betrieb createdBetrieb = betriebUseCase.createBetrieb(betrieb);
        BetriebResponse response = mapper.toResponse(createdBetrieb);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Override
    public ResponseEntity<BetriebListResponse> listBetriebe(@Valid Integer page, @Valid Integer size) {
        log.info("Retrieving betriebs - page: {}, size: {}", page, size);
        // Convert from 1-based API page to 0-based internal page
        int internalPage = page - 1;
        BetriebUseCase.BetriebPageResult result = betriebUseCase.getBetriebs(internalPage, size, null);
        List<BetriebResponse> content = result.getContent().stream()
                .map(mapper::toResponse)
                .toList();
        BetriebListResponse response = new BetriebListResponse()
                .content((List<Object>) (List<?>) content)
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .currentPage(result.getCurrentPage() + 1) // Convert back to 1-based page
                .pageSize(result.getPageSize())
                .hasNext(result.isHasNext())
                .hasPrevious(result.isHasPrevious());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BetriebResponse> getBetriebById(@PathVariable UUID id) {
        log.info("Retrieving betrieb with id: {}", id);
        Optional<Betrieb> betriebOpt = betriebUseCase.getBetriebById(id);
        if (betriebOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        BetriebResponse response = mapper.toResponse(betriebOpt.get());
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BetriebResponse> updateBetrieb(@PathVariable UUID id, @Valid @RequestBody UpdateBetriebRequest updateBetriebRequest) {
        log.info("Updating betrieb with id: {}", id);
        Betrieb betrieb = mapper.toDomain(updateBetriebRequest);
        Optional<Betrieb> updatedBetriebOpt = betriebUseCase.updateBetrieb(id, betrieb);
        if (updatedBetriebOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        BetriebResponse response = mapper.toResponse(updatedBetriebOpt.get());
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBetrieb(@PathVariable UUID id) {
        log.info("Deleting betrieb with id: {}", id);
        boolean deleted = betriebUseCase.deleteBetrieb(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BetriebResponse> updateBetriebStatus(@PathVariable UUID id, @Valid @RequestBody UpdateBetriebStatusRequest updateBetriebStatusRequest) {
        log.info("Updating betrieb status for id: {} to status: {}", id, updateBetriebStatusRequest.getStatus());
        BetriebStatus newStatus = BetriebStatus.valueOf(updateBetriebStatusRequest.getStatus().getValue());
        Optional<Betrieb> updatedBetriebOpt = betriebUseCase.updateBetriebStatus(id, newStatus);
        if (updatedBetriebOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        BetriebResponse response = mapper.toResponse(updatedBetriebOpt.get());
        return ResponseEntity.ok(response);
    }
} 