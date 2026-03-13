package com.revshop.controller.buyer;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.AddressDTO;
import com.revshop.dto.UpdateProfileDTO;
import com.revshop.model.User;
import com.revshop.model.Address;
import com.revshop.service.buyer.BuyerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer")
public class BuyerController {

    @Autowired
    private BuyerService buyerService;


    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        User user = buyerService.getBuyerById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Buyer profile retrieved successfully", user));
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody UpdateProfileDTO updateProfileDTO) {
        try {
            User updatedUser = buyerService.updateProfile(id, updateProfileDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @PostMapping("/addresses/{userId}")
    public ResponseEntity<?> saveAddress(@PathVariable Long userId, @RequestBody AddressDTO addressDTO) {
        Address savedAddress = buyerService.saveAddress(userId, addressDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Address saved successfully", savedAddress));
    }
    
}
