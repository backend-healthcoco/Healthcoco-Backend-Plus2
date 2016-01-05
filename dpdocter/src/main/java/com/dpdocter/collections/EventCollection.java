package com.dpdocter.collections;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.WorkingHours;

@Document(collection = "event_cl")
public class EventCollection extends GenericCollection{

	@Id
    private String id;

	@Field
    private String subject;

	@Field
    private String description;

	@Field
    private String userLocationId;

    @Field
    private WorkingHours time;

	@Field
    private Boolean isCalenderBlocked = false;

    @Field
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
		return "EventCollection [id=" + id + ", subject=" + subject + ", description=" + description
				+ ", userLocationId=" + userLocationId + ", time=" + time + ", isCalenderBlocked=" + isCalenderBlocked
				+ ", date=" + date + "]";
	}

}
