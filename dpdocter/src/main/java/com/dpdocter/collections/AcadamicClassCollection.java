package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "acadamic_class_cl")
public class AcadamicClassCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String name;
	@Field
	private ObjectId branchId;
	@Field
	private ObjectId schoolId;
	@Field
	private ObjectId sectionId;
	@Field
	private ObjectId teacherId;
	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

	}

	public ObjectId getBranchId() {
		return branchId;
	}

	public void setBranchId(ObjectId branchId) {
		this.branchId = branchId;
	}

	public ObjectId getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(ObjectId schoolId) {
		this.schoolId = schoolId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public ObjectId getSectionId() {
		return sectionId;
	}

	public void setSectionId(ObjectId sectionId) {
		this.sectionId = sectionId;
	}

	public ObjectId getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(ObjectId teacherId) {
		this.teacherId = teacherId;
	}

}
