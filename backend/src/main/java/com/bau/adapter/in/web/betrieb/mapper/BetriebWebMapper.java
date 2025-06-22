package com.bau.adapter.in.web.betrieb.mapper;

import com.bau.adapter.in.web.dto.BetriebResponse;
import com.bau.adapter.in.web.dto.CreateBetriebRequest;
import com.bau.adapter.in.web.dto.UpdateBetriebRequest;
import com.bau.application.domain.betrieb.Betrieb;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Betrieb domain objects and web DTOs.
 */
@Component
public class BetriebWebMapper {
    
    /**
     * Converts a CreateBetriebRequest to a Betrieb domain object.
     * @param request the create request
     * @return the domain object
     */
    public Betrieb toDomain(CreateBetriebRequest request) {
        if (request == null) {
            return null;
        }
        
        return Betrieb.builder()
                .name(request.getName())
                .adresse(request.getAdresse())
                .email(request.getEmail())
                .telefon(request.getTelefon())
                .build();
    }
    
    /**
     * Converts an UpdateBetriebRequest to a Betrieb domain object.
     * @param request the update request
     * @return the domain object
     */
    public Betrieb toDomain(UpdateBetriebRequest request) {
        if (request == null) {
            return null;
        }
        
        return Betrieb.builder()
                .name(request.getName())
                .adresse(request.getAdresse())
                .email(request.getEmail())
                .telefon(request.getTelefon())
                .build();
    }
    
    /**
     * Converts a Betrieb domain object to a BetriebResponse.
     * @param betrieb the domain object
     * @return the response DTO
     */
    public BetriebResponse toResponse(Betrieb betrieb) {
        if (betrieb == null) {
            return null;
        }
        
        BetriebResponse response = new BetriebResponse()
                .id(betrieb.getId())
                .name(betrieb.getName())
                .adresse(betrieb.getAdresse())
                .email(betrieb.getEmail())
                .telefon(betrieb.getTelefon());
        
        if (betrieb.getStatus() != null) {
            response.status(BetriebResponse.StatusEnum.fromValue(betrieb.getStatus().name()));
        }
        
        return response;
    }
} 