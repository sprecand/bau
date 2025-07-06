package com.bau.shared.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bau.application.domain.user.User;
import com.bau.application.domain.user.UserRole;
import com.bau.application.domain.user.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for extracting authentication context from JWT tokens.
 * Handles AWS Cognito JWT token claims and converts them to application domain objects.
 */
@Service
@Slf4j
public class AuthenticationContextService {
    
    /**
     * Get the current authenticated user from the security context.
     * 
     * @return Optional containing the current user, or empty if not authenticated
     */
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No authenticated user found");
            return Optional.empty();
        }
        
        // Check if we have a JWT token
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return extractUserFromJwt(jwt);
        }
        
        log.debug("Authentication principal is not a JWT token: {}", authentication.getPrincipal().getClass());
        return Optional.empty();
    }
    
    /**
     * Extract user information from JWT token string.
     * 
     * @param tokenString the JWT token string
     * @return the user information extracted from the token
     */
    public User extractUserFromJwtString(String tokenString) {
        try {
            // Decode the JWT token
            DecodedJWT decodedJWT = JWT.decode(tokenString);
            
            // Extract claims from AWS Cognito JWT
            String sub = decodedJWT.getSubject();
            String email = decodedJWT.getClaim("email").asString();
            String username = decodedJWT.getClaim("cognito:username").asString();
            String givenName = decodedJWT.getClaim("given_name").asString();
            String familyName = decodedJWT.getClaim("family_name").asString();
            Boolean emailVerified = decodedJWT.getClaim("email_verified").asBoolean();
            
            // Extract groups (roles) from Cognito
            List<String> groups = decodedJWT.getClaim("cognito:groups").asList(String.class);
            UserRole role = extractRoleFromGroups(groups);
            
            // Extract custom attributes
            String betriebIdClaim = decodedJWT.getClaim("custom:betrieb_id").asString();
            String betriebNameClaim = decodedJWT.getClaim("custom:betrieb_name").asString();
            
            // Build user object
            User.UserBuilder userBuilder = User.builder()
                    .id(UUID.fromString(sub))
                    .username(username != null ? username : email)
                    .email(email)
                    .firstName(givenName)
                    .lastName(familyName)
                    .role(role)
                    .status(UserStatus.AKTIV)
                    .emailVerified(emailVerified != null ? emailVerified : false)
                    .createdAt(Instant.now()) // JWT doesn't contain creation time
                    .updatedAt(Instant.now());
            
            // Set betrieb information if available
            if (betriebIdClaim != null) {
                try {
                    userBuilder.betriebId(UUID.fromString(betriebIdClaim));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid betrieb_id format in JWT: {}", betriebIdClaim);
                }
            }
            
            if (betriebNameClaim != null) {
                userBuilder.betriebName(betriebNameClaim);
            }
            
            User user = userBuilder.build();
            log.debug("Extracted user from JWT string: id={}, email={}, role={}", user.getId(), user.getEmail(), user.getRole());
            
            return user;
            
        } catch (Exception e) {
            log.error("Failed to extract user from JWT token string", e);
            throw new RuntimeException("Failed to decode JWT token", e);
        }
    }
    
    /**
     * Get the current user's ID.
     * 
     * @return Optional containing the current user's ID, or empty if not authenticated
     */
    public Optional<UUID> getCurrentUserId() {
        return getCurrentUser().map(User::getId);
    }
    
    /**
     * Get the current user's betrieb ID.
     * 
     * @return Optional containing the current user's betrieb ID, or empty if not authenticated or user has no betrieb
     */
    public Optional<UUID> getCurrentUserBetriebId() {
        return getCurrentUser().map(User::getBetriebId);
    }
    
    /**
     * Check if the current user has the specified role.
     * 
     * @param role the role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(UserRole role) {
        return getCurrentUser()
                .map(User::getRole)
                .map(userRole -> userRole == role)
                .orElse(false);
    }
    
    /**
     * Extract user information from JWT token.
     * 
     * @param jwt the JWT token
     * @return Optional containing the user, or empty if extraction fails
     */
    private Optional<User> extractUserFromJwt(Jwt jwt) {
        try {
            // Extract claims from AWS Cognito JWT
            String sub = jwt.getClaimAsString("sub");
            String email = jwt.getClaimAsString("email");
            String username = jwt.getClaimAsString("cognito:username");
            String givenName = jwt.getClaimAsString("given_name");
            String familyName = jwt.getClaimAsString("family_name");
            Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");
            
            // Extract groups (roles) from Cognito
            List<String> groups = jwt.getClaimAsStringList("cognito:groups");
            UserRole role = extractRoleFromGroups(groups);
            
            // Extract custom attributes
            String betriebIdClaim = jwt.getClaimAsString("custom:betrieb_id");
            String betriebNameClaim = jwt.getClaimAsString("custom:betrieb_name");
            
            // Build user object
            User.UserBuilder userBuilder = User.builder()
                    .id(UUID.fromString(sub))
                    .username(username != null ? username : email)
                    .email(email)
                    .firstName(givenName)
                    .lastName(familyName)
                    .role(role)
                    .status(UserStatus.AKTIV)
                    .emailVerified(emailVerified != null ? emailVerified : false)
                    .createdAt(Instant.now()) // JWT doesn't contain creation time
                    .updatedAt(Instant.now());
            
            // Set betrieb information if available
            if (betriebIdClaim != null) {
                try {
                    userBuilder.betriebId(UUID.fromString(betriebIdClaim));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid betrieb_id format in JWT: {}", betriebIdClaim);
                }
            }
            
            if (betriebNameClaim != null) {
                userBuilder.betriebName(betriebNameClaim);
            }
            
            User user = userBuilder.build();
            log.debug("Extracted user from JWT: id={}, email={}, role={}", user.getId(), user.getEmail(), user.getRole());
            
            return Optional.of(user);
            
        } catch (Exception e) {
            log.error("Failed to extract user from JWT token", e);
            return Optional.empty();
        }
    }
    
    /**
     * Extract user role from Cognito groups.
     * 
     * @param groups the list of Cognito groups
     * @return the user role
     */
    private UserRole extractRoleFromGroups(List<String> groups) {
        if (groups == null || groups.isEmpty()) {
            log.debug("No groups found in JWT, defaulting to BETRIEB role");
            return UserRole.BETRIEB;
        }
        
        // Check for admin role first
        if (groups.contains("ADMIN")) {
            return UserRole.ADMIN;
        }
        
        // Default to BETRIEB role
        return UserRole.BETRIEB;
    }
    
    /**
     * Create a mock user for local development.
     * This method should only be used when running in local profile.
     * 
     * @return a mock user for testing
     */
    public User createMockUser() {
        return User.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .username("john.doe@bau-ag-grabs.ch")
                .email("john.doe@bau-ag-grabs.ch")
                .firstName("John")
                .lastName("Doe")
                .betriebId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"))
                .betriebName("Bau AG Grabs")
                .role(UserRole.BETRIEB)
                .status(UserStatus.AKTIV)
                .emailVerified(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
} 