package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DietPlan;
import com.dpdocter.enums.MealTimeEnum;

public interface NutritionEngineService {
	
	//public boolean bodyParametersNutrientsMatchesRecipeNutrients(NutritionRDA nutritionRDA, RecipeCollection recipeCollection);
	
	public void filterRecipesByIngredientsNutrients();
	
	public void filterRecipes();
	
	public void checkFoodInteraction();
	
	public void checkDrugInteraction();
	
	public void filterByMedicalCondition();
	
	public void filterByNutritionDistribution();
	
//	public void recipeSelection();

	public DietPlan getRecipes(String userId, List<MealTimeEnum> mealTime, String doctorId, String locationId, String hospitalId);
	
}
