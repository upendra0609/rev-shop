package com.revshop.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ProductSalesDTO {

    private Long productId;
    private String productName;
    private String productDescription;
    private double productPrice;
    private int currentStock;
    private int totalQuantityOrdered; // Sum of all quantities ordered
    private double totalRevenue; // Sum of all order amounts for this product
    private int totalOrders; // Number of unique orders containing this product
    private LocalDateTime productCreatedAt;
    private LocalDateTime productUpdatedAt;
    private List<ProductOrderDetailDTO> orders; // List of all customer orders for this product

    // Constructors
    public ProductSalesDTO() {}

    public ProductSalesDTO(Long productId, String productName, String productDescription,
                          double productPrice, int currentStock, int totalQuantityOrdered,
                          double totalRevenue, int totalOrders, LocalDateTime productCreatedAt,
                          LocalDateTime productUpdatedAt, List<ProductOrderDetailDTO> orders) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.currentStock = currentStock;
        this.totalQuantityOrdered = totalQuantityOrdered;
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.productCreatedAt = productCreatedAt;
        this.productUpdatedAt = productUpdatedAt;
        this.orders = orders;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getTotalQuantityOrdered() {
        return totalQuantityOrdered;
    }

    public void setTotalQuantityOrdered(int totalQuantityOrdered) {
        this.totalQuantityOrdered = totalQuantityOrdered;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public LocalDateTime getProductCreatedAt() {
        return productCreatedAt;
    }

    public void setProductCreatedAt(LocalDateTime productCreatedAt) {
        this.productCreatedAt = productCreatedAt;
    }

    public LocalDateTime getProductUpdatedAt() {
        return productUpdatedAt;
    }

    public void setProductUpdatedAt(LocalDateTime productUpdatedAt) {
        this.productUpdatedAt = productUpdatedAt;
    }

    public List<ProductOrderDetailDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<ProductOrderDetailDTO> orders) {
        this.orders = orders;
    }
}
