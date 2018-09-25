package com.dpdocter.enums;

public enum MealTimeEnum {
	EARLY_MORNING("EARLY_MORNING"), BREAKFAST("BREAKFAST"), MID_MORNING("MID_MORNING"), LUNCH("LUNCH"), EVENING_SNACK("EVENING_SNACK"),
DINNER("DINNER"),MID_NIGHT("MID_NIGHT");

	private String time;

	public String getTime() {
		return time;
	}

	private MealTimeEnum(String time) {
		this.time = time;
	}

}
