package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.Achievement;

public class DoctorAchievementAddEditRequest {
	private String doctorId;

	private List<Achievement> achievements;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public List<Achievement> getAchievements() {
		return achievements;
	}

	public void setAchievements(List<Achievement> achievements) {
		this.achievements = achievements;
	}

	@Override
	public String toString() {
		return "DoctorAchievementAddEditRequest [doctorId=" + doctorId + ", achievements=" + achievements + "]";
	}

}
