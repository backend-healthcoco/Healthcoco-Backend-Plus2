package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.TemplateCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.TemplateRepository;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDeleteRequest;
import com.dpdocter.request.PrescriptionAddEditRequest;
import com.dpdocter.request.PrescriptionDeleteRequest;
import com.dpdocter.request.PrescriptionGetRequest;
import com.dpdocter.request.TemplateAddEditRequest;
import com.dpdocter.request.TemplateDeleteRequest;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponse;
import com.dpdocter.response.PrescriptionGetResponse;
import com.dpdocter.response.TemplateAddEditResponse;
import com.dpdocter.services.PrescriptionServices;

@Service
public class PrescriptionServicesImpl implements PrescriptionServices {
	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private TemplateRepository templateRepository;

	@Autowired
	private PrescriptionRepository prescriptionRepository;

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
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Template");
		}
		return response;
	}

	public PrescriptionAddEditResponse addPrescription(PrescriptionAddEditRequest request) {
		PrescriptionAddEditResponse response = null;
		PrescriptionCollection prescriptionCollection = new PrescriptionCollection();
		BeanUtil.map(request, prescriptionCollection);
		try {
			prescriptionCollection.setCreatedTime(new Date());
			prescriptionCollection = prescriptionRepository.save(prescriptionCollection);
			response = new PrescriptionAddEditResponse();
			BeanUtil.map(prescriptionCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Prescription");
		}
		return response;
	}

	public PrescriptionAddEditResponse editPrescription(PrescriptionAddEditRequest request) {
		PrescriptionAddEditResponse response = null;
		PrescriptionCollection prescriptionCollection = new PrescriptionCollection();
		BeanUtil.map(request, prescriptionCollection);
		try {
			prescriptionCollection.setCreatedTime(new Date());
			prescriptionCollection = prescriptionRepository.save(prescriptionCollection);
			response = new PrescriptionAddEditResponse();
			BeanUtil.map(prescriptionCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Prescription");
		}
		return response;
	}

	public Boolean deletePrescription(PrescriptionDeleteRequest request) {
		Boolean response = false;
		PrescriptionCollection prescriptionCollection = null;
		try {
			prescriptionCollection = prescriptionRepository.findOne(request.getId());
			if (prescriptionCollection != null) {
				if (prescriptionCollection.getDoctorId() != null && prescriptionCollection.getHospitalId() != null
						&& prescriptionCollection.getLocationId() != null) {
					if (prescriptionCollection.getDoctorId().equals(request.getDoctorId())
							&& prescriptionCollection.getHospitalId().equals(request.getHospitalId())
							&& prescriptionCollection.getLocationId().equals(request.getLocationId())
							&& prescriptionCollection.getPatientId().equals(request.getPatientId())) {
						prescriptionCollection.setIsDeleted(true);
						prescriptionCollection = prescriptionRepository.save(prescriptionCollection);
						response = true;
					} else {
						throw new BusinessException(ServiceError.NotAuthorized, "Invalid Doctor Id, Hospital Id, Location Id, Or Patient Id");
					}
				} else {
					throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Prescription");
				}
			} else {
				throw new BusinessException(ServiceError.NotFound, "Prescription Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Prescription");
		}
		return response;
	}

	public List<PrescriptionGetResponse> getPrescription(PrescriptionGetRequest request) {
		List<PrescriptionGetResponse> response = null;
		List<PrescriptionCollection> prescriptionCollections = new ArrayList<PrescriptionCollection>();
		try {
			prescriptionCollections = prescriptionRepository.getPrescription(request.getDoctorId(), request.getHospitalId(), request.getLocationId(),
					request.getPatientId(), new Sort(Sort.Direction.DESC, "createdTime"));
			if (prescriptionCollections != null) {
				response = new ArrayList<PrescriptionGetResponse>();
				BeanUtil.map(prescriptionCollections, response);
			} else {
				throw new BusinessException(ServiceError.NotFound, "Prescription Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription");
		}
		return response;
	}

	@Override
	public DrugAddEditResponse getDrugById(String drugId) {
		DrugAddEditResponse drugAddEditResponse = null;
		try {
			DrugCollection drugCollection = drugRepository.findOne(drugId);
			if(drugCollection != null){
				drugAddEditResponse = new DrugAddEditResponse();
				BeanUtil.map(drugCollection, drugAddEditResponse);
			}else{
				throw new BusinessException(ServiceError.Unknown, "Drug not found.Please check Drug Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug");
		}
		return drugAddEditResponse;
	}

}
