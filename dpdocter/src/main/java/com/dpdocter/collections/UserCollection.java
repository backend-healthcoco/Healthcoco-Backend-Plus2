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

	@Indexed(unique = true)
	private String emailAddress;

	@Field
	private String mobileNumber;

	/*@Field
	private String secPhoneNumber;*/

	@Field
	private String gender;

	@Field
	private String imageUrl;

	@Field
	private DOB dob;

	@Field
	private Boolean isActive = false;

	@Field
	private Boolean isTempPassword = true;

	public UserCollection() {
		super();
	}

	public UserCollection(String id, String firstName, String lastName, String middleName, String userName, String password, String emailAddress,
			String mobileNumber, String gender, String imageUrl, DOB dob, Boolean isActive) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
		this.userName = userName;
		this.password = password;
		this.emailAddress = emailAddress;
		this.mobileNumber = mobileNumber;
		this.gender = gender;
		this.imageUrl = imageUrl;
		this.dob = dob;
		this.isActive = isActive;
	}

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

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}



	public Boolean getIsTempPassword() {
		return isTempPassword;
	}

	public void setIsTempPassword(Boolean isTempPassword) {
		this.isTempPassword = isTempPassword;
	}

	@Override
	public String toString() {
		return "UserCollection [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", middleName=" + middleName + ", userName=" + userName
				+ ", password=" + password + ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber + ", gender=" + gender + ", imageUrl="
				+ imageUrl + ", dob=" + dob + ", isActive=" + isActive + ", tempPassword=" + isTempPassword + "]";
	}

}
