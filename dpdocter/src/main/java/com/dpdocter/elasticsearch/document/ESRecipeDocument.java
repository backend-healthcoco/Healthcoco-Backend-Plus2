package com.dpdocter.elasticsearch.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.dpdocter.beans.EquivalentQuantities;
import com.dpdocter.beans.IngredientAddItem;
import com.dpdocter.beans.MealQuantity;
import com.dpdocter.beans.RecipeAddItem;
import com.dpdocter.enums.LevelType;

@Document(indexName = "recipes_in", type = "recipes")
public class ESRecipeDocument {
	@Id
	private String id;

	@Field(type = FieldType.String)
	private String name;

	@Field(type = FieldType.String)
	private String recipeDescription;

	@Field(type = FieldType.Nested)
	private MealQuantity quantity;

	@Field(type = FieldType.Nested)
	private List<EquivalentQuantities> equivalentMeasurements;

	@Field(type = FieldType.String)
	private String videoUrl;

	@Field(type = FieldType.Nested)
	private List<String> recipeImages;

	@Field(type = FieldType.Nested)
	private List<RecipeAddItem> includeIngredients;

	@Field(type = FieldType.Nested)
	private List<RecipeAddItem> excludeIngredients;

	@Field(type = FieldType.String)
	private String locationId;

	@Field(type = FieldType.String)
	private String doctorId;

	@Field(type = FieldType.String)
	private String hospitalId;

	@Field(type = FieldType.Nested)
	private List<RecipeAddItem> ingredients;

	@Field(type = FieldType.String)
	private String dishType;

	@Field(type = FieldType.String)
	private String technique;

	@Field(type = FieldType.Boolean)
	private Boolean isPopular = false;

	@Field(type = FieldType.Boolean)
	private Boolean isHoliday = false;

	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;

	@Field(type = FieldType.String)
	private String direction;

	@Field(type = FieldType.String)
	private String dietaryConcerns;

	@Field(type = FieldType.Integer)
	private Integer forMember = 0;

	@Field(type = FieldType.Double)
	private Double cost = 0.0;

	@Field(type = FieldType.String)
	private LevelType costType;

	@Field(type = FieldType.String)
	private String meal;

	@Field(type = FieldType.String)
	private List<String> cuisine;

	@Field(type = FieldType.String)
	private List<String> course;

	@Field(type = FieldType.Integer)
	private Integer preparationTime = 0;

	@Field(type = FieldType.Boolean)
	private boolean verified = false;

	@Field(type = FieldType.Date)
	private Date updatedTime = new Date();

	@Field(type = FieldType.String)
	private String createdBy;

	@Field(type = FieldType.Nested)
	private List<String> mealTiming;

	@Field(type = FieldType.Nested)
	private MealQuantity calories;

	@Field(type = FieldType.Nested)
	private MealQuantity fat;

	@Field(type = FieldType.Nested)
	private MealQuantity protein;

	@Field(type = FieldType.Nested)
	private MealQuantity carbohydreate;

	@Field(type = FieldType.Nested)
	private MealQuantity fiber;

	@Field(type = FieldType.Nested)
	private List<IngredientAddItem> generalNutrients;

	@Field(type = FieldType.Nested)
	private List<IngredientAddItem> carbNutrients;

	@Field(type = FieldType.Nested)
	private List<IngredientAddItem> lipidNutrients;

	@Field(type = FieldType.Nested)
	private List<IngredientAddItem> proteinAminoAcidNutrients;

	@Field(type = FieldType.Nested)
	private List<IngredientAddItem> vitaminNutrients;

	@Field(type = FieldType.Nested)
	private List<IngredientAddItem> mineralNutrients;

	@Field(type = FieldType.Nested)
	private List<IngredientAddItem> otherNutrients;

	@Field(type = FieldType.Boolean)
	private boolean nutrientValueAtRecipeLevel = false;

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

	public MealQuantity getCarbohydreate() {
		return carbohydreate;
	}

	public void setCarbohydreate(MealQuantity carbohydreate) {
		this.carbohydreate = carbohydreate;
	}

	public List<IngredientAddItem> getCarbNutrients() {
		return carbNutrients;
	}

	public void setCarbNutrients(List<IngredientAddItem> carbNutrients) {
		this.carbNutrients = carbNutrients;
	}

	public List<IngredientAddItem> getLipidNutrients() {
		return lipidNutrients;
	}

	public void setLipidNutrients(List<IngredientAddItem> lipidNutrients) {
		this.lipidNutrients = lipidNutrients;
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

	public void setMineralNutrients(List<IngredientAddItem> mineralNutrients) {
		this.mineralNutrients = mineralNutrients;
	}

	public void setOtherNutrients(List<IngredientAddItem> otherNutrients) {
		this.otherNutrients = otherNutrients;
	}

	public List<IngredientAddItem> getGeneralNutrients() {
		return generalNutrients;
	}

	public void setGeneralNutrients(List<IngredientAddItem> generalNutrients) {
		this.generalNutrients = generalNutrients;
	}

	public List<IngredientAddItem> getProteinAminoAcidNutrients() {
		return proteinAminoAcidNutrients;
	}

	public void setProteinAminoAcidNutrients(List<IngredientAddItem> proteinAminoAcidNutrients) {
		this.proteinAminoAcidNutrients = proteinAminoAcidNutrients;
	}

	public List<IngredientAddItem> getMineralNutrients() {
		return mineralNutrients;
	}

	public List<IngredientAddItem> getOtherNutrients() {
		return otherNutrients;
	}

	public List<IngredientAddItem> getVitaminNutrients() {
		return vitaminNutrients;
	}

	public void setVitaminNutrients(List<IngredientAddItem> vitaminNutrients) {
		this.vitaminNutrients = vitaminNutrients;
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

	public String getRecipeDescription() {
		return recipeDescription;
	}

	public void setRecipeDescription(String recipeDescription) {
		this.recipeDescription = recipeDescription;
	}

}
