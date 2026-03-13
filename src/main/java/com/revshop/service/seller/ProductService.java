package com.revshop.service.seller;

import com.revshop.model.CartItem;
import com.revshop.model.Product;
import com.revshop.model.User;
import com.revshop.dto.CreateProductDTO;
import com.revshop.dto.UpdateProductDTO;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // Add Product (with seller/admin info)
    public Product addProduct(Product product, User seller) {
        product.setSeller(seller);  // ✅ Set who is creating this product
        return productRepository.save(product);
    }

    // Add Product (without seller info - for backward compatibility)
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // ✅ NEW: Add Product from CreateProductDTO
    public Product createProductFromDTO(CreateProductDTO createProductDTO) {
        // Validate user exists
        User seller = userRepository.findById(createProductDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + createProductDTO.getUserId()));

        // Create new Product entity from DTO
        Product product = new Product();
        product.setName(createProductDTO.getName());
        product.setDescription(createProductDTO.getDescription());
        product.setPrice(createProductDTO.getPrice());
        product.setQuantity(createProductDTO.getStock());
        product.setSeller(seller);

        // Save and return
        return productRepository.save(product);
    }

    // Get All Products (for public view)
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    // ✅ NEW: Get Products by Current User (Seller/Admin)
    public List<Product> getProductsByUser(User user) {
        return productRepository.findBySeller(user);
    }

    // Update Product
    public Product update(Long id, Product newProduct) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(newProduct.getName());
        product.setPrice(newProduct.getPrice());
        product.setDescription(newProduct.getDescription());
        product.setQuantity(newProduct.getQuantity());

        return productRepository.save(product);
    }

    // ✅ NEW: Update Product from UpdateProductDTO
    public Product updateProductFromDTO(Long id, UpdateProductDTO updateProductDTO) {
        // Fetch the product
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Update fields from DTO if provided
        if (updateProductDTO.getName() != null && !updateProductDTO.getName().isEmpty()) {
            product.setName(updateProductDTO.getName());
        }

        if (updateProductDTO.getDescription() != null && !updateProductDTO.getDescription().isEmpty()) {
            product.setDescription(updateProductDTO.getDescription());
        }

        if (updateProductDTO.getPrice() > 0) {
            product.setPrice(updateProductDTO.getPrice());
        }

        if (updateProductDTO.getStock() >= 0) {
            product.setQuantity(updateProductDTO.getStock());
        }

        // Save and return updated product
        return productRepository.save(product);
    }

    // ✅ NEW: Update Product (with seller verification)
    public Product update(Long id, Product newProduct, User currentUser) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Verify that current user owns this product
        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own products");
        }

        product.setName(newProduct.getName());
        product.setPrice(newProduct.getPrice());
        product.setDescription(newProduct.getDescription());

        return productRepository.save(product);
    }

    // Delete Product
    public void delete(Long id) {
    	
    	Product product =  productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found"));
    	
        productRepository.deleteById(id);
    }

    // ✅ NEW: Delete Product (with owner verification)
    public void delete(Long id, User currentUser) {
    	
    	Product product =  productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found"));

        // Verify that current user owns this product
        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own products");
        }
    	
        productRepository.deleteById(id);
    }
    
 // Get All Products (for public view)
    public List<Product> getAllByUserId(Long userId) {
        return productRepository.findAllBySeller_userId(userId);
    }
}
