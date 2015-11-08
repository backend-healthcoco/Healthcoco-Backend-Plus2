package com.dpdocter.request;

public class ChangeRecordLabelDescriptionRequest {
	
	private String recordId;

    private String label;

    private String description;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "ChangeRecordLabelDescriptionRequest [recordId=" + recordId + ", label=" + label + ", description="
				+ description + "]";
	}
}
