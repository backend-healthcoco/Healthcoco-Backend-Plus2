package com.dpdocter.beans;

public class DentalWorksLedger {

	private String id;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private String receiptId;

	private String invoiceId;

	private Double dueAmount;

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

}
