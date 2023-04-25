package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CombineRecipeNutrients;
import com.dpdocter.beans.DietPlan;
import com.dpdocter.beans.DietPlanRecipeAddItem;
import com.dpdocter.beans.DietplanAddItem;
import com.dpdocter.beans.NutritionRDA;
import com.dpdocter.collections.RecipeCollection;
import com.dpdocter.enums.MealTimeEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.RecipeRepository;
import com.dpdocter.services.NutritionEngineService;
import com.dpdocter.services.NutritionService;

import common.util.web.DPDoctorUtils;

@Service
public class NutritionEngineServiceImpl implements NutritionEngineService {

	private static Logger logger = Logger.getLogger(NutritionEngineServiceImpl.class.getName());

	@Autowired
	private NutritionService nutritionService;

	@Autowired
	private RecipeRepository recipeRepository;

	public boolean compareNutrients(Map<String, String> nutrient1, Map<String, Double> map, int perOfValue) {
		boolean isMatched = false;

		if (nutrient1 == null && map == null)
			return true;
		if (nutrient1 == null || map == null)
			return false;

		map.forEach((key, value) -> System.out.println(key + ":" + value));

		isMatched = nutrient1.entrySet().stream()
				.allMatch(x -> compareNutrientValues(x.getValue(), map.getOrDefault(x, 0.0), perOfValue));
		return isMatched;

	}

	private boolean compareNutrientValues(String value1, Double val2, int perOfValue) {
		// TODO Auto-generated method stub
		if (DPDoctorUtils.anyStringEmpty(value1))
			return true;

		Double val1 = Double.parseDouble(value1);

		System.out.println("val1" + val1);
		System.out.println("val2" + val2);

		if (isInRange((perOfValue * val1) / 100, val2))
			return true;

		return false;
	}

	private boolean isInRange(Double rangeValue, Double valueToBeCheck) {
		Double fivePerValue = (50 * rangeValue) / 100;
		System.out
				.println((rangeValue - fivePerValue) + " <= " + valueToBeCheck + " <= " + (rangeValue + fivePerValue));
		if ((rangeValue - fivePerValue) <= valueToBeCheck && valueToBeCheck <= (rangeValue + fivePerValue))
			return true;
		return false;
	}

	/*
	 * On the basis of body parameters get NutrientRDA Compare this nutrient RDA
	 * with the recipe nutrients
	 */
	// @Override
	public boolean bodyParametersNutrientsMatchesRecipeNutrients(NutritionRDA nutritionRDA,
			CombineRecipeNutrients recipeCollection, int perOfValue) {
		String carbsStr = nutritionRDA.getCarbNutrients().getOrDefault("Carbohydrates", "0.0");
		Double carbs = Double.parseDouble(!DPDoctorUtils.anyStringEmpty(carbsStr) ? carbsStr : "0.0");
		Double fats = Double.parseDouble(nutritionRDA.getLipidNutrients().getOrDefault("Total Fat", "0.0"));
		Double proteins = Double
				.parseDouble(nutritionRDA.getProteinAminoAcidNutrients().getOrDefault("Protein", "0.0"));

		Double recipeCarbs = recipeCollection.getCarbNutrients().getOrDefault("Carbohydrates", 0.0);
		Double recipeFats = recipeCollection.getLipidNutrients().getOrDefault("Total Fat", 0.0);
		Double recipeProteins = recipeCollection.getProteinAminoAcidNutrients().getOrDefault("Protein", 0.0);

		Boolean isMatch = true;

		Double rangeValue = (perOfValue * carbs) / 100;
		if (isInRange(rangeValue, recipeCarbs)) {

		} else {
			isMatch = false;
		}
		rangeValue = (perOfValue * fats) / 100;
		if (isInRange(rangeValue, recipeFats)) {
		} else {
			isMatch = false;
		}

		rangeValue = (perOfValue * proteins) / 100;
		if (isInRange(rangeValue, recipeProteins)) {

		} else {
			isMatch = false;
		}
		return isMatch;
	}

	/*
	 * On the basis of NutrientRDA Compare this nutrient RDA with the ingredient
	 * nutrients
	 */
	@Override
	public void filterRecipesByIngredientsNutrients() {
	}

