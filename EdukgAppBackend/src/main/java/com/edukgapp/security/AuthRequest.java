package com.edukgapp.security;

public class AuthRequest {
	private String username;
	private String password;

	public AuthRequest() {}
	public AuthRequest(final String username, final String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
