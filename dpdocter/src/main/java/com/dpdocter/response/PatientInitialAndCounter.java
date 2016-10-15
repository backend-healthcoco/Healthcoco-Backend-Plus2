package com.dpdocter.response;

public class PatientInitialAndCounter {

    private String locationId;

    private String patientInitial;

    private int patientCounter;

    public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public String getPatientInitial() {
	return patientInitial;
    }

    public void setPatientInitial(String patientInitial) {
	this.patientInitial = patientInitial;
    }

    public int getPatientCounter() {
	return patientCounter;
    }

    public void setPatientCounter(int patientCounter) {
	this.patientCounter = patientCounter;
    }

    @Override
    public String toString() {
	return "PatientInitialAndCounter [locationId=" + locationId + ", patientInitial=" + patientInitial + ", patientCounter="
		+ patientCounter + "]";
    }
}
