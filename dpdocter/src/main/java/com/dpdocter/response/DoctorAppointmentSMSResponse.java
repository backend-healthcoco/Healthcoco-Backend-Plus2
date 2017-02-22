package com.dpdocter.response;

import com.dpdocter.collections.UserCollection;

public class DoctorAppointmentSMSResponse {

	private int noOfAppointments = 0;
	
	private UserCollection doctor;
	
	private String message;

	public int getNoOfAppointments() {
		return noOfAppointments;
	}

	public void setNoOfAppointments(int noOfAppointments) {
		this.noOfAppointments = noOfAppointments;
	}

	public UserCollection getDoctor() {
		return doctor;
	}

	public void setDoctor(UserCollection doctor) {
		this.doctor = doctor;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "DoctorAppointmentSMSResponse [noOfAppointments=" + noOfAppointments + ", doctor=" + doctor
				+ ", message=" + message + "]";
	}
}
