package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "recommendation_cl")
public class RecommendationsCollection {
	@Id
	private ObjectId id;

	@Field
	private ObjectId doctorClinicProfileId;

	@Field
	private ObjectId patientId;

	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public ObjectId getDoctorClinicProfileId() {
		return doctorClinicProfileId;
	}

	public void setDoctorClinicProfileId(ObjectId doctorClinicProfileId) {
		this.doctorClinicProfileId = doctorClinicProfileId;
	}

}
