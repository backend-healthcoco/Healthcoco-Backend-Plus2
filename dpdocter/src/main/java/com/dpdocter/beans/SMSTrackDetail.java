package com.dpdocter.beans;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;

@Document(collection = "sms_track_cl")
public class SMSTrackDetail extends GenericCollection{
    @Id
    private String id;

    @Field
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private List<SMSDetail> smsDetails;

    @Field
    private String responseId;

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

    public List<SMSDetail> getSmsDetails() {
	return smsDetails;
    }

    public void setSmsDetails(List<SMSDetail> smsDetails) {
	this.smsDetails = smsDetails;
    }

    public String getResponseId() {
	return responseId;
    }

    public void setResponseId(String responseId) {
	this.responseId = responseId;
    }

    @Override
    public String toString() {
	return "SMSTrackDetail [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", smsDetails="
		+ smsDetails + ", responseId=" + responseId + "]";
    }

}
