package com.dpdocter.collections;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Age;
import com.dpdocter.enums.LifeStyleType;

@Document(collection = "nutrition_rda_cl")
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
	private double fromAgeInYears;
	
	@Field
	private double toAgeInYears;
	
	@Field
	private String gender;
		
	@Field
	private LifeStyleType type;
	
	@Field
	private List<String> pregnancyCategory;
	
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public LifeStyleType getType() {
		return type;
	}

	public void setType(LifeStyleType type) {
		this.type = type;
	}

	public List<String> getPregnancyCategory() {
		return pregnancyCategory;
	}

	public void setPregnancyCategory(List<String> pregnancyCategory) {
		this.pregnancyCategory = pregnancyCategory;
	}

	public double getFromAgeInYears() {
		return fromAgeInYears;
	}

	public void setFromAgeInYears(double fromAgeInYears) {
		this.fromAgeInYears = fromAgeInYears;
	}

	public double getToAgeInYears() {
		return toAgeInYears;
	}

	public void setToAgeInYears(double toAgeInYears) {
		this.toAgeInYears = toAgeInYears;
	}

	@Override
	public String toString() {
		return "NutritionRDACollection [id=" + id + ", countryId=" + countryId + ", country=" + country + ", fromAge="
				+ fromAge + ", toAge=" + toAge + ", fromAgeInYears=" + fromAgeInYears + ", toAgeInYears=" + toAgeInYears
				+ ", gender=" + gender + ", type=" + type + ", pregnancyCategory=" + pregnancyCategory
				+ ", generalNutrients=" + generalNutrients + ", carbNutrients=" + carbNutrients + ", lipidNutrients="
				+ lipidNutrients + ", proteinAminoAcidNutrients=" + proteinAminoAcidNutrients + ", vitaminNutrients="
				+ vitaminNutrients + ", mineralNutrients=" + mineralNutrients + ", otherNutrients=" + otherNutrients
				+ "]";
	}
}
