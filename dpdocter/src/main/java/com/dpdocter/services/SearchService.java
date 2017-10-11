package com.dpdocter.services;

import java.util.List;

import com.dpdocter.response.LabSearchResponse;

public interface SearchService {

	List<LabSearchResponse> searchLabsByTest(String city, String location, String latitude, String longitude, String searchTerm, List<String> testNames);

}
