package com.dpdocter.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//@JsonIgnoreProperties(ignoreUnknown=true)
public class EnrollByAadhaarResponse {
	public String txnId;
	public String message;
	public EnrollByAadhaarTokens tokens;
    @JsonProperty("ABHAProfile") // This line ensures the correct mapping
	public ABHAProfile ABHAProfile;
	public Boolean isNew;

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public ABHAProfile getABHAProfile() {
		return ABHAProfile;
	}

	public void setABHAProfile(ABHAProfile ABHAProfile) {
		this.ABHAProfile = ABHAProfile;
	}

	public EnrollByAadhaarTokens getTokens() {
		return tokens;
	}

	public void setTokens(EnrollByAadhaarTokens tokens) {
		this.tokens = tokens;
	}

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
