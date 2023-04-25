package com.dpdocter.request;

import java.util.List;

public class DoctorAppointmentNumbersAddEditRequest {

	private String id;

	private String doctorId;

	private String locationId;

	private List<String> appointmentBookingNumber;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public List<String> getAppointmentBookingNumber() {
		return appointmentBookingNumber;
	}

	public void setAppointmentBookingNumber(List<String> appointmentBookingNumber) {
		this.appointmentBookingNumber = appointmentBookingNumber;
	}

	@Override
	public String toString() {
		return "DoctorAppointmentNumbersAddEditRequest [id=" + id + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", appointmentBookingNumber=" + appointmentBookingNumber + "]";
	}
}
