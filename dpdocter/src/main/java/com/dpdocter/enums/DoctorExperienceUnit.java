package com.dpdocter.enums;

public enum DoctorExperienceUnit {
	MONTH("MONTH"), YEAR("YEAR");

	private String period;

	DoctorExperienceUnit(String period) {
		this.period = period;
	}

	public String getExperiencePeriod() {
		return period;
	}

}
