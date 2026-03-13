package com.revshop.dto;

public class CreateProductDTO {

    private String name;
    private String description;
    private double price;
    private int stock;
    private Long userId; // Seller/User ID

    // Constructors
    public CreateProductDTO() {}

    public CreateProductDTO(String name, String description, double price, int stock, Long userId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.userId = userId;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
