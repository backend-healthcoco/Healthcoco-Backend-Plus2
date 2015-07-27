package com.dpdocter.collections;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "import_contacts_request_cl")
public class ImportContactsRequestCollection {
    @Field
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private String specialComments;

    @Field
    private String contactsFileUrl;

    @Field
    private String emailAddress;

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public String getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	this.hospitalId = hospitalId;
    }

    public String getSpecialComments() {
	return specialComments;
    }

    public void setSpecialComments(String specialComments) {
	this.specialComments = specialComments;
    }

    public String getContactsFileUrl() {
	return contactsFileUrl;
    }

    public void setContactsFileUrl(String contactsFileUrl) {
	this.contactsFileUrl = contactsFileUrl;
    }

    public String getEmailAddress() {
	return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
    }

    @Override
    public String toString() {
	return "ImportContactsRequestCollection [doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", specialComments="
		+ specialComments + ", contactsFileUrl=" + contactsFileUrl + ", emailAddress=" + emailAddress + "]";
    }

}
