package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.domain.User;
import org.example.repo.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service layer for User operations.
 * Handles all business logic for creating, reading, updating, and deleting users.
 */
@Service
public class UserServices {

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final UserRepo repo;

    public UserServices(UserRepo repo) {
        super();
        this.repo = repo;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /** Persists a new user and returns it with a 201 CREATED status. */
    public ResponseEntity<User> createUser(User newUser) {
        User created = this.repo.save(newUser);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /** Returns all registered users. */
    public List<User> getUsers() {
        return this.repo.findAll();
    }

    /** Returns a single user by ID, or 404 if not found. */
    public ResponseEntity<User> getUser(int id) {
        Optional<User> found = this.repo.findById(id);
        if (found.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(found.get());
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /**
     * Partially updates a user by ID.
     * Only fields that are non-null are applied, allowing partial updates.
     * Returns the updated user, or 404 if not found.
     */
    public ResponseEntity<User> updateUser(int id, User userDetails) {
        Optional<User> found = this.repo.findById(id);
        if (found.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User exists = found.get();

        if (userDetails.getUsername() != null) {
            exists.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null) {
            exists.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPassword() != null) {
            exists.setPassword(userDetails.getPassword());
        }

        return ResponseEntity.ok(this.repo.save(exists));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /** Deletes a user by ID. Returns 204 NO CONTENT on success, 404 if not found. */
    public ResponseEntity<Void> deleteUser(int id) {
        if (!this.repo.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.repo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
