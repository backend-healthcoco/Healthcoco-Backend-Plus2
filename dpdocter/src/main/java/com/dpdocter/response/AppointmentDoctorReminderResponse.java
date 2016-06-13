package com.dpdocter.response;

public class AppointmentDoctorReminderResponse {

	private String doctorId;
	
	private int total;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "AppointmentDoctorReminderResponse [doctorId=" + doctorId + ", total=" + total + "]";
	}
	
	
}
