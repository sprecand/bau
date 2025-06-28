package com.bau.adapter.out.persistence.bedarf;

import com.bau.application.domain.bedarf.BedarfStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for Bedarf persistence.
 */
@Entity
@Table(name = "bedarf")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BedarfEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "betrieb_id", nullable = false)
    private UUID betriebId;
    
    @Column(name = "holzbau_anzahl", nullable = false)
    private Integer holzbauAnzahl;
    
    @Column(name = "zimmermann_anzahl", nullable = false)
    private Integer zimmermannAnzahl;
    
    @Column(name = "datum_von", nullable = false)
    private LocalDate datumVon;
    
    @Column(name = "datum_bis", nullable = false)
    private LocalDate datumBis;
    
    @Column(name = "adresse", nullable = false, length = 1000)
    private String adresse;
    
    @Column(name = "mit_werkzeug")
    private Boolean mitWerkzeug;
    
    @Column(name = "mit_fahrzeug")
    private Boolean mitFahrzeug;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BedarfStatus status;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
} 