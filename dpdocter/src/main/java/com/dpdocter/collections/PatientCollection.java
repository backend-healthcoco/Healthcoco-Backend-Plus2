package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="patient_cl")
public class PatientCollection {

	@Id
	private String id;
	
	@Field
	private String bloodGroup;
	
	@Field
	private String imageUrl;
	
	@Field
	private String userId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "PatientCollection [id=" + id + ", bloodGroup=" + bloodGroup
				+ ", imageUrl=" + imageUrl + ", userId=" + userId + "]";
	}

	
	
}
