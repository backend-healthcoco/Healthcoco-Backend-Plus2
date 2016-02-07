package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;

public class Appointment extends GenericCollection {

    private String id;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private WorkingHours time;

    private PatientCard patient;

    private AppointmentState state;

    private Boolean isReschduled = false;

    private Date date;

    private String appointmentId;

    private String subject;

    private String description;

    private AppointmentType type;

    private Boolean isCalenderBlocked = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public WorkingHours getTime() {
	return time;
    }

    public void setTime(WorkingHours time) {
	this.time = time;
    }

    public PatientCard getPatient() {
	return patient;
    }

    public void setPatient(PatientCard patient) {
	this.patient = patient;
    }

    public AppointmentState getState() {
	return state;
    }

    public void setState(AppointmentState state) {
	this.state = state;
    }

    public Boolean getIsReschduled() {
	return isReschduled;
    }

    public void setIsReschduled(Boolean isReschduled) {
	this.isReschduled = isReschduled;
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public String getAppointmentId() {
	return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
	this.appointmentId = appointmentId;
    }

    public String getSubject() {
	return subject;
    }

    public void setSubject(String subject) {
	this.subject = subject;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public AppointmentType getType() {
	return type;
    }

    public void setType(AppointmentType type) {
	this.type = type;
    }

    public Boolean getIsCalenderBlocked() {
	return isCalenderBlocked;
    }

    public void setIsCalenderBlocked(Boolean isCalenderBlocked) {
	this.isCalenderBlocked = isCalenderBlocked;
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
	return "Appointment [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", time=" + time
		+ ", patient=" + patient + ", state=" + state + ", isReschduled=" + isReschduled + ", date=" + date + ", appointmentId=" + appointmentId
		+ ", subject=" + subject + ", description=" + description + ", type=" + type + ", isCalenderBlocked=" + isCalenderBlocked + "]";
    }
}
