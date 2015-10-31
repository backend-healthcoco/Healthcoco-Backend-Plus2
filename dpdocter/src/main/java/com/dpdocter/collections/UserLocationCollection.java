package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "user_location_cl")
public class UserLocationCollection extends GenericCollection {
    @Id
    private String id;

    @Field
    private String userId;

    @Field
    private String locationId;
    
    @Field
    private Boolean isActive = false;

    @Field
    private Boolean isVerified = false;


    public UserLocationCollection(String userId, String locationId) {
	this.userId = userId;
	this.locationId = locationId;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getUserId() {
	return userId;
    }

    public void setUserId(String userId) {
	this.userId = userId;
    }

    public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	@Override
	public String toString() {
		return "UserLocationCollection [id=" + id + ", userId=" + userId + ", locationId=" + locationId + ", isActive="
				+ isActive + ", isVerified=" + isVerified + "]";
	}
}
