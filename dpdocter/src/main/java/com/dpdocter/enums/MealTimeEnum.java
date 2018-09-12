package com.dpdocter.enums;

public enum MealTimeEnum {
	EARLY_MORNING("EARLY_MORNING"), BREAKFAST("BREAKFAST"), MID_MORNING("MID_MORNING"), LUNCH("LUNCH"), SNACK("SNACK"),
	DINNER("DINNER");

	private String time;

	public String getTime() {
		return time;
	}

	private MealTimeEnum(String time) {
		this.time = time;
	}

}
