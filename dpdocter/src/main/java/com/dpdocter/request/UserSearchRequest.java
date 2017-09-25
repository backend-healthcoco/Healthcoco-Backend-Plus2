package com.dpdocter.request;

import java.util.List;

public class UserSearchRequest {

	private String userId;

	private String location;

	private Double latitude;

	private Double longitude;

	private PrescriptionRequest prescriptionRequest;

	private String localeId;

	private String uniqueRequestId;

	private List<String> pharmacyType;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public PrescriptionRequest getPrescriptionRequest() {
		return prescriptionRequest;
	}

	public void setPrescriptionRequest(PrescriptionRequest prescriptionRequest) {
		this.prescriptionRequest = prescriptionRequest;
	}

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
	}

	public String getUniqueRequestId() {
		return uniqueRequestId;
	}

	public void setUniqueRequestId(String uniqueRequestId) {
		this.uniqueRequestId = uniqueRequestId;
	}

	public List<String> getPharmacyType() {
		return pharmacyType;
	}

	public void setPharmacyType(List<String> pharmacyType) {
		this.pharmacyType = pharmacyType;
	}

	@Override
	public String toString() {
		return "UserSearchRequest [userId=" + userId + ", location=" + location + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", prescriptionRequest=" + prescriptionRequest + ", localeId=" + localeId
				+ ", uniqueRequestId=" + uniqueRequestId + ", pharmacyType=" + pharmacyType + "]";
	}
}
