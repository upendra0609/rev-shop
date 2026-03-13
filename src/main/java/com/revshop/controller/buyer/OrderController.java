package com.revshop.controller.buyer;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.OrderDTO;
import com.revshop.dto.PlaceOrderRequest;
import com.revshop.model.Address;
import com.revshop.service.buyer.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    // Place Order - Accepts order details from UI (address, payment method, items)
    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody PlaceOrderRequest request) {
        
        if (request == null || request.getUserId() == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "User ID is required"));
        }

        if (request.getAddressId() == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Address ID is required"));
        }

        if (request.getPaymentMethod() == null || request.getPaymentMethod().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Payment method is required (CASH, CARD, or UPI)"));
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Order must contain at least one item"));
        }

        try {
            OrderDTO dto = orderService.placeOrder(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Order placed successfully", dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }


    // Order History
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> history(@PathVariable Long userId) {
        List<OrderDTO> orders = orderService.getOrders(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order history retrieved successfully", orders));
    }

    // Get order detail by userId and orderId
    @GetMapping("/{userId}/orders/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long userId, @PathVariable Long orderId) {
        try {
            OrderDTO orderDTO = orderService.getOrderByUserIdAndOrderId(userId, orderId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Order details retrieved successfully", orderDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    // Add or update address for an order
    @PostMapping("/{orderId}/address")
    public ResponseEntity<?> addAddress(@PathVariable Long orderId, @RequestBody Address address) {
        OrderDTO dto = orderService.addAddressToOrder(orderId, address);
        return ResponseEntity.ok(new ApiResponse<>(true, "Address added to order successfully", dto));
    }

    // Add or update payment method for an order
    @PostMapping("/{orderId}/payment")
    public ResponseEntity<?> setPaymentMethod(@PathVariable Long orderId, @RequestParam String paymentMethod) {
        OrderDTO dto = orderService.setPaymentMethod(orderId, paymentMethod);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment method set successfully", dto));
    }

    // Get saved addresses for user
    @GetMapping("/addresses/{userId}")
    public ResponseEntity<?> addresses(@PathVariable Long userId) {
        List<Address> addressList = orderService.getAddressesForUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Addresses retrieved successfully", addressList));
    }
}