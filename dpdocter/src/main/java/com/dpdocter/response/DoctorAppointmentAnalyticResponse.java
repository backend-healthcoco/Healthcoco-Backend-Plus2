package com.dpdocter.response;

public class DoctorAppointmentAnalyticResponse {

	private Integer totalNoOfAppointment = 0;
	private Double bookedAppointmentInPercent = 0.0;
	private Double scheduledAppointmentInPercent = 0.0;
	private Double cancelledAppointmentInPercent = 0.0;
	private Double newPatientAppointmentInPercent = 0.0;
	private Double oldPatientAppointmentInPercent = 0.0;
	private Double changeInAppointmentPercent = 0.0;
	private Integer cancelByPatient = 0;
	private Integer cancelBydoctor = 0;

	public Double getChangeInAppointmentPercent() {
		return changeInAppointmentPercent;
	}

	public void setChangeInAppointmentPercent(Double changeInAppointmentPercent) {
		this.changeInAppointmentPercent = changeInAppointmentPercent;
	}

	public Integer getTotalNoOfAppointment() {
		return totalNoOfAppointment;
	}

	public void setTotalNoOfAppointment(Integer totalNoOfAppointment) {
		this.totalNoOfAppointment = totalNoOfAppointment;
	}

	public Double getBookedAppointmentInPercent() {
		return bookedAppointmentInPercent;
	}

	public void setBookedAppointmentInPercent(Double bookedAppointmentInPercent) {
		this.bookedAppointmentInPercent = bookedAppointmentInPercent;
	}

	public Double getScheduledAppointmentInPercent() {
		return scheduledAppointmentInPercent;
	}

	public void setScheduledAppointmentInPercent(Double scheduledAppointmentInPercent) {
		this.scheduledAppointmentInPercent = scheduledAppointmentInPercent;
	}

	public Double getCancelledAppointmentInPercent() {
		return cancelledAppointmentInPercent;
	}

	public void setCancelledAppointmentInPercent(Double cancelledAppointmentInPercent) {
		this.cancelledAppointmentInPercent = cancelledAppointmentInPercent;
	}

	public Double getNewPatientAppointmentInPercent() {
		return newPatientAppointmentInPercent;
	}

	public void setNewPatientAppointmentInPercent(Double newPatientAppointmentInPercent) {
		this.newPatientAppointmentInPercent = newPatientAppointmentInPercent;
	}

	public Double getOldPatientAppointmentInPercent() {
		return oldPatientAppointmentInPercent;
	}

	public void setOldPatientAppointmentInPercent(Double oldPatientAppointmentInPercent) {
		this.oldPatientAppointmentInPercent = oldPatientAppointmentInPercent;
	}

	public Integer getCancelByPatient() {
		return cancelByPatient;
	}

	public void setCancelByPatient(Integer cancelByPatient) {
		this.cancelByPatient = cancelByPatient;
	}

	public Integer getCancelBydoctor() {
		return cancelBydoctor;
	}

	public void setCancelBydoctor(Integer cancelBydoctor) {
		this.cancelBydoctor = cancelBydoctor;
	}

	@Override
	public String toString() {
		return "DoctorAppointmentAnalyticResponse [totalNoOfAppointment=" + totalNoOfAppointment
				+ ", bookedAppointmentInPercent=" + bookedAppointmentInPercent + ", scheduledAppointmentInPercent="
				+ scheduledAppointmentInPercent + ", cancelledAppointmentInPercent=" + cancelledAppointmentInPercent
				+ ", newPatientAppointmentInPercent=" + newPatientAppointmentInPercent
				+ ", oldPatientAppointmentInPercent=" + oldPatientAppointmentInPercent + ", changeInAppointmentPercent="
				+ changeInAppointmentPercent + ", cancelByPatient=" + cancelByPatient + ", cancelBydoctor="
				+ cancelBydoctor + "]";
	}

}
