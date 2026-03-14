package org.example.rest;

import java.util.List;

import jakarta.validation.Valid;
import org.example.rest.dto.UserCreateRequestDto;
import org.example.rest.dto.UserResponseDto;
import org.example.rest.dto.UserUpdateRequestDto;
import org.example.service.UserServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for User endpoints.
 * Base path: /api/users
 */
@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {

    private final UserServices service;

    public UserController(UserServices service) {
        this.service = service;
    }

    /** POST /api/users - Registers a new user. */
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto newUser) {
        return this.service.createUser(newUser);
    }

    /** GET /api/users - Returns all users. */
    @GetMapping
    public List<UserResponseDto> getUsers() {
        return this.service.getUsers();
    }

    /** GET /api/users/{id} - Returns a single user by ID. */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable int id) {
        return this.service.getUser(id);
    }

    /** PUT /api/users/{id} - Partially updates a user by ID. */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable int id, @Valid @RequestBody UserUpdateRequestDto userDetails) {
        return this.service.updateUser(id, userDetails);
    }

    /** DELETE /api/users/{id} - Deletes a user by ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        return this.service.deleteUser(id);
    }

}
