package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

public class PrescriptionAnalyticDetail {

	private String id;
	private String firstName;
	private String localPatientName;
	private String uniqueEmrId;
	private Date createdTime;
	private List<PrescriptionItemDetail> durgs;
	private List<DiagnosticTest> tests;
	private String doctorName;
	private String advice;
	public String getAdvice() {
		return advice;
	}
	public void setAdvice(String advice) {
		this.advice = advice;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLocalPatientName() {
		return localPatientName;
	}
	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}
	public String getUniqueEmrId() {
		return uniqueEmrId;
	}
	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public List<PrescriptionItemDetail> getDurgs() {
		return durgs;
	}
	public void setDurgs(List<PrescriptionItemDetail> durgs) {
		this.durgs = durgs;
	}
	public List<DiagnosticTest> getTests() {
		return tests;
	}
	public void setTests(List<DiagnosticTest> tests) {
		this.tests = tests;
	}
	public String getDoctorName() {
		return doctorName;
	}
	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	
	
}
