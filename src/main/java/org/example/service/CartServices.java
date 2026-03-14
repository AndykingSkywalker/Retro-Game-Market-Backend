package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.domain.Cart;
import org.example.domain.Item;
import org.example.domain.User;
import org.example.repo.CartRepo;
import org.example.repo.ItemRepo;
import org.example.repo.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service layer for Cart operations.
 * Handles all business logic for managing a user's cart, including
 * adding/removing items, updating quantities, and clearing the cart.
 */
@Service
public class CartServices {

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final CartRepo cartRepo;
    private final UserRepo userRepo;
    private final ItemRepo itemRepo;

    public CartServices(CartRepo cartRepo, UserRepo userRepo, ItemRepo itemRepo) {
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
        this.itemRepo = itemRepo;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Adds an item to a user's cart.
     * If the user already has a cart entry for that item, the quantity is incremented.
     * Otherwise a new cart entry is created with quantity = 1.
     * Returns 404 if the user or item does not exist.
     */
    public ResponseEntity<Cart> addItemToCart(int userId, int itemId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));

        Optional<Cart> existing = cartRepo.findByUserId(userId)
                .stream()
                .filter(c -> c.getItems().getId().equals(itemId))
                .findFirst();

        Cart cart;
        if (existing.isPresent()) {
            cart = existing.get();
            cart.setQuantity(cart.getQuantity() + 1);
        } else {
            cart = new Cart();
            cart.setUser(user);
            cart.setItems(item);
            cart.setQuantity(1);
        }

        return ResponseEntity.ok(cartRepo.save(cart));
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /** Returns every cart entry in the system. */
    public List<Cart> getAllCarts() {
        return cartRepo.findAll();
    }

    /** Returns a single cart entry by its own ID, or 404 if not found. */
    public ResponseEntity<Cart> getCartById(int cartId) {
        return cartRepo.findById(cartId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Returns all cart entries belonging to a specific user. */
    public List<Cart> getCartByUserId(int userId) {
        return cartRepo.findByUserId(userId);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /**
     * Updates the quantity on a specific cart entry.
     * Returns the updated cart entry, or 404 if not found.
     */
    public ResponseEntity<Cart> updateCartQuantity(int cartId, int quantity) {
        return cartRepo.findById(cartId)
                .map(cart -> {
                    cart.setQuantity(quantity);
                    return ResponseEntity.ok(cartRepo.save(cart));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /** Deletes a single cart entry by its ID. Returns 204 NO CONTENT, or 404 if not found. */
    public ResponseEntity<Void> deleteCart(int cartId) {
        if (!cartRepo.existsById(cartId)) {
            return ResponseEntity.notFound().build();
        }
        cartRepo.deleteById(cartId);
        return ResponseEntity.noContent().build();
    }

    /** Clears all cart entries for a given user (empties their basket). Returns 404 if no entries exist. */
    public ResponseEntity<Void> clearCartByUserId(int userId) {
        List<Cart> userCart = cartRepo.findByUserId(userId);
        if (userCart.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        cartRepo.deleteAll(userCart);
        return ResponseEntity.noContent().build();
    }

    // ── Item Helpers ──────────────────────────────────────────────────────────

    /**
     * Removes a specific item from a user's cart.
     * Finds and deletes the cart entry matching the given user and item.
     * Returns 404 if no matching entry is found.
     */
    public ResponseEntity<Void> removeItemFromCart(int userId, int itemId) {
        Optional<Cart> cartEntry = cartRepo.findByUserId(userId)
                .stream()
                .filter(c -> c.getItems().getId().equals(itemId))
                .findFirst();

        if (cartEntry.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        cartRepo.delete(cartEntry.get());
        return ResponseEntity.noContent().build();
    }
}