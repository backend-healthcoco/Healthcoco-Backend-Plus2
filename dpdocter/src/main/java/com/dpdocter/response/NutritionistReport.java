package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.AcademicProfile;
import com.dpdocter.beans.FoodCommunity;
import com.dpdocter.beans.NutritionDisease;
import com.dpdocter.enums.LifeStyleType;

public class NutritionistReport {

	private String id;
	
	private long timeTaken;
	
	private String cloneTemplateId;
	
	private String cloneTemplateName; 
	
	private AcademicProfile profile;
	
	private String bmiClassification;
	
	private Double bmi;
	
	private LifeStyleType type;
	
	private List<FoodCommunity> communities;
	
	private List<NutritionDisease> diseases;
	
	private String foodPreference;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(long timeTaken) {
		this.timeTaken = timeTaken;
	}

	public String getCloneTemplateId() {
		return cloneTemplateId;
	}

	public void setCloneTemplateId(String cloneTemplateId) {
		this.cloneTemplateId = cloneTemplateId;
	}

	public String getCloneTemplateName() {
		return cloneTemplateName;
	}

	public void setCloneTemplateName(String cloneTemplateName) {
		this.cloneTemplateName = cloneTemplateName;
	}

	public AcademicProfile getProfile() {
		return profile;
	}

	public void setProfile(AcademicProfile profile) {
		this.profile = profile;
	}

	public String getBmiClassification() {
		return bmiClassification;
	}

	public void setBmiClassification(String bmiClassification) {
		this.bmiClassification = bmiClassification;
	}

	public Double getBmi() {
		return bmi;
	}

	public void setBmi(Double bmi) {
		this.bmi = bmi;
	}

	public LifeStyleType getType() {
		return type;
	}

	public void setType(LifeStyleType type) {
		this.type = type;
	}

	public List<FoodCommunity> getCommunities() {
		return communities;
	}

	public void setCommunities(List<FoodCommunity> communities) {
		this.communities = communities;
	}

	public List<NutritionDisease> getDiseases() {
		return diseases;
	}

	public void setDiseases(List<NutritionDisease> diseases) {
		this.diseases = diseases;
	}

	public String getFoodPreference() {
		return foodPreference;
	}

	public void setFoodPreference(String foodPreference) {
		this.foodPreference = foodPreference;
	}

	@Override
	public String toString() {
		return "NutritionistReport [id=" + id + ", timeTaken=" + timeTaken + ", cloneTemplateId=" + cloneTemplateId
				+ ", cloneTemplateName=" + cloneTemplateName + ", profile=" + profile + ", bmiClassification="
				+ bmiClassification + ", bmi=" + bmi + ", type=" + type + ", communities=" + communities + ", diseases="
				+ diseases + ", foodPreference=" + foodPreference + "]";
	}
}
