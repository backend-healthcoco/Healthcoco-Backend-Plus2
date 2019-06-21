package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DailyPatientFeedback;

@Document(collection = "daily_improvement_feedback_cl")
public class DailyImprovementFeedbackCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId prescriptionId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId patientId;
	@Field
	private ObjectId hospitalId;
	@Field
	private List<DailyPatientFeedback> dailyPatientFeedbacks;
	@Field
	private Boolean discarded;
	@Field
	private Boolean isPatientDiscarded = false;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<DailyPatientFeedback> getDailyPatientFeedbacks() {
		return dailyPatientFeedbacks;
	}

	public void setDailyPatientFeedbacks(List<DailyPatientFeedback> dailyPatientFeedbacks) {
		this.dailyPatientFeedbacks = dailyPatientFeedbacks;
	}

	public ObjectId getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(ObjectId prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
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

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "DailyImprovementFeedbackCollection [id=" + id + ", prescriptionId=" + prescriptionId + ", locationId="
				+ locationId + ", doctorId=" + doctorId + ", patientId=" + patientId + ", hospitalId=" + hospitalId
				+ ", dailyPatientFeedbacks=" + dailyPatientFeedbacks + ", discarded=" + discarded
				+ ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}

}
