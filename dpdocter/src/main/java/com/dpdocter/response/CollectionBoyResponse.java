package com.dpdocter.response;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.Age;
import com.dpdocter.beans.Location;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.LabType;

public class CollectionBoyResponse extends GenericCollection {
	private String id;
	private String locationId;
	private String hospitalId;
	private String userId;
	private String name;
	private Age age;
	private String gender;
	private Address address;
	private String mobileNumber;
	private String profileImageURL;
	private Boolean discarded = false;
	private Boolean isAvailable = false;
	private Location location;
<<<<<<< HEAD
=======
	private String labType = LabType.DIAGNOSTIC.getType();
>>>>>>> release-1.0.63

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getProfileImageURL() {
		return profileImageURL;
	}

	public void setProfileImageURL(String profileImageURL) {
		this.profileImageURL = profileImageURL;
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

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getLabType() {
		return labType;
	}

	public void setLabType(String labType) {
		this.labType = labType;
	}

	@Override
	public String toString() {
		return "CollectionBoyResponse [id=" + id + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", userId=" + userId + ", name=" + name + ", age=" + age + ", gender=" + gender + ", address="
				+ address + ", mobileNumber=" + mobileNumber + ", profileImageURL=" + profileImageURL + ", discarded="
				+ discarded + ", isAvailable=" + isAvailable + ", location=" + location + "]";
	}

}
