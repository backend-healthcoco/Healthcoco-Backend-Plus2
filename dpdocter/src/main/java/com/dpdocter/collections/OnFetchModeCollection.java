package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.FetchResponse;
import com.dpdocter.beans.NdhmAuth;
import com.dpdocter.beans.NdhmErrorObject;

@Document(collection = "on_fetch_cl")
public class OnFetchModeCollection extends GenericCollection{

	@Id
	private ObjectId id;
	
	@Field
	private String requestId;
	
	@Field
	private String timestamp;
	
	@Field
	private NdhmAuth auth;
	
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

	public NdhmAuth getAuth() {
		return auth;
	}

	public void setAuth(NdhmAuth auth) {
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
