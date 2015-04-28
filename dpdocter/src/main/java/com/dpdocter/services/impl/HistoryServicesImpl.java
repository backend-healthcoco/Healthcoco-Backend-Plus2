package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.DiseasesCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.HistoryRepository;
import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.services.HistoryServices;

@Service
public class HistoryServicesImpl implements HistoryServices {
	@Autowired
	private HistoryRepository historyRepository;

	public List<DiseaseAddEditResponse> addDiseases(List<DiseaseAddEditRequest> request) {
		List<DiseaseAddEditResponse> response = null;
		List<DiseasesCollection> diseases = new ArrayList<DiseasesCollection>();
		BeanUtil.map(request, diseases);
		try {
			diseases = historyRepository.save(diseases);
			response = new ArrayList<DiseaseAddEditResponse>();
			BeanUtil.map(diseases, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Disease(s)");
		}
		return response;
	}

	public DiseaseAddEditResponse editDiseases(DiseaseAddEditRequest request) {
		DiseaseAddEditResponse response = null;
		DiseasesCollection disease = new DiseasesCollection();
		BeanUtil.map(request, disease);
		try {
			disease = historyRepository.save(disease);
			response = new DiseaseAddEditResponse();
			BeanUtil.map(disease, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Disease");
		}
		return response;
	}

	public Boolean deleteDisease(String diseaseId, String doctorId, String hospitalId, String locationId) {
		Boolean response = false;
		DiseasesCollection disease = null;
		try {
			disease = historyRepository.findOne(diseaseId);
			if (disease != null) {
				if (disease.getDoctorId() != null && disease.getHospitalId() != null && disease.getLocationId() != null) {
					if (disease.getDoctorId().equals(doctorId) && disease.getHospitalId().equals(hospitalId)
							&& disease.getLocationId().equals(locationId)) {
						disease.setDeleted(true);
						disease = historyRepository.save(disease);
						response = true;
					} else {
						throw new BusinessException(ServiceError.NotAuthorized, "Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Disease");
				}
			} else {
				throw new BusinessException(ServiceError.NotFound, "Disease Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Disease");
		}
		return response;
	}

}
