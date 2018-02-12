package com.dpdocter.response;

import com.dpdocter.collections.GenericCollection;

public class DynamicCollectionBoyAllocationResponse extends GenericCollection {

	private String id;
	private String assignorId; // ParentLab for lab-lab or DentalLab for dentalWorks
	private String assigneeId; // DaughterLab for lab-lab or Doctor for dental works
	private String collectionBoyId;
	private Long fromTime;
	private Long toTime;
	private String type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(String collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public Long getFromTime() {
		return fromTime;
	}

	public void setFromTime(Long fromTime) {
		this.fromTime = fromTime;
	}

	public Long getToTime() {
		return toTime;
	}

	public void setToTime(Long toTime) {
		this.toTime = toTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
