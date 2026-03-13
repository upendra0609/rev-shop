package com.revshop.dto;

import java.time.LocalDateTime;

/**
 * DTO that represents a single row from the product sales query
 * Maps directly to the SQL query: SELECT p.id as product_id, p.name as product_name, ...
 */
public class ProductSalesDataDTO {

    private Long productId;
    private String productName;
    private Double price;
    private Integer currentStock;
    private Long orderId;
    private String customerName;
    private String customerEmail;
    private Integer qtyOrdered;
    private LocalDateTime orderDate;

    // Constructors
    public ProductSalesDataDTO() {}

    public ProductSalesDataDTO(Long productId, String productName, Double price, Integer currentStock,
                               Long orderId, String customerName, String customerEmail,
                               Integer qtyOrdered, LocalDateTime orderDate) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.currentStock = currentStock;
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.qtyOrdered = qtyOrdered;
        this.orderDate = orderDate;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public Integer getQtyOrdered() {
        return qtyOrdered;
    }

    public void setQtyOrdered(Integer qtyOrdered) {
        this.qtyOrdered = qtyOrdered;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public String toString() {
        return "ProductSalesDataDTO{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", currentStock=" + currentStock +
                ", orderId=" + orderId +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", qtyOrdered=" + qtyOrdered +
                ", orderDate=" + orderDate +
                '}';
    }
}
