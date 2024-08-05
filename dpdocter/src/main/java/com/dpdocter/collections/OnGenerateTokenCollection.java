package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.FetchResponse;

@Document(collection = "on-generate-token_cl")
public class OnGenerateTokenCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String abhaAddress;
	@Field
	private String linkToken;
	@Field
	private FetchResponse response;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getAbhaAddress() {
		return abhaAddress;
	}

	public void setAbhaAddress(String abhaAddress) {
		this.abhaAddress = abhaAddress;
	}

	public String getLinkToken() {
		return linkToken;
	}

	public void setLinkToken(String linkToken) {
		this.linkToken = linkToken;
	}

	public FetchResponse getResponse() {
		return response;
	}

	public void setResponse(FetchResponse response) {
		this.response = response;
	}

}
