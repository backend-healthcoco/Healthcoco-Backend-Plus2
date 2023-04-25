package com.dpdocter.enums;

public enum PatientTreatmentService {

	SERVICE("SERVICE"), SERVICECOST("SERVICECOST"), SERVICEBYSPECIALITY("SERVICEBYSPECIALITY");

	private String type;

	private PatientTreatmentService(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
