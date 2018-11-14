package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vaccine_brand_association_cl")
public class VaccineBrandAssociationCollection extends GenericCollection {

	private ObjectId id;
	private ObjectId vaccineId;
	private ObjectId vaccineBrandId;
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
