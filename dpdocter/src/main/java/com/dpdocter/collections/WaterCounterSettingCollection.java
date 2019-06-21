package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.WaterIntakeEnum;

@Document(collection = "water_counter_setting_cl")
public class WaterCounterSettingCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private Boolean showCounter = true;
	@Field
	private Boolean showTip = false;
	@Field
	private Integer dailyGoal = 8;
	@Field
	private WaterIntakeEnum glassSize = WaterIntakeEnum.GLASS_250ML;
	@Field
	private ObjectId userId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Boolean getShowCounter() {
		return showCounter;
	}

	public void setShowCounter(Boolean showCounter) {
		this.showCounter = showCounter;
	}

	public Boolean getShowTip() {
		return showTip;
	}

	public void setShowTip(Boolean showTip) {
		this.showTip = showTip;
	}

	public Integer getDailyGoal() {
		return dailyGoal;
	}

	public void setDailyGoal(Integer dailyGoal) {
		this.dailyGoal = dailyGoal;
	}

	public WaterIntakeEnum getGlassSize() {
		return glassSize;
	}

	public void setGlassSize(WaterIntakeEnum glassSize) {
		this.glassSize = glassSize;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

}
