package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.enums.VisitedFor;

public class PatientTrack {
    private String id;

    private String patientId;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private Date visitedTime;

    private VisitedFor visitedFor;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
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

    public Date getVisitedTime() {
	return visitedTime;
    }

    public void setVisitedTime(Date visitedTime) {
	this.visitedTime = visitedTime;
    }

    public VisitedFor getVisitedFor() {
	return visitedFor;
    }

    public void setVisitedFor(VisitedFor visitedFor) {
	this.visitedFor = visitedFor;
    }

    @Override
    public String toString() {
	return "PatientTrack [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
		+ ", visitedTime=" + visitedTime + ", visitedFor=" + visitedFor + "]";
    }

}
