package com.dpdocter.beans;

import java.util.Date;

public class PatientAnalyticData {
	private String id;
	private String firstName;
	private String localPatientName;
	private String PID;
	private Long registrationDate;
	private Date createdTime;
	private WorkingHours visitedTime;

	public String getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public String getPID() {
		return PID;
	}

	public Long getRegistrationDate() {
		return registrationDate;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public void setPID(String pID) {
		PID = pID;
	}

	public void setRegistrationDate(Long registrationDate) {
		this.registrationDate = registrationDate;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public WorkingHours getVisitedTime() {
		return visitedTime;
	}

	public void setVisitedTime(WorkingHours visitedTime) {
		this.visitedTime = visitedTime;
	}

}
