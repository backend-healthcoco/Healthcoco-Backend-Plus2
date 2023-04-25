package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "vaccine_brand_association_cl")
public class VaccineBrandAssociationCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId vaccineId;
	@Field
	private ObjectId vaccineBrandId;
	@Field
	private String isActive;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getVaccineId() {
		return vaccineId;
	}

	public void setVaccineId(ObjectId vaccineId) {
		this.vaccineId = vaccineId;
	}

	public ObjectId getVaccineBrandId() {
		return vaccineBrandId;
	}

	public void setVaccineBrandId(ObjectId vaccineBrandId) {
		this.vaccineBrandId = vaccineBrandId;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

}
