package com.dpdocter.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MessageStatusData {

	private String message_id;

	private String status;

	private String status_details;

	private String recipient;

	private String sent_time;

	private String status_time;

	private String body;
	private String amount_charged;
	private String provider;
	private String location;
	private String ref;
	private String ref1;
	private String ref2;
	@JsonIgnore
	private String price;

	private String error_code;
	private String totalCount;

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

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getAmount_charged() {
		return amount_charged;
	}

	public void setAmount_charged(String amount_charged) {
		this.amount_charged = amount_charged;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getRef1() {
		return ref1;
	}

	public void setRef1(String ref1) {
		this.ref1 = ref1;
	}

	public String getRef2() {
		return ref2;
	}

	public void setRef2(String ref2) {
		this.ref2 = ref2;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

}
