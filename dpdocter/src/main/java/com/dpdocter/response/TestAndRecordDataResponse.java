package com.dpdocter.response;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.beans.DiagnosticTest;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TestAndRecordDataResponse {

	private DiagnosticTest test;

	private String recordId;
    
	public TestAndRecordDataResponse(DiagnosticTest test, String recordId) {
		this.test = test;
		this.recordId = recordId;
	}

	public TestAndRecordDataResponse() {

	}

	public DiagnosticTest getTest() {
		return test;
	}

	public void setTest(DiagnosticTest test) {
		this.test = test;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	@Override
	public String toString() {
		return "TestAndRecordDataResponse [test=" + test + ", recordId=" + recordId + "]";
	}
}
