package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.CareContextAcknowledgement;
import com.dpdocter.beans.FetchResponse;
import com.dpdocter.beans.NdhmErrorObject;

@Document(collection = "on_care_context_cl")
public class OnCareContextCollection extends GenericCollection{
	
	@Field
	private ObjectId id;
	
	@Field
	private String requestId;
	
	@Field
	private String timestamp;
	
	@Field
	private CareContextAcknowledgement acknowledgement;
	
	@Field
	private NdhmErrorObject error;
	
	@Field
	private FetchResponse response;
	
	@Field
	private String abhaAddress;
	@Field
	private String status;


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

	public CareContextAcknowledgement getAcknowledgement() {
		return acknowledgement;
	}

	public void setAcknowledgement(CareContextAcknowledgement acknowledgement) {
		this.acknowledgement = acknowledgement;
	}

	public NdhmErrorObject getError() {
		return error;
	}

	public void setError(NdhmErrorObject error) {
		this.error = error;
	}

	public FetchResponse getResponse() {
		return response;
	}

	public void setResponse(FetchResponse response) {
		this.response = response;
	}

	public String getAbhaAddress() {
		return abhaAddress;
	}

	public void setAbhaAddress(String abhaAddress) {
		this.abhaAddress = abhaAddress;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
