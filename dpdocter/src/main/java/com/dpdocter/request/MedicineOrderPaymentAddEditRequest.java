package com.dpdocter.request;

import com.dpdocter.enums.OrderStatus;
import com.dpdocter.enums.PaymentMode;

public class MedicineOrderPaymentAddEditRequest {

	private String id;
	private Float totalAmount;
	private Float discountedAmount;
	private Float discountedPercentage;
	private Float finalAmount;
	private Float deliveryCharges;
	private Float cashHandlingCharges;
	private String notes;
	private PaymentMode paymentMode = PaymentMode.COD;
	private OrderStatus orderStatus;
	private String callingPreference;
	private Boolean isPrescriptionRequired;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Float getDiscountedAmount() {
		return discountedAmount;
	}

	public void setDiscountedAmount(Float discountedAmount) {
		this.discountedAmount = discountedAmount;
	}

	public Float getDiscountedPercentage() {
		return discountedPercentage;
	}

	public void setDiscountedPercentage(Float discountedPercentage) {
		this.discountedPercentage = discountedPercentage;
	}

	public Float getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(Float finalAmount) {
		this.finalAmount = finalAmount;
	}

	public Float getDeliveryCharges() {
		return deliveryCharges;
	}

	public void setDeliveryCharges(Float deliveryCharges) {
		this.deliveryCharges = deliveryCharges;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getCallingPreference() {
		return callingPreference;
	}

	public void setCallingPreference(String callingPreference) {
		this.callingPreference = callingPreference;
	}

	public Float getCashHandlingCharges() {
		return cashHandlingCharges;
	}

	public void setCashHandlingCharges(Float cashHandlingCharges) {
		this.cashHandlingCharges = cashHandlingCharges;
	}

	public Boolean getIsPrescriptionRequired() {
		return isPrescriptionRequired;
	}

	public void setIsPrescriptionRequired(Boolean isPrescriptionRequired) {
		this.isPrescriptionRequired = isPrescriptionRequired;
	}

}
