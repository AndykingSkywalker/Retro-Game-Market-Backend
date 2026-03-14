package org.example.rest;

import java.util.List;

import org.example.domain.Item;
import org.example.service.ItemServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Item endpoints.
 * Base path: /api/item
 */
@RestController
@CrossOrigin
@RequestMapping("/api/item")
public class ItemController {

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final ItemServices service;

    public ItemController(ItemServices service) {
        super();
        this.service = service;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /** POST /api/item/create - Creates a new item. */
    @PostMapping("/create")
    public ResponseEntity<Item> createItem(@RequestBody Item newItem) {
        return this.service.createItem(newItem);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /** GET /api/item/get - Returns all items. */
    @GetMapping("/get")
    public List<Item> getItems() {
        return this.service.getItems();
    }

    /** GET /api/item/get/{id} - Returns a single item by ID. */
    @GetMapping("/get/{id}")
    public ResponseEntity<Item> getItem(@PathVariable int id) {
        return this.service.getItem(id);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /** PUT /api/item/update/{id} - Partially updates an item by ID. */
    @PutMapping("/update/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable int id, @RequestBody Item itemDetails) {
        return this.service.updateItem(id, itemDetails);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /** DELETE /api/item/delete/{id} - Deletes an item by ID. */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable int id) {
        return this.service.deleteItem(id);
    }
}
