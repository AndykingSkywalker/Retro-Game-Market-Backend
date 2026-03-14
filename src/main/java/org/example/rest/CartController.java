package org.example.rest;

import java.util.List;

import org.example.domain.Cart;
import org.example.service.CartServices;
import org.springframework.http.ResponseEntity;
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

    /** POST /api/carts/user/{userId}/item/{itemId} - Adds an item to a user's cart, or increments quantity if already present. */
    @PostMapping("/user/{userId}/item/{itemId}")
    public ResponseEntity<Cart> addItemToCart(@PathVariable int userId,
                                              @PathVariable int itemId) {
        return cartServices.addItemToCart(userId, itemId);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /** GET /api/carts - Returns all cart entries in the system. */
    @GetMapping
    public List<Cart> getAllCarts() {
        return cartServices.getAllCarts();
    }

    /** GET /api/carts/{cartId} - Returns a single cart entry by its ID. */
    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCartById(@PathVariable int cartId) {
        return cartServices.getCartById(cartId);
    }

    /** GET /api/carts/user/{userId} - Returns all cart entries for a given user. */
    @GetMapping("/user/{userId}")
    public List<Cart> getCartByUserId(@PathVariable int userId) {
        return cartServices.getCartByUserId(userId);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /** PUT /api/carts/{cartId}/quantity/{quantity} - Updates the quantity on a specific cart entry. */
    @PutMapping("/{cartId}/quantity/{quantity}")
    public ResponseEntity<Cart> updateCartQuantity(@PathVariable int cartId,
                                                   @PathVariable int quantity) {
        return cartServices.updateCartQuantity(cartId, quantity);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /** DELETE /api/carts/{cartId} - Deletes a single cart entry by its ID. */
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCart(@PathVariable int cartId) {
        return cartServices.deleteCart(cartId);
    }

    /** DELETE /api/carts/user/{userId} - Clears all cart entries for a given user. */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clearCartByUserId(@PathVariable int userId) {
        return cartServices.clearCartByUserId(userId);
    }

    /** DELETE /api/carts/user/{userId}/item/{itemId} - Removes a specific item from a user's cart. */
    @DeleteMapping("/user/{userId}/item/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable int userId,
                                                   @PathVariable int itemId) {
        return cartServices.removeItemFromCart(userId, itemId);
    }
}
