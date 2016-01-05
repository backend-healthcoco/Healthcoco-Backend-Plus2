package com.dpdocter.request;

import java.util.Date;

import com.dpdocter.beans.WorkingHours;

public class EventRequest {

	private String id;

    private String subject;

    private String description;

    private String locationId;
    
    private String doctorId;

    private WorkingHours time;

    private Boolean isCalenderBlocked = false;

    private Date date;

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

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public Boolean getIsCalenderBlocked() {
		return isCalenderBlocked;
	}

	public void setIsCalenderBlocked(Boolean isCalenderBlocked) {
		this.isCalenderBlocked = isCalenderBlocked;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "EventRequest [id=" + id + ", subject=" + subject + ", description=" + description + ", locationId="
				+ locationId + ", doctorId=" + doctorId + ", time=" + time + ", isCalenderBlocked=" + isCalenderBlocked
				+ ", date=" + date + "]";
	}
}
