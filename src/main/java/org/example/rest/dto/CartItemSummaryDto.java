package org.example.rest.dto;

public class CartItemSummaryDto {
    private int itemId;
    private String itemName;
    private String console;
    private String genre;
    private double price;
    private int quantity;

    public CartItemSummaryDto() {}

    public CartItemSummaryDto(int itemId, String itemName, String console, String genre, double price, int quantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.console = console;
        this.genre = genre;
        this.price = price;
        this.quantity = quantity;
    }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getConsole() { return console; }
    public void setConsole(String console) { this.console = console; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
