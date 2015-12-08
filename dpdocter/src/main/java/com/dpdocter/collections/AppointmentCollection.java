package com.dpdocter.collections;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Timing;

@Document(collection = "appointment_cl")
public class AppointmentCollection extends GenericCollection{

	@Id
    private String id;

	@Field
    private String userLocationId;
	
	@Field
    private String appointmentId;
	
	@Field
    private Timing time;
	
	@Field
    private String patientId;
	
	@Field
    private Boolean isConfirmed = false;
	
	@Field
    private Boolean isCanceled = false;
	
	@Field
    private Boolean isReschduled = false;
	
	@Field
    private Boolean isEvent = false;
	
	@Field
    private Boolean isCalenderBlocked = false;

	@Field
	private Date date;
	
	@Field
    private int day;
	
	@Field
    private int week;
	
	@Field
    private int month;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserLocationId() {
		return userLocationId;
	}

	public void setUserLocationId(String userLocationId) {
		this.userLocationId = userLocationId;
	}

	public Timing getTime() {
		return time;
	}

	public void setTime(Timing time) {
		this.time = time;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Boolean getIsConfirmed() {
		return isConfirmed;
	}

	public void setIsConfirmed(Boolean isConfirmed) {
		this.isConfirmed = isConfirmed;
	}

	public Boolean getIsCanceled() {
		return isCanceled;
	}

	public void setIsCanceled(Boolean isCanceled) {
		this.isCanceled = isCanceled;
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

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public Boolean getIsEvent() {
		return isEvent;
	}

	public void setIsEvent(Boolean isEvent) {
		this.isEvent = isEvent;
	}

	public Boolean getIsCalenderBlocked() {
		return isCalenderBlocked;
	}

	public void setIsCalenderBlocked(Boolean isCalenderBlocked) {
		this.isCalenderBlocked = isCalenderBlocked;
	}

	@Override
	public String toString() {
		return "AppointmentCollection [id=" + id + ", userLocationId=" + userLocationId + ", appointmentId="
				+ appointmentId + ", time=" + time + ", patientId=" + patientId + ", isConfirmed=" + isConfirmed
				+ ", isCanceled=" + isCanceled + ", isReschduled=" + isReschduled + ", isEvent=" + isEvent
				+ ", isCalenderBlocked=" + isCalenderBlocked + ", date=" + date + ", day=" + day + ", week=" + week
				+ ", month=" + month + "]";
	}
}
