package com.bau.application.usecase;

import com.bau.application.domain.user.User;
import com.bau.application.domain.user.UserRole;
import com.bau.application.domain.user.UserStatus;
import com.bau.application.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID testId;
    private UUID testBetriebId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testBetriebId = UUID.randomUUID();
        testUser = User.builder()
                .id(testId)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.BETRIEB)
                .status(UserStatus.AKTIV)
                .betriebId(testBetriebId)
                .betriebName("Test Bau GmbH")
                .emailVerified(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUserSuccessfully() {
            // Given
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            User result = userService.createUser(testUser);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("testuser");
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            assertThat(result.getRole()).isEqualTo(UserRole.BETRIEB);
            assertThat(result.getStatus()).isEqualTo(UserStatus.AKTIV);
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should create admin user successfully")
        void shouldCreateAdminUserSuccessfully() {
            // Given
            User adminUser = User.builder()
                    .id(UUID.randomUUID())
                    .username("admin")
                    .email("admin@bau.ch")
                    .firstName("Admin")
                    .lastName("User")
                    .role(UserRole.ADMIN)
                    .status(UserStatus.AKTIV)
                    .emailVerified(true)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            when(userRepository.save(any(User.class))).thenReturn(adminUser);

            // When
            User result = userService.createUser(adminUser);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getRole()).isEqualTo(UserRole.ADMIN);
            assertThat(result.getBetriebId()).isNull();
            verify(userRepository).save(adminUser);
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should get user by id successfully")
        void shouldGetUserByIdSuccessfully() {
            // Given
            when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userService.getUserById(testId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(testId);
            assertThat(result.get().getUsername()).isEqualTo("testuser");
            verify(userRepository).findById(testId);
        }

        @Test
        @DisplayName("Should return empty when user not found")
        void shouldReturnEmptyWhenUserNotFound() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When
            Optional<User> result = userService.getUserById(nonExistentId);

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).findById(nonExistentId);
        }

        @Test
        @DisplayName("Should find user by email")
        void shouldFindUserByEmail() {
            // Given
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userService.getUserByEmail("test@example.com");

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("test@example.com");
            verify(userRepository).findByEmail("test@example.com");
        }

        @Test
        @DisplayName("Should find user by username")
        void shouldFindUserByUsername() {
            // Given
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userService.getUserByUsername("testuser");

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("testuser");
            verify(userRepository).findByUsername("testuser");
        }
    }

    @Nested
    @DisplayName("List Users Tests")
    class ListUsersTests {

        @Test
        @DisplayName("Should list all users")
        void shouldListAllUsers() {
            // Given
            List<User> users = List.of(testUser);
            when(userRepository.findAll()).thenReturn(users);

            // When
            List<User> result = userService.getAllUsers();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUsername()).isEqualTo("testuser");
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Should handle empty result")
        void shouldHandleEmptyResult() {
            // Given
            when(userRepository.findAll()).thenReturn(List.of());

            // When
            List<User> result = userService.getAllUsers();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
            verify(userRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            // Given
            User updatedUser = User.builder()
                    .id(testId)
                    .username("updateduser")
                    .email("updated@example.com")
                    .firstName("Updated")
                    .lastName("User")
                    .role(UserRole.BETRIEB)
                    .status(UserStatus.AKTIV)
                    .betriebId(testBetriebId)
                    .betriebName("Updated Bau GmbH")
                    .emailVerified(true)
                    .createdAt(testUser.getCreatedAt())
                    .updatedAt(Instant.now())
                    .build();

            when(userRepository.save(any(User.class))).thenReturn(updatedUser);

            // When
            User result = userService.updateUser(testId, updatedUser);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("updateduser");
            assertThat(result.getEmail()).isEqualTo("updated@example.com");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should set correct id when updating user")
        void shouldSetCorrectIdWhenUpdatingUser() {
            // Given
            User userToUpdate = User.builder()
                    .username("updateme")
                    .email("update@example.com")
                    .firstName("Update")
                    .lastName("Me")
                    .role(UserRole.BETRIEB)
                    .status(UserStatus.AKTIV)
                    .emailVerified(true)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            when(userRepository.save(any(User.class))).thenReturn(userToUpdate);

            // When
            User result = userService.updateUser(testId, userToUpdate);

            // Then
            verify(userRepository).save(argThat(user -> user.getId().equals(testId)));
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            // Given
            doNothing().when(userRepository).deleteById(testId);

            // When
            userService.deleteUser(testId);

            // Then
            verify(userRepository).deleteById(testId);
        }

        @Test
        @DisplayName("Should handle delete for non-existent user")
        void shouldHandleDeleteForNonExistentUser() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            doNothing().when(userRepository).deleteById(nonExistentId);

            // When
            userService.deleteUser(nonExistentId);

            // Then
            verify(userRepository).deleteById(nonExistentId);
        }
    }

    @Nested
    @DisplayName("User Domain Logic Tests")
    class UserDomainLogicTests {

        @Test
        @DisplayName("Should get full name correctly")
        void shouldGetFullNameCorrectly() {
            // When
            String fullName = testUser.getFullName();

            // Then
            assertThat(fullName).isEqualTo("Test User");
        }

        @Test
        @DisplayName("Should check if user is active")
        void shouldCheckIfUserIsActive() {
            // When
            boolean isActive = testUser.isActive();

            // Then
            assertThat(isActive).isTrue();
        }

        @Test
        @DisplayName("Should activate user")
        void shouldActivateUser() {
            // Given
            testUser.setStatus(UserStatus.INACTIV);

            // When
            testUser.activate();

            // Then
            assertThat(testUser.getStatus()).isEqualTo(UserStatus.AKTIV);
            assertThat(testUser.isActive()).isTrue();
        }

        @Test
        @DisplayName("Should deactivate user")
        void shouldDeactivateUser() {
            // When
            testUser.deactivate();

            // Then
            assertThat(testUser.getStatus()).isEqualTo(UserStatus.INACTIV);
            assertThat(testUser.isActive()).isFalse();
        }

        @Test
        @DisplayName("Should validate user data")
        void shouldValidateUserData() {
            // Given
            User validUser = User.builder()
                    .username("validuser")
                    .email("valid@example.com")
                    .build();

            // When & Then
            assertThatCode(validUser::validate).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw exception for invalid user data")
        void shouldThrowExceptionForInvalidUserData() {
            // Given
            User invalidUser = User.builder()
                    .username("")
                    .email("invalid-email")
                    .build();

            // When & Then
            assertThatThrownBy(invalidUser::validate)
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
} 