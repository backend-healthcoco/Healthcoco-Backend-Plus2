package com.dpdocter.response;

public class CreateAbhaAddresseResponse {
	private String txnId;
	private String healthIdNumber;
	private String preferredAbhaAddress;

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getHealthIdNumber() {
		return healthIdNumber;
	}

	public void setHealthIdNumber(String healthIdNumber) {
		this.healthIdNumber = healthIdNumber;
	}

	public String getPreferredAbhaAddress() {
		return preferredAbhaAddress;
	}

	public void setPreferredAbhaAddress(String preferredAbhaAddress) {
		this.preferredAbhaAddress = preferredAbhaAddress;
	}

}
