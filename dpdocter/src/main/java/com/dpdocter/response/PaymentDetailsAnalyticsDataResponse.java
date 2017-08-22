package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.enums.ModeOfPayment;
import com.dpdocter.enums.ReceiptType;

public class PaymentDetailsAnalyticsDataResponse {

	private Date date;
	
	private String patientName;
	
	private String uniqueReceiptId;
	
	private String uniqueInvoiceId;
	
	private Double amountPaid = 0.0;
	
	private ModeOfPayment modeOfPayment;
	
	private ReceiptType receiptType;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
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
	
	@Override
	public String toString() {
		return "PaymentDetailsAnalyticsDataResponse [date=" + date + ", patientName=" + patientName
				+ ", uniqueReceiptId=" + uniqueReceiptId + ", uniqueInvoiceId=" + uniqueInvoiceId + ", amountPaid="
				+ amountPaid + ", modeOfPayment=" + modeOfPayment + ", receiptType=" + receiptType + "]";
	}
}
