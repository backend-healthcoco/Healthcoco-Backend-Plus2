package com.dpdocter.enums;

public enum ReplyType {

	YES("YES"), NO("NO"), REQUESTED("REQUESTED"), ACCEPTED("ACCEPTED"), DECLINED("DECLINED"),;

	private String replyType;

	public String getReplyType() {
		return replyType;
	}

	private ReplyType(String replyType) {
		this.replyType = replyType;
	}
}
