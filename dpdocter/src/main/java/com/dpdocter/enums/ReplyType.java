package com.dpdocter.enums;

public enum ReplyType {

	YES("YES"), NO("NO"), REQUESTED("REQUESTED");
	
	private String replyType;

	public String getReplyType() {
		return replyType;
	}

	private ReplyType(String replyType) {
		this.replyType = replyType;
	}
}
