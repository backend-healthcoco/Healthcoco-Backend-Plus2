package com.dpdocter.enums;

public enum NutritionPlanType {
	WEIGHT_LOSS_PLAN("WEIGHT_LOSS_PLAN"), NUTRIENT_IMPROVEMENT("NUTRIENT_IMPROVEMENT"), STAY_HEALTHY("STAY_HEALTHY"),
	CONDITION_SPECIFIC("CONDITION_SPECIFIC"), SPECIAL_PLANS("SPECIAL_PLANS"), ALL("ALL");

	private String type;

	public String getType() {
		return type;
	}

	private NutritionPlanType(String type) {
		this.type = type;
	}

}
