package com.revshop.controller.seller;

import com.revshop.dto.ApiResponse;
import com.revshop.service.seller.SellerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;


    // View Seller Orders
//    @GetMapping("/orders/{sellerId}")
//    public ResponseEntity<?> viewOrders(@PathVariable Long sellerId) {
//        return ResponseEntity.ok(new ApiResponse<>(true, "Seller orders retrieved successfully", sellerService.getOrders(sellerId)));
//    }

//    // ✅ NEW: Get All Orders for Seller's Products
//    @GetMapping("/product-orders/{sellerId}")
//    public ResponseEntity<?> getSellerProductOrders(@PathVariable Long sellerId) {
//        try {
//            return ResponseEntity.ok(new ApiResponse<>(true, "Orders for seller's products retrieved successfully", 
//                    sellerService.getOrdersForSellerProducts(sellerId)));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse<>(false, e.getMessage()));
//        }
//    }

//    // ✅ NEW: Get Orders Filtered by Seller's Products Only
//    @GetMapping("/product-orders/{sellerId}")
//    public ResponseEntity<?> getSellerProductOrdersFiltered(@PathVariable Long sellerId) {
//        try {
//            return ResponseEntity.ok(new ApiResponse<>(true, "Filtered orders for seller's products retrieved successfully", 
//                    sellerService.getSellerProductOrdersFiltered(sellerId)));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse<>(false, e.getMessage()));
//        }
//    }

    // ✅ NEW: Get All Products Added by Seller with Sales Information
    @GetMapping("/product-orders/{sellerId}")
    public ResponseEntity<?> getSellerProductSales(@PathVariable Long sellerId) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(true, "Seller product sales retrieved successfully", 
                    sellerService.getSellerProductSales(sellerId)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    // ✅ NEW: Get detailed product sales data (native SQL query)
    // Returns each product with all its orders and customer details
    @GetMapping("/product-sales-detailed/{sellerId}")
    public ResponseEntity<?> getSellerProductSalesDetailed(@PathVariable Long sellerId) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(true, "Seller product sales data retrieved successfully", 
                    sellerService.getSellerProductSalesNative(sellerId)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    // ✅ NEW: Get product sales summary (grouped and aggregated by product)
    // Returns products with total quantity ordered, total orders, and detailed order list
    @GetMapping("/orders/{sellerId}")
    public ResponseEntity<?> getSellerProductSalesSummary(@PathVariable Long sellerId) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(true, "Seller product sales summary retrieved successfully", 
                    sellerService.getSellerProductSalesSummary(sellerId)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }
}
