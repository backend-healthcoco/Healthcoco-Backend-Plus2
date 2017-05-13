package com.dpdocter.beans;

import java.util.List;

public class CollectionBoy {

	private String id;
	private String name;
	private Age age;
	private String gender;
	private Address address;
	private String mobileNumber;
	private List<String> assignedLabs;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Age getAge() {
		return age;
	}

	public void setAge(Age age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAssignedLabs() {
		return assignedLabs;
	}

	public void setAssignedLabs(List<String> assignedLabs) {
		this.assignedLabs = assignedLabs;
	}

	@Override
	public String toString() {
		return "CollectionBoy [id=" + id + ", name=" + name + ", age=" + age + ", gender=" + gender + ", address="
				+ address + ", mobileNumber=" + mobileNumber + "]";
	}

}
