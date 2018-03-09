package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dental_lab_doctor_association_cl")
public class DentalLabDoctorAssociationCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId dentalLabId;
	@Field
	private Boolean isActive = true;

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

	public ObjectId getDentalLabId() {
		return dentalLabId;
	}

	public void setDentalLabId(ObjectId dentalLabId) {
		this.dentalLabId = dentalLabId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "DentalLabDoctorAssociationCollection [id=" + id + ", doctorId=" + doctorId + ", dentalLabId="
				+ dentalLabId + ", isActive=" + isActive + "]";
	}

}
