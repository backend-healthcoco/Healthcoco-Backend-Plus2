package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "symptom_disease_condition_cl")
public class SymptomDiseaseConditionCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private String name;

	@Field
	private String type;

	@Field
	private Boolean toShow = true;

	@Field
	private List<String> specialities;

	@Field
	private List<ObjectId> specialityIds;

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

	public Boolean getToShow() {
		return toShow;
	}

	public void setToShow(Boolean toShow) {
		this.toShow = toShow;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public List<ObjectId> getSpecialityIds() {
		return specialityIds;
	}

	public void setSpecialityIds(List<ObjectId> specialityIds) {
		this.specialityIds = specialityIds;
	}

	@Override
	public String toString() {
		return "SymptomDiseaseConditionCollection [id=" + id + ", name=" + name + ", type=" + type + ", toShow="
				+ toShow + ", specialities=" + specialities + ", specialityIds=" + specialityIds + "]";
	}
}
