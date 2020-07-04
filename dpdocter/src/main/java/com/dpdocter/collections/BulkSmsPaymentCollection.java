package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Discount;
import com.dpdocter.enums.PaymentMode;

@Document(collection = "bulk_sms_payment_cl")
public class BulkSmsPaymentCollection extends GenericCollection{

	@Id
	private String id;

	@Field
	private String bulkSmsPackageId;

	@Field
	private String transactionId;
	
	@Field
	private String transactionStatus;

	@Field
	private String doctorId;	
	
//	private String razorPayAccountId;

	@Field
	private Discount discount;

	@Field
	private Double amount = 0.0;
	@Field
	private Double discountAmount = 0.0;
	@Field
	private Double transferAmount = 0.0;
	@Field
	private PaymentMode mode = PaymentMode.ONLINE;
	@Field
	private String chequeNo;
	@Field
	private String orderId;
	@Field
	private String reciept;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBulkSmsPackageId() {
		return bulkSmsPackageId;
	}

	public void setBulkSmsPackageId(String bulkSmsPackageId) {
		this.bulkSmsPackageId = bulkSmsPackageId;
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

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}
	
	

}
