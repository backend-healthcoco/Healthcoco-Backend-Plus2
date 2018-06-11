package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

public class DentalImagingRequest {

	private String id;
	private String patientId;
	private String localPatientName;
	private String mobileNumber;
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
	private Boolean discarded;
	private String type;
	private Boolean isPayAndSave = false;
	private Discount totalDiscount;
	private Double totalCost = 0.0;
	private Tax totalTax;
	private List<InvoiceTax> invoiceTaxes;
	private Double grandTotal = 0.0;
	private Double usedAdvanceAmount = 0.0;
	private Double refundAmount = 0.0;
	private Double balanceAmount = 0.0;
	private Boolean isTaxNotApplicable = false;
	private Date invoiceDate;
	private String uniqueInvoiceId;
	private String invoiceId;

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getIsPayAndSave() {
		return isPayAndSave;
	}

	public void setIsPayAndSave(Boolean isPayAndSave) {
		this.isPayAndSave = isPayAndSave;
	}

	public Discount getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Discount totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public Tax getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(Tax totalTax) {
		this.totalTax = totalTax;
	}

	public List<InvoiceTax> getInvoiceTaxes() {
		return invoiceTaxes;
	}

	public void setInvoiceTaxes(List<InvoiceTax> invoiceTaxes) {
		this.invoiceTaxes = invoiceTaxes;
	}

	public Double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(Double grandTotal) {
		this.grandTotal = grandTotal;
	}

	public Double getUsedAdvanceAmount() {
		return usedAdvanceAmount;
	}

	public void setUsedAdvanceAmount(Double usedAdvanceAmount) {
		this.usedAdvanceAmount = usedAdvanceAmount;
	}

	public Double getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(Double refundAmount) {
		this.refundAmount = refundAmount;
	}

	public Double getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(Double balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public Boolean getIsTaxNotApplicable() {
		return isTaxNotApplicable;
	}

	public void setIsTaxNotApplicable(Boolean isTaxNotApplicable) {
		this.isTaxNotApplicable = isTaxNotApplicable;
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

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	@Override
	public String toString() {
		return "DentalImagingRequest [id=" + id + ", patientId=" + patientId + ", localPatientName=" + localPatientName
				+ ", mobileNumber=" + mobileNumber + ", doctorId=" + doctorId + ", hospitalId=" + hospitalId
				+ ", locationId=" + locationId + ", dentalImagingDoctorId=" + dentalImagingDoctorId
				+ ", dentalImagingHospitalId=" + dentalImagingHospitalId + ", dentalImagingLocationId="
				+ dentalImagingLocationId + ", referringDoctor=" + referringDoctor + ", clinicalNotes=" + clinicalNotes
				+ ", reportsRequired=" + reportsRequired + ", specialInstructions=" + specialInstructions
				+ ", services=" + services + ", discarded=" + discarded + ", type=" + type + "]";
	}

}
