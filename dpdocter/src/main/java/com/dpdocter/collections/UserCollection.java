package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DOB;

@Document(collection = "user_cl")
public class UserCollection {

	@Id
	private String id;
	@Field
	private String firstName;
	@Field
	private String lastName;
	@Field
	private String middleName;
	@Indexed(unique = true)
	private String userName;
	@Field
	private String password;
	@Field
	private String emailAddress;
	@Field
	private String phoneNumber;
	@Field
	private String secPhoneNumber;
	@Field
	private String gender;

	@Field
	private String imageUrl;
	@Field
	private DOB dob;

	@Field
	private Boolean isActive = false;

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

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
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
	

	public String getSecPhoneNumber() {
		return secPhoneNumber;
	}

	public void setSecPhoneNumber(String secPhoneNumber) {
		this.secPhoneNumber = secPhoneNumber;
	}

	@Override
	public String toString() {
		return "UserCollection [id=" + id + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", middleName=" + middleName
				+ ", userName=" + userName + ", password=" + password
				+ ", emailAddress=" + emailAddress + ", phoneNumber="
				+ phoneNumber + ", secPhoneNumber=" + secPhoneNumber
				+ ", gender=" + gender + ", imageUrl=" + imageUrl + ", dob="
				+ dob + ", isActive=" + isActive + "]";
	}

	

}
