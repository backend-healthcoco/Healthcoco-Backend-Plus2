package com.dpdocter.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//@JsonIgnoreProperties(ignoreUnknown=true)
public class EnrollByAadhaarResponse {
	private String txnId;
	private String message;
//	private EnrollByAadhaarTokens tokens;

//	private AbhaProfile ABHAProfile;
	private Boolean isNew;

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

//	public AbhaProfile getABHAProfile() {
//		return ABHAProfile;
//	}
//
//	public void setABHAProfile(AbhaProfile ABHAProfile) {
//		ABHAProfile = ABHAProfile;
//	}

//	public EnrollByAadhaarTokens getTokens() {
//		return tokens;
//	}
//
//	public void setTokens(EnrollByAadhaarTokens tokens) {
//		this.tokens = tokens;
//	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

}
