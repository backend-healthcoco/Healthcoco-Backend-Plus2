package com.dpdocter.request;

public class GenerateLinkTokenV3Request {
	private String abhaNumber;
	private String abhaAddress;
	private String name;
	private String gender;
	private Integer yearOfBirth;
	private String hipId;

	public String getAbhaNumber() {
		return abhaNumber;
	}

	public void setAbhaNumber(String abhaNumber) {
		this.abhaNumber = abhaNumber;
	}

	public String getAbhaAddress() {
		return abhaAddress;
	}

	public void setAbhaAddress(String abhaAddress) {
		this.abhaAddress = abhaAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getYearOfBirth() {
		return yearOfBirth;
	}

	public void setYearOfBirth(Integer yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}

	public String getHipId() {
		return hipId;
	}

	public void setHipId(String hipId) {
		this.hipId = hipId;
	}

}
