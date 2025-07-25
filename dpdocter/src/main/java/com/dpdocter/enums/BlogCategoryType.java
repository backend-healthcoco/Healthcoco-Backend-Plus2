package com.dpdocter.enums;

public enum BlogCategoryType {
	MISCELLANEOUS("MISCELLANEOUS"), VETERINERY("VETERINERY"), WELLNESS_AND_AWARENESS(
			"WELLNESS_AND_AWARENESS"), AYURVEDA("AYURVEDA"), WEIGHT_LOSS("WEIGHT_LOSS"), SKIN_CARE(
					"SKIN_CARE"), PREGNANCY_AND_BABY_CARE("PREGNANCY_AND_BABY_CARE"), SEXUAL_HEALTH(
							"SEXUAL_HEALTH"), MENTAL_HEALTH(
									"MENTAL_HEALTH"), YOGA_AND_MEDEDITATION("YOGA_AND_MEDEDITATION"), FITNESS(
											"FITNESS"), NUTRITION("NUTRITION"), DISEASE_AND_CONDITIONS(
													"DISEASE_AND_CONDITIONS"), HEALTHCOCO("HEALTHCOCO");
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private BlogCategoryType(String type) {
		this.type = type;
	}

}
