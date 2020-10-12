package com.dpdocter.request;

public class DoctorAmountRequest {

	private String doctorId;
	
	private String patientId;
	
	private String locationId;
	
	private String hospitalId;
	
	private Double dueAmount = 0.0;
	
	private Double remainingAdvanceAmount = 0.0;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Double getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(Double dueAmount) {
		this.dueAmount = dueAmount;
	}

	public Double getRemainingAdvanceAmount() {
		return remainingAdvanceAmount;
	}

	public void setRemainingAdvanceAmount(Double remainingAdvanceAmount) {
		this.remainingAdvanceAmount = remainingAdvanceAmount;
	}
	
	
}
