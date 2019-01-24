package com.dpdocter.beans;

import java.util.List;

public class PrescriptionDownloadData {

	private String doctorName;

	private String patientId;
	
	private String patientName;
	
	private String drugName;

	private String drugType;

	private String duration;

	private String dosage;

	private String explanation;

	private String direction;

	private String instructions;

	private List<TestAndRecordData> diagnosticTests;

	private String tests;

	private String advice;
	
	private String date;

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public String getDrugType() {
		return drugType;
	}

	public void setDrugType(String drugType) {
		this.drugType = drugType;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public List<TestAndRecordData> getDiagnosticTests() {
		return diagnosticTests;
	}

	public void setDiagnosticTests(List<TestAndRecordData> diagnosticTests) {
		this.diagnosticTests = diagnosticTests;
	}

	public String getTests() {
		return tests;
	}

	public void setTests(String tests) {
		this.tests = tests;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "PrescriptionDownloadData [doctorName=" + doctorName + ", patientId=" + patientId + ", patientName="
				+ patientName + ", drugName=" + drugName + ", drugType=" + drugType + ", duration=" + duration
				+ ", dosage=" + dosage + ", explanation=" + explanation + ", direction=" + direction + ", instructions="
				+ instructions + ", diagnosticTests=" + diagnosticTests + ", tests=" + tests + ", advice=" + advice
				+ ", date=" + date + "]";
	}
}
