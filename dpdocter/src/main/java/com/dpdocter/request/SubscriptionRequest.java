package com.dpdocter.request;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.PaymentMode;

public class SubscriptionRequest extends GenericCollection {

	private String id;

	private String doctorId;

	private String subscriptionId;

	private PackageType packageName;

	private int amount = 0;

	private int discountAmount = 0;

	private PaymentMode mode = PaymentMode.ONLINE;

	private Boolean paymentStatus = Boolean.FALSE;

	private String currency = "INR";

	private Boolean paymentCapture = true;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public PackageType getPackageName() {
		return packageName;
	}

	public void setPackageName(PackageType packageName) {
		this.packageName = packageName;
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

	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}

	public Boolean getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(Boolean paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Boolean getPaymentCapture() {
		return paymentCapture;
	}

	public void setPaymentCapture(Boolean paymentCapture) {
		this.paymentCapture = paymentCapture;
	}

	@Override
	public String toString() {
		return "SubscriptionRequest [id=" + id + ", doctorId=" + doctorId + ", packageName=" + packageName + ", amount="
				+ amount + ", discountAmount=" + discountAmount + ", mode=" + mode + ", paymentStatus=" + paymentStatus
				+ ", currency=" + currency + ", paymentCapture=" + paymentCapture + "]";
	}

}
