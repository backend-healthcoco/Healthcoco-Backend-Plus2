package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "doctor_contact_cl")
public class DoctorContactCollection extends GenericCollection implements Comparable<DoctorContactCollection>{

    @Id
    private String id;

    @Field
    private String doctorId;

    @Field
    private String contactId;

    @Field
    private Boolean isBlocked = false;

    @Field
    private Boolean discarded = false;

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

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

    @Override
    public String toString() {
	return "DoctorContactCollection [id=" + id + ", doctorId=" + doctorId + ", contactId=" + contactId + ", isBlocked=" + isBlocked + ", discarded="
		+ discarded + "]";
    }

    @Override
    public int compareTo(DoctorContactCollection contactCollection) {
        if (this.getCreatedTime().getTime() > contactCollection.getCreatedTime().getTime()){
            return -1;
        }else{
            return 1;
        }
    }
}
