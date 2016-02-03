package com.dpdocter.beans;

public class TestAndRecordData {

    private String labTestId;

    private String recordId;

	public TestAndRecordData(String labTestId, String recordId) {
		this.labTestId = labTestId;
		this.recordId = recordId;
	}

	public String getLabTestId() {
		return labTestId;
	}

	public void setLabTestId(String labTestId) {
		this.labTestId = labTestId;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	@Override
	public String toString() {
		return "TestAndRecordData [labTestId=" + labTestId + ", recordId=" + recordId + "]";
	}
}
