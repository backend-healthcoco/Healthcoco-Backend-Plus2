package com.dpdocter.elasticsearch.document;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;

import com.dpdocter.beans.Disease;
import com.dpdocter.beans.EquivalentQuantities;
import com.dpdocter.beans.FoodCommunity;
import com.dpdocter.beans.FoodGroup;
import com.dpdocter.beans.MealQuantity;
import com.dpdocter.beans.NutrientGoal;
import com.dpdocter.beans.RecipeAddItem;
import com.dpdocter.beans.RecipeNutrientType;
import com.dpdocter.enums.LevelType;

@Document(indexName = "recipes_in", type = "recipes")
public class ESRecipeDocument {
	@Id
	private String id;

	@Field(type = FieldType.Text, fielddata = true)
	private String name;

	@Field(type = FieldType.Text)
	private String recipeDescription;

	@Field(type = FieldType.Nested)
	private MealQuantity quantity;

	@Field(type = FieldType.Nested)
	private List<EquivalentQuantities> equivalentMeasurements;

	@Field(type = FieldType.Text)
	private String videoUrl;

	@Field(type = FieldType.Nested)
	private List<String> recipeImages;

	@Field(type = FieldType.Nested)
	private List<RecipeAddItem> includeIngredients;

	@Field(type = FieldType.Nested)
	private List<RecipeAddItem> excludeIngredients;

	@Field(type = FieldType.Text)
	private String locationId;

	@Field(type = FieldType.Text)
	private String doctorId;

	@Field(type = FieldType.Text)
	private String hospitalId;

	@Field(type = FieldType.Nested)
	private List<RecipeAddItem> ingredients;

	@Field(type = FieldType.Text)
	private String dishType;

	@Field(type = FieldType.Text)
	private String technique;

	@Field(type = FieldType.Boolean)
	private Boolean isPopular = false;

	@Field(type = FieldType.Boolean)
	private Boolean isHoliday = false;

	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;

	@Field(type = FieldType.Text)
	private String direction;

	@Field(type = FieldType.Text)
	private String dietaryConcerns;

	@Field(type = FieldType.Integer)
	private Integer forMember = 0;

	@Field(type = FieldType.Double)
	private Double cost = 0.0;

	@Field(type = FieldType.Text)
	private LevelType costType;

	@Field(type = FieldType.Text)
	private String meal;

	@Field(type = FieldType.Text)
	private List<String> cuisine;

	@Field(type = FieldType.Text)
	private List<String> course;

	@Field(type = FieldType.Integer)
	private Integer preparationTime = 0;

	@Field(type = FieldType.Boolean)
	private boolean verified = false;

	@Field(type = FieldType.Date)
	private Date updatedTime = new Date();

	@Field(type = FieldType.Text)
	private String createdBy;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> mealTiming;

	@Field(type = FieldType.Nested)
	private MealQuantity calories;

	@Field(type = FieldType.Nested)
	private MealQuantity fat;

	@Field(type = FieldType.Nested)
	private MealQuantity protein;

	@Field(type = FieldType.Nested)
	private MealQuantity carbohydrate;

	@Field(type = FieldType.Nested)
	private MealQuantity fiber;

	@Field(type = FieldType.Nested)
	private Map<String, String> generalNutrients;

	@Field(type = FieldType.Nested)
	private Map<String, String> carbNutrients;

	@Field(type = FieldType.Nested)
	private Map<String, String> lipidNutrients;

	@Field(type = FieldType.Nested)
	private Map<String, String> proteinAminoAcidNutrients;

	@Field(type = FieldType.Nested)
	private Map<String, String> vitaminNutrients;

	@Field(type = FieldType.Nested)
	private Map<String, String> mineralNutrients;

	@Field(type = FieldType.Nested)
	private Map<String, String> otherNutrients;

	@Field(type = FieldType.Boolean)
	private boolean nutrientValueAtRecipeLevel = false;

	@Field(type = FieldType.Text)
	private String dietoryEvaluation;

	@Field(type = FieldType.Text)
	private String phLevel;

	@Field(type = FieldType.Text)
	private String giLevel;

	@Field(type = FieldType.Nested)
	private List<FoodCommunity> communities;

	@Field(type = FieldType.Nested)
	private List<String> foodCultures;

