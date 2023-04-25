package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "records_tags_cl")
public class RecordsTagsCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId recordsId;

	@Field
	private ObjectId tagsId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getrecordsId() {
		return recordsId;
	}

	public void setrecordsId(ObjectId recordsId) {
		this.recordsId = recordsId;
	}

	public ObjectId getTagsId() {
		return tagsId;
	}

	public void setTagsId(ObjectId tagsId) {
		this.tagsId = tagsId;
	}

	@Override
	public String toString() {
		return "recordsTagsCollection [id=" + id + ", recordsId=" + recordsId + ", tagsId=" + tagsId + "]";
	}

}
