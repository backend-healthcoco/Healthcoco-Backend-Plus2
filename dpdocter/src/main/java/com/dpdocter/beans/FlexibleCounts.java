package com.dpdocter.beans;

import java.util.List;

public class FlexibleCounts {
    private String doctorId;

    private String hospitalId;

    private String locationId;

    private String patientId;

    private List<Count> counts;

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

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public List<Count> getCounts() {
	return counts;
    }

    public void setCounts(List<Count> counts) {
	this.counts = counts;
    }

    @Override
    public String toString() {
	return "FlexibleCounts [doctorId=" + doctorId + ", hospitalId=" + hospitalId + ", locationId=" + locationId + ", patientId=" + patientId + ", counts="
		+ counts + "]";
    }

}
