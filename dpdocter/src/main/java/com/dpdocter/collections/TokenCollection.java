package com.dpdocter.collections;

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
    private String userLocationId;

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

	public String getUserLocationId() {
		return userLocationId;
	}

	public void setUserLocationId(String userLocationId) {
		this.userLocationId = userLocationId;
	}

	@Override
	public String toString() {
		return "TokenCollection [id=" + id + ", isUsed=" + isUsed + ", userLocationId=" + userLocationId + "]";
	}

}
