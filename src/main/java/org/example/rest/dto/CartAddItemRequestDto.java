package org.example.rest.dto;

import jakarta.validation.constraints.Min;

public class CartAddItemRequestDto {

    @Min(1)
    private int itemId;

    @Min(1)
    private int quantity = 1;

    public CartAddItemRequestDto() {}

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

