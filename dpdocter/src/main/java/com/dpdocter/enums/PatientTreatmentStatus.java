package com.dpdocter.enums;

public enum PatientTreatmentStatus {
    NOT_STARTED("NOT_STARTED"), IN_PROGRESS("IN_PROGRESS"), COMPLETED("COMPLETED");

    private String treamentStatus;

    private PatientTreatmentStatus(String treamentStatus) {
	this.treamentStatus = treamentStatus;
    }

    public String getTreamentStatus() {
	return treamentStatus;
    }

}
