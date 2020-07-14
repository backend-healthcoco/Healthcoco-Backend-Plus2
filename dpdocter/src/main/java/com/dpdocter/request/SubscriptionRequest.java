package com.dpdocter.request;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.PaymentMode;

public class SubscriptionRequest extends GenericCollection {

	private String id;

	private String doctorId;

	private Date fromDate;

	private Date toDate;

	private PackageType packageName;

	private int amount = 0;

	private int discountAmount = 0;

	private PaymentMode mode = PaymentMode.ONLINE;

	private String countryCode;

	private Boolean isAdvertisement = Boolean.FALSE;

	private Boolean isDiscarded = Boolean.FALSE;

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

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
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

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Boolean getIsAdvertisement() {
		return isAdvertisement;
	}

	public void setIsAdvertisement(Boolean isAdvertisement) {
		this.isAdvertisement = isAdvertisement;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
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
		return "SubscriptionRequest [id=" + id + ", doctorId=" + doctorId + ", fromDate=" + fromDate + ", toDate="
				+ toDate + ", packageName=" + packageName + ", amount=" + amount + ", discountAmount=" + discountAmount
				+ ", mode=" + mode + ", countryCode=" + countryCode + ", isAdvertisement=" + isAdvertisement
				+ ", isDiscarded=" + isDiscarded + ", paymentStatus=" + paymentStatus + "]";
	}

}
