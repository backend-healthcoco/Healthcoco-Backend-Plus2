package com.dpdocter.beans;

import java.util.List;

public class DiscoverPatient {

	private String id;
	
	private List<AuthConfirmIdentifier> verifiedIdentifiers;
	
	private List<AuthConfirmIdentifier> unverifiedIdentifiers;
	
	private String name;
	
	private String gender;
	
	private String yearOfBirth;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<AuthConfirmIdentifier> getVerifiedIdentifiers() {
		return verifiedIdentifiers;
	}

	public void setVerifiedIdentifiers(List<AuthConfirmIdentifier> verifiedIdentifiers) {
		this.verifiedIdentifiers = verifiedIdentifiers;
	}

	public List<AuthConfirmIdentifier> getUnverifiedIdentifiers() {
		return unverifiedIdentifiers;
	}

	public void setUnverifiedIdentifiers(List<AuthConfirmIdentifier> unverifiedIdentifiers) {
		this.unverifiedIdentifiers = unverifiedIdentifiers;
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
	
	
	
}
