package com.revshop.repository;

import com.revshop.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Required for OrderService
    List<Order> findByUserId(Long userId);

    // Find order by order ID and user ID
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    // ✅ NEW: Find all orders that contain products sold by a specific seller
    @Query("SELECT DISTINCT o FROM Order o " +
           "INNER JOIN o.items oi " +
           "INNER JOIN oi.product p " +
           "WHERE p.seller.userId = :sellerId " +
           "ORDER BY o.createdAt DESC")
    List<Order> findOrdersBySellerProductsSorted(@Param("sellerId") Long sellerId);

    // ✅ NEW: Native query to get detailed product sales information
    @Query(value = "SELECT " +
           "p.id as product_id, " +
           "p.name as product_name, " +
           "p.price, " +
           "p.quantity as current_stock, " +
           "o.id as order_id, " +
           "u.name as customer_name, " +
           "u.email as customer_email, " +
           "oi.quantity as qty_ordered, " +
           "o.created_at as order_date " +
           "FROM products p " +
           "LEFT JOIN order_items oi ON p.id = oi.product_id " +
           "LEFT JOIN orders o ON oi.order_id = o.id " +
           "LEFT JOIN users u ON o.user_id = u.user_id AND o.id IS NOT NULL " +
           "WHERE p.seller_id = :sellerId " +
           "ORDER BY p.id, o.created_at DESC", 
           nativeQuery = true)
    List<Object[]> findSellerProductSalesData(@Param("sellerId") Long sellerId);
}
