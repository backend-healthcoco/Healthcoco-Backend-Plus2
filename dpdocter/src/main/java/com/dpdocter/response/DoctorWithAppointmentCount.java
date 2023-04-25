package com.dpdocter.response;

public class DoctorWithAppointmentCount {

	private String doctorId;

	private String firstName;

	private String colorCode;

	private long noOfAppointments;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public long getNoOfAppointments() {
		return noOfAppointments;
	}

	public void setNoOfAppointments(long count) {
		this.noOfAppointments = count;
	}

	@Override
	public String toString() {
		return "DoctorWithAppointmentCount [doctorId=" + doctorId + ", firstName=" + firstName + ", colorCode="
				+ colorCode + ", noOfAppointments=" + noOfAppointments + "]";
	}
}
