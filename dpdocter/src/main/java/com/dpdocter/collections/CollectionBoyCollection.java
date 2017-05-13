package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.Age;

@Document(collection = "collection_boy_cl")
public class CollectionBoyCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String name;
	@Field
	private Age age;
	@Field
	private String gender;
	@Field
	private Address address;
	@Field
	@Indexed(unique = true)
	private String mobileNumber;
	@Field
	private List<ObjectId> assignedLabs;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public List<ObjectId> getAssignedLabs() {
		return assignedLabs;
	}

	public void setAssignedLabs(List<ObjectId> assignedLabs) {
		this.assignedLabs = assignedLabs;
	}

	@Override
	public String toString() {
		return "CollectionBoyCollection [id=" + id + ", name=" + name + ", age=" + age + ", gender=" + gender
				+ ", address=" + address + ", mobileNumber=" + mobileNumber + ", assignedLabs=" + assignedLabs + "]";
	}

}
