package com.dpdocter.tests;

import java.util.Arrays;

import com.dpdocter.solr.beans.AdvancedSearch;
import com.dpdocter.solr.beans.AdvancedSearchParameter;
import com.dpdocter.solr.enums.AdvancedSearchType;

public class GeneralTests {
    public static void main(String args[]) {
	AdvancedSearch advancedSearch = new AdvancedSearch();

	advancedSearch.setDoctorId("5566220cb732a94e37e2d0ac");
	advancedSearch.setLocationId("555260322736b2b121087651");
	advancedSearch.setHospitalId("5525ef96e4b077dfc16836a0");

	AdvancedSearchParameter advancedSearchParameter = new AdvancedSearchParameter();

	advancedSearchParameter.setSearchType(AdvancedSearchType.FIRST_NAME);
	advancedSearchParameter.setSearchValue("isank");

	advancedSearch.setSearchParameters(Arrays.asList(advancedSearchParameter));

	System.out.println(Converter.ObjectToJSON(advancedSearch));
    }
}
