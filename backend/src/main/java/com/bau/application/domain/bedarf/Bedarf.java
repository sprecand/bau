package com.bau.application.domain.bedarf;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Domain object representing a construction demand (Bedarf).
 * Contains business logic and validation rules.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = false)
public class Bedarf {
    
    private UUID id;
    private UUID betriebId;
    private Integer holzbauAnzahl;
    private Integer zimmermannAnzahl;
    private LocalDate datumVon;
    private LocalDate datumBis;
    private String adresse;
    private Boolean mitWerkzeug;
    private Boolean mitFahrzeug;
    private BedarfStatus status;
    
    /**
     * Sets the ID if not already set.
     * @param id the ID to set
     */
    public void setId(UUID id) {
        if (this.id == null) {
            this.id = id;
        }
    }
    
    /**
     * Sets the betrieb ID if not already set.
     * @param betriebId the betrieb ID to set
     */
    public void setBetriebId(UUID betriebId) {
        if (this.betriebId == null) {
            this.betriebId = betriebId;
        }
    }
    
    /**
     * Validates if the date range is valid (end date after start date).
     * @return true if the date range is valid
     */
    public boolean isValidDateRange() {
        return datumVon != null && datumBis != null && datumBis.isAfter(datumVon);
    }
    
    /**
     * Checks if tools are required for this bedarf.
     * @return true if tools are required
     */
    public boolean requiresTools() {
        return Boolean.TRUE.equals(mitWerkzeug) && (holzbauAnzahl > 0 || zimmermannAnzahl > 0);
    }
    
    /**
     * Checks if a vehicle is required for this bedarf.
     * @return true if a vehicle is required
     */
    public boolean requiresVehicle() {
        return Boolean.TRUE.equals(mitFahrzeug);
    }
    
    /**
     * Gets the total number of workers needed.
     * @return total number of workers
     */
    public int getTotalWorkers() {
        return (holzbauAnzahl != null ? holzbauAnzahl : 0) + 
               (zimmermannAnzahl != null ? zimmermannAnzahl : 0);
    }
    
    /**
     * Validates if at least one worker type is specified.
     * @return true if at least one worker is needed
     */
    public boolean hasWorkersSpecified() {
        return getTotalWorkers() > 0;
    }
} 