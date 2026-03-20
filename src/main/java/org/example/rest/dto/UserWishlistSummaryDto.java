package org.example.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class UserWishlistSummaryDto {
    private int userId;
    private String username;
    private String email;
    private List<WishlistItemSummaryDto> items = new ArrayList<>();

    public UserWishlistSummaryDto() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<WishlistItemSummaryDto> getItems() {
        return items;
    }

    public void setItems(List<WishlistItemSummaryDto> items) {
        this.items = items;
    }
}

