package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "dental_lab_service_association_cl")
public class DentalLabServiceAssociationCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId dentalImagingServiceId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDentalImagingServiceId() {
		return dentalImagingServiceId;
	}

	public void setDentalImagingServiceId(ObjectId dentalImagingServiceId) {
		this.dentalImagingServiceId = dentalImagingServiceId;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "DentalLabServiceAssociationCollection [id=" + id + ", dentalImagingServiceId=" + dentalImagingServiceId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + "]";
	}

}
