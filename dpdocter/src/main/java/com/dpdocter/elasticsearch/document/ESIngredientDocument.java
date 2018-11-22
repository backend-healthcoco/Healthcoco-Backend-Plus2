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
import com.dpdocter.enums.LevelType;

@Document(indexName = "ingredients_in", type = "ingredients")
public class ESIngredientDocument {
	@Id
	private String id;

	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;

	@Field(type = FieldType.Nested)
	private MealQuantity quantity;

	@Field(type = FieldType.Nested)
	private List<EquivalentQuantities> equivalentMeasurements;

	@Field(type = FieldType.String)
	private String name;

	@Field(type = FieldType.String)
	private String locationId;

	@Field(type = FieldType.String)
	private String doctorId;

	@Field(type = FieldType.String)
	private String hospitalId;

	@Field(type = FieldType.Double)
	private Double cost = 0.0;

	@Field(type = FieldType.String)
	private LevelType costType;

	@Field(type = FieldType.String)
	private String note;

	@Field(type = FieldType.Date)
	private Date updatedTime = new Date();

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
	private List<IngredientAddItem> vitaminNutrients;

	@Field(type = FieldType.Nested)
	private List<IngredientAddItem> proteinAminoAcidNutrients;

	@Field(type = FieldType.Nested)
	private List<IngredientAddItem> mineralNutrients;

	@Field(type = FieldType.Nested)
	private List<IngredientAddItem> otherNutrients;

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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
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

	public MealQuantity getFiber() {
		return fiber;
	}

	public void setFiber(MealQuantity fiber) {
		this.fiber = fiber;
	}

	public List<IngredientAddItem> getVitaminNutrients() {
		return vitaminNutrients;
	}

	public void setVitaminNutrients(List<IngredientAddItem> vitaminNutrients) {
		this.vitaminNutrients = vitaminNutrients;
	}
	

}
