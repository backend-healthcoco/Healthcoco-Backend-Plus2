package com.dpdocter.request;

public class ChangeRecordLabelDescriptionRequest {

	private String recordId;

	private String label;

	private String explanation;

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

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	@Override
	public String toString() {
		return "ChangeRecordLabelDescriptionRequest [recordId=" + recordId + ", label=" + label + ", explanation="
				+ explanation + "]";
	}

}
