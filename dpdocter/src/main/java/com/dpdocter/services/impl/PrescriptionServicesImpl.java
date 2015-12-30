package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionItemDetail;
import com.dpdocter.beans.PrescriptionJasperDetails;
import com.dpdocter.beans.PrintSettingsText;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.SMSTrackDetail;
import com.dpdocter.beans.TemplateItem;
import com.dpdocter.beans.TemplateItemDetail;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.DrugDirectionCollection;
import com.dpdocter.collections.DrugDosageCollection;
import com.dpdocter.collections.DrugDurationUnitCollection;
import com.dpdocter.collections.DrugStrengthUnitCollection;
import com.dpdocter.collections.DrugTypeCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.LabTestCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientAdmissionCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.TemplateCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.FONTSTYLE;
import com.dpdocter.enums.PrescriptionItems;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DrugDirectionRepository;
import com.dpdocter.repository.DrugDosageRepository;
import com.dpdocter.repository.DrugDurationUnitRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.DrugStrengthUnitRepository;
import com.dpdocter.repository.DrugTypeRepository;
import com.dpdocter.repository.LabTestRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientAdmissionRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.TemplateRepository;
import com.dpdocter.repository.UserRepository;
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
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.sms.services.SMSServices;
import com.dpdocter.solr.document.SolrDrugDocument;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import common.util.web.DPDoctorUtils;
import common.util.web.PrescriptionUtils;

@Service
public class PrescriptionServicesImpl implements PrescriptionServices {

    private static Logger logger = Logger.getLogger(PrescriptionServicesImpl.class.getName());

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

    @Autowired
    private JasperReportService jasperReportService;

    @Autowired
    private MailService mailService;

    @Autowired
    private PrintSettingsRepository printSettingsRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SMSServices sMSServices;

    @Autowired
    private EmailTackService emailTackService;

    @Autowired
    private PatientAdmissionRepository patientAdmissionRepository;

    @Autowired
    private LabTestRepository labTestRepository;

    @Autowired
    private PatientVisitRepository patientVisitRepository;

