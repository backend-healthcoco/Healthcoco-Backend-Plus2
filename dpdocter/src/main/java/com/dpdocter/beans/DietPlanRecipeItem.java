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

	private MealQuantity calaries;

	private MealQuantity fat;

	private MealQuantity protein;

	private MealQuantity carbohydreate;

	private MealQuantity fiber;

	private List<IngredientAddItem> genralNutrients;

	private List<IngredientAddItem> carbNutrients;

	private List<IngredientAddItem> lipidNutrients;

	private List<IngredientAddItem> prooteinAminoAcidNutrients;

	private List<IngredientAddItem> mineralNutrients;

	private List<IngredientAddItem> otherNutrients;

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

	public List<IngredientAddItem> getGenralNutrients() {
		return genralNutrients;
	}

	public void setGenralNutrients(List<IngredientAddItem> genralNutrients) {
		this.genralNutrients = genralNutrients;
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

	public List<IngredientAddItem> getProoteinAminoAcidNutrients() {
		return prooteinAminoAcidNutrients;
	}

	public void setProoteinAminoAcidNutrients(List<IngredientAddItem> prooteinAminoAcidNutrients) {
		this.prooteinAminoAcidNutrients = prooteinAminoAcidNutrients;
	}

	public List<IngredientAddItem> getMineralNutrients() {
		return mineralNutrients;
	}

	public void setMineralNutrients(List<IngredientAddItem> mineralNutrients) {
		this.mineralNutrients = mineralNutrients;
	}

	public List<IngredientAddItem> getOtherNutrients() {
		return otherNutrients;
	}

	public void setOtherNutrients(List<IngredientAddItem> otherNutrients) {
		this.otherNutrients = otherNutrients;
	}

	public MealQuantity getCalaries() {
		return calaries;
	}

	public void setCalaries(MealQuantity calaries) {
		this.calaries = calaries;
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

}
