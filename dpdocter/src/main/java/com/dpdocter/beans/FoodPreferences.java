package com.dpdocter.beans;

public class FoodPreferences { 
	
		
		private FoodPref foodpref;

		public FoodPref getFoodpref() {
			return foodpref;
		}

		public void setFoodpref(FoodPref foodpref) {
			this.foodpref = foodpref;
		}

		@Override
		public String toString() {
			return "FoodPreferences [foodpref=" + foodpref + "]";
		}
		
		
		

}
