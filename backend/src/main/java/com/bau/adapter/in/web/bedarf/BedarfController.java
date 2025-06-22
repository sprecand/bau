package com.bau.adapter.in.web.bedarf;

import com.bau.adapter.in.web.api.BedarfControllerApi;
import com.bau.adapter.in.web.dto.BedarfResponse;
import com.bau.adapter.in.web.dto.UpdateBedarfRequest;
import com.bau.adapter.in.web.dto.UpdateBedarfStatusRequest;
import com.bau.adapter.in.web.bedarf.mapper.BedarfWebMapper;
import com.bau.application.domain.bedarf.Bedarf;
import com.bau.application.domain.bedarf.BedarfStatus;
import com.bau.application.port.in.BedarfUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for Bedarf endpoints.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class BedarfController implements BedarfControllerApi {
    
    private final BedarfUseCase bedarfUseCase;
    private final BedarfWebMapper mapper;
    
    @Override
    @PreAuthorize("hasRole('BETRIEB')")
    public ResponseEntity<Void> deleteBedarf(UUID id) {
        log.info("Deleting bedarf with id: {}", id);
        bedarfUseCase.deleteBedarf(id);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<BedarfResponse> getBedarfById(UUID id) {
        log.info("Retrieving bedarf with id: {}", id);
        return bedarfUseCase.getBedarfById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Override
    public ResponseEntity<List<BedarfResponse>> getBedarfsByBetrieb(UUID betriebId) {
        log.info("Retrieving bedarfs for betrieb: {}", betriebId);
        List<BedarfResponse> bedarfs = bedarfUseCase.getBedarfsByBetriebId(betriebId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bedarfs);
    }
    
    @Override
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BedarfResponse> updateBedarf(UUID id, @Valid @RequestBody UpdateBedarfRequest updateBedarfRequest) {
        log.info("Updating bedarf with id: {}", id);
        Bedarf bedarf = mapper.toDomain(updateBedarfRequest);
        Bedarf updatedBedarf = bedarfUseCase.updateBedarf(id, bedarf)
                .orElseThrow(() -> new RuntimeException("Bedarf not found"));
        BedarfResponse response = mapper.toResponse(updatedBedarf);
        return ResponseEntity.ok(response);
    }
    
    @Override
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BedarfResponse> updateBedarfStatus(UUID id, @Valid @RequestBody UpdateBedarfStatusRequest updateBedarfStatusRequest) {
        log.info("Updating bedarf status with id: {}", id);
        BedarfStatus status = BedarfStatus.valueOf(updateBedarfStatusRequest.getStatus().name());
        Bedarf updatedBedarf = bedarfUseCase.updateBedarfStatus(id, status)
                .orElseThrow(() -> new RuntimeException("Bedarf not found"));
        BedarfResponse response = mapper.toResponse(updatedBedarf);
        return ResponseEntity.ok(response);
    }
}