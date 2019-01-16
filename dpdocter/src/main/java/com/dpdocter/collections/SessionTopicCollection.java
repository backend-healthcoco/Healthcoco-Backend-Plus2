package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "session_topic_cl")
public class SessionTopicCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String topic;
	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "SessionTopicCollection [id=" + id + ", topic=" + topic + ", discarded=" + discarded + ", getId()="
				+ getId() + ", getTopic()=" + getTopic() + ", getDiscarded()=" + getDiscarded() + ", getCreatedTime()="
				+ getCreatedTime() + ", getUpdatedTime()=" + getUpdatedTime() + ", getCreatedBy()=" + getCreatedBy()
				+ ", getAdminCreatedTime()=" + getAdminCreatedTime() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}

}
