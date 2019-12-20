package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.NutritionRDA;
import com.dpdocter.beans.Recipe;
import com.dpdocter.collections.RecipeCollection;
import com.dpdocter.enums.MealTimeEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.RecipeRepository;
import com.dpdocter.services.NutritionEngineService;
import com.dpdocter.services.NutritionService;
import com.dpdocter.services.RecipeService;

import common.util.web.DPDoctorUtils;

@Service
public class NutritionEngineServiceImpl implements NutritionEngineService {

	private static Logger logger = Logger.getLogger(NutritionEngineServiceImpl.class.getName());
	
	@Autowired
	private NutritionService nutritionService;
	
	@Autowired
	private RecipeService recipeService;
	
	@Autowired
	private RecipeRepository recipeRepository;
	
	
	public boolean compareNutrients(Map<String, String> nutrient1, Map<String, String> nutrient2){
		boolean isMatched = false;
		
		if(nutrient1 == null && nutrient2 == null) return true;
		if(nutrient1 == null) return false;
		if(nutrient2 == null) return false;
		if(nutrient1.size() != nutrient2.size())return isMatched;
		
		isMatched = nutrient1.entrySet().stream()
						.allMatch(x -> compareNutrientValues(x.getValue(), nutrient2.getOrDefault(x, null)));
		return isMatched;
		
	}
	
	private boolean compareNutrientValues(String value1, String value2) {
		// TODO Auto-generated method stub
		if(DPDoctorUtils.anyStringEmpty(value1, value2))return false;
		
		Double val1 = Double.parseDouble(value1);
		Double val2 = Double.parseDouble(value2);
		
		if(val1 == val2)return true;
		if(isInRange(val1, val2)) return true;
		if(isInRange(val2, val1)) return true;
		
		return false;
	}
	
	private boolean isInRange(Double rangeValue, Double valueToBeCheck) {
		Double fivePerValue = (5*rangeValue)/100;
		
		if((rangeValue-fivePerValue)<=valueToBeCheck  &&  valueToBeCheck <= (rangeValue+fivePerValue))return true;
		return false;
	}
	
	/*
	 * On the basis of body parameters get NutrientRDA 
	 * Compare this nutrient RDA
	 * with the recipe nutrients
	 */
	@Override
	public boolean bodyParametersNutrientsMatchesRecipeNutrients(NutritionRDA nutritionRDA, RecipeCollection recipeCollection) {
		// TODO Auto-generated method stub
		//API - nutritionService.getRDAForPatient(patientId, country, countryId)
		
		if(!compareNutrients(nutritionRDA.getGeneralNutrients(), recipeCollection.getGeneralNutrients())) return false;		
		if(!compareNutrients(nutritionRDA.getCarbNutrients(), recipeCollection.getCarbNutrients())) return false;
		if(!compareNutrients(nutritionRDA.getLipidNutrients(), recipeCollection.getLipidNutrients())) return false;
		if(!compareNutrients(nutritionRDA.getProteinAminoAcidNutrients(), recipeCollection.getProteinAminoAcidNutrients())) return false;
		if(!compareNutrients(nutritionRDA.getVitaminNutrients(), recipeCollection.getVitaminNutrients())) return false;
		if(!compareNutrients(nutritionRDA.getMineralNutrients(), recipeCollection.getMineralNutrients())) return false;
		if(!compareNutrients(nutritionRDA.getOtherNutrients(), recipeCollection.getOtherNutrients())) return false;
		
		return true;
	}

	/*
	 * On the basis of NutrientRDA 
	 * Compare this nutrient RDA
	 * with the ingredient nutrients
	 */
	@Override
	public void filterRecipesByIngredientsNutrients() {
		// TODO Auto-generated method stub
		//API - recipeService.getIngredient of recipes
	}

	/*
	 * Filter on the basis of Allergy 
	 * Community 
	 * Cuisine 
	 * Dietary preference 
	 * Season
	 */
	@Override
	public void filterRecipes() {
		// TODO Auto-generated method stub
		//API - getAssessmentLifeStyle of patient and filter accordingly
	}

	/*
	 * Find if there is interaction between
	 * Recipe 1 and Recipe 2
	 */
	@Override
	public void checkFoodInteraction() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Find if there is interaction between
	 * Recipe 1 and drug
	 */
	@Override
	public void checkDrugInteraction() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Filter recipes on the basis of 
	 * medical condition
	 */
	@Override
	public void filterByMedicalCondition() {
		// TODO Auto-generated method stub
		//API - getAssessment of patient and filter accordingly
	}

	/*
	 * Filter recipes on the basis of 
	 * meal type and nutrition
	 */
	@Override
	public void filterByNutritionDistribution() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Final recipe selection
	 */
//	@Override
	public List<Recipe> recipeSelection(String userId, List<MealTimeEnum> mealTime, String doctorId, String locationId, String hospitalId) {
		// TODO Auto-generated method stub
		List<Recipe> recipes = new ArrayList<>();
		List<RecipeCollection> recipeCollections = recipeRepository.findAll();
		
		NutritionRDA nutritionRDA = nutritionService.getRDAForPatient(userId, doctorId, locationId, hospitalId);
		for(RecipeCollection recipeCollection: recipeCollections) {
			if(bodyParametersNutrientsMatchesRecipeNutrients(nutritionRDA, recipeCollection)) {
				Recipe recipe = new Recipe();
				BeanUtil.map(recipeCollection, recipe);
				recipes.add(recipe);
			}
			if(recipes.size() == 4)break;
		}
		return recipes;
	}

	@Override
	public List<Recipe> getRecipes(String userId, List<MealTimeEnum> mealTime, 
			String doctorId, String locationId, String hospitalId) {
		try {
			return recipeSelection(userId, mealTime, doctorId, locationId, hospitalId);
		}catch(BusinessException e) {
			logger.error("Error while getting Recipes for patient " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Recipes for patient " + e.getMessage());
		}
	}

}
