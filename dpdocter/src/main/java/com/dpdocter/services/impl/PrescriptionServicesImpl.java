package com.dpdocter.services.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.TemplateCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.TemplateRepository;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDeleteRequest;
import com.dpdocter.request.TemplateAddEditRequest;
import com.dpdocter.request.TemplateDeleteRequest;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.response.TemplateAddEditResponse;
import com.dpdocter.services.PrescriptionServices;

@Service
public class PrescriptionServicesImpl implements PrescriptionServices {
	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private TemplateRepository templateRepository;

	public DrugAddEditResponse addDrug(DrugAddEditRequest request) {
		DrugAddEditResponse response = null;
		DrugCollection drugCollection = new DrugCollection();
		BeanUtil.map(request, drugCollection);
		UUID drugCode = UUID.randomUUID();
		drugCollection.setDrugCode(drugCode.toString());
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
						drugCollection.setIsDeleted(true);
						drugCollection = drugRepository.save(drugCollection);
						response = true;
					} else {
						throw new BusinessException(ServiceError.NotAuthorized, "Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Drug");
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

	public TemplateAddEditResponse addTemplate(TemplateAddEditRequest request) {
		TemplateAddEditResponse response = null;
		TemplateCollection templateCollection = new TemplateCollection();
		BeanUtil.map(request, templateCollection);
		try {
			templateCollection = templateRepository.save(templateCollection);
			response = new TemplateAddEditResponse();
			BeanUtil.map(templateCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Template");
		}
		return response;
	}

	public TemplateAddEditResponse editTemplate(TemplateAddEditRequest request) {
		TemplateAddEditResponse response = null;
		TemplateCollection templateCollection = new TemplateCollection();
		BeanUtil.map(request, templateCollection);
		try {
			templateCollection = templateRepository.save(templateCollection);
			response = new TemplateAddEditResponse();
			BeanUtil.map(templateCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Template");
		}
		return response;
	}

	public Boolean deleteTemplate(TemplateDeleteRequest request) {
		Boolean response = false;
		TemplateCollection templateCollection = null;
		try {
			templateCollection = templateRepository.findOne(request.getId());
			if (templateCollection != null) {
				if (templateCollection.getDoctorId() != null && templateCollection.getHospitalId() != null && templateCollection.getLocationId() != null) {
					if (templateCollection.getDoctorId().equals(request.getDoctorId()) && templateCollection.getHospitalId().equals(request.getHospitalId())
							&& templateCollection.getLocationId().equals(request.getLocationId())) {
						templateCollection.setIsDeleted(true);
						templateCollection = templateRepository.save(templateCollection);
						response = true;
					} else {
						throw new BusinessException(ServiceError.NotAuthorized, "Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Template");
				}
			} else {
				throw new BusinessException(ServiceError.NotFound, "Template Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug");
		}
		return response;
	}

}
