package com.bau.shared.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for AWS Cognito user management operations.
 * Handles operations like password changes, user updates, and other Cognito-specific functionality.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CognitoUserService {
    
    @Value("${aws.cognito.region:eu-central-1}")
    private String region;
    
    @Value("${aws.cognito.access-key-id:}")
    private String accessKeyId;
    
    @Value("${aws.cognito.secret-access-key:}")
    private String secretAccessKey;
    
    @Value("${aws.cognito.user-pool-id}")
    private String userPoolId;
    
    @Value("${aws.cognito.client-id}")
    private String clientId;
    
    @Value("${aws.cognito.client-secret:}")
    private String clientSecret;
    
    private CognitoIdentityProviderClient createCognitoClient() {
        if (accessKeyId.isEmpty() || secretAccessKey.isEmpty()) {
            // Use default credentials chain (IAM role, environment variables, etc.)
            return CognitoIdentityProviderClient.builder()
                .region(Region.of(region))
                .build();
        } else {
            // Use explicit credentials
            return CognitoIdentityProviderClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
        }
    }
    
    /**
     * Authenticate user with AWS Cognito using username/password.
     * 
     * @param username the user's username or email
     * @param password the user's password
     * @return AuthenticationResult containing tokens, null if authentication failed
     */
    public AuthenticationResultType authenticateUser(String username, String password) {
        log.info("Attempting to authenticate user: {}", username);
        
        try (CognitoIdentityProviderClient cognitoClient = createCognitoClient()) {
            
            Map<String, String> authParams = new HashMap<>();
            authParams.put("USERNAME", username);
            authParams.put("PASSWORD", password);
            
            if (!clientSecret.isEmpty()) {
                authParams.put("SECRET_HASH", calculateSecretHash(username));
            }
            
            InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .clientId(clientId)
                .authParameters(authParams)
                .build();
            
            InitiateAuthResponse response = cognitoClient.initiateAuth(authRequest);
            
            if (response.authenticationResult() != null) {
                log.info("User authenticated successfully: {}", username);
                return response.authenticationResult();
            } else {
                log.warn("Authentication failed for user: {}", username);
                return null;
            }
            
        } catch (NotAuthorizedException e) {
            log.warn("Authentication failed for user: {} - {}", username, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Failed to authenticate user: {}", username, e);
            return null;
        }
    }
    
    /**
     * Change user password in AWS Cognito.
     * 
     * @param currentPassword the user's current password
     * @param newPassword the new password
     * @param accessToken the user's access token from Cognito
     * @return true if password change was successful, false otherwise
     */
    public boolean changePassword(String currentPassword, String newPassword, String accessToken) {
        log.info("Attempting to change password via AWS Cognito");
        
        try (CognitoIdentityProviderClient cognitoClient = createCognitoClient()) {
            
            ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .accessToken(accessToken)
                .previousPassword(currentPassword)
                .proposedPassword(newPassword)
                .build();
            
            cognitoClient.changePassword(changePasswordRequest);
            log.info("Password changed successfully for user");
            return true;
            
        } catch (Exception e) {
            log.error("Failed to change password via AWS Cognito", e);
            return false;
        }
    }
    
    /**
     * Update user attributes in AWS Cognito.
     * 
     * @param accessToken the user's access token
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email (if changing)
     * @return true if update was successful, false otherwise
     */
    public boolean updateUserAttributes(String accessToken, String firstName, String lastName, String email) {
        log.info("Attempting to update user attributes via AWS Cognito");
        
        try (CognitoIdentityProviderClient cognitoClient = createCognitoClient()) {
            
            List<AttributeType> attributes = new ArrayList<>();
            if (firstName != null) {
                attributes.add(AttributeType.builder()
                    .name("given_name")
                    .value(firstName)
                    .build());
            }
            if (lastName != null) {
                attributes.add(AttributeType.builder()
                    .name("family_name")
                    .value(lastName)
                    .build());
            }
            if (email != null) {
                attributes.add(AttributeType.builder()
                    .name("email")
                    .value(email)
                    .build());
            }
            
            UpdateUserAttributesRequest request = UpdateUserAttributesRequest.builder()
                .accessToken(accessToken)
                .userAttributes(attributes)
                .build();
            
            cognitoClient.updateUserAttributes(request);
            log.info("User attributes updated successfully");
            return true;
            
        } catch (Exception e) {
            log.error("Failed to update user attributes via AWS Cognito", e);
            return false;
        }
    }
    
    /**
     * Global sign out user from AWS Cognito.
     * This will invalidate all tokens for the user.
     * 
     * @param accessToken the user's access token
     * @return true if sign out was successful, false otherwise
     */
    public boolean globalSignOut(String accessToken) {
        log.info("Attempting global sign out via AWS Cognito");
        
        try (CognitoIdentityProviderClient cognitoClient = createCognitoClient()) {
            
            GlobalSignOutRequest signOutRequest = GlobalSignOutRequest.builder()
                .accessToken(accessToken)
                .build();
            
            cognitoClient.globalSignOut(signOutRequest);
            log.info("User signed out globally from AWS Cognito");
            return true;
            
        } catch (Exception e) {
            log.error("Failed to sign out user via AWS Cognito", e);
            return false;
        }
    }
    
    /**
     * Calculate secret hash for Cognito client authentication.
     * Only needed if client secret is configured.
     */
    private String calculateSecretHash(String username) {
        if (clientSecret.isEmpty()) {
            return null;
        }
        
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(
                clientSecret.getBytes(), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal((username + clientId).getBytes());
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.error("Failed to calculate secret hash", e);
            return null;
        }
    }
} 