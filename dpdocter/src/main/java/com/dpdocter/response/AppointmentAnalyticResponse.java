package com.dpdocter.response;

import java.util.List;

public class AppointmentAnalyticResponse {

	private long totalAppointments;
	
	List<AppointmentDeatilAnalyticResponse> appointments;

	public long getTotalAppointments() {
		return totalAppointments;
	}

	public void setTotalAppointments(long count) {
		this.totalAppointments = count;
	}

	public List<AppointmentDeatilAnalyticResponse> getAppointments() {
		return appointments;
	}

	public void setAppointments(List<AppointmentDeatilAnalyticResponse> appointments) {
		this.appointments = appointments;
	}

	@Override
	public String toString() {
		return "AppointmentAnalyticResponse [totalAppointments=" + totalAppointments + ", appointments=" + appointments
				+ "]";
	}
}
