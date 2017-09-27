package com.dpdocter.response;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.request.PrescriptionRequest;

public class SearchRequestFromUserResponse extends GenericCollection {

	private String id;

	private String userId;

	private String uniqueRequestId;

	private PrescriptionRequest prescriptionRequest;

	private String localeId;

	private String pharmacyName;

	private String location;

	private Double latitude;

	private Double longitude;

	private Integer countForNo;
	private Integer countForYes;

	private Boolean isCancelled = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUniqueRequestId() {
		return uniqueRequestId;
	}

	public void setUniqueRequestId(String uniqueRequestId) {
		this.uniqueRequestId = uniqueRequestId;
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

	public Integer getCountForNo() {
		return countForNo;
	}

	public void setCountForNo(Integer countForNo) {
		this.countForNo = countForNo;
	}

	public Integer getCountForYes() {
		return countForYes;
	}

	public void setCountForYes(Integer countForYes) {
		this.countForYes = countForYes;
	}

	public Boolean getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(Boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public String getPharmacyName() {
		return pharmacyName;
	}

	public void setPharmacyName(String pharmacyName) {
		this.pharmacyName = pharmacyName;
	}

	@Override
	public String toString() {
		return "SearchRequestFromUserResponse [id=" + id + ", userId=" + userId + ", uniqueRequestId=" + uniqueRequestId
				+ ", prescriptionRequest=" + prescriptionRequest + ", localeId=" + localeId + ", pharmacyName="
				+ pharmacyName + ", location=" + location + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", countForNo=" + countForNo + ", countForYes=" + countForYes + ", isCancelled=" + isCancelled + "]";
	}

}
