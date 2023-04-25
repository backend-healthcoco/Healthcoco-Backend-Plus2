package com.dpdocter.beans;

import java.util.List;

import org.bson.types.ObjectId;

public class PatientNumberAndUserIds {
	String mobileNumber;
	List<ObjectId> userIds;

	public String getMobileNumber() {
		return mobileNumber;
	}

	public List<ObjectId> getUserIds() {
		return userIds;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public void setUserIds(List<ObjectId> userIds) {
		this.userIds = userIds;
	}

}
