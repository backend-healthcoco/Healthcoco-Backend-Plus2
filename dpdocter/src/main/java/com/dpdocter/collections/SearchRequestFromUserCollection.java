package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.request.PrescriptionRequest;

@Document(collection = "search_request_from_user_cl")
public class SearchRequestFromUserCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId userId;

	@Field
	private String uniqueRequestId;

	@Field
	private PrescriptionRequest prescriptionRequest;

	@Field
	private ObjectId localeId;

	@Field
	private String location;

	@Field
	private Double latitude;

	@Field
	private Double longitude;

	@Field
	private Boolean isCancelled = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
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

	public ObjectId getLocaleId() {
		return localeId;
	}

	public void setLocaleId(ObjectId localeId) {
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

	public Boolean getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(Boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	@Override
	public String toString() {
		return "SearchRequestFromUserCollection [id=" + id + ", userId=" + userId + ", uniqueRequestId="
				+ uniqueRequestId + ", prescriptionRequest=" + prescriptionRequest + ", localeId=" + localeId
				+ ", location=" + location + ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}
}
