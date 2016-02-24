package com.dpdocter.collections;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.WorkingHours;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;

@Document(collection = "appointment_cl")
public class AppointmentCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String subject;

    @Field
    private String description;

    @Field
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private String appointmentId;

    @Field
    private WorkingHours time;

    @Field
    private String patientId;

    @Field
    private AppointmentState state = AppointmentState.NEW;

    @Field
    private AppointmentType type = AppointmentType.APPOINTMENT;

    @Field
    private Boolean isReschduled = false;

    @Field
    private Date date;

    @Field
    private Boolean isCalenderBlocked = false;

    @Field
    private Boolean isFeedbackAvailable = false;
    
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
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

    public String getAppointmentId() {
	return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
	this.appointmentId = appointmentId;
    }

    public WorkingHours getTime() {
	return time;
    }

    public void setTime(WorkingHours time) {
	this.time = time;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public AppointmentState getState() {
	return state;
    }

    public void setState(AppointmentState state) {
	this.state = state;
    }

    public AppointmentType getType() {
	return type;
    }

    public void setType(AppointmentType type) {
	this.type = type;
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

	public Boolean getIsFeedbackAvailable() {
		return isFeedbackAvailable;
	}

	public void setIsFeedbackAvailable(Boolean isFeedbackAvailable) {
		this.isFeedbackAvailable = isFeedbackAvailable;
	}

	@Override
	public String toString() {
		return "AppointmentCollection [id=" + id + ", subject=" + subject + ", description=" + description
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", appointmentId=" + appointmentId + ", time=" + time + ", patientId=" + patientId + ", state="
				+ state + ", type=" + type + ", isReschduled=" + isReschduled + ", date=" + date
				+ ", isCalenderBlocked=" + isCalenderBlocked + ", isFeedbackAvailable=" + isFeedbackAvailable + "]";
	}
}
