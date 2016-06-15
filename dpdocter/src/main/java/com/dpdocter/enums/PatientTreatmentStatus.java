package com.dpdocter.enums;

public enum PatientTreatmentStatus {
    NOT_STARTED("Not Started"), IN_PROGRESS("In Progress"), COMPLETED("Completed");

    private String treamentStatus;

    private PatientTreatmentStatus(String treamentStatus) {
	this.treamentStatus = treamentStatus;
    }

    public String getTreamentStatus() {
	return treamentStatus;
    }

}
