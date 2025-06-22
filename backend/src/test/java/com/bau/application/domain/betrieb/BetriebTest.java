package com.bau.application.domain.betrieb;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Betrieb Domain Entity Tests")
class BetriebTest {

    @Nested
    @DisplayName("Builder Pattern")
    class BuilderPatternTests {

        @Test
        @DisplayName("Should create Betrieb with all fields using builder")
        void shouldCreateBetriebWithAllFields() {
            // Given
            UUID id = UUID.randomUUID();
            String name = "Bauunternehmen Müller AG";
            String adresse = "Bahnhofstrasse 123, 8001 Zürich";
            String email = "info@mueller-bau.ch";
            String telefon = "+41 44 123 45 67";

            // When
            Betrieb betrieb = Betrieb.builder()
                    .id(id)
                    .name(name)
                    .adresse(adresse)
                    .email(email)
                    .telefon(telefon)
                    .status(BetriebStatus.AKTIV)
                    .build();

            // Then
            assertThat(betrieb.getId()).isEqualTo(id);
            assertThat(betrieb.getName()).isEqualTo(name);
            assertThat(betrieb.getAdresse()).isEqualTo(adresse);
            assertThat(betrieb.getEmail()).isEqualTo(email);
            assertThat(betrieb.getTelefon()).isEqualTo(telefon);
            assertThat(betrieb.getStatus()).isEqualTo(BetriebStatus.AKTIV);
        }

        @Test
        @DisplayName("Should create minimal Betrieb with required fields only")
        void shouldCreateMinimalBetrieb() {
            // When
            Betrieb betrieb = Betrieb.builder()
                    .name("Test Betrieb")
                    .email("test@example.com")
                    .build();

            // Then
            assertThat(betrieb.getName()).isEqualTo("Test Betrieb");
            assertThat(betrieb.getEmail()).isEqualTo("test@example.com");
            assertThat(betrieb.getAdresse()).isNull();
            assertThat(betrieb.getTelefon()).isNull();
            assertThat(betrieb.getStatus()).isNull();
        }
    }

    @Nested
    @DisplayName("Status Management")
    class StatusManagementTests {

        @Test
        @DisplayName("Should handle all status types")
        void shouldHandleAllStatusTypes() {
            // Test AKTIV status
            Betrieb aktivBetrieb = Betrieb.builder()
                    .name("Aktiv Betrieb")
                    .status(BetriebStatus.AKTIV)
                    .build();
            assertThat(aktivBetrieb.getStatus()).isEqualTo(BetriebStatus.AKTIV);

            // Test INAKTIV status
            Betrieb inaktivBetrieb = Betrieb.builder()
                    .name("Inaktiv Betrieb")
                    .status(BetriebStatus.INAKTIV)
                    .build();
            assertThat(inaktivBetrieb.getStatus()).isEqualTo(BetriebStatus.INAKTIV);

            // Test GESPERRT status
            Betrieb gesperrtBetrieb = Betrieb.builder()
                    .name("Gesperrt Betrieb")
                    .status(BetriebStatus.GESPERRT)
                    .build();
            assertThat(gesperrtBetrieb.getStatus()).isEqualTo(BetriebStatus.GESPERRT);
        }
    }

    @Nested
    @DisplayName("Data Validation")
    class DataValidationTests {

        @Test
        @DisplayName("Should handle Swiss phone number format")
        void shouldHandleSwissPhoneNumberFormat() {
            // Given
            Betrieb betrieb = Betrieb.builder()
                    .name("Test Betrieb")
                    .telefon("+41 44 123 45 67")
                    .build();

            // Then
            assertThat(betrieb.getTelefon()).isEqualTo("+41 44 123 45 67");
        }

        @Test
        @DisplayName("Should handle Swiss email format")
        void shouldHandleSwissEmailFormat() {
            // Given
            Betrieb betrieb = Betrieb.builder()
                    .name("Test Betrieb")
                    .email("kontakt@bauunternehmen.ch")
                    .build();

            // Then
            assertThat(betrieb.getEmail()).isEqualTo("kontakt@bauunternehmen.ch");
        }

        @Test
        @DisplayName("Should handle Swiss address format")
        void shouldHandleSwissAddressFormat() {
            // Given
            String swissAddress = "Bahnhofstrasse 123, 8001 Zürich";
            Betrieb betrieb = Betrieb.builder()
                    .name("Test Betrieb")
                    .adresse(swissAddress)
                    .build();

            // Then
            assertThat(betrieb.getAdresse()).isEqualTo(swissAddress);
        }
    }
} 