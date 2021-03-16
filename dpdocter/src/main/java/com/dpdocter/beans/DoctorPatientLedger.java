package com.dpdocter.beans;

public class DoctorPatientLedger {

	private String id;
	
	private String locationId;
	
	private String hospitalId;
	
	private String patientId;
	
	private String receiptId;
	
	private String invoiceId;   
	
//	private Double dueAmount;

	private Double creditAmount;
	
	private Double debitAmount;

	private DoctorPatientInvoice invoice;
	
	private DoctorPatientReceipt receipt;

	private Boolean discarded = false;
	
	private String type;

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

//	public Double getDueAmount() {
//		return dueAmount;
//	}
//
//	public void setDueAmount(Double dueAmount) {
//		this.dueAmount = dueAmount;
//	}

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

	public DoctorPatientInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(DoctorPatientInvoice invoice) {
		this.invoice = invoice;
	}

	public DoctorPatientReceipt getReceipt() {
		return receipt;
	}

	public void setReceipt(DoctorPatientReceipt receipt) {
		this.receipt = receipt;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "DoctorPatientLedger [id=" + id + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", patientId=" + patientId + ", receiptId=" + receiptId + ", invoiceId=" + invoiceId
				+ ", creditAmount=" + creditAmount + ", debitAmount=" + debitAmount + ", invoice=" + invoice
				+ ", receipt=" + receipt + ", discarded=" + discarded + ", type=" + type + "]";
	}

	
}

