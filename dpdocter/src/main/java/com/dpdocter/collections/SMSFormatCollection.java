package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.SMSFormatType;

@Document(collection = "sms_format_cl")
public class SMSFormatCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private SMSFormatType type;

    @Field
    private List<String> content;

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

    public SMSFormatType getType() {
	return type;
    }

    public void setType(SMSFormatType type) {
	this.type = type;
    }

    public List<String> getContent() {
	return content;
    }

    public void setContent(List<String> content) {
	this.content = content;
    }

    @Override
    public String toString() {
	return "SMSFormatCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", type=" + type
		+ ", content=" + content + "]";
    }

}
