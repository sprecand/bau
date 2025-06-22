package com.bau.adapter.in.web.user;

import com.bau.adapter.in.web.api.UsersApi;
import com.bau.adapter.in.web.dto.*;
import com.bau.adapter.in.web.user.mapper.UserWebMapper;
import com.bau.application.domain.user.User;
import com.bau.application.port.in.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserController implements UsersApi {
    private final UserUseCase userUseCase;
    private final UserWebMapper mapper;

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
        // TODO: Implement get current user profile
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<UserResponse> updateCurrentUserProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        // TODO: Implement update current user profile
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        // TODO: Implement change password
        return ResponseEntity.ok().build();
    }
} 