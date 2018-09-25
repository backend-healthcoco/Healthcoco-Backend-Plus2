package com.dpdocter.elasticsearch.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.dpdocter.beans.IngredientAddItem;
import com.dpdocter.beans.MealQuantity;

@Document(indexName = "ingredients_in", type = "ingredients")
public class ESIngredientDocument {
	@Id
	private String id;

	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;

	@Field(type = FieldType.Nested)
	private MealQuantity quantity;

	@Field(type = FieldType.String)
	private String name;

	@Field(type = FieldType.String)
	private String locationId;

	@Field(type = FieldType.Nested)
	private List<IngredientAddItem> nutrients;

	@Field(type = FieldType.String)
	private String doctorId;

	@Field(type = FieldType.String)
	private String hospitalId;

	@Field(type = FieldType.String)
	private String note;

	@Field(type = FieldType.Date)
	private Date updatedTime = new Date();

	@Field(type = FieldType.Double)
	private double calaries;

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

	public List<IngredientAddItem> getNutrients() {
		return nutrients;
	}

	public void setNutrients(List<IngredientAddItem> nutrients) {
		this.nutrients = nutrients;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public MealQuantity getQuantity() {
		return quantity;
	}

	public void setQuantity(MealQuantity quantity) {
		this.quantity = quantity;
	}

	public double getCalaries() {
		return calaries;
	}

	public void setCalaries(double calaries) {
		this.calaries = calaries;
	}

}