	@Field(type = FieldType.Nested)
	private List<String> diseaseFriendly;

	@Field(type = FieldType.Boolean)
	private Boolean isPrebiotic = false;

	@Field(type = FieldType.Boolean)
	private Boolean isProBiotic = false;

	@Field(type = FieldType.Text)
	private String cookingMethod;

	@Field(type = FieldType.Text)
	private String medicineDosage;

	@Field(type = FieldType.Text)
	private String foodPreparationTemperature;

	@Field(type = FieldType.Nested)
	private List<FoodGroup> foodGroups;

	@Field(type = FieldType.Nested)
	private List<RecipeNutrientType> nutrientTypes;

	@Field(type = FieldType.Nested)
	private List<NutrientGoal> nutrientGoals;

	@Field(type = FieldType.Nested)
	private List<Disease> diseases;

	@Field(type = FieldType.Text)
	private String principle;

	public String getPrinciple() {
		return principle;
	}

	public void setPrinciple(String principle) {
		this.principle = principle;
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

	public MealQuantity getCarbohydrate() {
		return carbohydrate;
	}

	public void setCarbohydrate(MealQuantity carbohydrate) {
		this.carbohydrate = carbohydrate;
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

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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

	public LevelType getCostType() {
		return costType;
	}

	public void setCostType(LevelType costType) {
		this.costType = costType;
	}

	public MealQuantity getFiber() {
		return fiber;
	}

	public void setFiber(MealQuantity fiber) {
		this.fiber = fiber;
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

	public List<FoodCommunity> getCommunities() {
		return communities;
	}

	public void setCommunities(List<FoodCommunity> communities) {
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
		return "ESRecipeDocument [id=" + id + ", name=" + name + ", recipeDescription=" + recipeDescription
				+ ", quantity=" + quantity + ", equivalentMeasurements=" + equivalentMeasurements + ", videoUrl="
				+ videoUrl + ", recipeImages=" + recipeImages + ", includeIngredients=" + includeIngredients
				+ ", excludeIngredients=" + excludeIngredients + ", locationId=" + locationId + ", doctorId=" + doctorId
				+ ", hospitalId=" + hospitalId + ", ingredients=" + ingredients + ", dishType=" + dishType
				+ ", technique=" + technique + ", isPopular=" + isPopular + ", isHoliday=" + isHoliday + ", discarded="
				+ discarded + ", direction=" + direction + ", dietaryConcerns=" + dietaryConcerns + ", forMember="
				+ forMember + ", cost=" + cost + ", costType=" + costType + ", meal=" + meal + ", cuisine=" + cuisine
				+ ", course=" + course + ", preparationTime=" + preparationTime + ", verified=" + verified
				+ ", updatedTime=" + updatedTime + ", createdBy=" + createdBy + ", mealTiming=" + mealTiming
				+ ", calories=" + calories + ", fat=" + fat + ", protein=" + protein + ", carbohydrate=" + carbohydrate
				+ ", fiber=" + fiber + ", generalNutrients=" + generalNutrients + ", carbNutrients=" + carbNutrients
				+ ", lipidNutrients=" + lipidNutrients + ", proteinAminoAcidNutrients=" + proteinAminoAcidNutrients
				+ ", vitaminNutrients=" + vitaminNutrients + ", mineralNutrients=" + mineralNutrients
				+ ", otherNutrients=" + otherNutrients + ", nutrientValueAtRecipeLevel=" + nutrientValueAtRecipeLevel
				+ ", dietoryEvaluation=" + dietoryEvaluation + ", phLevel=" + phLevel + ", giLevel=" + giLevel
				+ ", communities=" + communities + ", foodCultures=" + foodCultures + ", diseaseFriendly="
				+ diseaseFriendly + ", isPrebiotic=" + isPrebiotic + ", isProBiotic=" + isProBiotic + ", cookingMethod="
				+ cookingMethod + ", medicineDosage=" + medicineDosage + ", foodPreparationTemperature="
				+ foodPreparationTemperature + ", foodGroups=" + foodGroups + ", nutrientTypes=" + nutrientTypes
				+ ", nutrientGoals=" + nutrientGoals + ", diseases=" + diseases + "]";
	}
}