	/*
	 * Filter on the basis of Allergy Community Cuisine Dietary preference Season
	 */
	@Override
	public void filterRecipes() {
	}

	/*
	 * Find if there is interaction between Recipe 1 and Recipe 2
	 */
	@Override
	public void checkFoodInteraction() {

	}

	/*
	 * Find if there is interaction between Recipe 1 and drug
	 */
	@Override
	public void checkDrugInteraction() {

	}

	/*
	 * Filter recipes on the basis of medical condition
	 */
	@Override
	public void filterByMedicalCondition() {
		// API - getAssessment of patient and filter accordingly
	}

	/*
	 * Filter recipes on the basis of meal type and nutrition
	 */
	@Override
	public void filterByNutritionDistribution() {

	}

	/*
	 * Final recipe selection
	 */
	public DietPlan recipeSelection(String userId, List<MealTimeEnum> mealTimes, String doctorId, String locationId,
			String hospitalId) {
		DietPlan dietPlan = new DietPlan();
		mealTimes = new ArrayList<MealTimeEnum>();

		mealTimes.add(MealTimeEnum.EARLY_MORNING);// 10%
		mealTimes.add(MealTimeEnum.BREAKFAST);// 20%
		mealTimes.add(MealTimeEnum.MID_MORNING);// 5%
		mealTimes.add(MealTimeEnum.LUNCH);// 20%
		mealTimes.add(MealTimeEnum.POST_LUNCH);// 5%
		mealTimes.add(MealTimeEnum.EVENING_SNACK);// 10%
		mealTimes.add(MealTimeEnum.DINNER);// 20%
		mealTimes.add(MealTimeEnum.POST_DINNER);// 10%

		NutritionRDA nutritionRDA = nutritionService.getRDAForPatient(userId, doctorId, locationId, hospitalId);
		if (nutritionRDA == null) {
			logger.warn("No RDA found");
			throw new BusinessException(ServiceError.InvalidInput, "No RDA found");
		}

		String carbsStr = nutritionRDA.getCarbNutrients().getOrDefault("Carbohydrates", "0.0");
		Double carbs = Double.parseDouble(!DPDoctorUtils.anyStringEmpty(carbsStr) ? carbsStr : "0.0");
		Double fats = Double.parseDouble(nutritionRDA.getLipidNutrients().getOrDefault("Total Fat", "0.0"));
		Double proteins = Double
				.parseDouble(nutritionRDA.getProteinAminoAcidNutrients().getOrDefault("Protein", "0.0"));

		System.out.println("RDA carbs: " + carbs + " fats: " + fats + " proteins: " + proteins);

		List<DietplanAddItem> addItems = new ArrayList<DietplanAddItem>();
		for (MealTimeEnum mealTimeEnum : mealTimes) {
			DietplanAddItem dietplanAddItem = new DietplanAddItem();
			dietplanAddItem.setMealTiming(mealTimeEnum);
			List<DietPlanRecipeAddItem> recipes = getRecipes(mealTimeEnum, nutritionRDA);
			dietplanAddItem.setRecipes(recipes);
			addItems.add(dietplanAddItem);
		}
		dietPlan.setItems(addItems);
		return dietPlan;
	}

