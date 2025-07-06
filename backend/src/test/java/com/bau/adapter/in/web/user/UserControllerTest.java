package com.bau.adapter.in.web.user;

import com.bau.adapter.in.web.dto.*;
import com.bau.adapter.in.web.user.mapper.UserWebMapper;
import com.bau.application.domain.user.User;
import com.bau.application.domain.user.UserRole;
import com.bau.application.domain.user.UserStatus;
import com.bau.application.port.in.UserUseCase;
import com.bau.config.TestSecurityConfig;
import com.bau.shared.service.AuthenticationContextService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@DisplayName("UserController Tests")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserUseCase userUseCase;

    @MockBean
    private UserWebMapper mapper;

    @MockBean
    private AuthenticationContextService authContextService;

    private User testUser;
    private UserResponse testUserResponse;
    private UUID testUserId;
    private UUID testBetriebId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testBetriebId = UUID.randomUUID();
        
        testUser = User.builder()
                .id(testUserId)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .betriebId(testBetriebId)
                .betriebName("Test Betrieb")
                .role(UserRole.BETRIEB)
                .status(UserStatus.AKTIV)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();

        testUserResponse = new UserResponse()
                .id(testUserId)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .betriebId(testBetriebId)
                .betriebName("Test Betrieb")
                .role(UserResponse.RoleEnum.BETRIEB)
                .emailVerified(true);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Get User By ID Tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return user when found")
        void shouldReturnUserWhenFound() throws Exception {
            // Given
            when(userUseCase.getUserById(testUserId)).thenReturn(Optional.of(testUser));
            when(mapper.toResponse(testUser)).thenReturn(testUserResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/users/{id}", testUserId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testUserId.toString()))
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"));

            verify(userUseCase).getUserById(testUserId);
            verify(mapper).toResponse(testUser);
        }

        @Test
        @DisplayName("Should return 404 when user not found")
        void shouldReturn404WhenUserNotFound() throws Exception {
            // Given
            when(userUseCase.getUserById(testUserId)).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/v1/users/{id}", testUserId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(userUseCase).getUserById(testUserId);
            verify(mapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("Get Users Tests")
    class GetUsersTests {

        @Test
        @DisplayName("Should return list of users")
        void shouldReturnListOfUsers() throws Exception {
            // Given
            List<User> users = Arrays.asList(testUser);
            
            when(userUseCase.getAllUsers()).thenReturn(users);
            when(mapper.toResponse(testUser)).thenReturn(testUserResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                            .param("page", "1")
                            .param("size", "20")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(testUserId.toString()))
                    .andExpect(jsonPath("$.content[0].username").value("testuser"));

            verify(userUseCase).getAllUsers();
            verify(mapper).toResponse(testUser);
        }

        @Test
        @DisplayName("Should return empty list when no users")
        void shouldReturnEmptyListWhenNoUsers() throws Exception {
            // Given
            when(userUseCase.getAllUsers()).thenReturn(Arrays.asList());

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isEmpty());

            verify(userUseCase).getAllUsers();
        }
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUserSuccessfully() throws Exception {
            // Given
            CreateUserRequest request = new CreateUserRequest()
                    .username("newuser")
                    .email("newuser@example.com")
                    .firstName("Jane")
                    .lastName("Smith")
                    .betriebId(testBetriebId)
                    .role(CreateUserRequest.RoleEnum.BETRIEB);

            when(mapper.toDomain(eq(request))).thenReturn(testUser);
            when(userUseCase.createUser(testUser)).thenReturn(testUser);
            when(mapper.toResponse(testUser)).thenReturn(testUserResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(testUserId.toString()))
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("test@example.com"));

            verify(mapper).toDomain(eq(request));
            verify(userUseCase).createUser(testUser);
            verify(mapper).toResponse(testUser);
        }

        @Test
        @DisplayName("Should return 400 when invalid request")
        void shouldReturn400WhenInvalidRequest() throws Exception {
            // Given
            CreateUserRequest request = new CreateUserRequest()
                    .username("") // Invalid empty username
                    .email("invalid-email") // Invalid email format
                    .firstName("Jane")
                    .lastName("Smith")
                    .betriebId(testBetriebId)
                    .role(CreateUserRequest.RoleEnum.BETRIEB);

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(mapper, never()).toDomain(any(CreateUserRequest.class));
            verify(userUseCase, never()).createUser(any());
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() throws Exception {
            // Given
            UpdateUserRequest request = new UpdateUserRequest()
                    .firstName("Updated")
                    .lastName("Name")
                    .email("updated@example.com")
                    .role(UpdateUserRequest.RoleEnum.BETRIEB)
                    .status(UpdateUserRequest.StatusEnum.AKTIV);

            User updatedUser = testUser.toBuilder()
                    .firstName("Updated")
                    .lastName("Name")
                    .email("updated@example.com")
                    .build();

            UserResponse updatedResponse = testUserResponse
                    .firstName("Updated")
                    .lastName("Name")
                    .email("updated@example.com");

            when(mapper.toDomain(eq(request))).thenReturn(updatedUser);
            when(userUseCase.updateUser(testUserId, updatedUser)).thenReturn(updatedUser);
            when(mapper.toResponse(updatedUser)).thenReturn(updatedResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/users/{id}", testUserId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Updated"))
                    .andExpect(jsonPath("$.lastName").value("Name"))
                    .andExpect(jsonPath("$.email").value("updated@example.com"));

            verify(mapper).toDomain(eq(request));
            verify(userUseCase).updateUser(testUserId, updatedUser);
            verify(mapper).toResponse(updatedUser);
        }

        @Test
        @DisplayName("Should return 400 when invalid request")
        void shouldReturn400WhenInvalidRequest() throws Exception {
            // Given
            UpdateUserRequest request = new UpdateUserRequest()
                    .firstName("") // Invalid empty firstName
                    .lastName("Name")
                    .email("invalid-email") // Invalid email format
                    .role(UpdateUserRequest.RoleEnum.BETRIEB)
                    .status(UpdateUserRequest.StatusEnum.AKTIV);

            // When & Then
            mockMvc.perform(put("/api/v1/users/{id}", testUserId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(mapper, never()).toDomain(any(UpdateUserRequest.class));
            verify(userUseCase, never()).updateUser(any(), any());
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() throws Exception {
            // Given
            doNothing().when(userUseCase).deleteUser(testUserId);

            // When & Then
            mockMvc.perform(delete("/api/v1/users/{id}", testUserId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(userUseCase).deleteUser(testUserId);
        }

        @Test
        @DisplayName("Should handle delete user exception")
        void shouldHandleDeleteUserException() throws Exception {
            // Given
            doThrow(new RuntimeException("User not found")).when(userUseCase).deleteUser(testUserId);

            // When & Then
            mockMvc.perform(delete("/api/v1/users/{id}", testUserId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(userUseCase).deleteUser(testUserId);
        }
    }

    @Nested
    @DisplayName("Get Current User Profile Tests")
    class GetCurrentUserProfileTests {

        @Test
        @DisplayName("Should return current user profile")
        void shouldReturnCurrentUserProfile() throws Exception {
            // Given
            when(authContextService.getCurrentUser()).thenReturn(Optional.of(testUser));
            when(mapper.toResponse(testUser)).thenReturn(testUserResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/users/profile")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testUserId.toString()))
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("test@example.com"));

            verify(authContextService).getCurrentUser();
            verify(mapper).toResponse(testUser);
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            // Given
            when(authContextService.getCurrentUser()).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/v1/users/profile")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(authContextService).getCurrentUser();
            verify(mapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("Update Current User Profile Tests")
    class UpdateCurrentUserProfileTests {

        @Test
        @DisplayName("Should update current user profile successfully")
        void shouldUpdateCurrentUserProfileSuccessfully() throws Exception {
            // Given
            UpdateUserProfileRequest request = new UpdateUserProfileRequest()
                    .firstName("Updated")
                    .lastName("Profile")
                    .email("updated.profile@example.com");

            User updatedUser = testUser.toBuilder()
                    .firstName("Updated")
                    .lastName("Profile")
                    .email("updated.profile@example.com")
                    .build();

            UserResponse updatedResponse = testUserResponse
                    .firstName("Updated")
                    .lastName("Profile")
                    .email("updated.profile@example.com");

            when(authContextService.getCurrentUserId()).thenReturn(Optional.of(testUserId));
            when(userUseCase.updateUser(eq(testUserId), any(User.class))).thenReturn(updatedUser);
            when(mapper.toResponse(updatedUser)).thenReturn(updatedResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/users/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Updated"))
                    .andExpect(jsonPath("$.lastName").value("Profile"))
                    .andExpect(jsonPath("$.email").value("updated.profile@example.com"));

            verify(authContextService).getCurrentUserId();
            verify(userUseCase).updateUser(eq(testUserId), any(User.class));
            verify(mapper).toResponse(updatedUser);
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            // Given
            UpdateUserProfileRequest request = new UpdateUserProfileRequest()
                    .firstName("Updated")
                    .lastName("Profile")
                    .email("updated.profile@example.com");

            when(authContextService.getCurrentUserId()).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(put("/api/v1/users/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());

            verify(authContextService).getCurrentUserId();
            verify(userUseCase, never()).updateUser(any(), any());
        }

        @Test
        @DisplayName("Should return 500 when update fails")
        void shouldReturn500WhenUpdateFails() throws Exception {
            // Given
            UpdateUserProfileRequest request = new UpdateUserProfileRequest()
                    .firstName("Updated")
                    .lastName("Profile")
                    .email("updated.profile@example.com");

            when(authContextService.getCurrentUserId()).thenReturn(Optional.of(testUserId));
            when(userUseCase.updateUser(eq(testUserId), any(User.class)))
                    .thenThrow(new RuntimeException("Update failed"));

            // When & Then
            mockMvc.perform(put("/api/v1/users/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());

            verify(authContextService).getCurrentUserId();
            verify(userUseCase).updateUser(eq(testUserId), any(User.class));
        }
    }

    @Nested
    @DisplayName("Change Password Tests")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should return 501 Not Implemented")
        void shouldReturn501NotImplemented() throws Exception {
            // Given
            ChangePasswordRequest request = new ChangePasswordRequest()
                    .currentPassword("oldpassword")
                    .newPassword("newpassword");

            when(authContextService.getCurrentUser()).thenReturn(Optional.of(testUser));

            // When & Then
            mockMvc.perform(post("/api/v1/users/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotImplemented());

            verify(authContextService).getCurrentUser();
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            // Given
            ChangePasswordRequest request = new ChangePasswordRequest()
                    .currentPassword("oldpassword")
                    .newPassword("newpassword");

            when(authContextService.getCurrentUser()).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(post("/api/v1/users/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());

            verify(authContextService).getCurrentUser();
        }
    }
} 