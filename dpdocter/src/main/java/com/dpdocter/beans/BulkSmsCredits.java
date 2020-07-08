package com.dpdocter.beans;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PaymentMode;

public class BulkSmsCredits {


	private Long creditBalance;
	
	private Long creditSpent;
	
	private String doctorId;
	
	private String locationId;
	
	private BulkSmsPackage smsPackage;
	
	private Date dateOfTransaction=new Date();
	
	private PaymentMode paymentMode;


	
	
	public Long getCreditBalance() {
		return creditBalance;
	}

	public void setCreditBalance(Long creditBalance) {
		this.creditBalance = creditBalance;
	}

	public Long getCreditSpent() {
		return creditSpent;
	}

	public void setCreditSpent(Long creditSpent) {
		this.creditSpent = creditSpent;
	}

	
	public Date getDateOfTransaction() {
		return dateOfTransaction;
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

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	
	

}
