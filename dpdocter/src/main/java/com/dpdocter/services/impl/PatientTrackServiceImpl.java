package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.PatientTrack;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientTrackCollection;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientTrackRepository;
import com.dpdocter.services.PatientTrackService;

@Service
public class PatientTrackServiceImpl implements PatientTrackService {

    private static Logger logger = Logger.getLogger(PatientTrackServiceImpl.class.getName());

    @Autowired
    private PatientTrackRepository patientTrackRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ContactsServiceImpl contactsService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public boolean addRecord(PatientTrack request) {
	boolean response = false;
	try {
	    PatientTrackCollection patientTrackCollection = new PatientTrackCollection();
	    BeanUtil.map(request, patientTrackCollection);
	    PatientCollection patientCollection = patientRepository.findByUserId(request.getPatientId());
	    if (patientCollection != null) {
		patientTrackCollection.setPatientId(patientCollection.getId());
	    }
	    patientTrackCollection.setVisitedTime(new Date());
	    patientTrackCollection.setCreatedTime(new Date());
	    patientTrackRepository.save(patientTrackCollection);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while saving patient track record : " + e.getCause().getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error while saving patient track record : " + e.getCause().getMessage());
	}
	return response;
    }

    @Override
    public boolean addRecord(Object details, VisitedFor visitedFor) {
	boolean response = false;
	try {
	    PatientTrackCollection patientTrackCollection = new PatientTrackCollection();
	    BeanUtil.map(details, patientTrackCollection);
	    PatientCollection patientCollection = patientRepository.findByUserId(patientTrackCollection.getPatientId());
	    if (patientCollection != null) {
		patientTrackCollection.setPatientId(patientCollection.getId());
	    }
	    patientTrackCollection.setVisitedFor(visitedFor);
	    patientTrackCollection.setVisitedTime(new Date());
	    patientTrackCollection.setCreatedTime(new Date());
	    patientTrackCollection.setId(null);
	    patientTrackRepository.save(patientTrackCollection);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while saving patient track record : " + e.getCause().getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error while saving patient track record : " + e.getCause().getMessage());
	}
	return response;
    }

    @Override
    public boolean addRecord(String patientId, String doctorId, String locationId, String hospitalId, VisitedFor visitedFor) {
	boolean response = false;
	try {
	    PatientTrackCollection patientTrackCollection = new PatientTrackCollection();
	    PatientCollection patientCollection = patientRepository.findByUserId(patientId);
	    if (patientCollection != null) {
		patientTrackCollection.setPatientId(patientCollection.getId());
	    }
	    patientTrackCollection.setDoctorId(doctorId);
	    patientTrackCollection.setLocationId(locationId);
	    patientTrackCollection.setHospitalId(hospitalId);
	    patientTrackCollection.setVisitedFor(visitedFor);
	    patientTrackCollection.setVisitedTime(new Date());
	    patientTrackCollection.setCreatedTime(new Date());
	    patientTrackRepository.save(patientTrackCollection);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while saving patient track record : " + e.getCause().getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error while saving patient track record : " + e.getCause().getMessage());
	}
	return response;
    }

    @Override
    public DoctorContactsResponse recentlyVisited(String doctorId, String locationId, String hospitalId, int page, int size) {
	DoctorContactsResponse response = null;
	try {
	    List<PatientTrackCollection> patientTrackCollections = patientTrackRepository.findAll(doctorId, locationId, hospitalId, size > 0 ? new PageRequest(
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

	    AggregationResults<PatientTrackCollection> aggregationResults = mongoTemplate.aggregate(aggregation, PatientTrackCollection.class,
		    PatientTrackCollection.class);

	    List<PatientTrackCollection> patientTrackCollections = aggregationResults.getMappedResults();

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
}
