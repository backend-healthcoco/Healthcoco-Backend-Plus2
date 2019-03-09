package com.dpdocter.enums;

public enum ProductType {
	DRUG("DRUG"), TREATMENT_SERVICE("TREATMENT_SERVICE"), NUTRITION_PLAN("NUTRITION_PLAN"),
	SUBSCRIPTION_NUTRITION_PLAN("SUBSCRIPTION_NUTRITION_PLAN");

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private ProductType(String type) {
		this.type = type;
	}

}
