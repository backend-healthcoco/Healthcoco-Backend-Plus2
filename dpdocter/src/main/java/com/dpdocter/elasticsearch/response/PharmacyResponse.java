package com.dpdocter.elasticsearch.response;

import com.dpdocter.enums.ReplyType;

public class PharmacyResponse {

	private ReplyType replyType;
	
	private String requestId;

	public ReplyType getReplyType() {
		return replyType;
	}

	public void setReplyType(ReplyType replyType) {
		this.replyType = replyType;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@Override
	public String toString() {
		return "PharmacyResponse [replyType=" + replyType + ", requestId=" + requestId + "]";
	}
	
	
}
