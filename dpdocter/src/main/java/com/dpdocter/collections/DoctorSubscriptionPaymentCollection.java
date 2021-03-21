package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Discount;
import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.PaymentMode;

@Document(collection = "subscription_payment_cl")
public class DoctorSubscriptionPaymentCollection extends GenericCollection {

	@Field
	private ObjectId id;

	@Field
	private ObjectId subscriptionId;

	@Field
	private ObjectId doctorId;

	@Field
	private String transactionId;

	@Field
	private String transactionStatus;

	@Field
	private Discount discount;

	@Field
	private int amount = 0;

	@Field
	private int discountAmount = 0;

	@Field
	private Double transferAmount = 0.0;

	@Field
	private PaymentMode mode = PaymentMode.ONLINE;

	@Field
	private PackageType packageName;

	@Field
	private String orderId;
	@Field
	private String reciept;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(int discountAmount) {
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

	public PackageType getPackageName() {
		return packageName;
	}

	public void setPackageName(PackageType packageName) {
		this.packageName = packageName;
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
