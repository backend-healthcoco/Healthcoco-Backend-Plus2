package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.IngredientAddItem;
import com.dpdocter.beans.MealQuantity;
import com.dpdocter.beans.Quantity;
import com.dpdocter.enums.MealType;

public class MealRequest {
	private String id;

	private String name;

	private List<IngredientAddItem> ingredients;

	private List<IngredientAddItem> nutrients;

	private MealQuantity quantity;

	private String note;

	private MealType type;

	public MealType getType() {
		return type;
	}

	public void setType(MealType type) {
		this.type = type;
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

	public List<IngredientAddItem> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<IngredientAddItem> ingredients) {
		this.ingredients = ingredients;
	}

	public List<IngredientAddItem> getNutrients() {
		return nutrients;
	}

	public void setNutrients(List<IngredientAddItem> nutrients) {
		this.nutrients = nutrients;
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

}
