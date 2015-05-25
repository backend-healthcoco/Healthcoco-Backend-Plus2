package com.dpdocter.beans;

import com.dpdocter.enums.AchievementType;

public class Achievement {
	private String achievementName;
	private int year;
	private AchievementType achievementType;

	public String getAchievementName() {
		return achievementName;
	}

	public void setAchievementName(String achievementName) {
		this.achievementName = achievementName;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public AchievementType getAchievementType() {
		return achievementType;
	}

	public void setAchievementType(AchievementType achievementType) {
		this.achievementType = achievementType;
	}

	@Override
	public String toString() {
		return "Achievement [achievementName=" + achievementName + ", year=" + year + ", achievementType=" + achievementType + "]";
	}

}
