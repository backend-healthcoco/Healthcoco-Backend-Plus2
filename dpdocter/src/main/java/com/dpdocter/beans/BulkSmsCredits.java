package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PaymentMode;

public class BulkSmsCredits extends GenericCollection {
	

	private Long creditBalance=0L;
	
	private Long creditSpent=0L;
	
	private Date dateOfTransaction=new Date();
	
	private BulkSmsPackage smsPackage;
	
	private PaymentMode paymentMode;
	
	private String doctorId;
	
	private String locationId;
	

	public void setCreditSpent(Long creditSpent) {
		this.creditSpent = creditSpent;
	}

	public Date getDateOfTransaction() {
		return dateOfTransaction;
	}

	public void setCreditBalance(Long creditBalance) {
		this.creditBalance = creditBalance;
	}

	public void setDateOfTransaction(Date dateOfTransaction) {
		this.dateOfTransaction = dateOfTransaction;
	}

	public BulkSmsPackage getSmsPackage() {
		return smsPackage;
	}

	public void setSmsPackage(BulkSmsPackage smsPackage) {
		this.smsPackage = smsPackage;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	

	public Long getCreditBalance() {
		return creditBalance;
	}

	public Long getCreditSpent() {
		return creditSpent;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	
	

	
}
