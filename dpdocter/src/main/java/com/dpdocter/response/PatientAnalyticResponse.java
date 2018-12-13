package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.GroupPatientCount;
import com.dpdocter.beans.PatientAnalyticData;

public class PatientAnalyticResponse {
	private int day;

	private int month;

	private int year;

	private int week;

	private int count;

	private String city;

	private Date date;

	private List<PatientAnalyticData> patients;

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getYear() {
		return year;
	}

	public int getCount() {
		return count;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public List<PatientAnalyticData> getPatients() {
		return patients;
	}

	public void setPatients(List<PatientAnalyticData> patients) {
		this.patients = patients;
	}

}
