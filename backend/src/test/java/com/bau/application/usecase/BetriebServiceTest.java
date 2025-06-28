package com.bau.application.usecase;

import com.bau.application.domain.betrieb.Betrieb;
import com.bau.application.domain.betrieb.BetriebStatus;
import com.bau.application.port.in.BetriebUseCase;
import com.bau.application.port.out.BetriebRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BetriebService Tests")
class BetriebServiceTest {

    @Mock
    private BetriebRepository betriebRepository;

    @InjectMocks
    private BetriebService betriebService;

    private Betrieb testBetrieb;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testBetrieb = Betrieb.builder()
                .id(testId)
                .name("Test Bau GmbH")
                .email("test@bau.ch")
                .telefon("041 123 45 67")
                .adresse("Musterstrasse 1, 6000 Luzern")
                .status(BetriebStatus.AKTIV)
                .build();
    }

    @Nested
    @DisplayName("Create Betrieb Tests")
    class CreateBetriebTests {

        @Test
        @DisplayName("Should create betrieb successfully")
        void shouldCreateBetriebSuccessfully() {
            // Given
            when(betriebRepository.existsByEmail("test@bau.ch")).thenReturn(false);
            when(betriebRepository.save(any(Betrieb.class))).thenReturn(testBetrieb);

            // When
            Betrieb result = betriebService.createBetrieb(testBetrieb);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Test Bau GmbH");
            assertThat(result.getEmail()).isEqualTo("test@bau.ch");
            assertThat(result.getStatus()).isEqualTo(BetriebStatus.AKTIV);
            verify(betriebRepository).existsByEmail("test@bau.ch");
            verify(betriebRepository).save(testBetrieb);
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // Given
            when(betriebRepository.existsByEmail("test@bau.ch")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> betriebService.createBetrieb(testBetrieb))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Betrieb with this email already exists");
            
            verify(betriebRepository).existsByEmail("test@bau.ch");
            verify(betriebRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when name is missing")
        void shouldThrowExceptionWhenNameIsMissing() {
            // Given
            Betrieb invalidBetrieb = Betrieb.builder()
                    .email("test@bau.ch")
                    .adresse("Musterstrasse 1")
                    .build();

            // When & Then
            assertThatThrownBy(() -> betriebService.createBetrieb(invalidBetrieb))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Company name is required");
        }

        @Test
        @DisplayName("Should throw exception when email is invalid")
        void shouldThrowExceptionWhenEmailIsInvalid() {
            // Given
            Betrieb invalidBetrieb = Betrieb.builder()
                    .name("Test Bau GmbH")
                    .email("invalid-email")
                    .adresse("Musterstrasse 1")
                    .build();

            // When & Then
            assertThatThrownBy(() -> betriebService.createBetrieb(invalidBetrieb))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid email format");
        }
    }

    @Nested
    @DisplayName("Get Betrieb Tests")
    class GetBetriebTests {

        @Test
        @DisplayName("Should get betrieb by id successfully")
        void shouldGetBetriebByIdSuccessfully() {
            // Given
            when(betriebRepository.findById(testId)).thenReturn(Optional.of(testBetrieb));

            // When
            Optional<Betrieb> result = betriebService.getBetriebById(testId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(testId);
            assertThat(result.get().getName()).isEqualTo("Test Bau GmbH");
            verify(betriebRepository).findById(testId);
        }

        @Test
        @DisplayName("Should return empty when betrieb not found")
        void shouldReturnEmptyWhenBetriebNotFound() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(betriebRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When
            Optional<Betrieb> result = betriebService.getBetriebById(nonExistentId);

            // Then
            assertThat(result).isEmpty();
            verify(betriebRepository).findById(nonExistentId);
        }

        @Test
        @DisplayName("Should get betrieb by email successfully")
        void shouldGetBetriebByEmailSuccessfully() {
            // Given
            when(betriebRepository.findByEmail("test@bau.ch")).thenReturn(Optional.of(testBetrieb));

            // When
            Optional<Betrieb> result = betriebService.getBetriebByEmail("test@bau.ch");

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("test@bau.ch");
            verify(betriebRepository).findByEmail("test@bau.ch");
        }

        @Test
        @DisplayName("Should get active betriebe")
        void shouldGetActiveBetriebe() {
            // Given
            List<Betrieb> activeBetriebe = List.of(testBetrieb);
            when(betriebRepository.findByStatus(BetriebStatus.AKTIV)).thenReturn(activeBetriebe);

            // When
            List<Betrieb> result = betriebService.getActiveBetriebs();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(BetriebStatus.AKTIV);
            verify(betriebRepository).findByStatus(BetriebStatus.AKTIV);
        }
    }

    @Nested
    @DisplayName("Update Betrieb Tests")
    class UpdateBetriebTests {

        @Test
        @DisplayName("Should update betrieb successfully")
        void shouldUpdateBetriebSuccessfully() {
            // Given
            Betrieb updatedBetrieb = Betrieb.builder()
                    .name("Updated Bau GmbH")
                    .email("test@bau.ch") // Same email
                    .telefon("041 987 65 43")
                    .adresse("Neue Strasse 2, 6001 Luzern")
                    .status(BetriebStatus.AKTIV)
                    .build();

            when(betriebRepository.findById(testId)).thenReturn(Optional.of(testBetrieb));
            when(betriebRepository.save(any(Betrieb.class))).thenReturn(updatedBetrieb);

            // When
            Optional<Betrieb> result = betriebService.updateBetrieb(testId, updatedBetrieb);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Updated Bau GmbH");
            verify(betriebRepository).findById(testId);
            verify(betriebRepository).save(any(Betrieb.class));
        }

        @Test
        @DisplayName("Should return empty when betrieb not found for update")
        void shouldReturnEmptyWhenBetriebNotFoundForUpdate() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(betriebRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When
            Optional<Betrieb> result = betriebService.updateBetrieb(nonExistentId, testBetrieb);

            // Then
            assertThat(result).isEmpty();
            verify(betriebRepository).findById(nonExistentId);
            verify(betriebRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when updating to existing email")
        void shouldThrowExceptionWhenUpdatingToExistingEmail() {
            // Given
            Betrieb existingBetrieb = Betrieb.builder()
                    .id(testId)
                    .name("Test Bau GmbH")
                    .email("old@bau.ch")
                    .build();

            Betrieb updatedBetrieb = Betrieb.builder()
                    .name("Test Bau GmbH")
                    .email("existing@bau.ch")
                    .build();

            when(betriebRepository.findById(testId)).thenReturn(Optional.of(existingBetrieb));
            when(betriebRepository.existsByEmail("existing@bau.ch")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> betriebService.updateBetrieb(testId, updatedBetrieb))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Betrieb with this email already exists");
        }
    }

    @Nested
    @DisplayName("Update Betrieb Status Tests")
    class UpdateBetriebStatusTests {

        @Test
        @DisplayName("Should update betrieb status successfully")
        void shouldUpdateBetriebStatusSuccessfully() {
            // Given
            BetriebStatus newStatus = BetriebStatus.GESPERRT;
            Betrieb updatedBetrieb = Betrieb.builder()
                    .id(testId)
                    .name(testBetrieb.getName())
                    .email(testBetrieb.getEmail())
                    .status(newStatus)
                    .build();

            when(betriebRepository.findById(testId)).thenReturn(Optional.of(testBetrieb));
            when(betriebRepository.save(any(Betrieb.class))).thenReturn(updatedBetrieb);

            // When
            Optional<Betrieb> result = betriebService.updateBetriebStatus(testId, newStatus);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo(BetriebStatus.GESPERRT);
            verify(betriebRepository).findById(testId);
            verify(betriebRepository).save(any(Betrieb.class));
        }

        @Test
        @DisplayName("Should return empty when betrieb not found for status update")
        void shouldReturnEmptyWhenBetriebNotFoundForStatusUpdate() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(betriebRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When
            Optional<Betrieb> result = betriebService.updateBetriebStatus(nonExistentId, BetriebStatus.GESPERRT);

            // Then
            assertThat(result).isEmpty();
            verify(betriebRepository).findById(nonExistentId);
            verify(betriebRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Betrieb Tests")
    class DeleteBetriebTests {

        @Test
        @DisplayName("Should delete betrieb successfully")
        void shouldDeleteBetriebSuccessfully() {
            // Given
            when(betriebRepository.existsById(testId)).thenReturn(true);
            when(betriebRepository.deleteById(testId)).thenReturn(true);

            // When
            boolean result = betriebService.deleteBetrieb(testId);

            // Then
            assertThat(result).isTrue();
            verify(betriebRepository).existsById(testId);
            verify(betriebRepository).deleteById(testId);
        }

        @Test
        @DisplayName("Should return false when betrieb not found for deletion")
        void shouldReturnFalseWhenBetriebNotFoundForDeletion() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(betriebRepository.existsById(nonExistentId)).thenReturn(false);

            // When
            boolean result = betriebService.deleteBetrieb(nonExistentId);

            // Then
            assertThat(result).isFalse();
            verify(betriebRepository).existsById(nonExistentId);
            verify(betriebRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Pagination Tests")
    class PaginationTests {

        @Test
        @DisplayName("Should get betriebe with pagination")
        void shouldGetBetriebeWithPagination() {
            // Given
            List<Betrieb> betriebe = List.of(testBetrieb);
            BetriebRepository.BetriebPageResult repositoryResult = 
                    new BetriebRepository.BetriebPageResult(betriebe, 1, 1, 0, 10);
            
            when(betriebRepository.findWithPagination(0, 10, BetriebStatus.AKTIV))
                    .thenReturn(repositoryResult);

            // When
            BetriebUseCase.BetriebPageResult result = betriebService.getBetriebs(0, 10, BetriebStatus.AKTIV);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getCurrentPage()).isEqualTo(0);
            assertThat(result.getPageSize()).isEqualTo(10);
            verify(betriebRepository).findWithPagination(0, 10, BetriebStatus.AKTIV);
        }
    }
} 