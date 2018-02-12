package com.dpdocter.request;

public class DynamicCollectionBoyAllocationRequest {

	private String assignorId; // ParentLab for lab-lab or DentalLab for dentalWorks
	private String assigneeId; // DaughterLab for lab-lab or Doctor for dental works
	private String collectionBoyId;
	private Long fromTime;
	private Integer duration;
	private String type;

	public String getAssignorId() {
		return assignorId;
	}

	public void setAssignorId(String assignorId) {
		this.assignorId = assignorId;
	}

	public String getAssigneeId() {
		return assigneeId;
	}

	public void setAssigneeId(String assigneeId) {
		this.assigneeId = assigneeId;
	}

	public Long getFromTime() {
		return fromTime;
	}

	public void setFromTime(Long fromTime) {
		this.fromTime = fromTime;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(String collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
