package com.dpdocter.beans;

import java.util.List;

public class FoodAndAllergies {

	private List<Meal> foods;
	private String allergies;

	public List<Meal> getFoods() {
		return foods;
	}

	public void setFoods(List<Meal> foods) {
		this.foods = foods;
	}

	public String getAllergies() {
		return allergies;
	}

	public void setAllergies(String allergies) {
		this.allergies = allergies;
	}

}
