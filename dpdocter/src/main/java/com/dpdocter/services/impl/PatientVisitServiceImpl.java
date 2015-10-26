package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionItemDetail;
import com.dpdocter.beans.Records;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.request.AddMultipleDataRequest;
import com.dpdocter.response.PatientVisitResponse;
import com.dpdocter.response.PrescriptionAddEditResponse;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.RecordsService;

@Service
public class PatientVisitServiceImpl implements PatientVisitService {

    private static Logger logger = Logger.getLogger(PatientVisitServiceImpl.class.getName());

    @Autowired
    private PatientVisitRepository patientTrackRepository;

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
    private RecordsService recordsService;

    @Autowired
    private DrugRepository drugRepository;

    @Context
    private UriInfo uriInfo;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    // @Override
    // public boolean addRecord(PatientVisit request) {
    // boolean response = false;
    // try {
    // PatientVisitCollection patientTrackCollection = new
    // PatientVisitCollection();
    // BeanUtil.map(request, patientTrackCollection);
    // PatientCollection patientCollection =
    // patientRepository.findByUserId(request.getPatientId());
    // if (patientCollection != null) {
    // patientTrackCollection.setPatientId(patientCollection.getId());
    // }
    // patientTrackCollection.setVisitedTime(new Date());
    // patientTrackCollection.setCreatedTime(new Date());
    // patientTrackRepository.save(patientTrackCollection);
    // response = true;
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error while saving patient track record : " +
    // e.getCause().getMessage());
    // throw new BusinessException(ServiceError.Unknown,
    // "Error while saving patient visit record : " +
    // e.getCause().getMessage());
    // }
    // return response;
    // }

