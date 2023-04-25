package com.dpdocter.response;

public class OPDDiagnosticTestResponse {
	public OPDDiagnosticTestResponse(String id, String testName, String recordId) {
		super();
		this.id = id;
		this.testName = testName;
		this.recordId = recordId;
	}

	private String id;

	private String testName;

	private String recordId;

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

}
