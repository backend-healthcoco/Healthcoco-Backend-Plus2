package com.dpdocter.enums;

public enum MealTimeEnum {
	EARLY_MORNING("EARLY_MORNING"), BREAKFAST("BREAKFAST"), MID_MORNING("MID_MORNING"), LUNCH("LUNCH"),
	EVENING_SNACK("EVENING_SNACK"), DINNER("DINNER"), MID_NIGHT("MID_NIGHT"), POST_LUNCH("POST_LUNCH"),
	POST_BREAKFAST("POST_BREAKFAST"), POST_DINNER("POST_DINNER"), POST_EVENING_SNACK("POST_EVENING_SNACK"),
	SNACK("SNACK"), ALL("ALL");

	private String time;

	public String getTime() {
		return time;
	}

	private MealTimeEnum(String time) {
		this.time = time;
	}

}
