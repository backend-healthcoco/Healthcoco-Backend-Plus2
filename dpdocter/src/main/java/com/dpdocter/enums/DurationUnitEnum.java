package com.dpdocter.enums;

public enum DurationUnitEnum {
	DAY("DAY"), WEEK("WEEK"), MONTH("MONTH");

	private String durationUnit;

	DurationUnitEnum(String durationUnit) {
		this.durationUnit = durationUnit;
	}

	public String getDurationUnit() {
		return durationUnit;
	}

}
