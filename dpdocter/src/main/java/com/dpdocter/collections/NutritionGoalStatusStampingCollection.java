package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "nutrition_goal_status_stamping_cl")
public class NutritionGoalStatusStampingCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;
	@Field
	private String goalStatus;
	@Field
	private ObjectId referredDoctorId;
	@Field
	private ObjectId referredLocationId;
	@Field
	private ObjectId referredHospitalId;

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

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public String getGoalStatus() {
		return goalStatus;
	}

	public void setGoalStatus(String goalStatus) {
		this.goalStatus = goalStatus;
	}

	public ObjectId getReferredDoctorId() {
		return referredDoctorId;
	}

	public void setReferredDoctorId(ObjectId referredDoctorId) {
		this.referredDoctorId = referredDoctorId;
	}

	public ObjectId getReferredLocationId() {
		return referredLocationId;
	}

	public void setReferredLocationId(ObjectId referredLocationId) {
		this.referredLocationId = referredLocationId;
	}

	public ObjectId getReferredHospitalId() {
		return referredHospitalId;
	}

	public void setReferredHospitalId(ObjectId referredHospitalId) {
		this.referredHospitalId = referredHospitalId;
	}

	@Override
	public String toString() {
		return "NutritionGoalStatusStampingCollection [id=" + id + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", goalStatus=" + goalStatus
				+ "]";
	}
}
