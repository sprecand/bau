package com.bau.application.domain.betrieb;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

/**
 * Domain object representing a company (Betrieb).
 * Contains business logic and validation rules.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = false)
public class Betrieb {
    
    private UUID id;
    private String name;
    private String adresse;
    private String email;
    private String telefon;
    private BetriebStatus status;
    
    /**
     * Sets the ID if not already set.
     * @param id the ID to set
     */
    public void setId(UUID id) {
        if (this.id == null) {
            this.id = id;
        }
    }
} 