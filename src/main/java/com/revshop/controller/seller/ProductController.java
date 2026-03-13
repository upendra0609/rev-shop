package com.revshop.controller.seller;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.CreateProductDTO;
import com.revshop.dto.UpdateProductDTO;
import com.revshop.model.Product;
import com.revshop.model.User;
import com.revshop.repository.UserRepository;
import com.revshop.service.seller.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;  // ✅ ADD THIS


    // Add Product
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody Product product,
    		@RequestParam Long userId) {
        
        // ✅ FIX: Get the seller from database instead of using the transient object
        if (product.getSeller() != null && product.getSeller().getId() != null) {
            User seller = userRepository.findById(product.getSeller().getId())
                    .orElseThrow(() -> new RuntimeException("Seller not found"));
            product.setSeller(seller);  // ✅ Use the managed entity from DB
        } else {
            throw new RuntimeException("Seller ID is required");
        }

        Product savedProduct = productService.addProduct(product);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product added successfully", savedProduct));
    }

    // ✅ NEW: Add Product from DTO
    @PostMapping("/add-product")
    public ResponseEntity<?> addProductFromDTO(@RequestBody CreateProductDTO createProductDTO) {
        try {
            // Validate required fields
            if (createProductDTO.getName() == null || createProductDTO.getName().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Product name is required"));
            }

            if (createProductDTO.getPrice() <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Product price must be greater than 0"));
            }

            if (createProductDTO.getStock() < 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Stock quantity cannot be negative"));
            }

            if (createProductDTO.getUserId() == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "User ID is required"));
            }

            Product savedProduct = productService.createProductFromDTO(createProductDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Product added successfully", savedProduct));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    // View All
    @GetMapping("/all/{userId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> all(@PathVariable Long userId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Products retrieved successfully", productService.getAllByUserId(userId)));
    }
    
    
    // View All
    @GetMapping("/all")
    @Transactional(readOnly = true)
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Products retrieved successfully", productService.getAll()));
    }


    // Update
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody Product product) {
        Product updatedProduct = productService.update(id, product);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product updated successfully", updatedProduct));
    }

    // ✅ NEW: Update Product from DTO
    @PutMapping("/update-product/{id}")
    public ResponseEntity<?> updateProductFromDTO(@PathVariable Long id, @RequestBody UpdateProductDTO updateProductDTO) {
        try {
            // Validate at least one field is provided
            if ((updateProductDTO.getName() == null || updateProductDTO.getName().isEmpty()) &&
                (updateProductDTO.getDescription() == null || updateProductDTO.getDescription().isEmpty()) &&
                updateProductDTO.getPrice() <= 0 &&
                updateProductDTO.getStock() < 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "At least one field must be provided for update"));
            }

            // Validate individual fields if provided
            if (updateProductDTO.getName() != null && !updateProductDTO.getName().isEmpty()) {
                if (updateProductDTO.getName().length() < 2) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse<>(false, "Product name must be at least 2 characters long"));
                }
            }

            if (updateProductDTO.getPrice() > 0 && updateProductDTO.getPrice() <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Product price must be greater than 0"));
            }

            if (updateProductDTO.getStock() >= 0 && updateProductDTO.getStock() < 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Stock quantity cannot be negative"));
            }

            Product updatedProduct = productService.updateProductFromDTO(id, updateProductDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Product updated successfully", updatedProduct));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }


    // Delete
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product deleted successfully"));
    }
}
