package com.dpdocter.collections;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Age;
import com.dpdocter.enums.RDACategory;
import com.dpdocter.enums.RDAGroup;
import com.dpdocter.enums.RDAPregnancyCategory;

@Document(collection = "nutriention_rda_cl")
public class NutritionRDACollection extends GenericCollection{

	@Id
	private ObjectId id;

	@Field
	private ObjectId countryId;
	
	@Field
	private String country;

	@Field
	private Age fromAge;
	
	@Field
	private Age toAge;
	
	@Field
	private RDAGroup group;
	
	@Field
	private RDACategory category;
	
	@Field
	private RDAPregnancyCategory pregnancyCategory;
	
	@Field
	private Map<String, String> generalNutrients;

	@Field
	private Map<String, String> carbNutrients;

	@Field
	private Map<String, String> lipidNutrients;

	@Field
	private Map<String, String> proteinAminoAcidNutrients;

	@Field
	private Map<String, String> vitaminNutrients;

	@Field
	private Map<String, String> mineralNutrients;

	@Field
	private Map<String, String> otherNutrients;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getCountryId() {
		return countryId;
	}

	public void setCountryId(ObjectId countryId) {
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

	public Age getFromAge() {
		return fromAge;
	}

	public void setFromAge(Age fromAge) {
		this.fromAge = fromAge;
	}

	public Age getToAge() {
		return toAge;
	}

	public void setToAge(Age toAge) {
		this.toAge = toAge;
	}

	public RDAPregnancyCategory getPregnancyCategory() {
		return pregnancyCategory;
	}

	public void setPregnancyCategory(RDAPregnancyCategory pregnancyCategory) {
		this.pregnancyCategory = pregnancyCategory;
	}

	@Override
	public String toString() {
		return "NutritionRDACollection [id=" + id + ", countryId=" + countryId + ", country=" + country + ", fromAge="
				+ fromAge + ", toAge=" + toAge + ", group=" + group + ", category=" + category + ", generalNutrients="
				+ generalNutrients + ", carbNutrients=" + carbNutrients + ", lipidNutrients=" + lipidNutrients
				+ ", proteinAminoAcidNutrients=" + proteinAminoAcidNutrients + ", vitaminNutrients=" + vitaminNutrients
				+ ", mineralNutrients=" + mineralNutrients + ", otherNutrients=" + otherNutrients + "]";
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
