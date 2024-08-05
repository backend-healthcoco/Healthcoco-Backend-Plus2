package com.dpdocter.request;

public class DeleteABHANumberRequest {
	private String txnId;

	private String otp;
	private String authMethods;
	private String reasons;

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getAuthMethods() {
		return authMethods;
	}

	public void setAuthMethods(String authMethods) {
		this.authMethods = authMethods;
	}

	public String getReasons() {
		return reasons;
	}

	public void setReasons(String reasons) {
		this.reasons = reasons;
	}

}
