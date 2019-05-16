package com.dpdocter.services;

import java.util.List;

import com.dpdocter.response.DoctorProfileBySlugUrlResponse;
import com.dpdocter.response.ResourcesCountResponse;
import com.dpdocter.response.SearchDoctorResponse;
import com.dpdocter.response.SearchLandmarkLocalityResponse;

public interface SearchService {

	SearchDoctorResponse searchDoctors(int page, int size, String city, String location, String latitude, String longitude,
			String speciality,
			String symptom, Boolean booking, Boolean calling, int minFee, int maxFee, int minTime, int maxTime,
			List<String> days, String gender, int minExperience, int maxExperience, String service, String locality, Boolean otherArea, String expertIn, String symptomDiseaseCondition);

	List<ResourcesCountResponse> getResourcesCountByCity(String city, List<String> type);

	List<SearchLandmarkLocalityResponse> getLandmarksAndLocalitiesByCity(String city, int page, int size, String searchTerm);

	DoctorProfileBySlugUrlResponse getDoctorProfileBySlugUrl(String userUId, String slugURL);

}
