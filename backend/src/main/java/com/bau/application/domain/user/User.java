package com.bau.application.domain.user;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain object representing a user (for AWS Cognito integration).
 * Contains business logic and validation rules.
 */
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private UUID betriebId;
    private String betriebName;
    private UserRole role;
    private UserStatus status;
    private boolean emailVerified;
    private Instant createdAt;
    private Instant updatedAt;

    public User(String cognitoId, String email) {
        this();
        this.username = cognitoId;
        this.email = email;
        validate();
    }

    /**
     * Validates the user according to business rules.
     * @throws IllegalArgumentException if validation fails
     */
    public void validate() {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Basic email validation.
     * @param email email to validate
     * @return true if email format is valid
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Checks if the user is currently active.
     * @return true if status is AKTIV
     */
    public boolean isActive() {
        return UserStatus.AKTIV.equals(status);
    }

    /**
     * Activates the user.
     */
    public void activate() {
        this.status = UserStatus.AKTIV;
    }

    /**
     * Deactivates the user.
     */
    public void deactivate() {
        this.status = UserStatus.INACTIV;
    }

    /**
     * Gets the full name of the user.
     * @return full name or email if name is not available
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
} 