package com.dpdocter.beans;

public class DentalImagingRequest {

	private String patientId;
	private String doctorId;
	private String hospitalId;
	private String locationId;
	private String referringDoctor;
	private String clinicalNotes;
	private Boolean reportsRequired;
	private String specialInstructions;

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getReferringDoctor() {
		return referringDoctor;
	}

	public void setReferringDoctor(String referringDoctor) {
		this.referringDoctor = referringDoctor;
	}

	public String getClinicalNotes() {
		return clinicalNotes;
	}

	public void setClinicalNotes(String clinicalNotes) {
		this.clinicalNotes = clinicalNotes;
	}

	public Boolean getReportsRequired() {
		return reportsRequired;
	}

	public void setReportsRequired(Boolean reportsRequired) {
		this.reportsRequired = reportsRequired;
	}

	public String getSpecialInstructions() {
		return specialInstructions;
	}

	public void setSpecialInstructions(String specialInstructions) {
		this.specialInstructions = specialInstructions;
	}

	@Override
	public String toString() {
		return "DentalImagingRequest [patientId=" + patientId + ", doctorId=" + doctorId + ", hospitalId=" + hospitalId
				+ ", locationId=" + locationId + ", referringDoctor=" + referringDoctor + ", clinicalNotes="
				+ clinicalNotes + ", reportsRequired=" + reportsRequired + ", specialInstructions="
				+ specialInstructions + "]";
	}

}
