package com.dpdocter.response;

import java.util.List;

public class AppointmentAnalyticResponse {

	private long totalAppointments;
	
	List<AppointmentDetailAnalyticResponse> appointments;

	public long getTotalAppointments() {
		return totalAppointments;
	}

	public void setTotalAppointments(long count) {
		this.totalAppointments = count;
	}

	public List<AppointmentDetailAnalyticResponse> getAppointments() {
		return appointments;
	}

	public void setAppointments(List<AppointmentDetailAnalyticResponse> appointments) {
		this.appointments = appointments;
	}

	@Override
	public String toString() {
		return "AppointmentAnalyticResponse [totalAppointments=" + totalAppointments + ", appointments=" + appointments
				+ "]";
	}
}
