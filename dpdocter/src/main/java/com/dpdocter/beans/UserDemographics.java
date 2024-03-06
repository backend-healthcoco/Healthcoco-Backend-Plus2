package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.NdhmGender;

public class UserDemographics {

	private String healthId;
	
	private String healthIdNumber;
	
	private String name;
	
	private NdhmGender gender;
	
	private Integer dayOfBirth;
	
	private Integer monthOfBirth;
	
	private Integer yearOfBirth;
	
	private NdhmAddress address;
	
	private List<PatientIdentifier> identifiers;

	public String getHealthId() {
		return healthId;
	}

	public void setHealthId(String healthId) {
		this.healthId = healthId;
	}

	public String getHealthIdNumber() {
		return healthIdNumber;
	}

	public void setHealthIdNumber(String healthIdNumber) {
		this.healthIdNumber = healthIdNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NdhmGender getGender() {
		return gender;
	}

	public void setGender(NdhmGender gender) {
		this.gender = gender;
	}

	

	public Integer getDayOfBirth() {
		return dayOfBirth;
	}

	public void setDayOfBirth(Integer dayOfBirth) {
		this.dayOfBirth = dayOfBirth;
	}

	public Integer getMonthOfBirth() {
		return monthOfBirth;
	}

	public void setMonthOfBirth(Integer monthOfBirth) {
		this.monthOfBirth = monthOfBirth;
	}

	public NdhmAddress getAddress() {
		return address;
	}

	public void setAddress(NdhmAddress address) {
		this.address = address;
	}

	public Integer getYearOfBirth() {
		return yearOfBirth;
	}

	public void setYearOfBirth(Integer yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}

	public List<PatientIdentifier> getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(List<PatientIdentifier> identifiers) {
		this.identifiers = identifiers;
	}

	
	
	
	 
}
