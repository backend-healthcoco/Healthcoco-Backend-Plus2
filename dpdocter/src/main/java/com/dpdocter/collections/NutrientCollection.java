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
	private String locationId;

	@Field
	private String doctorId;

	@Field
	private String hospitalId;

	public NutrientType getType() {
		return type;
	}

	public void setType(NutrientType type) {
		this.type = type;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
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
