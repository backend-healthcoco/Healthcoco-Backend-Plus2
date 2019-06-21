package com.dpdocter.beans;

import com.dpdocter.beans.v2.Duration;
import com.dpdocter.collections.GenericCollection;

public class BirthAchievement extends GenericCollection {

	private String id;
	private String patientId;
	private String doctorId;
	private String achievement;
	private Long achievementDate;
	private Duration duration;
	private String note;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "BirthAchievement [id=" + id + ", patientId=" + patientId + ", achievement=" + achievement
				+ ", achievementDate=" + achievementDate + ", duration=" + duration + ", note=" + note + "]";
	}

}
