package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.PatientQRCode;

@Document(collection = "patient_share_profile_cl")
public class PatientShareProfileCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private String requestId;
	@Field
	private String timeStamp;
	@Field
	private PatientQRCode patient;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public PatientQRCode getPatient() {
		return patient;
	}

	public void setPatient(PatientQRCode patient) {
		this.patient = patient;
	}
	
	
}
