package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.enums.GenderType;

public class AddEditPrimaryDetailResponse {
	
	private String patientId;
	private String Id;
	private String name;
	private String mobilenumber;
	private Date DateOfBirth;
	private GenderType gender;
	private Integer age;
	
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobilenumber() {
		return mobilenumber;
	}
	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}
	public Date getDateOfBirth() {
		return DateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		DateOfBirth = dateOfBirth;
	}
	public GenderType getGender() {
		return gender;
	}
	public void setGender(GenderType gender) {
		this.gender = gender;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
	
	@Override
	public String toString() {
		return "AddEditPrimaryDetailRequest [patientId=" + patientId + ", Id=" + Id + ", name=" + name
				+ ", mobilenumber=" + mobilenumber + ", DateOfBirth=" + DateOfBirth + ", gender=" + gender + ", age="
				+ age + "]";
	}
	
	
	
	
	
	
	

	

}
