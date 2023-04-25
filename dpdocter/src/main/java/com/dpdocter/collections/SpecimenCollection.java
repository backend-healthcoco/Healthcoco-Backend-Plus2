package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "specimen_cl")
public class SpecimenCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String specimen;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getSpecimen() {
		return specimen;
	}

	public void setSpecimen(String specimen) {
		this.specimen = specimen;
	}

	@Override
	public String toString() {
		return "SpecimenCollection [id=" + id + ", specimen=" + specimen + "]";
	}

}
