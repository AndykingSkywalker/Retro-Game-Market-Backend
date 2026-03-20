package org.example.repo;

import org.example.domain.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {

    @EntityGraph(attributePaths = {"user", "lines"})
    List<Order> findByUser_IdOrderByCreatedAtDescIdDesc(int userId);

    @EntityGraph(attributePaths = {"user", "lines"})
    List<Order> findAllByOrderByCreatedAtDescIdDesc();

    boolean existsByOrderNumber(String orderNumber);
}


