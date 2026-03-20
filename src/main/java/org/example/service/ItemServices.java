package org.example.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.domain.Item;
import org.example.domain.User;
import org.example.repo.UserRepo;
import org.example.repo.WishlistRepo;
import org.example.repo.ItemRepo;
import org.example.rest.dto.ItemCreateRequestDto;
import org.example.rest.dto.ItemResponseDto;
import org.example.rest.dto.ItemUpdateRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service layer for Item operations.
 * Handles all business logic for creating, reading, updating, and deleting items.
 */
@Service
public class ItemServices {

    // ── Dependencies ──────────────────────────────────────────────────────────

    private final ItemRepo repo;
    private final UserRepo userRepo;
    private final WishlistRepo wishlistRepo;

    public ItemServices(ItemRepo repo, UserRepo userRepo, WishlistRepo wishlistRepo) {
        super();
        this.repo = repo;
        this.userRepo = userRepo;
        this.wishlistRepo = wishlistRepo;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /** Persists a new item and returns it with a 201 CREATED status. */
    public ResponseEntity<ItemResponseDto> createItem(ItemCreateRequestDto newItem) {
        Item created = new Item();
        created.setItemName(newItem.getItemName());
        created.setConsole(newItem.getConsole());
        created.setGenre(newItem.getGenre());
        created.setStockLevel(newItem.getStockLevel());
        created.setPrice(newItem.getPrice());
        created.setImageUrl(newItem.getImageUrl());
        created.setOnSale(newItem.getOnSale());
        created.setSaleDiscountPercent(newItem.getSaleDiscountPercent());

        validateSaleDiscountPercent(created.getSaleDiscountPercent());
        Item saved = this.repo.save(created);
        return new ResponseEntity<>(toDto(saved, false), HttpStatus.CREATED);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /** Returns all items in the catalogue. */
    public List<ItemResponseDto> getItems(Authentication authentication) {
        List<Item> items = this.repo.findAll();
        Optional<Integer> maybeUserId = resolveAuthenticatedUserId(authentication);
        if (maybeUserId.isEmpty()) {
            return items.stream().map(item -> toDto(item, false)).toList();
        }
        if (items.isEmpty()) {
            return List.of();
        }

        List<Integer> itemIds = items.stream().map(Item::getId).toList();
        Set<Integer> wishedItemIds = wishlistRepo.findByUser_IdAndItem_IdIn(maybeUserId.get(), itemIds)
                .stream()
                .map(wishlist -> wishlist.getItem().getId())
                .collect(Collectors.toSet());

        return items.stream()
                .map(item -> toDto(item, wishedItemIds.contains(item.getId())))
                .toList();
    }

    /** Returns a single item by ID, or 404 if not found. */
    public ResponseEntity<ItemResponseDto> getItem(int id, Authentication authentication) {
        Optional<Item> found = this.repo.findById(id);
        if (found.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        boolean isWishlisted = resolveAuthenticatedUserId(authentication)
                .map(userId -> wishlistRepo.findByUser_IdAndItem_Id(userId, id).isPresent())
                .orElse(false);
        return ResponseEntity.ok(toDto(found.get(), isWishlisted));
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /**
     * Partially updates an item by ID.
     * Only fields that are non-null are applied.
     * Returns the updated item, or 404 if not found.
     */
    public ResponseEntity<ItemResponseDto> updateItem(int id, ItemUpdateRequestDto itemDetails) {
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
        if (itemDetails.getSaleDiscountPercent() != null) {
            validateSaleDiscountPercent(itemDetails.getSaleDiscountPercent());
            exists.setSaleDiscountPercent(itemDetails.getSaleDiscountPercent());
        }

        return ResponseEntity.ok(toDto(this.repo.save(exists), false));
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

    private static void validateSaleDiscountPercent(Double saleDiscountPercent) {
        if (saleDiscountPercent == null) {
            return;
        }
        if (saleDiscountPercent < 0.0 || saleDiscountPercent > 100.0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "saleDiscountPercent must be between 0 and 100");
        }
    }

    private Optional<Integer> resolveAuthenticatedUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof String username) || "anonymousUser".equals(username)) {
            return Optional.empty();
        }

        return userRepo.findByUsername(username).map(User::getId);
    }

    private static ItemResponseDto toDto(Item item, boolean isWishlisted) {
        return new ItemResponseDto(
                item.getId(),
                item.getItemName(),
                item.getConsole(),
                item.getGenre(),
                item.getStockLevel(),
                item.getPrice(),
                item.getImageUrl(),
                item.getInStock(),
                item.getOnSale(),
                item.getSaleDiscountPercent(),
                isWishlisted
        );
    }
}
