package com.dpdocter.request;

import java.util.Date;

import com.dpdocter.beans.Timing;
import com.dpdocter.enums.AppointmentType;

public class AppoinmentRequest {

	private String id;
	
	private AppointmentType type;
	
	private String doctorId;
	
	private String locationId;
	
	private String patientId;
	
	private Timing time;
	
	private Date date;

	private Boolean isEvent = false;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AppointmentType getType() {
		return type;
	}

	public void setType(AppointmentType type) {
		this.type = type;
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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Timing getTime() {
		return time;
	}

	public void setTime(Timing time) {
		this.time = time;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Boolean getIsEvent() {
		return isEvent;
	}

	public void setIsEvent(Boolean isEvent) {
		this.isEvent = isEvent;
	}

	@Override
	public String toString() {
		return "AppoinmentRequest [id=" + id + ", type=" + type + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", patientId=" + patientId + ", time=" + time + ", date=" + date + ", isEvent=" + isEvent
				+ "]";
	}
}
