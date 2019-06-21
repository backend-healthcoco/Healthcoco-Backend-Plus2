package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Duration;

@Document(collection = "birth_achievement_cl")
public class BirthAchievementCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId patientId;
	@Field
	private String achievement;
	@Field
	private Long achievementDate;
	@Field
	private Duration duration;
	@Field
	private String note;

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

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return "BirthAchievementCollection [id=" + id + ", patientId=" + patientId + ", achievement=" + achievement
				+ ", achievementDate=" + achievementDate + ", duration=" + duration + ", note=" + note + "]";
	}

}
