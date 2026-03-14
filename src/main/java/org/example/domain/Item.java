package org.example.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Represents a retro game item available in the marketplace.
 * Each item holds stock, pricing, and catalogue information.
 */
@Entity
@Table(name = "item")
public class Item {

    // ── Fields ────────────────────────────────────────────────────────────────

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "itemName is required")
    private String itemName;

    private String console;
    private String genre;

    @PositiveOrZero(message = "stockLevel must be >= 0")
    private Integer stockLevel;

    @PositiveOrZero(message = "price must be >= 0")
    private Double price;

    private Boolean inStock;
    private Boolean onSale;


    // ── Constructor ───────────────────────────────────────────────────────────

    public Item() {
        super();
    }


    // ── Getters & Setters ─────────────────────────────────────────────────────

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

    /** Derived: true when stockLevel is greater than zero. Never stored separately. */
    @Transient
    public Boolean getInStock() {
        return stockLevel != null && stockLevel > 0;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }
}
