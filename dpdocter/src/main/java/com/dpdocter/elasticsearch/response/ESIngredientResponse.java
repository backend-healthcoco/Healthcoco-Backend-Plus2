package com.dpdocter.elasticsearch.response;

import java.util.List;
import java.util.Map;

import com.dpdocter.beans.EquivalentQuantities;
import com.dpdocter.beans.MealQuantity;

public class ESIngredientResponse {

	private String id;

	private String name;

	private MealQuantity quantity;

	private String note;

	private MealQuantity calories;

	private List<EquivalentQuantities> equivalentMeasurements;

	private MealQuantity fat;

	private MealQuantity protein;

	private MealQuantity carbohydreate;

	private MealQuantity fiber;

	private Map<String, String> generalNutrients;

	private Map<String, String> carbNutrients;

	private Map<String, String> lipidNutrients;

	private Map<String, String> proteinAminoAcidNutrients;

	private Map<String, String> mineralNutrients;

	private Map<String, String> otherNutrients;
	
	private Map<String, String> vitaminNutrients;

	public MealQuantity getCalories() {
		return calories;
	}

	public void setCalories(MealQuantity calories) {
		this.calories = calories;
	}

	public List<EquivalentQuantities> getEquivalentMeasurements() {
		return equivalentMeasurements;
	}

	public void setEquivalentMeasurements(List<EquivalentQuantities> equivalentMeasurements) {
		this.equivalentMeasurements = equivalentMeasurements;
	}

	public MealQuantity getQuantity() {
		return quantity;
	}

	public void setQuantity(MealQuantity quantity) {
		this.quantity = quantity;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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

	public Map<String, String> getVitaminNutrients() {
		return vitaminNutrients;
	}

	public void setVitaminNutrients(Map<String, String> vitaminNutrients) {
		this.vitaminNutrients = vitaminNutrients;
	}
	
	

}
