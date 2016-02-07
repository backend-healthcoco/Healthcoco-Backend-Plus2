package com.dpdocter.response;

import java.util.List;

public class PrescriptionTestAndRecord {

    private String uniqueId;

    private List<TestAndRecordDataResponse> tests;

    public String getUniqueId() {
	return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
	this.uniqueId = uniqueId;
    }

    public List<TestAndRecordDataResponse> getTests() {
	return tests;
    }

    public void setTests(List<TestAndRecordDataResponse> tests) {
	this.tests = tests;
    }

    @Override
    public String toString() {
	return "PrescriptionTestAndRecord [uniqueId=" + uniqueId + ", tests=" + tests + "]";
    }
}
