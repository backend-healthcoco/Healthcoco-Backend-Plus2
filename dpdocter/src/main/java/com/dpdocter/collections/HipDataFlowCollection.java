package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.request.HiRequestNdhmDataFlow;

@Document(collection = "hip_data_flow_cl")
public class HipDataFlowCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private String requestId;
	@Field
	private String timestamp;
	@Field
	private String transactionId;
	@Field
	private HiRequestNdhmDataFlow hiRequest;
	
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
	public HiRequestNdhmDataFlow getHiRequest() {
		return hiRequest;
	}
	public void setHiRequest(HiRequestNdhmDataFlow hiRequest) {
		this.hiRequest = hiRequest;
	}
	
	
}
