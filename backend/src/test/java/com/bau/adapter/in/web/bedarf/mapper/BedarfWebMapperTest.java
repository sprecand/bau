package com.bau.adapter.in.web.bedarf.mapper;

import com.bau.adapter.in.web.dto.BedarfResponse;
import com.bau.adapter.in.web.dto.CreateBedarfRequest;
import com.bau.adapter.in.web.dto.UpdateBedarfRequest;
import com.bau.application.domain.bedarf.Bedarf;
import com.bau.application.domain.bedarf.BedarfStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BedarfWebMapper Tests")
class BedarfWebMapperTest {

    private BedarfWebMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BedarfWebMapper();
    }

    @Nested
    @DisplayName("toDomain from CreateBedarfRequest")
    class ToDomainFromCreateRequestTests {

        @Test
        @DisplayName("Should map CreateBedarfRequest to Bedarf domain object")
        void shouldMapCreateRequestToDomain() {
            // Given
            CreateBedarfRequest request = new CreateBedarfRequest()
                    .holzbauAnzahl(3)
                    .zimmermannAnzahl(2)
                    .datumVon(LocalDate.of(2024, 1, 15))
                    .datumBis(LocalDate.of(2024, 1, 20))
                    .adresse("Teststraße 123, 12345 Teststadt")
                    .mitWerkzeug(true)
                    .mitFahrzeug(false);

            // When
            Bedarf result = mapper.toDomain(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getHolzbauAnzahl()).isEqualTo(3);
            assertThat(result.getZimmermannAnzahl()).isEqualTo(2);
            assertThat(result.getDatumVon()).isEqualTo(LocalDate.of(2024, 1, 15));
            assertThat(result.getDatumBis()).isEqualTo(LocalDate.of(2024, 1, 20));
            assertThat(result.getAdresse()).isEqualTo("Teststraße 123, 12345 Teststadt");
            assertThat(result.getMitWerkzeug()).isTrue();
            assertThat(result.getMitFahrzeug()).isFalse();
        }

        @Test
        @DisplayName("Should return null when CreateBedarfRequest is null")
        void shouldReturnNullWhenCreateRequestIsNull() {
            // When
            Bedarf result = mapper.toDomain((CreateBedarfRequest) null);

            // Then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("toDomain from UpdateBedarfRequest")
    class ToDomainFromUpdateRequestTests {

        @Test
        @DisplayName("Should map UpdateBedarfRequest to Bedarf domain object")
        void shouldMapUpdateRequestToDomain() {
            // Given
            UpdateBedarfRequest request = new UpdateBedarfRequest()
                    .holzbauAnzahl(5)
                    .zimmermannAnzahl(3)
                    .datumVon(LocalDate.of(2024, 2, 1))
                    .datumBis(LocalDate.of(2024, 2, 10))
                    .adresse("Neue Straße 456, 67890 Neue Stadt")
                    .mitWerkzeug(false)
                    .mitFahrzeug(true);

            // When
            Bedarf result = mapper.toDomain(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getHolzbauAnzahl()).isEqualTo(5);
            assertThat(result.getZimmermannAnzahl()).isEqualTo(3);
            assertThat(result.getDatumVon()).isEqualTo(LocalDate.of(2024, 2, 1));
            assertThat(result.getDatumBis()).isEqualTo(LocalDate.of(2024, 2, 10));
            assertThat(result.getAdresse()).isEqualTo("Neue Straße 456, 67890 Neue Stadt");
            assertThat(result.getMitWerkzeug()).isFalse();
            assertThat(result.getMitFahrzeug()).isTrue();
        }

        @Test
        @DisplayName("Should return null when UpdateBedarfRequest is null")
        void shouldReturnNullWhenUpdateRequestIsNull() {
            // When
            Bedarf result = mapper.toDomain((UpdateBedarfRequest) null);

            // Then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("toResponse")
    class ToResponseTests {

        @Test
        @DisplayName("Should map Bedarf domain object to BedarfResponse")
        void shouldMapDomainToResponse() {
            // Given
            UUID bedarfId = UUID.randomUUID();
            UUID betriebId = UUID.randomUUID();
            Bedarf bedarf = Bedarf.builder()
                    .id(bedarfId)
                    .betriebId(betriebId)
                    .holzbauAnzahl(4)
                    .zimmermannAnzahl(2)
                    .datumVon(LocalDate.of(2024, 3, 1))
                    .datumBis(LocalDate.of(2024, 3, 15))
                    .adresse("Baustelle 789, 11111 Baustadt")
                    .mitWerkzeug(true)
                    .mitFahrzeug(true)
                    .status(BedarfStatus.AKTIV)
                    .build();

            // When
            BedarfResponse result = mapper.toResponse(bedarf);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(bedarfId);
            assertThat(result.getBetriebId()).isEqualTo(betriebId);
            assertThat(result.getHolzbauAnzahl()).isEqualTo(4);
            assertThat(result.getZimmermannAnzahl()).isEqualTo(2);
            assertThat(result.getDatumVon()).isEqualTo(LocalDate.of(2024, 3, 1));
            assertThat(result.getDatumBis()).isEqualTo(LocalDate.of(2024, 3, 15));
            assertThat(result.getAdresse()).isEqualTo("Baustelle 789, 11111 Baustadt");
            assertThat(result.getMitWerkzeug()).isTrue();
            assertThat(result.getMitFahrzeug()).isTrue();
            assertThat(result.getStatus()).isEqualTo(BedarfResponse.StatusEnum.AKTIV);
        }

        @Test
        @DisplayName("Should map Bedarf domain object to BedarfResponse with null status")
        void shouldMapDomainToResponseWithNullStatus() {
            // Given
            UUID bedarfId = UUID.randomUUID();
            UUID betriebId = UUID.randomUUID();
            Bedarf bedarf = Bedarf.builder()
                    .id(bedarfId)
                    .betriebId(betriebId)
                    .holzbauAnzahl(1)
                    .zimmermannAnzahl(1)
                    .datumVon(LocalDate.of(2024, 4, 1))
                    .datumBis(LocalDate.of(2024, 4, 5))
                    .adresse("Projekt 999, 22222 Projektstadt")
                    .mitWerkzeug(false)
                    .mitFahrzeug(false)
                    .status(null) // null status
                    .build();

            // When
            BedarfResponse result = mapper.toResponse(bedarf);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(bedarfId);
            assertThat(result.getBetriebId()).isEqualTo(betriebId);
            assertThat(result.getHolzbauAnzahl()).isEqualTo(1);
            assertThat(result.getZimmermannAnzahl()).isEqualTo(1);
            assertThat(result.getDatumVon()).isEqualTo(LocalDate.of(2024, 4, 1));
            assertThat(result.getDatumBis()).isEqualTo(LocalDate.of(2024, 4, 5));
            assertThat(result.getAdresse()).isEqualTo("Projekt 999, 22222 Projektstadt");
            assertThat(result.getMitWerkzeug()).isFalse();
            assertThat(result.getMitFahrzeug()).isFalse();
            assertThat(result.getStatus()).isNull();
        }

        @Test
        @DisplayName("Should return null when Bedarf is null")
        void shouldReturnNullWhenBedarfIsNull() {
            // When
            BedarfResponse result = mapper.toResponse(null);

            // Then
            assertThat(result).isNull();
        }
    }
} 