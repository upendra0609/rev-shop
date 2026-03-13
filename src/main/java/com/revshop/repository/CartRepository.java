package com.revshop.repository;

import com.revshop.model.Cart;
import com.revshop.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Get cart by user
    Optional<Cart> findByUser(User user);
}
