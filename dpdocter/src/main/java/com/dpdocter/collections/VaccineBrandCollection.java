package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "vaccine_brand_cl")
public class VaccineBrandCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String name;
	@Field
	private String groupFrom;

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

	public String getGroupFrom() {
		return groupFrom;
	}

	public void setGroupFrom(String groupFrom) {
		this.groupFrom = groupFrom;
	}

	@Override
	public String toString() {
		return "VaccineBrandCollection [id=" + id + ", name=" + name + "]";
	}

}
