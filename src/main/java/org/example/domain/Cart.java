package org.example.domain;

import jakarta.persistence.*;

/**
 * Represents a single line entry in a user's cart.
 * Each row ties one User to one Item with a given quantity.
 * Relationship summary:
 *   Cart (Many) --> (One) User
 *   Cart (Many) --> (One) Item
 */
@Entity
@Table(name = "cart")
public class Cart {

    // ── Fields ────────────────────────────────────────────────────────────────

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int quantity;

    /** Many cart entries can belong to one user. Foreign key: user_id. */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** Many cart entries can reference one item. Foreign key: item_id. */
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    // ── Constructor ───────────────────────────────────────────────────────────

    public Cart() {
        super();
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Item getItems() {
        return item;
    }

    public void setItems(Item item) {
        this.item = item;
    }
}
