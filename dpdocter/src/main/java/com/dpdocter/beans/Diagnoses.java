package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Diagnoses extends GenericCollection {

	private String id;

	private String diagnoses;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getDiagnoses() {
		return diagnoses;
	}

	public void setDiagnoses(String diagnoses) {
		this.diagnoses = diagnoses;
	}

	@Override
	public String toString() {
		return "Diagnoses [id=" + id + ", diagnoses=" + diagnoses + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ "]";
	}

}
