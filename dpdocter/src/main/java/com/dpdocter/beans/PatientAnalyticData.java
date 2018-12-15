package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

public class PatientAnalyticData {
	private String id;
	private String firstName;
	private String localPatientName;
	private String pid;
	private Long registrationDate;
	private Date createdTime;
	private List<Date> visitedTime;
	private String mobileNumber;
	private DOB dob;
	private Address address;
	private String gender;

	public String getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public Long getRegistrationDate() {
		return registrationDate;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setRegistrationDate(Long registrationDate) {
		this.registrationDate = registrationDate;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<Date> getVisitedTime() {
		return visitedTime;
	}

	public void setVisitedTime(List<Date> visitedTime) {
		this.visitedTime = visitedTime;
	}

}
