package com.dpdocter.beans;

import com.dpdocter.enums.AuditActionType;

public class AuditLog {

	private String id;

	private AuditActionType action;

	private String username;

	private long timestamp;

	private String dataModifiedId;

	private String deviceName;

	private String details;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AuditActionType getAction() {
		return action;
	}

	public void setAction(AuditActionType action) {
		this.action = action;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getDataModifiedId() {
		return dataModifiedId;
	}

	public void setDataModifiedId(String dataModifiedId) {
		this.dataModifiedId = dataModifiedId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
}
