package com.dpdocter.request;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Drug;

public class DrugsAndAllergiesAddRequest {

	private List<String> drugIds;
	private String allergies;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;

	public List<String> getDrugIds() {
		return drugIds;
	}

	public void setDrugIds(List<String> drugIds) {
		this.drugIds = drugIds;
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
		return "DrugsAndAllergiesAddRequest [drugIds=" + drugIds + ", allergies=" + allergies + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId + "]";
	}

}
