package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.FeelingTypeEnum;

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
	private String explanation;
	@Field
	private FeelingTypeEnum feelingType;
	@Field
	private Long submissionDate;
	@Field
	private Integer day;
	@Field
	private Boolean discarded;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public FeelingTypeEnum getFeelingType() {
		return feelingType;
	}

	public void setFeelingType(FeelingTypeEnum feelingType) {
		this.feelingType = feelingType;
	}

	public Long getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Long submissionDate) {
		this.submissionDate = submissionDate;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
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

	@Override
	public String toString() {
		return "DailyImprovementFeedbackCollection [id=" + id + ", prescriptionId=" + prescriptionId + ", explanation="
				+ explanation + ", feelingType=" + feelingType + ", submissionDate=" + submissionDate + ", day=" + day
				+ "]";
	}

}
