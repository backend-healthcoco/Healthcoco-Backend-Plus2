package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;

@Document(collection = "user_address_cl")
public class UserAddressCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private List<ObjectId> userIds;

	@Field
	private String fullName;

	@Field
	private Address address;

	@Field
	private Boolean discarded = false;

	@Field
	private String mobileNumber;

	@Field
	private String homeDeliveryMobileNumber;

	@Field
	private String type;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<ObjectId> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<ObjectId> userIds) {
		this.userIds = userIds;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getHomeDeliveryMobileNumber() {
		return homeDeliveryMobileNumber;
	}

	public void setHomeDeliveryMobileNumber(String homeDeliveryMobileNumber) {
		this.homeDeliveryMobileNumber = homeDeliveryMobileNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "UserAddressCollection [id=" + id + ", userIds=" + userIds + ", fullName=" + fullName + ", address="
				+ address + ", discarded=" + discarded + ", mobileNumber=" + mobileNumber
				+ ", homeDeliveryMobileNumber=" + homeDeliveryMobileNumber + "]";
	}

}
