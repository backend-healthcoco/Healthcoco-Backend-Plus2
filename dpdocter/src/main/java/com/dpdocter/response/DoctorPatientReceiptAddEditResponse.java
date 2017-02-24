package com.dpdocter.response;

import com.dpdocter.beans.DoctorPatientInvoice;
import com.dpdocter.beans.DoctorPatientReceipt;

public class DoctorPatientReceiptAddEditResponse {

	private DoctorPatientReceipt doctorPatientReceipt;
	
	private DoctorPatientInvoice invoice;
	
	private Double totalRemainingAdvanceAmount;
	
	private Double totalBalanceAmount;

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

	public Double getTotalBalanceAmount() {
		return totalBalanceAmount;
	}

	public void setTotalBalanceAmount(Double totalBalanceAmount) {
		this.totalBalanceAmount = totalBalanceAmount;
	}

	@Override
	public String toString() {
		return "DoctorPatientReceiptAddEditResponse [doctorPatientReceipt=" + doctorPatientReceipt + ", invoice="
				+ invoice + ", totalRemainingAdvanceAmount=" + totalRemainingAdvanceAmount + ", totalBalanceAmount="
				+ totalBalanceAmount + "]";
	}
}
