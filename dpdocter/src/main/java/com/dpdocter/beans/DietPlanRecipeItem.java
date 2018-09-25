package com.dpdocter.beans;

import java.util.List;

import org.bson.types.ObjectId;

public class DietPlanRecipeItem {

	private ObjectId id;

	private MealQuantity quantity;

	private String name;

	private List<RecipeItem> includeIngredients;

	private List<RecipeItem> excludeIngredients;

	private List<RecipeItem> ingredients;

	private List<IngredientItem> nutrients;

	private String direction;

	private String note;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public MealQuantity getQuantity() {
		return quantity;
	}

	public void setQuantity(MealQuantity quantity) {
		this.quantity = quantity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RecipeItem> getIncludeIngredients() {
		return includeIngredients;
	}

	public void setIncludeIngredients(List<RecipeItem> includeIngredients) {
		this.includeIngredients = includeIngredients;
	}

	public List<RecipeItem> getExcludeIngredients() {
		return excludeIngredients;
	}

	public void setExcludeIngredients(List<RecipeItem> excludeIngredients) {
		this.excludeIngredients = excludeIngredients;
	}

	public List<RecipeItem> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<RecipeItem> ingredients) {
		this.ingredients = ingredients;
	}

	public List<IngredientItem> getNutrients() {
		return nutrients;
	}

	public void setNutrients(List<IngredientItem> nutrients) {
		this.nutrients = nutrients;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
