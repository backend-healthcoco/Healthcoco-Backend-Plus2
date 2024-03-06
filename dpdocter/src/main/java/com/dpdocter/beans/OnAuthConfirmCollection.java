package com.dpdocter.beans;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;

@Document(collection = "on_auth_confirm_cl")
public class OnAuthConfirmCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private String requestId;
	@Field
	private String timestamp;
	@Field
	private NdhmAuthConfirm auth;
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
	public NdhmAuthConfirm getAuth() {
		return auth;
	}
	public void setAuth(NdhmAuthConfirm auth) {
		this.auth = auth;
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
