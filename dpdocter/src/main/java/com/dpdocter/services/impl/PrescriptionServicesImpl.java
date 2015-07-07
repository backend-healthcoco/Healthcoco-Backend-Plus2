package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Drug;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionItemDetail;
import com.dpdocter.beans.TemplateDrug;
import com.dpdocter.beans.TemplateItem;
import com.dpdocter.beans.TemplateItemDetail;
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
import com.dpdocter.request.PrescriptionAddEditRequest;
import com.dpdocter.request.TemplateAddEditRequest;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponseDetails;
import com.dpdocter.response.TemplateAddEditResponse;
import com.dpdocter.response.TemplateAddEditResponseDetails;
import com.dpdocter.response.TemplateGetResponse;
import com.dpdocter.services.PrescriptionServices;
import common.util.web.DPDoctorUtils;
import common.util.web.PrescriptionUtils;

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
			drugCollection.setCreatedTime(new Date());
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
			drugCollection.setCreatedTime(new Date());
			drugCollection = drugRepository.save(drugCollection);
			response = new DrugAddEditResponse();
			BeanUtil.map(drugCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug");
		}
		return response;
	}

	public Boolean deleteDrug(String drugId, String doctorId, String hospitalId, String locationId) {
		Boolean response = false;
		DrugCollection drugCollection = null;
		try {
			drugCollection = drugRepository.findOne(drugId);
			if (drugCollection != null) {
				if (drugCollection.getDoctorId() != null && drugCollection.getHospitalId() != null && drugCollection.getLocationId() != null) {
					if (drugCollection.getDoctorId().equals(doctorId) && drugCollection.getHospitalId().equals(hospitalId)
							&& drugCollection.getLocationId().equals(locationId)) {
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

	public Boolean deleteDrug(String drugId) {
		Boolean response = false;
		DrugCollection drugCollection = null;
		try {
			drugCollection = drugRepository.findOne(drugId);
			if (drugCollection != null) {
				drugCollection.setIsDeleted(true);
				drugCollection = drugRepository.save(drugCollection);
				response = true;
			} else {
				throw new BusinessException(ServiceError.NotFound, "Drug Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug");
		}
		return response;
	}

	public DrugAddEditResponse getDrugById(String drugId) {
		DrugAddEditResponse drugAddEditResponse = null;
		try {
			DrugCollection drugCollection = drugRepository.findOne(drugId);
			if (drugCollection != null) {
				drugAddEditResponse = new DrugAddEditResponse();
				BeanUtil.map(drugCollection, drugAddEditResponse);
			} else {
				throw new BusinessException(ServiceError.Unknown, "Drug not found. Please check Drug Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug");
		}
		return drugAddEditResponse;
	}

	public TemplateAddEditResponse addTemplate(TemplateAddEditRequest request) {
		TemplateAddEditResponse response = null;
		TemplateCollection templateCollection = new TemplateCollection();
		BeanUtil.map(request, templateCollection);
		try {
			templateCollection.setCreatedTime(new Date());
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
			templateCollection.setCreatedTime(new Date());
			templateCollection = templateRepository.save(templateCollection);
			response = new TemplateAddEditResponse();
			BeanUtil.map(templateCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Template");
		}
		return response;
	}

	public Boolean deleteTemplate(String templateId, String doctorId, String hospitalId, String locationId) {
		Boolean response = false;
		TemplateCollection templateCollection = null;
		try {
			templateCollection = templateRepository.findOne(templateId);
			if (templateCollection != null) {
				if (templateCollection.getDoctorId() != null && templateCollection.getHospitalId() != null && templateCollection.getLocationId() != null) {
					if (templateCollection.getDoctorId().equals(doctorId) && templateCollection.getHospitalId().equals(hospitalId)
							&& templateCollection.getLocationId().equals(locationId)) {
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

	public TemplateGetResponse getTemplate(String templateId, String doctorId, String hospitalId, String locationId) {
		TemplateGetResponse response = null;
		TemplateCollection templateCollection = new TemplateCollection();
		try {
			templateCollection = templateRepository.getTemplate(templateId, doctorId, hospitalId, locationId);
			if (templateCollection != null) {
				response = new TemplateGetResponse();
				BeanUtil.map(templateCollection, response);
				int i = 0;
				for (TemplateItem item : templateCollection.getItems()) {
					DrugCollection drugCollection = drugRepository.findOne(item.getDrugId());
					TemplateDrug drug = new TemplateDrug();
					BeanUtil.map(drugCollection, drug);
					response.getItems().get(i).setDrug(drug);
					i++;
				}
			} else {
				throw new BusinessException(ServiceError.NotFound, "Template Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Template");
		}
		return response;
	}

	public PrescriptionAddEditResponse addPrescription(PrescriptionAddEditRequest request) {
		PrescriptionAddEditResponse response = null;
		PrescriptionCollection prescriptionCollection = new PrescriptionCollection();
		BeanUtil.map(request, prescriptionCollection);
		try {
			prescriptionCollection.setCreatedTime(new Date());
			prescriptionCollection.setPrescriptionCode(PrescriptionUtils.generatePrescriptionCode());
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

	public Boolean deletePrescription(String prescriptionId, String doctorId, String hospitalId, String locationId, String patientId) {
		Boolean response = false;
		PrescriptionCollection prescriptionCollection = null;
		try {
			prescriptionCollection = prescriptionRepository.findOne(prescriptionId);
			if (prescriptionCollection != null) {
				if (prescriptionCollection.getDoctorId() != null && prescriptionCollection.getHospitalId() != null
						&& prescriptionCollection.getLocationId() != null && prescriptionCollection.getPatientId() != null) {
					if (prescriptionCollection.getDoctorId().equals(doctorId) && prescriptionCollection.getHospitalId().equals(hospitalId)
							&& prescriptionCollection.getLocationId().equals(locationId) && prescriptionCollection.getPatientId().equals(patientId)) {
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

	public List<Prescription> getPrescriptions(String doctorId, String hospitalId, String locationId, String patientId, String createdTime,
			boolean isOTPVerified) {
		List<PrescriptionCollection> prescriptionCollections = null;
		List<Prescription> prescriptions = null;
		try {
			if (StringUtils.isEmpty(createdTime)) {
				if (!isOTPVerified) {
					prescriptionCollections = prescriptionRepository.getPrescription(doctorId, hospitalId, locationId, patientId, false, new Sort(
							Sort.Direction.DESC, "createdTime"));
				} else {
					prescriptionCollections = prescriptionRepository.getPrescription(patientId, false, new Sort(Sort.Direction.DESC, "createdTime"));
				}
			} else {
				long createdTimestamp = Long.parseLong(createdTime);
				if (!isOTPVerified) {
					prescriptionCollections = prescriptionRepository.getPrescription(doctorId, hospitalId, locationId, patientId, new Date(createdTimestamp),
							false, new Sort(Sort.Direction.DESC, "createdTime"));
				} else {
					prescriptionCollections = prescriptionRepository.getPrescription(patientId, new Date(createdTimestamp), false, new Sort(
							Sort.Direction.DESC, "createdTime"));
				}
			}

			if (prescriptionCollections != null) {
				prescriptions = new ArrayList<Prescription>();
				for (PrescriptionCollection prescriptionCollection : prescriptionCollections) {
					if (prescriptionCollection.getItems() != null) {
						Prescription prescription = new Prescription();
						BeanUtil.map(prescriptionCollection, prescription);
						List<PrescriptionItemDetail> prescriptionItemDetailsList = new ArrayList<PrescriptionItemDetail>();
						for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
							PrescriptionItemDetail prescriptionItemDetails = new PrescriptionItemDetail();
							BeanUtil.map(prescriptionItem, prescriptionItemDetails);
							if (prescriptionItem.getDrugId() != null) {
								DrugCollection drugCollection = drugRepository.findOne(prescriptionItem.getDrugId());
								Drug drug = new Drug();
								BeanUtil.map(drugCollection, drug);
								prescriptionItemDetails.setDrug(drug);
							}
							prescriptionItemDetailsList.add(prescriptionItemDetails);
						}
						prescription.setItems(prescriptionItemDetailsList);
						prescriptions.add(prescription);
					}

				}
			} else {
				throw new BusinessException(ServiceError.NotFound, "Prescription Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription");
		}
		return prescriptions;
	}

	@SuppressWarnings("unchecked")
	public List<Prescription> getPrescriptionsByIds(List<String> prescriptionIds) {
		List<PrescriptionCollection> prescriptionCollections = null;
		List<Prescription> prescriptions = null;
		try {
			Iterable<PrescriptionCollection> prescriptionCollectionsIterable = prescriptionRepository.findAll(prescriptionIds);
			if (prescriptionCollectionsIterable != null) {
				prescriptionCollections = IteratorUtils.toList(prescriptionCollectionsIterable.iterator());
			}

			if (prescriptionCollections != null) {
				prescriptions = new ArrayList<Prescription>();
				for (PrescriptionCollection prescriptionCollection : prescriptionCollections) {
					if (prescriptionCollection.getItems() != null) {
						Prescription prescription = new Prescription();
						BeanUtil.map(prescriptionCollection, prescription);
						List<PrescriptionItemDetail> prescriptionItemDetailsList = new ArrayList<PrescriptionItemDetail>();
						for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
							PrescriptionItemDetail prescriptionItemDetails = new PrescriptionItemDetail();
							BeanUtil.map(prescriptionItem, prescriptionItemDetails);
							DrugCollection drugCollection = drugRepository.findOne(prescriptionItem.getDrugId());
							Drug drug = new Drug();
							BeanUtil.map(drugCollection, drug);
							prescriptionItemDetails.setDrug(drug);
							prescriptionItemDetailsList.add(prescriptionItemDetails);
						}
						prescription.setItems(prescriptionItemDetailsList);
						prescriptions.add(prescription);
					}

				}
			} else {
				throw new BusinessException(ServiceError.NotFound, "Prescription Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription");
		}
		return prescriptions;
	}

	@Override
	public List<TemplateGetResponse> getTemplates(String doctorId, String hospitalId, String locationId, String createdTime) {
		List<TemplateGetResponse> response = null;
		List<TemplateCollection> templateCollections = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(createdTime)) {
				if (hospitalId == null && locationId == null) {
					templateCollections = templateRepository.getTemplates(doctorId, new Sort(Sort.Direction.DESC, "createdTime"));
				} else {
					templateCollections = templateRepository.getTemplates(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"));
				}
			} else {
				long createdTimeStamp = Long.parseLong(createdTime);
				if (hospitalId == null && locationId == null) {
					templateCollections = templateRepository.getTemplates(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
				} else {
					templateCollections = templateRepository.getTemplates(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(
							Sort.Direction.DESC, "createdTime"));
				}
			}
			if (!templateCollections.isEmpty()) {
				response = new ArrayList<TemplateGetResponse>();
				for (TemplateCollection templateCollection : templateCollections) {
					TemplateGetResponse template = new TemplateGetResponse();
					BeanUtil.map(templateCollection, template);
					int i = 0;
					for (TemplateItem item : templateCollection.getItems()) {
						DrugCollection drugCollection = drugRepository.findOne(item.getDrugId());
						TemplateDrug drug = new TemplateDrug();
						BeanUtil.map(drugCollection, drug);
						template.getItems().get(i).setDrug(drug);
						i++;
					}
					response.add(template);
				}
			} else {
				throw new BusinessException(ServiceError.NotFound, "Template Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Template");
		}
		return response;
	}

	@Override
	public List<DrugAddEditResponse> getDrugs(String doctorId, String hospitalId, String locationId, String createdTime) {
		List<DrugAddEditResponse> response = null;
		List<DrugCollection> drugCollections = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(createdTime)) {
				if (hospitalId == null && locationId == null) {
					drugCollections = drugRepository.getDrugs(doctorId, new Sort(Sort.Direction.DESC, "createdTime"));
				} else {
					drugCollections = drugRepository.getDrugs(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"));
				}
			} else {
				long createdTimeStamp = Long.parseLong(createdTime);
				if (hospitalId == null && locationId == null) {
					drugCollections = drugRepository.getDrugs(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
				} else {
					drugCollections = drugRepository.getDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
							"createdTime"));
				}
			}
			if (!drugCollections.isEmpty()) {
				response = new ArrayList<DrugAddEditResponse>();
				BeanUtil.map(drugCollections, response);
			} else {
				throw new BusinessException(ServiceError.Unknown, "No Drugs Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	@Override
	public Integer getPrescriptionCount(String doctorId, String patientId, String locationId, String hospitalId) {
		Integer prescriptionCount = 0;
		try {
			prescriptionCount = prescriptionRepository.getPrescriptionCount(doctorId, patientId, hospitalId, locationId, false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription Count");
		}
		return prescriptionCount;
	}

	@Override
	public TemplateAddEditResponseDetails addTemplateHandheld(TemplateAddEditRequest request) {
		TemplateAddEditResponseDetails response = null;
		TemplateAddEditResponse template = addTemplate(request);
		if (template != null) {
			response = new TemplateAddEditResponseDetails();
			BeanUtil.map(template, response);
			List<TemplateItemDetail> templateItemDetails = new ArrayList<TemplateItemDetail>();
			for (TemplateItem templateItem : template.getItems()) {
				TemplateItemDetail templateItemDetail = new TemplateItemDetail();
				BeanUtil.map(templateItem, templateItemDetail);
				DrugCollection drugCollection = drugRepository.findOne(templateItem.getDrugId());
				Drug drug = new Drug();
				BeanUtil.map(drugCollection, drug);
				templateItemDetail.setDrug(drug);
				templateItemDetails.add(templateItemDetail);
			}
			response.setItems(templateItemDetails);
		}
		return response;
	}

	@Override
	public PrescriptionAddEditResponseDetails addPrescriptionHandheld(PrescriptionAddEditRequest request) {
		PrescriptionAddEditResponseDetails response = null;
		PrescriptionAddEditResponse prescription = addPrescription(request);
		if (prescription != null) {
			response = new PrescriptionAddEditResponseDetails();
			BeanUtil.map(prescription, response);
			List<PrescriptionItemDetail> prescriptionItemDetails = new ArrayList<PrescriptionItemDetail>();
			for (PrescriptionItem prescriptionItem : prescription.getItems()) {
				PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();
				BeanUtil.map(prescriptionItem, prescriptionItemDetail);
				DrugCollection drugCollection = drugRepository.findOne(prescriptionItem.getDrugId());
				Drug drug = new Drug();
				BeanUtil.map(drugCollection, drug);
				prescriptionItemDetail.setDrug(drug);
				prescriptionItemDetails.add(prescriptionItemDetail);
			}
			response.setItems(prescriptionItemDetails);
		}
		return response;
	}
}
