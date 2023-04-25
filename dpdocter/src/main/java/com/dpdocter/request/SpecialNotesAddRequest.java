package com.dpdocter.request;

import java.util.List;

public class SpecialNotesAddRequest {
	List<String> specialNotes;

	String patientId;

	String doctorId;

	String hospitalId;

	String locationId;

	public List<String> getSpecialNotes() {
		return specialNotes;
	}

	public void setSpecialNotes(List<String> specialNotes) {
		this.specialNotes = specialNotes;
	}

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

	@Override
	public String toString() {
		return "SpecialNotesAddRequest [specialNotes=" + specialNotes + ", patientId=" + patientId + ", doctorId="
				+ doctorId + ", hospitalId=" + hospitalId + ", locationId=" + locationId + "]";
	}

}
