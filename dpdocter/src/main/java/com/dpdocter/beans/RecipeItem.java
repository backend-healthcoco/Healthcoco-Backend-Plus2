package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.dpdocter.enums.LevelType;
import com.dpdocter.enums.MealTimeEnum;
import com.dpdocter.response.NutritionPlanWithNameResponse;

public class RecipeItem {
	private ObjectId id;

	private String name;

	private String recipeDescription;
	
	private MealQuantity quantity;

	private MealTimeEnum mealTime;
	
	private Double cost = 0.0;

	private LevelType costType;

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

	private boolean nutrientValueAtRecipeLevel = false;

	private List<NutritionPlanWithNameResponse> plans;

	private String dietoryEvaluation;
	private String phLevel;
	private String giLevel;
	private List<String> foodCultures;
	private List<String> diseaseFriendly;
	private Boolean isPrebiotic = false;
	private Boolean isProBiotic = false;
	private String cookingMethod;
	private String medicineDosage;
	private String foodPreparationTemperature;
	private List<FoodCommunity> communities;
	private List<FoodGroup> foodGroups;
	private List<RecipeNutrientType> nutrientTypes;
	private List<NutrientGoal> nutrientGoals;
	private List<Disease> diseases;
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public MealQuantity getCalories() {
		return calories;
	}

	public void setCalories(MealQuantity calories) {
		this.calories = calories;
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

	public MealQuantity getFiber() {
		return fiber;
	}

	public void setFiber(MealQuantity fiber) {
		this.fiber = fiber;
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

	public String getRecipeDescription() {
		return recipeDescription;
	}

	public void setRecipeDescription(String recipeDescription) {
		this.recipeDescription = recipeDescription;
	}

	public List<FoodCommunity> getCommunities() {
		return communities;
	}

	public void setCommunities(List<FoodCommunity> communities) {
		this.communities = communities;
	}

	public List<FoodGroup> getFoodGroups() {
		return foodGroups;
	}

	public void setFoodGroups(List<FoodGroup> foodGroups) {
		this.foodGroups = foodGroups;
	}

	public List<RecipeNutrientType> getNutrientTypes() {
		return nutrientTypes;
	}

	public void setNutrientTypes(List<RecipeNutrientType> nutrientTypes) {
		this.nutrientTypes = nutrientTypes;
	}

	public List<NutrientGoal> getNutrientGoals() {
		return nutrientGoals;
	}

	public void setNutrientGoals(List<NutrientGoal> nutrientGoals) {
		this.nutrientGoals = nutrientGoals;
	}

	public List<Disease> getDiseases() {
		return diseases;
	}

	public void setDiseases(List<Disease> diseases) {
		this.diseases = diseases;
	}

	@Override
	public String toString() {
		return "RecipeItem [id=" + id + ", name=" + name + ", recipeDescription=" + recipeDescription + ", quantity="
				+ quantity + ", mealTime=" + mealTime + ", cost=" + cost + ", costType=" + costType
				+ ", equivalentMeasurements=" + equivalentMeasurements + ", calories=" + calories + ", fat=" + fat
				+ ", protein=" + protein + ", carbohydrate=" + carbohydrate + ", fiber=" + fiber + ", generalNutrients="
				+ generalNutrients + ", carbNutrients=" + carbNutrients + ", lipidNutrients=" + lipidNutrients
				+ ", proteinAminoAcidNutrients=" + proteinAminoAcidNutrients + ", vitaminNutrients=" + vitaminNutrients
				+ ", mineralNutrients=" + mineralNutrients + ", otherNutrients=" + otherNutrients
				+ ", nutrientValueAtRecipeLevel=" + nutrientValueAtRecipeLevel + ", plans=" + plans
				+ ", dietoryEvaluation=" + dietoryEvaluation + ", phLevel=" + phLevel + ", giLevel=" + giLevel
				+ ", foodCultures=" + foodCultures + ", diseaseFriendly=" + diseaseFriendly + ", isPrebiotic="
				+ isPrebiotic + ", isProBiotic=" + isProBiotic + ", cookingMethod=" + cookingMethod
				+ ", medicineDosage=" + medicineDosage + ", foodPreparationTemperature=" + foodPreparationTemperature
				+ ", communities=" + communities + ", foodGroups=" + foodGroups + ", nutrientTypes=" + nutrientTypes
				+ ", nutrientGoals=" + nutrientGoals + ", diseases=" + diseases + "]";
	}
}
