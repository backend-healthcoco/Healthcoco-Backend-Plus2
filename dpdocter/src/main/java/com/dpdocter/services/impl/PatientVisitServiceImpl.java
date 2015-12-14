package com.dpdocter.services.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.ClinicalNotesJasperDetails;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.PatientVisit;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionItemDetail;
import com.dpdocter.beans.PrescriptionJasperDetails;
import com.dpdocter.beans.PrintSettingsText;
import com.dpdocter.beans.Records;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.ComplaintCollection;
import com.dpdocter.collections.DiagnosisCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.InvestigationCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.PatientAdmissionCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.FONTSTYLE;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.enums.VitalSignsUnit;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.ComplaintRepository;
import com.dpdocter.repository.DiagnosisRepository;
import com.dpdocter.repository.DiagramsRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.InvestigationRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.NotesRepository;
import com.dpdocter.repository.ObservationRepository;
import com.dpdocter.repository.PatientAdmissionRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddMultipleDataRequest;
import com.dpdocter.response.PatientVisitResponse;
import com.dpdocter.response.PrescriptionAddEditResponse;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.RecordsService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Service
public class PatientVisitServiceImpl implements PatientVisitService {

    private static Logger logger = Logger.getLogger(PatientVisitServiceImpl.class.getName());

    @Autowired
    private PatientVisitRepository patientVisitRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ContactsServiceImpl contactsService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ClinicalNotesService clinicalNotesService;

    @Autowired
    private PrescriptionServices prescriptionServices;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private ClinicalNotesRepository clinicalNotesRepository;

    @Autowired
    private RecordsService recordsService;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private JasperReportService jasperReportService;

    @Autowired
    private MailService mailService;

    @Autowired
    private PrintSettingsRepository printSettingsRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailTackService emailTackService;

    @Autowired
    private PatientAdmissionRepository patientAdmissionRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private ObservationRepository observationRepository;

