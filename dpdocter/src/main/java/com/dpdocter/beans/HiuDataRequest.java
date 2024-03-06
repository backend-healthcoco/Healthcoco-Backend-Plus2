package com.dpdocter.beans;

import com.dpdocter.request.HiRequestNdhmDataFlow;

public class HiuDataRequest {

	private String requestId;
	private String timestamp;

	private HiRequestNdhmDataFlow hiRequest;

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

	public HiRequestNdhmDataFlow getHiRequest() {
		return hiRequest;
	}

	public void setHiRequest(HiRequestNdhmDataFlow hiRequest) {
		this.hiRequest = hiRequest;
	}
	
	
}
