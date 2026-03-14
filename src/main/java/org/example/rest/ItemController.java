package org.example.rest;

import java.util.List;

import jakarta.validation.Valid;
import org.example.domain.Item;
import org.example.service.ItemServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Item endpoints.
 * Base path: /api/items
 */
@RestController
@CrossOrigin
@RequestMapping("/api/items")
public class ItemController {

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final ItemServices service;

    public ItemController(ItemServices service) {
        this.service = service;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /** POST /api/items - Creates a new item. */
    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item newItem) {
        return this.service.createItem(newItem);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /** GET /api/items - Returns all items. */
    @GetMapping
    public List<Item> getItems() {
        return this.service.getItems();
    }

    /** GET /api/items/{id} - Returns a single item by ID. */
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItem(@PathVariable int id) {
        return this.service.getItem(id);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /** PUT /api/items/{id} - Partially updates an item by ID. */
    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable int id, @RequestBody Item itemDetails) {
        return this.service.updateItem(id, itemDetails);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /** DELETE /api/items/{id} - Deletes an item by ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable int id) {
        return this.service.deleteItem(id);
    }

}
