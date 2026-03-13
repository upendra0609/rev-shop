package com.revshop.service.auth;

import com.revshop.dto.LoginResponse;
import com.revshop.dto.RegisterResponse;
import com.revshop.model.*;

import com.revshop.repository.UserRepository;

import com.revshop.security.JwtUtil;
import com.revshop.service.security.TokenBlacklistService;

import java.awt.image.RescaleOp;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

//register
    public RegisterResponse register(User user) {

        user.setPassword(encoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);  // ✅ FIX: Use Role enum instead of "USER"
        }

        User saved = userRepository.save(user);

        // ✅ NEW: Generate JWT token immediately after registration
        String token = jwtUtil.generateToken(saved.getEmail());

        RegisterResponse response = new RegisterResponse();
        response.setMessage("Registered Successfully");
        response.setEmail(saved.getEmail());
        response.setToken(token);           // ✅ NEW: Include token in response
        response.setUserId(saved.getId());  // ✅ NEW: Include user ID
        response.setRole(saved.getRole());  // ✅ NEW: Include role

        return response;
    }


    // Login
    public LoginResponse login(String email, String password) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid Email"));

        if (!encoder.matches(password, user.getPassword())) {

            throw new RuntimeException("Invalid Password");
        }

        String token = jwtUtil.generateToken(email);
        
        LoginResponse response = new LoginResponse();
        response.setRole(user.getRole());
        response.setToken(token);
        response.setEmail(email);
        response.setUserId(user.getId());
        response.setName(user.getName());
        return response;
    }
    
    
    
       // Logout
        @Autowired
        private TokenBlacklistService tokenBlacklistService;

        public void logout(String token) {
            tokenBlacklistService.blacklist(token);
        }
}
