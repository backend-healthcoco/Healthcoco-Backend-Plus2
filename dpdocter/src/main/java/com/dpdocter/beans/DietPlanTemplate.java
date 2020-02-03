package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.LifeStyleType;

public class DietPlanTemplate extends GenericCollection {

	private String id;

	private String uniquePlanId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Boolean discarded = false;

	private List<DietplanAddItem> items;

	private MealQuantity calories;
	
	private List<EquivalentQuantities> equivalentMeasurements;
	
	private String advice;
	
	private String country;

	private Age fromAge;
	
	private Age toAge;
	
	private double fromAgeInYears;
	
	private double toAgeInYears;
	
	private String gender;
		
	private LifeStyleType type;
	
	private List<String> pregnancyCategory;

	private List<FoodCommunity> communities;
	
	private String templateName;
	
	private String principle;
	
	public String getPrinciple() {
		return principle;
	}
	public void setPrinciple(String principle) {
		this.principle = principle;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUniquePlanId() {
		return uniquePlanId;
	}

	public void setUniquePlanId(String uniquePlanId) {
		this.uniquePlanId = uniquePlanId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
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

	public List<DietplanAddItem> getItems() {
		return items;
	}

	public void setItems(List<DietplanAddItem> items) {
		this.items = items;
	}

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

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public List<FoodCommunity> getCommunities() {
		return communities;
	}

	public void setCommunities(List<FoodCommunity> communities) {
		this.communities = communities;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	@Override
	public String toString() {
		return "DietPlanTemplate [id=" + id + ", uniquePlanId=" + uniquePlanId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", items="
				+ items + ", calories=" + calories + ", equivalentMeasurements=" + equivalentMeasurements + ", advice="
				+ advice + ", country=" + country + ", fromAge=" + fromAge + ", toAge=" + toAge + ", fromAgeInYears="
				+ fromAgeInYears + ", toAgeInYears=" + toAgeInYears + ", gender=" + gender + ", type=" + type
				+ ", pregnancyCategory=" + pregnancyCategory + ", communities=" + communities + ", templateName="
				+ templateName + "]";
	}

}
