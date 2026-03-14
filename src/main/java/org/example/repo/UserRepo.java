package org.example.repo;

import org.example.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entities.
 * Extends JpaRepository to provide standard CRUD operations out of the box.
 * Note: findByUsername will be required when JWT authentication is implemented.
 */
@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    /** Finds a user by their username - used for JWT authentication lookup. */
    Optional<User> findByUsername(String username);
}
