package com.dpdocter.beans;

public class OnFetchModesRequest {

	private String requestId;
	
	private String timestamp;
	
	private NdhmAuth auth;
	
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

	public NdhmAuth getAuth() {
		return auth;
	}

	public void setAuth(NdhmAuth auth) {
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
