package com.dpdocter.beans;

public class Recommendation {

	private String id;

	private String doctorClinicProfileId;

	private String patientId;

	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorClinicProfileId() {
		return doctorClinicProfileId;
	}

	public void setDoctorClinicProfileId(String doctorClinicProfileId) {
		this.doctorClinicProfileId = doctorClinicProfileId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
