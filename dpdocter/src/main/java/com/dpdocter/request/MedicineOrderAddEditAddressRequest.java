package com.dpdocter.request;

import com.dpdocter.beans.UserAddress;
import com.dpdocter.enums.DeliveryPreferences;
import com.dpdocter.enums.PaymentMode;

public class MedicineOrderAddEditAddressRequest {

	private String id;
	private UserAddress shippingAddress;
	private UserAddress billingAddress;
	private String patientName;
	private String prescriptionId;
	private String mobileNumber;
	private String emailAddress;
	private DeliveryPreferences deliveryPreference = DeliveryPreferences.ONE_TIME;
	private Long nextDeliveryDate;
	private PaymentMode paymentMode = PaymentMode.COD;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserAddress getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(UserAddress shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public UserAddress getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(UserAddress billingAddress) {
		this.billingAddress = billingAddress;
	}

	public DeliveryPreferences getDeliveryPreference() {
		return deliveryPreference;
	}

	public void setDeliveryPreference(DeliveryPreferences deliveryPreference) {
		this.deliveryPreference = deliveryPreference;
	}

	public Long getNextDeliveryDate() {
		return nextDeliveryDate;
	}

	public void setNextDeliveryDate(Long nextDeliveryDate) {
		this.nextDeliveryDate = nextDeliveryDate;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

}
