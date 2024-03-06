package com.dpdocter.beans;

public class StatusNotify {

	private String sessionStatus;
	
	private String hipId;
	
	private StatusResponse statusResponses;

	public String getSessionStatus() {
		return sessionStatus;
	}

	public void setSessionStatus(String sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	public String getHipId() {
		return hipId;
	}

	public void setHipId(String hipId) {
		this.hipId = hipId;
	}

	public StatusResponse getStatusResponses() {
		return statusResponses;
	}

	public void setStatusResponses(StatusResponse statusResponses) {
		this.statusResponses = statusResponses;
	}
	
	
}
