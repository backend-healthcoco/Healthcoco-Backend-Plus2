package com.dpdocter.request;

import com.dpdocter.beans.AppointmentSlot;

public class DoctorAppointmentSlotAddEditRequest {

	private String id;

	private String doctorId;

	private String locationId;

	private AppointmentSlot appointmentSlot;

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

	public AppointmentSlot getAppointmentSlot() {
		return appointmentSlot;
	}

	public void setAppointmentSlot(AppointmentSlot appointmentSlot) {
		this.appointmentSlot = appointmentSlot;
	}

	@Override
	public String toString() {
		return "DoctorAppointmentSlotAddEditRequest [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", appointmentSlot=" + appointmentSlot + "]";
	}
}
