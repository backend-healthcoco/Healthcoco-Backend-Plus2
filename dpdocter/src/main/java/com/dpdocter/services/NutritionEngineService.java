package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DietPlan;
import com.dpdocter.enums.MealTimeEnum;

public interface NutritionEngineService {
	
	public void filterRecipesByIngredientsNutrients();
	
	public void filterRecipes();
	
	public void checkFoodInteraction();
	
	public void checkDrugInteraction();
	
	public void filterByMedicalCondition();
	
	public void filterByNutritionDistribution();
	
	public DietPlan getRecipes(String userId, List<MealTimeEnum> mealTime, String doctorId, String locationId, String hospitalId);
	
}
