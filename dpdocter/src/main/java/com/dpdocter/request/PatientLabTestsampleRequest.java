package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.LabTestSample;

public class PatientLabTestsampleRequest {

	private String id;
	private String uid;
	private String patientName;
	private String mobileNumber;
	private Integer age;
	private String gender;
	private List<LabTestSample> labTestSamples;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public List<LabTestSample> getLabTestSamples() {
		return labTestSamples;
	}

	public void setLabTestSamples(List<LabTestSample> labTestSamples) {
		this.labTestSamples = labTestSamples;
	}

	@Override
	public String toString() {
		return "PatientLabTestsampleRequest [patientName=" + patientName + ", mobileNumber=" + mobileNumber + ", age="
				+ age + ", gender=" + gender + ", labTestSamples=" + labTestSamples + "]";
	}
}