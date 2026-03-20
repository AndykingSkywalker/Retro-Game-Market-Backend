package org.example.rest.dto;

public class OrderLineResponseDto {
    private int itemId;
    private String itemName;
    private String console;
    private String genre;
    private String imageUrl;
    private double unitPrice;
    private Boolean onSale;
    private Double saleDiscountPercent;
    private double discountedUnitPrice;
    private int quantity;
    private double lineTotal;

    public OrderLineResponseDto() {
    }

    public OrderLineResponseDto(
            int itemId,
            String itemName,
            String console,
            String genre,
            String imageUrl,
            double unitPrice,
            Boolean onSale,
            Double saleDiscountPercent,
            double discountedUnitPrice,
            int quantity,
            double lineTotal
    ) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.console = console;
        this.genre = genre;
        this.imageUrl = imageUrl;
        this.unitPrice = unitPrice;
        this.onSale = onSale;
        this.saleDiscountPercent = saleDiscountPercent;
        this.discountedUnitPrice = discountedUnitPrice;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
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

    public double getDiscountedUnitPrice() {
        return discountedUnitPrice;
    }

    public void setDiscountedUnitPrice(double discountedUnitPrice) {
        this.discountedUnitPrice = discountedUnitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(double lineTotal) {
        this.lineTotal = lineTotal;
    }
}

