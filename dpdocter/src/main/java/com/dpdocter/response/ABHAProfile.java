package com.dpdocter.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown=true)
public class ABHAProfile {
	private String firstName;
	private String middleName;
	private String lastName;
	private String dob;
	private String gender;
	private String photo;
	private String mobile;
	private String email;
	private List<String> phrAddress;
	private String address;
	private String districtCode;
	private String stateCode;
	private String abhaType;
    @JsonProperty("ABHANumber")
	private String ABHANumber;
	private String abhaStatus;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getPhrAddress() {
		return phrAddress;
	}

	public void setPhrAddress(List<String> phrAddress) {
		this.phrAddress = phrAddress;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getAbhaType() {
		return abhaType;
	}

	public void setAbhaType(String abhaType) {
		this.abhaType = abhaType;
	}

	public String getABHANumber() {
		return ABHANumber;
	}

	public void setABHANumber(String aBHANumber) {
		ABHANumber = aBHANumber;
	}

	public String getAbhaStatus() {
		return abhaStatus;
	}

	public void setAbhaStatus(String abhaStatus) {
		this.abhaStatus = abhaStatus;
	}
}
