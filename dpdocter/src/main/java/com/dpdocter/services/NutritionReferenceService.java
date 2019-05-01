package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.NutritionGoalAnalytics;
import com.dpdocter.beans.NutritionReference;
import com.dpdocter.request.AddEditNutritionReferenceRequest;
import com.dpdocter.response.NutritionReferenceResponse;

public interface NutritionReferenceService {

	NutritionReferenceResponse addEditNutritionReference(AddEditNutritionReferenceRequest request);

	List<NutritionReference> getNutritionReferenceList(String doctorId, String locationId, int page, int size,
			String searchTerm, String updatedTime);

	NutritionGoalAnalytics getGoalAnalytics(String doctorId, String locationId, Long fromDate, Long toDate);

	public NutritionReferenceResponse getNutritionReferenceById(String id);

	public Boolean changeStatus(String id, String regularityStatus, String goalStatus);

}