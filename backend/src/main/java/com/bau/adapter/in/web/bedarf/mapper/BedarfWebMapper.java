package com.bau.adapter.in.web.bedarf.mapper;

import com.bau.adapter.in.web.dto.BedarfResponse;
import com.bau.adapter.in.web.dto.CreateBedarfRequest;
import com.bau.adapter.in.web.dto.UpdateBedarfRequest;
import com.bau.application.domain.bedarf.Bedarf;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Bedarf domain objects and web DTOs.
 */
@Component
public class BedarfWebMapper {
    
    /**
     * Converts a CreateBedarfRequest to a Bedarf domain object.
     * @param request the create request
     * @return the domain object
     */
    public Bedarf toDomain(CreateBedarfRequest request) {
        if (request == null) {
            return null;
        }
        
        return Bedarf.builder()
                .holzbauAnzahl(request.getHolzbauAnzahl())
                .zimmermannAnzahl(request.getZimmermannAnzahl())
                .datumVon(request.getDatumVon())
                .datumBis(request.getDatumBis())
                .adresse(request.getAdresse())
                .mitWerkzeug(request.getMitWerkzeug())
                .mitFahrzeug(request.getMitFahrzeug())
                .build();
    }
    
    /**
     * Converts an UpdateBedarfRequest to a Bedarf domain object.
     * @param request the update request
     * @return the domain object
     */
    public Bedarf toDomain(UpdateBedarfRequest request) {
        if (request == null) {
            return null;
        }
        
        return Bedarf.builder()
                .holzbauAnzahl(request.getHolzbauAnzahl())
                .zimmermannAnzahl(request.getZimmermannAnzahl())
                .datumVon(request.getDatumVon())
                .datumBis(request.getDatumBis())
                .adresse(request.getAdresse())
                .mitWerkzeug(request.getMitWerkzeug())
                .mitFahrzeug(request.getMitFahrzeug())
                .build();
    }
    
    /**
     * Converts a Bedarf domain object to a BedarfResponse.
     * @param bedarf the domain object
     * @return the response DTO
     */
    public BedarfResponse toResponse(Bedarf bedarf) {
        if (bedarf == null) {
            return null;
        }
        
        BedarfResponse response = new BedarfResponse()
                .id(bedarf.getId())
                .betriebId(bedarf.getBetriebId())
                .holzbauAnzahl(bedarf.getHolzbauAnzahl())
                .zimmermannAnzahl(bedarf.getZimmermannAnzahl())
                .datumVon(bedarf.getDatumVon())
                .datumBis(bedarf.getDatumBis())
                .adresse(bedarf.getAdresse())
                .mitWerkzeug(bedarf.getMitWerkzeug())
                .mitFahrzeug(bedarf.getMitFahrzeug());
        
        if (bedarf.getStatus() != null) {
            response.status(BedarfResponse.StatusEnum.fromValue(bedarf.getStatus().name()));
        }
        
        return response;
    }
} 