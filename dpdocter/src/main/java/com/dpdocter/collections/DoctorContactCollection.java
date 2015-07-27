package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "doctor_contact_cl")
public class DoctorContactCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String doctorId;

    @Field
    private String contactId;

    @Field
    private Boolean isBlocked = false;

    @Field
    private Long createdDate = System.currentTimeMillis();

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
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

    public Long getCreatedDate() {
	return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
	this.createdDate = createdDate;
    }

    @Override
    public String toString() {
	return "DocterContactCollection [id=" + id + ", doctorId=" + doctorId + ", contactId=" + contactId + ", isBlocked=" + isBlocked + "]";
    }

}
