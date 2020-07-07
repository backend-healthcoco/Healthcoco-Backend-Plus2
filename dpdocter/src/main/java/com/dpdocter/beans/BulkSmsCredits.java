package com.dpdocter.beans;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PaymentMode;

public class BulkSmsCredits extends GenericCollection {

	private String id;

	private long creditBalance;
	
	private long creditSpent;
	
	private String doctorId;
	
	private String locationId;
	
	private String packageName;
	
	private Date dateOfTransaction=new Date();
	
	private PaymentMode paymentMode;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	public long getCreditBalance() {
		return creditBalance;
	}

	public void setCreditBalance(long creditBalance) {
		this.creditBalance = creditBalance;
	}

	public long getCreditSpent() {
		return creditSpent;
	}

	public void setCreditSpent(long creditSpent) {
		this.creditSpent = creditSpent;
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

	public Date getDateOfTransaction() {
		return dateOfTransaction;
	}

	public void setDateOfTransaction(Date dateOfTransaction) {
		this.dateOfTransaction = dateOfTransaction;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}
	
	

}
