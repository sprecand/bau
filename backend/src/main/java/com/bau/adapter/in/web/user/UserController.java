package com.bau.adapter.in.web.user;

import com.bau.adapter.in.web.api.UsersApi;
import com.bau.adapter.in.web.dto.*;
import com.bau.adapter.in.web.user.mapper.UserWebMapper;
import com.bau.application.domain.user.User;
import com.bau.application.port.in.UserUseCase;
import com.bau.shared.service.AuthenticationContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserController implements UsersApi {
    private final UserUseCase userUseCase;
    private final UserWebMapper mapper;
    private final AuthenticationContextService authContextService;

    @Override
    public ResponseEntity<UserResponse> getUserById(UUID id) {
        return userUseCase.getUserById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<UserListResponse> getUsers(Integer page, Integer size, UUID betriebId, String role) {
        List<UserResponse> users = userUseCase.getAllUsers().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        UserListResponse response = new UserListResponse().content((List<Object>) (List<?>) users);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = mapper.toDomain(request);
        User created = userUseCase.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(UUID id, @Valid @RequestBody UpdateUserRequest request) {
        User user = mapper.toDomain(request);
        User updated = userUseCase.updateUser(id, user);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @Override
    public ResponseEntity<Void> deleteUser(UUID id) {
        userUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserResponse> getCurrentUserProfile() {
        log.info("Getting current user profile");
        
        Optional<User> currentUser = authContextService.getCurrentUser();
        if (currentUser.isEmpty()) {
            log.warn("No authenticated user found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        User user = currentUser.get();
        log.debug("Retrieved profile for user: {}", user.getEmail());
        
        return ResponseEntity.ok(mapper.toResponse(user));
    }

    @Override
    public ResponseEntity<UserResponse> updateCurrentUserProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        log.info("Updating current user profile");
        
        Optional<UUID> currentUserId = authContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.warn("No authenticated user found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Map the update request to a User domain object
        User userUpdate = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build();
        
        log.debug("Updating profile for user: {}", currentUserId.get());
        
        try {
            User updatedUser = userUseCase.updateUser(currentUserId.get(), userUpdate);
            return ResponseEntity.ok(mapper.toResponse(updatedUser));
        } catch (Exception e) {
            log.error("Failed to update user profile for user: {}", currentUserId.get(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        log.info("Change password requested");
        
        Optional<User> currentUser = authContextService.getCurrentUser();
        if (currentUser.isEmpty()) {
            log.warn("No authenticated user found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        User user = currentUser.get();
        log.debug("Processing password change for user: {}", user.getEmail());
        
        // TODO: Implement AWS Cognito password change
        // This would typically involve:
        // 1. Validating the current password
        // 2. Calling AWS Cognito ChangePassword API
        // 3. Handling Cognito responses and errors
        
        // For now, return success but log that it's not implemented
        log.warn("Password change not yet implemented with AWS Cognito for user: {}", user.getEmail());
        
        // Return 501 Not Implemented to indicate this feature needs AWS Cognito integration
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
} 