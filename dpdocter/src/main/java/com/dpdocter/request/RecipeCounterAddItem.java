package com.dpdocter.request;

import java.util.List;
import java.util.Map;

import com.dpdocter.beans.EquivalentQuantities;
import com.dpdocter.beans.MealQuantity;

public class RecipeCounterAddItem {
	private String id;

	private String name;

	private MealQuantity quantity;

	private String note;

	private MealQuantity calories;

	private List<EquivalentQuantities> equivalentMeasurements;

	private MealQuantity fat;

	private MealQuantity protein;

	private MealQuantity carbohydrate;

	private MealQuantity fiber;

	private Map<String, String> multilingualName;

	public Map<String, String> getMultilingualName() {
		return multilingualName;
	}

	public void setMultilingualName(Map<String, String> multilingualName) {
		this.multilingualName = multilingualName;
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

	public MealQuantity getQuantity() {
		return quantity;
	}

	public void setQuantity(MealQuantity quantity) {
		this.quantity = quantity;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public MealQuantity getCalories() {
		return calories;
	}

	public void setCalories(MealQuantity calories) {
		this.calories = calories;
	}

	public List<EquivalentQuantities> getEquivalentMeasurements() {
		return equivalentMeasurements;
	}

	public void setEquivalentMeasurements(List<EquivalentQuantities> equivalentMeasurements) {
		this.equivalentMeasurements = equivalentMeasurements;
	}

	public MealQuantity getFat() {
		return fat;
	}

	public void setFat(MealQuantity fat) {
		this.fat = fat;
	}

	public MealQuantity getProtein() {
		return protein;
	}

	public void setProtein(MealQuantity protein) {
		this.protein = protein;
	}

	public MealQuantity getCarbohydrate() {
		return carbohydrate;
	}

	public void setCarbohydrate(MealQuantity carbohydrate) {
		this.carbohydrate = carbohydrate;
	}

	public MealQuantity getFiber() {
		return fiber;
	}

	public void setFiber(MealQuantity fiber) {
		this.fiber = fiber;
	}

	@Override
	public String toString() {
		return "RecipeCounterAddItem [id=" + id + ", name=" + name + ", quantity=" + quantity + ", note=" + note
				+ ", calories=" + calories + ", equivalentMeasurements=" + equivalentMeasurements + ", fat=" + fat
				+ ", protein=" + protein + ", carbohydrate=" + carbohydrate + ", fiber=" + fiber + ", multilingualName="
				+ multilingualName + "]";
	}
}
