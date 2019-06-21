package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class UserAddress extends GenericCollection {

	private String id;

	private List<String> userIds;

	private String mobileNumber;

	private String fullName;

	private Address address;

	private Boolean discarded = false;

	private String formattedAddress;

	private String homeDeliveryMobileNumber;

	private String type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
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

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
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
		return "UserAddress [id=" + id + ", userIds=" + userIds + ", mobileNumber=" + mobileNumber + ", fullName="
				+ fullName + ", address=" + address + ", discarded=" + discarded + ", formattedAddress="
				+ formattedAddress + ", homeDeliveryMobileNumber=" + homeDeliveryMobileNumber + "]";
	}

}
