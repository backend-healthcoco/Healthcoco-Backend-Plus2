package com.dpdocter.response;

import java.util.List;

import org.bson.types.ObjectId;

public class UserFakeRequestDetailResponse {
	private Integer noOfAttemptAllowedInHour = 3;

	private Integer noOfAttemptAllowedIn24Hour = 10;

	private Integer noOfAttemptIn24Hour = 0;

	private Integer noOfAttemptInHour = 0;

	private List<ObjectId> userIds;

	public List<ObjectId> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<ObjectId> userIds) {
		this.userIds = userIds;
	}

	public Integer getNoOfAttemptAllowedInHour() {
		return noOfAttemptAllowedInHour;
	}

	public Integer getNoOfAttemptAllowedIn24Hour() {
		return noOfAttemptAllowedIn24Hour;
	}

	public Integer getNoOfAttemptIn24Hour() {
		return noOfAttemptIn24Hour;
	}

	public Integer getNoOfAttemptInHour() {
		return noOfAttemptInHour;
	}

	public void setNoOfAttemptAllowedInHour(Integer noOfAttemptAllowedInHour) {
		this.noOfAttemptAllowedInHour = noOfAttemptAllowedInHour;
	}

	public void setNoOfAttemptAllowedIn24Hour(Integer noOfAttemptAllowedIn24Hour) {
		this.noOfAttemptAllowedIn24Hour = noOfAttemptAllowedIn24Hour;
	}

	public void setNoOfAttemptIn24Hour(Integer noOfAttemptIn24Hour) {
		this.noOfAttemptIn24Hour = noOfAttemptIn24Hour;
	}

	public void setNoOfAttemptInHour(Integer noOfAttemptInHour) {
		this.noOfAttemptInHour = noOfAttemptInHour;
	}

}
