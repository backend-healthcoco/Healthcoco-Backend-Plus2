package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="records_tags_cl")
public class RecordsTagsCollection {

	@Id
	private String id;
	@Field
	private String recordsId;
	@Field
	private String tagsId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getrecordsId() {
		return recordsId;
	}
	public void setrecordsId(String recordsId) {
		this.recordsId = recordsId;
	}
	public String getTagsId() {
		return tagsId;
	}
	public void setTagsId(String tagsId) {
		this.tagsId = tagsId;
	}
	@Override
	public String toString() {
		return "recordsTagsCollection [id=" + id + ", recordsId=" + recordsId
				+ ", tagsId=" + tagsId + "]";
	}
	
	
}
