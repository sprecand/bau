package com.bau.adapter.in.web.auth;

import com.bau.adapter.in.web.api.AuthenticationApi;
import com.bau.adapter.in.web.dto.LoginRequest;
import com.bau.adapter.in.web.dto.LoginResponse;
import com.bau.adapter.in.web.dto.UserProfileResponse;
import com.bau.adapter.in.web.auth.mapper.AuthWebMapper;
import com.bau.application.domain.user.User;
import com.bau.shared.service.AuthenticationContextService;
import com.bau.shared.service.CognitoUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class AuthController implements AuthenticationApi {
    private final AuthWebMapper mapper;
    private final AuthenticationContextService authContextService;
    private final CognitoUserService cognitoUserService;

    @Override
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("User login attempt for username: {}", loginRequest.getUsername());
        
        try {
            // Authenticate with AWS Cognito
            AuthenticationResultType authResult = cognitoUserService.authenticateUser(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            
            if (authResult == null) {
                log.warn("Authentication failed for user: {}", loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Extract user info from tokens
            User user = authContextService.extractUserFromJwtString(authResult.idToken());
            
            LoginResponse response = new LoginResponse()
                .accessToken(authResult.accessToken())
                .refreshToken(authResult.refreshToken())
                .idToken(authResult.idToken())
                .expiresIn(authResult.expiresIn())
                .tokenType("Bearer")
                .user(mapper.toProfileResponse(user));
            
            log.info("User logged in successfully: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        log.info("Getting user profile");
        
        Optional<User> currentUser = authContextService.getCurrentUser();
        
        if (currentUser.isEmpty()) {
            log.warn("No authenticated user found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        User user = currentUser.get();
        log.debug("Retrieved user profile for: {}", user.getEmail());
        
        return ResponseEntity.ok(mapper.toProfileResponse(user));
    }

    @Override
    public ResponseEntity<Void> logoutUser() {
        log.info("User logout requested");
        
        try {
            // Get current authentication to extract access token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getCredentials() instanceof String) {
                String accessToken = (String) authentication.getCredentials();
                
                // Perform global sign out via AWS Cognito
                boolean signOutSuccess = cognitoUserService.globalSignOut(accessToken);
                
                if (signOutSuccess) {
                    log.info("User signed out successfully from AWS Cognito");
                } else {
                    log.warn("Failed to sign out user from AWS Cognito");
                }
            }
            
            // Clear security context
            SecurityContextHolder.clearContext();
            
            log.debug("Logout completed");
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("Error during logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 