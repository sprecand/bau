package com.bau.adapter.in.web.bedarf;

import com.bau.adapter.in.web.api.BedarfApi;
import com.bau.adapter.in.web.dto.*;
import com.bau.adapter.in.web.bedarf.mapper.BedarfWebMapper;
import com.bau.application.domain.bedarf.Bedarf;
import com.bau.application.domain.bedarf.BedarfStatus;
import com.bau.application.port.in.BedarfUseCase;
import com.bau.shared.service.AuthenticationContextService;
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
import java.util.stream.Collectors;

/**
 * REST controller for Bedarf API endpoints.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class BedarfApiController implements BedarfApi {
    
    private final BedarfUseCase bedarfUseCase;
    private final BedarfWebMapper mapper;
    private final AuthenticationContextService authContextService;
    
    @Override
    @PreAuthorize("hasRole('BETRIEB')")
    public ResponseEntity<BedarfResponse> createBedarf(@Valid @RequestBody CreateBedarfRequest createBedarfRequest) {
        log.info("Creating new bedarf");
        
        // Get the current user's betrieb ID from authentication context
        Optional<UUID> currentUserBetriebId = authContextService.getCurrentUserBetriebId();
        if (currentUserBetriebId.isEmpty()) {
            log.warn("User has no betrieb ID - cannot create bedarf");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Bedarf bedarf = mapper.toDomain(createBedarfRequest);
        bedarf.setBetriebId(currentUserBetriebId.get());
        
        log.debug("Creating bedarf for betrieb: {}", currentUserBetriebId.get());
        
        Bedarf createdBedarf = bedarfUseCase.createBedarf(bedarf);
        BedarfResponse response = mapper.toResponse(createdBedarf);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Override
    public ResponseEntity<BedarfListResponse> listBedarfe(@Valid Integer page, @Valid Integer size) {
        log.info("Retrieving bedarfs - page: {}, size: {}", page, size);
        
        BedarfUseCase.BedarfPageResult result = bedarfUseCase.getBedarfs(page, size, null, null, null);
        
        List<BedarfResponse> content = result.getContent().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        
        BedarfListResponse response = new BedarfListResponse()
                .content((List<Object>) (List<?>) content)
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .currentPage(result.getCurrentPage())
                .pageSize(result.getPageSize())
                .hasNext(result.isHasNext())
                .hasPrevious(result.isHasPrevious());
        
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BedarfResponse> getBedarfById(@PathVariable UUID id) {
        log.info("Retrieving bedarf with id: {}", id);
        Optional<Bedarf> bedarfOpt = bedarfUseCase.getBedarfById(id);
        if (bedarfOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        BedarfResponse response = mapper.toResponse(bedarfOpt.get());
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasRole('BETRIEB')")
    public ResponseEntity<BedarfResponse> updateBedarf(@PathVariable UUID id, @Valid @RequestBody UpdateBedarfRequest updateBedarfRequest) {
        log.info("Updating bedarf with id: {}", id);
        Bedarf bedarf = mapper.toDomain(updateBedarfRequest);
        Optional<Bedarf> updatedBedarfOpt = bedarfUseCase.updateBedarf(id, bedarf);
        if (updatedBedarfOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        BedarfResponse response = mapper.toResponse(updatedBedarfOpt.get());
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBedarf(@PathVariable UUID id) {
        log.info("Deleting bedarf with id: {}", id);
        boolean deleted = bedarfUseCase.deleteBedarf(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BedarfResponse> updateBedarfStatus(@PathVariable UUID id, @Valid @RequestBody UpdateBedarfStatusRequest updateBedarfStatusRequest) {
        log.info("Updating bedarf status for id: {} to status: {}", id, updateBedarfStatusRequest.getStatus());
        BedarfStatus newStatus = BedarfStatus.valueOf(updateBedarfStatusRequest.getStatus().getValue());
        Optional<Bedarf> updatedBedarfOpt = bedarfUseCase.updateBedarfStatus(id, newStatus);
        if (updatedBedarfOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        BedarfResponse response = mapper.toResponse(updatedBedarfOpt.get());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<BedarfResponse>> getBedarfsByBetrieb(@PathVariable UUID betriebId) {
        log.info("Retrieving bedarfs for betrieb: {}", betriebId);
        List<Bedarf> bedarfs = bedarfUseCase.getBedarfsByBetriebId(betriebId);
        List<BedarfResponse> responses = bedarfs.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
} 