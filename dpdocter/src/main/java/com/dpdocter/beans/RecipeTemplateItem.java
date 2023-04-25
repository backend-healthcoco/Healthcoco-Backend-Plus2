package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.LevelType;
import com.dpdocter.enums.MealTimeEnum;

public class RecipeTemplateItem extends GenericCollection {

	private ObjectId id;

	private MealQuantity quantity;

	private MealTimeEnum mealTime;

	private List<EquivalentQuantities> equivalentMeasurements;

	private String name;

	private String recipeDescription;

	private String videoUrl;

	private List<String> recipeImages;

	private String thumbnailImages;

	private String thumbnailImage;

	private List<RecipeAddItem> includeIngredients;

	private List<RecipeAddItem> excludeIngredients;

	private List<RecipeAddItem> ingredients;

	private String dishType;

	private List<String> mealTiming;

	private String technique;

	private Boolean isPopular = false;

	private Boolean isHoliday = false;

	private Boolean discarded = false;

	private String direction;

	private String dietaryConcerns;

	private Integer forMember = 0;

	private String meal;

	private List<String> cuisine;

	private List<String> course;

	private String userId;

	private String locationId;

	private String doctorId;

	private String hospitalId;

	private Double cost = 0.0;

	private LevelType costType;

	private Integer preparationTime = 0;

	private boolean verified = false;

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
	private String principle;
	private Map<String, String> multilingualName;

	public Map<String, String> getMultilingualName() {
		return multilingualName;
	}

	public void setMultilingualName(Map<String, String> multilingualName) {
		this.multilingualName = multilingualName;
	}

	public String getPrinciple() {
		return principle;
	}

	public void setPrinciple(String principle) {
		this.principle = principle;
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

	public boolean getVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public List<String> getRecipeImages() {
		return recipeImages;
	}

	public void setRecipeImages(List<String> recipeImages) {
		this.recipeImages = recipeImages;
	}

	public String getDishType() {
		return dishType;
	}

	public void setDishType(String dishType) {
		this.dishType = dishType;
	}

	public String getTechnique() {
		return technique;
	}

	public void setTechnique(String technique) {
		this.technique = technique;
	}

	public Boolean getIsPopular() {
		return isPopular;
	}

	public void setIsPopular(Boolean isPopular) {
		this.isPopular = isPopular;
	}

	public Boolean getIsHoliday() {
		return isHoliday;
	}

	public void setIsHoliday(Boolean isHoliday) {
		this.isHoliday = isHoliday;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getDietaryConcerns() {
		return dietaryConcerns;
	}

	public void setDietaryConcerns(String dietaryConcerns) {
		this.dietaryConcerns = dietaryConcerns;
	}

	public Integer getForMember() {
		return forMember;
	}

	public void setForMember(Integer forMember) {
		this.forMember = forMember;
	}

	public String getMeal() {
		return meal;
	}

	public void setMeal(String meal) {
		this.meal = meal;
	}

	public List<String> getCuisine() {
		return cuisine;
	}

	public void setCuisine(List<String> cuisine) {
		this.cuisine = cuisine;
	}

	public List<String> getCourse() {
		return course;
	}

	public void setCourse(List<String> course) {
		this.course = course;
	}

	public Integer getPreparationTime() {
		return preparationTime;
	}

	public void setPreparationTime(Integer preparationTime) {
		this.preparationTime = preparationTime;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<RecipeAddItem> getIncludeIngredients() {
		return includeIngredients;
	}

	public void setIncludeIngredients(List<RecipeAddItem> includeIngredients) {
		this.includeIngredients = includeIngredients;
	}

	public List<RecipeAddItem> getExcludeIngredients() {
		return excludeIngredients;
	}

	public void setExcludeIngredients(List<RecipeAddItem> excludeIngredients) {
		this.excludeIngredients = excludeIngredients;
	}

	public List<RecipeAddItem> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<RecipeAddItem> ingredients) {
		this.ingredients = ingredients;
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

	public String getThumbnailImages() {
		return thumbnailImages;
	}

	public void setThumbnailImages(String thumbnailImages) {
		this.thumbnailImages = thumbnailImages;
	}

	public MealQuantity getCarbohydrate() {
		return carbohydrate;
	}

	public void setCarbohydrate(MealQuantity carbohydrate) {
		this.carbohydrate = carbohydrate;
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

	public List<String> getMealTiming() {
		return mealTiming;
	}

	public void setMealTiming(List<String> mealTiming) {
		this.mealTiming = mealTiming;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public MealQuantity getFiber() {
		return fiber;
	}

	public void setFiber(MealQuantity fiber) {
		this.fiber = fiber;
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

	public String getThumbnailImage() {
		return thumbnailImage;
	}

	public void setThumbnailImage(String thumbnailImage) {
		this.thumbnailImage = thumbnailImage;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public MealTimeEnum getMealTime() {
		return mealTime;
	}

	public void setMealTime(MealTimeEnum mealTime) {
		this.mealTime = mealTime;
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
		return "RecipeTemplateItem [id=" + id + ", quantity=" + quantity + ", mealTime=" + mealTime
				+ ", equivalentMeasurements=" + equivalentMeasurements + ", name=" + name + ", recipeDescription="
				+ recipeDescription + ", videoUrl=" + videoUrl + ", recipeImages=" + recipeImages + ", thumbnailImages="
				+ thumbnailImages + ", thumbnailImage=" + thumbnailImage + ", includeIngredients=" + includeIngredients
				+ ", excludeIngredients=" + excludeIngredients + ", ingredients=" + ingredients + ", dishType="
				+ dishType + ", mealTiming=" + mealTiming + ", technique=" + technique + ", isPopular=" + isPopular
				+ ", isHoliday=" + isHoliday + ", discarded=" + discarded + ", direction=" + direction
				+ ", dietaryConcerns=" + dietaryConcerns + ", forMember=" + forMember + ", meal=" + meal + ", cuisine="
				+ cuisine + ", course=" + course + ", userId=" + userId + ", locationId=" + locationId + ", doctorId="
				+ doctorId + ", hospitalId=" + hospitalId + ", cost=" + cost + ", costType=" + costType
				+ ", preparationTime=" + preparationTime + ", verified=" + verified + ", calories=" + calories
				+ ", fat=" + fat + ", protein=" + protein + ", carbohydrate=" + carbohydrate + ", fiber=" + fiber
				+ ", generalNutrients=" + generalNutrients + ", carbNutrients=" + carbNutrients + ", lipidNutrients="
				+ lipidNutrients + ", proteinAminoAcidNutrients=" + proteinAminoAcidNutrients + ", vitaminNutrients="
				+ vitaminNutrients + ", mineralNutrients=" + mineralNutrients + ", otherNutrients=" + otherNutrients
				+ ", nutrientValueAtRecipeLevel=" + nutrientValueAtRecipeLevel + ", dietoryEvaluation="
				+ dietoryEvaluation + ", phLevel=" + phLevel + ", giLevel=" + giLevel + ", foodCultures=" + foodCultures
				+ ", diseaseFriendly=" + diseaseFriendly + ", isPrebiotic=" + isPrebiotic + ", isProBiotic="
				+ isProBiotic + ", cookingMethod=" + cookingMethod + ", medicineDosage=" + medicineDosage
				+ ", foodPreparationTemperature=" + foodPreparationTemperature + ", communities=" + communities
				+ ", foodGroups=" + foodGroups + ", nutrientTypes=" + nutrientTypes + ", nutrientGoals=" + nutrientGoals
				+ ", diseases=" + diseases + ", principle=" + principle + ", multilingualName=" + multilingualName
				+ "]";
	}
}
