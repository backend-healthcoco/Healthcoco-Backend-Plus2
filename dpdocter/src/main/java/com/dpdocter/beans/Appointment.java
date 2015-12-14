package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.Day;

public class Appointment extends GenericCollection {

    private String id;

    private Timing time;

    private String patientId;

    private Boolean isConfirmed = false;

    private Boolean isCanceled = false;

    private Boolean isReschduled = false;

    private Date date;

    private Day day;

    private String week;

    private String month;

    private String appointmentId;

    private Boolean isEvent = false;

    private Boolean isCalenderBlocked = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
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

    public Day getDay() {
	return day;
    }

    public void setDay(Day day) {
	this.day = day;
    }

    public String getWeek() {
	return week;
    }

    public void setWeek(String week) {
	this.week = week;
    }

    public String getMonth() {
	return month;
    }

    public void setMonth(String month) {
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
	return "Appointment [id=" + id + ", time=" + time + ", patientId=" + patientId + ", isConfirmed=" + isConfirmed + ", isCanceled=" + isCanceled
		+ ", isReschduled=" + isReschduled + ", date=" + date + ", day=" + day + ", week=" + week + ", month=" + month + ", appointmentId="
		+ appointmentId + ", isEvent=" + isEvent + ", isCalenderBlocked=" + isCalenderBlocked + "]";
    }
}
