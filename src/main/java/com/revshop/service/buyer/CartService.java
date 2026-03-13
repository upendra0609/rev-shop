package com.revshop.service.buyer;

import com.revshop.model.Cart;
import com.revshop.model.CartItem;
import com.revshop.model.Product;
import com.revshop.model.User;
import com.revshop.repository.CartRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    // Add To Cart
    public void addToCart(Long userId, Long productId, int qty) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));


        Cart cart = cartRepository.findByUser(user)
                .orElse(new Cart(user));
        
        System.out.println(cart);


        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst();


        if (existingItem.isPresent()) {

            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + qty);

        } else {

            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(qty);

            cart.getItems().add(item);
        }
        
        cart.setUser(user);
        

      cartRepository.save(cart);
    }


    // View Cart
    public Cart viewCart(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Optional<Cart> op = cartRepository.findByUser(user);
        
        System.out.println(op.get().getItems().size());

        return cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }


    // Remove Item - Decrease quantity or remove if quantity is 1
    public void removeItem(Long userId, Long productId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Find the cart item with the matching product ID
        Optional<CartItem> itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (itemToUpdate.isPresent()) {
            CartItem item = itemToUpdate.get();
            
            // If quantity is 1, remove the item from cart
            if (item.getQuantity() == 1) {
                cart.getItems().remove(item);
            } else {
                // Otherwise, decrease quantity by 1
                item.setQuantity(item.getQuantity() - 1);
            }
        }
        
        // Log remaining items in cart
        cart.getItems().forEach(i -> System.out.println(i.getProduct().getId() + " - " + i.getQuantity()));

        cartRepository.save(cart);
    }
}
