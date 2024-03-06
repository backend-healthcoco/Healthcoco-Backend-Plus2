package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.FetchResponse;
import com.dpdocter.beans.NdhmErrorObject;
import com.dpdocter.request.ConsentDataFlowRequest;

@Document(collection = "consent_request_init_cl")
public class ConsentOnInitRequestCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private String requestId;
	@Field
	private String timestamp;
	@Field
	private ConsentDataFlowRequest consentRequest;
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
	public ConsentDataFlowRequest getConsentRequest() {
		return consentRequest;
	}
	public void setConsentRequest(ConsentDataFlowRequest consentRequest) {
		this.consentRequest = consentRequest;
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
