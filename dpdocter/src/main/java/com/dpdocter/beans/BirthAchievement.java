package com.dpdocter.beans;

public class BirthAchievement {

	private String achievement;
	private Long achievementDate;

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

	@Override
	public String toString() {
		return "BirthAchievement [achievement=" + achievement + ", achievementDate=" + achievementDate + "]";
	}
	
	

}
