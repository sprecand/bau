package com.bau.adapter.out.persistence.betrieb;

import com.bau.application.domain.betrieb.BetriebStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity for Betrieb table.
 * Represents the database structure for betrieb data.
 */
@Entity
@Table(name = "betrieb")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = false)
@EntityListeners(AuditingEntityListener.class)
public class BetriebEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "adresse", nullable = false)
    private String adresse;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "telefon")
    private String telefon;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BetriebStatus status;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = BetriebStatus.AKTIV;
        }
    }
} 