package org.example.rest;

import java.util.List;

import org.example.domain.User;
import org.example.service.UserServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for User endpoints.
 * Base path: /api/user
 */
@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final UserServices service;

    public UserController(UserServices service) {
        super();
        this.service = service;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /** POST /api/user/create - Registers a new user. */
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        return this.service.createUser(newUser);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /** GET /api/user/get - Returns all users. */
    @GetMapping("/get")
    public List<User> getUsers() {
        return this.service.getUsers();
    }

    /** GET /api/user/get/{id} - Returns a single user by ID. */
    @GetMapping("/get/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id) {
        return this.service.getUser(id);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /** PUT /api/user/update/{id} - Partially updates a user by ID. */
    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User userDetails) {
        return this.service.updateUser(id, userDetails);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /** DELETE /api/user/delete/{id} - Deletes a user by ID. */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        return this.service.deleteUser(id);
    }
}
