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
     * Data structure to hold JWT claims extracted from different token types.
     */
    private record JwtClaims(
            String sub,
            String email,
            String username,
            String givenName,
            String familyName,
            Boolean emailVerified,
            List<String> groups,
            String betriebIdClaim,
            String betriebNameClaim
    ) {}
    
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
            JwtClaims claims = new JwtClaims(
                    decodedJWT.getSubject(),
                    decodedJWT.getClaim("email").asString(),
                    decodedJWT.getClaim("cognito:username").asString(),
                    decodedJWT.getClaim("given_name").asString(),
                    decodedJWT.getClaim("family_name").asString(),
                    decodedJWT.getClaim("email_verified").asBoolean(),
                    decodedJWT.getClaim("cognito:groups").asList(String.class),
                    decodedJWT.getClaim("custom:betrieb_id").asString(),
                    decodedJWT.getClaim("custom:betrieb_name").asString()
            );
            
            User user = buildUserFromClaims(claims);
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
            JwtClaims claims = new JwtClaims(
                    jwt.getClaimAsString("sub"),
                    jwt.getClaimAsString("email"),
                    jwt.getClaimAsString("cognito:username"),
                    jwt.getClaimAsString("given_name"),
                    jwt.getClaimAsString("family_name"),
                    jwt.getClaimAsBoolean("email_verified"),
                    jwt.getClaimAsStringList("cognito:groups"),
                    jwt.getClaimAsString("custom:betrieb_id"),
                    jwt.getClaimAsString("custom:betrieb_name")
            );
            
            User user = buildUserFromClaims(claims);
            log.debug("Extracted user from JWT: id={}, email={}, role={}", user.getId(), user.getEmail(), user.getRole());
            
            return Optional.of(user);
            
        } catch (Exception e) {
            log.error("Failed to extract user from JWT token", e);
            return Optional.empty();
        }
    }

    /**
     * Build a User object from extracted JWT claims.
     * This method contains the common logic for creating User objects from JWT claims.
     * 
     * @param claims the extracted JWT claims
     * @return the User object built from the claims
     */
    private User buildUserFromClaims(JwtClaims claims) {
        // Extract role from groups
        UserRole role = extractRoleFromGroups(claims.groups());
        
        // Build user object
        User.UserBuilder userBuilder = User.builder()
                .id(UUID.fromString(claims.sub()))
                .username(claims.username() != null ? claims.username() : claims.email())
                .email(claims.email())
                .firstName(claims.givenName())
                .lastName(claims.familyName())
                .role(role)
                .status(UserStatus.AKTIV)
                .emailVerified(claims.emailVerified() != null ? claims.emailVerified() : false)
                .createdAt(Instant.now()) // JWT doesn't contain creation time
                .updatedAt(Instant.now());
        
        // Set betrieb information if available
        if (claims.betriebIdClaim() != null) {
            try {
                userBuilder.betriebId(UUID.fromString(claims.betriebIdClaim()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid betrieb_id format in JWT: {}", claims.betriebIdClaim());
            }
        }
        
        if (claims.betriebNameClaim() != null) {
            userBuilder.betriebName(claims.betriebNameClaim());
        }
        
        return userBuilder.build();
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
        
        // Check for specific roles in groups
        for (String group : groups) {
            switch (group.toLowerCase()) {
                case "admin", "administrators":
                    return UserRole.ADMIN;
                case "betrieb", "company", "arbeiter", "worker":
                    return UserRole.BETRIEB;
                default:
                    log.debug("Unknown group in JWT: {}", group);
            }
        }
        
        // Default to BETRIEB if no recognized role found
        log.debug("No recognized role found in groups: {}, defaulting to BETRIEB", groups);
        return UserRole.BETRIEB;
    }
    
    /**
     * Create a mock user for testing.
     * 
     * @return a mock user
     */
    public User createMockUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.BETRIEB)
                .status(UserStatus.AKTIV)
                .emailVerified(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
} 