package com.dpdocter.beans;

import com.dpdocter.enums.DoctorExperienceUnit;

public class DoctorExperience {
	private float experience;

	private DoctorExperienceUnit period;

	public float getExperience() {
		return experience;
	}

	public void setExperience(float experience) {
		this.experience = experience;
	}

	public DoctorExperienceUnit getPeriod() {
		return period;
	}

	public void setPeriod(DoctorExperienceUnit period) {
		this.period = period;
	}

	@Override
	public String toString() {
		return "DoctorExperience [experience=" + experience + ", period=" + period + "]";
	}

}
