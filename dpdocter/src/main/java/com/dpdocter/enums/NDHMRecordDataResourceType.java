package com.dpdocter.enums;

public enum NDHMRecordDataResourceType {

	MedicationRequest("MedicationRequest");

	private String resourceType;

	public String getResourceType() {
		return resourceType;
	}

	private NDHMRecordDataResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
}
