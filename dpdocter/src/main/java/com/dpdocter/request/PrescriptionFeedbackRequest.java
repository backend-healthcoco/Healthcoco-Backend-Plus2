package com.dpdocter.request;

import com.dpdocter.enums.MedicationEffectType;

public class PrescriptionFeedbackRequest {

	private String locationId;
	private String doctorId;
	private String patientId;
	private String hospitalId;
	private Boolean medicationOnTime;
	private MedicationEffectType medicationEffectType; // how patient feeling
	// after taking medicine
	private String description;

	public String getLocationId() {
		return locationId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public String getPatientId() {
		return patientId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public Boolean getMedicationOnTime() {
		return medicationOnTime;
	}

	public MedicationEffectType getMedicationEffectType() {
		return medicationEffectType;
	}

	public String getDescription() {
		return description;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public void setMedicationOnTime(Boolean medicationOnTime) {
		this.medicationOnTime = medicationOnTime;
	}

	public void setMedicationEffectType(MedicationEffectType medicationEffectType) {
		this.medicationEffectType = medicationEffectType;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
