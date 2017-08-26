package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.FeelingTypeEnum;

public class DailyImprovementFeedback extends GenericCollection {

	private String id;
	private String locationId;
	private String doctorId;
	private String patientId;
	private String hospitalId;
	private String prescriptionId;
	private String explanation;
	private FeelingTypeEnum feelingType;
	private Long submissionDate;
	private Integer day;
	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public Long getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Long submissionDate) {
		this.submissionDate = submissionDate;
	}

	public FeelingTypeEnum getFeelingType() {
		return feelingType;
	}

	public void setFeelingType(FeelingTypeEnum feelingType) {
		this.feelingType = feelingType;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
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
		return "DailyImprovementFeedback [id=" + id + ", prescriptionId=" + prescriptionId + ", explanation="
				+ explanation + ", feelingType=" + feelingType + ", submissionDate=" + submissionDate + ", day=" + day
				+ "]";
	}

}
