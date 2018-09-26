package com.dpdocter.elasticsearch.response;

import java.util.List;

import com.dpdocter.beans.EquivalentQuantities;
import com.dpdocter.beans.IngredientItem;
import com.dpdocter.beans.MealQuantity;

public class ESIngredientResponse {

	private String id;

	private String name;

	private List<IngredientItem> nutrients;

	private MealQuantity quantity;

	private String note;
	
	private MealQuantity calaries;
	
	private List<EquivalentQuantities> equivalentMeasurements;
	
	

	public MealQuantity getCalaries() {
		return calaries;
	}

	public void setCalaries(MealQuantity calaries) {
		this.calaries = calaries;
	}

	public List<EquivalentQuantities> getEquivalentMeasurements() {
		return equivalentMeasurements;
	}

	public void setEquivalentMeasurements(List<EquivalentQuantities> equivalentMeasurements) {
		this.equivalentMeasurements = equivalentMeasurements;
	}

	public MealQuantity getQuantity() {
		return quantity;
	}

	public void setQuantity(MealQuantity quantity) {
		this.quantity = quantity;
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

	public List<IngredientItem> getNutrients() {
		return nutrients;
	}

	public void setNutrients(List<IngredientItem> nutrients) {
		this.nutrients = nutrients;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
