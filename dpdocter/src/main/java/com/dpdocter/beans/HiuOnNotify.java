package com.dpdocter.beans;

import java.util.List;

public class HiuOnNotify {

	private String requestId;
	
	private String timestamp;
	
	private List<AcknowledgementRequest> acknowledgement;
	
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

	public List<AcknowledgementRequest> getAcknowledgement() {
		return acknowledgement;
	}

	public void setAcknowledgement(List<AcknowledgementRequest> acknowledgement) {
		this.acknowledgement = acknowledgement;
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
