package com.dpdocter.services;

import java.util.List;

import com.dpdocter.request.AddEditNutritionReferenceRequest;
import com.dpdocter.response.NutritionReferenceResponse;

public interface NutritionService {

	NutritionReferenceResponse addEditNutritionReference(AddEditNutritionReferenceRequest request);

	List<NutritionReferenceResponse> getNutritionReferenceList(String doctorId, String locationId, int page, int size);

}
