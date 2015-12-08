package com.dpdocter.beans;

import java.util.List;

public class DoctorClinicProfile {
    private String id;

    private String locationId;

    private String clinicAddress;

    private String patientInitial = "P";

    private int patientCounter = 0;

    private List<String> appointmentBookingNumber;

    private ConsultationFee consultationFee;

    private AppointmentSlot appointmentSlot;

    private List<WorkingSchedule> workingSchedules;
    
    private Boolean isIBSOn = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public String getClinicAddress() {
	return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
	this.clinicAddress = clinicAddress;
    }

    public String getPatientInitial() {
	return patientInitial;
    }

    public void setPatientInitial(String patientInitial) {
	this.patientInitial = patientInitial;
    }

    public int getPatientCounter() {
	return patientCounter;
    }

    public void setPatientCounter(int patientCounter) {
	this.patientCounter = patientCounter;
    }

    public List<String> getAppointmentBookingNumber() {
	return appointmentBookingNumber;
    }

    public void setAppointmentBookingNumber(List<String> appointmentBookingNumber) {
	this.appointmentBookingNumber = appointmentBookingNumber;
    }

    public ConsultationFee getConsultationFee() {
	return consultationFee;
    }

    public void setConsultationFee(ConsultationFee consultationFee) {
	this.consultationFee = consultationFee;
    }

    public AppointmentSlot getAppointmentSlot() {
	return appointmentSlot;
    }

    public void setAppointmentSlot(AppointmentSlot appointmentSlot) {
	this.appointmentSlot = appointmentSlot;
    }

    public List<WorkingSchedule> getWorkingSchedules() {
	return workingSchedules;
    }

    public void setWorkingSchedules(List<WorkingSchedule> workingSchedules) {
	this.workingSchedules = workingSchedules;
    }

	public Boolean getIsIBSOn() {
		return isIBSOn;
	}

	public void setIsIBSOn(Boolean isIBSOn) {
		this.isIBSOn = isIBSOn;
	}

	@Override
	public String toString() {
		return "DoctorClinicProfile [id=" + id + ", locationId=" + locationId + ", clinicAddress=" + clinicAddress
				+ ", patientInitial=" + patientInitial + ", patientCounter=" + patientCounter
				+ ", appointmentBookingNumber=" + appointmentBookingNumber + ", consultationFee=" + consultationFee
				+ ", appointmentSlot=" + appointmentSlot + ", workingSchedules=" + workingSchedules + ", isIBSOn="
				+ isIBSOn + "]";
	}
}
