package com.dpdocter.enums;

public enum VaccineStatus {

	GIVEN("GIVEN"), PLANNED("PLANNED");

	private String status;

	public String getStatus() {
		return status;
	}

	private VaccineStatus(String status) {
		this.status = status;
	}

}
