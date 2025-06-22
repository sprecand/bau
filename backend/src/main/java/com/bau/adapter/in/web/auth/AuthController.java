package com.bau.adapter.in.web.auth;

import com.bau.adapter.in.web.api.AuthenticationApi;
import com.bau.adapter.in.web.dto.UserProfileResponse;
import com.bau.adapter.in.web.auth.mapper.AuthWebMapper;
import com.bau.application.domain.user.User;
import com.bau.application.domain.user.UserRole;
import com.bau.application.domain.user.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController implements AuthenticationApi {
    private final AuthWebMapper mapper;

    @Override
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        // TODO: Replace with real authentication context
        User mockUser = User.builder()
                .id(UUID.randomUUID())
                .username("john.doe@bau-ag-grabs.ch")
                .email("john.doe@bau-ag-grabs.ch")
                .firstName("John")
                .lastName("Doe")
                .betriebId(UUID.randomUUID())
                .betriebName("Bau AG Grabs")
                .role(UserRole.BETRIEB)
                .status(UserStatus.AKTIV)
                .emailVerified(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return ResponseEntity.ok(mapper.toProfileResponse(mockUser));
    }

    @Override
    public ResponseEntity<Void> logoutUser() {
        // TODO: Integrate with AWS Cognito logout
        return ResponseEntity.ok().build();
    }
} 