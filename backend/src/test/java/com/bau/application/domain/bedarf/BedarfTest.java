package com.bau.application.domain.bedarf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Bedarf Domain Entity Tests")
class BedarfTest {

    @Nested
    @DisplayName("Date Range Validation")
    class DateRangeValidationTests {

        @Test
        @DisplayName("Should return true for valid date range")
        void shouldReturnTrueForValidDateRange() {
            // Given
            Bedarf bedarf = Bedarf.builder()
                    .datumVon(LocalDate.of(2024, 1, 1))
                    .datumBis(LocalDate.of(2024, 1, 31))
                    .build();

            // When
            boolean result = bedarf.isValidDateRange();

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false when end date is before start date")
        void shouldReturnFalseWhenEndDateIsBeforeStartDate() {
            // Given
            Bedarf bedarf = Bedarf.builder()
                    .datumVon(LocalDate.of(2024, 1, 31))
                    .datumBis(LocalDate.of(2024, 1, 1))
                    .build();

            // When
            boolean result = bedarf.isValidDateRange();

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for null dates")
        void shouldReturnFalseForNullDates() {
            // Given
            Bedarf bedarf = Bedarf.builder()
                    .datumVon(null)
                    .datumBis(null)
                    .build();

            // When
            boolean result = bedarf.isValidDateRange();

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Tool Requirements")
    class ToolRequirementsTests {

        @Test
        @DisplayName("Should require tools when mitWerkzeug is true")
        void shouldRequireToolsWhenMitWerkzeugIsTrue() {
            // Given
            Bedarf bedarf = Bedarf.builder()
                    .mitWerkzeug(true)
                    .holzbauAnzahl(2)
                    .zimmermannAnzahl(1)
                    .build();

            // When
            boolean result = bedarf.requiresTools();

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should not require tools when mitWerkzeug is false")
        void shouldNotRequireToolsWhenMitWerkzeugIsFalse() {
            // Given
            Bedarf bedarf = Bedarf.builder()
                    .mitWerkzeug(false)
                    .holzbauAnzahl(2)
                    .zimmermannAnzahl(1)
                    .build();

            // When
            boolean result = bedarf.requiresTools();

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Total Workers Calculation")
    class TotalWorkersCalculationTests {

        @Test
        @DisplayName("Should calculate total workers correctly")
        void shouldCalculateTotalWorkersCorrectly() {
            // Given
            Bedarf bedarf = Bedarf.builder()
                    .holzbauAnzahl(3)
                    .zimmermannAnzahl(2)
                    .build();

            // When
            int totalWorkers = bedarf.getTotalWorkers();

            // Then
            assertThat(totalWorkers).isEqualTo(5);
        }

        @Test
        @DisplayName("Should handle null values")
        void shouldHandleNullValues() {
            // Given
            Bedarf bedarf = Bedarf.builder()
                    .holzbauAnzahl(null)
                    .zimmermannAnzahl(null)
                    .build();

            // When
            int totalWorkers = bedarf.getTotalWorkers();

            // Then
            assertThat(totalWorkers).isEqualTo(0);
        }
    }
} 