package com.bau.application.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Domain Entity Tests")
class UserTest {

    @Nested
    @DisplayName("Builder Pattern")
    class BuilderPatternTests {

        @Test
        @DisplayName("Should create User with all fields")
        void shouldCreateUserWithAllFields() {
            // Given
            UUID id = UUID.randomUUID();
            String username = "hans.mueller";
            String email = "hans.mueller@bauunternehmen.ch";

            // When
            User user = User.builder()
                    .id(id)
                    .username(username)
                    .email(email)
                    .firstName("Hans")
                    .lastName("Müller")
                    .role(UserRole.ADMIN)
                    .status(UserStatus.AKTIV)
                    .emailVerified(true)
                    .build();

            // Then
            assertThat(user.getId()).isEqualTo(id);
            assertThat(user.getUsername()).isEqualTo(username);
            assertThat(user.getEmail()).isEqualTo(email);
            assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
            assertThat(user.getStatus()).isEqualTo(UserStatus.AKTIV);
            assertThat(user.isEmailVerified()).isTrue();
        }

        @Test
        @DisplayName("Should create minimal User with required fields only")
        void shouldCreateMinimalUser() {
            // When
            User user = User.builder()
                    .username("testuser")
                    .email("test@example.com")
                    .firstName("Test")
                    .lastName("User")
                    .role(UserRole.BETRIEB)
                    .status(UserStatus.AKTIV)
                    .build();

            // Then
            assertThat(user.getUsername()).isEqualTo("testuser");
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getFirstName()).isEqualTo("Test");
            assertThat(user.getLastName()).isEqualTo("User");
            assertThat(user.getRole()).isEqualTo(UserRole.BETRIEB);
            assertThat(user.getStatus()).isEqualTo(UserStatus.AKTIV);
            assertThat(user.getBetriebId()).isNull();
            assertThat(user.getBetriebName()).isNull();
            assertThat(user.isEmailVerified()).isFalse();
        }
    }

    @Nested
    @DisplayName("Role Management")
    class RoleManagementTests {

        @Test
        @DisplayName("Should handle all user roles")
        void shouldHandleAllUserRoles() {
            // Test ADMIN role
            User adminUser = User.builder()
                    .username("admin")
                    .role(UserRole.ADMIN)
                    .build();
            assertThat(adminUser.getRole()).isEqualTo(UserRole.ADMIN);

            // Test BETRIEB role
            User regularUser = User.builder()
                    .username("user")
                    .role(UserRole.BETRIEB)
                    .build();
            assertThat(regularUser.getRole()).isEqualTo(UserRole.BETRIEB);
        }
    }

    @Nested
    @DisplayName("Status Management")
    class StatusManagementTests {

        @Test
        @DisplayName("Should handle all user statuses")
        void shouldHandleAllUserStatuses() {
            // Test AKTIV status
            User aktivUser = User.builder()
                    .username("aktiv")
                    .status(UserStatus.AKTIV)
                    .build();
            assertThat(aktivUser.getStatus()).isEqualTo(UserStatus.AKTIV);

            // Test INACTIV status
            User inactivUser = User.builder()
                    .username("inactiv")
                    .status(UserStatus.INACTIV)
                    .build();
            assertThat(inactivUser.getStatus()).isEqualTo(UserStatus.INACTIV);
        }
    }

    @Nested
    @DisplayName("Email Verification")
    class EmailVerificationTests {

        @Test
        @DisplayName("Should handle email verification status")
        void shouldHandleEmailVerificationStatus() {
            // Test verified email
            User verifiedUser = User.builder()
                    .username("verified")
                    .email("verified@example.com")
                    .emailVerified(true)
                    .build();
            assertThat(verifiedUser.isEmailVerified()).isTrue();

            // Test unverified email
            User unverifiedUser = User.builder()
                    .username("unverified")
                    .email("unverified@example.com")
                    .emailVerified(false)
                    .build();
            assertThat(unverifiedUser.isEmailVerified()).isFalse();
        }
    }

    @Nested
    @DisplayName("Swiss Context")
    class SwissContextTests {

        @Test
        @DisplayName("Should handle Swiss email domains")
        void shouldHandleSwissEmailDomains() {
            // Given
            User user = User.builder()
                    .username("swiss.user")
                    .email("user@bauunternehmen.ch")
                    .build();

            // Then
            assertThat(user.getEmail()).isEqualTo("user@bauunternehmen.ch");
            assertThat(user.getEmail()).endsWith(".ch");
        }

        @Test
        @DisplayName("Should handle Swiss names with umlauts")
        void shouldHandleSwissNamesWithUmlauts() {
            // Given
            User user = User.builder()
                    .firstName("Jürgen")
                    .lastName("Müller")
                    .build();

            // Then
            assertThat(user.getFirstName()).isEqualTo("Jürgen");
            assertThat(user.getLastName()).isEqualTo("Müller");
        }
    }
} 