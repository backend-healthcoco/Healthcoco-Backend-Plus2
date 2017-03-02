package com.dpdocter.response;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Discount;

public class SearchRequestToPharmacyResponse {

	private String id;

	private String userId;

	private String uniqueRequestId;

	private String replyType;

	private String localeId;

	private String discount;

	private String uniqueResponseId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUniqueRequestId() {
		return uniqueRequestId;
	}

	public void setUniqueRequestId(String uniqueRequestId) {
		this.uniqueRequestId = uniqueRequestId;
	}

	public String getReplyType() {
		return replyType;
	}

	public void setReplyType(String replyType) {
		this.replyType = replyType;
	}

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getUniqueResponseId() {
		return uniqueResponseId;
	}

	public void setUniqueResponseId(String uniqueResponseId) {
		this.uniqueResponseId = uniqueResponseId;
	}

	@Override
	public String toString() {
		return "SearchRequestToPharmacyResponse [id=" + id + ", userId=" + userId + ", uniqueRequestId="
				+ uniqueRequestId + ", replyType=" + replyType + ", localeId=" + localeId + ", discount=" + discount
				+ ", uniqueResponseId=" + uniqueResponseId + "]";
	}

}
