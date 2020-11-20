package com.dpdocter.beans;

public class OnAuthConfirmRequest {

	private String requestId;
	
	private String timestamp;
	
	private NdhmAuthConfirm auth;
	
	private NdhmErrorObject error;
	
	private FetchResponse resp;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public NdhmAuthConfirm getAuth() {
		return auth;
	}

	public void setAuth(NdhmAuthConfirm auth) {
		this.auth = auth;
	}

	public NdhmErrorObject getError() {
		return error;
	}

	public void setError(NdhmErrorObject error) {
		this.error = error;
	}

	public FetchResponse getResp() {
		return resp;
	}

	public void setResp(FetchResponse resp) {
		this.resp = resp;
	}

	
}
