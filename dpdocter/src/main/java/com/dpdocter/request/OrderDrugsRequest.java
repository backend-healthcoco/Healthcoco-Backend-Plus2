package com.dpdocter.request;

import com.dpdocter.enums.ReplyType;

public class OrderDrugsRequest {

	private String localeId;

	private String userId;

	private String uniqueRequestId;

	private String uniqueResponseId;

	private ReplyType replyType ;
	

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
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

	public String getUniqueResponseId() {
		return uniqueResponseId;
	}

	public void setUniqueResponseId(String uniqueResponseId) {
		this.uniqueResponseId = uniqueResponseId;
	}

	@Override
	public String toString() {
		return "OrderDrugsRequest [localeId=" + localeId + ", userId=" + userId + ", uniqueRequestId=" + uniqueRequestId
				+ ", uniqueResponseId=" + uniqueResponseId + "]";
	}

	public ReplyType getReplyType() {
		return replyType;
	}

	public void setReplyType(ReplyType replyType) {
		this.replyType = replyType;
	}

}
