package com.dpdocter.collections;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.response.MessageData;

@Document(collection = "message_cl")
public class MessageCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private String body;
	@Field
	private String sender;
	@Field
	private String type;
	@Field
	private String source;
	@Field
	private String messageId;
	@Field
	private String createdDateTime;
	@Field
	private Integer  totalCount;
	
	@Field
	private Integer unicode;
	
	@Field
	private List<MessageData> data;
	@Field
	private String dlrurl;
	@Field
	private Map<String,String>error;
	
	@Field
	private ObjectId doctorId;
	
	@Field
	private ObjectId hospitalId;
	
	@Field
	private ObjectId locationId;
	
	@Field
	private String messageType;
	
	@Field
	private Long totalCreditsSpent=0L;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	
	

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public List<MessageData> getData() {
		return data;
	}

	public void setData(List<MessageData> data) {
		this.data = data;
	}

	public String getDlrurl() {
		return dlrurl;
	}

	public void setDlrurl(String dlrurl) {
		this.dlrurl = dlrurl;
	}

	public Map<String, String> getError() {
		return error;
	}

	public void setError(Map<String, String> error) {
		this.error = error;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Long getTotalCreditsSpent() {
		return totalCreditsSpent;
	}

	public void setTotalCreditsSpent(Long totalCreditsSpent) {
		this.totalCreditsSpent = totalCreditsSpent;
	}

	public Integer getUnicode() {
		return unicode;
	}

	public void setUnicode(Integer unicode) {
		this.unicode = unicode;
	}

	
	

}