    @Autowired
    private DiagnosticTestRepository diagnosticTestRepository;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @Override
    public DrugAddEditResponse addDrug(DrugAddEditRequest request) {
	DrugAddEditResponse response = null;
	DrugCollection drugCollection = new DrugCollection();
	BeanUtil.map(request, drugCollection);
	UUID drugCode = UUID.randomUUID();
	drugCollection.setDrugCode(drugCode.toString());
	try {
	    Date createdTime = new Date();
	    drugCollection.setCreatedTime(createdTime);
	    if (drugCollection.getDrugType() != null) {
		if (drugCollection.getDrugType().getId() == null)
		    drugCollection.setDrugType(null);
		else {
		    DrugTypeCollection drugTypeCollection = drugTypeRepository.findOne(drugCollection.getDrugType().getId());
		    if (drugTypeCollection != null) {
			DrugType drugType = new DrugType();
			BeanUtil.map(drugTypeCollection, drugType);
			drugCollection.setDrugType(drugType);
		    }
		}
	    }
	    if (drugCollection.getStrength() != null && drugCollection.getStrength().getStrengthUnit() != null) {
		if (drugCollection.getStrength().getStrengthUnit().getId() == null)
		    drugCollection.getStrength().setStrengthUnit(null);
	    }
	    drugCollection = drugRepository.save(drugCollection);
	    response = new DrugAddEditResponse();
	    BeanUtil.map(drugCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Drug");
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
	    DrugCollection oldDrug = drugRepository.findOne(request.getId());
	    drugCollection.setCreatedBy(oldDrug.getCreatedBy());
	    drugCollection.setCreatedTime(oldDrug.getCreatedTime());
	    drugCollection.setDiscarded(oldDrug.getDiscarded());
	    if (drugCollection.getDrugType() != null) {
		if (drugCollection.getDrugType().getId() == null)
		    drugCollection.setDrugType(null);
		else {
		    DrugTypeCollection drugTypeCollection = drugTypeRepository.findOne(drugCollection.getDrugType().getId());
		    if (drugTypeCollection != null) {
			DrugType drugType = new DrugType();
			BeanUtil.map(drugTypeCollection, drugType);
			drugCollection.setDrugType(drugType);
		    }
		}
	    }
	    if (drugCollection.getStrength() != null && drugCollection.getStrength().getStrengthUnit() != null) {
		if (drugCollection.getStrength().getStrengthUnit().getId() == null)
		    drugCollection.getStrength().setStrengthUnit(null);
	    }
	    drugCollection = drugRepository.save(drugCollection);
	    response = new DrugAddEditResponse();
	    BeanUtil.map(drugCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Drug");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug");
	}
	return response;
    }

    @Override
    public Boolean deleteDrug(String drugId, String doctorId, String hospitalId, String locationId, Boolean discarded) {
	Boolean response = false;
	DrugCollection drugCollection = null;
	try {
	    drugCollection = drugRepository.findOne(drugId);
	    if (drugCollection != null) {
		if (drugCollection.getDoctorId() != null && drugCollection.getHospitalId() != null && drugCollection.getLocationId() != null) {
		    if (drugCollection.getDoctorId().equals(doctorId) && drugCollection.getHospitalId().equals(hospitalId)
			    && drugCollection.getLocationId().equals(locationId)) {
			drugCollection.setDiscarded(discarded);
			drugCollection.setUpdatedTime(new Date());
			drugCollection = drugRepository.save(drugCollection);
			response = true;
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.NotAuthorized, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    logger.warn("Cannot Delete Global Drug");
		    throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Drug");
		}
	    } else {
		logger.warn("Drug Not Found");
		throw new BusinessException(ServiceError.NotFound, "Drug Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Drug");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug");
	}
	return response;
    }

    @Override
    public Boolean deleteDrug(String drugId, Boolean discarded) {
	Boolean response = false;
	DrugCollection drugCollection = null;
	try {
	    drugCollection = drugRepository.findOne(drugId);
	    if (drugCollection != null) {
		drugCollection.setUpdatedTime(new Date());
		drugCollection.setDiscarded(discarded);
		drugCollection = drugRepository.save(drugCollection);
		response = true;
	    } else {
		logger.warn("Drug Not Found");
		throw new BusinessException(ServiceError.NotFound, "Drug Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Drug");
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
		logger.warn("Drug not found. Please check Drug Id");
		throw new BusinessException(ServiceError.Unknown, "Drug not found. Please check Drug Id");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug");
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
	    Date createdTime = new Date();
	    templateCollection.setCreatedTime(createdTime);
	    templateCollection = templateRepository.save(templateCollection);
	    response = new TemplateAddEditResponse();
	    BeanUtil.map(templateCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Template");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Template");
	}
	return response;
    }

    @Override
    public TemplateAddEditResponseDetails editTemplate(TemplateAddEditRequest request) {
	TemplateAddEditResponseDetails response = null;
	TemplateAddEditResponse template = null;
	TemplateCollection templateCollection = new TemplateCollection();
	BeanUtil.map(request, templateCollection);
	try {
	    TemplateCollection oldTemplate = templateRepository.findOne(request.getId());
	    templateCollection.setCreatedBy(oldTemplate.getCreatedBy());
	    templateCollection.setCreatedTime(oldTemplate.getCreatedTime());
	    templateCollection.setDiscarded(oldTemplate.getDiscarded());
	    templateCollection = templateRepository.save(templateCollection);
	    template = new TemplateAddEditResponse();
	    BeanUtil.map(templateCollection, template);

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
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Template");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Template");
	}
	return response;
    }

    @Override
    public Boolean deleteTemplate(String templateId, String doctorId, String hospitalId, String locationId, Boolean discarded) {
	Boolean response = false;
	TemplateCollection templateCollection = null;
	try {
	    templateCollection = templateRepository.findOne(templateId);
	    if (templateCollection != null) {
		if (templateCollection.getDoctorId() != null && templateCollection.getHospitalId() != null && templateCollection.getLocationId() != null) {
		    if (templateCollection.getDoctorId().equals(doctorId) && templateCollection.getHospitalId().equals(hospitalId)
			    && templateCollection.getLocationId().equals(locationId)) {
			templateCollection.setUpdatedTime(new Date());
			templateCollection.setDiscarded(discarded);
			templateCollection = templateRepository.save(templateCollection);
			response = true;
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.NotAuthorized, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    logger.warn("Cannot Delete Global Template");
		    throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Template");
		}
	    } else {
		logger.warn("Template Not Found");
		throw new BusinessException(ServiceError.NotFound, "Template Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Template");
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
		logger.warn("Template Not Found");
		throw new BusinessException(ServiceError.NotFound, "Template Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Template");
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
	    Date createdTime = new Date();
	    prescriptionCollection.setCreatedTime(createdTime);
	    prescriptionCollection.setPrescriptionCode(PrescriptionUtils.generatePrescriptionCode());
	    if (prescriptionCollection.getItems() != null) {
		List<PrescriptionItem> items = null;
		for (PrescriptionItem item : prescriptionCollection.getItems()) {
		    if (item.getDrugId() != null) {
			List<DrugDirection> directions = null;
			if (item.getDirection() != null) {
			    for (DrugDirection drugDirection : item.getDirection()) {
				if (drugDirection != null && drugDirection.getId() != null) {
				    if (directions == null)
					directions = new ArrayList<DrugDirection>();
				    directions.add(drugDirection);
				}
			    }
			    item.setDirection(directions);
			}
			if (item.getDuration() != null && item.getDuration().getDurationUnit() != null) {
			    if (item.getDuration().getDurationUnit().getId() == null)
				item.getDuration().setDurationUnit(null);
			}
			if (items == null)
			    items = new ArrayList<PrescriptionItem>();
			items.add(item);
		    }
		}
		prescriptionCollection.setItems(items);
	    }
	    if (prescriptionCollection.getLabTests() != null) {
		List<LabTest> labTests = null;
		for (LabTest labTest : prescriptionCollection.getLabTests()) {
		    if (labTest.getId() != null) {
			if (labTests == null)
			    labTests = new ArrayList<LabTest>();
			labTests.add(labTest);
		    }
		}
		prescriptionCollection.setLabTests(labTests);
	    }
	    UserCollection userCollection = userRepository.findOne(prescriptionCollection.getDoctorId());
	    if (userCollection != null) {
		prescriptionCollection.setCreatedBy((userCollection.getTitle()!=null?userCollection.getTitle()+" ":"")+userCollection.getFirstName());
	    }
	    prescriptionCollection = prescriptionRepository.save(prescriptionCollection);
	    response = new PrescriptionAddEditResponse();
	    BeanUtil.map(prescriptionCollection, response);
	    response.setVisitId(request.getVisitId());
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Prescription");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Prescription");
	}
	return response;
    }

    @Override
    public PrescriptionAddEditResponseDetails editPrescription(PrescriptionAddEditRequest request) {
	PrescriptionAddEditResponseDetails response = null;
	PrescriptionAddEditResponse prescription = null;
	PrescriptionCollection prescriptionCollection = new PrescriptionCollection();
	BeanUtil.map(request, prescriptionCollection);
	try {
	    PrescriptionCollection oldPrescription = prescriptionRepository.findOne(request.getId());
	    prescriptionCollection.setCreatedBy(oldPrescription.getCreatedBy());
	    prescriptionCollection.setCreatedTime(oldPrescription.getCreatedTime());
	    prescriptionCollection.setDiscarded(oldPrescription.getDiscarded());
	    prescriptionCollection.setInHistory(oldPrescription.isInHistory());

	    if (prescriptionCollection.getItems() != null) {
		for (PrescriptionItem item : prescriptionCollection.getItems()) {
		    List<DrugDirection> directions = null;
		    if (item.getDirection() != null) {
			for (DrugDirection drugDirection : item.getDirection()) {
			    if (drugDirection != null && drugDirection.getId() != null) {
				if (directions == null)
				    directions = new ArrayList<DrugDirection>();
				directions.add(drugDirection);
			    }
			}
			item.setDirection(directions);
		    }
		    if (item.getDuration() != null && item.getDuration().getDurationUnit() != null) {
			if (item.getDuration().getDurationUnit().getId() == null)
			    item.getDuration().setDurationUnit(null);
		    }
		}
	    }
	    if (prescriptionCollection.getLabTests() != null) {
		List<LabTest> labTests = null;
		for (LabTest labTest : prescriptionCollection.getLabTests()) {
		    if (labTest.getId() != null) {
			if (labTests == null)
			    labTests = new ArrayList<LabTest>();
			labTests.add(labTest);
		    }
		}
		prescriptionCollection.setLabTests(labTests);
	    }
	    prescriptionCollection = prescriptionRepository.save(prescriptionCollection);
	    prescription = new PrescriptionAddEditResponse();
	    BeanUtil.map(prescriptionCollection, prescription);

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
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Prescription");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Prescription");
	}
	return response;
    }

    @Override
    public Boolean deletePrescription(String prescriptionId, String doctorId, String hospitalId, String locationId, String patientId, Boolean discarded) {
	Boolean response = false;
	PrescriptionCollection prescriptionCollection = null;
	try {
	    prescriptionCollection = prescriptionRepository.findOne(prescriptionId);
	    if (prescriptionCollection != null) {
		if (prescriptionCollection.getDoctorId() != null && prescriptionCollection.getHospitalId() != null
			&& prescriptionCollection.getLocationId() != null && prescriptionCollection.getPatientId() != null) {
		    if (prescriptionCollection.getDoctorId().equals(doctorId) && prescriptionCollection.getHospitalId().equals(hospitalId)
			    && prescriptionCollection.getLocationId().equals(locationId) && prescriptionCollection.getPatientId().equals(patientId)) {
			prescriptionCollection.setDiscarded(discarded);
			prescriptionCollection.setUpdatedTime(new Date());
			prescriptionCollection = prescriptionRepository.save(prescriptionCollection);
			response = true;
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Location Id, Or Patient Id");
			throw new BusinessException(ServiceError.NotAuthorized, "Invalid Doctor Id, Hospital Id, Location Id, Or Patient Id");
		    }
		} else {
		    logger.warn("Invalid Doctor Id, Hospital Id, Location Id, Or Patient Id");
		    throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Prescription");
		}
	    } else {
		logger.warn("Prescription Not Found");
		throw new BusinessException(ServiceError.NotFound, "Prescription Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error("Error Occurred While Deleting Prescription");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Prescription");
	}
	return response;
    }

    @Override
    public List<Prescription> getPrescriptions(int page, int size, String doctorId, String hospitalId, String locationId, String patientId, String updatedTime,
	    boolean isOTPVerified, boolean discarded, boolean inHistory) {
	List<PrescriptionCollection> prescriptionCollections = null;
	List<Prescription> prescriptions = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;

	boolean[] inHistorys = new boolean[2];
	inHistorys[0] = true;
	inHistorys[1] = true;

	try {
	    if (discarded)
		discards[1] = true;
	    if (!inHistory)
		inHistorys[1] = false;

	    long createdTimestamp = Long.parseLong(updatedTime);

	    if (!isOTPVerified) {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			prescriptionCollections = prescriptionRepository.getPrescription(doctorId, patientId, new Date(createdTimestamp), discards, inHistorys,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			prescriptionCollections = prescriptionRepository.getPrescription(doctorId, patientId, new Date(createdTimestamp), discards, inHistorys,
				new Sort(Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			prescriptionCollections = prescriptionRepository.getPrescription(doctorId, hospitalId, locationId, patientId,
				new Date(createdTimestamp), discards, inHistorys, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			prescriptionCollections = prescriptionRepository.getPrescription(doctorId, hospitalId, locationId, patientId,
				new Date(createdTimestamp), discards, inHistorys, new Sort(Sort.Direction.DESC, "updatedTime"));
		}
	    } else {
		if (size > 0)
		    prescriptionCollections = prescriptionRepository.getPrescription(patientId, new Date(createdTimestamp), discards, inHistorys,
			    new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    prescriptionCollections = prescriptionRepository.getPrescription(patientId, new Date(createdTimestamp), discards, inHistorys, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
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
			UserCollection userCollection = userRepository.findOne(prescription.getDoctorId());
			if (userCollection != null) {
			    prescription.setDoctorName(userCollection.getFirstName());
			}
			PatientVisitCollection patientVisitCollection = patientVisitRepository.findByPrescriptionId(prescription.getId());
			if (patientVisitCollection != null)
			    prescription.setVisitId(patientVisitCollection.getId());
			prescriptions.add(prescription);
		    }

		}
	    } else {
		logger.warn("Prescription Not Found");
		throw new BusinessException(ServiceError.NotFound, "Prescription Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(" Error Occurred While Getting Prescription");
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
			    if (drugCollection != null) {
				Drug drug = new Drug();
				BeanUtil.map(drugCollection, drug);
				prescriptionItemDetails.setDrug(drug);
			    }
			    prescriptionItemDetailsList.add(prescriptionItemDetails);
			}
			prescription.setItems(prescriptionItemDetailsList);
			UserCollection userCollection = userRepository.findOne(prescription.getDoctorId());
			if (userCollection != null) {
			    prescription.setDoctorName(userCollection.getFirstName());
			}
			PatientVisitCollection patientVisitCollection = patientVisitRepository.findByPrescriptionId(prescription.getId());
			if (patientVisitCollection != null)
			    prescription.setVisitId(patientVisitCollection.getId());
			prescriptions.add(prescription);
		    }

		}
	    } else {
		logger.warn("Prescription Not Found");
		throw new BusinessException(ServiceError.NotFound, "Prescription Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Prescription");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription");
	}
	return prescriptions;
    }

    @Override
    public List<TemplateAddEditResponseDetails> getTemplates(int page, int size, String doctorId, String hospitalId, String locationId, String updatedTime,
	    boolean discarded) {
	List<TemplateAddEditResponseDetails> response = null;
	List<TemplateCollection> templateCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (hospitalId == null && locationId == null) {
		if (size > 0)
		    templateCollections = templateRepository.getTemplates(doctorId, new Date(createdTimeStamp), discards, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    templateCollections = templateRepository.getTemplates(doctorId, new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    } else {
		if (size > 0)
		    templateCollections = templateRepository.getTemplates(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    templateCollections = templateRepository.getTemplates(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discards, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
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
		logger.warn("Template Not Found");
		throw new BusinessException(ServiceError.NotFound, "Template Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Template");
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
	    logger.error(e + " Error Occurred While Getting Prescription Count");
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
	    if (prescription.getItems() != null) {
		for (PrescriptionItem prescriptionItem : prescription.getItems()) {
		    PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();
		    BeanUtil.map(prescriptionItem, prescriptionItemDetail);
		    if (prescriptionItem.getDrugId() != null) {
			DrugCollection drugCollection = drugRepository.findOne(prescriptionItem.getDrugId());
			Drug drug = new Drug();
			if (drugCollection != null)
			    BeanUtil.map(drugCollection, drug);
			prescriptionItemDetail.setDrug(drug);
			prescriptionItemDetails.add(prescriptionItemDetail);
		    }
		}
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
	    logger.error(e + " Error Occurred While Saving Drug Type");
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
	    DrugTypeCollection oldDrug = drugTypeRepository.findOne(request.getId());
	    drugTypeCollection.setCreatedBy(oldDrug.getCreatedBy());
	    drugTypeCollection.setCreatedTime(oldDrug.getCreatedTime());
	    drugTypeCollection.setDiscarded(oldDrug.getDiscarded());
	    drugTypeCollection = drugTypeRepository.save(drugTypeCollection);
	    response = new DrugTypeAddEditResponse();
	    BeanUtil.map(drugTypeCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Drug Type");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug Type");
	}
	return response;

    }

    @Override
    public Boolean deleteDrugType(String drugTypeId, Boolean discarded) {

	Boolean response = false;
	DrugTypeCollection drugTypeCollection = null;
	try {
	    drugTypeCollection = drugTypeRepository.findOne(drugTypeId);
	    if (drugTypeCollection != null) {
		drugTypeCollection.setDiscarded(discarded);
		drugTypeCollection.setUpdatedTime(new Date());
		drugTypeCollection = drugTypeRepository.save(drugTypeCollection);
		response = true;
	    } else {
		logger.warn("Drug Type Not Found");
		throw new BusinessException(ServiceError.NotFound, "Drug Type Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Drug Type");
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
	    logger.error(e + " Error Occurred While Saving Drug Strength");
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
	    DrugStrengthUnitCollection oldDrugStrength = drugStrengthRepository.findOne(request.getId());
	    drugStrengthUnitCollection.setCreatedBy(oldDrugStrength.getCreatedBy());
	    drugStrengthUnitCollection.setCreatedTime(oldDrugStrength.getCreatedTime());
	    drugStrengthUnitCollection.setDiscarded(oldDrugStrength.getDiscarded());
	    drugStrengthUnitCollection = drugStrengthRepository.save(drugStrengthUnitCollection);
	    response = new DrugStrengthAddEditResponse();
	    BeanUtil.map(drugStrengthUnitCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Drug Strength");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug Strength");
	}
	return response;
    }

    @Override
    public Boolean deleteDrugStrength(String drugStrengthId, Boolean discarded) {

	Boolean response = false;
	DrugStrengthUnitCollection drugStrengthCollection = null;
	try {
	    drugStrengthCollection = drugStrengthRepository.findOne(drugStrengthId);
	    if (drugStrengthCollection != null) {
		drugStrengthCollection.setUpdatedTime(new Date());
		drugStrengthCollection.setDiscarded(discarded);
		drugStrengthCollection = drugStrengthRepository.save(drugStrengthCollection);
		response = true;
	    } else {
		logger.warn("Drug Strength Not Found");
		throw new BusinessException(ServiceError.NotFound, "Drug Strength Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Drug Strength");
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
	    logger.error(e + " Error Occurred While Saving Drug Dosage");
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
	    DrugDosageCollection oldDrugDosage = drugDosageRepository.findOne(request.getId());
	    drugDosageCollection.setCreatedBy(oldDrugDosage.getCreatedBy());
	    drugDosageCollection.setCreatedTime(oldDrugDosage.getCreatedTime());
	    drugDosageCollection.setDiscarded(oldDrugDosage.getDiscarded());
	    drugDosageCollection = drugDosageRepository.save(drugDosageCollection);
	    response = new DrugDosageAddEditResponse();
	    BeanUtil.map(drugDosageCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Drug Dosage");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editin Drug Dosage");
	}
	return response;
    }

    @Override
    public Boolean deleteDrugDosage(String drugDosageId, Boolean discarded) {
	Boolean response = false;
	DrugDosageCollection drugDosageCollection = null;
	try {
	    drugDosageCollection = drugDosageRepository.findOne(drugDosageId);
	    if (drugDosageCollection != null) {
		drugDosageCollection.setDiscarded(discarded);
		drugDosageCollection.setUpdatedTime(new Date());
		drugDosageCollection = drugDosageRepository.save(drugDosageCollection);
		response = true;
	    } else {
		logger.warn("Drug Dosage Not Found");
		throw new BusinessException(ServiceError.NotFound, "Drug Dosage Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Drug Dosage");
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
	    logger.error(e + " Error Occurred While Saving Drug Direction");
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
	    DrugDirectionCollection oldDrugDirection = drugDirectionRepository.findOne(request.getId());
	    drugDirectionCollection.setCreatedBy(oldDrugDirection.getCreatedBy());
	    drugDirectionCollection.setCreatedTime(oldDrugDirection.getCreatedTime());
	    drugDirectionCollection.setDiscarded(oldDrugDirection.getDiscarded());
	    drugDirectionCollection = drugDirectionRepository.save(drugDirectionCollection);
	    response = new DrugDirectionAddEditResponse();
	    BeanUtil.map(drugDirectionCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Drug Direction");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug Direction");
	}
	return response;

    }

    @Override
    public Boolean deleteDrugDirection(String drugDirectionId, Boolean discarded) {
	Boolean response = false;
	DrugDirectionCollection drugDirectionCollection = null;
	try {
	    drugDirectionCollection = drugDirectionRepository.findOne(drugDirectionId);
	    if (drugDirectionCollection != null) {
		drugDirectionCollection.setDiscarded(discarded);
		drugDirectionCollection.setUpdatedTime(new Date());
		drugDirectionCollection = drugDirectionRepository.save(drugDirectionCollection);
		response = true;
	    } else {
		logger.warn("Drug Dosage Not Found");
		throw new BusinessException(ServiceError.NotFound, "Drug Dosage Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Drug Direction");
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
	    logger.error(e + " Error Occurred While Saving Drug Duration Unit");
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
	    DrugDurationUnitCollection oldDrugDuration = drugDurationUnitRepository.findOne(request.getId());
	    drugDurationUnitCollection.setCreatedBy(oldDrugDuration.getCreatedBy());
	    drugDurationUnitCollection.setCreatedTime(oldDrugDuration.getCreatedTime());
	    drugDurationUnitCollection.setDiscarded(oldDrugDuration.getDiscarded());
	    drugDurationUnitCollection = drugDurationUnitRepository.save(drugDurationUnitCollection);
	    response = new DrugDurationUnitAddEditResponse();
	    BeanUtil.map(drugDurationUnitCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Drug Duration Unit");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug Duration Unit");
	}
	return response;

    }

    @Override
    public Boolean deleteDrugDurationUnit(String drugDurationUnitId, Boolean discarded) {
	Boolean response = false;
	DrugDurationUnitCollection drugDurationUnitCollection = null;
	try {
	    drugDurationUnitCollection = drugDurationUnitRepository.findOne(drugDurationUnitId);
	    if (drugDurationUnitCollection != null) {
		drugDurationUnitCollection.setDiscarded(discarded);
		drugDurationUnitCollection.setUpdatedTime(new Date());
		drugDurationUnitCollection = drugDurationUnitRepository.save(drugDurationUnitCollection);
		response = true;
	    } else {
		logger.warn("Drug Duration Unit Not Found");
		throw new BusinessException(ServiceError.NotFound, "Drug Duration Unit Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Drug Duration Unit");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug Duration Unit");
	}
	return response;
    }

    @Override
    public List<Prescription> getPrescriptionById(String prescriptionId) {
	Prescription prescription = null;
	List<Prescription> prescriptions = null;
	try {
	    List<PrescriptionCollection> prescriptionCollections = new ArrayList<PrescriptionCollection>();
	    PrescriptionCollection prescriptionCl = prescriptionRepository.findOne(prescriptionId);
	    if (prescriptionCl != null) {
		prescriptionCollections.add(prescriptionCl);
	    }

	    if (prescriptionCollections.isEmpty()) {
		prescriptionCollections = prescriptionRepository.findAll(prescriptionId);
	    }

	    if (prescriptionCollections != null && !prescriptionCollections.isEmpty()) {
		prescriptions = new ArrayList<Prescription>();
		for (PrescriptionCollection prescriptionCollection : prescriptionCollections) {
		    prescription = new Prescription();
		    BeanUtil.map(prescriptionCollection, prescription);
		    if (prescriptionCollection.getItems() != null && !prescriptionCollection.getItems().isEmpty()) {
			List<PrescriptionItemDetail> prescriptionItemDetails = new ArrayList<PrescriptionItemDetail>();
			for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
			    PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();
			    BeanUtil.map(prescriptionItem, prescriptionItemDetail);
			    DrugCollection drugCollection = drugRepository.findOne(prescriptionItem.getDrugId());
			    if (drugCollection != null) {
				Drug drug = new Drug();
				BeanUtil.map(drugCollection, drug);
				prescriptionItemDetail.setDrug(drug);
			    }
			    prescriptionItemDetails.add(prescriptionItemDetail);
			}
			PatientVisitCollection patientVisitCollection = patientVisitRepository.findByPrescriptionId(prescription.getId());
			if (patientVisitCollection != null)
			    prescription.setVisitId(patientVisitCollection.getId());
			prescription.setItems(prescriptionItemDetails);
		    }
		    prescriptions.add(prescription);
		}

	    } else {
		throw new BusinessException(ServiceError.NotFound, "No Prescription Found For the Given Prescription or Patient Id");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while getting prescription : " + e.getCause().getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error while getting prescription : " + e.getCause().getMessage());
	}
	return prescriptions;
    }

    @Override
    public List<Object> getPrescriptionItems(String type, String range, int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded) {

	List<Object> response = new ArrayList<Object>();

	switch (PrescriptionItems.valueOf(type.toUpperCase())) {

	case DRUG: {

	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
		response = getGlobalDrugs(page, size, updatedTime, discarded);
		break;
	    case CUSTOM:
		response = getCustomDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
		response = getCustomGlobalDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    }
	    break;
	}
	case DRUGTYPE: {
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
		response = getGlobalDrugType(page, size, updatedTime, discarded);
		break;
	    case CUSTOM:
		response = getCustomDrugType(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
		response = getCustomGlobalDrugType(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    }
	    break;
	}
	case DRUGDIRECTION: {
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
		response = getGlobalDrugDirection(page, size, updatedTime, discarded);
		break;
	    case CUSTOM:
		response = getCustomDrugDirection(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
		response = getCustomGlobalDrugDirection(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    }
	    break;
	}
	case DRUGDOSAGE: {
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
		response = getGlobalDrugDosage(page, size, updatedTime, discarded);
		break;
	    case CUSTOM:
		response = getCustomDrugDosage(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
		response = getCustomGlobalDrugDosage(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    }
	    break;
	}
	case DRUGDURATIONUNIT: {
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
		response = getGlobalDrugDurationUnit(page, size, updatedTime, discarded);
		break;
	    case CUSTOM:
		response = getCustomDrugDurationUnit(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
		response = getCustomGlobalDrugDurationUnit(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    }
	    break;
	}
	case DRUGSTRENGTHUNIT: {
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
		response = getGlobalDrugStrengthUnit(page, size, updatedTime, discarded);
		break;
	    case CUSTOM:
		response = getCustomDrugStrengthUnit(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
		response = getCustomGlobalDrugStrengthUnit(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		break;
	    }
	    break;
	}
	case LABTEST: {

	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
		response = getGlobalLabTests(page, size, updatedTime, discarded);
		break;
	    case CUSTOM:
		response = getCustomLabTests(page, size, locationId, hospitalId, updatedTime, discarded);
		break;
	    case BOTH:
		response = getCustomGlobalLabTests(page, size, locationId, hospitalId, updatedTime, discarded);
		break;
	    }
	    break;
	}

	default:
	    break;
	}
	return response;
    }

    private List<Object> getGlobalLabTests(int page, int size, String updatedTime, Boolean discarded) {
	List<Object> response = null;
	List<LabTestCollection> labTestCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0)
		labTestCollections = labTestRepository.getGlobalLabTests(new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC,
			"updatedTime"));

	    else
		labTestCollections = labTestRepository.getGlobalLabTests(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));

	    if (!labTestCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(labTestCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting LabTests");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
	}
	return response;
    }

    private List<Object> getCustomLabTests(int page, int size, String locationId, String hospitalId, String updatedTime, boolean discarded) {
	List<Object> response = null;
	List<LabTestCollection> labTestCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (locationId == null && hospitalId == null) {
		labTestCollections = new ArrayList<LabTestCollection>();
	    } else {
		if (size > 0)
		    labTestCollections = labTestRepository.getCustomLabTests(hospitalId, locationId, new Date(createdTimeStamp), discards, new PageRequest(
			    page, size, Direction.DESC, "updatedTime"));
		else
		    labTestCollections = labTestRepository.getCustomLabTests(hospitalId, locationId, new Date(createdTimeStamp), discards, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    }
	    if (!labTestCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(labTestCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting LabTests");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
	}
	return response;
    }

    private List<Object> getCustomGlobalLabTests(int page, int size, String locationId, String hospitalId, String updatedTime, boolean discarded) {
	List<Object> response = null;
	List<LabTestCollection> labTestCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (locationId == null && hospitalId == null) {
		if (size > 0)
		    labTestCollections = labTestRepository.getCustomGlobalLabTests(new Date(createdTimeStamp), discards, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    labTestCollections = labTestRepository.getCustomGlobalLabTests(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    } else {
		if (size > 0)
		    labTestCollections = labTestRepository.getCustomGlobalLabTests(hospitalId, locationId, new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    labTestCollections = labTestRepository.getCustomGlobalLabTests(hospitalId, locationId, new Date(createdTimeStamp), discards, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    }
	    if (!labTestCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(labTestCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting LabTests");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
	}
	return response;
    }

    private List<Object> getGlobalDrugs(int page, int size, String updatedTime, boolean discarded) {
	List<Object> response = null;
	List<DrugCollection> drugCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (size > 0)
		drugCollections = drugRepository.getGlobalDrugs(new Date(createdTimeStamp), discards,
			new PageRequest(page, size, Direction.DESC, "updatedTime"));

	    else
		drugCollections = drugRepository.getGlobalDrugs(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));

	    if (!drugCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drugs");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
	}
	return response;
    }

    private List<Object> getCustomDrugs(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, boolean discarded) {
	List<Object> response = null;
	List<DrugCollection> drugCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null)
		drugCollections = new ArrayList<DrugCollection>();

	    if (locationId == null && hospitalId == null) {
		if (size > 0)
		    drugCollections = drugRepository.getCustomDrugs(doctorId, new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC,
			    "updatedTime"));
		else
		    drugCollections = drugRepository.getCustomDrugs(doctorId, new Date(createdTimeStamp), discards,
			    new Sort(Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    drugCollections = drugRepository.getCustomDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discards, new PageRequest(
			    page, size, Direction.DESC, "updatedTime"));
		else
		    drugCollections = drugRepository.getCustomDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discards, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    }
	    if (!drugCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drugs");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
	}
	return response;
    }

    private List<Object> getCustomGlobalDrugs(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, boolean discarded) {
	List<Object> response = null;
	List<DrugCollection> drugCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null) {
		if (size > 0)
		    drugCollections = drugRepository.getCustomGlobalDrugs(new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC,
			    "updatedTime"));
		else
		    drugCollections = drugRepository.getCustomGlobalDrugs(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));

	    } else {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			drugCollections = drugRepository.getCustomGlobalDrugs(doctorId, new Date(createdTimeStamp), discards, new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			drugCollections = drugRepository.getCustomGlobalDrugs(doctorId, new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
				"updatedTime"));
		} else {
		    if (size > 0)
			drugCollections = drugRepository.getCustomGlobalDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			drugCollections = drugRepository.getCustomGlobalDrugs(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discards, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		}
	    }
	    if (!drugCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drugs");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
	}
	return response;
    }

    private List<Object> getGlobalDrugType(int page, int size, String updatedTime, boolean discarded) {
	List<Object> response = null;
	List<DrugTypeCollection> drugTypeCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (size > 0)
		drugTypeCollections = drugTypeRepository.getGlobalDrugType(new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC,
			"updatedTime"));
	    else
		drugTypeCollections = drugTypeRepository.getGlobalDrugType(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));

	    if (!drugTypeCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugTypeCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug Type");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Type");
	}
	return response;
    }

    private List<Object> getCustomDrugType(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, boolean discarded) {
	List<Object> response = null;
	List<DrugTypeCollection> drugTypeCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null)
		drugTypeCollections = new ArrayList<DrugTypeCollection>();
	    else {
		if (locationId == null & hospitalId == null) {
		    if (size > 0)
			drugTypeCollections = drugTypeRepository.getCustomDrugType(doctorId, new Date(createdTimeStamp), discards, new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			drugTypeCollections = drugTypeRepository.getCustomDrugType(doctorId, new Date(createdTimeStamp), discards, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			drugTypeCollections = drugTypeRepository.getCustomDrugType(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			drugTypeCollections = drugTypeRepository.getCustomDrugType(doctorId, hospitalId, locationId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "updatedTime"));
		}
	    }
	    if (!drugTypeCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugTypeCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug Type");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Type");
	}
	return response;
    }

    private List<Object> getCustomGlobalDrugType(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded) {
	List<Object> response = null;
	List<DrugTypeCollection> drugTypeCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null) {
		if (size > 0)
		    drugTypeCollections = drugTypeRepository.getCustomGlobalDrugType(new Date(createdTimeStamp), discards, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    drugTypeCollections = drugTypeRepository.getCustomGlobalDrugType(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    } else {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			drugTypeCollections = drugTypeRepository.getCustomGlobalDrugType(doctorId, new Date(createdTimeStamp), discards, new PageRequest(page,
				size, Direction.DESC, "updatedTime"));
		    else
			drugTypeCollections = drugTypeRepository.getCustomGlobalDrugType(doctorId, new Date(createdTimeStamp), discards, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			drugTypeCollections = drugTypeRepository.getCustomGlobalDrugType(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			drugTypeCollections = drugTypeRepository.getCustomGlobalDrugType(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				discards, new Sort(Sort.Direction.DESC, "updatedTime"));
		}
	    }
	    if (!drugTypeCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugTypeCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug Type");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Type");
	}
	return response;
    }

    private List<Object> getGlobalDrugDirection(int page, int size, String updatedTime, boolean discarded) {
	List<Object> response = null;
	List<DrugDirectionCollection> drugDirectionCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (size > 0)
		drugDirectionCollections = drugDirectionRepository.getGlobalDrugDirection(new Date(createdTimeStamp), discards, new PageRequest(page, size,
			Direction.DESC, "updatedTime"));
	    else
		drugDirectionCollections = drugDirectionRepository.getGlobalDrugDirection(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
			"updatedTime"));

	    if (!drugDirectionCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugDirectionCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug Direction");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
	}
	return response;
    }

    private List<Object> getCustomDrugDirection(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, boolean discarded) {
	List<Object> response = null;
	List<DrugDirectionCollection> drugDirectionCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null)
		drugDirectionCollections = new ArrayList<DrugDirectionCollection>();
	    else {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			drugDirectionCollections = drugDirectionRepository.getCustomDrugDirection(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			drugDirectionCollections = drugDirectionRepository.getCustomDrugDirection(doctorId, new Date(createdTimeStamp), discards, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			drugDirectionCollections = drugDirectionRepository.getCustomDrugDirection(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			drugDirectionCollections = drugDirectionRepository.getCustomDrugDirection(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				discards, new Sort(Sort.Direction.DESC, "updatedTime"));
		}
	    }
	    if (!drugDirectionCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugDirectionCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug Direction");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
	}
	return response;
    }

    private List<Object> getCustomGlobalDrugDirection(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded) {
	List<Object> response = null;
	List<DrugDirectionCollection> drugDirectionCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null) {
		if (size > 0)
		    drugDirectionCollections = drugDirectionRepository.getCustomGlobalDrugDirection(new Date(createdTimeStamp), discards, new PageRequest(page,
			    size, Direction.DESC, "updatedTime"));
		else
		    drugDirectionCollections = drugDirectionRepository.getCustomGlobalDrugDirection(new Date(createdTimeStamp), discards, new Sort(
			    Sort.Direction.DESC, "updatedTime"));

	    } else {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			drugDirectionCollections = drugDirectionRepository.getCustomGlobalDrugDirection(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			drugDirectionCollections = drugDirectionRepository.getCustomGlobalDrugDirection(doctorId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			drugDirectionCollections = drugDirectionRepository.getCustomGlobalDrugDirection(doctorId, hospitalId, locationId, new Date(
				createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			drugDirectionCollections = drugDirectionRepository.getCustomGlobalDrugDirection(doctorId, hospitalId, locationId, new Date(
				createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
		}
	    }
	    if (!drugDirectionCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugDirectionCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug Direction");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
	}
	return response;
    }

    private List<Object> getGlobalDrugDosage(int page, int size, String updatedTime, boolean discarded) {
	List<Object> response = null;
	List<DrugDosageCollection> drugDosageCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0)
		drugDosageCollections = drugDosageRepository.getGlobalDrugDosage(new Date(createdTimeStamp), discards, new PageRequest(page, size,
			Direction.DESC, "updatedTime"));
	    else
		drugDosageCollections = drugDosageRepository.getGlobalDrugDosage(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
			"updatedTime"));

	    if (!drugDosageCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugDosageCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug Dosage");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
	}
	return response;
    }

    private List<Object> getCustomDrugDosage(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, boolean discarded) {
	List<Object> response = null;
	List<DrugDosageCollection> drugDosageCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null) {
		drugDosageCollections = new ArrayList<DrugDosageCollection>();
	    } else {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			drugDosageCollections = drugDosageRepository.getCustomDrugDosage(doctorId, new Date(createdTimeStamp), discards, new PageRequest(page,
				size, Direction.DESC, "updatedTime"));
		    else
			drugDosageCollections = drugDosageRepository.getCustomDrugDosage(doctorId, new Date(createdTimeStamp), discards, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			drugDosageCollections = drugDosageRepository.getCustomDrugDosage(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			drugDosageCollections = drugDosageRepository.getCustomDrugDosage(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
				discards, new Sort(Sort.Direction.DESC, "updatedTime"));
		}
	    }
	    if (!drugDosageCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugDosageCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug Dosage");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
	}
	return response;
    }

    private List<Object> getCustomGlobalDrugDosage(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded) {
	List<Object> response = null;
	List<DrugDosageCollection> drugDosageCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null) {
		if (size > 0)
		    drugDosageCollections = drugDosageRepository.getCustomGlobalDrugDosage(new Date(createdTimeStamp), discards, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    drugDosageCollections = drugDosageRepository.getCustomGlobalDrugDosage(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    } else if (locationId == null && hospitalId == null) {
		if (size > 0)
		    drugDosageCollections = drugDosageRepository.getCustomGlobalDrugDosage(doctorId, new Date(createdTimeStamp), discards, new PageRequest(
			    page, size, Direction.DESC, "updatedTime"));
		else
		    drugDosageCollections = drugDosageRepository.getCustomGlobalDrugDosage(doctorId, new Date(createdTimeStamp), discards, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    drugDosageCollections = drugDosageRepository.getCustomGlobalDrugDosage(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
			    discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    drugDosageCollections = drugDosageRepository.getCustomGlobalDrugDosage(doctorId, hospitalId, locationId, new Date(createdTimeStamp),
			    discards, new Sort(Sort.Direction.DESC, "updatedTime"));

	    }
	    if (!drugDosageCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugDosageCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug Dosage");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
	}
	return response;
    }

    private List<Object> getGlobalDrugDurationUnit(int page, int size, String updatedTime, boolean discarded) {
	List<Object> response = null;
	List<DrugDurationUnitCollection> drugDurationUnitCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0)
		drugDurationUnitCollections = drugDurationUnitRepository.getGlobalDrugDurationUnit(new Date(createdTimeStamp), discards, new PageRequest(page,
			size, Direction.DESC, "updatedTime"));
	    else
		drugDurationUnitCollections = drugDurationUnitRepository.getGlobalDrugDurationUnit(new Date(createdTimeStamp), discards, new Sort(
			Sort.Direction.DESC, "updatedTime"));

	    if (!drugDurationUnitCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugDurationUnitCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug Duration Unit");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug DurationUnit");
	}
	return response;
    }

    private List<Object> getCustomDrugDurationUnit(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded) {
	List<Object> response = null;
	List<DrugDurationUnitCollection> drugDurationUnitCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null)
		drugDurationUnitCollections = new ArrayList<DrugDurationUnitCollection>();

	    else {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			drugDurationUnitCollections = drugDurationUnitRepository.getCustomDrugDurationUnit(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			drugDurationUnitCollections = drugDurationUnitRepository.getCustomDrugDurationUnit(doctorId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			drugDurationUnitCollections = drugDurationUnitRepository.getCustomDrugDurationUnit(doctorId, hospitalId, locationId, new Date(
				createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			drugDurationUnitCollections = drugDurationUnitRepository.getCustomDrugDurationUnit(doctorId, hospitalId, locationId, new Date(
				createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
		}
	    }
	    if (!drugDurationUnitCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugDurationUnitCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug Duration Unit");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Duration Unit");
	}
	return response;
    }

    private List<Object> getCustomGlobalDrugDurationUnit(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded) {
	List<Object> response = null;
	List<DrugDurationUnitCollection> drugDurationUnitCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (doctorId == null) {
		if (size > 0)
		    drugDurationUnitCollections = drugDurationUnitRepository.getCustomGlobalDrugDurationUnit(new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    drugDurationUnitCollections = drugDurationUnitRepository.getCustomGlobalDrugDurationUnit(new Date(createdTimeStamp), discards, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
	    } else if (locationId == null && hospitalId == null) {
		if (size > 0)
		    drugDurationUnitCollections = drugDurationUnitRepository.getCustomGlobalDrugDurationUnit(doctorId, new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    drugDurationUnitCollections = drugDurationUnitRepository.getCustomGlobalDrugDurationUnit(doctorId, new Date(createdTimeStamp), discards,
			    new Sort(Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    drugDurationUnitCollections = drugDurationUnitRepository.getCustomGlobalDrugDurationUnit(doctorId, hospitalId, locationId, new Date(
			    createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    drugDurationUnitCollections = drugDurationUnitRepository.getCustomGlobalDrugDurationUnit(doctorId, hospitalId, locationId, new Date(
			    createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
	    }

	    if (!drugDurationUnitCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugDurationUnitCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug Duration Unit");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug DurationUnit");
	}
	return response;
    }

    private List<Object> getGlobalDrugStrengthUnit(int page, int size, String updatedTime, boolean discarded) {
	List<Object> response = null;
	List<DrugStrengthUnitCollection> drugStrengthUnitCollections = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(updatedTime)) {
		if (discarded) {
		    if (size > 0)
			drugStrengthUnitCollections = drugStrengthRepository.getGlobalDrugStrengthUnit(new PageRequest(page, size, Direction.DESC,
				"updatedTime"));
		    else
			drugStrengthUnitCollections = drugStrengthRepository.getGlobalDrugStrengthUnit(new Sort(Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			drugStrengthUnitCollections = drugStrengthRepository.getGlobalDrugStrengthUnit(discarded, new PageRequest(page, size, Direction.DESC,
				"updatedTime"));
		    else
			drugStrengthUnitCollections = drugStrengthRepository.getGlobalDrugStrengthUnit(discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		}
	    } else {
		long createdTimeStamp = Long.parseLong(updatedTime);
		if (discarded) {
		    if (size > 0)
			drugStrengthUnitCollections = drugStrengthRepository.getGlobalDrugStrengthUnit(new Date(createdTimeStamp), new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			drugStrengthUnitCollections = drugStrengthRepository.getGlobalDrugStrengthUnit(new Date(createdTimeStamp), new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			drugStrengthUnitCollections = drugStrengthRepository.getGlobalDrugStrengthUnit(new Date(createdTimeStamp), discarded, new PageRequest(
				page, size, Direction.DESC, "updatedTime"));
		    else
			drugStrengthUnitCollections = drugStrengthRepository.getGlobalDrugStrengthUnit(new Date(createdTimeStamp), discarded, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		}
	    }
	    if (!drugStrengthUnitCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugStrengthUnitCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug Strength Unit");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug StrengthUnit");
	}
	return response;
    }

    private List<Object> getCustomDrugStrengthUnit(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded) {
	List<Object> response = null;
	List<DrugStrengthUnitCollection> drugStrengthUnitCollections = null;
	try {
	    if (doctorId == null)
		drugStrengthUnitCollections = new ArrayList<DrugStrengthUnitCollection>();
	    else if (DPDoctorUtils.anyStringEmpty(updatedTime)) {
		if (locationId == null && hospitalId == null) {
		    if (discarded) {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, new PageRequest(page, size,
				    Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, new Sort(Sort.Direction.DESC,
				    "updatedTime"));
		    } else {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, discarded, new PageRequest(page, size,
				    Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, discarded, new Sort(Sort.Direction.DESC,
				    "updatedTime"));
		    }
		} else {
		    if (discarded) {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId, locationId, new PageRequest(
				    page, size, Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId, locationId, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId, locationId, discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId, locationId, discarded,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    } else {
		long createdTimeStamp = Long.parseLong(updatedTime);
		if (locationId == null && hospitalId == null) {
		    if (discarded) {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, new Date(createdTimeStamp),
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, new Date(createdTimeStamp), new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, new Date(createdTimeStamp), discarded,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (discarded) {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId, locationId, new Date(
				    createdTimeStamp), new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId, locationId, new Date(
				    createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId, locationId, new Date(
				    createdTimeStamp), discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId, locationId, new Date(
				    createdTimeStamp), discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	    if (!drugStrengthUnitCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugStrengthUnitCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug Strength Unit");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug StrengthUnit");
	}
	return response;
    }

    private List<Object> getCustomGlobalDrugStrengthUnit(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded) {
	List<Object> response = null;
	List<DrugStrengthUnitCollection> drugStrengthUnitCollections = null;
	try {
	    if (doctorId == null) {
		if (!DPDoctorUtils.allStringsEmpty(updatedTime)) {
		    long createdTimeStamp = Long.parseLong(updatedTime);
		    if (discarded) {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(new Date(createdTimeStamp), new PageRequest(
				    page, size, Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(new Date(createdTimeStamp), new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(new Date(createdTimeStamp), discarded,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (discarded) {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.findAll(new PageRequest(page, size, Direction.DESC, "updatedTime"))
				    .getContent();
			else
			    drugStrengthUnitCollections = drugStrengthRepository.findAll(new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(discarded, new PageRequest(page, size,
				    Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(discarded, new Sort(Sort.Direction.DESC,
				    "updatedTime"));
		    }
		}
	    } else if (DPDoctorUtils.anyStringEmpty(updatedTime)) {
		if (locationId == null && hospitalId == null) {
		    if (discarded) {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, new PageRequest(page, size,
				    Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, new Sort(Sort.Direction.DESC,
				    "updatedTime"));
		    } else {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, discarded, new PageRequest(page,
				    size, Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (discarded) {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, hospitalId, locationId,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, hospitalId, locationId, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, hospitalId, locationId, discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, hospitalId, locationId, discarded,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    } else {
		long createdTimeStamp = Long.parseLong(updatedTime);
		if (locationId == null && hospitalId == null) {
		    if (discarded) {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, new Date(createdTimeStamp),
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, new Date(createdTimeStamp),
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, new Date(createdTimeStamp),
				    discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, new Date(createdTimeStamp),
				    discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (discarded) {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, hospitalId, locationId, new Date(
				    createdTimeStamp), new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, hospitalId, locationId, new Date(
				    createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, hospitalId, locationId, new Date(
				    createdTimeStamp), discarded, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    drugStrengthUnitCollections = drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, hospitalId, locationId, new Date(
				    createdTimeStamp), discarded, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	    if (!drugStrengthUnitCollections.isEmpty()) {
		response = new ArrayList<Object>();
		BeanUtil.map(drugStrengthUnitCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drug Strength Unit");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug StrengthUnit");
	}
	return response;
    }

    @Override
    public void emailPrescription(String prescriptionId, String doctorId, String locationId, String hospitalId, String emailAddress, UriInfo uriInfo) {
	try {
	    MailAttachment mailAttachment = createMailDate(prescriptionId, doctorId, locationId, hospitalId, uriInfo);
	    mailService.sendEmail(emailAddress, "Prescription", "PFA.", mailAttachment);
	} catch (MessagingException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public MailAttachment getPrescriptionMailData(String prescriptionId, String doctorId, String locationId, String hospitalId, UriInfo uriInfo) {
	return createMailDate(prescriptionId, doctorId, locationId, hospitalId, uriInfo);
    }

    private MailAttachment createMailDate(String prescriptionId, String doctorId, String locationId, String hospitalId, UriInfo uriInfo) {
	PrescriptionCollection prescriptionCollection = null;
	Map<String, Object> parameters = new HashMap<String, Object>();
	MailAttachment mailAttachment = null;
	List<PrescriptionJasperDetails> prescriptionItems = new ArrayList<PrescriptionJasperDetails>();
	PatientCollection patient = null;
	PatientAdmissionCollection patientAdmission = null;
	UserCollection user = null;
	EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
	try {
	    prescriptionCollection = prescriptionRepository.findOne(prescriptionId);
	    if (prescriptionCollection != null) {
		if (prescriptionCollection.getDoctorId() != null && prescriptionCollection.getHospitalId() != null
			&& prescriptionCollection.getLocationId() != null) {
		    if (prescriptionCollection.getDoctorId().equals(doctorId) && prescriptionCollection.getHospitalId().equals(hospitalId)
			    && prescriptionCollection.getLocationId().equals(locationId)) {
			int no = 0;
			for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
			    if (prescriptionItem != null && prescriptionItem.getDrugId() != null) {
				DrugCollection drug = drugRepository.findOne(prescriptionItem.getDrugId());
				if (drug != null) {
				    String drugType = drug.getDrugType() != null ? (drug.getDrugType().getType() != null ? drug.getDrugType().getType() + " "
					    : "") : "";
				    String drugName = drug.getDrugName() != null ? drug.getDrugName() : "";
				    drugName = (drugType + drugName) == "" ? "----" : drugType + " " + drugName;
				    String durationValue = prescriptionItem.getDuration() != null ? (prescriptionItem.getDuration().getValue() != null ? prescriptionItem
					    .getDuration().getValue() : "")
					    : "";
				    String durationUnit = prescriptionItem.getDuration() != null ? (prescriptionItem.getDuration().getDurationUnit() != null ? prescriptionItem
					    .getDuration().getDurationUnit().getUnit()
					    : "")
					    : "";

				    String directions = "";
				    if (prescriptionItem.getDirection() != null)
					for (DrugDirection drugDirection : prescriptionItem.getDirection()) {
					    if (drugDirection.getDirection() != null)
						if (directions == "")
						    directions = directions + (drugDirection.getDirection());
						else
						    directions = directions + "," + (drugDirection.getDirection());
					}
				    String duration = "";
				    if (durationValue == "" && durationValue == "")
					duration = "----";
				    else
					duration = durationValue + " " + durationUnit;
				    PrescriptionJasperDetails prescriptionJasperDetails = new PrescriptionJasperDetails(++no, drugName,
					    prescriptionItem.getDosage() != null ? prescriptionItem.getDosage() : "----", duration,
					    directions.isEmpty() ? "----" : directions,
					    prescriptionItem.getInstructions() != null ? prescriptionItem.getInstructions() : "----");

				    prescriptionItems.add(prescriptionJasperDetails);
				}
			    }
			    parameters.put("prescriptionItems", prescriptionItems);
			}

			patientAdmission = patientAdmissionRepository.findByUserIdAndDoctorId(prescriptionCollection.getPatientId(), doctorId);
			user = userRepository.findOne(prescriptionCollection.getPatientId());
			patient = patientRepository.findByUserId(prescriptionCollection.getPatientId());

			emailTrackCollection.setDoctorId(doctorId);
			emailTrackCollection.setHospitalId(hospitalId);
			emailTrackCollection.setLocationId(locationId);
			emailTrackCollection.setType(ComponentType.PRESCRIPTIONS.getType());
			emailTrackCollection.setSubject("Prescription");
			if (user != null) {
			    emailTrackCollection.setPatientName(user.getFirstName());
			    emailTrackCollection.setPatientId(user.getId());
			}
			parameters.put("prescriptionId", prescriptionId);
			parameters.put("advice", prescriptionCollection.getAdvice() != null ? prescriptionCollection.getAdvice() : "----");
			if (prescriptionCollection.getLabTests() != null && !prescriptionCollection.getLabTests().isEmpty()) {
			    String labTest = "";
			    int i = 1;
			    for (LabTest labTests : prescriptionCollection.getLabTests()) {
				if (labTests.getTest() != null && labTests.getTest().getTestName() != null) {
				    labTest = labTest + i + ") " + labTests.getTest().getTestName() + "<br>";
				    i++;
				}
			    }
			    parameters.put("labTest", labTest);
			} else {
			    parameters.put("labTest", null);
			}

		    } else {
			logger.warn("Prescription Id, doctorId, location Id, hospital Id does not match");
			throw new BusinessException(ServiceError.NotFound, "Prescription Id, doctorId, location Id, hospital Id does not match");
		    }
		}

		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(doctorId, locationId, hospitalId,
			ComponentType.PRESCRIPTIONS.getType());
		DBObject printId = new BasicDBObject();
		if (printSettings == null) {
		    printSettings = printSettingsRepository.getSettings(doctorId, locationId, hospitalId, ComponentType.ALL.getType());
		    if (printSettings != null) {
			printId.put("$oid", printSettings.getId());

		    }
		} else
		    printId.put("$oid", printSettings.getId());

		parameters.put("printSettingsId", Arrays.asList(printId));
		String headerLeftText = "", headerRightText = "", footerBottomText = "";
		String patientName = "", dob = "", gender = "", mobileNumber = "";
		if (printSettings != null) {
		    if (printSettings.getHeaderSetup() != null) {
			for (PrintSettingsText str : printSettings.getHeaderSetup().getTopLeftText()) {
			    boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
			    boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
			    String text = str.getText();
			    if (isBold && isItalic)
				text = "<b><i>" + text + "</i></b>";
			    else if (isBold)
				text = "<b>" + text + "</b>";
			    else if (isItalic)
				text = "<i>" + text + "</i>";

			    if (headerLeftText.isEmpty())
				headerLeftText = "<span style='font-size:" + str.getFontSize() + ";'>" + text + "</span>";
			    else
				headerLeftText = headerLeftText + "<br/>" + "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
			}

			for (PrintSettingsText str : printSettings.getHeaderSetup().getTopRightText()) {
			    boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
			    boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
			    String text = str.getText();
			    if (isBold && isItalic)
				text = "<b><i>" + text + "</i></b>";
			    else if (isBold)
				text = "<b>" + text + "</b>";
			    else if (isItalic)
				text = "<i>" + text + "</i>";

			    if (headerRightText.isEmpty())
				headerRightText = "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
			    else
				headerRightText = headerRightText + "<br/>" + "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
			}

		    }
		    if (printSettings.getFooterSetup() != null) {
			if (printSettings.getFooterSetup().getCustomFooter())
			    for (PrintSettingsText str : printSettings.getFooterSetup().getBottomText()) {
				boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
				boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
				String text = str.getText();
				if (isBold && isItalic)
				    text = "<b><i>" + text + "</i></b>";
				else if (isBold)
				    text = "<b>" + text + "</b>";
				else if (isItalic)
				    text = "<i>" + text + "</i>";

				if (footerBottomText.isEmpty())
				    footerBottomText = "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
				else
				    footerBottomText = footerBottomText + "" + "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
			    }
			UserCollection doctorUser = userRepository.findOne(doctorId);
			if (doctorUser != null)
			    parameters.put("footerSignature", doctorUser.getTitle() + " " + doctorUser.getFirstName());
		    }
		}
		patientName = "Patient Name: " + (user != null ? user.getFirstName() : "--") + "<br>";
		dob = "Patient Age: " + ((patient != null && patient.getDob() != null) ? (patient.getDob().getAge() + "years") : "--") + "<br>";
		gender = "Patient Gender: " + (patient != null ? patient.getGender() : "--") + "<br>";
		mobileNumber = "Mobile Number: " + (user != null ? user.getMobileNumber() : "--") + "<br>";

		parameters.put("patientLeftText", patientName + "Patient Id: " + (patient != null ? patient.getPID() : "--") + "<br>" + dob + gender);
		parameters
			.put("patientRightText",
				mobileNumber
					+ "Reffered By: "
					+ (patientAdmission != null && patientAdmission.getReferredBy() != null && patientAdmission.getReferredBy() != "" ? patientAdmission
						.getReferredBy() : "--") + "<br>" + "Date: " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		parameters.put("headerLeftText", headerLeftText);
		parameters.put("headerRightText", headerRightText);
		parameters.put("footerBottomText", footerBottomText);

		LocationCollection location = locationRepository.findOne(locationId);
		if (location != null)
		    parameters.put("logoURL", getFinalImageURL(location.getLogoUrl(), uriInfo));

		String layout = printSettings != null ? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
			: "PORTRAIT";
		String pageSize = printSettings != null ? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4") : "A4";
		String margins = printSettings != null ? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getMargins() : null) : null;

		String path = jasperReportService.createPDF(parameters, "mongo-prescription", layout, pageSize, margins, user.getFirstName() + new Date()
			+ "PRESCRIPTION");
		FileSystemResource file = new FileSystemResource(path);
		mailAttachment = new MailAttachment();
		mailAttachment.setAttachmentName(file.getFilename());
		mailAttachment.setFileSystemResource(file);
		emailTackService.saveEmailTrack(emailTrackCollection);
	    } else {
		logger.warn("Prescription not found.Please check prescriptionId.");
		throw new BusinessException(ServiceError.Unknown, "Prescription not found.Please check prescriptionId.");
	    }
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return mailAttachment;
    }

    @Override
    public void smsPrescription(String prescriptionId, String doctorId, String locationId, String hospitalId, String mobileNumber) {
	PrescriptionCollection prescriptionCollection = null;
	try {
	    prescriptionCollection = prescriptionRepository.findOne(prescriptionId);
	    if (prescriptionCollection != null) {
		if (prescriptionCollection.getDoctorId() != null && prescriptionCollection.getHospitalId() != null
			&& prescriptionCollection.getLocationId() != null) {
		    if (prescriptionCollection.getDoctorId().equals(doctorId) && prescriptionCollection.getHospitalId().equals(hospitalId)
			    && prescriptionCollection.getLocationId().equals(locationId)) {

			UserCollection userCollection = userRepository.findOne(prescriptionCollection.getPatientId());
			PatientCollection patientCollection = patientRepository.findByUserId(prescriptionCollection.getPatientId());
			// LocationCollection location =
			// locationRepository.findOne(locationId);
			if (patientCollection != null) {
			    String prescriptionDetails = "";
			    int i = 0;
			    for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
				if (prescriptionItem != null && prescriptionItem.getDrugId() != null) {
				    DrugCollection drug = drugRepository.findOne(prescriptionItem.getDrugId());
				    if (drug != null) {
					i++;

					String drugType = drug.getDrugType() != null ? (drug.getDrugType().getType() != null ? drug.getDrugType().getType()
						: "") : "";
					String drugName = drug.getDrugName() != null ? drug.getDrugName() : "";
					String strengthValue = drug.getStrength() != null ? (drug.getStrength().getValue() != null ? drug.getStrength()
						.getValue() : "") : "";
					String strengthUnit = drug.getStrength() != null ? (drug.getStrength().getStrengthUnit() != null ? drug.getStrength()
						.getStrengthUnit().getUnit() : "") : "";

					String durationValue = prescriptionItem.getDuration() != null ? (prescriptionItem.getDuration().getValue() != null ? prescriptionItem
						.getDuration().getValue() : "")
						: "";
					String durationUnit = prescriptionItem.getDuration() != null ? (prescriptionItem.getDuration().getDurationUnit() != null ? prescriptionItem
						.getDuration().getDurationUnit().getUnit()
						: "")
						: "";

					List<String> directions = new ArrayList<String>();
					if (prescriptionItem.getDirection() != null && !prescriptionItem.getDirection().isEmpty()) {
					    for (DrugDirection drugDirection : prescriptionItem.getDirection()) {
						if (drugDirection.getDirection() != null)
						    directions.add(drugDirection.getDirection());
					    }
					}
					prescriptionDetails = prescriptionDetails + i + "." + drugType + " " + drugName + strengthValue + strengthUnit + " "
						+ prescriptionItem.getDosage() + " " + durationValue + " " + durationUnit + " " + directions;
				    }
				}
			    }
			    SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
			    smsTrackDetail.setDoctorId(doctorId);
			    smsTrackDetail.setHospitalId(hospitalId);
			    smsTrackDetail.setLocationId(locationId);

			    SMSDetail smsDetail = new SMSDetail();
			    smsDetail.setPatientId(prescriptionCollection.getPatientId());
			    if (userCollection != null)
				smsDetail.setPatientName(userCollection.getFirstName());
			    SMS sms = new SMS();
			    sms.setSmsText("PID : " + patientCollection.getPID() + ", " + prescriptionDetails);// location.getLocationName()+

			    SMSAddress smsAddress = new SMSAddress();
			    smsAddress.setRecipient(mobileNumber);
			    sms.setSmsAddress(smsAddress);

			    smsDetail.setSms(sms);
			    smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
			    List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
			    smsDetails.add(smsDetail);
			    smsTrackDetail.setSmsDetails(smsDetails);
			    sMSServices.sendSMS(smsTrackDetail, true);

			}
		    } else {
			logger.warn("Prescription not found.Please check prescriptionId.");
			throw new BusinessException(ServiceError.Unknown, "Prescription not found.Please check prescriptionId.");
		    }
		}
	    }
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public LabTest addLabTest(LabTest request) {
	LabTest response = null;
	LabTestCollection labTestCollection = new LabTestCollection();
	BeanUtil.map(request, labTestCollection);
	try {
	    Date createdTime = new Date();
	    labTestCollection.setCreatedTime(createdTime);
	    if (request.getTest() != null) {
		DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository.findOne(request.getTest().getId());
		DiagnosticTest diagnosticTest = new DiagnosticTest();
		BeanUtil.map(diagnosticTestCollection, diagnosticTest);
		labTestCollection.setTest(diagnosticTest);
	    }
	    labTestCollection = labTestRepository.save(labTestCollection);
	    response = new LabTest();
	    BeanUtil.map(labTestCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Lab Test");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Lab Test");
	}
	return response;
    }

    @Override
    public LabTest editLabTest(LabTest request) {
	LabTest response = null;
	LabTestCollection labTestCollection = new LabTestCollection();
	BeanUtil.map(request, labTestCollection);
	try {
	    LabTestCollection oldLabTest = labTestRepository.findOne(request.getId());
	    labTestCollection.setCreatedBy(oldLabTest.getCreatedBy());
	    labTestCollection.setCreatedTime(oldLabTest.getCreatedTime());
	    labTestCollection.setDiscarded(oldLabTest.getDiscarded());
	    if (request.getTest() != null) {
		DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository.findOne(request.getTest().getId());
		DiagnosticTest diagnosticTest = new DiagnosticTest();
		BeanUtil.map(diagnosticTestCollection, diagnosticTest);
		labTestCollection.setTest(diagnosticTest);
	    }
	    labTestCollection = labTestRepository.save(labTestCollection);
	    response = new LabTest();
	    BeanUtil.map(labTestCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Lab Test");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Lab Test");
	}
	return response;

    }

    @Override
    public Boolean deleteLabTest(String labTestId, String hospitalId, String locationId, Boolean discarded) {
	Boolean response = false;
	LabTestCollection labTestCollection = null;
	try {
	    labTestCollection = labTestRepository.findOne(labTestId);
	    if (labTestCollection != null) {
		if (labTestCollection.getHospitalId() != null && labTestCollection.getLocationId() != null) {
		    if (labTestCollection.getHospitalId().equals(hospitalId) && labTestCollection.getLocationId().equals(locationId)) {
			labTestCollection.setDiscarded(discarded);
			labTestCollection.setUpdatedTime(new Date());
			labTestCollection = labTestRepository.save(labTestCollection);
			response = true;
		    } else {
			logger.warn("Invalid Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.NotAuthorized, "Invalid Hospital Id, Or Location Id");
		    }
		} else {
		    logger.warn("Cannot Delete Global Lab Test");
		    throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Lab Test");
		}
	    } else {
		logger.warn("Lab Test Not Found");
		throw new BusinessException(ServiceError.NotFound, "Lab Test Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Lab Test");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Lab Test");
	}
	return response;

    }

    @Override
    public Boolean deleteLabTest(String labTestId, Boolean discarded) {
	Boolean response = false;
	LabTestCollection labTestCollection = null;
	try {
	    labTestCollection = labTestRepository.findOne(labTestId);
	    if (labTestCollection != null) {
		labTestCollection.setUpdatedTime(new Date());
		labTestCollection.setDiscarded(discarded);
		labTestCollection = labTestRepository.save(labTestCollection);
		response = true;
	    } else {
		logger.warn("Lab Test Not Found");
		throw new BusinessException(ServiceError.NotFound, "Lab Test Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Lab Test");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Lab Test");
	}
	return response;
    }

    @Override
    public LabTest getLabTestById(String labTestId) {
	LabTest response = null;
	try {
	    LabTestCollection labTestCollection = labTestRepository.findOne(labTestId);
	    if (labTestCollection != null) {
		response = new LabTest();
		BeanUtil.map(labTestCollection, response);
	    } else {
		logger.warn("Lab Test not found. Please check Lab Test Id");
		throw new BusinessException(ServiceError.Unknown, "Lab Test not found. Please check Lab Test Id");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Lab Test");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Lab Test");
	}
	return response;

    }

    @Override
    public void importDrug() {
	String csvFile = "/home/suresh/drug_solr.csv";
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";

	try {

	    br = new BufferedReader(new FileReader(csvFile));
	    int i = 0;
	    while ((line = br.readLine()) != null) {
		System.out.println(i++);
		String[] obj = line.split(cvsSplitBy);
		String drugType = obj[1];
		// DrugTypeCollection drugTypeCollection = null;
		// if (drugType.equals("TAB")) {
		// drugTypeCollection = drugTypeRepository.findByType("TABLET");
		// } else if (drugType.equals("CAP")) {
		// drugTypeCollection =
		// drugTypeRepository.findByType("CAPSULE");
		// } else if (drugType.equals("OINT")) {
		// drugTypeCollection =
		// drugTypeRepository.findByType("OINTMENT");
		// } else if (drugType.equals("SYP")) {
		// drugTypeCollection = drugTypeRepository.findByType("SYRUP");
		// }
		//
		// DrugType type = null;
		// if (drugTypeCollection != null) {
		// type = new DrugType();
		// drugTypeCollection.setType(drugType);
		// BeanUtil.map(drugTypeCollection, type);
		//
		// }
		//
		// DrugCollection drugCollection = new DrugCollection();
		// drugCollection.setCreatedBy("ADMIN");
		// drugCollection.setCreatedTime(new Date());
		// drugCollection.setDiscarded(false);
		// drugCollection.setDrugName(obj[0]);
		// drugCollection.setDrugType(type);
		// drugCollection.setDoctorId(null);
		// drugCollection.setHospitalId(null);
		// drugCollection.setLocationId(null);
		//
		// drugRepository.save(drugCollection);

		SolrDrugDocument solrDrugDocument = new SolrDrugDocument();
		solrDrugDocument.setDrugName(obj[1]);
		solrDrugDocument.setId(obj[0]);
		solrDrugDocument.setDescription(null);
		solrDrugDocument.setDoctorId(null);
		solrDrugDocument.setHospitalId(null);
		solrDrugDocument.setLocationId(null);
		solrDrugDocument.setDrugCode(null);

	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    if (br != null) {
		try {
		    br.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}

	System.out.println("Done");

    }

    private String getFinalImageURL(String imageURL, UriInfo uriInfo) {
	if (imageURL != null && uriInfo != null) {
	    String finalImageURL = uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath);
	    return finalImageURL + imageURL;
	} else
	    return null;
    }

    public boolean containsIgnoreCase(String str, List<String> list) {
	if (list != null && !list.isEmpty())
	    for (String i : list) {
		if (i.equalsIgnoreCase(str))
		    return true;
	    }
	return false;
    }

    @Override
    public List<DiagnosticTestCollection> getDiagnosticTest() {
	List<DiagnosticTestCollection> response = null;
	try {
	    response = diagnosticTestRepository.findAll();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting LabTests");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
	}
	return response;
    }
}