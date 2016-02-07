package com.dpdocter.response;

import com.dpdocter.beans.LabTest;

public class TestAndRecordDataResponse {

    private LabTest labTest;

    private String recordId;

    public TestAndRecordDataResponse(LabTest labTest, String recordId) {
	this.labTest = labTest;
	this.recordId = recordId;
    }

    public LabTest getLabTest() {
	return labTest;
    }

    public void setLabTest(LabTest labTest) {
	this.labTest = labTest;
    }

    public String getRecordId() {
	return recordId;
    }

    public void setRecordId(String recordId) {
	this.recordId = recordId;
    }

    @Override
    public String toString() {
	return "TestAndRecordDataResponse [labTest=" + labTest + ", recordId=" + recordId + "]";
    }
}
