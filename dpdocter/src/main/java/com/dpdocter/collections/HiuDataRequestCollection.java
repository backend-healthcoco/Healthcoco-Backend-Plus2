package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.FetchResponse;
import com.dpdocter.beans.NdhmErrorObject;
import com.dpdocter.webservices.GateWayHiOnRequest;

@Document(collection = "hiu_data_request_cl")
public class HiuDataRequestCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String requestId;
	@Field
	private String timestamp;
	@Field
	private GateWayHiOnRequest hiRequest;
	@Field
	private NdhmErrorObject error;
	@Field
	private FetchResponse resp;
	@Field
	private String healthId;
	@Field
	private ObjectId doctorId;

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

	public GateWayHiOnRequest getHiRequest() {
		return hiRequest;
	}

	public void setHiRequest(GateWayHiOnRequest hiRequest) {
		this.hiRequest = hiRequest;
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

	public String getHealthId() {
		return healthId;
	}

	public void setHealthId(String healthId) {
		this.healthId = healthId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

}
