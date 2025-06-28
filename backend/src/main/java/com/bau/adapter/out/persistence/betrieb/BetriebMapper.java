package com.bau.adapter.out.persistence.betrieb;

import com.bau.application.domain.betrieb.Betrieb;
import com.bau.application.domain.betrieb.BetriebStatus;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Betrieb domain objects and BetriebEntity.
 */
@Component
public class BetriebMapper {
    
    /**
     * Converts a BetriebEntity to a Betrieb domain object.
     * @param entity the JPA entity
     * @return the domain object
     */
    public Betrieb toDomain(BetriebEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Betrieb.builder()
                .id(entity.getId())
                .name(entity.getName())
                .adresse(entity.getAdresse())
                .email(entity.getEmail())
                .telefon(entity.getTelefon())
                .status(entity.getStatus())
                .build();
    }
    
    /**
     * Converts a Betrieb domain object to a BetriebEntity.
     * @param betrieb the domain object
     * @return the JPA entity
     */
    public BetriebEntity toEntity(Betrieb betrieb) {
        if (betrieb == null) {
            return null;
        }
        
        return BetriebEntity.builder()
                .id(betrieb.getId())
                .name(betrieb.getName())
                .adresse(betrieb.getAdresse())
                .email(betrieb.getEmail())
                .telefon(betrieb.getTelefon())
                .status(betrieb.getStatus())
                .build();
    }
} 