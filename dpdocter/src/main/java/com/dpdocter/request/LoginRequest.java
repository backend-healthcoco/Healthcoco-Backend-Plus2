package com.dpdocter.request;

import java.util.Arrays;

public class LoginRequest {

	private String username;

	private char[] password;

	private String locationId;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	@Override
	public String toString() {
		return "LoginRequest [username=" + username + ", password=" + Arrays.toString(password) + ", locationId="
				+ locationId + "]";
	}
}
