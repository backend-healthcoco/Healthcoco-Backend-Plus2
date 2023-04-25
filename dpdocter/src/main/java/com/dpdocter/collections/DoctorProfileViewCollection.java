package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "doctor_profile_view_cl")
public class DoctorProfileViewCollection extends GenericCollection {

	@Id
	public ObjectId id;

	@Field
	public ObjectId doctorId;

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

	@Override
	public String toString() {
		return "DoctorProfileViewCollection [id=" + id + ", doctorId=" + doctorId + "]";
	}

}