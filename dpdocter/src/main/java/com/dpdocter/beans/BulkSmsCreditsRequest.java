package com.dpdocter.beans;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PaymentMode;

public class BulkSmsCreditsRequest extends GenericCollection {

	//private String id;

	private Long creditBalance=0L;
		
	private String doctorId;
	
	private String locationId;
	
	//private BulkSmsCredit bulkSmsCredit;
	
	private Date dateOfTransaction=new Date();
	
	private BulkSmsPackage smsPackage;
	
	private PaymentMode paymentMode;

	

	public long getCreditBalance() {
		return creditBalance;
	}

	public void setCreditBalance(long creditBalance) {
		this.creditBalance = creditBalance;
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

	

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	
	public BulkSmsPackage getSmsPackage() {
		return smsPackage;
	}

	public void setSmsPackage(BulkSmsPackage smsPackage) {
		this.smsPackage = smsPackage;
	}
	

}
