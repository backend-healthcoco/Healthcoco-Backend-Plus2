package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.EquivalentQuantities;
import com.dpdocter.beans.IngredientItem;
import com.dpdocter.beans.MealQuantity;
import com.dpdocter.beans.RecipeItem;
import com.dpdocter.enums.LevelType;

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
	private MealQuantity carbohydreate;

	@Field
	private MealQuantity fiber;

	@Field
	private List<IngredientItem> generalNutrients;

	@Field
	private List<IngredientItem> carbNutrients;

	@Field
	private List<IngredientItem> lipidNutrients;

	@Field
	private List<IngredientItem> proteinAminoAcidNutrients;

	@Field
	private List<IngredientItem> vitaminNutrients;

	@Field
	private List<IngredientItem> mineralNutrients;

	@Field
	private List<IngredientItem> otherNutrients;

	@Field
	private boolean nutrientValueAtRecipeLevel = false;

	@Field
	private List<ObjectId> planIds;

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

	public MealQuantity getCarbohydreate() {
		return carbohydreate;
	}

	public void setCarbohydreate(MealQuantity carbohydreate) {
		this.carbohydreate = carbohydreate;
	}

	public List<IngredientItem> getCarbNutrients() {
		return carbNutrients;
	}

	public void setCarbNutrients(List<IngredientItem> carbNutrients) {
		this.carbNutrients = carbNutrients;
	}

	public List<IngredientItem> getLipidNutrients() {
		return lipidNutrients;
	}

	public void setLipidNutrients(List<IngredientItem> lipidNutrients) {
		this.lipidNutrients = lipidNutrients;
	}

	public List<IngredientItem> getMineralNutrients() {
		return mineralNutrients;
	}

	public void setMineralNutrients(List<IngredientItem> mineralNutrients) {
		this.mineralNutrients = mineralNutrients;
	}

	public List<IngredientItem> getOtherNutrients() {
		return otherNutrients;
	}

	public void setOtherNutrients(List<IngredientItem> otherNutrients) {
		this.otherNutrients = otherNutrients;
	}

	public List<IngredientItem> getGeneralNutrients() {
		return generalNutrients;
	}

	public void setGeneralNutrients(List<IngredientItem> generalNutrients) {
		this.generalNutrients = generalNutrients;
	}

	public List<IngredientItem> getProteinAminoAcidNutrients() {
		return proteinAminoAcidNutrients;
	}

	public void setProteinAminoAcidNutrients(List<IngredientItem> proteinAminoAcidNutrients) {
		this.proteinAminoAcidNutrients = proteinAminoAcidNutrients;
	}

	public List<IngredientItem> getVitaminNutrients() {
		return vitaminNutrients;
	}

	public void setVitaminNutrients(List<IngredientItem> vitaminNutrients) {
		this.vitaminNutrients = vitaminNutrients;
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

	public String getRecipeDescription() {
		return recipeDescription;
	}

	public void setRecipeDescription(String recipeDescription) {
		this.recipeDescription = recipeDescription;
	}

}
