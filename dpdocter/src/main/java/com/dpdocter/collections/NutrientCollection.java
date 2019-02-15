package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.NutrientCategaoryEnum;
import com.dpdocter.enums.NutrientType;

@Document(collection = "nutrient_cl")
public class NutrientCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private String name;

	@Field
	private Boolean discarded = false;

	@Field
	private String note;

	@Field
	private NutrientType type = NutrientType.MICRO_NUTRIENT;
	
	@Field
	private NutrientCategaoryEnum category = NutrientCategaoryEnum.CARBOHYDRATE;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId hospitalId;

	@Field
	private String nutrientCode;

	public String getNutrientCode() {
		return nutrientCode;
	}

	public void setNutrientCode(String nutrientCode) {
		this.nutrientCode = nutrientCode;
	}

	public NutrientType getType() {
		return type;
	}

	public void setType(NutrientType type) {
		this.type = type;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public NutrientCategaoryEnum getCategory() {
		return category;
	}

	public void setCategory(NutrientCategaoryEnum category) {
		this.category = category;
	}

}
