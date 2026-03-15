package org.example.rest.dto;

public class ItemResponseDto {

    private Integer id;
    private String itemName;
    private String console;
    private String genre;
    private Integer stockLevel;
    private Double price;
    private String imageUrl;
    private Boolean inStock;
    private Boolean onSale;
    private Double saleDiscountPercent;

    public ItemResponseDto() {
    }

    public ItemResponseDto(
            Integer id,
            String itemName,
            String console,
            String genre,
            Integer stockLevel,
            Double price,
            String imageUrl,
            Boolean inStock,
            Boolean onSale,
            Double saleDiscountPercent
    ) {
        this.id = id;
        this.itemName = itemName;
        this.console = console;
        this.genre = genre;
        this.stockLevel = stockLevel;
        this.price = price;
        this.imageUrl = imageUrl;
        this.inStock = inStock;
        this.onSale = onSale;
        this.saleDiscountPercent = saleDiscountPercent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
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

