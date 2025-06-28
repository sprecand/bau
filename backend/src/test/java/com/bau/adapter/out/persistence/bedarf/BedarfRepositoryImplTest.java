package com.bau.adapter.out.persistence.bedarf;

import com.bau.application.domain.bedarf.Bedarf;
import com.bau.application.domain.bedarf.BedarfStatus;
import com.bau.shared.config.JpaConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({BedarfRepositoryImpl.class, BedarfMapper.class, JpaConfig.class})
@DisplayName("BedarfRepository Integration Tests")
class BedarfRepositoryImplTest {

    @Autowired
    private BedarfRepositoryImpl bedarfRepository;

    @Autowired
    private BedarfJpaRepository jpaRepository;

    @Test
    @DisplayName("Should save and retrieve bedarf successfully")
    void shouldSaveAndRetrieveBedarfSuccessfully() {
        // Given
        Bedarf bedarf = createValidBedarf();

        // When
        Bedarf savedBedarf = bedarfRepository.save(bedarf);

        // Then
        assertThat(savedBedarf.getId()).isNotNull();
        assertThat(savedBedarf.getBetriebId()).isEqualTo(bedarf.getBetriebId());
        assertThat(savedBedarf.getHolzbauAnzahl()).isEqualTo(bedarf.getHolzbauAnzahl());
        assertThat(savedBedarf.getZimmermannAnzahl()).isEqualTo(bedarf.getZimmermannAnzahl());
        assertThat(savedBedarf.getDatumVon()).isEqualTo(bedarf.getDatumVon());
        assertThat(savedBedarf.getDatumBis()).isEqualTo(bedarf.getDatumBis());
        assertThat(savedBedarf.getAdresse()).isEqualTo(bedarf.getAdresse());
        assertThat(savedBedarf.getMitWerkzeug()).isEqualTo(bedarf.getMitWerkzeug());
        assertThat(savedBedarf.getMitFahrzeug()).isEqualTo(bedarf.getMitFahrzeug());
        assertThat(savedBedarf.getStatus()).isEqualTo(bedarf.getStatus());
    }

    @Test
    @DisplayName("Should find bedarf by id")
    void shouldFindBedarfById() {
        // Given
        Bedarf bedarf = createValidBedarf();
        Bedarf savedBedarf = bedarfRepository.save(bedarf);

        // When
        Optional<Bedarf> foundBedarf = bedarfRepository.findById(savedBedarf.getId());

        // Then
        assertThat(foundBedarf).isPresent();
        assertThat(foundBedarf.get().getId()).isEqualTo(savedBedarf.getId());
        assertThat(foundBedarf.get().getBetriebId()).isEqualTo(savedBedarf.getBetriebId());
    }

    @Test
    @DisplayName("Should return empty when bedarf not found")
    void shouldReturnEmptyWhenBedarfNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<Bedarf> foundBedarf = bedarfRepository.findById(nonExistentId);

        // Then
        assertThat(foundBedarf).isEmpty();
    }

    @Test
    @DisplayName("Should find bedarfs by betrieb id")
    void shouldFindBedarfsByBetriebId() {
        // Given
        UUID betriebId = UUID.randomUUID();
        Bedarf bedarf1 = createValidBedarf().toBuilder().betriebId(betriebId).build();
        Bedarf bedarf2 = createValidBedarf().toBuilder().betriebId(betriebId).build();
        Bedarf bedarf3 = createValidBedarf(); // Different betrieb

        bedarfRepository.save(bedarf1);
        bedarfRepository.save(bedarf2);
        bedarfRepository.save(bedarf3);

        // When
        List<Bedarf> foundBedarfs = bedarfRepository.findByBetriebId(betriebId);

        // Then
        assertThat(foundBedarfs).hasSize(2);
        assertThat(foundBedarfs).allMatch(bedarf -> bedarf.getBetriebId().equals(betriebId));
    }

    @Test
    @DisplayName("Should update bedarf successfully")
    void shouldUpdateBedarfSuccessfully() {
        // Given
        Bedarf bedarf = createValidBedarf();
        Bedarf savedBedarf = bedarfRepository.save(bedarf);

        // When
        Bedarf updatedBedarf = savedBedarf.toBuilder()
                .holzbauAnzahl(5)
                .zimmermannAnzahl(3)
                .status(BedarfStatus.AKTIV)
                .build();
        Bedarf result = bedarfRepository.save(updatedBedarf);

        // Then
        assertThat(result.getId()).isEqualTo(savedBedarf.getId());
        assertThat(result.getHolzbauAnzahl()).isEqualTo(5);
        assertThat(result.getZimmermannAnzahl()).isEqualTo(3);
        assertThat(result.getStatus()).isEqualTo(BedarfStatus.AKTIV);
    }

    @Test
    @DisplayName("Should delete bedarf successfully")
    void shouldDeleteBedarfSuccessfully() {
        // Given
        Bedarf bedarf = createValidBedarf();
        Bedarf savedBedarf = bedarfRepository.save(bedarf);

        // When
        boolean deleted = bedarfRepository.deleteById(savedBedarf.getId());

        // Then
        assertThat(deleted).isTrue();
        Optional<Bedarf> foundBedarf = bedarfRepository.findById(savedBedarf.getId());
        assertThat(foundBedarf).isEmpty();
    }

    @Test
    @DisplayName("Should check if bedarf exists")
    void shouldCheckIfBedarfExists() {
        // Given
        Bedarf bedarf = createValidBedarf();
        Bedarf savedBedarf = bedarfRepository.save(bedarf);

        // When & Then
        assertThat(bedarfRepository.existsById(savedBedarf.getId())).isTrue();
        assertThat(bedarfRepository.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void shouldHandleNullValuesCorrectly() {
        // Given
        Bedarf bedarf = Bedarf.builder()
                .betriebId(UUID.randomUUID())
                .holzbauAnzahl(1)
                .zimmermannAnzahl(1)
                .datumVon(LocalDate.now())
                .datumBis(LocalDate.now().plusDays(7))
                .adresse("Test Address")
                .mitWerkzeug(null) // null values
                .mitFahrzeug(null) // null values
                .status(null) // null status
                .build();

        // When
        Bedarf savedBedarf = bedarfRepository.save(bedarf);

        // Then
        assertThat(savedBedarf.getId()).isNotNull();
        assertThat(savedBedarf.getMitWerkzeug()).isNull();
        assertThat(savedBedarf.getMitFahrzeug()).isNull();
        assertThat(savedBedarf.getStatus()).isNull();
    }

    private Bedarf createValidBedarf() {
        return Bedarf.builder()
                .betriebId(UUID.randomUUID())
                .holzbauAnzahl(2)
                .zimmermannAnzahl(3)
                .datumVon(LocalDate.of(2024, 1, 1))
                .datumBis(LocalDate.of(2024, 1, 31))
                .adresse("Musterstrasse 123, 8001 Zürich")
                .mitWerkzeug(true)
                .mitFahrzeug(false)
                .status(BedarfStatus.AKTIV)
                .build();
    }
} 