package org.example.rest;

import org.example.rest.dto.OrderResponseDto;
import org.example.service.OrderServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for order checkout and order history.
 * Base path: /api/orders
 */
@RestController
@CrossOrigin
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderServices orderServices;

    public OrderController(OrderServices orderServices) {
        this.orderServices = orderServices;
    }

    /** GET /api/orders - Returns all orders for admin review. */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponseDto> getAllOrders() {
        return orderServices.getAllOrders();
    }

    /** POST /api/orders/users/{userId}/checkout - Processes the user's current basket into an order. */
    @PostMapping("/users/{userId}/checkout")
    @PreAuthorize("@authorizationService.isSelfOrAdmin(authentication, #userId)")
    public ResponseEntity<OrderResponseDto> checkout(@PathVariable int userId) {
        return orderServices.checkout(userId);
    }

    /** GET /api/orders/users/{userId} - Returns the user's order history. */
    @GetMapping("/users/{userId}")
    @PreAuthorize("@authorizationService.isSelfOrAdmin(authentication, #userId)")
    public List<OrderResponseDto> getOrderHistory(@PathVariable int userId) {
        return orderServices.getOrderHistory(userId);
    }
}


