package com.dpdocter.request;

public class ChangeRecordLabelRequest {
	private String recordId;

	private String label;

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "ChangeRecordLabelRequest [recordId=" + recordId + ", label=" + label + "]";
	}

}
