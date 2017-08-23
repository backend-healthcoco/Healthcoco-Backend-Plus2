package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "user_allowance_details_cl")
public class UserAllowanceDetailsCollection extends  GenericCollection{

	@Id
	private ObjectId id;
	
	@Field
	private List<ObjectId> userIds;
	
    @Field
    private Double allowedRecordsSizeInMB = 50.0;

    @Field
    private Double availableRecordsSizeInMB = 50.0;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<ObjectId> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<ObjectId> userIds) {
		this.userIds = userIds;
	}

	public Double getAllowedRecordsSizeInMB() {
		return allowedRecordsSizeInMB;
	}

	public void setAllowedRecordsSizeInMB(Double allowedRecordsSizeInMB) {
		this.allowedRecordsSizeInMB = allowedRecordsSizeInMB;
	}

	public Double getAvailableRecordsSizeInMB() {
		return availableRecordsSizeInMB;
	}

	public void setAvailableRecordsSizeInMB(Double availableRecordsSizeInMB) {
		this.availableRecordsSizeInMB = availableRecordsSizeInMB;
	}

	@Override
	public String toString() {
		return "UserAllowanceDetailsCollection [id=" + id + ", userIds=" + userIds + ", allowedRecordsSizeInMB="
				+ allowedRecordsSizeInMB + ", availableRecordsSizeInMB=" + availableRecordsSizeInMB + "]";
	}
    
}
