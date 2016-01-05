package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.Appointment;

public class ClinicAppointmentsResponse {

	private String doctorId;
	
	private List<Appointment> appointments;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public List<Appointment> getAppointments() {
		return appointments;
	}

	public void setAppointments(List<Appointment> appointments) {
		this.appointments = appointments;
	}

	@Override
	public String toString() {
		return "ClinicAppointmentsResponse [doctorId=" + doctorId + ", appointments="+ appointments + "]";
	}
}
