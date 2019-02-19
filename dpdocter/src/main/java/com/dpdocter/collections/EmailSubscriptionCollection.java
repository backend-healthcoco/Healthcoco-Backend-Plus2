package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "email_subscription_cl")
public class EmailSubscriptionCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private ObjectId subscriberId;

	@Field
	private String reason;

	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public ObjectId getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(ObjectId subscriberId) {
		this.subscriberId = subscriberId;
	}

}
