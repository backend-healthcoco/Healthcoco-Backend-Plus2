package com.dpdocter.beans;

import java.util.Date;

public class PatientAnalyticData {
	private String id;
	private String firstName;
	private String localPatientName;
	private String pid;
	private Long registrationDate;
	private Date createdTime;
	private WorkingHours visitedTime;
	private String PNUM;
	
	public String getId() {
		return id;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLocalPatientName() {
		return localPatientName;
	}
	
	public Long getRegistrationDate() {
		return registrationDate;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public WorkingHours getVisitedTime() {
		return visitedTime;
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
	
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public void setRegistrationDate(Long registrationDate) {
		this.registrationDate = registrationDate;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public void setVisitedTime(WorkingHours visitedTime) {
		this.visitedTime = visitedTime;
	}
	public String getPNUM() {
		return PNUM;
	}
	public void setPNUM(String pNUM) {
		PNUM = pNUM;
	}
	@Override
	public String toString() {
		return "PatientAnalyticData [id=" + id + ", firstName=" + firstName + ", localPatientName=" + localPatientName
				+ ", pid=" + pid + ", registrationDate=" + registrationDate + ", createdTime=" + createdTime
				+ ", visitedTime=" + visitedTime + ", PNUM=" + PNUM + "]";
	}

	
}
