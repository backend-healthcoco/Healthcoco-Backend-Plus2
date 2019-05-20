package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class DailyImprovementFeedback extends GenericCollection {

	private String id;
	private String locationId;
	private String doctorId;
	private String patientId;
	private String hospitalId;
	private String prescriptionId;
	private List<DailyPatientFeedback> dailyPatientFeedbacks;
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

	public List<DailyPatientFeedback> getDailyPatientFeedbacks() {
		return dailyPatientFeedbacks;
	}

	public void setDailyPatientFeedbacks(List<DailyPatientFeedback> dailyPatientFeedbacks) {
		this.dailyPatientFeedbacks = dailyPatientFeedbacks;
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
		return "DailyImprovementFeedback [id=" + id + ", locationId=" + locationId + ", doctorId=" + doctorId
				+ ", patientId=" + patientId + ", hospitalId=" + hospitalId + ", prescriptionId=" + prescriptionId
				+ ", dailyPatientFeedbacks=" + dailyPatientFeedbacks + ", discarded=" + discarded + "]";
	}

}
