package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;

public class Event extends GenericCollection{

	private String id;

    private String subject;

    private String description;

    private String userLocationId;

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

	public String getUserLocationId() {
		return userLocationId;
	}

	public void setUserLocationId(String userLocationId) {
		this.userLocationId = userLocationId;
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
		return "Event [id=" + id + ", subject=" + subject + ", description=" + description + ", userLocationId="
				+ userLocationId + ", time=" + time + ", isCalenderBlocked=" + isCalenderBlocked + ", date=" + date
				+ "]";
	}
}
