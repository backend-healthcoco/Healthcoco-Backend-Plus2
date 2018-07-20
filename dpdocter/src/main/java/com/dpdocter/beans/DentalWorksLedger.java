package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class DentalWorksLedger extends GenericCollection {

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String dentalLabLocationId;

	private String dentalLabHospitalId;

	private String patientId;

	private String receiptId;

	private String invoiceId;

	private Double dueAmount;

	private Double paidAmount;

	List<DentalLabPickup> requests;

	private Double creditAmount;

	private Double debitAmount;

	private DentalWorksInvoice invoice;

	private DentalWorksReceipt receipt;

	private Boolean discarded = false;

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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getDentalLabLocationId() {
		return dentalLabLocationId;
	}

	public void setDentalLabLocationId(String dentalLabLocationId) {
		this.dentalLabLocationId = dentalLabLocationId;
	}

	public String getDentalLabHospitalId() {
		return dentalLabHospitalId;
	}

	public void setDentalLabHospitalId(String dentalLabHospitalId) {
		this.dentalLabHospitalId = dentalLabHospitalId;

	}
	
	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Double getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(Double dueAmount) {
		this.dueAmount = dueAmount;
	}

	public Double getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Double creditAmount) {
		this.creditAmount = creditAmount;
	}

	public Double getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(Double debitAmount) {
		this.debitAmount = debitAmount;
	}

	public DentalWorksInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(DentalWorksInvoice invoice) {
		this.invoice = invoice;
	}

	public DentalWorksReceipt getReceipt() {
		return receipt;
	}

	public void setReceipt(DentalWorksReceipt receipt) {
		this.receipt = receipt;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(Double paidAmount) {
		this.paidAmount = paidAmount;
	}

	public List<DentalLabPickup> getRequests() {
		return requests;
	}

	public void setRequests(List<DentalLabPickup> requests) {
		this.requests = requests;
	}

	@Override
	public String toString() {
		return "DentalWorksLedger [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", dentalLabLocationId=" + dentalLabLocationId + ", dentalLabHospitalId="
				+ dentalLabHospitalId + ", patientId=" + patientId + ", receiptId=" + receiptId + ", invoiceId="
				+ invoiceId + ", dueAmount=" + dueAmount + ", paidAmount=" + paidAmount + ", requests=" + requests
				+ ", creditAmount=" + creditAmount + ", debitAmount=" + debitAmount + ", invoice=" + invoice
				+ ", receipt=" + receipt + ", discarded=" + discarded + "]";
	}

}
