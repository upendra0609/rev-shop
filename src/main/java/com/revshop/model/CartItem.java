package com.revshop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Relationship with Cart (owning side)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonIgnore
    private Cart cart;

    // ✅ Relationship with Product
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties("cartItems")
    private Product product;

    // ✅ Quantity in cart (can be managed by customer)
    @Column(nullable = false)
    private int quantity;

    public CartItem() {}

    public CartItem(Cart cart, Product product, int quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    // ==========================
    // Getters & Setters
    // ==========================

    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public Cart getCart() { 
        return cart; 
    }

    public void setCart(Cart cart) { 
        this.cart = cart; 
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
}
