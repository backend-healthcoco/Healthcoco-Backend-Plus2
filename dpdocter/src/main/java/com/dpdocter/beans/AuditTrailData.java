package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AuditActionType;

public class AuditTrailData extends GenericCollection {
	private String id;
	private String doctorId;

	private String locationId;

	private String hospitalId;
	private AuditActionType action;

	private String username;

	private String dataModifiedId;

	private String deviceName;

	private String details;
	private String dataViewId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
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

	public String getDataViewId() {
		return dataViewId;
	}

	public void setDataViewId(String dataViewId) {
		this.dataViewId = dataViewId;
	}

}
