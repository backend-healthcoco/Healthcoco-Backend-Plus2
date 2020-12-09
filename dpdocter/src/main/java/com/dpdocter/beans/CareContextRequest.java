package com.dpdocter.beans;

public class CareContextRequest {

	private String requestId;
	
	private String timestamp;
	
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
	
	
	
	
}
