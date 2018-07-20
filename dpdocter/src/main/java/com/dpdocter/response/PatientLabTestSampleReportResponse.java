package com.dpdocter.response;

import java.util.List;

public class PatientLabTestSampleReportResponse {

	private String id;
	private String uid;
	private String patientName;
	private String mobileNumber;
	private Integer age;
	private String gender;
	private List<LabTestSampleReportResponse> labTestSamples;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<LabTestSampleReportResponse> getLabTestSamples() {
		return labTestSamples;
	}

	public void setLabTestSamples(List<LabTestSampleReportResponse> labTestSamples) {
		this.labTestSamples = labTestSamples;
	}

	@Override
	public String toString() {
		return "PatientLabTestSample [patientName=" + patientName + ", mobileNumber=" + mobileNumber + ", age=" + age
				+ ", gender=" + gender + ", labTestSamples=" + labTestSamples + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}