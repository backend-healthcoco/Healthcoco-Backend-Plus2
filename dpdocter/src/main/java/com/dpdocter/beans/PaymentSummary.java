package com.dpdocter.beans;

import java.util.List;

public class PaymentSummary {

 private List<DoctorConsultation> consultationType;
	 
	 private String doctorId;
	 
	 private Double totalAmountReceived=0.0;

	public List<DoctorConsultation> getConsultationType() {
		return consultationType;
	}

	public void setConsultationType(List<DoctorConsultation> consultationType) {
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
