package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TestAndRecordData {

    private String testId;

    private String recordId;

    public TestAndRecordData(String testId, String recordId) {
		this.testId = testId;
		this.recordId = recordId;
	}

	public String getTestId() {
		return testId;
	}


	public void setTestId(String testId) {
		this.testId = testId;
	}


	public String getRecordId() {
	return recordId;
    }

    public void setRecordId(String recordId) {
	this.recordId = recordId;
    }

	@Override
	public String toString() {
		return "TestAndRecordData [testId=" + testId + ", recordId=" + recordId + "]";
	}
}
