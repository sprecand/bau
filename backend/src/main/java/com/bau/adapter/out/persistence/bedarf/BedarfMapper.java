package com.bau.adapter.out.persistence.bedarf;

import com.bau.application.domain.bedarf.Bedarf;
import com.bau.application.domain.bedarf.BedarfStatus;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Bedarf domain objects and BedarfEntity.
 */
@Component
public class BedarfMapper {
    
    /**
     * Converts a BedarfEntity to a Bedarf domain object.
     * @param entity the JPA entity
     * @return the domain object
     */
    public Bedarf toDomain(BedarfEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Bedarf.builder()
                .id(entity.getId())
                .betriebId(entity.getBetriebId())
                .holzbauAnzahl(entity.getHolzbauAnzahl())
                .zimmermannAnzahl(entity.getZimmermannAnzahl())
                .datumVon(entity.getDatumVon())
                .datumBis(entity.getDatumBis())
                .adresse(entity.getAdresse())
                .mitWerkzeug(entity.getMitWerkzeug())
                .mitFahrzeug(entity.getMitFahrzeug())
                .status(entity.getStatus())
                .build();
    }
    
    /**
     * Converts a Bedarf domain object to a BedarfEntity.
     * @param bedarf the domain object
     * @return the JPA entity
     */
    public BedarfEntity toEntity(Bedarf bedarf) {
        if (bedarf == null) {
            return null;
        }
        
        return BedarfEntity.builder()
                .id(bedarf.getId())
                .betriebId(bedarf.getBetriebId())
                .holzbauAnzahl(bedarf.getHolzbauAnzahl())
                .zimmermannAnzahl(bedarf.getZimmermannAnzahl())
                .datumVon(bedarf.getDatumVon())
                .datumBis(bedarf.getDatumBis())
                .adresse(bedarf.getAdresse())
                .mitWerkzeug(bedarf.getMitWerkzeug())
                .mitFahrzeug(bedarf.getMitFahrzeug())
                .status(bedarf.getStatus())
                .build();
    }
} 