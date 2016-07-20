package com.dpdocter.request;

import java.util.List;

public class DrugDosageAddEditRequest {

    private String id;

    private String dosage;

    private List<Long> dosageTime;
    
    private String doctorId;

    private String locationId;

    private String hospitalId;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public List<Long> getDosageTime() {
		return dosageTime;
	}

	public void setDosageTime(List<Long> dosageTime) {
		this.dosageTime = dosageTime;
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

	@Override
	public String toString() {
		return "DrugDosageAddEditRequest [id=" + id + ", dosage=" + dosage + ", dosageTime=" + dosageTime
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + "]";
	}
}
