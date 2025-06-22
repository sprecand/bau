package com.bau.adapter.in.web.user.mapper;

import com.bau.adapter.in.web.dto.*;
import com.bau.application.domain.user.User;
import com.bau.application.domain.user.UserRole;
import com.bau.application.domain.user.UserStatus;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class UserWebMapper {
    public UserResponse toResponse(User user) {
        if (user == null) return null;
        return new UserResponse()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .betriebId(user.getBetriebId())
                .betriebName(user.getBetriebName())
                .role(UserResponse.RoleEnum.fromValue(user.getRole().name()))
                .status(UserResponse.StatusEnum.fromValue(user.getStatus().name()))
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().atOffset(ZoneOffset.UTC) : null)
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().atOffset(ZoneOffset.UTC) : null);
    }

    public User toDomain(CreateUserRequest request) {
        if (request == null) return null;
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .betriebId(request.getBetriebId())
                .role(UserRole.valueOf(request.getRole().name()))
                .status(UserStatus.AKTIV)
                .emailVerified(false)
                .build();
    }

    public User toDomain(UpdateUserRequest request) {
        if (request == null) return null;
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .betriebId(request.getBetriebId())
                .role(UserRole.valueOf(request.getRole().name()))
                .status(UserStatus.valueOf(request.getStatus().name()))
                .build();
    }
} 