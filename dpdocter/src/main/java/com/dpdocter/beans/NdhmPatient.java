package com.dpdocter.beans;

import java.util.List;

public class NdhmPatient {

	private String id;

	private String name;

	private String gender;

	private String yearOfBirth;
	private String dayOfBirth;
	private String monthOfBirth;

	private NdhmAddress address;

	private List<AuthConfirmIdentifier> identifiers;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getYearOfBirth() {
		return yearOfBirth;
	}

	public void setYearOfBirth(String yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}

	public NdhmAddress getAddress() {
		return address;
	}

	public void setAddress(NdhmAddress address) {
		this.address = address;
	}

	public List<AuthConfirmIdentifier> getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(List<AuthConfirmIdentifier> identifiers) {
		this.identifiers = identifiers;
	}

	public String getDayOfBirth() {
		return dayOfBirth;
	}

	public void setDayOfBirth(String dayOfBirth) {
		this.dayOfBirth = dayOfBirth;
	}

	public String getMonthOfBirth() {
		return monthOfBirth;
	}

	public void setMonthOfBirth(String monthOfBirth) {
		this.monthOfBirth = monthOfBirth;
	}

}
