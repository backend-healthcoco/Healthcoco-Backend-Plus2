package com.dpdocter.collections;

import java.util.Calendar;
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
    private String userLocationId;

    @Field
    private String appointmentId;

    @Field
    private WorkingHours time;

    @Field
    private String patientId;

    @Field
    private AppointmentState state = AppointmentState.NEW;

    @Field
    private Boolean isReschduled = false;

    @Field
    private Date date;

    @Field
    private int day;

    @Field
    private int week;

    @Field
    private int month;

    @Field
    private int year;

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
		if(this.date != null){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			this.week = calendar.get(Calendar.WEEK_OF_MONTH);
			this.day = calendar.get(Calendar.DAY_OF_MONTH);
			this.month = calendar.get(Calendar.MONTH);
			this.year = calendar.get(Calendar.YEAR);
		}
	}

	public int getDay() {
		if(this.date != null){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return calendar.get(Calendar.DAY_OF_MONTH);
		}
		else return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getWeek() {
		if(this.date != null){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return calendar.get(Calendar.WEEK_OF_MONTH);
		}
		else return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public int getMonth() {
		if(this.date != null){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return calendar.get(Calendar.MONTH);
		}
		else return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		if(this.date != null){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return calendar.get(Calendar.YEAR);
		}
		else return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return "AppointmentCollection [id=" + id + ", userLocationId=" + userLocationId + ", appointmentId="
				+ appointmentId + ", time=" + time + ", patientId=" + patientId + ", state=" + state + ", isReschduled="
				+ isReschduled + ", date=" + date + ", day=" + day + ", week=" + week + ", month=" + month + ", year="
				+ year + "]";
	}
}
