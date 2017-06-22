package com.dpdocter.enums;

public enum ReplyType {

	YES("YES"), NO("NO"), PICKUP_REQUESTED("PICKUP_REQUESTED"), ACCEPTED("ACCEPTED"), DECLINED("DECLINED"),;

	private String replyType;

	public String getReplyType() {
		return replyType;
	}

	private ReplyType(String replyType) {
		this.replyType = replyType;
	}
}
