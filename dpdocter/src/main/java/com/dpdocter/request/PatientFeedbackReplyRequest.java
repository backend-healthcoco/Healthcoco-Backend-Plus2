package com.dpdocter.request;

public class PatientFeedbackReplyRequest {

	private String id;
	private String reply;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	@Override
	public String toString() {
		return "PatientFeedbackReplyRequest [id=" + id + ", reply=" + reply + "]";
	}

}
