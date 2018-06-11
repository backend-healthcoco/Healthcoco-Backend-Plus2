package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.DentalDiagnosticServiceRequest;
import com.dpdocter.beans.DentalImagingReports;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.beans.User;
import com.dpdocter.collections.GenericCollection;

public class DentalImagingResponse extends GenericCollection {

	private String id;
	private String patientId;
	private String doctorId;
	private String hospitalId;
	private String locationId;
	private String dentalImagingDoctorId;
	private String dentalImagingHospitalId;
	private String dentalImagingLocationId;
	private String referringDoctor;
	private String clinicalNotes;
	private Boolean reportsRequired;
	private String specialInstructions;
	private List<DentalDiagnosticServiceRequest> services;
	private Boolean discarded = false;
	private PatientShortCard patient;
	private Location location;
	private User doctor;
	private List<DentalImagingReports> reports;
	private String patientName;
	private String mobileNumber;
	private Date invoiceDate;
	private String uniqueInvoiceId;
	private String invoiceId;
	private Double totalCost = 0.0;
	private Boolean isPaid = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getReferringDoctor() {
		return referringDoctor;
	}

	public void setReferringDoctor(String referringDoctor) {
		this.referringDoctor = referringDoctor;
	}

	public String getClinicalNotes() {
		return clinicalNotes;
	}

	public void setClinicalNotes(String clinicalNotes) {
		this.clinicalNotes = clinicalNotes;
	}

	public Boolean getReportsRequired() {
		return reportsRequired;
	}

	public void setReportsRequired(Boolean reportsRequired) {
		this.reportsRequired = reportsRequired;
	}

	public String getSpecialInstructions() {
		return specialInstructions;
	}

	public void setSpecialInstructions(String specialInstructions) {
		this.specialInstructions = specialInstructions;
	}

	public List<DentalDiagnosticServiceRequest> getServices() {
		return services;
	}

	public void setServices(List<DentalDiagnosticServiceRequest> services) {
		this.services = services;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public PatientShortCard getPatient() {
		return patient;
	}

	public void setPatient(PatientShortCard patient) {
		this.patient = patient;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getDentalImagingDoctorId() {
		return dentalImagingDoctorId;
	}

	public void setDentalImagingDoctorId(String dentalImagingDoctorId) {
		this.dentalImagingDoctorId = dentalImagingDoctorId;
	}

	public String getDentalImagingHospitalId() {
		return dentalImagingHospitalId;
	}

	public void setDentalImagingHospitalId(String dentalImagingHospitalId) {
		this.dentalImagingHospitalId = dentalImagingHospitalId;
	}

	public String getDentalImagingLocationId() {
		return dentalImagingLocationId;
	}

	public void setDentalImagingLocationId(String dentalImagingLocationId) {
		this.dentalImagingLocationId = dentalImagingLocationId;
	}

	public List<DentalImagingReports> getReports() {
		return reports;
	}

	public void setReports(List<DentalImagingReports> reports) {
		this.reports = reports;
	}

	public User getDoctor() {
		return doctor;
	}

	public void setDoctor(User doctor) {
		this.doctor = doctor;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getUniqueInvoiceId() {
		return uniqueInvoiceId;
	}

	public void setUniqueInvoiceId(String uniqueInvoiceId) {
		this.uniqueInvoiceId = uniqueInvoiceId;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public Boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

}
