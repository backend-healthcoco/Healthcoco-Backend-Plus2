package com.dpdocter.beans;

public class SmsPatientNotify {

	private String phoneNo;
	
	private String receiverName;
	
	private String careContextInfo;
	
	private String deeplinkUrl;
	
	private HipConsent hip;

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getCareContextInfo() {
		return careContextInfo;
	}

	public void setCareContextInfo(String careContextInfo) {
		this.careContextInfo = careContextInfo;
	}

	public String getDeeplinkUrl() {
		return deeplinkUrl;
	}

	public void setDeeplinkUrl(String deeplinkUrl) {
		this.deeplinkUrl = deeplinkUrl;
	}

	public HipConsent getHip() {
		return hip;
	}

	public void setHip(HipConsent hip) {
		this.hip = hip;
	}
	
	
}
