package com.dpdocter.beans;

import java.util.Date;

public class GroupPatientCount {

	private String groupName;

	private String groupId;

	private int patientCount;

	private Date groupCreatedDate;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getPatientCount() {
		return patientCount;
	}

	public void setPatientCount(int patientCount) {
		this.patientCount = patientCount;
	}

	public Date getGroupCreatedDate() {
		return groupCreatedDate;
	}

	public void setGroupCreatedDate(Date groupCreatedDate) {
		this.groupCreatedDate = groupCreatedDate;
	}

}
