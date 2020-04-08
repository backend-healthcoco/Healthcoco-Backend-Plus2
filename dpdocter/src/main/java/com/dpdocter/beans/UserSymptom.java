package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class UserSymptom extends GenericCollection{
private String id;
	
	private String name;
	
	private String mobile;
	
	private String gender;
	
	private Integer age;
	
	private Symptom symtoms;
	
	private String covid19Possibilities;
	
	private String commonColdPossibilities;
	
	private String fluPossibilities;
	
	private String allergiesPossibilities;
	
	private Boolean discarded=false;
	
	private Address address;

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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Symptom getSymtoms() {
		return symtoms;
	}

	public void setSymtoms(Symptom symtoms) {
		this.symtoms = symtoms;
	}

	public String getCovid19Possibilities() {
		return covid19Possibilities;
	}

	public void setCovid19Possibilities(String covid19Possibilities) {
		this.covid19Possibilities = covid19Possibilities;
	}

	public String getCommonColdPossibilities() {
		return commonColdPossibilities;
	}

	public void setCommonColdPossibilities(String commonColdPossibilities) {
		this.commonColdPossibilities = commonColdPossibilities;
	}

	public String getFluPossibilities() {
		return fluPossibilities;
	}

	public void setFluPossibilities(String fluPossibilities) {
		this.fluPossibilities = fluPossibilities;
	}

	public String getAllergiesPossibilities() {
		return allergiesPossibilities;
	}

	public void setAllergiesPossibilities(String allergiesPossibilities) {
		this.allergiesPossibilities = allergiesPossibilities;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	
}
