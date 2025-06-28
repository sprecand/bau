package com.bau.adapter.out.persistence.bedarf;

import com.bau.application.domain.bedarf.Bedarf;
import com.bau.application.domain.bedarf.BedarfStatus;
import com.bau.application.port.out.BedarfRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of BedarfRepository using JPA.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class BedarfRepositoryImpl implements BedarfRepository {
    
    private final BedarfJpaRepository jpaRepository;
    private final BedarfMapper mapper;
    
    @Override
    public Bedarf save(Bedarf bedarf) {
        BedarfEntity entity = mapper.toEntity(bedarf);
        BedarfEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Bedarf> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Bedarf> findByBetriebId(UUID betriebId) {
        return jpaRepository.findByBetriebId(betriebId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Bedarf> findByStatus(BedarfStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public BedarfPageResult findWithPagination(int page, int size, BedarfStatus status, 
                                             LocalDate datumVon, LocalDate datumBis) {
        PageRequest pageRequest = PageRequest.of(page, size);
        
        var pageResult = jpaRepository.findWithFilters(status, datumVon, datumBis, pageRequest);
        
        List<Bedarf> content = pageResult.getContent()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
        
        return new BedarfPageResult(
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
} 