package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.domain.Item;
import org.example.repo.ItemRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service layer for Item operations.
 * Handles all business logic for creating, reading, updating, and deleting items.
 */
@Service
public class ItemServices {

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final ItemRepo repo;

    public ItemServices(ItemRepo repo) {
        super();
        this.repo = repo;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /** Persists a new item and returns it with a 201 CREATED status. */
    public ResponseEntity<Item> createItem(Item newItem) {
        Item created = this.repo.save(newItem);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /** Returns all items in the catalogue. */
    public List<Item> getItems() {
        return this.repo.findAll();
    }

    /** Returns a single item by ID, or 404 if not found. */
    public ResponseEntity<Item> getItem(int id) {
        Optional<Item> found = this.repo.findById(id);
        if (found.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(found.get());
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /**
     * Partially updates an item by ID.
     * Only fields that are non-null (and non-zero for numerics) are applied.
     * Returns the updated item, or 404 if not found.
     */
    public ResponseEntity<Item> updateItem(int id, Item itemDetails) {
        Optional<Item> found = this.repo.findById(id);
        if (found.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Item exists = found.get();

        if (itemDetails.getItemName() != null) {
            exists.setItemName(itemDetails.getItemName());
        }
        if (itemDetails.getConsole() != null) {
            exists.setConsole(itemDetails.getConsole());
        }
        if (itemDetails.getGenre() != null) {
            exists.setGenre(itemDetails.getGenre());
        }
        if (itemDetails.getStockLevel() != null) {
            exists.setStockLevel(itemDetails.getStockLevel());
        }
        if (itemDetails.getPrice() != null) {
            exists.setPrice(itemDetails.getPrice());
        }
        if (itemDetails.getImageUrl() != null) {
            exists.setImageUrl(itemDetails.getImageUrl());
        }
        if (itemDetails.getOnSale() != null) {
            exists.setOnSale(itemDetails.getOnSale());
        }

        return ResponseEntity.ok(this.repo.save(exists));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /** Deletes an item by ID. Returns 204 NO CONTENT on success, 404 if not found. */
    public ResponseEntity<Void> deleteItem(int id) {
        if (!this.repo.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.repo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
