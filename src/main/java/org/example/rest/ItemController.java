package org.example.rest;

import java.util.List;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.example.rest.dto.ItemCreateRequestDto;
import org.example.rest.dto.ItemResponseDto;
import org.example.rest.dto.ItemUpdateRequestDto;
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
    public ResponseEntity<ItemResponseDto> createItem(@Valid @RequestBody ItemCreateRequestDto newItem) {
        return this.service.createItem(newItem);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /** GET /api/items - Returns all items. */
    @GetMapping
    @SecurityRequirements
    public List<ItemResponseDto> getItems() {
        return this.service.getItems();
    }

    /** GET /api/items/{id} - Returns a single item by ID. */
    @GetMapping("/{id}")
    @SecurityRequirements
    public ResponseEntity<ItemResponseDto> getItem(@PathVariable int id) {
        return this.service.getItem(id);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /** PUT /api/items/{id} - Partially updates an item by ID. */
    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDto> updateItem(@PathVariable int id, @Valid @RequestBody ItemUpdateRequestDto itemDetails) {
        return this.service.updateItem(id, itemDetails);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /** DELETE /api/items/{id} - Deletes an item by ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable int id) {
        return this.service.deleteItem(id);
    }

}
