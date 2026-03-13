package com.revshop.service.buyer;

import com.revshop.dto.OrderDTO;
import com.revshop.dto.OrderItemDTO;
import com.revshop.dto.PlaceOrderRequest;
import com.revshop.model.Address;
import com.revshop.model.Order;
import com.revshop.model.OrderItem;
import com.revshop.model.Product;
import com.revshop.model.User;
import com.revshop.repository.AddressRepository;
import com.revshop.repository.CartRepository;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.UserRepository;
import com.revshop.model.Cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;


    // Place Order - NEW METHOD with order details from UI
    public OrderDTO placeOrder(PlaceOrderRequest request) {
        
        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate address exists
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Validate payment method
        if (!request.getPaymentMethod().equals("CASH") && 
            !request.getPaymentMethod().equals("CARD") && 
            !request.getPaymentMethod().equals("UPI")) {
            throw new RuntimeException("Invalid payment method. Must be CASH, CARD, or UPI");
        }

        // Create new order
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus("PLACED");

        // Calculate total cost and add items
        double totalCost = 0;
        
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (PlaceOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
                
                // Validate product exists
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + itemRequest.getProductId()));

                // Create order item
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setPrice(itemRequest.getPrice()); // Current product price
                orderItem.setProductName(product.getName()); // Snapshot of product name
                orderItem.setItemTotal(itemRequest.getPrice() * itemRequest.getQuantity());

                order.getItems().add(orderItem);
                totalCost += orderItem.getItemTotal();
            }
        } else {
            throw new RuntimeException("Order must contain at least one item");
        }

        // Set total cost
        order.setTotalCost(totalCost);

        // Save order
        order = orderRepository.save(order);

        // Clear cart after successful order placement
        Cart userCart = cartRepository.findByUser(user).orElse(null);
        if (userCart != null) {
            userCart.getItems().clear();
            cartRepository.save(userCart);
        }

        return mapToDTO(order);
    }

    // OLD METHOD - for backward compatibility (if called without request body)
    public OrderDTO placeOrder(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PLACED");

        order = orderRepository.save(order);

        return mapToDTO(order);
    }


    // Add / Update Address for an existing Order
    public OrderDTO addAddressToOrder(Long orderId, Address address) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // if address has user set, ensure it matches order user
        if (address.getUser() == null) {
            address.setUser(order.getUser());
        }

        Address saved = addressRepository.save(address);

        order.setAddress(saved);
        order = orderRepository.save(order);

        return mapToDTO(order);
    }

    // Add / Update Payment Method for an existing Order
    public OrderDTO setPaymentMethod(Long orderId, String paymentMethod) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Validate payment method
        if (!paymentMethod.equals("CASH") && !paymentMethod.equals("CARD") && !paymentMethod.equals("UPI")) {
            throw new RuntimeException("Invalid payment method. Must be CASH, CARD, or UPI");
        }

        order.setPaymentMethod(paymentMethod);
        order = orderRepository.save(order);

        return mapToDTO(order);
    }

    // Get saved addresses for a user
    public List<Address> getAddressesForUser(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    // Get Order History for a user
    @Transactional
    public List<OrderDTO> getOrders(Long userId) {

        List<Order> orders = orderRepository.findByUserId(userId);

        // Initialize lazy-loaded User and Address entities within transaction
        orders.forEach(order -> {
            // Force initialization of User
            order.getUser().getId();
            order.getUser().getName();
            
            // Force initialization of Address if present
            if (order.getAddress() != null) {
                order.getAddress().getLine1();
            }
            
            // Force initialization of OrderItems and their Products
            order.getItems().forEach(item -> {
                if (item.getProduct() != null) {
                    item.getProduct().getName();
                }
            });
        });

        return orders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get all orders (for admin)
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Get order details by userId and orderId
    @Transactional
    public OrderDTO getOrderByUserIdAndOrderId(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found for user: " + userId + " with order ID: " + orderId));

        // Initialize lazy-loaded entities within transaction
        order.getUser().getId();
        order.getUser().getName();
        
        if (order.getAddress() != null) {
            order.getAddress().getLine1();
        }
        
        order.getItems().forEach(item -> {
            if (item.getProduct() != null) {
                item.getProduct().getName();
            }
        });

        return mapToDTO(order);
    }

    // Convert Entity → DTO
    private OrderDTO mapToDTO(Order order) {

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

        // items - Convert OrderItem to OrderItemDTO with complete product information
        List<OrderItemDTO> orderItemDTOs = order.getItems().stream().map((OrderItem orderItem) -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setItemId(orderItem.getId());
            itemDTO.setProductId(orderItem.getProduct().getId());
            itemDTO.setProductName(orderItem.getProductName());
            itemDTO.setProductDescription(orderItem.getProduct().getDescription());
            itemDTO.setProductPrice(orderItem.getProduct().getPrice());
            itemDTO.setQuantity(orderItem.getQuantity());
            itemDTO.setPrice(orderItem.getPrice()); // Price at time of order
            itemDTO.setItemTotal(orderItem.getItemTotal());
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItems(orderItemDTOs);

        // total cost: prefer stored totalCost else compute
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
}