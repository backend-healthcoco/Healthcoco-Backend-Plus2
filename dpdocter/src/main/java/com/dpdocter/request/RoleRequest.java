package com.dpdocter.request;

public class RoleRequest {

	private String id;

	private String role;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "RoleRequest [id=" + id + ", role=" + role + "]";
	}
}
