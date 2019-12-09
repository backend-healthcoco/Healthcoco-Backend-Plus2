package com.dpdocter.beans;

import java.util.Map;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.RDACategory;
import com.dpdocter.enums.RDAGroup;

public class NutritionRDA extends GenericCollection{

	private String id;

	private String countryId;
	
	private String country;
	
	private RDAGroup group;
	
	private RDACategory category;
	
	private Map<String, String> generalNutrients;

	private Map<String, String> carbNutrients;

	private Map<String, String> lipidNutrients;

	private Map<String, String> proteinAminoAcidNutrients;

	private Map<String, String> vitaminNutrients;

	private Map<String, String> mineralNutrients;

	private Map<String, String> otherNutrients;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public RDAGroup getGroup() {
		return group;
	}

	public void setGroup(RDAGroup group) {
		this.group = group;
	}

	public RDACategory getCategory() {
		return category;
	}

	public void setCategory(RDACategory category) {
		this.category = category;
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

	@Override
	public String toString() {
		return "NutritionRDA [id=" + id + ", countryId=" + countryId + ", country=" + country + ", group=" + group
				+ ", category=" + category + ", generalNutrients=" + generalNutrients + ", carbNutrients="
				+ carbNutrients + ", lipidNutrients=" + lipidNutrients + ", proteinAminoAcidNutrients="
				+ proteinAminoAcidNutrients + ", vitaminNutrients=" + vitaminNutrients + ", mineralNutrients="
				+ mineralNutrients + ", otherNutrients=" + otherNutrients + "]";
	}

	
	
//	private Integer bodyWeight; //in kg
//	
//	@Field
//	private Integer energy; //Kcal/day
//	
//	@Field
//	private Integer proteins; //g/day
//	
//	@Field
//	private Integer visibleFat; // (g/day) 
//		
//	@Field
//	private Integer calcium; // (mgl/Day) 
//	
//	@Field
//	private Integer iron; // (mg/day)
//	
//	@Field
//	private Integer retinol; // (μg/day) 
//	
//	@Field
//	private Integer bCarotene; // (μg/day) 
//	
//	@Field
//	private Integer thiamin; // (mg/day);
//	
//	@Field
//	private Integer riboflavin; // (mg/day) 
//	
//	@Field
//	private Integer niacinEq; // (mg/day) 
//	
//	@Field
//	private Integer vitaminB6; // (mg/day)
//	
//	@Field
//	private Integer vitaminC; // (mg/day) 
//	
//	@Field
//	private Integer dietaryFolate; // (μg/day) 
//	
//	@Field
//	private Integer vitaminB12; // (μg/Day)
//	
//	@Field
//	private Integer zinc; // (mg/Day) 
//	
//	@Field
//	private Integer magnesium; // (mg/Day)

}
