package com.dpdocter.response;

public class LocationWithPatientQueueDetails {

	private String locationId;
	
	private Integer scheduledPatientNum = 0;
	
	private Integer waitingPatientNum = 0;
	
	private Integer engagedPatientNum = 0;
	
	private Integer checkedOutPatientNum = 0;

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public Integer getScheduledPatientNum() {
		return scheduledPatientNum;
	}

	public void setScheduledPatientNum(Integer scheduledPatientNum) {
		this.scheduledPatientNum = scheduledPatientNum;
	}

	public Integer getWaitingPatientNum() {
		return waitingPatientNum;
	}

	public void setWaitingPatientNum(Integer waitingPatientNum) {
		this.waitingPatientNum = waitingPatientNum;
	}

	public Integer getEngagedPatientNum() {
		return engagedPatientNum;
	}

	public void setEngagedPatientNum(Integer engagedPatientNum) {
		this.engagedPatientNum = engagedPatientNum;
	}

	public Integer getCheckedOutPatientNum() {
		return checkedOutPatientNum;
	}

	public void setCheckedOutPatientNum(Integer checkedOutPatientNum) {
		this.checkedOutPatientNum = checkedOutPatientNum;
	}

	@Override
	public String toString() {
		return "LocationWithPatientQueueDetails [locationId=" + locationId + ", scheduledPatientNum="
				+ scheduledPatientNum + ", waitingPatientNum=" + waitingPatientNum + ", engagedPatientNum="
				+ engagedPatientNum + ", checkedOutPatientNum=" + checkedOutPatientNum + "]";
	}
}
