package org.example.rest.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderResponseDto {
    private int id;
    private String orderNumber;
    private int userId;
    private String username;
    private Instant createdAt;
    private int lineItemCount;
    private int totalQuantity;
    private double totalAmount;
    private List<OrderLineResponseDto> items = new ArrayList<>();

    public OrderResponseDto() {
    }

    public OrderResponseDto(
            int id,
            String orderNumber,
            int userId,
            String username,
            Instant createdAt,
            int lineItemCount,
            int totalQuantity,
            double totalAmount,
            List<OrderLineResponseDto> items
    ) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.username = username;
        this.createdAt = createdAt;
        this.lineItemCount = lineItemCount;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public int getLineItemCount() {
        return lineItemCount;
    }

    public void setLineItemCount(int lineItemCount) {
        this.lineItemCount = lineItemCount;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderLineResponseDto> getItems() {
        return items;
    }

    public void setItems(List<OrderLineResponseDto> items) {
        this.items = items;
    }
}


