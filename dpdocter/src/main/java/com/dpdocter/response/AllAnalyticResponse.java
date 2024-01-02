package com.dpdocter.response;

public class AllAnalyticResponse {
	private Integer totalNewPatient = 0;
	private Integer totalVisitedPatient = 0;
	private long totalAppointments;
	private long cancelledAppointments;
	private Integer totalPrescription = 0;
	private Double totalPayment = 0.0;
	private Double totalAmountDue = 0.0;
	private Double totalDiscount = 0.0;

	public Integer getTotalNewPatient() {
		return totalNewPatient;
	}

	public void setTotalNewPatient(Integer totalNewPatient) {
		this.totalNewPatient = totalNewPatient;
	}

	public Integer getTotalVisitedPatient() {
		return totalVisitedPatient;
	}

	public void setTotalVisitedPatient(Integer totalVisitedPatient) {
		this.totalVisitedPatient = totalVisitedPatient;
	}

	public long getTotalAppointments() {
		return totalAppointments;
	}

	public void setTotalAppointments(long totalAppointments) {
		this.totalAppointments = totalAppointments;
	}

	public long getCancelledAppointments() {
		return cancelledAppointments;
	}

	public void setCancelledAppointments(long cancelledAppointments) {
		this.cancelledAppointments = cancelledAppointments;
	}

	public Integer getTotalPrescription() {
		return totalPrescription;
	}

	public void setTotalPrescription(Integer totalPrescription) {
		this.totalPrescription = totalPrescription;
	}

	public Double getTotalPayment() {
		return totalPayment;
	}

	public void setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
	}

	
	public Double getTotalAmountDue() {
		return totalAmountDue;
	}

	public void setTotalAmountDue(Double totalAmountDue) {
		this.totalAmountDue = totalAmountDue;
	}

	public Double getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Double totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

}
