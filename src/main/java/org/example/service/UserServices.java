package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.domain.User;
import org.example.repo.UserRepo;
import org.example.rest.dto.UserCreateRequestDto;
import org.example.rest.dto.UserResponseDto;
import org.example.rest.dto.UserUpdateRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

/**
 * Service layer for User operations.
 * Handles all business logic for creating, reading, updating, and deleting users.
 */
@Service
public class UserServices {

    private final UserRepo repo;

    public UserServices(UserRepo repo) {
        this.repo = repo;
    }

    private static UserResponseDto toDto(User user) {
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail());
    }

    // ── Create ────────────────────────────────────────────────────────────────

    public ResponseEntity<UserResponseDto> createUser(UserCreateRequestDto newUser) {
        // Basic uniqueness check on username (best enforced with a DB unique index as well)
        if (repo.findByUsername(newUser.getUsername()).isPresent()) {
            throw new ResponseStatusException(CONFLICT, "username already exists");
        }

        User created = new User();
        created.setUsername(newUser.getUsername());
        created.setEmail(newUser.getEmail());
        created.setPassword(newUser.getPassword());

        User saved = this.repo.save(created);
        return new ResponseEntity<>(toDto(saved), HttpStatus.CREATED);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    public List<UserResponseDto> getUsers() {
        return this.repo.findAll().stream().map(UserServices::toDto).toList();
    }

    public ResponseEntity<UserResponseDto> getUser(int id) {
        Optional<User> found = this.repo.findById(id);
        if (found.isEmpty()) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        return ResponseEntity.ok(toDto(found.get()));
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public ResponseEntity<UserResponseDto> updateUser(int id, UserUpdateRequestDto userDetails) {
        Optional<User> found = this.repo.findById(id);
        if (found.isEmpty()) {
            return new ResponseEntity<>(NOT_FOUND);
        }

        User exists = found.get();

        if (userDetails.getUsername() != null) {
            // if changing username, ensure it's not taken
            repo.findByUsername(userDetails.getUsername())
                    .filter(u -> u.getId() != id)
                    .ifPresent(u -> { throw new ResponseStatusException(CONFLICT, "username already exists"); });
            exists.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null) {
            exists.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPassword() != null) {
            exists.setPassword(userDetails.getPassword());
        }

        return ResponseEntity.ok(toDto(this.repo.save(exists)));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public ResponseEntity<Void> deleteUser(int id) {
        if (!this.repo.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.repo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
