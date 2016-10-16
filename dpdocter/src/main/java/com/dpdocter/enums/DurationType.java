package com.dpdocter.enums;

public enum DurationType {
	HOURS("hours"),DAYs("days"),WEEKS("weeks"),MONTHS("months"),YEARS("years");
	private String duration;

	public String getDuration() {
		return duration;
	}

	private DurationType(String duration) {
		this.duration = duration;
	}
	

}
