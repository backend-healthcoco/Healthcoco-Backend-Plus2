package com.dpdocter.enums;

public enum TimeUnit {
	MINS("MINS");

	private String timeUnit;

	TimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}

	public String getTimeUnit() {
		return timeUnit;
	}

}
