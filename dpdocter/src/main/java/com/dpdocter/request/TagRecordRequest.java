package com.dpdocter.request;

import java.util.ArrayList;
import java.util.List;

public class TagRecordRequest {
	private List<String> tags = new ArrayList<String>();

	private String recordId;

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	@Override
	public String toString() {
		return "TagRecordRequest [tags=" + tags + ", recordId=" + recordId + "]";
	}

}
