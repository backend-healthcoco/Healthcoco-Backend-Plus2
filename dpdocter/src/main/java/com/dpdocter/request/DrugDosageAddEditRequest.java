package com.dpdocter.request;

import com.dpdocter.beans.DosageWithTime;

public class DrugDosageAddEditRequest {

    private String id;

    private DosageWithTime dosage;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public DosageWithTime getDosage() {
	return dosage;
    }

    public void setDosage(DosageWithTime dosage) {
	this.dosage = dosage;
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
	return "DrugDosageAddEditRequest [id=" + id + ", dosage=" + dosage + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
		+ hospitalId + "]";
    }

}
