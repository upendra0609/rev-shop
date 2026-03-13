package com.revshop.service.seller;

import com.revshop.model.Order;
import com.revshop.model.Product;
import com.revshop.model.User;
import com.revshop.dto.OrderDTO;
import com.revshop.dto.ProductSalesDTO;
import com.revshop.dto.ProductOrderDetailDTO;
import com.revshop.dto.ProductSalesDataDTO;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class SellerService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    // Get All Orders (For Seller Dashboard)
    public List<Order> getOrders(Long sellerId) {
        return orderRepository.findByUserId(sellerId);
    }

    // ✅ NEW: Get All Products Added by Seller with Sales Information
    @Transactional
    public List<ProductSalesDTO> getSellerProductSales(Long sellerId) {
        // Verify seller exists
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + sellerId));

        System.out.println("\n=== SELLER PRODUCT SALES DASHBOARD ===");
        System.out.println("Seller ID: " + sellerId);
        System.out.println("Seller Name: " + seller.getName());

        // STEP 1: Fetch all products where seller_id = sellerId
        List<Product> products = productRepository.findAllBySeller_userId(sellerId);
        System.out.println("\n[STEP 1] Products fetched: " + products.size());
        products.forEach(p -> System.out.println("  - Product ID: " + p.getId() + ", Name: " + p.getName() + ", Seller ID: " + (p.getSeller() != null ? p.getSeller().getId() : "NULL")));

        if (products.isEmpty()) {
            System.out.println("[WARNING] No products found for seller " + sellerId);
            return new ArrayList<>();
        }

        // STEP 2: Fetch ALL orders (don't use custom query, just get all)
        List<Order> allOrders = orderRepository.findAll();
        System.out.println("\n[STEP 2] Total orders in system: " + allOrders.size());

        if (allOrders.isEmpty()) {
            System.out.println("[WARNING] No orders found in system");
            return products.stream()
                    .map(product -> buildProductSalesDTO(product, new ArrayList<>()))
                    .collect(Collectors.toList());
        }

        // STEP 3: Initialize ALL lazy-loaded data
        System.out.println("\n[STEP 3] Initializing lazy-loaded data...");
        allOrders.forEach(order -> {
            try {
                // Initialize User
                if (order.getUser() != null) {
                    order.getUser().getId();
                    order.getUser().getName();
                    order.getUser().getEmail();
                }

                // Initialize OrderItems and Products
                List<com.revshop.model.OrderItem> items = order.getItems();
                if (items != null) {
                    items.forEach(item -> {
                        if (item.getProduct() != null) {
                            item.getProduct().getId();
                            item.getProduct().getName();
                            item.getProduct().getPrice();
                            if (item.getProduct().getSeller() != null) {
                                item.getProduct().getSeller().getId();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("Error initializing order " + order.getId() + ": " + e.getMessage());
            }
        });
        System.out.println("Lazy-loading complete");

        // STEP 4: For each product, find matching orders and build DTO
        System.out.println("\n[STEP 4] Building ProductSalesDTO for each product...");
        List<ProductSalesDTO> results = new ArrayList<>();

        for (Product product : products) {
            System.out.println("\n  Processing Product: " + product.getId() + " (" + product.getName() + ")");

            // Find all orders that contain this product
            List<ProductOrderDetailDTO> productOrders = new ArrayList<>();

            for (Order order : allOrders) {
                List<com.revshop.model.OrderItem> orderItems = order.getItems();
                
                if (orderItems != null) {
                    for (com.revshop.model.OrderItem orderItem : orderItems) {
                        if (orderItem.getProduct() != null && 
                            orderItem.getProduct().getId().equals(product.getId())) {
                            
                            System.out.println("    ✓ Found in Order " + order.getId() + 
                                             " by " + order.getUser().getName() + 
                                             " (Qty: " + orderItem.getQuantity() + ")");

                            ProductOrderDetailDTO detail = new ProductOrderDetailDTO();
                            detail.setOrderId(order.getId());
                            detail.setCustomerId(order.getUser().getId());
                            detail.setCustomerName(order.getUser().getName());
                            detail.setCustomerEmail(order.getUser().getEmail());
                            detail.setQuantityOrdered(orderItem.getQuantity());
                            detail.setPricePerUnit(orderItem.getPrice());
                            detail.setTotalPrice(orderItem.getItemTotal());
                            detail.setOrderStatus(order.getStatus());
                            detail.setOrderDate(order.getCreatedAt());
                            detail.setPaymentMethod(order.getPaymentMethod());
                            
                            productOrders.add(detail);
                        }
                    }
                }
            }

            System.out.println("    Total Orders: " + productOrders.size());

            // Build and add DTO
            ProductSalesDTO salesDTO = buildProductSalesDTO(product, productOrders);
            results.add(salesDTO);
        }

        System.out.println("\n=== FINAL RESULT: " + results.size() + " products with sales info ===\n");
        return results;
    }

    // Helper method to build ProductSalesDTO
    private ProductSalesDTO buildProductSalesDTO(Product product, List<ProductOrderDetailDTO> productOrders) {
        int totalQuantityOrdered = productOrders.stream()
                .mapToInt(ProductOrderDetailDTO::getQuantityOrdered)
                .sum();
        
        double totalRevenue = productOrders.stream()
                .mapToDouble(ProductOrderDetailDTO::getTotalPrice)
                .sum();

        ProductSalesDTO salesDTO = new ProductSalesDTO();
        salesDTO.setProductId(product.getId());
        salesDTO.setProductName(product.getName());
        salesDTO.setProductDescription(product.getDescription());
        salesDTO.setProductPrice(product.getPrice());
        salesDTO.setCurrentStock(product.getQuantity());
        salesDTO.setTotalQuantityOrdered(totalQuantityOrdered);
        salesDTO.setTotalRevenue(totalRevenue);
        salesDTO.setTotalOrders(productOrders.size());
        salesDTO.setProductCreatedAt(product.getCreatedAt());
        salesDTO.setProductUpdatedAt(product.getUpdatedAt());
        salesDTO.setOrders(productOrders);

        return salesDTO;
    }

    // ✅ NEW: Get All Orders for Products Sold by a Seller
    @Transactional
    public List<OrderDTO> getOrdersForSellerProducts(Long sellerId) {
        // Verify seller exists
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + sellerId));

        // Fetch all orders containing products from this seller
        List<Order> orders = orderRepository.findOrdersBySellerProductsSorted(sellerId);

        // Initialize lazy-loaded entities within transaction
        orders.forEach(order -> {
            order.getUser().getId();
            order.getUser().getName();
            
            if (order.getAddress() != null) {
                order.getAddress().getLine1();
            }
            
            order.getItems().forEach(item -> {
                if (item.getProduct() != null) {
                    item.getProduct().getName();
                    item.getProduct().getSeller().getId();
                }
            });
        });

        // Convert to OrderDTO and return
        return orders.stream()
                .map(this::mapToOrderDTO)
                .collect(Collectors.toList());
    }

    // ✅ NEW: Get Orders for Seller Products filtered by seller ID (only their products in the order)
    @Transactional
    public List<OrderDTO> getSellerProductOrdersFiltered(Long sellerId) {
        // Verify seller exists
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + sellerId));

        // Fetch all orders
        List<Order> orders = orderRepository.findOrdersBySellerProductsSorted(sellerId);

        // Initialize lazy-loaded entities
        orders.forEach(order -> {
            order.getUser().getId();
            order.getUser().getName();
            
            if (order.getAddress() != null) {
                order.getAddress().getLine1();
            }
            
            order.getItems().forEach(item -> {
                if (item.getProduct() != null) {
                    item.getProduct().getName();
                    item.getProduct().getSeller().getId();
                }
            });
        });

        // Convert to OrderDTO and return
        return orders.stream()
                .map(this::mapToOrderDTO)
                .collect(Collectors.toList());
    }

    // Helper method to convert Order to OrderDTO
    private OrderDTO mapToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();

        dto.setOrderId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setUserId(order.getUser().getId());
        dto.setUserName(order.getUser().getName());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        if (order.getAddress() != null) {
            dto.setAddress(order.getAddress().toString());
        }

        if (order.getPaymentMethod() != null) {
            dto.setPaymentMethod(order.getPaymentMethod());
        }

        // Convert OrderItems to OrderItemDTOs
        List<com.revshop.dto.OrderItemDTO> orderItemDTOs = order.getItems().stream()
                .map(orderItem -> {
                    com.revshop.dto.OrderItemDTO itemDTO = new com.revshop.dto.OrderItemDTO();
                    itemDTO.setItemId(orderItem.getId());
                    itemDTO.setProductId(orderItem.getProduct().getId());
                    itemDTO.setProductName(orderItem.getProductName());
                    itemDTO.setProductDescription(orderItem.getProduct().getDescription());
                    itemDTO.setProductPrice(orderItem.getProduct().getPrice());
                    itemDTO.setQuantity(orderItem.getQuantity());
                    itemDTO.setPrice(orderItem.getPrice());
                    itemDTO.setItemTotal(orderItem.getItemTotal());
                    return itemDTO;
                })
                .collect(Collectors.toList());

        dto.setItems(orderItemDTOs);

        // Set total cost
        if (order.getTotalCost() > 0) {
            dto.setTotalCost(order.getTotalCost());
        } else {
            double total = order.getItems().stream()
                    .mapToDouble(it -> it.getPrice() * it.getQuantity())
                    .sum();
            dto.setTotalCost(total);
        }

        return dto;
    }

    // ✅ NEW: Get detailed product sales data using native SQL query
    // Returns all products with their sales information (who ordered, how much, when)
    @Transactional(readOnly = true)
    public List<ProductSalesDataDTO> getSellerProductSalesNative(Long sellerId) {
        // Verify seller exists
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + sellerId));

        System.out.println("\n=== FETCHING SELLER PRODUCT SALES DATA ===");
        System.out.println("Seller ID: " + sellerId);
        System.out.println("Seller Name: " + seller.getName());

        // Execute native query
        List<Object[]> rawResults = orderRepository.findSellerProductSalesData(sellerId);
        System.out.println("Raw query results: " + rawResults.size() + " rows");

        if (rawResults.isEmpty()) {
            System.out.println("[INFO] No products found for seller " + sellerId);
            return new ArrayList<>();
        }

        // Convert Object[] to ProductSalesDataDTO
        List<ProductSalesDataDTO> results = new ArrayList<>();
        for (Object[] row : rawResults) {
            ProductSalesDataDTO dto = new ProductSalesDataDTO();
            
            // Map columns from the SQL query
            // SELECT p.id, p.name, p.price, p.quantity, o.id, u.name, u.email, oi.quantity, o.created_at
            dto.setProductId(((Number) row[0]).longValue());                           // p.id
            dto.setProductName((String) row[1]);                                       // p.name
            dto.setPrice(((Number) row[2]).doubleValue());                             // p.price
            dto.setCurrentStock(((Number) row[3]).intValue());                         // p.quantity
            dto.setOrderId(row[4] != null ? ((Number) row[4]).longValue() : null);     // o.id
            dto.setCustomerName((String) row[5]);                                      // u.name
            dto.setCustomerEmail((String) row[6]);                                     // u.email
            dto.setQtyOrdered(row[7] != null ? ((Number) row[7]).intValue() : null);   // oi.quantity
            dto.setOrderDate((LocalDateTime) row[8]);                                  // o.created_at

            results.add(dto);
        }

        System.out.println("Converted to DTOs: " + results.size() + " records");
        System.out.println("=== COMPLETE ===\n");

        return results;
    }

    // ✅ NEW: Get grouped product sales data (aggregated by product)
    // Returns products with aggregated sales metrics
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSellerProductSalesSummary(Long sellerId) {
        // Verify seller exists
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + sellerId));

        System.out.println("\n=== FETCHING SELLER PRODUCT SALES SUMMARY ===");
        System.out.println("Seller ID: " + sellerId);

        // Get raw data first
        List<ProductSalesDataDTO> salesData = getSellerProductSalesNative(sellerId);

        // Group by product and aggregate
        Map<Long, Map<String, Object>> groupedData = new HashMap<>();

        for (ProductSalesDataDTO row : salesData) {
            Long productId = row.getProductId();

            if (!groupedData.containsKey(productId)) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("productId", row.getProductId());
                productData.put("productName", row.getProductName());
                productData.put("price", row.getPrice());
                productData.put("currentStock", row.getCurrentStock());
                productData.put("totalQuantityOrdered", 0);
                productData.put("totalOrders", 0);
                productData.put("orders", new ArrayList<Map<String, Object>>());
                groupedData.put(productId, productData);
            }

            Map<String, Object> productData = groupedData.get(productId);

            // Add order details if order exists
            if (row.getOrderId() != null) {
                int totalQty = (Integer) productData.get("totalQuantityOrdered");
                productData.put("totalQuantityOrdered", totalQty + (row.getQtyOrdered() != null ? row.getQtyOrdered() : 0));

                int totalOrders = (Integer) productData.get("totalOrders");
                productData.put("totalOrders", totalOrders + 1);

                Map<String, Object> orderDetail = new HashMap<>();
                orderDetail.put("orderId", row.getOrderId());
                orderDetail.put("customerName", row.getCustomerName());
                orderDetail.put("customerEmail", row.getCustomerEmail());
                orderDetail.put("qtyOrdered", row.getQtyOrdered());
                orderDetail.put("orderDate", row.getOrderDate());

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> orders = (List<Map<String, Object>>) productData.get("orders");
                orders.add(orderDetail);
            }
        }

        System.out.println("Grouped into " + groupedData.size() + " unique products");
        System.out.println("=== COMPLETE ===\n");

        return new ArrayList<>(groupedData.values());
    }
}
