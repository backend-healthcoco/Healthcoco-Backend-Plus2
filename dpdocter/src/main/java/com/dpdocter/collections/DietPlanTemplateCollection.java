package com.dpdocter.collections;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Age;
import com.dpdocter.beans.DietplanItem;
import com.dpdocter.beans.FoodCommunity;
import com.dpdocter.beans.MealQuantity;
import com.dpdocter.beans.NutritionDisease;
import com.dpdocter.enums.LifeStyleType;

@Document(collection = "diet_plan_template_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}") })
public class DietPlanTemplateCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String uniquePlanId;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;
	@Field
	private Boolean discarded = false;
	@Field
	private List<DietplanItem> items;
	@Field
	private MealQuantity calories;
	@Field
	private Boolean isPatientDiscarded = false;
	@Field
	private String advice;
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
	private List<FoodCommunity> communities;
	@Field
	private String templateName;
	@Field
	private String principle;
	@Field
	private String foodPreference;
	@Field
	private List<NutritionDisease> diseases;
	@Field
	private double bmiFrom;
	@Field
	private double bmiTo;
	@Field
	private Map<ObjectId, String> multilingualTemplateName;

	public Map<ObjectId, String> getMultilingualTemplateName() {
		return multilingualTemplateName;
	}

	public void setMultilingualTemplateName(Map<ObjectId, String> multilingualTemplateName) {
		this.multilingualTemplateName = multilingualTemplateName;
	}

	public String getPrinciple() {
		return principle;
	}

	public void setPrinciple(String principle) {
		this.principle = principle;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getUniquePlanId() {
		return uniquePlanId;
	}

	public void setUniquePlanId(String uniquePlanId) {
		this.uniquePlanId = uniquePlanId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<DietplanItem> getItems() {
		return items;
	}

	public void setItems(List<DietplanItem> items) {
		this.items = items;
	}

	public MealQuantity getCalories() {
		return calories;
	}

	public void setCalories(MealQuantity calories) {
		this.calories = calories;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
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

	public String getFoodPreference() {
		return foodPreference;
	}

	public void setFoodPreference(String foodPreference) {
		this.foodPreference = foodPreference;
	}

	public List<NutritionDisease> getDiseases() {
		return diseases;
	}

	public void setDiseases(List<NutritionDisease> diseases) {
		this.diseases = diseases;
	}

	public double getBmiFrom() {
		return bmiFrom;
	}

	public void setBmiFrom(double bmiFrom) {
		this.bmiFrom = bmiFrom;
	}

	public double getBmiTo() {
		return bmiTo;
	}

	public void setBmiTo(double bmiTo) {
		this.bmiTo = bmiTo;
	}

	@Override
	public String toString() {
		return "DietPlanTemplateCollection [id=" + id + ", uniquePlanId=" + uniquePlanId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId
				+ ", discarded=" + discarded + ", items=" + items + ", calories=" + calories + ", isPatientDiscarded="
				+ isPatientDiscarded + ", advice=" + advice + ", country=" + country + ", fromAge=" + fromAge
				+ ", toAge=" + toAge + ", fromAgeInYears=" + fromAgeInYears + ", toAgeInYears=" + toAgeInYears
				+ ", gender=" + gender + ", type=" + type + ", pregnancyCategory=" + pregnancyCategory
				+ ", communities=" + communities + ", templateName=" + templateName + ", principle=" + principle
				+ ", foodPreference=" + foodPreference + ", diseases=" + diseases + ", bmiFrom=" + bmiFrom + ", bmiTo="
				+ bmiTo + ", multilingualTemplateName=" + multilingualTemplateName + "]";
	}
}
