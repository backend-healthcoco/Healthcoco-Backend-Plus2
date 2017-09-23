package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.Resource;

@Document(collection = "user_resource_favourite_cl")
public class UserResourceFavouriteCollection extends GenericCollection{
	
	@Id
	private ObjectId id;

	@Field
	private ObjectId userId;

	@Field
	private ObjectId resourceId;
	
	@Field
	private Resource resourceType;
	
	@Field
	private ObjectId locationId; //For resourceType = DOCTOR only
	
	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public ObjectId getResourceId() {
		return resourceId;
	}

	public void setResourceId(ObjectId resourceId) {
		this.resourceId = resourceId;
	}

	public Resource getResourceType() {
		return resourceType;
	}

	public void setResourceType(Resource resourceType) {
		this.resourceType = resourceType;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	@Override
	public String toString() {
		return "UserResourceFavouriteCollection [id=" + id + ", userId=" + userId + ", resourceId=" + resourceId
				+ ", resourceType=" + resourceType + ", locationId=" + locationId + ", discarded=" + discarded + "]";
	}
}
