package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="docter_cl")
public class DoctorCollection {
	@Id
	private String id;
	
	@Field
	private String doctorImageUrl;
	
	@Field
	private String specialization;
	
	@Field
	private String userId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	


	public String getDoctorImageUrl() {
		return doctorImageUrl;
	}

	public void setDoctorImageUrl(String doctorImageUrl) {
		this.doctorImageUrl = doctorImageUrl;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}
	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "DoctorCollection [id=" + id + ", doctorImageUrl="
				+ doctorImageUrl + ", specialization=" + specialization
				+ ", userId=" + userId + "]";
	}

	

	
}
