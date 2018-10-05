package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class Ingredient extends GenericCollection {

	private String id;

	private MealQuantity quantity;
	
	private List<EquivalentQuantities> equivalentMeasurements;

	private String name;

	private String locationId;

	private String doctorId;

	private String hospitalId;

	private Boolean discarded = false;

	private String note;

	private MealQuantity calaries;
	
	private MealQuantity fat;

	private MealQuantity protein;

	private MealQuantity carbohydreate;

	private List<IngredientAddItem> genralNutrients;

	private List<IngredientAddItem> carbNutrients;

	private List<IngredientAddItem> lipidNutrients;

	private List<IngredientAddItem> prooteinAminoAcidNutrients;

	private List<IngredientAddItem> mineralNutrients;

	private List<IngredientAddItem> otherNutrients;

	public MealQuantity getCalaries() {
		return calaries;
	}

	public void setCalaries(MealQuantity calaries) {
		this.calaries = calaries;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
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



}
