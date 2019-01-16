package com.dpdocter.beans;

import org.bson.types.ObjectId;

public class OrganizingCommittee {
	private ObjectId id;
	private String role;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
