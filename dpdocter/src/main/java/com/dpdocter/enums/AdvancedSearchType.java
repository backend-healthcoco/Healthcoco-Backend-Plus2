package com.dpdocter.enums;

public enum AdvancedSearchType {
    FIRST_NAME("firstName"), MIDDLE_NAME("middleName"), LAST_NAME("lastName"), PID("PID"), MOBILE_NUMBER("mobileNumber"), EMAIL_ADDRESS(
	    "emailAddress"), USERNAME("userName"), CITY("city"), LOCALITY("locality"), BLOOD_GROUP("bloodGroup"), REFERRED_BY("referredBy"), PROFESSION(
		    "profession"), POSTAL_CODE("postalCode"), GENDER("gender"), REGISTRATION_DATE("registrationDate"), DOB("dob"), CREATED_TIME("createdTime"),
    LOCAL_PATIENT_NAME("localPatientName"), PNUM("PNUM");

    private String searchType;

    AdvancedSearchType(String searchType) {
	this.searchType = searchType;
    }

    public String getSearchType() {
	return searchType;
    }

}
