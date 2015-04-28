package com.dpdocter.services;

import java.util.List;

import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.response.DiseaseAddEditResponse;

public interface HistoryServices {

	List<DiseaseAddEditResponse> addDiseases(List<DiseaseAddEditRequest> request);

	DiseaseAddEditResponse editDiseases(DiseaseAddEditRequest request);

	Boolean deleteDisease(String diseaseId, String doctorId, String hospitalId, String locationId);

}
