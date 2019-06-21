package com.dpdocter.enums;

public enum TimeUnit {
	SEC("SEC"), MINS("MINS"), HOURS("HOURS"), DAY("DAY"), NIGHT("NIGHT");

	private String timeUnit;

	TimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}

	public String getTimeUnit() {
		return timeUnit;
	}

}
