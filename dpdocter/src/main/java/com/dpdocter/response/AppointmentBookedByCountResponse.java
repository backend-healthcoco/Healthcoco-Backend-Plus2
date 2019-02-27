package com.dpdocter.response;

public class AppointmentBookedByCountResponse {
	private long bookedByPatient = 0;
	private long bookedBydoctor = 0;
	private long total = 0;

	public long getBookedByPatient() {
		return bookedByPatient;
	}

	public void setBookedByPatient(long bookedByPatient) {
		this.bookedByPatient = bookedByPatient;
	}

	public long getBookedBydoctor() {
		return bookedBydoctor;
	}

	public void setBookedBydoctor(long bookedBydoctor) {
		this.bookedBydoctor = bookedBydoctor;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

}
