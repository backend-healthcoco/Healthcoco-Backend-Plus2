package com.dpdocter.request;

import com.dpdocter.beans.FetchResponse;
import com.dpdocter.beans.NdhmErrorObject;

public class ConsentOnInitRequest {

	private String requestId;
	private String timestamp;
	private ConsentDataFlowRequest consentRequest;
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

	

	public ConsentDataFlowRequest getConsentRequest() {
		return consentRequest;
	}

	public void setConsentRequest(ConsentDataFlowRequest consentRequest) {
		this.consentRequest = consentRequest;
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
