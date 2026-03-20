package org.example.rest.dto;

import jakarta.validation.constraints.Min;

public class WishlistAddItemRequestDto {

    @Min(1)
    private int itemId;

    public WishlistAddItemRequestDto() {
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}

