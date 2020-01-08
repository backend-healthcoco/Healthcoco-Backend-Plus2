package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

import com.dpdocter.enums.LevelType;
import com.dpdocter.enums.MealTimeEnum;
import com.dpdocter.response.NutritionPlanWithNameResponse;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeAddItem {
	private String id;

	private String name;

	private String recipeDescription;
	
	private Double cost = 0.0;

	private LevelType costType;

	private MealQuantity quantity;

	private MealTimeEnum mealTime;
	
	private List<EquivalentQuantities> equivalentMeasurements;

	private MealQuantity calories;

	private MealQuantity fat;

	private MealQuantity protein;

	private MealQuantity carbohydrate;

	private MealQuantity fiber;

	private Map<String, String> generalNutrients;

	private Map<String, String> carbNutrients;

	private Map<String, String> lipidNutrients;

	private Map<String, String> proteinAminoAcidNutrients;

	private Map<String, String> vitaminNutrients;

	private Map<String, String> mineralNutrients;

	private Map<String, String> otherNutrients;

	private String colour;

	private boolean nutrientValueAtRecipeLevel = false;

	private List<NutritionPlanWithNameResponse> plans;

	private String dietoryEvaluation;
	private String phLevel;
	private String giLevel;
	private List<String> communities;
	private List<String> foodCultures;
	private List<String> diseaseFriendly;
	private Boolean isPrebiotic = false;
	private Boolean isProBiotic = false;
	private String cookingMethod;
	private String medicineDosage;
	private String foodPreparationTemperature;
	private List<String> foodGroups;
	private List<String> nutrientTypes;
	
	public MealQuantity getFiber() {
		return fiber;
	}

	public void setFiber(MealQuantity fiber) {
		this.fiber = fiber;
	}

	public MealQuantity getCalories() {
		return calories;
	}

	public void setCalories(MealQuantity calories) {
		this.calories = calories;
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

	public List<EquivalentQuantities> getEquivalentMeasurements() {
		return equivalentMeasurements;
	}

	public void setEquivalentMeasurements(List<EquivalentQuantities> equivalentMeasurements) {
		this.equivalentMeasurements = equivalentMeasurements;
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

	public MealQuantity getCarbohydrate() {
		return carbohydrate;
	}

	public void setCarbohydrate(MealQuantity carbohydrate) {
		this.carbohydrate = carbohydrate;
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

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}

	public MealTimeEnum getMealTime() {
		return mealTime;
	}

	public void setMealTime(MealTimeEnum mealTime) {
		this.mealTime = mealTime;
	}

	public boolean isNutrientValueAtRecipeLevel() {
		return nutrientValueAtRecipeLevel;
	}

	public void setNutrientValueAtRecipeLevel(boolean nutrientValueAtRecipeLevel) {
		this.nutrientValueAtRecipeLevel = nutrientValueAtRecipeLevel;
	}

	public List<NutritionPlanWithNameResponse> getPlans() {
		return plans;
	}

	public void setPlans(List<NutritionPlanWithNameResponse> plans) {
		this.plans = plans;
	}

	public String getDietoryEvaluation() {
		return dietoryEvaluation;
	}

	public void setDietoryEvaluation(String dietoryEvaluation) {
		this.dietoryEvaluation = dietoryEvaluation;
	}

	public String getPhLevel() {
		return phLevel;
	}

	public void setPhLevel(String phLevel) {
		this.phLevel = phLevel;
	}

	public String getGiLevel() {
		return giLevel;
	}

	public void setGiLevel(String giLevel) {
		this.giLevel = giLevel;
	}

	public List<String> getCommunities() {
		return communities;
	}

	public void setCommunities(List<String> communities) {
		this.communities = communities;
	}

	public List<String> getFoodCultures() {
		return foodCultures;
	}

	public void setFoodCultures(List<String> foodCultures) {
		this.foodCultures = foodCultures;
	}

	public List<String> getDiseaseFriendly() {
		return diseaseFriendly;
	}

	public void setDiseaseFriendly(List<String> diseaseFriendly) {
		this.diseaseFriendly = diseaseFriendly;
	}

	public Boolean getIsPrebiotic() {
		return isPrebiotic;
	}

	public void setIsPrebiotic(Boolean isPrebiotic) {
		this.isPrebiotic = isPrebiotic;
	}

	public Boolean getIsProBiotic() {
		return isProBiotic;
	}

	public void setIsProBiotic(Boolean isProBiotic) {
		this.isProBiotic = isProBiotic;
	}

	public String getCookingMethod() {
		return cookingMethod;
	}

	public void setCookingMethod(String cookingMethod) {
		this.cookingMethod = cookingMethod;
	}

	public String getMedicineDosage() {
		return medicineDosage;
	}

	public void setMedicineDosage(String medicineDosage) {
		this.medicineDosage = medicineDosage;
	}

	public String getFoodPreparationTemperature() {
		return foodPreparationTemperature;
	}

	public void setFoodPreparationTemperature(String foodPreparationTemperature) {
		this.foodPreparationTemperature = foodPreparationTemperature;
	}

	public List<String> getFoodGroups() {
		return foodGroups;
	}

	public void setFoodGroups(List<String> foodGroups) {
		this.foodGroups = foodGroups;
	}

	public List<String> getNutrientTypes() {
		return nutrientTypes;
	}

	public void setNutrientTypes(List<String> nutrientTypes) {
		this.nutrientTypes = nutrientTypes;
	}

	public String getRecipeDescription() {
		return recipeDescription;
	}

	public void setRecipeDescription(String recipeDescription) {
		this.recipeDescription = recipeDescription;
	}

	@Override
	public String toString() {
		return "RecipeAddItem [id=" + id + ", name=" + name + ", recipeDescription=" + recipeDescription + ", cost="
				+ cost + ", costType=" + costType + ", quantity=" + quantity + ", mealTime=" + mealTime
				+ ", equivalentMeasurements=" + equivalentMeasurements + ", calories=" + calories + ", fat=" + fat
				+ ", protein=" + protein + ", carbohydrate=" + carbohydrate + ", fiber=" + fiber + ", generalNutrients="
				+ generalNutrients + ", carbNutrients=" + carbNutrients + ", lipidNutrients=" + lipidNutrients
				+ ", proteinAminoAcidNutrients=" + proteinAminoAcidNutrients + ", vitaminNutrients=" + vitaminNutrients
				+ ", mineralNutrients=" + mineralNutrients + ", otherNutrients=" + otherNutrients + ", colour=" + colour
				+ ", nutrientValueAtRecipeLevel=" + nutrientValueAtRecipeLevel + ", plans=" + plans
				+ ", dietoryEvaluation=" + dietoryEvaluation + ", phLevel=" + phLevel + ", giLevel=" + giLevel
				+ ", communities=" + communities + ", foodCultures=" + foodCultures + ", diseaseFriendly="
				+ diseaseFriendly + ", isPrebiotic=" + isPrebiotic + ", isProBiotic=" + isProBiotic + ", cookingMethod="
				+ cookingMethod + ", medicineDosage=" + medicineDosage + ", foodPreparationTemperature="
				+ foodPreparationTemperature + ", foodGroups=" + foodGroups + ", nutrientTypes=" + nutrientTypes + "]";
	}

}
