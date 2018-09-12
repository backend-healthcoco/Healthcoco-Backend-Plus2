package com.dpdocter.beans;

import com.dpdocter.enums.NutrientCategaoryEnum;
import com.dpdocter.enums.QuantityEnum;

public class IngredientAddItem {
	private String id;

	private String name;
	private NutrientCategaoryEnum category = NutrientCategaoryEnum.CARBOHYDRATE;
	private int value;
	private QuantityEnum type;
	private double inPercent;
	private String note;

	public NutrientCategaoryEnum getCategory() {
		return category;
	}

	public void setCategory(NutrientCategaoryEnum category) {
		this.category = category;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public QuantityEnum getType() {
		return type;
	}

	public void setType(QuantityEnum type) {
		this.type = type;
	}

	public double getInPercent() {
		return inPercent;
	}

	public void setInPercent(double inPercent) {
		this.inPercent = inPercent;
	}

	public String getNote() {
		return note;
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

}
