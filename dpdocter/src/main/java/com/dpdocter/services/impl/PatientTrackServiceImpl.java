package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Patient;
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

    @Autowired
    private PatientTrackRepository patientTrackRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public boolean addRecord(PatientTrack request) {
	boolean response = false;
	try {
	    PatientTrackCollection patientTrackCollection = new PatientTrackCollection();
	    BeanUtil.map(request, patientTrackCollection);
	    patientTrackCollection.setVisitedTime(new Date());
	    patientTrackRepository.save(patientTrackCollection);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
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
	    patientTrackCollection.setVisitedFor(visitedFor);
	    patientTrackCollection.setVisitedTime(new Date());
	    patientTrackCollection.setId(null);
	    patientTrackRepository.save(patientTrackCollection);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error while saving patient track record : " + e.getCause().getMessage());
	}
	return response;
    }

    @Override
    public boolean addRecord(String patientId, String doctorId, String locationId, String hospitalId, VisitedFor visitedFor) {
	boolean response = false;
	try {
	    PatientTrackCollection patientTrackCollection = new PatientTrackCollection();
	    patientTrackCollection.setPatientId(patientId);
	    patientTrackCollection.setDoctorId(doctorId);
	    patientTrackCollection.setLocationId(locationId);
	    patientTrackCollection.setHospitalId(hospitalId);
	    patientTrackCollection.setVisitedFor(visitedFor);
	    patientTrackCollection.setVisitedTime(new Date());
	    patientTrackRepository.save(patientTrackCollection);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error while saving patient track record : " + e.getCause().getMessage());
	}
	return response;
    }

    @Override
    public List<Patient> recentlyVisited(String doctorId, String locationId, String hospitalId, int page, int size) {
	List<Patient> patients = null;
	try {
	    List<PatientTrackCollection> patientTrackCollections = patientTrackRepository.findAll(doctorId, locationId, hospitalId, new PageRequest(page, size,
		    Direction.DESC, "visitedTime"));
	    if (patientTrackCollections != null && !patientTrackCollections.isEmpty()) {
		@SuppressWarnings("unchecked")
		List<String> patientIds = (List<String>) CollectionUtils.collect(patientTrackCollections, new BeanToPropertyValueTransformer("patientId"));
		List<PatientCollection> patientCollections = patientRepository.findByUserId(patientIds);
		if (patientCollections != null) {
		    patients = new ArrayList<Patient>();
		    BeanUtil.map(patientCollections, patients);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error while getting recently visited patients record : " + e.getCause().getMessage());
	}
	return patients;
    }

    @Override
    public List<Patient> mostVisited(String doctorId, String locationId, String hospitalId, int page, int size) {
	List<Patient> patients = null;
	try {
	    Criteria matchCriteria = Criteria.where("doctorId").is(doctorId).and("locationId").is(locationId).and("hospitalId").is(hospitalId);
	    Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(matchCriteria), Aggregation.group("patientId").count().as("total"),
		    Aggregation.project("total").and("patientId").previousOperation(), Aggregation.sort(Sort.Direction.DESC, "total"),
		    Aggregation.skip(page * size), Aggregation.limit(size + 1));

	    AggregationResults<PatientTrackCollection> aggregationResults = mongoTemplate.aggregate(aggregation, PatientTrackCollection.class,
		    PatientTrackCollection.class);

	    List<PatientTrackCollection> patientTrackCollections = aggregationResults.getMappedResults();
	    if (patientTrackCollections != null && !patientTrackCollections.isEmpty()) {
		@SuppressWarnings("unchecked")
		List<String> patientIds = (List<String>) CollectionUtils.collect(patientTrackCollections, new BeanToPropertyValueTransformer("patientId"));
		List<PatientCollection> patientCollections = patientRepository.findByUserId(patientIds);
		if (patientCollections != null) {
		    patients = new ArrayList<Patient>();
		    BeanUtil.map(patientCollections, patients);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error while getting most visited patients record : " + e.getCause().getMessage());
	}
	return patients;
    }
}
