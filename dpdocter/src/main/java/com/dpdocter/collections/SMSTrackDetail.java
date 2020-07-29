package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.SMSDetail;

@Document(collection = "sms_track_cl")
@CompoundIndexes({
    @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}")
})
public class SMSTrackDetail extends GenericCollection {
    @Id
    private ObjectId id;

    @Indexed
    private ObjectId doctorId;

    @Field
    private ObjectId locationId;

    @Field
    private ObjectId hospitalId;

    @Field
    private List<SMSDetail> smsDetails;

    @Field
    private String type;

    @Field
    private String responseId;
    
    @Field
    private Long totalCreditsSpent=0L; 

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public ObjectId getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(ObjectId doctorId) {
	this.doctorId = doctorId;
    }

    public ObjectId getLocationId() {
	return locationId;
    }

    public void setLocationId(ObjectId locationId) {
	this.locationId = locationId;
    }

    public ObjectId getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(ObjectId hospitalId) {
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

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }
    
    
   

	

	

	public Long getTotalCreditsSpent() {
		return totalCreditsSpent;
	}

	public void setTotalCreditsSpent(Long totalCreditsSpent) {
		this.totalCreditsSpent = totalCreditsSpent;
	}

	@Override
    public String toString() {
	return "SMSTrackDetail [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", smsDetails="
		+ smsDetails + ", type=" + type + ", responseId=" + responseId + "]";
    }
}
