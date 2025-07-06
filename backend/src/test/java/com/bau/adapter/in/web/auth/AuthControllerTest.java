package com.bau.adapter.in.web.auth;

import com.bau.adapter.in.web.auth.mapper.AuthWebMapper;
import com.bau.adapter.in.web.dto.LoginRequest;
import com.bau.adapter.in.web.dto.UserProfileResponse;
import com.bau.application.domain.user.User;
import com.bau.application.domain.user.UserRole;
import com.bau.application.domain.user.UserStatus;
import com.bau.shared.service.AuthenticationContextService;
import com.bau.shared.service.CognitoUserService;
import com.bau.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthWebMapper authWebMapper;

    @MockitoBean
    private AuthenticationContextService authenticationContextService;

    @MockitoBean
    private CognitoUserService cognitoUserService;

    private User testUser;
    private UserProfileResponse testUserProfile;
    private LoginRequest testLoginRequest;
    private AuthenticationResultType testAuthResult;

    @BeforeEach
    void setUp() {
        // Clear SecurityContext to ensure test isolation
        SecurityContextHolder.clearContext();
        
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .betriebId(UUID.randomUUID())
                .betriebName("Test Betrieb")
                .role(UserRole.BETRIEB)
                .status(UserStatus.AKTIV)
                .emailVerified(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        testUserProfile = new UserProfileResponse()
                .id(testUser.getId().toString())
                .username(testUser.getUsername())
                .email(testUser.getEmail())
                .betriebId(testUser.getBetriebId())
                .betriebName(testUser.getBetriebName())
                .role(UserProfileResponse.RoleEnum.BETRIEB)
                .emailVerified(true);

        testLoginRequest = new LoginRequest()
                .username("testuser")
                .password("password123");

        testAuthResult = AuthenticationResultType.builder()
                .accessToken("mock-access-token")
                .idToken("mock-id-token")
                .refreshToken("mock-refresh-token")
                .expiresIn(3600)
                .build();
    }

    @AfterEach
    void tearDown() {
        // Clear SecurityContext after each test to prevent state leakage
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully with valid credentials")
        void shouldLoginSuccessfully() throws Exception {
            // Given
            when(cognitoUserService.authenticateUser(anyString(), anyString()))
                    .thenReturn(testAuthResult);
            when(authenticationContextService.extractUserFromJwtString(anyString()))
                    .thenReturn(testUser);
            when(authWebMapper.toProfileResponse(any(User.class)))
                    .thenReturn(testUserProfile);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testLoginRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
                    .andExpect(jsonPath("$.idToken").value("mock-id-token"))
                    .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"))
                    .andExpect(jsonPath("$.expiresIn").value(3600))
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.user.email").value("test@example.com"));

            verify(cognitoUserService).authenticateUser("testuser", "password123");
            verify(authenticationContextService).extractUserFromJwtString("mock-id-token");
            verify(authWebMapper).toProfileResponse(testUser);
        }

        @Test
        @DisplayName("Should return 401 when authentication fails")
        void shouldReturn401WhenAuthenticationFails() throws Exception {
            // Given
            when(cognitoUserService.authenticateUser(anyString(), anyString()))
                    .thenReturn(null);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testLoginRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(cognitoUserService).authenticateUser("testuser", "password123");
            verify(authenticationContextService, never()).extractUserFromJwtString(anyString());
            verify(authWebMapper, never()).toProfileResponse(any(User.class));
        }

        @Test
        @DisplayName("Should return 401 when exception occurs during authentication")
        void shouldReturn401WhenExceptionOccurs() throws Exception {
            // Given
            when(cognitoUserService.authenticateUser(anyString(), anyString()))
                    .thenThrow(new RuntimeException("Authentication failed"));

            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testLoginRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(cognitoUserService).authenticateUser("testuser", "password123");
        }

        @Test
        @DisplayName("Should return 401 when username is empty")
        void shouldReturn401WhenUsernameIsEmpty() throws Exception {
            // Given
            LoginRequest invalidRequest = new LoginRequest()
                    .username("") // Empty username
                    .password("password123");
            
            when(cognitoUserService.authenticateUser(anyString(), anyString()))
                    .thenReturn(null);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(cognitoUserService).authenticateUser("", "password123");
        }
    }

    @Nested
    @DisplayName("Get User Profile Tests")
    class GetUserProfileTests {

        @Test
        @DisplayName("Should return user profile when authenticated")
        void shouldReturnUserProfileWhenAuthenticated() throws Exception {
            // Given
            when(authenticationContextService.getCurrentUser())
                    .thenReturn(Optional.of(testUser));
            when(authWebMapper.toProfileResponse(any(User.class)))
                    .thenReturn(testUserProfile);

            // When & Then
            mockMvc.perform(get("/api/v1/auth/profile")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.betriebName").value("Test Betrieb"))
                    .andExpect(jsonPath("$.role").value("BETRIEB"));

            verify(authenticationContextService).getCurrentUser();
            verify(authWebMapper).toProfileResponse(testUser);
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            // Given
            when(authenticationContextService.getCurrentUser())
                    .thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/v1/auth/profile")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(authenticationContextService).getCurrentUser();
            verify(authWebMapper, never()).toProfileResponse(any(User.class));
        }
    }

    @Nested
    @DisplayName("Logout Tests")
    class LogoutTests {

        @Test
        @DisplayName("Should logout successfully")
        void shouldLogoutSuccessfully() throws Exception {
            // Given
            Authentication mockAuth = mock(Authentication.class);
            SecurityContext mockContext = mock(SecurityContext.class);
            
            when(mockContext.getAuthentication()).thenReturn(mockAuth);
            when(mockAuth.getCredentials()).thenReturn("mock-access-token");
            when(cognitoUserService.globalSignOut(anyString())).thenReturn(true);
            
            SecurityContextHolder.setContext(mockContext);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(cognitoUserService).globalSignOut("mock-access-token");
        }

        @Test
        @DisplayName("Should logout successfully even when Cognito sign out fails")
        void shouldLogoutSuccessfullyEvenWhenCognitoFails() throws Exception {
            // Given
            Authentication mockAuth = mock(Authentication.class);
            SecurityContext mockContext = mock(SecurityContext.class);
            
            when(mockContext.getAuthentication()).thenReturn(mockAuth);
            when(mockAuth.getCredentials()).thenReturn("mock-access-token");
            when(cognitoUserService.globalSignOut(anyString())).thenReturn(false);
            
            SecurityContextHolder.setContext(mockContext);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(cognitoUserService).globalSignOut("mock-access-token");
        }

        @Test
        @DisplayName("Should return 500 when exception occurs during logout")
        void shouldReturn500WhenExceptionOccurs() throws Exception {
            // Given
            Authentication mockAuth = mock(Authentication.class);
            SecurityContext mockContext = mock(SecurityContext.class);
            
            when(mockContext.getAuthentication()).thenReturn(mockAuth);
            when(mockAuth.getCredentials()).thenReturn("mock-access-token");
            when(cognitoUserService.globalSignOut(anyString()))
                    .thenThrow(new RuntimeException("Cognito error"));
            
            SecurityContextHolder.setContext(mockContext);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());

            verify(cognitoUserService).globalSignOut("mock-access-token");
        }

        @Test
        @DisplayName("Should logout successfully when no authentication present")
        void shouldLogoutSuccessfullyWhenNoAuth() throws Exception {
            // Given
            SecurityContext mockContext = mock(SecurityContext.class);
            when(mockContext.getAuthentication()).thenReturn(null);
            SecurityContextHolder.setContext(mockContext);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(cognitoUserService, never()).globalSignOut(anyString());
        }
    }
} 