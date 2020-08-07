package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.QueueStatus;

public class PatientPaymentDetails extends GenericCollection {

	private String userId;
	
	private String localPatientName;
	
	private String appointmentId;
	
	private Date appointmentDate;
	
	private DoctorConsultation consultationType;
	
	private QueueStatus status = QueueStatus.SCHEDULED;
	
	private Boolean isSettled;
	
	private Date settledDate;
	
	private String orderId;
	
	private String paymentId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public Date getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public DoctorConsultation getConsultationType() {
		return consultationType;
	}

	public void setConsultationType(DoctorConsultation consultationType) {
		this.consultationType = consultationType;
	}

	public QueueStatus getStatus() {
		return status;
	}

	public void setStatus(QueueStatus status) {
		this.status = status;
	}

	

	public Boolean getIsSettled() {
		return isSettled;
	}

	public void setIsSettled(Boolean isSettled) {
		this.isSettled = isSettled;
	}

	public Date getSettledDate() {
		return settledDate;
	}

	public void setSettledDate(Date settledDate) {
		this.settledDate = settledDate;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	
	

}
