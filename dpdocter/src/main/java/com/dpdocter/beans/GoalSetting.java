package com.dpdocter.beans;

import java.util.Set;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.GenderType;

public class GoalSetting extends GenericCollection {

	private String id;
	private String patientId;
	private GenderType genderType;
	private String wellnessGoal;
	private String fitnessGoal;
	private Integer height;
	private Integer currentWeight;
	private Integer targetWeight;
	private Set<String> diseaseManagement;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getCurrentWeight() {
		return currentWeight;
	}

	public void setCurrentWeight(Integer currentWeight) {
		this.currentWeight = currentWeight;
	}

	public Integer getTargetWeight() {
		return targetWeight;
	}

	public void setTargetWeight(Integer targetWeight) {
		this.targetWeight = targetWeight;
	}

	public GenderType getGenderType() {
		return genderType;
	}

	public void setGenderType(GenderType genderType) {
		this.genderType = genderType;
	}

	public String getWellnessGoal() {
		return wellnessGoal;
	}

	public void setWellnessGoal(String wellnessGoal) {
		this.wellnessGoal = wellnessGoal;
	}

	public String getFitnessGoal() {
		return fitnessGoal;
	}

	public void setFitnessGoal(String fitnessGoal) {
		this.fitnessGoal = fitnessGoal;
	}

	public Set<String> getDiseaseManagement() {
		return diseaseManagement;
	}

	public void setDiseaseManagement(Set<String> diseaseManagement) {
		this.diseaseManagement = diseaseManagement;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

}
