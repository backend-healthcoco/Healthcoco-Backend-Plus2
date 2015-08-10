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
import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.DrugDosage;
import com.dpdocter.beans.DrugDurationUnit;
import com.dpdocter.beans.DrugStrengthUnit;
import com.dpdocter.beans.DrugType;
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
    public List<Prescription> getPrescriptions(String doctorId, String hospitalId, String locationId, String patientId, String createdTime,
	    boolean isOTPVerified, boolean isDeleted) {
	List<PrescriptionCollection> prescriptionCollections = null;
	List<Prescription> prescriptions = null;
	try {
	    if (StringUtils.isEmpty(createdTime)) {
		if (!isOTPVerified) {
		    if (isDeleted)
			prescriptionCollections = prescriptionRepository.getPrescription(doctorId, hospitalId, locationId, patientId, new Sort(
				Sort.Direction.DESC, "createdDate"));
		    else
			prescriptionCollections = prescriptionRepository.getPrescription(doctorId, hospitalId, locationId, patientId, isDeleted, new Sort(
				Sort.Direction.DESC, "createdDate"));
		} else {
		    if (isDeleted)
			prescriptionCollections = prescriptionRepository.getPrescription(patientId, new Sort(Sort.Direction.DESC, "createdDate"));
		    else
			prescriptionCollections = prescriptionRepository.getPrescription(patientId, false, new Sort(Sort.Direction.DESC, "createdDate"));
		}
	    } else {
		long createdTimestamp = Long.parseLong(createdTime);
		if (!isOTPVerified) {
		    if (isDeleted)
			prescriptionCollections = prescriptionRepository.getPrescription(doctorId, hospitalId, locationId, patientId,
				new Date(createdTimestamp), new Sort(Sort.Direction.DESC, "createdDate"));
		    else
			prescriptionCollections = prescriptionRepository.getPrescription(doctorId, hospitalId, locationId, patientId,
				new Date(createdTimestamp), isDeleted, new Sort(Sort.Direction.DESC, "createdDate"));
		} else {
		    if (isDeleted)
			prescriptionCollections = prescriptionRepository.getPrescription(patientId, new Date(createdTimestamp), new Sort(Sort.Direction.DESC,
				"createdDate"));
		    else
			prescriptionCollections = prescriptionRepository.getPrescription(patientId, new Date(createdTimestamp), isDeleted, new Sort(
				Sort.Direction.DESC, "createdDate"));
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
    public List<TemplateAddEditResponseDetails> getTemplates(String doctorId, String hospitalId, String locationId, String createdTime, boolean isDeleted) {
	List<TemplateAddEditResponseDetails> response = null;
	List<TemplateCollection> templateCollections = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
		if (hospitalId == null && locationId == null) {
		    if (isDeleted)
			templateCollections = templateRepository.getTemplates(doctorId, new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			templateCollections = templateRepository.getTemplates(doctorId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
		} else {
		    if (isDeleted)
			templateCollections = templateRepository.getTemplates(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			templateCollections = templateRepository.getTemplates(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC,
				"createdTime"));
		}
	    } else {
		long createdTimeStamp = Long.parseLong(createdTime);
		if (hospitalId == null && locationId == null) {
		    if (isDeleted)
			templateCollections = templateRepository.getTemplates(doctorId, new Date(createdTimeStamp),
				new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			templateCollections = templateRepository.getTemplates(doctorId, new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC,
				"createdTime"));
		} else {
		    if (isDeleted)
			templateCollections = templateRepository.getTemplates(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(
				Sort.Direction.DESC, "createdTime"));
		    else
			templateCollections = templateRepository.getTemplates(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted,
				new Sort(Sort.Direction.DESC, "createdTime"));
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
    public List<DrugAddEditResponse> getDrugs(String doctorId, String hospitalId, String locationId, String createdTime, boolean isDeleted) {
	List<DrugAddEditResponse> response = null;
	List<DrugCollection> drugCollections = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
		if (hospitalId == null && locationId == null) {
		    if (isDeleted)
			drugCollections = drugRepository.getDrugs(doctorId, new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			drugCollections = drugRepository.getDrugs(doctorId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
		} else {
		    if (isDeleted)
			drugCollections = drugRepository.getDrugs(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			drugCollections = drugRepository.getDrugs(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
		}
	    } else {
		long createdTimeStamp = Long.parseLong(createdTime);
		if (hospitalId == null && locationId == null) {
		    if (isDeleted)
			drugCollections = drugRepository.getDrugs(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			drugCollections = drugRepository
				.getDrugs(doctorId, new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
		} else {
		    if (isDeleted)
			drugCollections = drugRepository.getDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
				"createdTime"));
		    else
			drugCollections = drugRepository.getDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted, new Sort(
				Sort.Direction.DESC, "createdTime"));
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
    public List<DrugType> getAllDrugType() {
	List<DrugType> response = null;
	List<DrugTypeCollection> drugTypeCollections = null;
	try {
	    drugTypeCollections = drugTypeRepository.findAll();
	    if (drugTypeCollections != null) {
		response = new ArrayList<DrugType>();
		BeanUtil.map(drugTypeCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Types");
	}
	return response;
    }

    @Override
    public List<DrugType> getDrugType(String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {

	List<DrugType> response = null;
	List<DrugTypeCollection> drugTypeCollections = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
		if (hospitalId == null && locationId == null && doctorId == null) {
		    if (isDeleted)
			drugTypeCollections = drugTypeRepository.findAll(new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			drugTypeCollections = drugTypeRepository.getDrugType(isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
		} else {
		    if (isDeleted)
			drugTypeCollections = drugTypeRepository.getDrugType(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			drugTypeCollections = drugTypeRepository.getDrugType(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC,
				"createdTime"));
		}
	    } else {
		long createdTimeStamp = Long.parseLong(createdTime);
		if (hospitalId == null && locationId == null && doctorId == null) {
		    if (isDeleted)
			drugTypeCollections = drugTypeRepository.getDrugType(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			drugTypeCollections = drugTypeRepository.getDrugType(new Date(createdTimeStamp), isDeleted,
				new Sort(Sort.Direction.DESC, "createdTime"));
		} else {
		    if (isDeleted)
			drugTypeCollections = drugTypeRepository.getDrugType(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(
				Sort.Direction.DESC, "createdTime"));
		    else
			drugTypeCollections = drugTypeRepository.getDrugType(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted, new Sort(
				Sort.Direction.DESC, "createdTime"));
		}
		
	    }

	    if (drugTypeCollections != null) {
		response = new ArrayList<DrugType>();
		BeanUtil.map(drugTypeCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Types");
	}
	return response;
    }

    @Override
    public List<DrugStrengthUnit> getAllDrugStrengthUnit() {
	List<DrugStrengthUnit> response = null;
	List<DrugStrengthUnitCollection> drugStrengthUnitCollections = null;
	try {
	    drugStrengthUnitCollections = drugStrengthRepository.findAll();
	    if (drugStrengthUnitCollections != null) {
		response = new ArrayList<DrugStrengthUnit>();
		BeanUtil.map(drugStrengthUnitCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Strength Units");
	}
	return response;
    }

    @Override
    public List<DrugStrengthUnit> getDrugStrengthUnit(String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
	List<DrugStrengthUnit> response = null;
	List<DrugStrengthUnitCollection> drugStrengthUnitCollections = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
		if (hospitalId == null && locationId == null && doctorId == null) {
		    if (isDeleted)
			drugStrengthUnitCollections = drugStrengthRepository.findAll(new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			drugStrengthUnitCollections = drugStrengthRepository.getDrugStrengthUnit(isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
		} else {
		    if (isDeleted)
			drugStrengthUnitCollections = drugStrengthRepository.getDrugStrengthUnit(doctorId, hospitalId, locationId, new Sort(
				Sort.Direction.DESC, "createdTime"));
		    else
			drugStrengthUnitCollections = drugStrengthRepository.getDrugStrengthUnit(doctorId, hospitalId, locationId, isDeleted, new Sort(
				Sort.Direction.DESC, "createdTime"));
		}
	    } else {
		long createdTimeStamp = Long.parseLong(createdTime);
		if (hospitalId == null && locationId == null && doctorId == null) {
		    if (isDeleted)
			drugStrengthUnitCollections = drugStrengthRepository.getDrugStrengthUnit(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
				"createdTime"));
		    else
			drugStrengthUnitCollections = drugStrengthRepository.getDrugStrengthUnit(new Date(createdTimeStamp), isDeleted, new Sort(
				Sort.Direction.DESC, "createdTime"));
		} else {
		    if (isDeleted)
			drugStrengthUnitCollections = drugStrengthRepository.getDrugStrengthUnit(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			drugStrengthUnitCollections = drugStrengthRepository.getDrugStrengthUnit(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
		}

	    }

	    if (drugStrengthUnitCollections != null) {
		response = new ArrayList<DrugStrengthUnit>();
		BeanUtil.map(drugStrengthUnitCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Strength Unit");
	}
	return response;

    }

    @Override
    public List<DrugDosage> getAllDrugDosage() {
	List<DrugDosage> response = null;
	List<DrugDosageCollection> drugDosageCollections = null;
	try {
	    drugDosageCollections = drugDosageRepository.findAll();
	    if (drugDosageCollections != null) {
		response = new ArrayList<DrugDosage>();
		BeanUtil.map(drugDosageCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
	}
	return response;
    }

    @Override
    public List<DrugDosage> getDrugDosage(String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
	List<DrugDosage> response = null;
	List<DrugDosageCollection> drugDosageCollections = null;
	try {

	    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
		if (hospitalId == null && locationId == null && doctorId == null) {
		    if (isDeleted)
			drugDosageCollections = drugDosageRepository.findAll(new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			drugDosageCollections = drugDosageRepository.getDrugDosage(isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
		} else {
		    if (isDeleted)
			drugDosageCollections = drugDosageRepository.getDrugDosage(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC,
				"createdTime"));
		    else
			drugDosageCollections = drugDosageRepository.getDrugDosage(doctorId, hospitalId, locationId, isDeleted, new Sort(Sort.Direction.DESC,
				"createdTime"));
		}
	    } else {
		long createdTimeStamp = Long.parseLong(createdTime);
		if (hospitalId == null && locationId == null && doctorId == null) {
		    if (isDeleted)
			drugDosageCollections = drugDosageRepository.getDrugDosage(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			drugDosageCollections = drugDosageRepository.getDrugDosage(new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC,
				"createdTime"));
		} else {
		    if (isDeleted)
			drugDosageCollections = drugDosageRepository.getDrugDosage(doctorId, hospitalId, locationId, new Date(createdTimeStamp), new Sort(
				Sort.Direction.DESC, "createdTime"));
		    else
			drugDosageCollections = drugDosageRepository.getDrugDosage(doctorId, hospitalId, locationId, new Date(createdTimeStamp), isDeleted,
				new Sort(Sort.Direction.DESC, "createdTime"));
		}

	    }

	    if (drugDosageCollections != null) {
		response = new ArrayList<DrugDosage>();
		BeanUtil.map(drugDosageCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Custom Drug Dosage");
	}
	return response;
    }

    @Override
    public List<DrugDurationUnit> getAllDrugDurationUnit() {
	List<DrugDurationUnit> response = null;
	List<DrugDurationUnitCollection> drugDurationUnitCollections = null;
	try {
	    drugDurationUnitCollections = drugDurationUnitRepository.findAll();
	    if (drugDurationUnitCollections != null) {
		response = new ArrayList<DrugDurationUnit>();
		BeanUtil.map(drugDurationUnitCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Duration Units");
	}
	return response;
    }

    @Override
    public List<DrugDurationUnit> getDrugDurationUnit(String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
	List<DrugDurationUnit> response = null;
	List<DrugDurationUnitCollection> drugDurationUnitCollections = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
		if (hospitalId == null && locationId == null && doctorId == null) {
		    if (isDeleted)
			drugDurationUnitCollections = drugDurationUnitRepository.findAll(new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			drugDurationUnitCollections = drugDurationUnitRepository.getDrugDurationUnit(isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
		} else {
		    if (isDeleted)
			drugDurationUnitCollections = drugDurationUnitRepository.getDrugDurationUnit(doctorId, hospitalId, locationId, new Sort(
				Sort.Direction.DESC, "createdTime"));
		    else
			drugDurationUnitCollections = drugDurationUnitRepository.getDrugDurationUnit(doctorId, hospitalId, locationId, isDeleted, new Sort(
				Sort.Direction.DESC, "createdTime"));
		}
	    } else {
		long createdTimeStamp = Long.parseLong(createdTime);
		if (hospitalId == null && locationId == null && doctorId == null) {
		    if (isDeleted)
			drugDurationUnitCollections = drugDurationUnitRepository.getDrugDurationUnit(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
				"createdTime"));
		    else
			drugDurationUnitCollections = drugDurationUnitRepository.getDrugDurationUnit(new Date(createdTimeStamp), isDeleted, new Sort(
				Sort.Direction.DESC, "createdTime"));
		} else {
		    if (isDeleted)
			drugDurationUnitCollections = drugDurationUnitRepository.getDrugDurationUnit(doctorId, hospitalId, locationId, new Date(
				createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			drugDurationUnitCollections = drugDurationUnitRepository.getDrugDurationUnit(doctorId, hospitalId, locationId, new Date(
				createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
		}

	    }

	    if (drugDurationUnitCollections != null) {
		response = new ArrayList<DrugDurationUnit>();
		BeanUtil.map(drugDurationUnitCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Duration Units");
	}
	return response;
    }

    @Override
    public List<DrugDirection> getAllDrugDirection() {
	List<DrugDirection> response = null;
	List<DrugDirectionCollection> drugDirectionCollections = null;
	try {
	    drugDirectionCollections = drugDirectionRepository.findAll();
	    if (drugDirectionCollections != null) {
		response = new ArrayList<DrugDirection>();
		BeanUtil.map(drugDirectionCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Directions");
	}
	return response;
    }

    @Override
    public List<DrugDirection> getDrugDirection(String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
	List<DrugDirection> response = null;
	List<DrugDirectionCollection> drugDirectionCollections = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
		if (hospitalId == null && locationId == null && doctorId == null) {
		    if (isDeleted)
			drugDirectionCollections = drugDirectionRepository.findAll(new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			drugDirectionCollections = drugDirectionRepository.getDrugDirection(isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
		} else {
		    if (isDeleted)
			drugDirectionCollections = drugDirectionRepository.getDrugDirection(doctorId, hospitalId, locationId, new Sort(Sort.Direction.DESC,
				"createdTime"));
		    else
			drugDirectionCollections = drugDirectionRepository.getDrugDirection(doctorId, hospitalId, locationId, isDeleted, new Sort(
				Sort.Direction.DESC, "createdTime"));
		}
	    } else {
		long createdTimeStamp = Long.parseLong(createdTime);
		if (hospitalId == null && locationId == null && doctorId == null) {
		    if (isDeleted)
			drugDirectionCollections = drugDirectionRepository.getDrugDirection(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
				"createdTime"));
		    else
			drugDirectionCollections = drugDirectionRepository.getDrugDirection(new Date(createdTimeStamp), isDeleted, new Sort(
				Sort.Direction.DESC, "createdTime"));
		} else {
		    if (isDeleted)
			drugDirectionCollections = drugDirectionRepository.getDrugDirection(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				new Sort(Sort.Direction.DESC, "createdTime"));
		    else
			drugDirectionCollections = drugDirectionRepository.getDrugDirection(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
		}

	    }
	    if (drugDirectionCollections != null) {
		response = new ArrayList<DrugDirection>();
		BeanUtil.map(drugDirectionCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Custom Getting Drug Directions");
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

}
