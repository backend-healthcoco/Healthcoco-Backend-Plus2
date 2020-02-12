package com.dpdocter.response;

import java.util.List;
import java.util.Map;

import com.dpdocter.beans.EquivalentQuantities;
import com.dpdocter.beans.MealQuantity;
import com.dpdocter.enums.LevelType;

public class RecipeItemResponse {

	private String id;

	private String name;

	private Double cost = 0.0;

	private LevelType costType;

	private MealQuantity quantity;

	private List<EquivalentQuantities> equivalentMeasurements;

	private MealQuantity calories;

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

	public List<EquivalentQuantities> getEquivalentMeasurements() {
		return equivalentMeasurements;
	}

	public void setEquivalentMeasurements(List<EquivalentQuantities> equivalentMeasurements) {
		this.equivalentMeasurements = equivalentMeasurements;
	}

	public MealQuantity getCalories() {
		return calories;
	}

	public void setCalories(MealQuantity calories) {
		this.calories = calories;
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

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public LevelType getCostType() {
		return costType;
	}

	public void setCostType(LevelType costType) {
		this.costType = costType;
	}

	@Override
	public String toString() {
		return "RecipeItemResponse [id=" + id + ", name=" + name + ", cost=" + cost + ", costType=" + costType
				+ ", quantity=" + quantity + ", equivalentMeasurements=" + equivalentMeasurements + ", calories="
				+ calories + ", fat=" + fat + ", protein=" + protein + ", carbohydrate=" + carbohydrate + ", fiber="
				+ fiber + ", multilingualName=" + multilingualName + "]";
	}
}
