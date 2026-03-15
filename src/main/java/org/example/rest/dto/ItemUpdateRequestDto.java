package org.example.rest.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;

public class ItemUpdateRequestDto {

    private String itemName;
    private String console;
    private String genre;

    @PositiveOrZero(message = "stockLevel must be >= 0")
    private Integer stockLevel;

    @PositiveOrZero(message = "price must be >= 0")
    private Double price;

    private String imageUrl;
    private Boolean onSale;

    @DecimalMin(value = "0.0", message = "saleDiscountPercent must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "saleDiscountPercent must be between 0 and 100")
    private Double saleDiscountPercent;

    public ItemUpdateRequestDto() {
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

    public Integer getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(Integer stockLevel) {
        this.stockLevel = stockLevel;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
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

