package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Drug;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionItemDetail;
import com.dpdocter.beans.TemplateItem;
import com.dpdocter.beans.TemplateItemDetail;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.DrugDirectionCollection;
import com.dpdocter.collections.DrugDosageCollection;
import com.dpdocter.collections.DrugDurationUnitCollection;
import com.dpdocter.collections.DrugStrengthUnitCollection;
import com.dpdocter.collections.DrugTypeCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.TemplateCollection;
import com.dpdocter.enums.PrescriptionItems;
import com.dpdocter.enums.Range;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DrugDirectionRepository;
import com.dpdocter.repository.DrugDosageRepository;
import com.dpdocter.repository.DrugDurationUnitRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.DrugStrengthUnitRepository;
import com.dpdocter.repository.DrugTypeRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.TemplateRepository;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDirectionAddEditRequest;
import com.dpdocter.request.DrugDosageAddEditRequest;
import com.dpdocter.request.DrugDurationUnitAddEditRequest;
import com.dpdocter.request.DrugStrengthAddEditRequest;
import com.dpdocter.request.DrugTypeAddEditRequest;
import com.dpdocter.request.PrescriptionAddEditRequest;
import com.dpdocter.request.TemplateAddEditRequest;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.response.DrugDirectionAddEditResponse;
import com.dpdocter.response.DrugDosageAddEditResponse;
import com.dpdocter.response.DrugDurationUnitAddEditResponse;
import com.dpdocter.response.DrugStrengthAddEditResponse;
import com.dpdocter.response.DrugTypeAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponseDetails;
import com.dpdocter.response.TemplateAddEditResponse;
import com.dpdocter.response.TemplateAddEditResponseDetails;
import com.dpdocter.services.PrescriptionServices;

import common.util.web.DPDoctorUtils;
import common.util.web.PrescriptionUtils;

@Service
public class PrescriptionServicesImpl implements PrescriptionServices {
    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private DrugDirectionRepository drugDirectionRepository;

    @Autowired
    private DrugStrengthUnitRepository drugStrengthRepository;

    @Autowired
    private DrugTypeRepository drugTypeRepository;

