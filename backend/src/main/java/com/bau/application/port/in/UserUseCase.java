package com.bau.application.port.in;

import com.bau.application.domain.user.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserUseCase {
    Optional<User> getUserById(UUID id);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    User createUser(User user);
    User updateUser(UUID id, User user);
    void deleteUser(UUID id);
} 