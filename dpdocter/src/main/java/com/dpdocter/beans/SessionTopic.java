package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class SessionTopic extends GenericCollection{
	
	private String id;
	
	private String topic;
	
	private Boolean discarded=false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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
		return "SessionTopic [id=" + id + ", topic=" + topic + ", discarded=" + discarded + ", getId()=" + getId()
				+ ", getTopic()=" + getTopic() + ", getDiscarded()=" + getDiscarded() + ", getCreatedTime()="
				+ getCreatedTime() + ", getUpdatedTime()=" + getUpdatedTime() + ", getCreatedBy()=" + getCreatedBy()
				+ ", getAdminCreatedTime()=" + getAdminCreatedTime() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	
	

}
