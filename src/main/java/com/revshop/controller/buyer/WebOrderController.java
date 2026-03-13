package com.revshop.controller.buyer;

import com.revshop.dto.OrderDTO;
import com.revshop.model.Address;
import com.revshop.service.buyer.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class WebOrderController {

    @Autowired
    private OrderService orderService;

    // Render buyer order history page
    @GetMapping("/buyer/orders")
    public String orders(Model model) {
        // In a real app, you would get authenticated user id; here we assume userId=1 for demo or use query param
        // To keep it generic, expect client to pass userId as query param ?userId=1; if missing, use 1
        model.addAttribute("title", "My Orders");
        return "buyer/orders";
    }

    // Render list with orders for a user
    @GetMapping("/buyer/orders/{userId}/list")
    public String ordersList(@PathVariable Long userId, Model model) {
        List<OrderDTO> orders = orderService.getOrders(userId);
        model.addAttribute("orders", orders);
        model.addAttribute("title", "My Orders");
        model.addAttribute("userId", userId);
        return "buyer/orders";
    }

    // Handle address form POST from buyer UI
    @PostMapping("/buyer/orders/{userId}/addAddress/{orderId}")
    public String addAddressFromForm(@PathVariable Long userId,
                                     @PathVariable Long orderId,
                                     @RequestParam String line1,
                                     @RequestParam(required = false) String line2,
                                     @RequestParam String city,
                                     @RequestParam(required = false) String state,
                                     @RequestParam(required = false) String postalCode,
                                     @RequestParam(required = false) String country) {

        Address a = new Address();
        a.setLine1(line1);
        a.setLine2(line2);
        a.setCity(city);
        a.setState(state);
        a.setPostalCode(postalCode);
        a.setCountry(country);

        orderService.addAddressToOrder(orderId, a);

        return "redirect:/buyer/orders/" + userId + "/list";
    }

    // Handle payment method form POST from buyer UI
    @PostMapping("/buyer/orders/{userId}/setPayment/{orderId}")
    public String setPaymentMethod(@PathVariable Long userId,
                                   @PathVariable Long orderId,
                                   @RequestParam String paymentMethod) {

        orderService.setPaymentMethod(orderId, paymentMethod);

        return "redirect:/buyer/orders/" + userId + "/list";
    }
}