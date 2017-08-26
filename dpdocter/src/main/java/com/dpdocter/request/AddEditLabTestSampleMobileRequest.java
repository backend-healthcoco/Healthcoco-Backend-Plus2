package com.dpdocter.request;

public class AddEditLabTestSampleMobileRequest {

	private String id;
	private String status;
	private Boolean isCollected;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getIsCollected() {
		return isCollected;
	}

	public void setIsCollected(Boolean isCollected) {
		this.isCollected = isCollected;
	}

	@Override
	public String toString() {
		return "AddEditLabTestSampleMobileRequest [id=" + id + ", status=" + status + ", isCollected=" + isCollected
				+ "]";
	}

}
