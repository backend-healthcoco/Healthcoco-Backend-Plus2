package com.dpdocter.request;

public class CreateAbhaAddressV3Request {
	private String txnId;
	private String abhaAddress;
	private Integer preferred;

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getAbhaAddress() {
		return abhaAddress;
	}

	public void setAbhaAddress(String abhaAddress) {
		this.abhaAddress = abhaAddress;
	}

	public Integer getPreferred() {
		return preferred;
	}

	public void setPreferred(Integer preferred) {
		this.preferred = preferred;
	}

}
