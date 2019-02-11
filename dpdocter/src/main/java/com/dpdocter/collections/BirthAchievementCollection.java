package com.dpdocter.collections;

import org.bson.types.ObjectId;

public class BirthAchievementCollection extends GenericCollection{

	private ObjectId id;
	private ObjectId patientId;
	private String achievement;
	private Long achievementDate;

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

	public String getAchievement() {
		return achievement;
	}

	public void setAchievement(String achievement) {
		this.achievement = achievement;
	}

	public Long getAchievementDate() {
		return achievementDate;
	}

	public void setAchievementDate(Long achievementDate) {
		this.achievementDate = achievementDate;
	}

}
