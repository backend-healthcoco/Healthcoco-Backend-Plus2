package com.dpdocter.beans;

public class IngredientAddItem {
	private String id;
	private String name;
	private double value = 0.0;
	private double inPercent;
	private String note;
	private String nutrientCode;

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
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

	public String getNutrientCode() {
		return nutrientCode;
	}

	public void setNutrientCode(String nutrientCode) {
		this.nutrientCode = nutrientCode;
	}

}
