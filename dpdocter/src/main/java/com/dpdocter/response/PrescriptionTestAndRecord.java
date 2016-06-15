package com.dpdocter.response;

import java.util.List;

public class PrescriptionTestAndRecord {

    private String uniqueEmrId;

    private List<TestAndRecordDataResponse> tests;

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

	@Override
	public String toString() {
		return "PrescriptionTestAndRecord [uniqueEmrId=" + uniqueEmrId + ", tests=" + tests + "]";
	}
}
