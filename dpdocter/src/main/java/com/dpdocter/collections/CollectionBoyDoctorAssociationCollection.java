package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "collection_boy_doctor_association_cl")
public class CollectionBoyDoctorAssociationCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId collectionBoyId;
	@Field
	private ObjectId dentalLabId;
	@Field
	private ObjectId doctorId;
	@Field
	private Boolean isActive = true;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(ObjectId collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public ObjectId getDentalLabId() {
		return dentalLabId;
	}

	public void setDentalLabId(ObjectId dentalLabId) {
		this.dentalLabId = dentalLabId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "CollectionBoyDoctorAssociationCollection [id=" + id + ", collectionBoyId=" + collectionBoyId
				+ ", dentalLabId=" + dentalLabId + ", doctorId=" + doctorId + ", isActive=" + isActive + "]";
	}

}
