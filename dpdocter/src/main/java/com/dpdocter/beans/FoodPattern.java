package com.dpdocter.beans;

public class FoodPattern {

	private String type;
	private String food;
	private String recipeId;
	private String recipeName;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFood() {
		return food;
	}

	public void setFood(String food) {
		this.food = food;
	}

	public String getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(String recipeId) {
		this.recipeId = recipeId;
	}

	public String getRecipeName() {
		return recipeName;
	}

	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}

	@Override
	public String toString() {
		return "FoodPattern [type=" + type + ", food=" + food + ", recipeId=" + recipeId + ", recipeName=" + recipeName
				+ "]";
	}
}
