package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vaccine_brand_cl")
public class VaccineBrandCollection extends GenericCollection {

	private ObjectId id;
	private String name;
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
