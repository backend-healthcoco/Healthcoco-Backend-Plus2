package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class UserAddress extends GenericCollection{

	private String id;

    private List<String> userIds;
	
    private String mobileNumber;
	
	private String fullName;
	
	private Address address;
	
	private Boolean discarded = false;

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

	@Override
	public String toString() {
		return "UserAddress [id=" + id + ", userIds=" + userIds + ", mobileNumber=" + mobileNumber + ", fullName="
				+ fullName + ", address=" + address + ", discarded=" + discarded + "]";
	}
}
