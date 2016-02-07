package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.DoctorFacility;

public class DoctorClinicProfile {
    private String id;

    private String locationId;

    private String clinicAddress;

    private String locationName;

    private String country;

    private String state;

    private String city;

    private String postalCode;

    private String patientInitial = "P";

    private int patientCounter = 0;

    private List<String> appointmentBookingNumber;

    private ConsultationFee consultationFee;

    private AppointmentSlot appointmentSlot;

    private List<WorkingSchedule> workingSchedules;

    private DoctorFacility facility;

    private List<ClinicImage> images;

    private String logoUrl;

    private String logoThumbnailUrl;

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

    public List<ClinicImage> getImages() {
	return images;
    }

    public void setImages(List<ClinicImage> images) {
	this.images = images;
    }

    public String getLogoUrl() {
	return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
	this.logoUrl = logoUrl;
    }

    public String getLogoThumbnailUrl() {
	return logoThumbnailUrl;
    }

    public void setLogoThumbnailUrl(String logoThumbnailUrl) {
	this.logoThumbnailUrl = logoThumbnailUrl;
    }

    public String getLocationName() {
	return locationName;
    }

    public void setLocationName(String locationName) {
	this.locationName = locationName;
    }

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getPostalCode() {
	return postalCode;
    }

    public void setPostalCode(String postalCode) {
	this.postalCode = postalCode;
    }

    public DoctorFacility getFacility() {
	return facility;
    }

    public void setFacility(DoctorFacility facility) {
	this.facility = facility;
    }

    @Override
    public String toString() {
	return "DoctorClinicProfile [id=" + id + ", locationId=" + locationId + ", clinicAddress=" + clinicAddress + ", locationName=" + locationName
		+ ", country=" + country + ", state=" + state + ", city=" + city + ", postalCode=" + postalCode + ", patientInitial=" + patientInitial
		+ ", patientCounter=" + patientCounter + ", appointmentBookingNumber=" + appointmentBookingNumber + ", consultationFee=" + consultationFee
		+ ", appointmentSlot=" + appointmentSlot + ", workingSchedules=" + workingSchedules + ", facility=" + facility + ", images=" + images
		+ ", logoUrl=" + logoUrl + ", logoThumbnailUrl=" + logoThumbnailUrl + "]";
    }
}
