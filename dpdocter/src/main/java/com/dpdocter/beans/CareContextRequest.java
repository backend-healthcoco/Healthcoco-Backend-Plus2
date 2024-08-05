package com.dpdocter.beans;

public class CareContextRequest {

	private String requestId;
	
	private String timestamp;
	private String hipId;

	private long abhaNumber;
	private String abhaAddress;

	private CareContextLink link;

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

	public CareContextLink getLink() {
		return link;
	}

	public void setLink(CareContextLink link) {
		this.link = link;
	}

	public String getHipId() {
		return hipId;
	}

	public void setHipId(String hipId) {
		this.hipId = hipId;
	}

	public long getAbhaNumber() {
		return abhaNumber;
	}

	public void setAbhaNumber(long abhaNumber) {
		this.abhaNumber = abhaNumber;
	}

	public String getAbhaAddress() {
		return abhaAddress;
	}

	public void setAbhaAddress(String abhaAddress) {
		this.abhaAddress = abhaAddress;
	}
	
	
}
