package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "token_cl")
public class TokenCollection extends GenericCollection {

	@Id
	private String id;

	@Field
	private Boolean isUsed = false;

	@Field
	private ObjectId resourceId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	public ObjectId getResourceId() {
		return resourceId;
	}

	public void setResourceId(ObjectId resourceId) {
		this.resourceId = resourceId;
	}

	@Override
	public String toString() {
		return "TokenCollection [id=" + id + ", isUsed=" + isUsed + ", resourceId=" + resourceId + "]";
	}
}