    @Override
    public String addRecord(Object details, VisitedFor visitedFor, String visitId) {
	PatientVisitCollection patientTrackCollection = new PatientVisitCollection();
	try {

	    BeanUtil.map(details, patientTrackCollection);
	    String id = patientTrackCollection.getId();
	    if (visitId != null)
		patientTrackCollection = patientTrackRepository.findOne(visitId);

	    if (patientTrackCollection.getCreatedTime() == null) {
		patientTrackCollection.setCreatedTime(new Date());
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

	    patientTrackCollection = patientTrackRepository.save(patientTrackCollection);

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
	    PatientVisitCollection patientTrackCollection = patientTrackRepository.find(doctorId, locationId, hospitalId, patientId);
	    PatientCollection patientCollection = patientRepository.findByUserId(patientId);
	    if (patientCollection != null) {
		patientTrackCollection.setPatientId(patientCollection.getId());
	    }
	    if (patientTrackCollection == null) {
		patientTrackCollection = new PatientVisitCollection();
		patientTrackCollection.setDoctorId(doctorId);
		patientTrackCollection.setLocationId(locationId);
		patientTrackCollection.setHospitalId(hospitalId);
		patientTrackCollection.setVisitedTime(new Date());
		patientTrackCollection.setCreatedTime(new Date());

		List<VisitedFor> visitedforList = new ArrayList<VisitedFor>();
		visitedforList.add(visitedFor);
		patientTrackCollection.setVisitedFor(visitedforList);
	    } else {
		patientTrackCollection.setVisitedTime(new Date());
		patientTrackCollection.getVisitedFor().add(visitedFor);
	    }
	    patientTrackRepository.save(patientTrackCollection);
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
	    List<PatientVisitCollection> patientTrackCollections = patientTrackRepository.findAll(doctorId, locationId, hospitalId, size > 0 ? new PageRequest(
		    page, size, Direction.DESC, "visitedTime") : null);
	    if (patientTrackCollections != null && !patientTrackCollections.isEmpty()) {
		@SuppressWarnings("unchecked")
		List<String> patientIds = (List<String>) CollectionUtils.collect(patientTrackCollections, new BeanToPropertyValueTransformer("patientId"));
		List<PatientCard> patientCards = contactsService.getSpecifiedPatientCards(patientIds, doctorId, locationId, hospitalId);
		int totalSize = patientCards.size();
		// patientTrackRepository.count(doctorId, locationId,
		// hospitalId);
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
		aggregation = Aggregation.newAggregation(Aggregation.match(matchCriteria), Aggregation.group("patientId").count().as("total"), Aggregation
			.project("total").and("patientId").previousOperation(), Aggregation.sort(Sort.Direction.DESC, "total"), Aggregation.skip(page * size),
			Aggregation.limit(size));
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
		List<String> patientIds = (List<String>) CollectionUtils.collect(patientTrackCollections, new BeanToPropertyValueTransformer("patientId"));
		List<PatientCard> patientCards = contactsService.getSpecifiedPatientCards(patientIds, doctorId, locationId, hospitalId);
		int totalSize = patientCards.size();
		// mongoTemplate.aggregate(aggregationCount,
		// PatientTrackCollection.class,
		// PatientTrackCollection.class).getMappedResults().size();
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
    public PatientVisitResponse addMultipleData(AddMultipleDataRequest request) {
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
		    records.setRecordsUrl(getFinalImageURL(records.getRecordsUrl()));
		    String visitId = addRecord(records, VisitedFor.REPORTS, request.getVisitId());
		    records.setVisitId(visitId);
		    request.setVisitId(visitId);
		    List<Records> list = new ArrayList<Records>();
		    list.add(records);
		    response.setRecords(list);
		}
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
    		 Boolean isOTPVerified, String updatedTime) {
	List<PatientVisitResponse> response = null;
	List<PatientVisitCollection> patientVisitCollections = null;
	try {
		if (StringUtils.isEmpty(updatedTime)) {
			if (!isOTPVerified) {
			    if (locationId == null && hospitalId == null) {
			    	 if (size > 0) patientVisitCollections = patientTrackRepository.find(doctorId, patientId, new PageRequest(page, size, Direction.DESC,"updatedTime"));
			    	 else patientVisitCollections = patientTrackRepository.find(doctorId, patientId, new Sort(Sort.Direction.DESC, "updatedTime"));
			    }
			    else{
			    	 if (size > 0)patientVisitCollections = patientTrackRepository.find(doctorId, locationId, hospitalId, patientId, new PageRequest(page, size, Direction.DESC,"updatedTime"));
	    		    else patientVisitCollections = patientTrackRepository.find(doctorId, locationId, hospitalId, patientId, new Sort(Sort.Direction.DESC, "updatedTime"));
			    }
			}
			else{
				 if (size > 0) patientVisitCollections = patientTrackRepository.find(patientId, new PageRequest(page, size, Direction.DESC,"updatedTime"));
		    	 else patientVisitCollections = patientTrackRepository.find(patientId, new Sort(Sort.Direction.DESC, "updatedTime"));
			}
		}
		else{
			long createdTimestamp = Long.parseLong(updatedTime);
			if (!isOTPVerified) {
			    if (locationId == null && hospitalId == null) {
			    	 if (size > 0) patientVisitCollections = patientTrackRepository.find(doctorId, patientId, new Date(createdTimestamp), new PageRequest(page, size, Direction.DESC,"updatedTime"));
			    	 else patientVisitCollections = patientTrackRepository.find(doctorId, patientId, new Date(createdTimestamp), new Sort(Sort.Direction.DESC, "updatedTime"));
			    }
			    else{
			    	 if (size > 0)patientVisitCollections = patientTrackRepository.find(doctorId, locationId, hospitalId, patientId, new Date(createdTimestamp), new PageRequest(page, size, Direction.DESC,"updatedTime"));
	    		    else patientVisitCollections = patientTrackRepository.find(doctorId, locationId, hospitalId, patientId, new Date(createdTimestamp), new Sort(Sort.Direction.DESC, "updatedTime"));
			    }
			}
			else{
				 if (size > 0) patientVisitCollections = patientTrackRepository.find(patientId, new Date(createdTimestamp), new PageRequest(page, size, Direction.DESC,"updatedTime"));
		    	 else patientVisitCollections = patientTrackRepository.find(patientId, new Date(createdTimestamp), new Sort(Sort.Direction.DESC, "updatedTime"));
			}
		}
	    if (patientVisitCollections != null) {
		response = new ArrayList<PatientVisitResponse>();

		for (PatientVisitCollection patientVisitCollection : patientVisitCollections) {
		    PatientVisitResponse patientVisitResponse = new PatientVisitResponse();
		    BeanUtil.map(patientVisitCollection, patientVisitResponse);

		    if (patientVisitCollection.getPrescriptionId() != null) {
			List<Prescription> prescriptions = getPrescriptions(patientVisitCollection.getPrescriptionId());
			patientVisitResponse.setPrescriptions(prescriptions);
		    }
		    if (patientVisitCollection.getClinicalNotesId() != null) {
			List<ClinicalNotes> clinicalNotes = getClinicalNotes(patientVisitCollection.getClinicalNotesId());
			patientVisitResponse.setClinicalNotes(clinicalNotes);
		    }
		    if (patientVisitCollection.getRecordId() != null) {
			List<Records> records = getRecords(patientVisitCollection.getRecordId());
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

    private List<Records> getRecords(Collection<String> recordIds) {
	List<Records> response = null;
	Query query = new Query();

	query.addCriteria(Criteria.where("id").in(recordIds));

	List<RecordsCollection> recordCollection = mongoTemplate.find(query, RecordsCollection.class);
	if (recordCollection != null) {
	    response = new ArrayList<Records>();
	    BeanUtil.map(recordCollection, response);
	}
	return response;
    }

    private List<ClinicalNotes> getClinicalNotes(Collection<String> clinicalNotesIds) {
	List<ClinicalNotes> response = null;
	Query query = new Query();

	query.addCriteria(Criteria.where("id").in(clinicalNotesIds));

	List<ClinicalNotesCollection> clinicalNotesCollection = mongoTemplate.find(query, ClinicalNotesCollection.class);
	if (clinicalNotesCollection != null) {
	    response = new ArrayList<ClinicalNotes>();
	    BeanUtil.map(clinicalNotesCollection, response);
	}
	return response;
    }

    private List<Prescription> getPrescriptions(Collection<String> prescriptionIds) {
	List<Prescription> response = null;
	Query query = new Query();

	query.addCriteria(Criteria.where("id").in(prescriptionIds));

	List<PrescriptionCollection> prescriptionCollection = mongoTemplate.find(query, PrescriptionCollection.class);
	if (prescriptionCollection != null) {
	    response = new ArrayList<Prescription>();
	    BeanUtil.map(prescriptionCollection, response);
	}
	return response;
    }

    private String getFinalImageURL(String imageURL) {
	if (imageURL != null && uriInfo != null) {
	    String finalImageURL = uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath);
	    return finalImageURL + imageURL;
	} else
	    return null;

    }
}
