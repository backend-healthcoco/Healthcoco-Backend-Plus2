package com.dpdocter.response;

public class VerifyAndUpdateDataResponse {
	private String txnId;
	private String authResult;
	private String message;
	private VerifyAndUpdateAccount accounts;

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getAuthResult() {
		return authResult;
	}

	public void setAuthResult(String authResult) {
		this.authResult = authResult;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public VerifyAndUpdateAccount getAccounts() {
		return accounts;
	}

	public void setAccounts(VerifyAndUpdateAccount accounts) {
		this.accounts = accounts;
	}

}
