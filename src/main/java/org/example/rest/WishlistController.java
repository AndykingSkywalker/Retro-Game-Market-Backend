package org.example.rest;

import jakarta.validation.Valid;
import org.example.rest.dto.UserWishlistSummaryDto;
import org.example.rest.dto.WishlistAddItemRequestDto;
import org.example.service.WishlistServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for wishlist endpoints.
 * Base path: /api/wishlists
 */
@RestController
@CrossOrigin
@RequestMapping("/api/wishlists")
public class WishlistController {

    private final WishlistServices wishlistServices;

    public WishlistController(WishlistServices wishlistServices) {
        this.wishlistServices = wishlistServices;
    }

    /** POST /api/wishlists/users/{userId}/items - Adds an item to wishlist (idempotent). */
    @PostMapping("/users/{userId}/items")
    @PreAuthorize("@authorizationService.isSelfOrAdmin(authentication, #userId)")
    public ResponseEntity<UserWishlistSummaryDto> addItemToWishlist(
            @PathVariable int userId,
            @Valid @RequestBody WishlistAddItemRequestDto request
    ) {
        return wishlistServices.addItemToWishlist(userId, request.getItemId());
    }

    /** GET /api/wishlists/users/{userId} - Returns user's wishlist items. */
    @GetMapping("/users/{userId}")
    @PreAuthorize("@authorizationService.isSelfOrAdmin(authentication, #userId)")
    public ResponseEntity<UserWishlistSummaryDto> getWishlistByUserId(@PathVariable int userId) {
        return wishlistServices.getWishlistByUserId(userId);
    }

    /** DELETE /api/wishlists/users/{userId}/items/{itemId} - Removes one item from wishlist. */
    @DeleteMapping("/users/{userId}/items/{itemId}")
    @PreAuthorize("@authorizationService.isSelfOrAdmin(authentication, #userId)")
    public ResponseEntity<Void> removeItemFromWishlist(@PathVariable int userId, @PathVariable int itemId) {
        return wishlistServices.removeItemFromWishlist(userId, itemId);
    }

    /** DELETE /api/wishlists/users/{userId} - Clears user's wishlist. */
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("@authorizationService.isSelfOrAdmin(authentication, #userId)")
    public ResponseEntity<Void> clearWishlistByUserId(@PathVariable int userId) {
        return wishlistServices.clearWishlistByUserId(userId);
    }
}

