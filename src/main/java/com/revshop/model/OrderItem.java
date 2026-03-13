package com.revshop.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "order_items")
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Relationship with Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties("items")
    private Order order;

    // ✅ Relationship with Product (captures product snapshot)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties("orderItems")
    private Product product;

    // ✅ Quantity ordered
    @Column(nullable = false)
    private int quantity;

    // ✅ Price at the time of order (snapshot of product price)
    @Column(nullable = false)
    private double price;

    // ✅ Optional: Store product name snapshot
    private String productName;

    // ✅ Optional: Total price for this item (quantity * price)
    @Column(nullable = false)
    private double itemTotal;

    // ==========================
    // Getters & Setters
    // ==========================

    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public Order getOrder() { 
        return order; 
    }

    public void setOrder(Order order) { 
        this.order = order; 
    }

    public Product getProduct() { 
        return product; 
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() { 
        return quantity; 
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() { 
        return price; 
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(double itemTotal) {
        this.itemTotal = itemTotal;
    }
}
