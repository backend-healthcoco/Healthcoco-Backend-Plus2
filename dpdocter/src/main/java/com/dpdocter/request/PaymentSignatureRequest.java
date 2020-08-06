package com.dpdocter.request;

import com.dpdocter.enums.PaymentMode;

public class PaymentSignatureRequest {

	public String orderId;
	public String paymentId;
	public String signature;
	private String doctorId;
	private String locationId;
	private String bulkSmsPackageId;
	private PaymentMode mode;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	public String getBulkSmsPackageId() {
		return bulkSmsPackageId;
	}
	public void setBulkSmsPackageId(String bulkSmsPackageId) {
		this.bulkSmsPackageId = bulkSmsPackageId;
	}
	
	
	
	
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public PaymentMode getMode() {
		return mode;
	}
	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}
	@Override
	public String toString() {
		return "PaymentSignatureRequest [orderId=" + orderId + ", paymentId=" + paymentId + ", signature=" + signature
				+ ", doctorId=" + doctorId + ", bulkSmsPackageId=" + bulkSmsPackageId + "]";
	}
	
	
}
