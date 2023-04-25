package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class MedicalHistoryHandler {
	private String patientId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private List<String> addDiseases;

	private List<String> removeDiseases;

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

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public List<String> getAddDiseases() {
		return addDiseases;
	}

	public void setAddDiseases(List<String> addDiseases) {
		this.addDiseases = addDiseases;
	}

	public List<String> getRemoveDiseases() {
		return removeDiseases;
	}

	public void setRemoveDiseases(List<String> removeDiseases) {
		this.removeDiseases = removeDiseases;
	}

	@Override
	public String toString() {
		return "MedicalHistoryHandler [doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", addDiseases=" + addDiseases + ", removeDiseases=" + removeDiseases + "]";
	}

}
