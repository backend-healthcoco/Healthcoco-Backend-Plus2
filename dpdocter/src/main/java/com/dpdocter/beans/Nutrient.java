package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.NutrientCategaoryEnum;
import com.dpdocter.enums.NutrientType;

public class Nutrient extends GenericCollection {

	private String id;

	private String name;

	private String locationId;

	private String doctorId;

	private String hospitalId;

	private NutrientType type = NutrientType.MICRO_NUTRIENT;

	private NutrientCategaoryEnum category = NutrientCategaoryEnum.CARBOHYDRATE;

	private Boolean discarded = false;

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

	private String note;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public NutrientCategaoryEnum getCategory() {
		return category;
	}

	public void setCategory(NutrientCategaoryEnum category) {
		this.category = category;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
