package org.example.rest.dto;

public class WishlistItemSummaryDto {
    private int itemId;
    private String itemName;
    private String console;
    private String genre;
    private double price;
    private String imageUrl;
    private Boolean onSale;
    private Double saleDiscountPercent;

    public WishlistItemSummaryDto() {
    }

    public WishlistItemSummaryDto(
            int itemId,
            String itemName,
            String console,
            String genre,
            double price,
            String imageUrl,
            Boolean onSale,
            Double saleDiscountPercent
    ) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.console = console;
        this.genre = genre;
        this.price = price;
        this.imageUrl = imageUrl;
        this.onSale = onSale;
        this.saleDiscountPercent = saleDiscountPercent;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getConsole() {
        return console;
    }

    public void setConsole(String console) {
        this.console = console;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }

    public Double getSaleDiscountPercent() {
        return saleDiscountPercent;
    }

    public void setSaleDiscountPercent(Double saleDiscountPercent) {
        this.saleDiscountPercent = saleDiscountPercent;
    }
}

