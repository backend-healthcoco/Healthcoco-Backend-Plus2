package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.ConsultationType;

@Document(collection = "unified_communication_details_cl")
public class UnifiedCommunicationDetailsCollection extends GenericCollection{

	@Id
	private ObjectId id;
	
	@Field
	private ConsultationType type;
	
	@Field
	private ObjectId userId;
	
	@Field
	private Integer ttl;
	
	@Field
	private Boolean isExpired = false;
	
	@Field
	private String token;

	public UnifiedCommunicationDetailsCollection(ConsultationType type, ObjectId userId, Integer ttl, Boolean isExpired,
			String token) {
		super();
		this.type = type;
		this.userId = userId;
		this.ttl = ttl;
		this.isExpired = isExpired;
		this.token = token;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ConsultationType getType() {
		return type;
	}

	public void setType(ConsultationType type) {
		this.type = type;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public Integer getTtl() {
		return ttl;
	}

	public void setTtl(Integer ttl) {
		this.ttl = ttl;
	}

	public Boolean getIsExpired() {
		return isExpired;
	}

	public void setIsExpired(Boolean isExpired) {
		this.isExpired = isExpired;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "UnifiedCommunicationDetailsCollection [id=" + id + ", type=" + type + ", userId=" + userId + ", ttl="
				+ ttl + ", isExpired=" + isExpired + ", token=" + token + "]";
	}
}
