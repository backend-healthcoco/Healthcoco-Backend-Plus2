package com.dpdocter.enums;

public enum LifeStyleType {

	//SEDENTARY("SEDENTARY"), MODERATE("MODERATE"), HEAVY("HEAVY");
	EXTREMELY_INACTIVE("EXTERMELY INACTIVE (<1.40)"), 
	SEDENTARY("SEDENTARY (1.40-1.69)"), 
	MODERATE("MODERATE (1.70-1.99)"),
	ACTIVE("ACTIVE (2.00-2.40)"), 
	EXTREMELY_ACTIVE("EXTREMELY_ACTIVE (>2.40)"), 
	PREGNANT("PREGNANT"), 
	LACTATION_6("LACTATION (<6 Months)"), 
	LACTATION_12("LACTATION (6-12 Months)"),
	TRIMESTER_1st("TRIMESTER_1st"), 
	TRIMESTER_2nd("TRIMESTER_2nd"), 
	TRIMESTER_3rd("TRIMESTER_3rd"), 
	POST_PARTUM("POST_PARTUM (0-6 Months)"),
	POST_PARTUM_6("POST_PARTUM (>6 Months)"); 
	private String type;

	public String getType() {
		return type;
	}

	private LifeStyleType(String type) {
		this.type = type;
	}

}
