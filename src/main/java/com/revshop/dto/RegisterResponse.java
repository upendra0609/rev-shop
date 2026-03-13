package com.revshop.dto;

import com.revshop.model.Role;

public class RegisterResponse {

	private String message;
	private String email;
	private String token;        // ✅ NEW: JWT Token
	private Long userId;         // ✅ NEW: User ID
	private Role role;           // ✅ NEW: User Role

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}