    @Autowired
    private DrugDurationUnitRepository drugDurationUnitRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private DrugDosageRepository drugDosageRepository;

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public TemplateAddEditResponseDetails getTemplate(String templateId, String doctorId, String hospitalId, String locationId) {
	TemplateAddEditResponseDetails response = null;
	TemplateCollection templateCollection = new TemplateCollection();
	try {
	    templateCollection = templateRepository.getTemplate(templateId, doctorId, hospitalId, locationId);
	    if (templateCollection != null) {
		response = new TemplateAddEditResponseDetails();
		BeanUtil.map(templateCollection, response);
		int i = 0;
		for (TemplateItem item : templateCollection.getItems()) {
		    DrugCollection drugCollection = drugRepository.findOne(item.getDrugId());
		    Drug drug = new Drug();
		    if (drugCollection != null)
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

    @Override
    public PrescriptionAddEditResponse addPrescription(PrescriptionAddEditRequest request) {
	PrescriptionAddEditResponse response = null;
	PrescriptionCollection prescriptionCollection = new PrescriptionCollection();
	BeanUtil.map(request, prescriptionCollection);
	try {
	    prescriptionCollection.setCreatedTime(new Date());
	    prescriptionCollection.setCreatedDate(prescriptionCollection.getCreatedTime().getTime());
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

    @Override
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

    @Override
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

    @Override
    public List<Prescription> getPrescriptions(int page, int size, String doctorId, String hospitalId, String locationId, String patientId, String createdTime,
	    boolean isOTPVerified, boolean isDeleted) {
	List<PrescriptionCollection> prescriptionCollections = null;
	List<Prescription> prescriptions = null;
	try {
	    if (StringUtils.isEmpty(createdTime)) {
		if (!isOTPVerified) {
		    if (isDeleted)
			prescriptionCollections = prescriptionRepository.getPrescription(doctorId, hospitalId, locationId, patientId, new Sort(Sort.Direction.DESC, "createdDate"), size>0 ? new PageRequest(page, size):null);
		    else
			prescriptionCollections = prescriptionRepository.getPrescription(doctorId, hospitalId, locationId, patientId, isDeleted, new Sort(Sort.Direction.DESC, "createdDate"), size>0 ? new PageRequest(page, size):null);
		} else {
		    if (isDeleted)
			prescriptionCollections = prescriptionRepository.getPrescription(patientId, new Sort(Sort.Direction.DESC, "createdDate"), size>0 ? new PageRequest(page, size):null);
		    else
			prescriptionCollections = prescriptionRepository.getPrescription(patientId, false, new Sort(Sort.Direction.DESC, "createdDate"), size>0 ? new PageRequest(page, size):null);
		}
	    } else {
		long createdTimestamp = Long.parseLong(createdTime);
		if (!isOTPVerified) {
		    if (isDeleted)
			prescriptionCollections = prescriptionRepository.getPrescription(doctorId, hospitalId, locationId, patientId, new Date(createdTimestamp), new Sort(Sort.Direction.DESC, "createdDate"), size>0 ? new PageRequest(page, size):null);
		    else
			prescriptionCollections = prescriptionRepository.getPrescription(doctorId, hospitalId, locationId, patientId, new Date(createdTimestamp), isDeleted, new Sort(Sort.Direction.DESC, "createdDate"), size>0 ? new PageRequest(page, size):null);
		} else {
		    if (isDeleted)
			prescriptionCollections = prescriptionRepository.getPrescription(patientId, new Date(createdTimestamp), new Sort(Sort.Direction.DESC,"createdDate"), size>0 ? new PageRequest(page, size):null);
		    else
			prescriptionCollections = prescriptionRepository.getPrescription(patientId, new Date(createdTimestamp), isDeleted, new Sort(Sort.Direction.DESC, "createdDate"), size>0 ? new PageRequest(page, size):null);
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
				if (drugCollection != null)
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

    @Override
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
    public List<TemplateAddEditResponseDetails> getTemplates(int page, int size, String doctorId, String hospitalId, String locationId, String createdTime, boolean isDeleted) {
	List<TemplateAddEditResponseDetails> response = null;
	List<TemplateCollection> templateCollections = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
		if (hospitalId == null && locationId == null) {
		    if (isDeleted)
			templateCollections = templateRepository.getTemplates(doctorId, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
		    else
			templateCollections = templateRepository.getTemplates(doctorId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
		} else {
		    if (isDeleted)
			templateCollections = templateRepository.getTemplates(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
		    else
			templateCollections = templateRepository.getTemplates(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC,"createdTime"), size>0 ? new PageRequest(page, size):null);
		}
	    } else {
		long createdTimeStamp = Long.parseLong(createdTime);
		if (hospitalId == null && locationId == null) {
		    if (isDeleted)
			templateCollections = templateRepository.getTemplates(doctorId, new Date(createdTimeStamp),new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
		    else
			templateCollections = templateRepository.getTemplates(doctorId, new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC,"createdTime"), size>0 ? new PageRequest(page, size):null);
		} else {
		    if (isDeleted)
			templateCollections = templateRepository.getTemplates(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
		    else
			templateCollections = templateRepository.getTemplates(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
		}
	    }
	    if (!templateCollections.isEmpty()) {
		response = new ArrayList<TemplateAddEditResponseDetails>();
		for (TemplateCollection templateCollection : templateCollections) {
		    TemplateAddEditResponseDetails template = new TemplateAddEditResponseDetails();
		    BeanUtil.map(templateCollection, template);
		    int i = 0;
		    for (TemplateItem item : templateCollection.getItems()) {
			DrugCollection drugCollection = drugRepository.findOne(item.getDrugId());
			Drug drug = new Drug();
			if (drugCollection != null)
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
		if (drugCollection != null)
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
		if (drugCollection != null)
		    BeanUtil.map(drugCollection, drug);
		prescriptionItemDetail.setDrug(drug);
		prescriptionItemDetails.add(prescriptionItemDetail);
	    }
	    response.setItems(prescriptionItemDetails);
	}
	return response;
    }

    @Override
    public DrugTypeAddEditResponse addDrugType(DrugTypeAddEditRequest request) {
	DrugTypeAddEditResponse response = null;

	DrugTypeCollection drugTypeCollection = new DrugTypeCollection();
	BeanUtil.map(request, drugTypeCollection);
	try {
	    drugTypeCollection.setCreatedTime(new Date());
	    drugTypeCollection = drugTypeRepository.save(drugTypeCollection);
	    response = new DrugTypeAddEditResponse();
	    BeanUtil.map(drugTypeCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug Type");
	}
	return response;

    }

    @Override
    public DrugTypeAddEditResponse editDrugType(DrugTypeAddEditRequest request) {

	DrugTypeAddEditResponse response = null;

	DrugTypeCollection drugTypeCollection = new DrugTypeCollection();
	BeanUtil.map(request, drugTypeCollection);
	try {
	    drugTypeCollection.setCreatedTime(new Date());
	    drugTypeCollection = drugTypeRepository.save(drugTypeCollection);
	    response = new DrugTypeAddEditResponse();
	    BeanUtil.map(drugTypeCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug Type");
	}
	return response;

    }

    @Override
    public Boolean deleteDrugType(String drugTypeId) {

	Boolean response = false;
	DrugTypeCollection drugTypeCollection = null;
	try {
	    drugTypeCollection = drugTypeRepository.findOne(drugTypeId);
	    if (drugTypeCollection != null) {
		drugTypeCollection.setIsDeleted(true);
		drugTypeCollection = drugTypeRepository.save(drugTypeCollection);
		response = true;
	    } else {
		throw new BusinessException(ServiceError.NotFound, "Drug Type Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug Type");
	}
	return response;
    }

    @Override
    public DrugStrengthAddEditResponse addDrugStrength(DrugStrengthAddEditRequest request) {

	DrugStrengthAddEditResponse response = null;

	DrugStrengthUnitCollection drugStrengthUnitCollection = new DrugStrengthUnitCollection();
	BeanUtil.map(request, drugStrengthUnitCollection);
	try {
	    drugStrengthUnitCollection.setCreatedTime(new Date());
	    drugStrengthUnitCollection = drugStrengthRepository.save(drugStrengthUnitCollection);
	    response = new DrugStrengthAddEditResponse();
	    BeanUtil.map(drugStrengthUnitCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug Strength");
	}
	return response;
    }

    @Override
    public DrugStrengthAddEditResponse editDrugStrength(DrugStrengthAddEditRequest request) {

	DrugStrengthAddEditResponse response = null;

	DrugStrengthUnitCollection drugStrengthUnitCollection = new DrugStrengthUnitCollection();
	BeanUtil.map(request, drugStrengthUnitCollection);
	try {
	    drugStrengthUnitCollection.setCreatedTime(new Date());
	    drugStrengthUnitCollection = drugStrengthRepository.save(drugStrengthUnitCollection);
	    response = new DrugStrengthAddEditResponse();
	    BeanUtil.map(drugStrengthUnitCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug Strength");
	}
	return response;
    }

    @Override
    public Boolean deleteDrugStrength(String drugStrengthId) {

	Boolean response = false;
	DrugStrengthUnitCollection drugStrengthCollection = null;
	try {
	    drugStrengthCollection = drugStrengthRepository.findOne(drugStrengthId);
	    if (drugStrengthCollection != null) {
		drugStrengthCollection.setIsDeleted(true);
		drugStrengthCollection = drugStrengthRepository.save(drugStrengthCollection);
		response = true;
	    } else {
		throw new BusinessException(ServiceError.NotFound, "Drug Strength Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug Strength");
	}
	return response;
    }

    @Override
    public DrugDosageAddEditResponse addDrugDosage(DrugDosageAddEditRequest request) {

	DrugDosageAddEditResponse response = null;

	DrugDosageCollection drugDosageCollection = new DrugDosageCollection();
	BeanUtil.map(request, drugDosageCollection);
	try {
	    drugDosageCollection.setCreatedTime(new Date());
	    drugDosageCollection = drugDosageRepository.save(drugDosageCollection);
	    response = new DrugDosageAddEditResponse();
	    BeanUtil.map(drugDosageCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug Dosage");
	}
	return response;
    }

    @Override
    public DrugDosageAddEditResponse editDrugDosage(DrugDosageAddEditRequest request) {

	DrugDosageAddEditResponse response = null;

	DrugDosageCollection drugDosageCollection = new DrugDosageCollection();
	BeanUtil.map(request, drugDosageCollection);
	try {
	    drugDosageCollection.setCreatedTime(new Date());
	    drugDosageCollection = drugDosageRepository.save(drugDosageCollection);
	    response = new DrugDosageAddEditResponse();
	    BeanUtil.map(drugDosageCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editin Drug Dosage");
	}
	return response;
    }

    @Override
    public Boolean deleteDrugDosage(String drugDosageId) {
	Boolean response = false;
	DrugDosageCollection drugDosageCollection = null;
	try {
	    drugDosageCollection = drugDosageRepository.findOne(drugDosageId);
	    if (drugDosageCollection != null) {
		drugDosageCollection.setIsDeleted(true);
		drugDosageCollection = drugDosageRepository.save(drugDosageCollection);
		response = true;
	    } else {
		throw new BusinessException(ServiceError.NotFound, "Drug Dosage Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug Dosage");
	}
	return response;
    }

    @Override
    public DrugDirectionAddEditResponse addDrugDirection(DrugDirectionAddEditRequest request) {

	DrugDirectionAddEditResponse response = null;

	DrugDirectionCollection drugDirectionCollection = new DrugDirectionCollection();
	BeanUtil.map(request, drugDirectionCollection);
	try {
	    drugDirectionCollection.setCreatedTime(new Date());
	    drugDirectionCollection = drugDirectionRepository.save(drugDirectionCollection);
	    response = new DrugDirectionAddEditResponse();
	    BeanUtil.map(drugDirectionCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug Direction");
	}
	return response;
    }

    @Override
    public DrugDirectionAddEditResponse editDrugDirection(DrugDirectionAddEditRequest request) {

	DrugDirectionAddEditResponse response = null;

	DrugDirectionCollection drugDirectionCollection = new DrugDirectionCollection();
	BeanUtil.map(request, drugDirectionCollection);
	try {
	    drugDirectionCollection.setCreatedTime(new Date());
	    drugDirectionCollection = drugDirectionRepository.save(drugDirectionCollection);
	    response = new DrugDirectionAddEditResponse();
	    BeanUtil.map(drugDirectionCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug Direction");
	}
	return response;

    }

    @Override
    public Boolean deleteDrugDirection(String drugDirectionId) {
	Boolean response = false;
	DrugDirectionCollection drugDirectionCollection = null;
	try {
	    drugDirectionCollection = drugDirectionRepository.findOne(drugDirectionId);
	    if (drugDirectionCollection != null) {
		drugDirectionCollection.setIsDeleted(true);
		drugDirectionCollection = drugDirectionRepository.save(drugDirectionCollection);
		response = true;
	    } else {
		throw new BusinessException(ServiceError.NotFound, "Drug Dosage Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug Direction");
	}
	return response;
    }

    @Override
    public DrugDurationUnitAddEditResponse addDrugDurationUnit(DrugDurationUnitAddEditRequest request) {

	DrugDurationUnitAddEditResponse response = null;

	DrugDurationUnitCollection drugDurationUnitCollection = new DrugDurationUnitCollection();
	BeanUtil.map(request, drugDurationUnitCollection);
	try {
	    drugDurationUnitCollection.setCreatedTime(new Date());
	    drugDurationUnitCollection = drugDurationUnitRepository.save(drugDurationUnitCollection);
	    response = new DrugDurationUnitAddEditResponse();
	    BeanUtil.map(drugDurationUnitCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug Duration Unit");
	}
	return response;
    }

    @Override
    public DrugDurationUnitAddEditResponse editDrugDurationUnit(DrugDurationUnitAddEditRequest request) {

	DrugDurationUnitAddEditResponse response = null;

	DrugDurationUnitCollection drugDurationUnitCollection = new DrugDurationUnitCollection();
	BeanUtil.map(request, drugDurationUnitCollection);
	try {
	    drugDurationUnitCollection.setCreatedTime(new Date());
	    drugDurationUnitCollection = drugDurationUnitRepository.save(drugDurationUnitCollection);
	    response = new DrugDurationUnitAddEditResponse();
	    BeanUtil.map(drugDurationUnitCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug Duration Unit");
	}
	return response;

    }

    @Override
    public Boolean deleteDrugDurationUnit(String drugDurationUnitId) {
	Boolean response = false;
	DrugDurationUnitCollection drugDurationUnitCollection = null;
	try {
	    drugDurationUnitCollection = drugDurationUnitRepository.findOne(drugDurationUnitId);
	    if (drugDurationUnitCollection != null) {
		drugDurationUnitCollection.setIsDeleted(true);
		drugDurationUnitCollection = drugDurationUnitRepository.save(drugDurationUnitCollection);
		response = true;
	    } else {
		throw new BusinessException(ServiceError.NotFound, "Drug Duration Unit Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug Duration Unit");
	}
	return response;
    }

    @Override
    public Prescription getPrescriptionById(String prescriptionId) {
	Prescription prescription = null;
	try {
	    PrescriptionCollection prescriptionCollection = prescriptionRepository.findOne(prescriptionId);
	    if (prescriptionCollection != null) {
		prescription = new Prescription();
		BeanUtil.map(prescriptionCollection, prescription);
		if (prescriptionCollection.getItems() != null && !prescriptionCollection.getItems().isEmpty()) {
		    List<PrescriptionItemDetail> prescriptionItemDetails = new ArrayList<PrescriptionItemDetail>();
		    for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
			PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();
			BeanUtil.map(prescriptionItem, prescriptionItemDetail);
			DrugCollection drugCollection = drugRepository.findOne(prescriptionItem.getDrugId());
			Drug drug = new Drug();
			BeanUtil.map(drugCollection, drug);
			prescriptionItemDetail.setDrug(drug);
			prescriptionItemDetails.add(prescriptionItemDetail);
		    }
		    prescription.setItems(prescriptionItemDetails);
		}
	    } else {
		throw new BusinessException(ServiceError.NotFound, "Prescription Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error while getting prescription : " + e.getCause().getMessage());
	}
	return prescription;
    }

	@Override
	public List<Object> getPrescriptionItems(String type, String range, int page, int size, String doctorId, String locationId, String hospitalId, String createdTime, Boolean isDeleted) {
 
		List<Object> response = new ArrayList<Object>();
		
		switch(PrescriptionItems.valueOf(type.toUpperCase())){
			
		case DRUG : {
			
			switch(Range.valueOf(range.toUpperCase())){
			
			case GLOBAL :  response = getGlobalDrugs(page, size, createdTime, isDeleted);	break;
			case CUSTOM : response=getCustomDrugs(page, size, doctorId, locationId, hospitalId, createdTime, isDeleted); break;
			case BOTH : response=getCustomGlobalDrugs(page, size, doctorId, locationId, hospitalId, createdTime, isDeleted); break;
			}
			break;
		}
		case DRUGTYPE : {
			switch(Range.valueOf(range.toUpperCase())){
			
			case GLOBAL :  response = getGlobalDrugType(page, size, createdTime, isDeleted);	break;
			case CUSTOM : response=getCustomDrugType(page, size, doctorId, locationId, hospitalId, createdTime, isDeleted); break;
			case BOTH : response=getCustomGlobalDrugType(page, size, doctorId, locationId, hospitalId, createdTime, isDeleted); break;
			}
			break;
		}
		case DRUGDIRECTION :{ 
			switch(Range.valueOf(range.toUpperCase())){
			
			case GLOBAL :  response = getGlobalDrugDirection(page, size, createdTime, isDeleted);	break;
			case CUSTOM : response=getCustomDrugDirection(page, size, doctorId, locationId, hospitalId, createdTime, isDeleted); break;
			case BOTH : response=getCustomGlobalDrugDirection(page, size, doctorId, locationId, hospitalId, createdTime, isDeleted); break;
			}
			break;
		}
		case DRUGDOSAGE :{
			switch(Range.valueOf(range.toUpperCase())){
			
			case GLOBAL :  response = getGlobalDrugDosage(page, size, createdTime, isDeleted);	break;
			case CUSTOM : response=getCustomDrugDosage(page, size, doctorId, locationId, hospitalId, createdTime, isDeleted); break;
			case BOTH : response=getCustomGlobalDrugDosage(page, size, doctorId, locationId, hospitalId, createdTime, isDeleted); break;
			}
			break;
		}
		case DRUGDURATIONUNIT : {
			switch(Range.valueOf(range.toUpperCase())){
			
			case GLOBAL :  response = getGlobalDrugDurationUnit(page, size, createdTime, isDeleted);	break;
			case CUSTOM : response=getCustomDrugDurationUnit(page, size, doctorId, locationId, hospitalId, createdTime, isDeleted); break;
			case BOTH : response=getCustomGlobalDrugDurationUnit(page, size, doctorId, locationId, hospitalId, createdTime, isDeleted); break;
			}
			break;
		}
		case DRUGSTRENGTHUNIT : {
			switch(Range.valueOf(range.toUpperCase())){
			
			case GLOBAL :  response = getGlobalDrugStrengthUnit(page, size, createdTime, isDeleted);	break;
			case CUSTOM : response=getCustomDrugStrengthUnit(page, size, doctorId, locationId, hospitalId, createdTime, isDeleted); break;
			case BOTH : response=getCustomGlobalDrugStrengthUnit(page, size, doctorId, locationId, hospitalId, createdTime, isDeleted); break;
			}
			break;
		}
		default : break;
		}
		return response;
	}

	private List<Object> getGlobalDrugs(int page, int size, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugCollection> drugCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted) drugCollections = drugRepository.getGlobalDrugs(new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else drugCollections = drugRepository.getGlobalDrugs(isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)	drugCollections = drugRepository.getGlobalDrugs(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else drugCollections = drugRepository.getGlobalDrugs(new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
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
	private List<Object> getCustomDrugs(int page, int size, String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugCollection> drugCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted)
				drugCollections = drugRepository.getCustomDrugs(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugCollections = drugRepository.getCustomDrugs(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)
				drugCollections = drugRepository.getCustomDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
					"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugCollections = drugRepository.getCustomDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted, new Sort(
					Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
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
	private List<Object> getCustomGlobalDrugs(int page, int size, String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugCollection> drugCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted)
				drugCollections = drugRepository.getCustomGlobalDrugs(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugCollections = drugRepository.getCustomGlobalDrugs(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)
				drugCollections = drugRepository.getCustomGlobalDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
					"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugCollections = drugRepository.getCustomGlobalDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted, new Sort(
					Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
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

	private List<Object> getGlobalDrugType(int page, int size, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugTypeCollection> drugTypeCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted) drugTypeCollections = drugTypeRepository.getGlobalDrugType(new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else drugTypeCollections = drugTypeRepository.getGlobalDrugType(isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)	drugTypeCollections = drugTypeRepository.getGlobalDrugType(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else drugTypeCollections = drugTypeRepository.getGlobalDrugType(new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
		    }
		    if (!drugTypeCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
		    	BeanUtil.map(drugTypeCollections, response);
		    } else {
		    	throw new BusinessException(ServiceError.Unknown, "No Drug Type Found");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Type");
		}
		return response;
}
	private List<Object> getCustomDrugType(int page, int size, String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugTypeCollection> drugTypeCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted)
				drugTypeCollections = drugTypeRepository.getCustomDrugType(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugTypeCollections = drugTypeRepository.getCustomDrugType(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)
				drugTypeCollections = drugTypeRepository.getCustomDrugType(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
					"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugTypeCollections = drugTypeRepository.getCustomDrugType(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted, new Sort(
					Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugTypeCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
		    	BeanUtil.map(drugTypeCollections, response);
		    } else {
		    	throw new BusinessException(ServiceError.Unknown, "No Drug Type Found");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Type");
		}
		return response;
}
	private List<Object> getCustomGlobalDrugType(int page, int size, String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugTypeCollection> drugTypeCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted)
				drugTypeCollections = drugTypeRepository.getCustomGlobalDrugType(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugTypeCollections = drugTypeRepository.getCustomGlobalDrugType(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)
				drugTypeCollections = drugTypeRepository.getCustomGlobalDrugType(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
					"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugTypeCollections = drugTypeRepository.getCustomGlobalDrugType(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted, new Sort(
					Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugTypeCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
		    	BeanUtil.map(drugTypeCollections, response);
		    } else {
		    	throw new BusinessException(ServiceError.Unknown, "No Drug Type Found");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Type");
		}
		return response;
}

	private List<Object> getGlobalDrugDirection(int page, int size, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugDirectionCollection> drugDirectionCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted) drugDirectionCollections = drugDirectionRepository.getGlobalDrugDirection(new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else drugDirectionCollections = drugDirectionRepository.getGlobalDrugDirection(isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)	drugDirectionCollections = drugDirectionRepository.getGlobalDrugDirection(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else drugDirectionCollections = drugDirectionRepository.getGlobalDrugDirection(new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugDirectionCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
		    	BeanUtil.map(drugDirectionCollections, response);
		    } else {
		    	throw new BusinessException(ServiceError.Unknown, "No Drug Direction Found");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
		}
		return response;
}
	private List<Object> getCustomDrugDirection(int page, int size, String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugDirectionCollection> drugDirectionCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted)
				drugDirectionCollections = drugDirectionRepository.getCustomDrugDirection(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugDirectionCollections = drugDirectionRepository.getCustomDrugDirection(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)
				drugDirectionCollections = drugDirectionRepository.getCustomDrugDirection(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
					"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugDirectionCollections = drugDirectionRepository.getCustomDrugDirection(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted, new Sort(
					Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugDirectionCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
		    	BeanUtil.map(drugDirectionCollections, response);
		    } else {
		    	throw new BusinessException(ServiceError.Unknown, "No Drug Direction Found");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
		}
		return response;
}
	private List<Object> getCustomGlobalDrugDirection(int page, int size, String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugDirectionCollection> drugDirectionCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted)
				drugDirectionCollections = drugDirectionRepository.getCustomGlobalDrugDirection(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugDirectionCollections = drugDirectionRepository.getCustomGlobalDrugDirection(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)
				drugDirectionCollections = drugDirectionRepository.getCustomGlobalDrugDirection(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
					"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugDirectionCollections = drugDirectionRepository.getCustomGlobalDrugDirection(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted, new Sort(
					Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugDirectionCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
		    	BeanUtil.map(drugDirectionCollections, response);
		    } else {
		    	throw new BusinessException(ServiceError.Unknown, "No Drug Direction Found");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
		}
		return response;
}

	private List<Object> getGlobalDrugDosage(int page, int size, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugDosageCollection> drugDosageCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted) drugDosageCollections = drugDosageRepository.getGlobalDrugDosage(new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else drugDosageCollections = drugDosageRepository.getGlobalDrugDosage(isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)	drugDosageCollections = drugDosageRepository.getGlobalDrugDosage(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else drugDosageCollections = drugDosageRepository.getGlobalDrugDosage(new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugDosageCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
		    	BeanUtil.map(drugDosageCollections, response);
		    } else {
		    	throw new BusinessException(ServiceError.Unknown, "No Drug Dosage Found");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
		}
		return response;
}
	private List<Object> getCustomDrugDosage(int page, int size, String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugDosageCollection> drugDosageCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted)
				drugDosageCollections = drugDosageRepository.getCustomDrugDosage(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugDosageCollections = drugDosageRepository.getCustomDrugDosage(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)
				drugDosageCollections = drugDosageRepository.getCustomDrugDosage(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
					"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugDosageCollections = drugDosageRepository.getCustomDrugDosage(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted, new Sort(
					Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugDosageCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
		    	BeanUtil.map(drugDosageCollections, response);
		    } else {
		    	throw new BusinessException(ServiceError.Unknown, "No Drug Dosage Found");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
		}
		return response;
}
	private List<Object> getCustomGlobalDrugDosage(int page, int size, String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugDosageCollection> drugDosageCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted)
				drugDosageCollections = drugDosageRepository.getCustomGlobalDrugDosage(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugDosageCollections = drugDosageRepository.getCustomGlobalDrugDosage(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)
				drugDosageCollections = drugDosageRepository.getCustomGlobalDrugDosage(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
					"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugDosageCollections = drugDosageRepository.getCustomGlobalDrugDosage(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted, new Sort(
					Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugDosageCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
		    	BeanUtil.map(drugDosageCollections, response);
		    } else {
		    	throw new BusinessException(ServiceError.Unknown, "No Drug Dosage Found");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
		}
		return response;
}

	private List<Object> getGlobalDrugDurationUnit(int page, int size, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugDurationUnitCollection> drugDurationUnitCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted) drugDurationUnitCollections = drugDurationUnitRepository.getGlobalDrugDurationUnit(new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else drugDurationUnitCollections = drugDurationUnitRepository.getGlobalDrugDurationUnit(isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)	drugDurationUnitCollections = drugDurationUnitRepository.getGlobalDrugDurationUnit(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else drugDurationUnitCollections = drugDurationUnitRepository.getGlobalDrugDurationUnit(new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugDurationUnitCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
		    	BeanUtil.map(drugDurationUnitCollections, response);
		    } else {
		    	throw new BusinessException(ServiceError.Unknown, "No Drug DurationUnit Found");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug DurationUnit");
		}
		return response;
}
	private List<Object> getCustomDrugDurationUnit(int page, int size, String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugDurationUnitCollection> drugDurationUnitCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted)
				drugDurationUnitCollections = drugDurationUnitRepository.getCustomDrugDurationUnit(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugDurationUnitCollections = drugDurationUnitRepository.getCustomDrugDurationUnit(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)
				drugDurationUnitCollections = drugDurationUnitRepository.getCustomDrugDurationUnit(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
					"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugDurationUnitCollections = drugDurationUnitRepository.getCustomDrugDurationUnit(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted, new Sort(
					Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugDurationUnitCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
		    	BeanUtil.map(drugDurationUnitCollections, response);
		    } else {
		    	throw new BusinessException(ServiceError.Unknown, "No Drug DurationUnit Found");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug DurationUnit");
		}
		return response;
}
	private List<Object> getCustomGlobalDrugDurationUnit(int page, int size, String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugDurationUnitCollection> drugDurationUnitCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted)
				drugDurationUnitCollections = drugDurationUnitRepository.getCustomGlobalDrugDurationUnit(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugDurationUnitCollections = drugDurationUnitRepository.getCustomGlobalDrugDurationUnit(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)
				drugDurationUnitCollections = drugDurationUnitRepository.getCustomGlobalDrugDurationUnit(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
					"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugDurationUnitCollections = drugDurationUnitRepository.getCustomGlobalDrugDurationUnit(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted, new Sort(
					Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugDurationUnitCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
		    	BeanUtil.map(drugDurationUnitCollections, response);
		    } else {
		    	throw new BusinessException(ServiceError.Unknown, "No Drug DurationUnit Found");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug DurationUnit");
		}
		return response;
}

	private List<Object> getGlobalDrugStrengthUnit(int page, int size, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugStrengthUnitCollection> drugStrengthUnitCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted) drugStrengthUnitCollections = drugStrengthRepository.getGlobalDrugStrengthUnit(new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else drugStrengthUnitCollections = drugStrengthRepository.getGlobalDrugStrengthUnit(isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)	drugStrengthUnitCollections = drugStrengthRepository.getGlobalDrugStrengthUnit(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else drugStrengthUnitCollections = drugStrengthRepository.getGlobalDrugStrengthUnit(new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugStrengthUnitCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
		    	BeanUtil.map(drugStrengthUnitCollections, response);
		    } else {
		    	throw new BusinessException(ServiceError.Unknown, "No Drug StrengthUnit Found");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug StrengthUnit");
		}
		return response;
}
	private List<Object> getCustomDrugStrengthUnit(int page, int size, String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugStrengthUnitCollection> drugStrengthUnitCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted)
				drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)
				drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
					"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted, new Sort(
					Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugStrengthUnitCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
		    	BeanUtil.map(drugStrengthUnitCollections, response);
		    } else {
		    	throw new BusinessException(ServiceError.Unknown, "No Drug StrengthUnit Found");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug StrengthUnit");
		}
		return response;
}
	private List<Object> getCustomGlobalDrugStrengthUnit(int page, int size, String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
		List<Object> response = null;
		List<DrugStrengthUnitCollection> drugStrengthUnitCollections = null;
		try {
		    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
			    if (isDeleted)
				drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    } else {
			long createdTimeStamp = Long.parseLong(createdTime);
			    if (isDeleted)
				drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
					"createdTime"), size>0 ? new PageRequest(page, size):null);
			    else
				drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted, new Sort(
					Sort.Direction.DESC, "createdTime"), size>0 ? new PageRequest(page, size):null);
			
		    }
		    if (!drugStrengthUnitCollections.isEmpty()) {
		    	response = new ArrayList<Object>();
		    	BeanUtil.map(drugStrengthUnitCollections, response);
		    } else {
		    	throw new BusinessException(ServiceError.Unknown, "No Drug StrengthUnit Found");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug StrengthUnit");
		}
		return response;
}

}
