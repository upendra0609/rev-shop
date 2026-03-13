package com.revshop.dto;

import java.util.List;

/**
 * Request DTO for placing an order
 * UI sends cart items, address, and payment method
 */
public class PlaceOrderRequest {

    private Long userId;
    private Long addressId;
    private String paymentMethod; // CASH, CARD, UPI
    private List<OrderItemRequest> items; // Cart items to order

    // Getters & Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "PlaceOrderRequest{" +
                "userId=" + userId +
                ", addressId=" + addressId +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", items=" + items +
                '}';
    }

    /**
     * Nested class for order items
     */
    public static class OrderItemRequest {
        private Long productId;
        private int quantity;
        private double price; // Current product price

        public OrderItemRequest() {}

        public OrderItemRequest(Long productId, int quantity, double price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }

        // Getters & Setters
        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
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

        @Override
        public String toString() {
            return "OrderItemRequest{" +
                    "productId=" + productId +
                    ", quantity=" + quantity +
                    ", price=" + price +
                    '}';
        }
    }
}
