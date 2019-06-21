package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.Age;
import com.dpdocter.enums.LabType;

@Document(collection = "collection_boy_cl")
public class CollectionBoyCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId userId;
	@Field
	private String name;
	@Field
	private Age age;
	@Field
	private String gender;
	@Field
	private Address address;
	@Field
	private String mobileNumber;
	@Field
	private Boolean discarded;
	@Field
	private Boolean isAvailable;
	@Field
	private String labType = LabType.DIAGNOSTIC.getType();

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

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public String getLabType() {
		return labType;
	}

	public void setLabType(String labType) {
		this.labType = labType;
	}

	@Override
	public String toString() {
		return "CollectionBoyCollection [id=" + id + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", userId=" + userId + ", name=" + name + ", age=" + age + ", gender=" + gender + ", address="
				+ address + ", mobileNumber=" + mobileNumber + ", discarded=" + discarded + ", isAvailable="
				+ isAvailable + ", labType=" + labType + "]";
	}

}
