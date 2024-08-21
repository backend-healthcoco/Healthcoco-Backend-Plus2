package com.dpdocter.webservices;

import com.dpdocter.beans.FetchResponse;
import com.dpdocter.beans.NdhmErrorObject;

public class GateWayOnRequest {

	private String requestId;
	private String timestamp;
	private GateWayHiOnRequest hiRequest;
	private NdhmErrorObject error;
	private FetchResponse response;

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

	public GateWayHiOnRequest getHiRequest() {
		return hiRequest;
	}

	public void setHiRequest(GateWayHiOnRequest hiRequest) {
		this.hiRequest = hiRequest;
	}

	public NdhmErrorObject getError() {
		return error;
	}

	public void setError(NdhmErrorObject error) {
		this.error = error;
	}

	public FetchResponse getResponse() {
		return response;
	}

	public void setResponse(FetchResponse response) {
		this.response = response;
	}

}
