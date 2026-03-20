package org.example.service;

import org.example.domain.Item;
import org.example.domain.User;
import org.example.domain.Wishlist;
import org.example.repo.ItemRepo;
import org.example.repo.UserRepo;
import org.example.repo.WishlistRepo;
import org.example.rest.dto.UserWishlistSummaryDto;
import org.example.rest.dto.WishlistItemSummaryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Service layer for wishlist operations.
 */
@Service
public class WishlistServices {

    private final WishlistRepo wishlistRepo;
    private final UserRepo userRepo;
    private final ItemRepo itemRepo;

    public WishlistServices(WishlistRepo wishlistRepo, UserRepo userRepo, ItemRepo itemRepo) {
        this.wishlistRepo = wishlistRepo;
        this.userRepo = userRepo;
        this.itemRepo = itemRepo;
    }

    @Transactional
    public ResponseEntity<UserWishlistSummaryDto> addItemToWishlist(int userId, int itemId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found with id: " + userId));
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Item not found with id: " + itemId));

        if (wishlistRepo.findByUser_IdAndItem_Id(userId, itemId).isEmpty()) {
            Wishlist wishlist = new Wishlist();
            wishlist.setUser(user);
            wishlist.setItem(item);
            wishlistRepo.save(wishlist);
        }

        return getWishlistByUserId(userId);
    }

    public ResponseEntity<UserWishlistSummaryDto> getWishlistByUserId(int userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found with id: " + userId));

        List<Wishlist> entries = wishlistRepo.findByUser_Id(userId);
        UserWishlistSummaryDto summary = new UserWishlistSummaryDto();
        summary.setUserId(user.getId());
        summary.setUsername(user.getUsername());
        summary.setEmail(user.getEmail());

        for (Wishlist entry : entries) {
            Item item = entry.getItem();
            double price = item.getPrice() != null ? item.getPrice() : 0.0;
            summary.getItems().add(new WishlistItemSummaryDto(
                    item.getId(),
                    item.getItemName(),
                    item.getConsole(),
                    item.getGenre(),
                    price,
                    item.getImageUrl(),
                    item.getOnSale(),
                    item.getSaleDiscountPercent()
            ));
        }

        return ResponseEntity.ok(summary);
    }

    @Transactional
    public ResponseEntity<Void> removeItemFromWishlist(int userId, int itemId) {
        Wishlist existing = wishlistRepo.findByUser_IdAndItem_Id(userId, itemId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                        "Wishlist item not found for user id " + userId + " and item id " + itemId));

        wishlistRepo.delete(existing);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<Void> clearWishlistByUserId(int userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(NOT_FOUND, "User not found with id: " + userId);
        }

        List<Wishlist> entries = wishlistRepo.findByUser_Id(userId);
        if (!entries.isEmpty()) {
            wishlistRepo.deleteAll(entries);
        }
        return ResponseEntity.noContent().build();
    }
}

