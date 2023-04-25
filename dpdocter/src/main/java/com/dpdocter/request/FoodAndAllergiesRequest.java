package com.dpdocter.request;

import java.util.List;

public class FoodAndAllergiesRequest {
	private List<MealRequest> foods;
	private String allergies;

	public List<MealRequest> getFoods() {
		return foods;
	}

	public void setFoods(List<MealRequest> foods) {
		this.foods = foods;
	}

	public String getAllergies() {
		return allergies;
	}

	public void setAllergies(String allergies) {
		this.allergies = allergies;
	}

}
