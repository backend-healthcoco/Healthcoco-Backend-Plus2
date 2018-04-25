package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "dental_diagnostic_service_cl")
public class DentalDiagnosticServiceCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String name;
	@Field
	private String type;
	@Field
	private Boolean discarded = false;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "DentalDiagnosticServiceCollection [id=" + id + ", name=" + name + ", type=" + type + ", discarded="
				+ discarded + "]";
	}

}