	private List<DietPlanRecipeAddItem> getRecipes(MealTimeEnum mealTimeEnum, NutritionRDA nutritionRDA) {
		List<DietPlanRecipeAddItem> recipeAddItems = new ArrayList<DietPlanRecipeAddItem>();
		try {

			int perOfValue = 0;

			switch (mealTimeEnum) {

			case EARLY_MORNING: {
				List<RecipeCollection> recipeCollections = recipeRepository.findByMealTiming(mealTimeEnum.getTime());
				if (recipeCollections != null && recipeCollections.isEmpty())
					Collections.shuffle(recipeCollections);
				perOfValue = 10;
				System.out.println("EARLY_MORNING : %of RDA = " + perOfValue + " Total Recipes= "
						+ (recipeCollections != null ? recipeCollections.size() : 0));

				for (RecipeCollection recipeCollection : recipeCollections) {
					System.out.println("Recipe Name : " + recipeCollection.getName());
					CombineRecipeNutrients forCheck = new CombineRecipeNutrients();
					forCheck.setGeneralNutrients(addNutrientsValue(recipeCollection.getGeneralNutrients()));
					forCheck.setCarbNutrients(addNutrientsValue(recipeCollection.getCarbNutrients()));
					forCheck.setLipidNutrients(addNutrientsValue(recipeCollection.getLipidNutrients()));
					forCheck.setProteinAminoAcidNutrients(
							addNutrientsValue(recipeCollection.getProteinAminoAcidNutrients()));
					forCheck.setVitaminNutrients(addNutrientsValue(recipeCollection.getVitaminNutrients()));
					forCheck.setMineralNutrients(addNutrientsValue(recipeCollection.getMineralNutrients()));
					forCheck.setOtherNutrients(addNutrientsValue(recipeCollection.getOtherNutrients()));

					if (bodyParametersNutrientsMatchesRecipeNutrients(nutritionRDA, forCheck, perOfValue)) {
						DietPlanRecipeAddItem recipe = new DietPlanRecipeAddItem();
						BeanUtil.map(recipeCollection, recipe);
						recipeAddItems.add(recipe);
						break;
					}
				}
				break;
			}

			case BREAKFAST: {
				perOfValue = 20;
				getBreakFastSelectedRecipes(mealTimeEnum, recipeAddItems, nutritionRDA, perOfValue);
				break;
			}
			case MID_MORNING: {
				perOfValue = 5;
				List<RecipeCollection> recipeCollections = recipeRepository.findByMealTiming(mealTimeEnum.getTime());
				if (recipeCollections != null && recipeCollections.isEmpty())
					Collections.shuffle(recipeCollections);
				System.out.println("MID_MORNING : %of RDA = " + perOfValue + " Total Recipes= "
						+ (recipeCollections != null ? recipeCollections.size() : 0));

				for (RecipeCollection recipeCollection : recipeCollections) {
					System.out.println("Recipe Name : " + recipeCollection.getName());
					CombineRecipeNutrients forCheck = new CombineRecipeNutrients();
					forCheck.setGeneralNutrients(addNutrientsValue(recipeCollection.getGeneralNutrients()));
					forCheck.setCarbNutrients(addNutrientsValue(recipeCollection.getCarbNutrients()));
					forCheck.setLipidNutrients(addNutrientsValue(recipeCollection.getLipidNutrients()));
					forCheck.setProteinAminoAcidNutrients(
							addNutrientsValue(recipeCollection.getProteinAminoAcidNutrients()));
					forCheck.setVitaminNutrients(addNutrientsValue(recipeCollection.getVitaminNutrients()));
					forCheck.setMineralNutrients(addNutrientsValue(recipeCollection.getMineralNutrients()));
					forCheck.setOtherNutrients(addNutrientsValue(recipeCollection.getOtherNutrients()));

					if (bodyParametersNutrientsMatchesRecipeNutrients(nutritionRDA, forCheck, perOfValue)) {
						DietPlanRecipeAddItem recipe = new DietPlanRecipeAddItem();
						BeanUtil.map(recipeCollection, recipe);
						recipeAddItems.add(recipe);
						break;
					}
				}
				break;
			}

			case LUNCH: {
				perOfValue = 20;
				getLunchSelectedRecipes(mealTimeEnum, recipeAddItems, nutritionRDA, perOfValue);
				break;
			}
			case POST_LUNCH: {
				perOfValue = 5;
				List<RecipeCollection> recipeCollections = recipeRepository.findByMealTiming(mealTimeEnum.getTime());
				if (recipeCollections != null && recipeCollections.isEmpty())
					Collections.shuffle(recipeCollections);
				System.out.println("POST_LUNCH : %of RDA = " + perOfValue + " Total Recipes= "
						+ (recipeCollections != null ? recipeCollections.size() : 0));

				for (RecipeCollection recipeCollection : recipeCollections) {
					System.out.println("Recipe Name : " + recipeCollection.getName());
					CombineRecipeNutrients forCheck = new CombineRecipeNutrients();
					forCheck.setGeneralNutrients(addNutrientsValue(recipeCollection.getGeneralNutrients()));
					forCheck.setCarbNutrients(addNutrientsValue(recipeCollection.getCarbNutrients()));
					forCheck.setLipidNutrients(addNutrientsValue(recipeCollection.getLipidNutrients()));
					forCheck.setProteinAminoAcidNutrients(
							addNutrientsValue(recipeCollection.getProteinAminoAcidNutrients()));
					forCheck.setVitaminNutrients(addNutrientsValue(recipeCollection.getVitaminNutrients()));
					forCheck.setMineralNutrients(addNutrientsValue(recipeCollection.getMineralNutrients()));
					forCheck.setOtherNutrients(addNutrientsValue(recipeCollection.getOtherNutrients()));

					if (bodyParametersNutrientsMatchesRecipeNutrients(nutritionRDA, forCheck, perOfValue)) {
						DietPlanRecipeAddItem recipe = new DietPlanRecipeAddItem();
						BeanUtil.map(recipeCollection, recipe);
						recipeAddItems.add(recipe);
						break;
					}
				}
				break;
			}
			case EVENING_SNACK: {
				perOfValue = 10;
				List<RecipeCollection> recipeCollections = recipeRepository.findByMealTimingAndFoodGroupsValueIn(
						mealTimeEnum.getTime(), Arrays.asList("Milk And Milk Products", "Other Vegetables", "Nut",
								"Leafy Vegetables", "Fruits"));
				if (recipeCollections != null && recipeCollections.isEmpty())
					Collections.shuffle(recipeCollections);
				System.out.println("EVENING_SNACK : %of RDA = " + perOfValue + " Total Recipes= "
						+ (recipeCollections != null ? recipeCollections.size() : 0));

				for (RecipeCollection recipeCollection : recipeCollections) {
					System.out.println("Recipe Name : " + recipeCollection.getName());
					CombineRecipeNutrients forCheck = new CombineRecipeNutrients();
					forCheck.setGeneralNutrients(addNutrientsValue(recipeCollection.getGeneralNutrients()));
					forCheck.setCarbNutrients(addNutrientsValue(recipeCollection.getCarbNutrients()));
					forCheck.setLipidNutrients(addNutrientsValue(recipeCollection.getLipidNutrients()));
					forCheck.setProteinAminoAcidNutrients(
							addNutrientsValue(recipeCollection.getProteinAminoAcidNutrients()));
					forCheck.setVitaminNutrients(addNutrientsValue(recipeCollection.getVitaminNutrients()));
					forCheck.setMineralNutrients(addNutrientsValue(recipeCollection.getMineralNutrients()));
					forCheck.setOtherNutrients(addNutrientsValue(recipeCollection.getOtherNutrients()));

					if (bodyParametersNutrientsMatchesRecipeNutrients(nutritionRDA, forCheck, perOfValue)) {
						DietPlanRecipeAddItem recipe = new DietPlanRecipeAddItem();
						BeanUtil.map(recipeCollection, recipe);
						recipeAddItems.add(recipe);
						break;
					}
				}
				break;
			}
			case DINNER: {
				perOfValue = 20;
				getLunchSelectedRecipes(mealTimeEnum, recipeAddItems, nutritionRDA, perOfValue);
				break;
			}
			case POST_DINNER: {
				perOfValue = 10;
				List<RecipeCollection> recipeCollections = recipeRepository.findByMealTiming(mealTimeEnum.getTime());
				if (recipeCollections != null && recipeCollections.isEmpty())
					Collections.shuffle(recipeCollections);
				System.out.println("POST_DINNER : %of RDA = " + perOfValue + " Total Recipes= "
						+ (recipeCollections != null ? recipeCollections.size() : 0));
				for (RecipeCollection recipeCollection : recipeCollections) {
					System.out.println("Recipe Name : " + recipeCollection.getName());
					CombineRecipeNutrients forCheck = new CombineRecipeNutrients();
					forCheck.setGeneralNutrients(addNutrientsValue(recipeCollection.getGeneralNutrients()));
					forCheck.setCarbNutrients(addNutrientsValue(recipeCollection.getCarbNutrients()));
					forCheck.setLipidNutrients(addNutrientsValue(recipeCollection.getLipidNutrients()));
					forCheck.setProteinAminoAcidNutrients(
							addNutrientsValue(recipeCollection.getProteinAminoAcidNutrients()));
					forCheck.setVitaminNutrients(addNutrientsValue(recipeCollection.getVitaminNutrients()));
					forCheck.setMineralNutrients(addNutrientsValue(recipeCollection.getMineralNutrients()));
					forCheck.setOtherNutrients(addNutrientsValue(recipeCollection.getOtherNutrients()));

					if (bodyParametersNutrientsMatchesRecipeNutrients(nutritionRDA, forCheck, perOfValue)) {
						DietPlanRecipeAddItem recipe = new DietPlanRecipeAddItem();
						BeanUtil.map(recipeCollection, recipe);
						recipeAddItems.add(recipe);
						break;
					}
				}
				break;
			}

			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recipeAddItems;
	}

	private void getLunchSelectedRecipes(MealTimeEnum mealTimeEnum, List<DietPlanRecipeAddItem> recipeAddItems,
			NutritionRDA nutritionRDA, int perOfValue) {
		try {
			List<RecipeCollection> mainCourseRecipes = recipeRepository
					.findByMealTimingAndFoodGroupsValueRegex(mealTimeEnum.getTime(), "Cereal");
			if (mainCourseRecipes != null && !mainCourseRecipes.isEmpty())
				Collections.shuffle(mainCourseRecipes);
			System.out.println(mealTimeEnum + ": %of RDA = " + perOfValue + " Total mainCourseRecipes= "
					+ (mainCourseRecipes != null ? mainCourseRecipes.size() : 0));

			List<RecipeCollection> sideCourseRecipes = recipeRepository
					.findByFoodGroupsValueIn(Arrays.asList("Pulses"));

			if (sideCourseRecipes != null && !sideCourseRecipes.isEmpty())
				Collections.shuffle(sideCourseRecipes);

			List<RecipeCollection> vegetablesRecipes = recipeRepository.findByMealTimingAndFoodGroupsValueIn(
					mealTimeEnum.getTime(), Arrays.asList("Other Vegetables", "Leafy Vegetables"));
			RecipeCollection vegetablesRecipe1 = null, vegetablesRecipe2 = null;
			if (vegetablesRecipes != null && !vegetablesRecipes.isEmpty()) {
				Collections.shuffle(vegetablesRecipes);
				vegetablesRecipe1 = vegetablesRecipes.get(0);
				if (vegetablesRecipes.size() > 1)
					vegetablesRecipe1 = vegetablesRecipes.get(1);
			}

			RecipeCollection curdRecipe = recipeRepository.findByNameRegex("Curd Lassi");
			System.out.println(mealTimeEnum + ": Total sideCourseRecipes"
					+ (sideCourseRecipes != null ? sideCourseRecipes.size() : 0));

			for (RecipeCollection recipeCollection : mainCourseRecipes) {
				boolean isMatched = false;
				if (sideCourseRecipes != null && !sideCourseRecipes.isEmpty()) {
					for (RecipeCollection sideDish : sideCourseRecipes) {
						System.out.print("Recipe Name : " + recipeCollection.getName());
						System.out.print(", vegetablesRecipe1 : "
								+ (vegetablesRecipe1 != null ? vegetablesRecipe1.getName() : ""));
						System.out.print(", vegetablesRecipe2 : "
								+ (vegetablesRecipe2 != null ? vegetablesRecipe2.getName() : ""));
						System.out.print(", curdRecipe : " + (curdRecipe != null ? curdRecipe.getName() : ""));

						System.out.println(" and " + sideDish.getName());
						CombineRecipeNutrients forCheck = addNutrientsValue(recipeCollection, sideDish,
								vegetablesRecipe1, vegetablesRecipe2, curdRecipe);
						if (bodyParametersNutrientsMatchesRecipeNutrients(nutritionRDA, forCheck, perOfValue)) {
							isMatched = true;
							DietPlanRecipeAddItem recipe = new DietPlanRecipeAddItem();
							BeanUtil.map(sideDish, recipe);
							recipeAddItems.add(recipe);
							break;
						}
					}
					if (isMatched) {
						DietPlanRecipeAddItem recipe = new DietPlanRecipeAddItem();
						BeanUtil.map(recipeCollection, recipe);
						recipeAddItems.add(recipe);
						if (vegetablesRecipe1 != null) {
							DietPlanRecipeAddItem recipe2 = new DietPlanRecipeAddItem();
							BeanUtil.map(vegetablesRecipe1, recipe2);
							recipeAddItems.add(recipe2);
						}
						if (vegetablesRecipe2 != null) {
							DietPlanRecipeAddItem recipe2 = new DietPlanRecipeAddItem();
							BeanUtil.map(vegetablesRecipe2, recipe2);
							recipeAddItems.add(recipe2);
						}
						if (curdRecipe != null) {
							DietPlanRecipeAddItem recipe2 = new DietPlanRecipeAddItem();
							BeanUtil.map(curdRecipe, recipe2);
							recipeAddItems.add(recipe2);
						}
						break;
					}
				} else {
					System.out.print("Recipe Name : " + recipeCollection.getName());
					System.out.print(
							", vegetablesRecipe1 : " + (vegetablesRecipe1 != null ? vegetablesRecipe1.getName() : ""));
					System.out.print(
							", vegetablesRecipe2 : " + (vegetablesRecipe2 != null ? vegetablesRecipe2.getName() : ""));
					System.out.println(", curdRecipe : " + (curdRecipe != null ? curdRecipe.getName() : ""));

					CombineRecipeNutrients forCheck = addNutrientsValue(recipeCollection, vegetablesRecipe1,
							vegetablesRecipe2, curdRecipe);

					if (bodyParametersNutrientsMatchesRecipeNutrients(nutritionRDA, forCheck, perOfValue)) {
						DietPlanRecipeAddItem recipe = new DietPlanRecipeAddItem();
						BeanUtil.map(recipeCollection, recipe);
						recipeAddItems.add(recipe);
						if (vegetablesRecipe1 != null) {
							DietPlanRecipeAddItem recipe2 = new DietPlanRecipeAddItem();
							BeanUtil.map(vegetablesRecipe1, recipe2);
							recipeAddItems.add(recipe2);
						}
						if (vegetablesRecipe2 != null) {
							DietPlanRecipeAddItem recipe2 = new DietPlanRecipeAddItem();
							BeanUtil.map(vegetablesRecipe2, recipe2);
							recipeAddItems.add(recipe2);
						}
						if (curdRecipe != null) {
							DietPlanRecipeAddItem recipe2 = new DietPlanRecipeAddItem();
							BeanUtil.map(curdRecipe, recipe2);
							recipeAddItems.add(recipe2);
						}
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private CombineRecipeNutrients addNutrientsValue(RecipeCollection... recipes) {

		CombineRecipeNutrients forCheck = new CombineRecipeNutrients();

		Map<String, Double> generalNutrients = new HashMap<String, Double>();
		Map<String, Double> carbNutrients = new HashMap<String, Double>();
		Map<String, Double> lipidNutrients = new HashMap<String, Double>();
		Map<String, Double> proteinAminoAcidNutrients = new HashMap<String, Double>();
		Map<String, Double> vitaminNutrients = new HashMap<String, Double>();
		Map<String, Double> mineralNutrients = new HashMap<String, Double>();
		Map<String, Double> otherNutrients = new HashMap<String, Double>();

		for (RecipeCollection recipe : recipes) {
			if (recipe != null) {
				recipe.getGeneralNutrients().forEach((key, value) -> generalNutrients.put(key,
						generalNutrients.getOrDefault(key, 0.0) + Double.parseDouble(value)));
				recipe.getCarbNutrients().forEach((key, value) -> carbNutrients.put(key,
						carbNutrients.getOrDefault(key, 0.0) + Double.parseDouble(value)));
				recipe.getLipidNutrients().forEach((key, value) -> lipidNutrients.put(key,
						lipidNutrients.getOrDefault(key, 0.0) + Double.parseDouble(value)));
				recipe.getProteinAminoAcidNutrients().forEach((key, value) -> proteinAminoAcidNutrients.put(key,
						proteinAminoAcidNutrients.getOrDefault(key, 0.0) + Double.parseDouble(value)));
				recipe.getVitaminNutrients().forEach((key, value) -> vitaminNutrients.put(key,
						vitaminNutrients.getOrDefault(key, 0.0) + Double.parseDouble(value)));
				recipe.getMineralNutrients().forEach((key, value) -> mineralNutrients.put(key,
						mineralNutrients.getOrDefault(key, 0.0) + Double.parseDouble(value)));
				recipe.getOtherNutrients().forEach((key, value) -> otherNutrients.put(key,
						otherNutrients.getOrDefault(key, 0.0) + Double.parseDouble(value)));
			}
		}
		forCheck.setGeneralNutrients(generalNutrients);
		forCheck.setCarbNutrients(carbNutrients);
		forCheck.setLipidNutrients(lipidNutrients);
		forCheck.setProteinAminoAcidNutrients(proteinAminoAcidNutrients);
		forCheck.setVitaminNutrients(vitaminNutrients);
		forCheck.setMineralNutrients(mineralNutrients);
		forCheck.setOtherNutrients(otherNutrients);

		return forCheck;
	}

	private void getBreakFastSelectedRecipes(MealTimeEnum mealTimeEnum, List<DietPlanRecipeAddItem> recipeAddItems,
			NutritionRDA nutritionRDA, int perOfValue) {
		try {
			List<RecipeCollection> mainCourseRecipes = recipeRepository
					.findByMealTimingAndFoodGroupsValueRegex(mealTimeEnum.getTime(), "Cereal");
			if (mainCourseRecipes != null && !mainCourseRecipes.isEmpty())
				Collections.shuffle(mainCourseRecipes);
			System.out.println(mealTimeEnum + ": %of RDA = " + perOfValue + " Total mainCourseRecipes= "
					+ (mainCourseRecipes != null ? mainCourseRecipes.size() : 0));

			List<RecipeCollection> sideCourseRecipes = recipeRepository
					.findByFoodGroupsValueIn(Arrays.asList("Pulses"));

			if (sideCourseRecipes != null && !sideCourseRecipes.isEmpty())
				Collections.shuffle(sideCourseRecipes);

			List<RecipeCollection> recipesSide2 = recipeRepository
					.findByFoodGroupsValueIn(Arrays.asList("Milk And Milk Products", "Sugar"));
			RecipeCollection recipeSide2 = null;
			if (recipesSide2 != null && recipesSide2.isEmpty()) {
				Collections.shuffle(recipesSide2);
				recipeSide2 = recipesSide2.get(0);
			}

			System.out.println(mealTimeEnum + ": Total sideCourseRecipes"
					+ (sideCourseRecipes != null ? sideCourseRecipes.size() : 0));

			for (RecipeCollection recipeCollection : mainCourseRecipes) {
				boolean isMatched = false;
				if (sideCourseRecipes != null && !sideCourseRecipes.isEmpty()) {
					for (RecipeCollection sideDish : sideCourseRecipes) {
						System.out
								.println("Recipe Name : " + recipeCollection.getName() + " and " + sideDish.getName());
						CombineRecipeNutrients forCheck = new CombineRecipeNutrients();
						forCheck.setGeneralNutrients(addNutrientsValue(recipeCollection.getGeneralNutrients(),
								sideDish.getGeneralNutrients(), (recipeSide2 != null ? recipeSide2.getGeneralNutrients()
										: new HashMap<String, String>())));
						forCheck.setCarbNutrients(addNutrientsValue(recipeCollection.getCarbNutrients(),
								sideDish.getCarbNutrients(), (recipeSide2 != null ? recipeSide2.getCarbNutrients()
										: new HashMap<String, String>())));
						forCheck.setLipidNutrients(addNutrientsValue(recipeCollection.getLipidNutrients(),
								sideDish.getLipidNutrients(), (recipeSide2 != null ? recipeSide2.getLipidNutrients()
										: new HashMap<String, String>())));
						forCheck.setProteinAminoAcidNutrients(
								addNutrientsValue(recipeCollection.getProteinAminoAcidNutrients(),
										sideDish.getProteinAminoAcidNutrients(),
										(recipeSide2 != null ? recipeSide2.getProteinAminoAcidNutrients()
												: new HashMap<String, String>())));
						forCheck.setVitaminNutrients(addNutrientsValue(recipeCollection.getVitaminNutrients(),
								sideDish.getVitaminNutrients(), (recipeSide2 != null ? recipeSide2.getVitaminNutrients()
										: new HashMap<String, String>())));
						forCheck.setMineralNutrients(addNutrientsValue(recipeCollection.getMineralNutrients(),
								sideDish.getMineralNutrients(), (recipeSide2 != null ? recipeSide2.getMineralNutrients()
										: new HashMap<String, String>())));
						forCheck.setOtherNutrients(addNutrientsValue(recipeCollection.getOtherNutrients(),
								sideDish.getOtherNutrients(), (recipeSide2 != null ? recipeSide2.getOtherNutrients()
										: new HashMap<String, String>())));

						if (bodyParametersNutrientsMatchesRecipeNutrients(nutritionRDA, forCheck, perOfValue)) {
							isMatched = true;
							DietPlanRecipeAddItem recipe = new DietPlanRecipeAddItem();
							BeanUtil.map(sideDish, recipe);
							recipeAddItems.add(recipe);
							break;
						}
					}
					if (isMatched) {
						DietPlanRecipeAddItem recipe = new DietPlanRecipeAddItem();
						BeanUtil.map(recipeCollection, recipe);
						recipeAddItems.add(recipe);
						if (recipeSide2 != null) {
							DietPlanRecipeAddItem recipe2 = new DietPlanRecipeAddItem();
							BeanUtil.map(recipeSide2, recipe2);
							recipeAddItems.add(recipe2);
						}
						break;
					}
				} else {
					System.out.println("Recipe Name : " + recipeCollection.getName());

					CombineRecipeNutrients forCheck = new CombineRecipeNutrients();
					forCheck.setGeneralNutrients(addNutrientsValue(recipeCollection.getGeneralNutrients()));
					forCheck.setCarbNutrients(addNutrientsValue(recipeCollection.getCarbNutrients()));
					forCheck.setLipidNutrients(addNutrientsValue(recipeCollection.getLipidNutrients()));
					forCheck.setProteinAminoAcidNutrients(
							addNutrientsValue(recipeCollection.getProteinAminoAcidNutrients()));
					forCheck.setVitaminNutrients(addNutrientsValue(recipeCollection.getVitaminNutrients()));
					forCheck.setMineralNutrients(addNutrientsValue(recipeCollection.getMineralNutrients()));
					forCheck.setOtherNutrients(addNutrientsValue(recipeCollection.getOtherNutrients()));

					if (bodyParametersNutrientsMatchesRecipeNutrients(nutritionRDA, forCheck, perOfValue)) {
						DietPlanRecipeAddItem recipe = new DietPlanRecipeAddItem();
						BeanUtil.map(recipeCollection, recipe);
						recipeAddItems.add(recipe);
						if (recipeSide2 != null) {
							DietPlanRecipeAddItem recipe2 = new DietPlanRecipeAddItem();
							BeanUtil.map(recipeSide2, recipe2);
							recipeAddItems.add(recipe2);
						}
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SafeVarargs
	private Map<String, Double> addNutrientsValue(Map<String, String>... nutrients) {
		Map<String, Double> response = new HashMap<String, Double>();
		for (Map<String, String> nutrient : nutrients) {
			nutrient.forEach(
					(key, value) -> response.put(key, response.getOrDefault(key, 0.0) + Double.parseDouble(value)));
		}
		return response;
	}

	@Override
	public DietPlan getRecipes(String userId, List<MealTimeEnum> mealTime, String doctorId, String locationId,
			String hospitalId) {
		try {
			return recipeSelection(userId, mealTime, doctorId, locationId, hospitalId);
		} catch (BusinessException e) {
			logger.error("Error while getting Recipes for patient " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Recipes for patient " + e.getMessage());
		}
	}

}
