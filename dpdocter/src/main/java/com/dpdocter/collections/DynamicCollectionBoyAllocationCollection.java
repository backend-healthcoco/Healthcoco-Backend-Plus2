package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "dynamic_collection_boy_allocation_cl")
public class DynamicCollectionBoyAllocationCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId assignorId; // ParentLab for lab-lab or DentalLab for dentalWorks
	@Field
	private ObjectId assigneeId; // DaughterLab for lab-lab or Doctor for dental works
	@Field
	private ObjectId collectionBoyId;
	@Field
	private Long fromTime;
	@Field
	private Long toTime;
	@Field
	private String type;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getAssignorId() {
		return assignorId;
	}

	public void setAssignorId(ObjectId assignorId) {
		this.assignorId = assignorId;
	}

	public ObjectId getAssigneeId() {
		return assigneeId;
	}

	public void setAssigneeId(ObjectId assigneeId) {
		this.assigneeId = assigneeId;
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

	public ObjectId getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(ObjectId collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "DynamicCollectionBoyAllocationCollection [id=" + id + ", assignorId=" + assignorId + ", assigneeId="
				+ assigneeId + ", fromTime=" + fromTime + ", toTime=" + toTime + "]";
	}

}
