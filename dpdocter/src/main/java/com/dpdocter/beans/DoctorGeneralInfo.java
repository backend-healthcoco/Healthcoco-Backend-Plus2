package com.dpdocter.beans;

import java.util.List;

public class DoctorGeneralInfo {
    private String id;

    private String doctorId;

    private String locationId;

    private AppointmentSlot appointmentSlot;

    private ConsultationFee consultationFee;

    private List<String> appointmentBookingNumber;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public AppointmentSlot getAppointmentSlot() {
	return appointmentSlot;
    }

    public void setAppointmentSlot(AppointmentSlot appointmentSlot) {
	this.appointmentSlot = appointmentSlot;
    }

    public ConsultationFee getConsultationFee() {
	return consultationFee;
    }

    public void setConsultationFee(ConsultationFee consultationFee) {
	this.consultationFee = consultationFee;
    }

    public List<String> getAppointmentBookingNumber() {
	return appointmentBookingNumber;
    }

    public void setAppointmentBookingNumber(List<String> appointmentBookingNumber) {
	this.appointmentBookingNumber = appointmentBookingNumber;
    }

    @Override
    public String toString() {
	return "DoctorGeneralInfo [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", appointmentSlot=" + appointmentSlot
		+ ", consultationFee=" + consultationFee + ", appointmentBookingNumber=" + appointmentBookingNumber + "]";
    }

}
