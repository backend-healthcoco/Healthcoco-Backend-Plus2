package com.dpdocter.beans;

public class ReceiptDownloadData {

	private String doctorName;

	private String patientName;

	private String patientId;

	private String date;

	private String receiptId;

	private String invoiceId;

	private String modeOfPayment;

	private Double amountPaid = 0.0;

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public Double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
	}

	@Override
	public String toString() {
		return "ReceiptDownloadData [doctorName=" + doctorName + ", patientName=" + patientName + ", patientId="
				+ patientId + ", date=" + date + ", receiptId=" + receiptId + ", invoiceId=" + invoiceId
				+ ", modeOfPayment=" + modeOfPayment + ", amountPaid=" + amountPaid + "]";
	}
}
