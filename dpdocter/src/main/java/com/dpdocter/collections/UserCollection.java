package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "user_cl")
public class UserCollection {

	@Id
	private String id;
/*	@Field
	private String firstName;
	@Field
	private String lastName;
	@Field
	private String middleName;*/
	@Indexed(unique = true)
	private String userName;
	@Field
	private String password;
/*	@Field
	private String emailAddress;*/
	@Field
	private String mobileNumber;
/*	@Field
	private String secPhoneNumber;*/
	@Field
	private String gender;
/*
	@Field
	private String imageUrl;
	@Field
	private DOB dob;*/

	@Field
	private Boolean isActive = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "UserCollection [id=" + id + ", userName=" + userName
				+ ", password=" + password + ", mobileNumber=" + mobileNumber
				+ ", gender=" + gender + ", isActive=" + isActive + "]";
	}

	
	

}
