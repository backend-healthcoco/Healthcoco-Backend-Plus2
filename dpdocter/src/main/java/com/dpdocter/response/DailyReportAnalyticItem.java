package com.dpdocter.response;

import java.util.Date;

import org.bson.types.ObjectId;

import com.dpdocter.enums.ModeOfPayment;
import com.dpdocter.enums.PaymentStatusType;

public class DailyReportAnalyticItem {
	private Date date;
	private String invoiceId; // For ReceiptType=INVOICE

	private String patientId;
	private String patientName;
	private Boolean isNewPatient;
	private Boolean isReturningPatient;
	private String locationName;
	private String serviceName;

	private double serviceFees = 0.0;
	private double discount = 0.0;
	private double totalAmountPaid = 0.0;
	private double totalAmountPending = 0.0;
	private double advancedAmount = 0.0;
	private ModeOfPayment paymentMode;
	private PaymentStatusType paymentStatus;
	private String consultingDentist;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public Boolean getIsNewPatient() {
		return isNewPatient;
	}

	public void setIsNewPatient(Boolean isNewPatient) {
		this.isNewPatient = isNewPatient;
	}

	public Boolean getIsReturningPatient() {
		return isReturningPatient;
	}

	public void setIsReturningPatient(Boolean isReturningPatient) {
		this.isReturningPatient = isReturningPatient;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public double getServiceFees() {
		return serviceFees;
	}

	public void setServiceFees(double serviceFees) {
		this.serviceFees = serviceFees;
	}


	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getTotalAmountPaid() {
		return totalAmountPaid;
	}

	public void setTotalAmountPaid(double totalAmountPaid) {
		this.totalAmountPaid = totalAmountPaid;
	}

	public double getTotalAmountPending() {
		return totalAmountPending;
	}

	public void setTotalAmountPending(double totalAmountPending) {
		this.totalAmountPending = totalAmountPending;
	}

	public double getAdvancedAmount() {
		return advancedAmount;
	}

	public void setAdvancedAmount(double advancedAmount) {
		this.advancedAmount = advancedAmount;
	}

	public ModeOfPayment getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(ModeOfPayment paymentMode) {
		this.paymentMode = paymentMode;
	}

	public PaymentStatusType getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatusType paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getConsultingDentist() {
		return consultingDentist;
	}

	public void setConsultingDentist(String consultingDentist) {
		this.consultingDentist = consultingDentist;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

}
