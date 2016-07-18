package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "user_location_cl")
public class UserLocationCollection extends GenericCollection {
    @Id
    private ObjectId id;

    @Indexed
    private ObjectId userId;

    @Indexed
    private ObjectId locationId;

    @Field
    private Boolean isActivate = false;

    @Field
    private Boolean isVerified = true;

    @Field
    private Boolean discarded = false;

    public UserLocationCollection(ObjectId userId, ObjectId locationId) {
	this.userId = userId;
	this.locationId = locationId;
    }

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

    public ObjectId getLocationId() {
	return locationId;
    }

    public void setLocationId(ObjectId locationId) {
	this.locationId = locationId;
    }

    public Boolean getIsActivate() {
	return isActivate;
    }

    public void setIsActivate(Boolean isActivate) {
	this.isActivate = isActivate;
    }

    public Boolean getIsVerified() {
	return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
	this.isVerified = isVerified;
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

    @Override
    public String toString() {
	return "UserLocationCollection [id=" + id + ", userId=" + userId + ", locationId=" + locationId + ", isActivate=" + isActivate + ", isVerified="
		+ isVerified + ", discarded=" + discarded + "]";
    }
}
