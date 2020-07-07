package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Discount;
import com.dpdocter.enums.PaymentMode;

public class DoctorSubscriptionCollection {

	private String id;

	private ObjectId subscriptionId;

	private ObjectId doctorId;

	private String transactionId;

	private String transactionStatus;

	private Double amount = 0.0;

	private Double discountAmount = 0.0;
	
	private PaymentMode mode = PaymentMode.ONLINE;

	private String chequeNo;

	private String accountNo;

	private String bankName;

	private String branch;

	private Date chequeDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ObjectId getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(ObjectId subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(Double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}
	
	
}
