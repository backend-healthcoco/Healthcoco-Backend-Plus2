package com.dpdocter.beans;

public class FetchModesRequest {
 
	private String requestId;
	
	private String timeStamp;
	
	private FetchModeQuery query;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public FetchModeQuery getQuery() {
		return query;
	}

	public void setQuery(FetchModeQuery query) {
		this.query = query;
	} 
	
	
	
	
}
