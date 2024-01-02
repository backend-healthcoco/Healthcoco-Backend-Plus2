package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

import com.dpdocter.collections.GenericCollection;

public class MessageStatus extends GenericCollection{
	private String code;

	private String message;
	private List<MessageStatusData> data;
	
	private Map<String, String> error;

	public List<MessageStatusData> getData() {
		return data;
	}

	public void setData(List<MessageStatusData> data) {
		this.data = data;
	}

	public Map<String, String> getError() {
		return error;
	}

	public void setError(Map<String, String> error) {
		this.error = error;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
