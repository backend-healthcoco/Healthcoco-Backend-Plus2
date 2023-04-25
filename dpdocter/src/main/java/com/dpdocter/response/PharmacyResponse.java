package com.dpdocter.response;

import com.dpdocter.enums.ReplyType;

public class PharmacyResponse {

	private ReplyType replyType;

	private String uniqueRequestId;

	private String localeId;

	private String userId;

	public ReplyType getReplyType() {
		return replyType;
	}

	public void setReplyType(ReplyType replyType) {
		this.replyType = replyType;
	}

	public String getUniqueRequestId() {
		return uniqueRequestId;
	}

	public void setUniqueRequestId(String uniqueRequestId) {
		this.uniqueRequestId = uniqueRequestId;
	}

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

	@Override
	public String toString() {
		return "PharmacyResponse [replyType=" + replyType + ", uniqueRequestId=" + uniqueRequestId + ", localeId="
				+ localeId + ", userId=" + userId + "]";
	}

}
