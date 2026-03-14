package org.example.rest;

import java.util.List;

import jakarta.validation.Valid;
import org.example.domain.Cart;
import org.example.rest.dto.CartAddItemRequestDto;
import org.example.rest.dto.CartUpdateQuantityRequestDto;
import org.example.rest.dto.UserCartSummaryDto;
import org.example.service.CartServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Cart endpoints.
 * Base path: /api/carts
 */
@RestController
@CrossOrigin
@RequestMapping("/api/carts")
public class CartController {

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final CartServices cartServices;

    public CartController(CartServices cartServices) {
        this.cartServices = cartServices;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * POST /api/carts/users/{userId}/items - Adds an item to a user's cart.
     * Body: { itemId, quantity }
     * Returns the full updated cart summary for the user.
     */
    @PostMapping("/users/{userId}/items")
    @PreAuthorize("@authorizationService.isSelfOrAdmin(authentication, #userId)")
    public ResponseEntity<UserCartSummaryDto> addItemToCart(@PathVariable int userId,
                                                            @Valid @RequestBody CartAddItemRequestDto request) {
        return cartServices.addItemToCart(userId, request.getItemId(), request.getQuantity());
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /** GET /api/carts - Returns a cart summary for every user that has items in their cart. */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserCartSummaryDto> getAllCartSummaries() {
        return cartServices.getAllCartSummaries();
    }

    /** GET /api/carts/users/{userId} - Returns the user with their full cart and running total. */
    @GetMapping("/users/{userId}")
    @PreAuthorize("@authorizationService.isSelfOrAdmin(authentication, #userId)")
    public ResponseEntity<UserCartSummaryDto> getCartByUserId(@PathVariable int userId) {
        return cartServices.getCartSummaryByUserId(userId);
    }

    /**
     * PUT /api/carts/{cartId} - Updates quantity on a specific cart entry.
     * Body: { quantity }
     */
    @PutMapping("/{cartId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cart> updateCartQuantity(@PathVariable int cartId,
                                                   @Valid @RequestBody CartUpdateQuantityRequestDto request) {
        return cartServices.updateCartQuantity(cartId, request.getQuantity());
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /** DELETE /api/carts/{cartId} - Deletes a single cart entry by its ID. */
    @DeleteMapping("/{cartId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCart(@PathVariable int cartId) {
        return cartServices.deleteCart(cartId);
    }

    /** DELETE /api/carts/users/{userId} - Clears all cart entries for a given user. */
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("@authorizationService.isSelfOrAdmin(authentication, #userId)")
    public ResponseEntity<Void> clearCartByUserId(@PathVariable int userId) {
        return cartServices.clearCartByUserId(userId);
    }

    /** DELETE /api/carts/users/{userId}/items/{itemId} - Removes a specific item from a user's cart. */
    @DeleteMapping("/users/{userId}/items/{itemId}")
    @PreAuthorize("@authorizationService.isSelfOrAdmin(authentication, #userId)")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable int userId,
                                                   @PathVariable int itemId) {
        return cartServices.removeItemFromCart(userId, itemId);
    }

}
