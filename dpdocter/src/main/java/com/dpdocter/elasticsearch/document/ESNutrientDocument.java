package com.dpdocter.elasticsearch.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.dpdocter.enums.NutrientCategaoryEnum;
import com.dpdocter.enums.NutrientType;

@Document(indexName = "nutrients_in", type = "nutrients")
public class ESNutrientDocument {

	@Id
	private String id;

	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;

	@Field(type = FieldType.String)
	private String name;

	@Field(type = FieldType.String)
	private String locationId;

	@Field(type = FieldType.String)
	private NutrientType type = NutrientType.MICRO_NUTRIENT;

	@Field(type = FieldType.String)
	private NutrientCategaoryEnum category = NutrientCategaoryEnum.CARBOHYDRATE;

	@Field(type = FieldType.String)
	private String doctorId;

	@Field(type = FieldType.String)
	private String hospitalId;

	@Field(type = FieldType.Date)
	private Date updatedTime = new Date();

	@Field(type = FieldType.String)
	private String nutrientCode;

	public String getNutrientCode() {
		return nutrientCode;
	}

	public void setNutrientCode(String nutrientCode) {
		this.nutrientCode = nutrientCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public NutrientType getType() {
		return type;
	}

	public void setType(NutrientType type) {
		this.type = type;
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

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public NutrientCategaoryEnum getCategory() {
		return category;
	}

	public void setCategory(NutrientCategaoryEnum category) {
		this.category = category;
	}

}
