package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.AuditActionType;
@Document(collection = "audit_log_cl")
public class AuditLogCollection extends GenericCollection{

	@Id
	private ObjectId id;
	
	@Indexed
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private AuditActionType action;

	@Field
	private String username;

	@Field
	private String dataModifiedId;

	@Field
	private String deviceName;

	@Field
	private String details;
	
	@Field
	private String dataViewId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
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
