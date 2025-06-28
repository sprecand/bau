package com.bau.adapter.out.persistence.betrieb;

import com.bau.application.domain.betrieb.Betrieb;
import com.bau.application.domain.betrieb.BetriebStatus;
import com.bau.application.port.out.BetriebRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of BetriebRepository using JPA.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class BetriebRepositoryImpl implements BetriebRepository {
    
    private final BetriebJpaRepository jpaRepository;
    private final BetriebMapper mapper;
    
    @Override
    public Betrieb save(Betrieb betrieb) {
        BetriebEntity entity = mapper.toEntity(betrieb);
        BetriebEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Betrieb> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Betrieb> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Betrieb> findByStatus(BetriebStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public BetriebPageResult findWithPagination(int page, int size, BetriebStatus status) {
        PageRequest pageRequest = PageRequest.of(page, size);
        
        var pageResult = jpaRepository.findWithFilters(status, pageRequest);
        
        List<Betrieb> content = pageResult.getContent()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
        
        return new BetriebPageResult(
                content,
                (int) pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.getNumber(),
                pageResult.getSize()
        );
    }
    
    @Override
    public boolean deleteById(UUID id) {
        if (!jpaRepository.existsById(id)) {
            return false;
        }
        jpaRepository.deleteById(id);
        return true;
    }
    
    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
} 