    @Autowired
    private InvestigationRepository investigationRepository;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private DiagramsRepository diagramsRepository;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @Override
    public String addRecord(Object details, VisitedFor visitedFor, String visitId) {
	PatientVisitCollection patientTrackCollection = new PatientVisitCollection();
	try {

	    BeanUtil.map(details, patientTrackCollection);
	    String id = patientTrackCollection.getId();

	    if (visitId != null)
		patientTrackCollection = patientVisitRepository.findOne(visitId);
	    else
		patientTrackCollection.setId(null);

	    if (patientTrackCollection.getId() == null) {
		patientTrackCollection.setCreatedTime(new Date());
		UserCollection userCollection = userRepository.findOne(patientTrackCollection.getDoctorId());
		if (userCollection != null) {
		    patientTrackCollection.setCreatedBy(userCollection.getFirstName());
		}
	    }

	    if (patientTrackCollection.getVisitedFor() != null) {
		patientTrackCollection.getVisitedFor().add(visitedFor);
	    } else {
		List<VisitedFor> visitedforList = new ArrayList<VisitedFor>();
		visitedforList.add(visitedFor);
		patientTrackCollection.setVisitedFor(visitedforList);
	    }

	    patientTrackCollection.setVisitedTime(new Date());
	    if (visitedFor.equals(VisitedFor.PRESCRIPTION)) {
		if (patientTrackCollection.getPrescriptionId() == null) {
		    List<String> prescriptionId = new ArrayList<String>();
		    prescriptionId.add(id);
		    patientTrackCollection.setPrescriptionId(prescriptionId);
		} else {
		    patientTrackCollection.getPrescriptionId().add(id);
		}
	    } else if (visitedFor.equals(VisitedFor.CLINICAL_NOTES)) {
		if (patientTrackCollection.getClinicalNotesId() == null) {
		    List<String> clinicalNotes = new ArrayList<String>();
		    clinicalNotes.add(id);
		    patientTrackCollection.setClinicalNotesId(clinicalNotes);
		} else {
		    patientTrackCollection.getClinicalNotesId().add(id);
		}
	    } else if (visitedFor.equals(VisitedFor.REPORTS)) {
		if (patientTrackCollection.getRecordId() == null) {
		    List<String> recordId = new ArrayList<String>();
		    recordId.add(id);
		    patientTrackCollection.setRecordId(recordId);
		} else {
		    patientTrackCollection.getRecordId().add(id);
		}
	    }

	    patientTrackCollection = patientVisitRepository.save(patientTrackCollection);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while saving patient visit record : " + e.getCause().getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error while saving patient visit record : " + e.getCause().getMessage());
	}
	return patientTrackCollection.getId();
    }

    @Override
    public boolean addRecord(String patientId, String doctorId, String locationId, String hospitalId, VisitedFor visitedFor) {
	boolean response = false;
	try {
	    PatientVisitCollection patientTrackCollection = patientVisitRepository.find(doctorId, locationId, hospitalId, patientId);
	    PatientCollection patientCollection = patientRepository.findByUserId(patientId);
	    UserCollection userCollection = userRepository.findOne(doctorId);

	    if (patientTrackCollection == null) {
		patientTrackCollection = new PatientVisitCollection();
		patientTrackCollection.setDoctorId(doctorId);
		patientTrackCollection.setLocationId(locationId);
		patientTrackCollection.setHospitalId(hospitalId);
		patientTrackCollection.setVisitedTime(new Date());
		patientTrackCollection.setCreatedTime(new Date());
		if (patientCollection != null) {
		    patientTrackCollection.setPatientId(patientCollection.getUserId());
		}
		if (userCollection != null) {
		    if (userCollection.getFirstName() != null) {
			patientTrackCollection.setCreatedBy(userCollection.getFirstName());
		    }
		}

		List<VisitedFor> visitedforList = new ArrayList<VisitedFor>();
		visitedforList.add(visitedFor);
		patientTrackCollection.setVisitedFor(visitedforList);
	    } else {
		patientTrackCollection.setVisitedTime(new Date());
		patientTrackCollection.getVisitedFor().add(visitedFor);
	    }
	    patientVisitRepository.save(patientTrackCollection);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while saving patient visit record : " + e.getCause().getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error while saving patient visit record : " + e.getCause().getMessage());
	}
	return response;
    }

    @Override
    public DoctorContactsResponse recentlyVisited(String doctorId, String locationId, String hospitalId, int page, int size) {
	DoctorContactsResponse response = null;
	try {

	    Aggregation aggregation = null;
	    int totalSize = 0;
	    if (size > 0)
		aggregation = Aggregation.newAggregation(
			Aggregation.match((Criteria.where("doctorId").is(doctorId).andOperator(Criteria.where("locationId").is(locationId)
				.andOperator(Criteria.where("hospitalId").is(hospitalId))))), Aggregation.sort(new Sort(Sort.Direction.DESC, "visitedTime")),
			Aggregation.group("patientId").max("visitedTime").as("visitedTime"), Aggregation.skip((page) * size), Aggregation.limit(size));

	    else
		aggregation = Aggregation.newAggregation(
			Aggregation.match((Criteria.where("doctorId").is(doctorId).andOperator(Criteria.where("locationId").is(locationId)
				.andOperator(Criteria.where("hospitalId").is(hospitalId))))), Aggregation.sort(new Sort(Sort.Direction.DESC, "visitedTime")),
			Aggregation.group("patientId").max("visitedTime").as("visitedTime"));

	    AggregationResults<PatientVisit> groupResults = mongoTemplate.aggregate(aggregation, PatientVisitCollection.class, PatientVisit.class);
	    List<PatientVisit> results = groupResults.getMappedResults();

	    if (results != null && !results.isEmpty()) {
		@SuppressWarnings("unchecked")
		List<String> patientIds = (List<String>) CollectionUtils.collect(results, new BeanToPropertyValueTransformer("id"));
		List<PatientCard> patientCards = contactsService.getSpecifiedPatientCards(patientIds, doctorId, locationId, hospitalId);

		Aggregation aggregationCount = Aggregation.newAggregation(
			Aggregation.match((Criteria.where("doctorId").is(doctorId).andOperator(Criteria.where("locationId").is(locationId)
				.andOperator(Criteria.where("hospitalId").is(hospitalId))))), Aggregation.group("patientId"));

		groupResults = mongoTemplate.aggregate(aggregationCount, PatientVisitCollection.class, PatientVisit.class);
		results = groupResults.getMappedResults();

		totalSize = results.size();
		response = new DoctorContactsResponse();
		response.setPatientCards(patientCards);
		response.setTotalSize(totalSize);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while recently visited patients record : " + e.getCause().getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error while getting recently visited patients record : " + e.getCause().getMessage());
	}
	return response;
    }

    @Override
    public DoctorContactsResponse mostVisited(String doctorId, String locationId, String hospitalId, int page, int size) {
	DoctorContactsResponse response = null;
	try {
	    Criteria matchCriteria = Criteria.where("doctorId").is(doctorId).and("locationId").is(locationId).and("hospitalId").is(hospitalId);
	    Aggregation aggregation;

	    if (size > 0) {
		aggregation = Aggregation.newAggregation(Aggregation.match(matchCriteria), Aggregation.group("patientId").count().as("total"),
			Aggregation.sort(Sort.Direction.DESC, "total"), Aggregation.skip(page * size), Aggregation.limit(size));
	    } else {
		aggregation = Aggregation.newAggregation(Aggregation.match(matchCriteria), Aggregation.group("patientId").count().as("total"), Aggregation
			.project("total").and("patientId").previousOperation(), Aggregation.sort(Sort.Direction.DESC, "total"));
	    }

	    Aggregation aggregationCount = Aggregation.newAggregation(Aggregation.match(matchCriteria), Aggregation.group("patientId").count().as("total"),
		    Aggregation.project("total").and("patientId").previousOperation(), Aggregation.sort(Sort.Direction.DESC, "total"));

	    AggregationResults<PatientVisitCollection> aggregationResults = mongoTemplate.aggregate(aggregation, PatientVisitCollection.class,
		    PatientVisitCollection.class);

	    List<PatientVisitCollection> patientTrackCollections = aggregationResults.getMappedResults();

	    if (patientTrackCollections != null && !patientTrackCollections.isEmpty()) {
		@SuppressWarnings("unchecked")
		List<String> patientIds = (List<String>) CollectionUtils.collect(patientTrackCollections, new BeanToPropertyValueTransformer("id"));
		List<PatientCard> patientCards = contactsService.getSpecifiedPatientCards(patientIds, doctorId, locationId, hospitalId);
		int totalSize = mongoTemplate.aggregate(aggregationCount, PatientVisitCollection.class, PatientVisitCollection.class).getMappedResults().size();
		response = new DoctorContactsResponse();
		response.setPatientCards(patientCards);
		response.setTotalSize(totalSize);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while getting most visited patients record : " + e.getCause().getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error while getting most visited patients record : " + e.getCause().getMessage());
	}
	return response;
    }

    @Override
    public PatientVisitResponse addMultipleData(AddMultipleDataRequest request, UriInfo uriInfo) {
	PatientVisitResponse response = new PatientVisitResponse();
	try {
	    BeanUtil.map(request, response);
	    if (request.getClinicalNote() != null) {
		ClinicalNotes clinicalNotes = clinicalNotesService.addNotes(request.getClinicalNote());
		String visitId = addRecord(clinicalNotes, VisitedFor.CLINICAL_NOTES, request.getVisitId());
		clinicalNotes.setVisitId(visitId);
		request.setVisitId(visitId);
		List<ClinicalNotes> list = new ArrayList<ClinicalNotes>();
		list.add(clinicalNotes);
		response.setClinicalNotes(list);
	    }

	    if (request.getPrescription() != null) {
		PrescriptionAddEditResponse prescriptionResponse = prescriptionServices.addPrescription(request.getPrescription());
		Prescription prescription = new Prescription();
		BeanUtil.map(prescriptionResponse, prescription);

		if (prescriptionResponse.getItems() != null) {
		    List<PrescriptionItemDetail> prescriptionItemDetailsList = new ArrayList<PrescriptionItemDetail>();
		    for (PrescriptionItem prescriptionItem : prescriptionResponse.getItems()) {
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
		}
		if (prescriptionResponse != null) {
		    String visitId = addRecord(prescriptionResponse, VisitedFor.PRESCRIPTION, request.getVisitId());
		    prescriptionResponse.setVisitId(visitId);
		    request.setVisitId(visitId);
		    List<Prescription> list = new ArrayList<Prescription>();
		    list.add(prescription);
		    response.setPrescriptions(list);
		}
	    }

	    if (request.getRecord() != null) {
		Records records = recordsService.addRecord(request.getRecord());

		if (records != null) {
		    records.setRecordsUrl(getFinalImageURL(records.getRecordsUrl(), uriInfo));
		    String visitId = addRecord(records, VisitedFor.REPORTS, request.getVisitId());
		    records.setVisitId(visitId);
		    request.setVisitId(visitId);
		    List<Records> list = new ArrayList<Records>();
		    list.add(records);
		    response.setRecords(list);
		}
	    }

	    PatientVisitCollection patientVisitCollection = patientVisitRepository.findOne(request.getVisitId());
	    if (patientVisitCollection != null) {
		response.setId(patientVisitCollection.getId());
		response.setVisitedFor(patientVisitCollection.getVisitedFor());
		response.setVisitedTime(patientVisitCollection.getVisitedTime());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while adding patient Visit : " + e.getCause().getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error while adding patient Visit : " + e.getCause().getMessage());
	}
	return response;
    }

    @Override
    public List<PatientVisitResponse> getVisit(String doctorId, String locationId, String hospitalId, String patientId, int page, int size,
	    Boolean isOTPVerified, String updatedTime, UriInfo uriInfo) {
	List<PatientVisitResponse> response = null;
	List<PatientVisitCollection> patientVisitCollections = null;
	try {

	    long createdTimestamp = Long.parseLong(updatedTime);
	    if (!isOTPVerified) {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			patientVisitCollections = patientVisitRepository.find(doctorId, patientId, new Date(createdTimestamp), new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			patientVisitCollections = patientVisitRepository.find(doctorId, patientId, new Date(createdTimestamp), new Sort(Sort.Direction.DESC,
				"updatedTime"));
		} else {
		    if (size > 0)
			patientVisitCollections = patientVisitRepository.find(doctorId, locationId, hospitalId, patientId, new Date(createdTimestamp),
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			patientVisitCollections = patientVisitRepository.find(doctorId, locationId, hospitalId, patientId, new Date(createdTimestamp),
				new Sort(Sort.Direction.DESC, "updatedTime"));
		}
	    } else {
		if (size > 0)
		    patientVisitCollections = patientVisitRepository.find(patientId, new Date(createdTimestamp), new PageRequest(page, size, Direction.DESC,
			    "updatedTime"));
		else
		    patientVisitCollections = patientVisitRepository.find(patientId, new Date(createdTimestamp), new Sort(Sort.Direction.DESC, "updatedTime"));
	    }
	    if (patientVisitCollections != null) {
		response = new ArrayList<PatientVisitResponse>();

		for (PatientVisitCollection patientVisitCollection : patientVisitCollections) {
		    PatientVisitResponse patientVisitResponse = new PatientVisitResponse();
		    BeanUtil.map(patientVisitCollection, patientVisitResponse);

		    if (patientVisitCollection.getPrescriptionId() != null) {
			List<Prescription> prescriptions = prescriptionServices.getPrescriptionsByIds(patientVisitCollection.getPrescriptionId());
			patientVisitResponse.setPrescriptions(prescriptions);
		    }

		    if (patientVisitCollection.getClinicalNotesId() != null) {
			List<ClinicalNotes> clinicalNotes = new ArrayList<ClinicalNotes>();
			for (String clinicalNotesId : patientVisitCollection.getClinicalNotesId()) {
			    ClinicalNotes clinicalNote = clinicalNotesService.getNotesById(clinicalNotesId);
			    if (clinicalNote != null) {
				if (clinicalNote.getDiagrams() != null && !clinicalNote.getDiagrams().isEmpty()) {
				    clinicalNote.setDiagrams(getFinalDiagrams(clinicalNote.getDiagrams(), uriInfo));
				}
				clinicalNotes.add(clinicalNote);
			    }
			}
			patientVisitResponse.setClinicalNotes(clinicalNotes);
		    }

		    if (patientVisitCollection.getRecordId() != null) {
			List<Records> records = recordsService.getRecordsByIds(patientVisitCollection.getRecordId());
			if (records != null && !records.isEmpty()) {
			    for (Records record : records) {
				record.setRecordsUrl(getFinalImageURL(record.getRecordsUrl(), uriInfo));
			    }
			}
			patientVisitResponse.setRecords(records);
		    }
		    response.add(patientVisitResponse);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while geting patient Visit : " + e.getCause().getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error while geting patient Visit : " + e.getCause().getMessage());
	}
	return response;
    }

    private String getFinalImageURL(String imageURL, UriInfo uriInfo) {
	if (imageURL != null && uriInfo != null) {
	    String finalImageURL = uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath);
	    return finalImageURL + imageURL;
	} else
	    return null;
    }

    @Override
    public Boolean email(String visitId, String emailAddress, UriInfo uriInfo) {
	PatientVisitCollection patientVisitCollection = null;
	Map<String, Object> parameters = new HashMap<String, Object>();
	MailAttachment mailAttachment = null;
	EmailTrackCollection emailTrackCollection = new EmailTrackCollection();

	try {
	    patientVisitCollection = patientVisitRepository.findOne(visitId);

	    if (patientVisitCollection != null) {
		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(patientVisitCollection.getDoctorId(),
			patientVisitCollection.getLocationId(), patientVisitCollection.getHospitalId(), ComponentType.ALL.getType());
		if (printSettings != null) {
		    DBObject printId = new BasicDBObject();
		    printId.put("$oid", printSettings.getId());

		    parameters.put("printSettingsId", Arrays.asList(printId));

		    PatientAdmissionCollection patientAdmission = patientAdmissionRepository.findByPatientIdAndDoctorId(patientVisitCollection.getPatientId(),
			    patientVisitCollection.getDoctorId());
		    PatientCollection patient = patientRepository.findByUserId(patientVisitCollection.getPatientId());
		    UserCollection user = userRepository.findOne(patientVisitCollection.getPatientId());

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
			    UserCollection doctorUser = userRepository.findOne(patientVisitCollection.getDoctorId());
			    if (doctorUser != null)
				parameters.put("footerSignature", doctorUser.getTitle() + " " + doctorUser.getFirstName());
			}
		    }
		    patientName = "Patient Name: " + (user != null ? user.getFirstName() : "--") + "<br>";
		    dob = "Patient Age: " + ((user != null && user.getDob() != null) ? (user.getDob().getAge() + " years") : "--") + "<br>";
		    gender = "Patient Gender: " + (user != null ? user.getGender() : "--") + "<br>";
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

		    LocationCollection location = locationRepository.findOne(patientVisitCollection.getLocationId());
		    if (location != null)
			parameters.put("logoURL", getFinalImageURL(location.getLogoUrl(), uriInfo));

		    String layout = printSettings != null ? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
			    : "PORTRAIT";
		    String pageSize = printSettings != null ? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4") : "A4";
		    String margins = printSettings != null ? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getMargins() : null) : null;

		    List<DBObject> prescriptions = new ArrayList<DBObject>();
		    if (patientVisitCollection.getPrescriptionId() != null) {
			for (String prescriptionId : patientVisitCollection.getPrescriptionId()) {
			    DBObject prescriptionItems = new BasicDBObject();
			    List<PrescriptionJasperDetails> prescriptionJasperDetails = getPrescriptionJasperDetails(prescriptionId, prescriptionItems);
			    prescriptionItems.put("items", prescriptionJasperDetails);
			    prescriptions.add(prescriptionItems);
			}
		    }
		    List<ClinicalNotesJasperDetails> clinicalNotes = new ArrayList<ClinicalNotesJasperDetails>();
		    if (patientVisitCollection.getPrescriptionId() != null) {
			for (String clinicalNotesId : patientVisitCollection.getClinicalNotesId()) {
			    ClinicalNotesJasperDetails clinicalJasperDetails = getClinicalNotesJasperDetails(clinicalNotesId, uriInfo);
			    clinicalNotes.add(clinicalJasperDetails);
			}
		    }
		    parameters.put("prescriptions", prescriptions);
		    parameters.put("clinicalNotes", clinicalNotes);

		    String path = jasperReportService.createPDF(parameters, "mongo-multiple-data", layout, pageSize, margins, user.getFirstName() + new Date()
			    + "CLINICALNOTES&PRESCRIPTION");
		    if (user != null) {
			emailTrackCollection.setPatientName(user.getFirstName());
			emailTrackCollection.setPatientId(user.getId());
		    }
		    List<MailAttachment> mailAttachments = new ArrayList<MailAttachment>();

		    FileSystemResource file = new FileSystemResource(path);
		    mailAttachment = new MailAttachment();
		    mailAttachment.setAttachmentName(file.getFilename());
		    mailAttachment.setFileSystemResource(file);

		    mailAttachments.add(mailAttachment);
		    if (patientVisitCollection.getRecordId() != null) {
			for (String recordId : patientVisitCollection.getRecordId()) {
			    Records record = recordsService.getRecordById(recordId);
			    mailAttachment = recordsService.getRecordMailData(recordId, record.getDoctorId(), record.getLocationId(), record.getHospitalId(),
				    uriInfo);
			    if (mailAttachment != null)
				mailAttachments.add(mailAttachment);
			}
		    }
		    mailService.sendEmailMultiAttach(emailAddress, "Patient Visit", "PFA.", mailAttachments);

		    emailTrackCollection.setDoctorId(patientVisitCollection.getDoctorId());
		    emailTrackCollection.setHospitalId(patientVisitCollection.getHospitalId());
		    emailTrackCollection.setLocationId(patientVisitCollection.getLocationId());
		    emailTrackCollection.setType(ComponentType.ALL.getType());
		    emailTrackCollection.setSubject("Patient Visit");
		    emailTackService.saveEmailTrack(emailTrackCollection);

		} else {
		    logger.warn("PrintSettings not found");
		    throw new BusinessException(ServiceError.NotFound, "PrintSettings not found");
		}
	    } else {
		logger.warn("Patient Visit Id does not exist");
		throw new BusinessException(ServiceError.NotFound, "Patient Visit Id does not exist");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return true;
    }

    private ClinicalNotesJasperDetails getClinicalNotesJasperDetails(String clinicalNotesId, UriInfo uriInfo) {
	ClinicalNotesCollection clinicalNotesCollection = null;
	ClinicalNotesJasperDetails clinicalNotesJasperDetails = null;
	try {
	    clinicalNotesCollection = clinicalNotesRepository.findOne(clinicalNotesId);
	    if (clinicalNotesCollection != null) {
		if (clinicalNotesCollection.getDoctorId() != null && clinicalNotesCollection.getHospitalId() != null
			&& clinicalNotesCollection.getLocationId() != null) {

		    clinicalNotesJasperDetails = new ClinicalNotesJasperDetails();
		    if (clinicalNotesCollection.getVitalSigns() != null) {
			String pulse = clinicalNotesCollection.getVitalSigns().getPulse();
			pulse = pulse != null && !pulse.isEmpty() ? "Pulse: " + pulse + VitalSignsUnit.PULSE.getUnit() + "    " : "";

			String temp = clinicalNotesCollection.getVitalSigns().getTemperature();
			temp = temp != null && !temp.isEmpty() ? "Temperature: " + temp + "    " : "";

			String breathing = clinicalNotesCollection.getVitalSigns().getBreathing();
			breathing = breathing != null && !breathing.isEmpty() ? "Breathing: " + breathing + VitalSignsUnit.BREATHING.getUnit() + "    " : "";

			String bloodPressure = "";
			if (clinicalNotesCollection.getVitalSigns().getBloodPressure() != null) {
			    String systolic = clinicalNotesCollection.getVitalSigns().getBloodPressure().getSystolic();
			    systolic = systolic != null && !systolic.isEmpty() ? systolic : "";

			    String diastolic = clinicalNotesCollection.getVitalSigns().getBloodPressure().getDiastolic();
			    diastolic = diastolic != null && !diastolic.isEmpty() ? diastolic : "";

			    bloodPressure = "Blood Pressure: " + systolic + "/" + diastolic + VitalSignsUnit.BLOODPRESSURE.getUnit();
			}
			String vitalSigns = pulse + temp + breathing + bloodPressure;
			clinicalNotesJasperDetails.setVitalSigns(vitalSigns != null && !vitalSigns.isEmpty() ? vitalSigns : null);
		    }
		    String observations = "";
		    for (String observationId : clinicalNotesCollection.getObservations()) {
			ObservationCollection observationCollection = observationRepository.findOne(observationId);
			if (observationCollection != null) {
			    if (observations == "")
				observations = observationCollection.getObservation();
			    else
				observations = observations + ", " + observationCollection.getObservation();
			}
		    }
		    clinicalNotesJasperDetails.setObservations(observations);

		    String notes = "";
		    for (String noteId : clinicalNotesCollection.getNotes()) {
			NotesCollection note = notesRepository.findOne(noteId);
			if (note != null) {
			    if (notes == "")
				notes = note.getNote();
			    else
				notes = notes + ", " + note.getNote();
			}
		    }
		    clinicalNotesJasperDetails.setNotes(notes);

		    String investigations = "";
		    for (String investigationId : clinicalNotesCollection.getInvestigations()) {
			InvestigationCollection investigation = investigationRepository.findOne(investigationId);
			if (investigation != null) {
			    if (investigations == "")
				investigations = investigation.getInvestigation();
			    else
				investigations = investigations + ", " + investigation.getInvestigation();
			}
		    }
		    clinicalNotesJasperDetails.setInvestigations(investigations);

		    String diagnosis = "";
		    for (String diagnosisId : clinicalNotesCollection.getDiagnoses()) {
			DiagnosisCollection diagnosisCollection = diagnosisRepository.findOne(diagnosisId);
			if (diagnosisCollection != null) {
			    if (diagnosis == "")
				diagnosis = diagnosisCollection.getDiagnosis();
			    else
				diagnosis = diagnosis + ", " + diagnosisCollection.getDiagnosis();
			}
		    }
		    clinicalNotesJasperDetails.setDiagnosis(diagnosis);

		    String complaints = "";
		    for (String complaintId : clinicalNotesCollection.getComplaints()) {
			ComplaintCollection complaint = complaintRepository.findOne(complaintId);
			if (complaint != null) {
			    if (complaints == "")
				complaints = complaint.getComplaint();
			    else
				complaints = complaints + ", " + complaint.getComplaint();
			}
		    }
		    clinicalNotesJasperDetails.setComplaints(complaints);

		    List<DBObject> diagramIds = new ArrayList<DBObject>();
		    for (String diagramId : clinicalNotesCollection.getDiagrams()) {
			DBObject diagram = new BasicDBObject();
			DiagramsCollection diagramsCollection = diagramsRepository.findOne(diagramId);
			if (diagramsCollection != null) {
			    if (diagramsCollection.getDiagramUrl() != null) {
				diagram.put("url", getFinalImageURL(diagramsCollection.getDiagramUrl(), uriInfo));
			    }
			    diagram.put("tags", diagramsCollection.getTags());
			    diagramIds.add(diagram);
			}
		    }
		    if (!diagramIds.isEmpty())
			clinicalNotesJasperDetails.setDiagrams(diagramIds);
		    else
			clinicalNotesJasperDetails.setDiagrams(null);
		}
	    } else {
		logger.warn("Clinical Notes not found. Please check clinicalNotesId.");
		throw new BusinessException(ServiceError.NotFound, "Clinical Notes not found. Please check clinicalNotesId.");
	    }
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return clinicalNotesJasperDetails;
    }

    private List<PrescriptionJasperDetails> getPrescriptionJasperDetails(String prescriptionId, DBObject prescriptionItemsObj) {
	PrescriptionCollection prescriptionCollection = null;
	List<PrescriptionJasperDetails> prescriptionItems = new ArrayList<PrescriptionJasperDetails>();
	try {
	    prescriptionCollection = prescriptionRepository.findOne(prescriptionId);
	    if (prescriptionCollection != null) {
		prescriptionItemsObj.put("advice", prescriptionCollection.getAdvice() != null ? prescriptionCollection.getAdvice() : "----");
		if (prescriptionCollection.getLabTests() != null && !prescriptionCollection.getLabTests().isEmpty()) {
		    String labTest = "";
		    int i = 1;
		    for (LabTest labTests : prescriptionCollection.getLabTests()) {
			labTest = labTest + i + ") " + labTests.getTestName() + "<br>";
			i++;
		    }
		    prescriptionItemsObj.put("labTest", labTest);
		} else {
		    prescriptionItemsObj.put("labTest", null);
		}
		if (prescriptionCollection.getDoctorId() != null && prescriptionCollection.getHospitalId() != null
			&& prescriptionCollection.getLocationId() != null) {
		    int no = 0;
		    for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
			if (prescriptionItem != null && prescriptionItem.getDrugId() != null) {
			    DrugCollection drug = drugRepository.findOne(prescriptionItem.getDrugId());
			    if (drug != null) {
				String drugType = drug.getDrugType() != null ? (drug.getDrugType().getType() != null ? drug.getDrugType().getType() : "") : "";
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
					prescriptionItem.getDosage() != null ? prescriptionItem.getDosage() : "----", duration, directions.isEmpty() ? "----"
						: directions, prescriptionItem.getInstructions() != null ? prescriptionItem.getInstructions() : "----");

				prescriptionItems.add(prescriptionJasperDetails);
			    }
			}
		    }
		}
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

	return prescriptionItems;
    }

    @Override
    public Boolean deleteVisit(String visitId, Boolean discarded) {
	try {
	    PatientVisitCollection patientVisitCollection = patientVisitRepository.findOne(visitId);
	    if (patientVisitCollection != null) {

		patientVisitCollection.setDiscarded(discarded);
		patientVisitCollection.setUpdatedTime(new Date());
		patientVisitRepository.save(patientVisitCollection);

		if (patientVisitCollection.getClinicalNotesId() != null) {
		    for (String clinicalNotesId : patientVisitCollection.getClinicalNotesId()) {
			clinicalNotesService.deleteNote(clinicalNotesId, discarded);
		    }
		}
		if (patientVisitCollection.getPrescriptionId() != null) {
		    for (String prescriptionId : patientVisitCollection.getPrescriptionId()) {
			prescriptionServices.deletePrescription(prescriptionId, patientVisitCollection.getDoctorId(), patientVisitCollection.getHospitalId(),
				patientVisitCollection.getLocationId(), patientVisitCollection.getPatientId(), discarded);
		    }
		}
		if (patientVisitCollection.getRecordId() != null) {
		    for (String recordId : patientVisitCollection.getRecordId()) {
			recordsService.deleteRecord(recordId, discarded);
		    }
		}

	    } else {
		logger.warn("Visit not found!");
		throw new BusinessException(ServiceError.Unknown, "Visit not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return true;
    }

    @Override
    public void smsVisit(String visitId, String doctorId, String locationId, String hospitalId, String mobileNumber) {
	try {
	    PatientVisitCollection patientVisitCollection = patientVisitRepository.findOne(visitId);
	    if (patientVisitCollection != null) {
		if (patientVisitCollection.getPrescriptionId() != null) {
		    for (String prescriptionId : patientVisitCollection.getPrescriptionId()) {
			prescriptionServices.smsPrescription(prescriptionId, doctorId, locationId, hospitalId, mobileNumber);
		    }
		}
	    } else {
		logger.warn("Visit not found!");
		throw new BusinessException(ServiceError.Unknown, "Visit not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
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
    public PatientVisitResponse getVisit(String visitId, UriInfo uriInfo) {
	PatientVisitResponse response = null;
	try {
	    PatientVisitCollection patientVisitCollection = patientVisitRepository.findOne(visitId);
	    if (patientVisitCollection != null) {
		List<Prescription> prescriptions = new ArrayList<Prescription>();
		List<ClinicalNotes> clinicalNotes = new ArrayList<ClinicalNotes>();
		List<Records> records = new ArrayList<Records>();

		if (patientVisitCollection.getPrescriptionId() != null && !patientVisitCollection.getPrescriptionId().isEmpty()) {
		    prescriptions.addAll(prescriptionServices.getPrescriptionsByIds(patientVisitCollection.getPrescriptionId()));
		}

		if (patientVisitCollection.getClinicalNotesId() != null && !patientVisitCollection.getClinicalNotesId().isEmpty()) {
		    for (String clinicalNotesId : patientVisitCollection.getClinicalNotesId()) {
			ClinicalNotes clinicalNote = clinicalNotesService.getNotesById(clinicalNotesId);
			if (clinicalNote != null) {
			    if (clinicalNote.getDiagrams() != null && !clinicalNote.getDiagrams().isEmpty()) {
				clinicalNote.setDiagrams(getFinalDiagrams(clinicalNote.getDiagrams(), uriInfo));
			    }
			    clinicalNotes.add(clinicalNote);
			}
		    }
		}
		if (patientVisitCollection.getRecordId() != null && !patientVisitCollection.getRecordId().isEmpty()) {
		    records = recordsService.getRecordsByIds(patientVisitCollection.getRecordId());
		    if (records != null && !records.isEmpty()) {
			for (Records record : records) {
			    record.setRecordsUrl(getFinalImageURL(record.getRecordsUrl(), uriInfo));
			}
		    }
		    records.addAll(records);
		}

		response = new PatientVisitResponse();
		BeanUtil.map(patientVisitCollection, response);
		response.setPrescriptions(prescriptions);
		response.setClinicalNotes(clinicalNotes);
		response.setRecords(records);
	    } else {
		logger.warn("Visit not found!");
		throw new BusinessException(ServiceError.NotFound, "Visit not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    public List<PatientVisit> getVisitsHandheld(String doctorId, String locationId, String hospitalId, String patientId, int page, int size,
	    Boolean isOTPVerified, String updatedTime) {
	List<PatientVisit> response = null;
	List<PatientVisitCollection> patientVisitCollections = null;
	try {
	    long createdTimestamp = Long.parseLong(updatedTime);
	    if (!isOTPVerified) {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			patientVisitCollections = patientVisitRepository.find(doctorId, patientId, new Date(createdTimestamp), new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			patientVisitCollections = patientVisitRepository.find(doctorId, patientId, new Date(createdTimestamp), new Sort(Sort.Direction.DESC,
				"updatedTime"));
		} else {
		    if (size > 0)
			patientVisitCollections = patientVisitRepository.find(doctorId, locationId, hospitalId, patientId, new Date(createdTimestamp),
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			patientVisitCollections = patientVisitRepository.find(doctorId, locationId, hospitalId, patientId, new Date(createdTimestamp),
				new Sort(Sort.Direction.DESC, "updatedTime"));
		}
	    } else {
		if (size > 0)
		    patientVisitCollections = patientVisitRepository.find(patientId, new Date(createdTimestamp), new PageRequest(page, size, Direction.DESC,
			    "updatedTime"));
		else
		    patientVisitCollections = patientVisitRepository.find(patientId, new Date(createdTimestamp), new Sort(Sort.Direction.DESC, "updatedTime"));
	    }
	    if (patientVisitCollections != null) {
		response = new ArrayList<PatientVisit>();
		BeanUtil.map(patientVisitCollections, response);

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while geting patient Visit : " + e.getCause().getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error while geting patient Visit : " + e.getCause().getMessage());
	}
	return response;
    }

    @Override
    public String editRecord(String id, VisitedFor visitedFor) {
	PatientVisitCollection patientTrackCollection = new PatientVisitCollection();
	try {
	    switch (visitedFor) {
	    case PRESCRIPTION:
		patientTrackCollection = patientVisitRepository.findByPrescriptionId(id);
		break;
	    case CLINICAL_NOTES:
		patientTrackCollection = patientVisitRepository.findByClinialNotesId(id);
		break;
	    case REPORTS:
		patientTrackCollection = patientVisitRepository.findByRecordId(id);
		break;
	    default:
		break;
	    }
	    if (patientTrackCollection != null) {
		patientTrackCollection.setUpdatedTime(new Date());
		patientTrackCollection = patientVisitRepository.save(patientTrackCollection);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while editing patient visit record : " + e.getCause().getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error while editing patient visit record : " + e.getCause().getMessage());
	}
	return patientTrackCollection.getId();

    }

    private List<Diagram> getFinalDiagrams(List<Diagram> diagrams, UriInfo uriInfo) {
	for (Diagram diagram : diagrams) {
	    if (diagram.getDiagramUrl() != null) {
		diagram.setDiagramUrl(getFinalImageURL(diagram.getDiagramUrl(), uriInfo));
	    }
	}
	return diagrams;
    }

    @Override
    public int getVisitCount(String doctorId, String patientId, String locationId, String hospitalId) {
	Integer visitCount = 0;
	try {
	    visitCount = patientVisitRepository.getVisitCount(doctorId, patientId, hospitalId, locationId, false);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while getting Visits Count");
	    throw new BusinessException(ServiceError.Unknown, "Error while getting Visits Count");
	}
	return visitCount;
    }
}
