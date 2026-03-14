package org.example.repo;

import org.example.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface CartRepo extends JpaRepository<Cart, Integer> {
    List<Cart> findByUserId(int userId);
}
