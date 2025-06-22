package com.bau.adapter.in.web.betrieb;

import com.bau.adapter.in.web.api.BetriebApi;
import com.bau.adapter.in.web.dto.BetriebListResponse;
import com.bau.adapter.in.web.dto.BetriebResponse;
import com.bau.adapter.in.web.dto.CreateBetriebRequest;
import com.bau.adapter.in.web.betrieb.mapper.BetriebWebMapper;
import com.bau.application.domain.betrieb.Betrieb;
import com.bau.application.port.in.BetriebUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
        BetriebUseCase.BetriebPageResult result = betriebUseCase.getBetriebs(page, size, null);
        List<BetriebResponse> content = result.getContent().stream()
                .map(mapper::toResponse)
                .toList();
        BetriebListResponse response = new BetriebListResponse()
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