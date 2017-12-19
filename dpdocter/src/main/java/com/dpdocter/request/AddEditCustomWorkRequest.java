package com.dpdocter.request;

public class AddEditCustomWorkRequest {

	private String id;
	private String workName;
	private Boolean isShadeRequired = false;
	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWorkName() {
		return workName;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
	}

	public Boolean getIsShadeRequired() {
		return isShadeRequired;
	}

	public void setIsShadeRequired(Boolean isShadeRequired) {
		this.isShadeRequired = isShadeRequired;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "AddEditCustomWorkRequest [id=" + id + ", workName=" + workName + ", isShadeRequired=" + isShadeRequired
				+ ", discarded=" + discarded + "]";
	}

}
