package com.bau.adapter.in.web.bedarf;

import com.bau.adapter.in.web.api.BedarfApi;
import com.bau.adapter.in.web.dto.BedarfListResponse;
import com.bau.adapter.in.web.dto.BedarfResponse;
import com.bau.adapter.in.web.dto.CreateBedarfRequest;
import com.bau.adapter.in.web.bedarf.mapper.BedarfWebMapper;
import com.bau.application.domain.bedarf.Bedarf;
import com.bau.application.port.in.BedarfUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
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
    
    @Override
    @PreAuthorize("hasRole('BETRIEB')")
    public ResponseEntity<BedarfResponse> createBedarf(@Valid @RequestBody CreateBedarfRequest createBedarfRequest) {
        log.info("Creating new bedarf");
        Bedarf bedarf = mapper.toDomain(createBedarfRequest);
        
        // TODO: Replace with real authentication context when AWS Cognito is integrated
        // For now, use a mock betriebId for local development
        UUID mockBetriebId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        bedarf.setBetriebId(mockBetriebId);
        
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
} 