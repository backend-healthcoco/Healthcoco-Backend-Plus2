package com.dpdocter.enums;

public enum AchievementType {
	AWARD("AWARD"), PUBLICATION("PUBLICATION"), OTHER("OTHER");

	private String achievementType;

	AchievementType(String achievementType) {
		this.achievementType = achievementType;
	}

	public String getAchievementType() {
		return achievementType;
	}

}
