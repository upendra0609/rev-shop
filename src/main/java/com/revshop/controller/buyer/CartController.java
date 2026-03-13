package com.revshop.controller.buyer;

import com.revshop.dto.ApiResponse;
import com.revshop.model.Cart;
import com.revshop.service.buyer.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;


    // Add to Cart
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestParam Long userId,
                                      @RequestParam Long productId,
                                      @RequestParam int qty) {

        cartService.addToCart(userId, productId, qty);

        return ResponseEntity.ok(new ApiResponse<>(true, "Product added to cart successfully"));
    }


    // View Cart
    @GetMapping("/{userId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> viewCart(@PathVariable Long userId) {

        Cart cart = cartService.viewCart(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart retrieved successfully", cart));
    }


    // Remove
    @DeleteMapping("/remove")
    public ResponseEntity<?> remove(@RequestParam Long userId,
                                    @RequestParam Long productId) {

        cartService.removeItem(userId, productId);

        return ResponseEntity.ok(new ApiResponse<>(true, "Product removed from cart successfully"));
    }
}
