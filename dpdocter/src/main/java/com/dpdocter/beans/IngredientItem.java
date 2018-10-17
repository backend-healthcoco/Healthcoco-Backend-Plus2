package com.dpdocter.beans;

import org.bson.types.ObjectId;

import com.dpdocter.enums.NutrientCategaoryEnum;
import com.dpdocter.enums.QuantityEnum;

public class IngredientItem {

	private ObjectId id;
	private String name;
	private NutrientCategaoryEnum category = NutrientCategaoryEnum.CARBOHYDRATE;
	private double inPercent;
	private Double value;
	private QuantityEnum type = QuantityEnum.G;
	private String note;
	private String nutrientCode;
	private Boolean isImportant = false;

	public Boolean getIsImportant() {
		return isImportant;
	}

	public void setIsImportant(Boolean isImportant) {
		this.isImportant = isImportant;
	}

	public String getNutrientCode() {
		return nutrientCode;
	}

	public void setNutrientCode(String nutrientCode) {
		this.nutrientCode = nutrientCode;
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

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public QuantityEnum getType() {
		return type;
	}

	public void setType(QuantityEnum type) {
		this.type = type;
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

	public double getInPercent() {
		return inPercent;
	}

	public void setInPercent(double inPercent) {
		this.inPercent = inPercent;
	}

}
