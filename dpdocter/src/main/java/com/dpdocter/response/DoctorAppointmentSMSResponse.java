package com.dpdocter.response;

import java.util.List;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserDeviceCollection;

public class DoctorAppointmentSMSResponse {

	private int noOfAppointments = 0;
	
	private UserCollection doctor;
	
	private String message;

	private List<UserDeviceCollection> userDevices ;
	
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

	public List<UserDeviceCollection> getUserDevices() {
		return userDevices;
	}

	public void setUserDevices(List<UserDeviceCollection> userDevices) {
		this.userDevices = userDevices;
	}

	@Override
	public String toString() {
		return "DoctorAppointmentSMSResponse [noOfAppointments=" + noOfAppointments + ", doctor=" + doctor
				+ ", message=" + message + ", userDevices=" + userDevices + "]";
	}

}
