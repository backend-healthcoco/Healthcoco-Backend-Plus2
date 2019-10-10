package com.dpdocter.elasticsearch.response;

import java.util.List;
import java.util.Map;

import com.dpdocter.beans.EquivalentQuantities;
import com.dpdocter.beans.MealQuantity;
import com.dpdocter.enums.LevelType;
import com.dpdocter.response.RecipeItemResponse;

public class ESRecipeResponse {

	private String id;

	private String name;

	private MealQuantity quantity;

	private String recipeDescription;

	private List<EquivalentQuantities> equivalentMeasurements;

	private List<RecipeItemResponse> ingredients;

	private boolean nutrientValueAtRecipeLevel = false;

	private MealQuantity calories;

	private List<String> mealTiming;

	private String direction;

	private Double cost = 0.0;

	private LevelType costType;

	private MealQuantity fiber;

	private MealQuantity fat;

	private MealQuantity protein;

	private MealQuantity carbohydreate;

	private Map<String, String> generalNutrients;

	private Map<String, String> carbNutrients;

	private Map<String, String> lipidNutrients;

	private Map<String, String> proteinAminoAcidNutrients;

	private Map<String, String> vitaminNutrients;

	private Map<String, String> mineralNutrients;

	private Map<String, String> otherNutrients;

	public List<EquivalentQuantities> getEquivalentMeasurements() {
		return equivalentMeasurements;
	}

	public void setEquivalentMeasurements(List<EquivalentQuantities> equivalentMeasurements) {
		this.equivalentMeasurements = equivalentMeasurements;
	}

	public boolean getNutrientValueAtRecipeLevel() {
		return nutrientValueAtRecipeLevel;
	}

	public void setNutrientValueAtRecipeLevel(boolean nutrientValueAtRecipeLevel) {
		this.nutrientValueAtRecipeLevel = nutrientValueAtRecipeLevel;
	}

	public MealQuantity getCalories() {
		return calories;
	}

	public void setCalories(MealQuantity calories) {
		this.calories = calories;
	}

	public List<String> getMealTiming() {
		return mealTiming;
	}

	public void setMealTiming(List<String> mealTiming) {
		this.mealTiming = mealTiming;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MealQuantity getQuantity() {
		return quantity;
	}

	public void setQuantity(MealQuantity quantity) {
		this.quantity = quantity;
	}

	public MealQuantity getFiber() {
		return fiber;
	}

	public void setFiber(MealQuantity fiber) {
		this.fiber = fiber;
	}

	public MealQuantity getFat() {
		return fat;
	}

	public void setFat(MealQuantity fat) {
		this.fat = fat;
	}

	public MealQuantity getProtein() {
		return protein;
	}

	public void setProtein(MealQuantity protein) {
		this.protein = protein;
	}

	public MealQuantity getCarbohydreate() {
		return carbohydreate;
	}

	public void setCarbohydreate(MealQuantity carbohydreate) {
		this.carbohydreate = carbohydreate;
	}

	public List<RecipeItemResponse> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<RecipeItemResponse> ingredients) {
		this.ingredients = ingredients;
	}

	public Map<String, String> getGeneralNutrients() {
		return generalNutrients;
	}

	public void setGeneralNutrients(Map<String, String> generalNutrients) {
		this.generalNutrients = generalNutrients;
	}

	public Map<String, String> getCarbNutrients() {
		return carbNutrients;
	}

	public void setCarbNutrients(Map<String, String> carbNutrients) {
		this.carbNutrients = carbNutrients;
	}

	public Map<String, String> getLipidNutrients() {
		return lipidNutrients;
	}

	public void setLipidNutrients(Map<String, String> lipidNutrients) {
		this.lipidNutrients = lipidNutrients;
	}

	public Map<String, String> getProteinAminoAcidNutrients() {
		return proteinAminoAcidNutrients;
	}

	public void setProteinAminoAcidNutrients(Map<String, String> proteinAminoAcidNutrients) {
		this.proteinAminoAcidNutrients = proteinAminoAcidNutrients;
	}

	public Map<String, String> getVitaminNutrients() {
		return vitaminNutrients;
	}

	public void setVitaminNutrients(Map<String, String> vitaminNutrients) {
		this.vitaminNutrients = vitaminNutrients;
	}

	public Map<String, String> getMineralNutrients() {
		return mineralNutrients;
	}

	public void setMineralNutrients(Map<String, String> mineralNutrients) {
		this.mineralNutrients = mineralNutrients;
	}

	public Map<String, String> getOtherNutrients() {
		return otherNutrients;
	}

	public void setOtherNutrients(Map<String, String> otherNutrients) {
		this.otherNutrients = otherNutrients;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public LevelType getCostType() {
		return costType;
	}

	public void setCostType(LevelType costType) {
		this.costType = costType;
	}

	public String getRecipeDescription() {
		return recipeDescription;
	}

	public void setRecipeDescription(String recipeDescription) {
		this.recipeDescription = recipeDescription;
	}

}
