package com.dpdocter.beans;

import java.util.List;

public class PaymentSummary {

	private List<DoctorConsultation> consultationType;
	 
	 private String doctorId;
	 
	 private Double totalAmountReceivedByChat=0.0;

	 private Double totalAmountReceivedByVideo=0.0;
	 
	 private Double totalAmountReceivedByOnlineConsultation=0.0;
	
	 
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

	public Double getTotalAmountReceivedByChat() {
		return totalAmountReceivedByChat;
	}

	public void setTotalAmountReceivedByChat(Double totalAmountReceivedByChat) {
		this.totalAmountReceivedByChat = totalAmountReceivedByChat;
	}

	public Double getTotalAmountReceivedByVideo() {
		return totalAmountReceivedByVideo;
	}

	public void setTotalAmountReceivedByVideo(Double totalAmountReceivedByVideo) {
		this.totalAmountReceivedByVideo = totalAmountReceivedByVideo;
	}

	public Double getTotalAmountReceivedByOnlineConsultation() {
		return totalAmountReceivedByOnlineConsultation;
	}

	public void setTotalAmountReceivedByOnlineConsultation(Double totalAmountReceivedByOnlineConsultation) {
		this.totalAmountReceivedByOnlineConsultation = totalAmountReceivedByOnlineConsultation;
	}

	
	
	
	
	
}
