package com.bean;

import java.util.Arrays;

public class FoodPreferences {
	
	private String FoodPref[][]={{"Veg","false"},
			{"Grains","false"},
			{"Milk","false"},
			{"Egg","false"},
			{"Fish","false"},
			{"Meat","false"},
			{"Sea Food","false"},
			{"Honey","false"}}; 
	
	

	public String[][] getFoodPref() {
		return FoodPref;
	}



	public void setFoodPref(String[][] foodPref) {
		FoodPref = foodPref;
	}



	@Override
	public String toString() {
		return "FoodPreferences [FoodPref=" + Arrays.toString(FoodPref) + "]";
	}
	

}
