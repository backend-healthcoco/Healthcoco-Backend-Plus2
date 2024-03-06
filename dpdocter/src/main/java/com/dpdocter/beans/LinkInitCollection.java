package com.dpdocter.beans;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;

@Document(collection = "link_init_cl")
public class LinkInitCollection extends GenericCollection{
	
	@Id
	private ObjectId id;
	
	@Field
	private String requestId;
	
	@Field
	private String timestamp;
	
	@Field
	private String transactionId;
	
	@Field
	private LinkPatientRequest patient;

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

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public LinkPatientRequest getPatient() {
		return patient;
	}

	public void setPatient(LinkPatientRequest patient) {
		this.patient = patient;
	}
	
	

}
