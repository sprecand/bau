package com.bau.application.usecase;

import com.bau.application.domain.bedarf.Bedarf;
import com.bau.application.domain.bedarf.BedarfStatus;
import com.bau.application.port.in.BedarfUseCase;
import com.bau.application.port.out.BedarfRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BedarfService Use Case Tests")
class BedarfServiceTest {

    @Mock
    private BedarfRepository bedarfRepository;

    private BedarfService bedarfService;

    @BeforeEach
    void setUp() {
        bedarfService = new BedarfService(bedarfRepository);
    }

    @Nested
    @DisplayName("Create Bedarf")
    class CreateBedarfTests {

        @Test
        @DisplayName("Should create bedarf successfully")
        void shouldCreateBedarfSuccessfully() {
            // Given
            Bedarf inputBedarf = createValidBedarf();
            Bedarf savedBedarf = inputBedarf.toBuilder().id(UUID.randomUUID()).build();
            
            when(bedarfRepository.save(any(Bedarf.class))).thenReturn(savedBedarf);

            // When
            Bedarf result = bedarfService.createBedarf(inputBedarf);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            verify(bedarfRepository).save(inputBedarf);
        }

        @Test
        @DisplayName("Should set default status when creating bedarf")
        void shouldSetDefaultStatusWhenCreatingBedarf() {
            // Given
            Bedarf inputBedarf = createValidBedarf().toBuilder().status(null).build();
            Bedarf savedBedarf = inputBedarf.toBuilder()
                    .id(UUID.randomUUID())
                    .status(BedarfStatus.AKTIV)
                    .build();
            
            when(bedarfRepository.save(any(Bedarf.class))).thenReturn(savedBedarf);

            // When
            Bedarf result = bedarfService.createBedarf(inputBedarf);

            // Then
            assertThat(result.getStatus()).isEqualTo(BedarfStatus.AKTIV);
        }
    }

    @Nested
    @DisplayName("Get Bedarf")
    class GetBedarfTests {

        @Test
        @DisplayName("Should get bedarf by id successfully")
        void shouldGetBedarfByIdSuccessfully() {
            // Given
            UUID bedarfId = UUID.randomUUID();
            Bedarf expectedBedarf = createValidBedarf().toBuilder().id(bedarfId).build();
            
            when(bedarfRepository.findById(bedarfId)).thenReturn(Optional.of(expectedBedarf));

            // When
            Optional<Bedarf> result = bedarfService.getBedarfById(bedarfId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(bedarfId);
            verify(bedarfRepository).findById(bedarfId);
        }

        @Test
        @DisplayName("Should return empty when bedarf not found")
        void shouldReturnEmptyWhenBedarfNotFound() {
            // Given
            UUID bedarfId = UUID.randomUUID();
            when(bedarfRepository.findById(bedarfId)).thenReturn(Optional.empty());

            // When
            Optional<Bedarf> result = bedarfService.getBedarfById(bedarfId);

            // Then
            assertThat(result).isEmpty();
            verify(bedarfRepository).findById(bedarfId);
        }
    }

    @Nested
    @DisplayName("Get Bedarfs by Betrieb")
    class GetBedarfsByBetriebTests {

        @Test
        @DisplayName("Should get bedarfs by betrieb id successfully")
        void shouldGetBedarfsByBetriebIdSuccessfully() {
            // Given
            UUID betriebId = UUID.randomUUID();
            List<Bedarf> expectedBedarfs = Arrays.asList(
                    createValidBedarf().toBuilder().betriebId(betriebId).build(),
                    createValidBedarf().toBuilder().betriebId(betriebId).build()
            );
            
            when(bedarfRepository.findByBetriebId(betriebId)).thenReturn(expectedBedarfs);

            // When
            List<Bedarf> result = bedarfService.getBedarfsByBetriebId(betriebId);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(bedarf -> bedarf.getBetriebId().equals(betriebId));
            verify(bedarfRepository).findByBetriebId(betriebId);
        }

        @Test
        @DisplayName("Should return empty list when no bedarfs found for betrieb")
        void shouldReturnEmptyListWhenNoBedarfsFoundForBetrieb() {
            // Given
            UUID betriebId = UUID.randomUUID();
            when(bedarfRepository.findByBetriebId(betriebId)).thenReturn(Arrays.asList());

            // When
            List<Bedarf> result = bedarfService.getBedarfsByBetriebId(betriebId);

            // Then
            assertThat(result).isEmpty();
            verify(bedarfRepository).findByBetriebId(betriebId);
        }
    }

