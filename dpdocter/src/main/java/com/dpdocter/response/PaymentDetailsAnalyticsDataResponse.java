package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.enums.ModeOfPayment;
import com.dpdocter.enums.ReceiptType;

public class PaymentDetailsAnalyticsDataResponse {

	private String id;

	private Date date;

	private String firstName;

	private String localPatientName;

	private String doctorName;

	private String mobileNumber;

	private String uniqueReceiptId;

	private String uniqueInvoiceId;

	private Double amountPaid = 0.0;

	private ModeOfPayment modeOfPayment;

	private Double usedAdvanceAmount = 0.0;

	private ReceiptType receiptType;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUniqueReceiptId() {
		return uniqueReceiptId;
	}

	public void setUniqueReceiptId(String uniqueReceiptId) {
		this.uniqueReceiptId = uniqueReceiptId;
	}

	public String getUniqueInvoiceId() {
		return uniqueInvoiceId;
	}

	public void setUniqueInvoiceId(String uniqueInvoiceId) {
		this.uniqueInvoiceId = uniqueInvoiceId;
	}

	public Double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public ModeOfPayment getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(ModeOfPayment modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public ReceiptType getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(ReceiptType receiptType) {
		this.receiptType = receiptType;
	}

	public Double getUsedAdvanceAmount() {
		return usedAdvanceAmount;
	}

	public void setUsedAdvanceAmount(Double usedAdvanceAmount) {
		this.usedAdvanceAmount = usedAdvanceAmount;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String toString() {
		return "PaymentDetailsAnalyticsDataResponse [id=" + id + ", date=" + date + ", firstName=" + firstName
				+ ", localPatientName=" + localPatientName + ", doctorName=" + doctorName + ", mobileNumber="
				+ mobileNumber + ", uniqueReceiptId=" + uniqueReceiptId + ", uniqueInvoiceId=" + uniqueInvoiceId
				+ ", amountPaid=" + amountPaid + ", modeOfPayment=" + modeOfPayment + ", usedAdvanceAmount="
				+ usedAdvanceAmount + ", receiptType=" + receiptType + "]";
	}

}
