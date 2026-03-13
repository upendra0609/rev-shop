package com.revshop.service.buyer;

import com.revshop.model.User;
import com.revshop.model.Address;
import com.revshop.dto.AddressDTO;
import com.revshop.dto.UpdateProfileDTO;
import com.revshop.repository.UserRepository;
import com.revshop.repository.AddressRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuyerService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AddressRepository addressRepository;

	public User getBuyerById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Buyer not found"));
		
	}

	public Address saveAddress(Long userId, AddressDTO addressDTO) {
		// Fetch the user
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		// Create a new Address entity from DTO
		Address address = new Address();
		address.setLine1(addressDTO.getLine1());
		address.setLine2(addressDTO.getLine2());
		address.setCity(addressDTO.getCity());
		address.setState(addressDTO.getState());
		address.setPostalCode(addressDTO.getPostalCode());
		address.setCountry(addressDTO.getCountry());
		address.setPhoneNumber(addressDTO.getPhoneNumber());
		address.setAddressType(addressDTO.getAddressType());
		address.setDefault(false); // Default is false, can be set to true separately
		address.setUser(user);

		// Save and return
		return addressRepository.save(address);
	}

	@Transactional
	public User updateProfile(Long userId, UpdateProfileDTO updateProfileDTO) {
		// Fetch the user
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		// Update user fields
		if (updateProfileDTO.getName() != null && !updateProfileDTO.getName().isEmpty()) {
			user.setName(updateProfileDTO.getName());
		}

		if (updateProfileDTO.getEmail() != null && !updateProfileDTO.getEmail().isEmpty()) {
			user.setEmail(updateProfileDTO.getEmail());
		}

		// Save and return updated user
		return userRepository.save(user);
	}
}
