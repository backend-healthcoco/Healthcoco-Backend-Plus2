package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "block_user_cl")
public class BlockUserCollection extends GenericCollection {

	@Field
	private ObjectId id;

	@Field
	private List<ObjectId> userIds;

	@Field
	private Boolean discarded = false;

	@Field
	private Boolean isForHour = false;

	@Field
	private Boolean isForDay = false;

	public Boolean getIsForHour() {
		return isForHour;
	}

	public Boolean getIsForDay() {
		return isForDay;
	}

	public void setIsForHour(Boolean isForHour) {
		this.isForHour = isForHour;
	}

	public void setIsForDay(Boolean isForDay) {
		this.isForDay = isForDay;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public ObjectId getId() {
		return id;
	}

	public List<ObjectId> getUserIds() {
		return userIds;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public void setUserIds(List<ObjectId> userIds) {
		this.userIds = userIds;
	}

}
