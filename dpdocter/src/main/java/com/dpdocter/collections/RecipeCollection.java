package com.dpdocter.collections;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.EquivalentQuantities;
import com.dpdocter.beans.FoodCommunity;
import com.dpdocter.beans.FoodGroup;
import com.dpdocter.beans.MealQuantity;
import com.dpdocter.beans.NutrientGoal;
import com.dpdocter.beans.RecipeItem;
import com.dpdocter.beans.RecipeNutrientType;
import com.dpdocter.enums.LevelType;
import com.dpdocter.enums.MealTimeEnum;

@Document(collection = "recipe_cl")
public class RecipeCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private String name;

	@Field
	private String recipeDescription;
	
	@Field
	private MealQuantity quantity;

	@Field
	private List<EquivalentQuantities> equivalentMeasurements;

	@Field
	private String videoUrl;

	@Field
	private List<String> recipeImages;

	@Field
	private List<RecipeItem> includeIngredients;

	@Field
	private List<RecipeItem> excludeIngredients;

	@Field
	private List<RecipeItem> ingredients;

	@Field
	private ObjectId userId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId hospitalId;

	@Field
	private String dishType;

	@Field
	private String technique;

	@Field
	private Boolean isPopular = false;

	@Field
	private Boolean isHoliday = false;

	@Field
	private Boolean discarded = false;

	@Field
	private String direction;

	@Field
	private String dietaryConcerns;

	@Field
	private Integer forMember = 0;

	@Field
	private Double cost = 0.0;

	@Field
	private LevelType costType;

	@Field
	private String meal;

	@Field
	private List<String> cuisine;

	@Field
	private List<String> course;

	@Field
	private Integer preparationTime = 0;

	@Field
	private boolean verified = false;

	@Field
	private List<String> mealTiming;

	@Field
	private MealQuantity calories;

	@Field
	private MealQuantity fat;

	@Field
	private MealQuantity protein;

	@Field
	private MealQuantity carbohydrate;

	@Field
	private MealQuantity fiber;

	@Field
	private Map<String, String> generalNutrients;

	@Field
	private Map<String, String> carbNutrients;

	@Field
	private Map<String, String> lipidNutrients;

	@Field
	private Map<String, String> proteinAminoAcidNutrients;

	@Field
	private Map<String, String> vitaminNutrients;

	@Field
	private Map<String, String> mineralNutrients;

	@Field
	private Map<String, String> otherNutrients;

	@Field
	private boolean nutrientValueAtRecipeLevel = false;

	@Field
	private List<ObjectId> planIds;

	@Field
	private String dietoryEvaluation;
	
	@Field
	private String phLevel;
	
	@Field
	private String giLevel;
	
	@Field
	private List<FoodCommunity> communities;
	
	@Field
	private List<String> foodCultures;
	
	@Field
	private List<String> diseaseFriendly;
	
	@Field
	private Boolean isPrebiotic = false;
	
	@Field
	private Boolean isProBiotic = false;
	
	@Field
	private String cookingMethod;
	
	@Field
	private String medicineDosage;
	
	@Field
	private String foodPreparationTemperature;
	
	@Field
	private List<FoodGroup> foodGroups;
	
	@Field
	private List<RecipeNutrientType> nutrientTypes;
	
	@Field
	private List<NutrientGoal> nutrientGoals;
	
	@Field
	private List<DiseasesCollection> diseases;
	
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

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

	public List<RecipeItem> getIncludeIngredients() {
		return includeIngredients;
	}

	public void setIncludeIngredients(List<RecipeItem> includeIngredients) {
		this.includeIngredients = includeIngredients;
	}

	public List<RecipeItem> getExcludeIngredients() {
		return excludeIngredients;
	}

	public void setExcludeIngredients(List<RecipeItem> excludeIngredients) {
		this.excludeIngredients = excludeIngredients;
	}

	public List<RecipeItem> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<RecipeItem> ingredients) {
		this.ingredients = ingredients;
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

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getMeal() {
		return meal;
	}

	public void setMeal(String meal) {
		this.meal = meal;
	}

	public Integer getPreparationTime() {
		return preparationTime;
	}

	public void setPreparationTime(Integer preparationTime) {
		this.preparationTime = preparationTime;
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

	public boolean getVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public List<String> getMealTiming() {
		return mealTiming;
	}

	public void setMealTiming(List<String> mealTiming) {
		this.mealTiming = mealTiming;
	}

	public boolean getNutrientValueAtRecipeLevel() {
		return nutrientValueAtRecipeLevel;
	}

	public void setNutrientValueAtRecipeLevel(boolean nutrientValueAtRecipeLevel) {
		this.nutrientValueAtRecipeLevel = nutrientValueAtRecipeLevel;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
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

	public LevelType getCostType() {
		return costType;
	}

	public void setCostType(LevelType costType) {
		this.costType = costType;
	}

	public List<ObjectId> getPlanIds() {
		return planIds;
	}

	public void setPlanIds(List<ObjectId> planIds) {
		this.planIds = planIds;
	}
	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
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

	public List<DiseasesCollection> getDiseases() {
		return diseases;
	}

	public void setDiseases(List<DiseasesCollection> diseases) {
		this.diseases = diseases;
	}

	@Override
	public String toString() {
		return "RecipeCollection [id=" + id + ", name=" + name + ", recipeDescription=" + recipeDescription
				+ ", quantity=" + quantity + ", equivalentMeasurements="
				+ equivalentMeasurements + ", videoUrl=" + videoUrl + ", recipeImages=" + recipeImages
				+ ", includeIngredients=" + includeIngredients + ", excludeIngredients=" + excludeIngredients
				+ ", ingredients=" + ingredients + ", userId=" + userId + ", locationId=" + locationId + ", doctorId="
				+ doctorId + ", hospitalId=" + hospitalId + ", dishType=" + dishType + ", technique=" + technique
				+ ", isPopular=" + isPopular + ", isHoliday=" + isHoliday + ", discarded=" + discarded + ", direction="
				+ direction + ", dietaryConcerns=" + dietaryConcerns + ", forMember=" + forMember + ", cost=" + cost
				+ ", costType=" + costType + ", meal=" + meal + ", cuisine=" + cuisine + ", course=" + course
				+ ", preparationTime=" + preparationTime + ", verified=" + verified + ", mealTiming=" + mealTiming
				+ ", calories=" + calories + ", fat=" + fat + ", protein=" + protein + ", carbohydrate=" + carbohydrate
				+ ", fiber=" + fiber + ", generalNutrients=" + generalNutrients + ", carbNutrients=" + carbNutrients
				+ ", lipidNutrients=" + lipidNutrients + ", proteinAminoAcidNutrients=" + proteinAminoAcidNutrients
				+ ", vitaminNutrients=" + vitaminNutrients + ", mineralNutrients=" + mineralNutrients
				+ ", otherNutrients=" + otherNutrients + ", nutrientValueAtRecipeLevel=" + nutrientValueAtRecipeLevel
				+ ", planIds=" + planIds + ", dietoryEvaluation=" + dietoryEvaluation + ", phLevel=" + phLevel
				+ ", giLevel=" + giLevel + ", communities=" + communities + ", foodCultures=" + foodCultures
				+ ", diseaseFriendly=" + diseaseFriendly + ", isPrebiotic=" + isPrebiotic + ", isProBiotic="
				+ isProBiotic + ", cookingMethod=" + cookingMethod + ", medicineDosage=" + medicineDosage
				+ ", foodPreparationTemperature=" + foodPreparationTemperature + ", foodGroups=" + foodGroups
				+ ", nutrientTypes=" + nutrientTypes + ", nutrientGoals=" + nutrientGoals + ", diseases=" + diseases
				+ "]";
	}
}
