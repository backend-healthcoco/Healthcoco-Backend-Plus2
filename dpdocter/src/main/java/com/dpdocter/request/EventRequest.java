package com.dpdocter.request;

import java.util.Date;

import com.dpdocter.beans.WorkingHours;
import com.dpdocter.enums.AppointmentState;

public class EventRequest {

    private String id;

    private AppointmentState state;

    private String subject;

    private String explanation;

    private String locationId;

    private String doctorId;

    private WorkingHours time;

    private Boolean isCalenderBlocked = false;

    private Date fromDate;
    
    private Date toDate;

    private Boolean isAllDayEvent = false;
    
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public AppointmentState getState() {
	return state;
    }

    public void setState(AppointmentState state) {
	this.state = state;
    }

    public String getSubject() {
	return subject;
    }

    public void setSubject(String subject) {
	this.subject = subject;
    }

    public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
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

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Boolean getIsAllDayEvent() {
		return isAllDayEvent;
	}

	public void setIsAllDayEvent(Boolean isAllDayEvent) {
		this.isAllDayEvent = isAllDayEvent;
	}

	@Override
	public String toString() {
		return "EventRequest [id=" + id + ", state=" + state + ", subject=" + subject + ", explanation=" + explanation
				+ ", locationId=" + locationId + ", doctorId=" + doctorId + ", time=" + time + ", isCalenderBlocked="
				+ isCalenderBlocked + ", fromDate=" + fromDate + ", toDate=" + toDate + ", isAllDayEvent="
				+ isAllDayEvent + "]";
	}

}
