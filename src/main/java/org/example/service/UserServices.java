package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.domain.User;
import org.example.domain.UserRole;
import org.example.repo.UserRepo;
import org.example.rest.dto.AuthLoginRequestDto;
import org.example.rest.dto.AuthTokenResponseDto;
import org.example.rest.dto.UserCreateRequestDto;
import org.example.rest.dto.UserResponseDto;
import org.example.rest.dto.UserUpdateRequestDto;
import org.example.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserServices(UserRepo repo, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    private static UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfilePicture(),
                user.getRole()
        );
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
        created.setPassword(passwordEncoder.encode(newUser.getPassword()));
        created.setProfilePicture(normalizeProfilePictureForStorage(newUser.getProfilePicture()));
        created.setRole(UserRole.CUSTOMER);

        User saved = this.repo.save(created);
        return new ResponseEntity<>(toDto(saved), HttpStatus.CREATED);
    }

    public ResponseEntity<AuthTokenResponseDto> login(AuthLoginRequestDto loginRequest) {
        User user = repo.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "invalid username or password"));

        boolean passwordMatches;
        try {
            passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        } catch (RuntimeException ex) {
            // Legacy/plaintext password rows should not leak internals to clients.
            throw new ResponseStatusException(UNAUTHORIZED, "invalid username or password");
        }

        if (!passwordMatches) {
            throw new ResponseStatusException(UNAUTHORIZED, "invalid username or password");
        }

        if (user.getRole() == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "invalid username or password");
        }

        return ResponseEntity.ok(new AuthTokenResponseDto(
                jwtService.generateToken(user.getUsername(), user.getRole()),
                user.getRole()
        ));
    }

    public ResponseEntity<UserResponseDto> getCurrentUser(String username) {
        User user = repo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "invalid username or password"));
        return ResponseEntity.ok(toDto(user));
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
            exists.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        if (userDetails.getProfilePicture() != null) {
            exists.setProfilePicture(normalizeProfilePictureForStorage(userDetails.getProfilePicture()));
        }

        return ResponseEntity.ok(toDto(this.repo.save(exists)));
    }

    public ResponseEntity<UserResponseDto> promoteUserToAdmin(int id) {
        Optional<User> found = this.repo.findById(id);
        if (found.isEmpty()) {
            return new ResponseEntity<>(NOT_FOUND);
        }

        User user = found.get();
        user.setRole(UserRole.ADMIN);
        return ResponseEntity.ok(toDto(this.repo.save(user)));
    }

    public ResponseEntity<UserResponseDto> demoteUserToCustomer(int id) {
        Optional<User> found = this.repo.findById(id);
        if (found.isEmpty()) {
            return new ResponseEntity<>(NOT_FOUND);
        }

        User user = found.get();
        user.setRole(UserRole.CUSTOMER);
        return ResponseEntity.ok(toDto(this.repo.save(user)));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public ResponseEntity<Void> deleteUser(int id) {
        if (!this.repo.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.repo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private static String normalizeProfilePictureForStorage(String profilePicture) {
        if (profilePicture == null) {
            return null;
        }
        String trimmed = profilePicture.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
