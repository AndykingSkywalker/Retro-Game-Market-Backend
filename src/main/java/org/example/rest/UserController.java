package org.example.rest;

import java.util.List;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.example.rest.dto.UserCreateRequestDto;
import org.example.rest.dto.UserResponseDto;
import org.example.rest.dto.UserUpdateRequestDto;
import org.example.service.UserServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @SecurityRequirements
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto newUser) {
        return this.service.createUser(newUser);
    }

    /** GET /api/users - Returns all users. */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDto> getUsers() {
        return this.service.getUsers();
    }

    /** GET /api/users/{id} - Returns a single user by ID. */
    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.isSelfOrAdmin(authentication, #id)")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable int id) {
        return this.service.getUser(id);
    }

    /** PUT /api/users/{id} - Partially updates a user by ID. */
    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.isSelfOrAdmin(authentication, #id)")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable int id, @Valid @RequestBody UserUpdateRequestDto userDetails) {
        return this.service.updateUser(id, userDetails);
    }

    /** POST /api/users/{id}/promote-admin - Promotes a user to ADMIN. */
    @PostMapping("/{id}/promote-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> promoteUserToAdmin(@PathVariable int id) {
        return this.service.promoteUserToAdmin(id);
    }

    /** POST /api/users/{id}/demote-customer - Demotes a user to CUSTOMER. */
    @PostMapping("/{id}/demote-customer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> demoteUserToCustomer(@PathVariable int id) {
        return this.service.demoteUserToCustomer(id);
    }

    /** DELETE /api/users/{id} - Deletes a user by ID. */
    @DeleteMapping("/{id}")
    @PreAuthorize("@authorizationService.isSelfOrAdmin(authentication, #id)")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        return this.service.deleteUser(id);
    }

}
