package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.enums.DoctorExperienceUnit;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DoctorExperience {
	private int experience;

	private DoctorExperienceUnit period;

	public DoctorExperience() {
	}

	public DoctorExperience(int experience, DoctorExperienceUnit period) {
		this.experience = experience;
		this.period = period;
	}

	public Integer getExperience() {
		return experience;
	}

	public void setExperience(Integer experience) {
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
		return "{experience=" + experience + ", period=" + period + "}";
	}

}
