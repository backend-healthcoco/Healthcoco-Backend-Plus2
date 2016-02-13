package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.FeedbackType;

public class Feedback extends GenericCollection{

	private String id;
	
	private FeedbackType type;
	
	private String doctorId;
	
	private String locationId;
	
	private String hospitalId;
	
	private String description;

	private String  deviceType;
	
	private String  deviceInfo;
	
	private String  isVisible;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public FeedbackType getType() {
		return type;
	}

	public void setType(FeedbackType type) {
		this.type = type;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(String isVisible) {
		this.isVisible = isVisible;
	}

	@Override
	public String toString() {
		return "Feedback [id=" + id + ", type=" + type + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", description=" + description + ", deviceType=" + deviceType
				+ ", deviceInfo=" + deviceInfo + ", isVisible=" + isVisible + "]";
	}
}
