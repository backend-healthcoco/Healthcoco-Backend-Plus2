package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class UserAllowanceDetails extends GenericCollection {

	private String id;

	private List<String> userIds;

	private Double allowedRecordsSizeInMB;

	private Double availableRecordsSizeInMB;

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

	public Double getAllowedRecordsSizeInMB() {
		return allowedRecordsSizeInMB;
	}

	public void setAllowedRecordsSizeInMB(Double allowedRecordsSizeInMB) {
		this.allowedRecordsSizeInMB = allowedRecordsSizeInMB;
	}

	public Double getAvailableRecordsSizeInMB() {
		return availableRecordsSizeInMB;
	}

	public void setAvailableRecordsSizeInMB(Double availableRecordsSizeInMB) {
		this.availableRecordsSizeInMB = availableRecordsSizeInMB;
	}

	@Override
	public String toString() {
		return "UserAllowanceDetails [id=" + id + ", userIds=" + userIds + ", allowedRecordsSizeInMB="
				+ allowedRecordsSizeInMB + ", availableRecordsSizeInMB=" + availableRecordsSizeInMB + "]";
	}
}
