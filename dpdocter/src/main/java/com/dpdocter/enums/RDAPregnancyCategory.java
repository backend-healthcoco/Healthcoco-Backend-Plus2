package com.dpdocter.enums;

public enum RDAPregnancyCategory {

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

		private RDAPregnancyCategory(String value) {
			this.value = value;
		}
}
