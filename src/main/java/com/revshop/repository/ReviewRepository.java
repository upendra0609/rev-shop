package com.revshop.repository;

import com.revshop.model.Review;
import com.revshop.model.Product;
import com.revshop.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Get reviews of product
    List<Review> findByProduct(Product product);

    // Get reviews by user
    List<Review> findByUser(User user);
}
