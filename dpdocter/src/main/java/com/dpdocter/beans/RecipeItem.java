package com.dpdocter.beans;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.enums.QuantityEnum;

public class RecipeItem {
	private ObjectId id;
	private String name;
	private double value;
	private QuantityEnum type;
	private List<EquivalentQuantities> equivalentMeasurements;
	private MealQuantity calaries;

	private List<IngredientItem> nutrients;

	private MealQuantity fat;

	private MealQuantity protein;

	private MealQuantity carbohydreate;

	private MealQuantity fiber;

	private List<IngredientItem> genralNutrients;

	private List<IngredientItem> carbNutrients;

	private List<IngredientItem> lipidNutrients;

	private List<IngredientItem> prooteinAminoAcidNutrients;

	private List<IngredientItem> mineralNutrients;

	private List<IngredientItem> otherNutrients;

	public MealQuantity getCalaries() {
		return calaries;
	}

	public void setCalaries(MealQuantity calaries) {
		this.calaries = calaries;
	}

	public List<IngredientItem> getNutrients() {
		return nutrients;
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

	public List<IngredientItem> getGenralNutrients() {
		return genralNutrients;
	}

	public void setGenralNutrients(List<IngredientItem> genralNutrients) {
		this.genralNutrients = genralNutrients;
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

	public List<IngredientItem> getProoteinAminoAcidNutrients() {
		return prooteinAminoAcidNutrients;
	}

	public void setProoteinAminoAcidNutrients(List<IngredientItem> prooteinAminoAcidNutrients) {
		this.prooteinAminoAcidNutrients = prooteinAminoAcidNutrients;
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

	public void setNutrients(List<IngredientItem> nutrients) {
		this.nutrients = nutrients;
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

	public QuantityEnum getType() {
		return type;
	}

	public void setType(QuantityEnum type) {
		this.type = type;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public List<EquivalentQuantities> getEquivalentMeasurements() {
		return equivalentMeasurements;
	}

	public void setEquivalentMeasurements(List<EquivalentQuantities> equivalentMeasurements) {
		this.equivalentMeasurements = equivalentMeasurements;
	}

	public MealQuantity getFiber() {
		return fiber;
	}

	public void setFiber(MealQuantity fiber) {
		this.fiber = fiber;
	}

	public void setOtherNutrients(List<IngredientItem> otherNutrients) {
		this.otherNutrients = otherNutrients;
	}

}
