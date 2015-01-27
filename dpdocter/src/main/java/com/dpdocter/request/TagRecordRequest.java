package com.dpdocter.request;

import java.util.ArrayList;
import java.util.List;

import com.dpdocter.beans.Tags;

public class TagRecordRequest {
	private List<Tags> tags = new ArrayList<Tags>();
	private String recordId;
	public List<Tags> getTags() {
		return tags;
	}
	public void setTags(List<Tags> tags) {
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
		return "TagRecordRequest [tags=" + tags + ", recordId=" + recordId
				+ "]";
	}
	
	

}
