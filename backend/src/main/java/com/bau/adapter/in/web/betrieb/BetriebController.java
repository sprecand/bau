package com.bau.adapter.in.web.betrieb;

import com.bau.adapter.in.web.api.BetriebControllerApi;
import com.bau.adapter.in.web.dto.BetriebResponse;
import com.bau.adapter.in.web.dto.UpdateBetriebRequest;
import com.bau.adapter.in.web.dto.UpdateBetriebStatusRequest;
import com.bau.adapter.in.web.betrieb.mapper.BetriebWebMapper;
import com.bau.application.domain.betrieb.Betrieb;
import com.bau.application.domain.betrieb.BetriebStatus;
import com.bau.application.port.in.BetriebUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

/**
 * REST controller for Betrieb endpoints.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class BetriebController implements BetriebControllerApi {
    
    private final BetriebUseCase betriebUseCase;
    private final BetriebWebMapper mapper;
    
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBetrieb(UUID id) {
        log.info("Deleting betrieb with id: {}", id);
        betriebUseCase.deleteBetrieb(id);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<BetriebResponse> getBetriebById(UUID id) {
        log.info("Retrieving betrieb with id: {}", id);
        return betriebUseCase.getBetriebById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BetriebResponse> updateBetrieb(UUID id, @Valid @RequestBody UpdateBetriebRequest updateBetriebRequest) {
        log.info("Updating betrieb with id: {}", id);
        Betrieb betrieb = mapper.toDomain(updateBetriebRequest);
        Betrieb updatedBetrieb = betriebUseCase.updateBetrieb(id, betrieb)
                .orElseThrow(() -> new RuntimeException("Betrieb not found"));
        BetriebResponse response = mapper.toResponse(updatedBetrieb);
        return ResponseEntity.ok(response);
    }
    
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BetriebResponse> updateBetriebStatus(UUID id, @Valid @RequestBody UpdateBetriebStatusRequest updateBetriebStatusRequest) {
        log.info("Updating betrieb status with id: {}", id);
        BetriebStatus status = BetriebStatus.valueOf(updateBetriebStatusRequest.getStatus().name());
        Betrieb updatedBetrieb = betriebUseCase.updateBetriebStatus(id, status)
                .orElseThrow(() -> new RuntimeException("Betrieb not found"));
        BetriebResponse response = mapper.toResponse(updatedBetrieb);
        return ResponseEntity.ok(response);
    }
}