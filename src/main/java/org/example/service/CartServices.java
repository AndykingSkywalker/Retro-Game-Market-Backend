package org.example.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.domain.Cart;
import org.example.domain.Item;
import org.example.domain.User;
import org.example.repo.CartRepo;
import org.example.repo.ItemRepo;
import org.example.repo.UserRepo;
import org.example.rest.dto.CartItemSummaryDto;
import org.example.rest.dto.UserCartSummaryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

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
     * Otherwise a new cart entry is created.
     * Returns the full updated cart summary for the user.
     */
    @Transactional
    public ResponseEntity<UserCartSummaryDto> addItemToCart(int userId, int itemId, int quantityToAdd) {
        if (quantityToAdd <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "quantity must be >= 1");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found with id: " + userId));
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Item not found with id: " + itemId));

        Cart cart = cartRepo.findByUser_IdAndItem_Id(userId, itemId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    c.setItem(item);
                    c.setQuantity(0);
                    return c;
                });

        cart.setQuantity(cart.getQuantity() + quantityToAdd);
        cartRepo.save(cart);
        return getCartSummaryByUserId(userId);
    }


    // ── Read ──────────────────────────────────────────────────────────────────

    /** Returns a cart summary for every user that has at least one item in their cart. */
    public List<UserCartSummaryDto> getAllCartSummaries() {
        Map<User, List<Cart>> byUser = cartRepo.findAll().stream()
                .collect(Collectors.groupingBy(Cart::getUser));

        return byUser.entrySet().stream().map(entry -> {
            User user = entry.getKey();
            List<Cart> entries = entry.getValue();

            UserCartSummaryDto summary = new UserCartSummaryDto();
            summary.setUserId(user.getId());
            summary.setUsername(user.getUsername());
            summary.setEmail(user.getEmail());

            double total = 0.0;
            for (Cart cartEntry : entries) {
                Item item = cartEntry.getItem();
                int quantity = cartEntry.getQuantity();
                double price = item.getPrice() != null ? item.getPrice() : 0.0;

                summary.getItems().add(new CartItemSummaryDto(
                        item.getId(),
                        item.getItemName(),
                        item.getConsole(),
                        item.getGenre(),
                        price,
                        quantity
                ));
                total += price * quantity;
            }

            summary.setTotal(total);
            return summary;
        }).collect(Collectors.toList());
    }

    public ResponseEntity<UserCartSummaryDto> getCartSummaryByUserId(int userId) {
        List<Cart> userCart = cartRepo.findByUser_Id(userId);

        // Prefer returning an empty cart rather than 404 if user exists.
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found with id: " + userId));

        UserCartSummaryDto summary = new UserCartSummaryDto();
        summary.setUserId(user.getId());
        summary.setUsername(user.getUsername());
        summary.setEmail(user.getEmail());

        double total = 0.0;
        for (Cart entry : userCart) {
            Item item = entry.getItem();
            int quantity = entry.getQuantity();
            double price = item.getPrice() != null ? item.getPrice() : 0.0;

            summary.getItems().add(new CartItemSummaryDto(
                    item.getId(),
                    item.getItemName(),
                    item.getConsole(),
                    item.getGenre(),
                    price,
                    quantity
            ));

            total += price * quantity;
        }

        summary.setTotal(total);
        return ResponseEntity.ok(summary);
    }


    // ── Update ────────────────────────────────────────────────────────────────

    /**
     * Updates the quantity on a specific cart entry.
     * Returns the updated cart entry, or 404 if not found.
     */
    public ResponseEntity<Cart> updateCartQuantity(int cartId, int quantity) {
        if (quantity < 0) {
            throw new ResponseStatusException(BAD_REQUEST, "quantity must be >= 0");
        }

        Optional<Cart> found = cartRepo.findById(cartId);
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Cart cart = found.get();
        if (quantity == 0) {
            cartRepo.delete(cart);
            return ResponseEntity.noContent().build();
        }

        cart.setQuantity(quantity);
        return ResponseEntity.ok(cartRepo.save(cart));
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
        List<Cart> userCart = cartRepo.findByUser_Id(userId);
        if (userCart.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        cartRepo.deleteAll(userCart);
        return ResponseEntity.noContent().build();
    }

    /** Removes a specific item from a user's cart.
     * Finds and deletes the cart entry matching the given user and item.
     * Returns 404 if no matching entry is found.
     */
    public ResponseEntity<Void> removeItemFromCart(int userId, int itemId) {
        Optional<Cart> cartEntry = cartRepo.findByUser_IdAndItem_Id(userId, itemId);

        if (cartEntry.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        cartRepo.delete(cartEntry.get());
        return ResponseEntity.noContent().build();
    }
}
