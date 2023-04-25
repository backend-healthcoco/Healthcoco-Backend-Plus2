package com.dpdocter.response;

import com.dpdocter.beans.DoctorPatientInvoice;
import com.dpdocter.beans.DoctorPatientReceipt;

public class DoctorPatientReceiptAddEditResponse {

	private DoctorPatientReceipt doctorPatientReceipt;

	private DoctorPatientInvoice invoice;

	private Double totalRemainingAdvanceAmount = 0.0;

	private Double totalDueAmount = 0.0;

	private String paymentInfo;

	public String getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	public DoctorPatientReceipt getDoctorPatientReceipt() {
		return doctorPatientReceipt;
	}

	public void setDoctorPatientReceipt(DoctorPatientReceipt doctorPatientReceipt) {
		this.doctorPatientReceipt = doctorPatientReceipt;
	}

	public DoctorPatientInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(DoctorPatientInvoice invoice) {
		this.invoice = invoice;
	}

	public Double getTotalRemainingAdvanceAmount() {
		return totalRemainingAdvanceAmount;
	}

	public void setTotalRemainingAdvanceAmount(Double totalRemainingAdvanceAmount) {
		this.totalRemainingAdvanceAmount = totalRemainingAdvanceAmount;
	}

	public Double getTotalDueAmount() {
		return totalDueAmount;
	}

	public void setTotalDueAmount(Double totalDueAmount) {
		this.totalDueAmount = totalDueAmount;
	}

	@Override
	public String toString() {
		return "DoctorPatientReceiptAddEditResponse [doctorPatientReceipt=" + doctorPatientReceipt + ", invoice="
				+ invoice + ", totalRemainingAdvanceAmount=" + totalRemainingAdvanceAmount + ", totalDueAmount="
				+ totalDueAmount + "]";
	}

}
