package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

public class PrescriptionAnalyticDetail {

	private String id;
	private String firstName;
	private String localPatientName;
	private String uniqueEmrId;
	private Date createdTime;
	private List<Drug> drugs;
	private List<DiagnosticTest> diagnosticTests;
	private List<TestAndRecordData> tests;
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

	public List<Drug> getDrugs() {
		return drugs;
	}

	public void setDrugs(List<Drug> drugs) {
		this.drugs = drugs;
	}

	public List<DiagnosticTest> getDiagnosticTests() {
		return diagnosticTests;
	}

	public void setDiagnosticTests(List<DiagnosticTest> diagnosticTests) {
		this.diagnosticTests = diagnosticTests;
	}

	public List<TestAndRecordData> getTests() {
		return tests;
	}

	public void setTests(List<TestAndRecordData> tests) {
		this.tests = tests;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

}
