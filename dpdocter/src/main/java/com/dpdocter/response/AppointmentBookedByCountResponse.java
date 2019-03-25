package com.dpdocter.response;

public class AppointmentBookedByCountResponse {
	private long bookedByPatient = 0;
	private long bookedByDoctor = 0;
	private long total = 0;

	public long getBookedByPatient() {
		return bookedByPatient;
	}

	public void setBookedByPatient(long bookedByPatient) {
		this.bookedByPatient = bookedByPatient;
	}

	public long getBookedByDoctor() {
		return bookedByDoctor;
	}

	public void setBookedByDoctor(long bookedByDoctor) {
		this.bookedByDoctor = bookedByDoctor;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

}
