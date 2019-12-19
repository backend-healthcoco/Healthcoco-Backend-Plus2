package com.dpdocter.enums;

public enum RDACategory {  //Unit - PAL (Physical Activity Level)
	
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
	
	private String value;

	public String getValue() {
		return value;
	}

	private RDACategory(String value) {
		this.value = value;
	}
//	SEDENTARY, // ("Sedentary"),
//	MODERATE, // ("Moderate"),
//	HEAVY, // ("Heavy"),
//	PREGNANT, // ("Pregnant"),
//	LACTATION_LESS_THAN_SIX_MONTHS, // ("Lact.<6months"),
//	LACTATION_SIX_TO_TEN_MONTHS, // ("Lact. 6-12 months"), 
//	ZERO_TO_SIX_MONTHS, 
//	SIX_TO_TWELVE_MONTHS, 
//	ONE_TO_THREE_YEARS, 
//	FOUR_TO_SIX_YEARS, 
//	SEVEN_TO_NINE_YEARS, 
//	TEN_TO_TWELVE_YEARS,
//	THIRTEEN_TO_FIFTEEN_YEARS,
//	SIXTEEN_TO_SEVENTEEN_YEARS;

}
