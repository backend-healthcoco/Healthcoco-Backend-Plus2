package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.DoctorPatientLedger;

public class DoctorPatientLedgerResponse {

	private List<DoctorPatientLedger> doctorPatientLedgers;

	private Double totalDebitAmount = 0.0;

	private Double totalCreditAmount = 0.0;

	public List<DoctorPatientLedger> getDoctorPatientLedgers() {
		return doctorPatientLedgers;
	}

	public void setDoctorPatientLedgers(List<DoctorPatientLedger> doctorPatientLedgers) {
		this.doctorPatientLedgers = doctorPatientLedgers;
	}

	public Double getTotalDebitAmount() {
		return totalDebitAmount;
	}

	public void setTotalDebitAmount(Double totalDebitAmount) {
		this.totalDebitAmount = totalDebitAmount;
	}

	public Double getTotalCreditAmount() {
		return totalCreditAmount;
	}

	public void setTotalCreditAmount(Double totalCreditAmount) {
		this.totalCreditAmount = totalCreditAmount;
	}

	@Override
	public String toString() {
		return "DoctorPatientLedgerResponse [doctorPatientLedgers=" + doctorPatientLedgers + "]";
	}

//	public Double getTotalDueAmount() {
//		return totalDueAmount;
//	}
//
//	public void setTotalDueAmount(Double totalDueAmount) {
//		this.totalDueAmount = totalDueAmount;
//	}

}
