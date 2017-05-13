package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "crn_collection")
public class CRNCollection extends GenericCollection {

	private ObjectId id;
	private String crnNumber;
	private Long usedAt;
	private ObjectId doctorId;
	private ObjectId locationId;
	private Boolean isUsed;
	private String requestId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getCrnNumber() {
		return crnNumber;
	}

	public void setCrnNumber(String crnNumber) {
		this.crnNumber = crnNumber;
	}

	public Long getCreatedAt() {
		return usedAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.usedAt = createdAt;
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

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	public Long getUsedAt() {
		return usedAt;
	}

	public void setUsedAt(Long usedAt) {
		this.usedAt = usedAt;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@Override
	public String toString() {
		return "CRNCollection [id=" + id + ", crnNumber=" + crnNumber + ", usedAt=" + usedAt + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", isUsed=" + isUsed + ", requestId=" + requestId + "]";
	}

}
