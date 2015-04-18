package com.dpdocter.enums;

public enum DurationUnitEnum {
	DAY("DAY"), WEEK("WEEK"), MONTH("MONTH"),LIFE_TIME("LIFE_TIME");

	private String durationUnit;

	DurationUnitEnum(String durationUnit) {
		this.durationUnit = durationUnit;
	}

	public String getDurationUnit() {
		return durationUnit;
	}

}
