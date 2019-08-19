package com.dpdocter.collections;

import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.GenderType;

@Document(collection = "goal_setting_cl")
public class GoalSettingCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId patientId;
	@Field
	private GenderType genderType;
	@Field
	private String wellnessGoal;
	@Field
	private String fitnessGoal;
	@Field
	private Integer height;
	@Field
	private Integer currentWeight;
	@Field
	private Integer targetWeight;
	@Field
	private Set<String> diseaseManagement;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
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

	@Override
	public String toString() {
		return "GoalSettingCollection [id=" + id + ", patientId=" + patientId + ", wellnessGoal=" + wellnessGoal
				+ ", fitnessGoal=" + fitnessGoal + ", height=" + height + ", currentWeight=" + currentWeight
				+ ", targetWeight=" + targetWeight + ", diseaseManagement=" + diseaseManagement + "]";
	}

}
