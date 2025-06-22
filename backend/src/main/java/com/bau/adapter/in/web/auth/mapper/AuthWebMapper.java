package com.bau.adapter.in.web.auth.mapper;

import com.bau.adapter.in.web.dto.UserProfileResponse;
import com.bau.application.domain.user.User;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class AuthWebMapper {
    public UserProfileResponse toProfileResponse(User user) {
        if (user == null) return null;
        return new UserProfileResponse()
                .id(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .betriebId(user.getBetriebId())
                .betriebName(user.getBetriebName())
                .role(UserProfileResponse.RoleEnum.fromValue(user.getRole().name()))
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().atOffset(ZoneOffset.UTC) : null)
                .lastLoginAt(user.getUpdatedAt() != null ? user.getUpdatedAt().atOffset(ZoneOffset.UTC) : null);
    }
} 