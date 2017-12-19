package com.dpdocter.beans;

import java.util.List;

import org.bson.types.ObjectId;

public class PatientLabTestItem {
	private String uid;;
	private String patientName;
	private String mobileNumber;
	private Integer age;
	private String gender;
	private List<ObjectId> labTestSampleIds;

	public String getPatientName() {
		return patientName;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public List<ObjectId> getLabTestSampleIds() {
		return labTestSampleIds;
	}

	public void setLabTestSampleIds(List<ObjectId> labTestSampleIds) {
		this.labTestSampleIds = labTestSampleIds;
	}

	@Override
	public String toString() {
		return "PatientLabTestItem [patientName=" + patientName + ", mobileNumber=" + mobileNumber + ", age=" + age
				+ ", gender=" + gender + ", labTestSampleIds=" + labTestSampleIds + "]";
	}

}
