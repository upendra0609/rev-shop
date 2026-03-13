package com.revshop.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.LoginDTO;
import com.revshop.dto.LoginResponse;
import com.revshop.dto.RegisterDTO;
import com.revshop.dto.RegisterResponse;
import com.revshop.model.Role;
import com.revshop.model.User;
import com.revshop.service.auth.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    // Register - Accept role parameter from DTO
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterDTO dto) {

        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        
        // ✅ Accept role from DTO, default to CUSTOMER if not provided
        String roleStr = dto.getRole();
        if (roleStr != null && !roleStr.isEmpty()) {
            try {
                // Allow CUSTOMER or SELLER registration only
                if (roleStr.equalsIgnoreCase("SELLER")) {
                    user.setRole(Role.SELLER);
                } else if (roleStr.equalsIgnoreCase("CUSTOMER")) {
                    user.setRole(Role.CUSTOMER);
                } else {
                    user.setRole(Role.CUSTOMER);  // Default to CUSTOMER
                }
            } catch (Exception e) {
                user.setRole(Role.CUSTOMER);  // Default to CUSTOMER if invalid
            }
        } else {
            user.setRole(Role.CUSTOMER);  // Default to CUSTOMER
        }

        RegisterResponse response =   authService.register(user);

        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", response));
    }
    
    // ✅ NEW: Register as Seller
    @PostMapping("/register/seller")
    public ResponseEntity<?> registerSeller(
            @RequestBody RegisterDTO dto) {

        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(Role.SELLER);

        RegisterResponse response =   authService.register(user);

        return ResponseEntity.ok(new ApiResponse<>(true, "Seller registered successfully", response));
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginDTO dto) {

        LoginResponse token =
                authService.login(
                        dto.getEmail(),
                        dto.getPassword()
                );

        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", token));
    }
    
    // Logout
    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER')")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);
            authService.logout(token);
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Logged out successfully"));
    }

}
