package com.dpdocter.beans;

import com.dpdocter.enums.DoctorExperienceUnit;

public class DoctorExperience {
    private String experience;

    private DoctorExperienceUnit period;

    public String getExperience() {
	return experience;
    }

    public void setExperience(String experience) {
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
