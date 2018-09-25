package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class Recipe extends GenericCollection {

	private String id;

	private MealQuantity quantity;

	private List<MealQuantity> equivalentMeasurements;

	private String name;

	private String videoUrl;

	private List<String> recipeImages;

	private List<RecipeAddItem> includeIngredients;

	private List<RecipeAddItem> excludeIngredients;

	private List<RecipeAddItem> ingredients;

	private List<IngredientAddItem> nutrients;

	private String dishType;

	private List<String> recipeTiming;

	private String technique;

	private Boolean isPopular = false;

	private Boolean isHoliday = false;

	private Boolean discarded = false;

	private String direction;

	private String dietaryConcerns;

	private Integer forMember = 0;

	private Double cost = 0.0;

	private String meal;

	private String cuisine;

	private String course;

	private Integer preparationTime = 0;

	private boolean verified = false;

	private double calaries;

	public double getCalaries() {
		return calaries;
	}

	public void setCalaries(double calaries) {
		this.calaries = calaries;
	}

	public boolean isVerified() {
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

	public String getCuisine() {
		return cuisine;
	}

	public void setCuisine(String cuisine) {
		this.cuisine = cuisine;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public Integer getPreparationTime() {
		return preparationTime;
	}

	public void setPreparationTime(Integer preparationTime) {
		this.preparationTime = preparationTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public List<IngredientAddItem> getNutrients() {
		return nutrients;
	}

	public void setNutrients(List<IngredientAddItem> nutrients) {
		this.nutrients = nutrients;
	}

	public MealQuantity getQuantity() {
		return quantity;
	}

	public void setQuantity(MealQuantity quantity) {
		this.quantity = quantity;
	}

	public List<MealQuantity> getEquivalentMeasurements() {
		return equivalentMeasurements;
	}

	public void setEquivalentMeasurements(List<MealQuantity> equivalentMeasurements) {
		this.equivalentMeasurements = equivalentMeasurements;
	}

	public List<String> getRecipeTiming() {
		return recipeTiming;
	}

	public void setRecipeTiming(List<String> recipeTiming) {
		this.recipeTiming = recipeTiming;
	}

}
