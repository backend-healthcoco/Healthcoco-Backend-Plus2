package com.dpdocter.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.DrugCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDeleteRequest;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.services.PrescriptionServices;

@Service
public class PrescriptionServicesImpl implements PrescriptionServices {
	@Autowired
	private DrugRepository drugRepository;

	public DrugAddEditResponse addDrug(DrugAddEditRequest request) {
		DrugAddEditResponse response = null;
		DrugCollection drugCollection = new DrugCollection();
		BeanUtil.map(request, drugCollection);
		try {
			drugCollection = drugRepository.save(drugCollection);
			response = new DrugAddEditResponse();
			BeanUtil.map(drugCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug");
		}
		return response;
	}

	public DrugAddEditResponse editDrug(DrugAddEditRequest request) {
		DrugAddEditResponse response = null;
		DrugCollection drugCollection = new DrugCollection();
		BeanUtil.map(request, drugCollection);
		try {
			drugCollection = drugRepository.save(drugCollection);
			response = new DrugAddEditResponse();
			BeanUtil.map(drugCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug");
		}
		return response;
	}

	public Boolean deleteDrug(DrugDeleteRequest request) {
		Boolean response = false;
		DrugCollection drugCollection = null;
		try {
			drugCollection = drugRepository.findOne(request.getId());
			if (drugCollection != null) {
				if (drugCollection.getDoctorId() != null && drugCollection.getHospitalId() != null && drugCollection.getLocationId() != null) {
					if (drugCollection.getDoctorId().equals(request.getDoctorId()) && drugCollection.getHospitalId().equals(request.getHospitalId())
							&& drugCollection.getLocationId().equals(request.getLocationId())) {
						drugCollection.setDeleted(true);
						drugCollection = drugRepository.save(drugCollection);
						response = true;
					} else {
						throw new BusinessException(ServiceError.NotAuthorized, "Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Complaint");
				}
			} else {
				throw new BusinessException(ServiceError.NotFound, "Drug Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug");
		}
		return response;
	}

}
