package com.dpdocter.beans;

public class OAuth2SimpleGrantedAuthority {
	
	private String role;

	
	public OAuth2SimpleGrantedAuthority() {
		super();
	}

	public OAuth2SimpleGrantedAuthority(String role) {
		this.role = role;
	}

	public String getAuthority() {
		return role;
	}

	public void setAuthority(String role) {
		this.role = role;
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "OAuth2SimpleGrantedAuthority [role=" + role + "]";
	}
}