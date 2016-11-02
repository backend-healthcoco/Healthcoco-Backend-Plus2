package com.dpdocter.response;

public class PatientInitialAndCounter {

	private String doctorId;
	
    private String locationId;

    private String patientInitial;

    private int patientCounter;

    public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

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
		return "PatientInitialAndCounter [doctorId=" + doctorId + ", locationId=" + locationId + ", patientInitial="
				+ patientInitial + ", patientCounter=" + patientCounter + "]";
	}
}
