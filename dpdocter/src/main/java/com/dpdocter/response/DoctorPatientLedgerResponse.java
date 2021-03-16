package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.DoctorPatientLedger;

public class DoctorPatientLedgerResponse {

	private List<DoctorPatientLedger> doctorPatientLedgers;
	
	//private Double totalDueAmount;

	public List<DoctorPatientLedger> getDoctorPatientLedgers() {
		return doctorPatientLedgers;
	}

	public void setDoctorPatientLedgers(List<DoctorPatientLedger> doctorPatientLedgers) {
		this.doctorPatientLedgers = doctorPatientLedgers;
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
