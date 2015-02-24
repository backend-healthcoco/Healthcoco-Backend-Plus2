package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DOB;

@Document(collection="docter_cl")
public class DoctorCollection {
	@Id
	private String id;
	
	@Field
	private String firstName;
	@Field
	private String lastName;
	@Field
	private String middleName;
	@Field
	private String emailAddress;
	@Field
	private String secMobile;
	@Field
	private String imageUrl;
	@Field
	private DOB dob;
	
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getSecMobile() {
		return secMobile;
	}

	public void setSecMobile(String secMobile) {
		this.secMobile = secMobile;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
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
		return "DoctorCollection [id=" + id + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", middleName=" + middleName
				+ ", emailAddress=" + emailAddress + ", secMobile=" + secMobile
				+ ", imageUrl=" + imageUrl + ", dob=" + dob
				+ ", specialization=" + specialization + ", userId=" + userId
				+ "]";
	}

	
	
}
