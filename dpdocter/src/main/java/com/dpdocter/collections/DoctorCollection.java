package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "docter_cl")
public class DoctorCollection {
	@Id
	private String id;

	@Field
	private String secMobile;

	@Field
	private String specialization;

	@Field
	private String userId;

	@Field
	private String patientInitial = "P";

	@Field
	private int patientCounter = 0;

	/*@Field
	private String firstName;
	
	@Field
	private String lastName;
	
	@Field
	private String middleName;
	
	@Field
	private String emailAddress;
	
	@Field
	private String imageUrl;
	
	@Field
	private DOB dob;*/

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecMobile() {
		return secMobile;
	}

	public void setSecMobile(String secMobile) {
		this.secMobile = secMobile;
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

	public String getPatientInitial() {
		return patientInitial;
	}

	public void setPatientInitial(String patientInitial) {
		this.patientInitial = patientInitial;
	}

	public int getPatientCounter() {
		return patientCounter;
	}

	public void setPatientCounter(int patientCounter) {
		this.patientCounter = patientCounter;
	}

	@Override
	public String toString() {
		return "DoctorCollection [id=" + id + ", secMobile=" + secMobile + ", specialization=" + specialization + ", userId=" + userId + ", patientInitial="
				+ patientInitial + ", patientCounter=" + patientCounter + "]";
	}

}
