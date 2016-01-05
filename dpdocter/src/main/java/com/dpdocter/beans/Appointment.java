package com.dpdocter.beans;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.Day;

public class Appointment extends GenericCollection {

    private String id;

    private String time;
    
    private String patientId;

    private AppointmentState state;

    private Boolean isReschduled = false;

    private Date date;

    private String appointmentId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
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

	@Override
	public String toString() {
		return "Appointment [id=" + id + ", time=" + time + ", patientId=" + patientId + ", state=" + state
				+ ", isReschduled=" + isReschduled + ", date=" + date + ", appointmentId=" + appointmentId + "]";
	}
}
