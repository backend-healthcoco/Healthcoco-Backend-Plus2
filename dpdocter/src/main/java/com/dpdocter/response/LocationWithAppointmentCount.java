package com.dpdocter.response;

import java.util.List;

public class LocationWithAppointmentCount {

	private String locationId;
	
	private long noOfAppointments;
	
	private List<DoctorWithAppointmentCount> doctors;

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public long getNoOfAppointments() {
		return noOfAppointments;
	}

	public void setNoOfAppointments(long noOfAppointments) {
		this.noOfAppointments = noOfAppointments;
	}

	public List<DoctorWithAppointmentCount> getDoctors() {
		return doctors;
	}

	public void setDoctors(List<DoctorWithAppointmentCount> doctors) {
		this.doctors = doctors;
	}

	@Override
	public String toString() {
		return "LocationWithAppointmentCount [locationId=" + locationId + ", noOfAppointments=" + noOfAppointments
				+ ", doctors=" + doctors + "]";
	}
}
