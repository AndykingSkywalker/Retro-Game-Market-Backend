package org.example.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class UserCartSummaryDto {
    private int userId;
    private String username;
    private String email;
    private List<CartItemSummaryDto> items = new ArrayList<>();
    private double total;

    public UserCartSummaryDto() {}

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<CartItemSummaryDto> getItems() { return items; }
    public void setItems(List<CartItemSummaryDto> items) { this.items = items; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
