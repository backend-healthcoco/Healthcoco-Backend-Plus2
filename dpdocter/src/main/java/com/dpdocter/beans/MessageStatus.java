package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

import com.dpdocter.collections.GenericCollection;

public class MessageStatus extends GenericCollection{

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
	
	
}
