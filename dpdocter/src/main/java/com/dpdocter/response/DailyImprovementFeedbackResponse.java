package com.dpdocter.response;

import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.FeelingTypeEnum;

public class DailyImprovementFeedbackResponse extends GenericCollection {

	private String id;
	private String locationId;
	private String locationName;
	private String doctorId;
	private String doctorName;
	private String patientId;
	private PatientShortCard patientCard;
	private String hospitalId;
	private String hospitalName;
	private String prescriptionId;
	private String explanation;
	private FeelingTypeEnum feelingType;
	private Long submissionDate;
	private Integer day;
	private Boolean discarded;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public PatientShortCard getPatientCard() {
		return patientCard;
	}

	public void setPatientCard(PatientShortCard patientCard) {
		this.patientCard = patientCard;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
