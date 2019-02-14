package com.dpdocter.beans;

import java.util.List;

import org.bson.types.ObjectId;

public class DietPlanRecipeItem {

	private ObjectId id;

	private MealQuantity quantity;

	private String name;

	private List<RecipeItem> ingredients;

	private String direction;

	private String note;

	private MealQuantity calories;

	private MealQuantity fat;

	private MealQuantity protein;

	private MealQuantity carbohydreate;

	private MealQuantity fiber;

	private List<IngredientItem> generalNutrients;

	private List<IngredientItem> carbNutrients;

	private List<IngredientItem> lipidNutrients;

	private List<IngredientItem> proteinAminoAcidNutrients;

	private List<IngredientItem> mineralNutrients;

	private List<IngredientItem> otherNutrients;
	
	private List<IngredientItem> vitaminNutrients;

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

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public MealQuantity getQuantity() {
		return quantity;
	}

	public void setQuantity(MealQuantity quantity) {
		this.quantity = quantity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RecipeItem> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<RecipeItem> ingredients) {
		this.ingredients = ingredients;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<IngredientItem> getGeneralNutrients() {
		return generalNutrients;
	}

	public void setGeneralNutrients(List<IngredientItem> generalNutrients) {
		this.generalNutrients = generalNutrients;
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

	public List<IngredientItem> getProteinAminoAcidNutrients() {
		return proteinAminoAcidNutrients;
	}

	public void setProteinAminoAcidNutrients(List<IngredientItem> proteinAminoAcidNutrients) {
		this.proteinAminoAcidNutrients = proteinAminoAcidNutrients;
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

	public List<IngredientItem> getVitaminNutrients() {
		return vitaminNutrients;
	}

	public void setVitaminNutrients(List<IngredientItem> vitaminNutrients) {
		this.vitaminNutrients = vitaminNutrients;
	}

}
