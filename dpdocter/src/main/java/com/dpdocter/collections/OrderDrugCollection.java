package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.ReplyType;

@Document(collection = "order_drug_cl")
public class OrderDrugCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId localeId;
	@Field
	private ObjectId userId;
	@Field
	private String uniqueRequestId;
	@Field
	private String uniqueResponseId;
	@Field
	private ReplyType replyType = ReplyType.PICKUP_REQUESTED;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getLocaleId() {
		return localeId;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public void setLocaleId(ObjectId localeId) {
		this.localeId = localeId;
	}

	public String getUniqueRequestId() {
		return uniqueRequestId;
	}

	public void setUniqueRequestId(String uniqueRequestId) {
		this.uniqueRequestId = uniqueRequestId;
	}

	public String getUniqueResponseId() {
		return uniqueResponseId;
	}

	public void setUniqueResponseId(String uniqueResponseId) {
		this.uniqueResponseId = uniqueResponseId;
	}

	@Override
	public String toString() {
		return "OrderDrugCollection [id=" + id + ", localeId=" + localeId + ", patientId=" + ", uniqueRequestId="
				+ uniqueRequestId + ", uniqueResponseId=" + uniqueResponseId + "]";
	}

	public ReplyType getReplyType() {
		return replyType;
	}

	public void setReplyType(ReplyType replyType) {
		this.replyType = replyType;
	}

}
