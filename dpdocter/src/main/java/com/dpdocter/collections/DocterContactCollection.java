package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="docter_contact_cl")
public class DocterContactCollection {

	@Id
	private String id;
	
	@Field
	private String docterId;
	
	@Field
	private String contactId;
	
	@Field
	private Boolean isBlocked;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDocterId() {
		return docterId;
	}

	public void setDocterId(String docterId) {
		this.docterId = docterId;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public Boolean getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(Boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	@Override
	public String toString() {
		return "DocterContactCollection [id=" + id + ", docterId=" + docterId
				+ ", contactId=" + contactId + ", isBlocked=" + isBlocked + "]";
	}
	
	
}
