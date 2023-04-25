package com.dpdocter.beans;

import org.bson.types.ObjectId;

public class OrganizingCommittee {
	private ObjectId speakerId;
	private String role;

	public ObjectId getSpeakerId() {
		return speakerId;
	}

	public void setSpeakerId(ObjectId speakerId) {
		this.speakerId = speakerId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
