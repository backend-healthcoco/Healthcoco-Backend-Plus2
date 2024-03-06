package com.dpdocter.request;

public class DataFlowRequest {

	private String requestId;
	private String timestamp;
	private String transactionId;
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
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public HiRequestNdhmDataFlow getHiRequest() {
		return hiRequest;
	}
	public void setHiRequest(HiRequestNdhmDataFlow hiRequest) {
		this.hiRequest = hiRequest;
	}
			   
	
}