    @Nested
    @DisplayName("Update Bedarf")
    class UpdateBedarfTests {

        @Test
        @DisplayName("Should update bedarf successfully")
        void shouldUpdateBedarfSuccessfully() {
            // Given
            UUID bedarfId = UUID.randomUUID();
            Bedarf existingBedarf = createValidBedarf().toBuilder().id(bedarfId).build();
            Bedarf updatedBedarf = existingBedarf.toBuilder()
                    .holzbauAnzahl(5)
                    .zimmermannAnzahl(3)
                    .build();
            
            when(bedarfRepository.findById(bedarfId)).thenReturn(Optional.of(existingBedarf));
            when(bedarfRepository.save(any(Bedarf.class))).thenReturn(updatedBedarf);

            // When
            Optional<Bedarf> result = bedarfService.updateBedarf(bedarfId, updatedBedarf);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getHolzbauAnzahl()).isEqualTo(5);
            assertThat(result.get().getZimmermannAnzahl()).isEqualTo(3);
            verify(bedarfRepository).findById(bedarfId);
            verify(bedarfRepository).save(any(Bedarf.class));
        }

        @Test
        @DisplayName("Should return empty when updating non-existent bedarf")
        void shouldReturnEmptyWhenUpdatingNonExistentBedarf() {
            // Given
            UUID bedarfId = UUID.randomUUID();
            Bedarf updatedBedarf = createValidBedarf();
            
            when(bedarfRepository.findById(bedarfId)).thenReturn(Optional.empty());

            // When
            Optional<Bedarf> result = bedarfService.updateBedarf(bedarfId, updatedBedarf);

            // Then
            assertThat(result).isEmpty();
            verify(bedarfRepository).findById(bedarfId);
            verify(bedarfRepository, never()).save(any(Bedarf.class));
        }
    }

    @Nested
    @DisplayName("Delete Bedarf")
    class DeleteBedarfTests {

        @Test
        @DisplayName("Should delete bedarf successfully")
        void shouldDeleteBedarfSuccessfully() {
            // Given
            UUID bedarfId = UUID.randomUUID();
            when(bedarfRepository.existsById(bedarfId)).thenReturn(true);
            when(bedarfRepository.deleteById(bedarfId)).thenReturn(true);

            // When
            boolean result = bedarfService.deleteBedarf(bedarfId);

            // Then
            assertThat(result).isTrue();
            verify(bedarfRepository).existsById(bedarfId);
            verify(bedarfRepository).deleteById(bedarfId);
        }

        @Test
        @DisplayName("Should return false when deleting non-existent bedarf")
        void shouldReturnFalseWhenDeletingNonExistentBedarf() {
            // Given
            UUID bedarfId = UUID.randomUUID();
            when(bedarfRepository.existsById(bedarfId)).thenReturn(false);

            // When
            boolean result = bedarfService.deleteBedarf(bedarfId);

            // Then
            assertThat(result).isFalse();
            verify(bedarfRepository).existsById(bedarfId);
            verify(bedarfRepository, never()).deleteById(bedarfId);
        }
    }

    @Nested
    @DisplayName("Pagination Tests")
    class PaginationTests {

        @Test
        @DisplayName("Should get bedarfs with pagination")
        void shouldGetBedarfsWithPagination() {
            // Given
            List<Bedarf> bedarfs = Arrays.asList(
                    createValidBedarf(),
                    createValidBedarf(),
                    createValidBedarf()
            );
            
            BedarfRepository.BedarfPageResult pageResult = new BedarfRepository.BedarfPageResult(
                    bedarfs, 3, 1, 0, 20
            );
            
            when(bedarfRepository.findWithPagination(eq(0), eq(20), any(), any(), any()))
                    .thenReturn(pageResult);

            // When
            BedarfUseCase.BedarfPageResult result = bedarfService.getBedarfs(0, 20, null, null, null);

            // Then
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getCurrentPage()).isEqualTo(0);
            assertThat(result.getPageSize()).isEqualTo(20);
            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.isHasNext()).isFalse();
            assertThat(result.isHasPrevious()).isFalse();
        }
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