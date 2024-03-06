package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.FetchResponse;
import com.dpdocter.beans.NdhmErrorObject;
import com.dpdocter.beans.OnPatientFind;

@Document(collection = "ndhm_patient_find_cl")
public class NdhmPatientFindCollection extends GenericCollection{
	@Id
	private ObjectId id;
	@Field
	private String requestId;
	@Field
	private String timestamp;
	@Field
	private OnPatientFind patient;
	@Field
	private NdhmErrorObject error;
	@Field
	private FetchResponse resp;
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
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public OnPatientFind getPatient() {
		return patient;
	}
	public void setPatient(OnPatientFind patient) {
		this.patient = patient;
	}
	public NdhmErrorObject getError() {
		return error;
	}
	public void setError(NdhmErrorObject error) {
		this.error = error;
	}
	public FetchResponse getResp() {
		return resp;
	}
	public void setResp(FetchResponse resp) {
		this.resp = resp;
	}
	
	
	
}
