package com.dpdocter.beans;

import com.dpdocter.enums.QuantityEnum;

public class IngredientAddItem {
	private String id;
	private String name;
	private Double value;
	private QuantityEnum type = QuantityEnum.G;
	private String note;
	private String nutrientCode;
	private Boolean isImportant = false;

	public String getNote() {
		return note;
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

	public void setNote(String note) {
		this.note = note;
	}

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

	public String getNutrientCode() {
		return nutrientCode;
	}

	public void setNutrientCode(String nutrientCode) {
		this.nutrientCode = nutrientCode;
	}

	public Boolean getIsImportant() {
		return isImportant;
	}

	public void setIsImportant(Boolean isImportant) {
		this.isImportant = isImportant;
	}

}
