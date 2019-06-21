package com.dpdocter.beans;

public class LabReportJasperDetail {
	private int no;

	private String patientName;

	private String gender;

	private String test;

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	@Override
	public String toString() {
		return "LabReportJasperDetail [no=" + no + ", patientName=" + patientName + ", gender=" + gender + ", test="
				+ test + "]";
	}

}
