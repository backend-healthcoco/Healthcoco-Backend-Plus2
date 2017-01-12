package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.DoctorPatientLedger;

public class DoctorPatientLedgerResponse {

	private List<DoctorPatientLedger> doctorPatientLedgers;
	
	private Double totalBalanceAmount;

	public List<DoctorPatientLedger> getDoctorPatientLedgers() {
		return doctorPatientLedgers;
	}

	public void setDoctorPatientLedgers(List<DoctorPatientLedger> doctorPatientLedgers) {
		this.doctorPatientLedgers = doctorPatientLedgers;
	}

	public Double getTotalBalanceAmount() {
		return totalBalanceAmount;
	}

	public void setTotalBalanceAmount(Double totalBalanceAmount) {
		this.totalBalanceAmount = totalBalanceAmount;
	}

	@Override
	public String toString() {
		return "DoctorPatientLedgerResponse [doctorPatientLedgers=" + doctorPatientLedgers + ", totalBalanceAmount="
				+ totalBalanceAmount + "]";
	}
	
}
