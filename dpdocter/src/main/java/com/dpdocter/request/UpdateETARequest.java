package com.dpdocter.request;

public class UpdateETARequest {

	private String requestId;
	private String uniqueWorkId;
	private Long etaInDate;
	private Integer etaInHour;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getUniqueWorkId() {
		return uniqueWorkId;
	}

	public void setUniqueWorkId(String uniqueWorkId) {
		this.uniqueWorkId = uniqueWorkId;
	}

	public Long getEtaInDate() {
		return etaInDate;
	}

	public void setEtaInDate(Long etaInDate) {
		this.etaInDate = etaInDate;
	}

	public Integer getEtaInHour() {
		return etaInHour;
	}

	public void setEtaInHour(Integer etaInHour) {
		this.etaInHour = etaInHour;
	}

	@Override
	public String toString() {
		return "UpdateETARequest [requestId=" + requestId + ", uniqueWorkId=" + uniqueWorkId + ", etaInDate="
				+ etaInDate + ", etaInHour=" + etaInHour + "]";
	}

}
