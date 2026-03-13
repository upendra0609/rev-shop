package com.revshop.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Rating (1-5 stars)
    @Column(nullable = false)
    private int rating;

    // ✅ Review comment
    @Column(length = 1000)
    private String comment;

    // ✅ Timestamp for when review was created
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ✅ Relationship with User (who wrote the review)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties("reviews")
    private User user;

    // ✅ Relationship with Product (which product is being reviewed)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties("reviews")
    private Product product;

    // ✅ PrePersist and PreUpdate for timestamps
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Review() {}

    // ==========================
    // Getters & Setters
    // ==========================

    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public int getRating() { 
        return rating; 
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() { 
        return comment; 
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() { 
        return user; 
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() { 
        return product; 
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
