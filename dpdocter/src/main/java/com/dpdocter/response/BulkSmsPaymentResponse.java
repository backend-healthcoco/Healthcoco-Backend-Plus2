package com.dpdocter.response;

import com.dpdocter.beans.Discount;
import com.dpdocter.enums.PaymentMode;

public class BulkSmsPaymentResponse {

	private String id;

	private String userId;

	private String problemDetailsId;

	private String transactionId;

//	private String transactionStatus;

	private String doctorId;	
	
//	private String razorPayAccountId;

	private Discount discount;

	private Double amount = 0.0;

	private Double discountAmount = 0.0;

	private Double transferAmount = 0.0;

	private PaymentMode mode = PaymentMode.ONLINE;

	//private String chequeNo;

	private String orderId;

	private String reciept;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProblemDetailsId() {
		return problemDetailsId;
	}

	public void setProblemDetailsId(String problemDetailsId) {
		this.problemDetailsId = problemDetailsId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
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

	public Double getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(Double transferAmount) {
		this.transferAmount = transferAmount;
	}

	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getReciept() {
		return reciept;
	}

	public void setReciept(String reciept) {
		this.reciept = reciept;
	}
	
	
}
