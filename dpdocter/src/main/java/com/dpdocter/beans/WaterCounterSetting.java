package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.WaterIntakeEnum;

public class WaterCounterSetting extends GenericCollection {

	private String id;

	private Boolean showCounter = true;

	private Boolean showTip = false;

	private Integer dailyGoal = 8;

	private WaterIntakeEnum glassSize = WaterIntakeEnum.GLASS_250ML;

	private String userId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
