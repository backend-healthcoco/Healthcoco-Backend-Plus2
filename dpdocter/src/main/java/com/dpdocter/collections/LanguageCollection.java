package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection ="language_cl" )
public class LanguageCollection extends GenericCollection {

	@Id
	private ObjectId id;
	
	@Field
	private String name;
	@Field
	private Boolean discarded=Boolean.FALSE;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "LanguageCollection [id=" + id + ", name=" + name + ", discarded=" + discarded + "]";
	}

	
	
	
}
