package com.dpdocter.beans;

public class AuthDemographic {

private String name;
	
	private String gender;
	
	private String dateOfBirth;
	
	private AuthConfirmIdentifier identifier;

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

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public AuthConfirmIdentifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(AuthConfirmIdentifier identifier) {
		this.identifier = identifier;
	}
	
	
}
