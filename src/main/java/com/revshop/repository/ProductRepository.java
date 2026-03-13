package com.revshop.repository;

import com.revshop.model.Product;
import com.revshop.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // ✅ NEW: Find all products by seller/admin user
    List<Product> findBySeller(User seller);

	List<Product> findAllBySeller_userId(Long userId);
}

