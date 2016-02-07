package com.dpdocter.beans;

import java.util.Date;

public class PatientQueue {

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private PatientCard patient;

    private Date date;

    private String sequenceNo;

    private String appointmentId;

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

    public PatientCard getPatient() {
	return patient;
    }

    public void setPatient(PatientCard patient) {
	this.patient = patient;
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public String getSequenceNo() {
	return sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
	this.sequenceNo = sequenceNo;
    }

    public String getAppointmentId() {
	return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
	this.appointmentId = appointmentId;
    }

    @Override
    public String toString() {
	return "PatientQueue [doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", patient=" + patient + ", date=" + date
		+ ", sequenceNo=" + sequenceNo + ", appointmentId=" + appointmentId + "]";
    }
}
