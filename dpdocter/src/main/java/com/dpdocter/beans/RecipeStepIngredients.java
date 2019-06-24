package com.dpdocter.beans;

public class RecipeStepIngredients {

	private String imageUrl;
	private String name;
	private MealQuantity mealQuantity;

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MealQuantity getMealQuantity() {
		return mealQuantity;
	}

	public void setMealQuantity(MealQuantity mealQuantity) {
		this.mealQuantity = mealQuantity;
	}

	@Override
	public String toString() {
		return "RecipeStepIngredients [imageUrl=" + imageUrl + ", name=" + name + ", mealQuantity=" + mealQuantity
				+ "]";
	}

}
