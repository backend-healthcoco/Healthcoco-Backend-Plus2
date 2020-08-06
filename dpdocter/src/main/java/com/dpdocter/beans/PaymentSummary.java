package com.dpdocter.beans;

import java.util.List;

public class PaymentSummary {

 private DoctorConsultation consultationType;
	 
	 private String doctorId;
	 
	 private Double totalAmountReceived=0.0;

	
	 
	 
	 
	public DoctorConsultation getConsultationType() {
		return consultationType;
	}

	public void setConsultationType(DoctorConsultation consultationType) {
		this.consultationType = consultationType;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Double getTotalAmountReceived() {
		return totalAmountReceived;
	}

	public void setTotalAmountReceived(Double totalAmountReceived) {
		this.totalAmountReceived = totalAmountReceived;
	}
	
	
}
