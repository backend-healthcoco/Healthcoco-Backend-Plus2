package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AppType;
import com.dpdocter.enums.FeedbackType;

public class Feedback extends GenericCollection{

	private String id;
	
	private FeedbackType type;
	
	private AppType appType;

	private String resourceId;
	
    private String doctorId;
	
	private String locationId;
	
	private String hospitalId;
	
    private String userId;
	
	private String explanation;

	private String  deviceType;
	
	private String  deviceInfo;
	
	private Boolean  isVisible = false;

	private Boolean isRecommended = false;

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

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
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

	public AppType getAppType() {
		return appType;
	}

	public void setAppType(AppType appType) {
		this.appType = appType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Boolean getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}

	public Boolean getIsRecommended() {
		return isRecommended;
	}

	public void setIsRecommended(Boolean isRecommended) {
		this.isRecommended = isRecommended;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	@Override
	public String toString() {
		return "Feedback [id=" + id + ", type=" + type + ", appType=" + appType + ", resourceId=" + resourceId
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", userId="
				+ userId + ", explanation=" + explanation + ", deviceType=" + deviceType + ", deviceInfo=" + deviceInfo
				+ ", isVisible=" + isVisible + ", isRecommended=" + isRecommended + "]";
	}
}
