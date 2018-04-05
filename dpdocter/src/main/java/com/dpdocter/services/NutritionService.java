package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.NutritionGoalAnalytics;
import com.dpdocter.request.AddEditNutritionReferenceRequest;
import com.dpdocter.response.NutritionReferenceResponse;

public interface NutritionService {

	NutritionReferenceResponse addEditNutritionReference(AddEditNutritionReferenceRequest request);

	List<NutritionReferenceResponse> getNutritionReferenceList(String doctorId, String locationId, String role,
			int page, int size);

	NutritionGoalAnalytics getGoalAnalytics(String doctorId, String locationId, String role, Long fromDate,
			Long toDate);

}
