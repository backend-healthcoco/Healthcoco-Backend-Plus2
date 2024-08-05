package com.dpdocter.response;

import java.util.List;

public class SuggestedAbhaAddressesResponse {
	private String txnId;
	private List<String> abhaAddressList;

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public List<String> getAbhaAddressList() {
		return abhaAddressList;
	}

	public void setAbhaAddressList(List<String> abhaAddressList) {
		this.abhaAddressList = abhaAddressList;
	}

}
