package com.dpdocter.request;

import com.dpdocter.beans.DailyPatientFeedback;

public class DailyImprovementFeedbackRequest {

	private String id;
	private String locationId;
	private String doctorId;
	private String patientId;
	private String hospitalId;
	private String prescriptionId;
	private DailyPatientFeedback dailyPatientFeedback;
	private Boolean discarded = false;

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

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public DailyPatientFeedback getDailyPatientFeedback() {
		return dailyPatientFeedback;
	}

	public void setDailyPatientFeedback(DailyPatientFeedback dailyPatientFeedback) {
		this.dailyPatientFeedback = dailyPatientFeedback;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "DailyImprovementFeedbackRequest [id=" + id + ", locationId=" + locationId + ", doctorId=" + doctorId
				+ ", patientId=" + patientId + ", hospitalId=" + hospitalId + ", prescriptionId=" + prescriptionId
				+ ", dailyPatientFeedback=" + dailyPatientFeedback + ", discarded=" + discarded + "]";
	}

}
