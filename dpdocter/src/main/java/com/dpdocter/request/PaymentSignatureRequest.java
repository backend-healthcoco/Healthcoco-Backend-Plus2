package com.dpdocter.request;

public class PaymentSignatureRequest {

	public String orderId;
	public String paymentId;
	public String signature;
	private String userId;
	private String doctorId;
	private String bulkSmsPackageId;
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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
	
	@Override
	public String toString() {
		return "PaymentSignatureRequest [orderId=" + orderId + ", paymentId=" + paymentId + ", signature=" + signature
				+ ", userId=" + userId + ", doctorId=" + doctorId + ", bulkSmsPackageId=" + bulkSmsPackageId + "]";
	}
	
	
}
