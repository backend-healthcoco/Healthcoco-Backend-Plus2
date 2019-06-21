package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "master_baby_achievement_cl")
public class MasterBabyAchievementCollection {

	@Id
	private ObjectId id;
	@Field
	private String achievement;
	@Field
	private String note;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getAchievement() {
		return achievement;
	}

	public void setAchievement(String achievement) {
		this.achievement = achievement;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return "MasterBabyAchievementCollection [id=" + id + ", achievement=" + achievement + ", note=" + note + "]";
	}

}
