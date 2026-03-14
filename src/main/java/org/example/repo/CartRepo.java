package org.example.repo;

import org.example.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for Cart entities.
 * Extends JpaRepository to provide standard CRUD operations out of the box.
 * Includes a custom query to fetch all cart entries for a specific user.
 */
@Repository
public interface CartRepo extends JpaRepository<Cart, Integer> {

    /** Returns all cart entries belonging to the given user ID. */
    List<Cart> findByUserId(int userId);
}
