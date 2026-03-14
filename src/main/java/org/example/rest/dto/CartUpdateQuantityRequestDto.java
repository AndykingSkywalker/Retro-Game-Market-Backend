package org.example.rest.dto;

import jakarta.validation.constraints.Min;

public class CartUpdateQuantityRequestDto {
    @Min(0)
    private int quantity;

    public CartUpdateQuantityRequestDto() {}

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
