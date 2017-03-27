package com.dpdocter.request;

public class AddEditEyePrescriptionRequest {

	private String visitId;

	private String patientId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String eyePrescriptionId;

	private AppointmentRequest appointmentRequest;

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getEyePrescriptionId() {
		return eyePrescriptionId;
	}

	public void setEyePrescriptionId(String eyePrescriptionId) {
		this.eyePrescriptionId = eyePrescriptionId;
	}

	public AppointmentRequest getAppointmentRequest() {
		return appointmentRequest;
	}

	public void setAppointmentRequest(AppointmentRequest appointmentRequest) {
		this.appointmentRequest = appointmentRequest;
	}

	@Override
	public String toString() {
		return "AddEditEyePrescriptionRequest [visitId=" + visitId + ", patientId=" + patientId + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", eyePrescriptionId="
				+ eyePrescriptionId + ", appointmentRequest=" + appointmentRequest + "]";
	}

}
