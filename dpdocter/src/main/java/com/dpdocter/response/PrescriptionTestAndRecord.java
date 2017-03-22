package com.dpdocter.response;

import java.util.List;

public class PrescriptionTestAndRecord {

	private Boolean isPatientRegistered;
	
	private String patientId;
	
	private String localPatientName;
	
	private String mobileNumber;
	
    private String uniqueEmrId;

    private List<TestAndRecordDataResponse> tests;

    public Boolean getIsPatientRegistered() {
		return isPatientRegistered;
	}

	public void setIsPatientRegistered(Boolean isPatientRegistered) {
		this.isPatientRegistered = isPatientRegistered;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public List<TestAndRecordDataResponse> getTests() {
	return tests;
    }

    public void setTests(List<TestAndRecordDataResponse> tests) {
	this.tests = tests;
    }

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String toString() {
		return "PrescriptionTestAndRecord [isPatientRegistered=" + isPatientRegistered + ", patientId=" + patientId
				+ ", localPatientName=" + localPatientName + ", mobileNumber=" + mobileNumber + ", uniqueEmrId="
				+ uniqueEmrId + ", tests=" + tests + "]";
	}

}
