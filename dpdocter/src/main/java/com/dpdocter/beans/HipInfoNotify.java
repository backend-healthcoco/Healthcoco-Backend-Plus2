package com.dpdocter.beans;

public class HipInfoNotify {

	private String consentId;
	
	private String transactionId;
	
	private String doneAt;
	
	private HipNotifier notifier;
	
	private StatusNotify statusNotification;

	public String getConsentId() {
		return consentId;
	}

	public void setConsentId(String consentId) {
		this.consentId = consentId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getDoneAt() {
		return doneAt;
	}

	public void setDoneAt(String doneAt) {
		this.doneAt = doneAt;
	}

	public HipNotifier getNotifier() {
		return notifier;
	}

	public void setNotifier(HipNotifier notifier) {
		this.notifier = notifier;
	}

	public StatusNotify getStatusNotification() {
		return statusNotification;
	}

	public void setStatusNotification(StatusNotify statusNotification) {
		this.statusNotification = statusNotification;
	}
	
	
	
	
	
}
