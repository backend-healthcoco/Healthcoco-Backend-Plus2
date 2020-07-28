package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.BulkSmsPackage;
import com.dpdocter.enums.PaymentMode;

@Document(collection = "bulk_sms_credit_cl")
public class BulkSmsCreditsCollection extends GenericCollection{

	@Id
	private ObjectId id;

	@Field
	private Long creditBalance;
	@Field
	private Long creditSpent;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private Date dateOfTransaction=new Date();
	@Field
	private BulkSmsPackage smsPackage;
	@Field
	private PaymentMode paymentMode;


	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
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

	public void setCreditBalance(Long creditBalance) {
		this.creditBalance = creditBalance;
	}

	public void setCreditSpent(Long creditSpent) {
		this.creditSpent = creditSpent;
	}

	
	
	
	
}
