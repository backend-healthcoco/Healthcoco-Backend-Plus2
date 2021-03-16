package com.dpdocter.beans;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MessageStatusData {

	private String message_id;
	
	private String status;
	
	private String status_details;
	
	private String recipient;
	
	private String sent_time;
	
	private String status_time;
	
	
	
	@JsonIgnore
	private String price;
	
	private String error_code;

	public String getMessage_id() {
		return message_id;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus_details() {
		return status_details;
	}

	public void setStatus_details(String status_details) {
		this.status_details = status_details;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getSent_time() {
		return sent_time;
	}

	public void setSent_time(String sent_time) {
		this.sent_time = sent_time;
	}

	public String getStatus_time() {
		return status_time;
	}

	public void setStatus_time(String status_time) {
		this.status_time = status_time;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
	
	
}
