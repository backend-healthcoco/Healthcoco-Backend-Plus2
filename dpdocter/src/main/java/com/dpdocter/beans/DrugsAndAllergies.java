package com.dpdocter.beans;

import java.util.List;

public class DrugsAndAllergies {

	private String id;
	private List<Drug> drugs;
	private String allergies;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Drug> getDrugs() {
		return drugs;
	}

	public void setDrugs(List<Drug> drugs) {
		this.drugs = drugs;
	}

	public String getAllergies() {
		return allergies;
	}

	public void setAllergies(String allergies) {
		this.allergies = allergies;
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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	@Override
	public String toString() {
		return "DrugsAndAllergies [id=" + id + ", drugs=" + drugs + ", allergies=" + allergies + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId
				+ "]";
	}

}
