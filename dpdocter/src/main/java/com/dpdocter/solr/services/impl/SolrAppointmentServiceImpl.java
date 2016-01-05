package com.dpdocter.solr.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.solr.beans.AppointmentSearchResponse;
import com.dpdocter.solr.beans.SolrLabTest;
import com.dpdocter.solr.document.SolrDiagnosticTestDocument;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.solr.document.SolrLabTestDocument;
import com.dpdocter.solr.document.SolrLocationDocument;
import com.dpdocter.solr.document.SolrSpecialityDocument;
import com.dpdocter.solr.document.SolrSymptomsDocument;
import com.dpdocter.solr.enums.AppointmentResponseType;
import com.dpdocter.solr.repository.SolrDiagnosticTestRepository;
import com.dpdocter.solr.repository.SolrDoctorRepository;
import com.dpdocter.solr.repository.SolrLabTestRepository;
import com.dpdocter.solr.repository.SolrLocationRepository;
import com.dpdocter.solr.repository.SolrSpecialityRepository;
import com.dpdocter.solr.repository.SolrSymptomsRepository;
import com.dpdocter.solr.response.LabResponse;
import com.dpdocter.solr.services.SolrAppointmentService;

import common.util.web.DPDoctorUtils;

@Service
public class SolrAppointmentServiceImpl implements SolrAppointmentService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private SolrDoctorRepository solrDoctorRepository;

    @Autowired
    private SolrLocationRepository solrLocationRepository;

    @Autowired
    private SolrSpecialityRepository solrSpecialityRepository;

    @Autowired
    private SolrDiagnosticTestRepository solrDiagnosticTestRepository;
    
    @Autowired
    private SolrLabTestRepository solrLabTestRepository;
    
    @Autowired
    private SolrSymptomsRepository solrSymptomRepository;
    
    @Override
    public boolean addLocation(List<SolrLocationDocument> request) {
	boolean response = false;
	try {
	    solrLocationRepository.save(request);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error While Saving Location Details to Solr : " + e.getMessage());
	}
	return response;
    }

    @Override
    public boolean addSpeciality(List<SolrSpecialityDocument> request) {
	boolean response = false;
	try {
	    solrSpecialityRepository.save(request);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error While Saving Speciality Details to Solr : " + e.getMessage());
	}
	return response;
    }

    @Override
    public List<AppointmentSearchResponse> search(String city, String location, String latitude, String longitude, String searchTerm) {
	List<AppointmentSearchResponse> response = null;
	try {
	    List<SolrSpecialityDocument> solrSpecialityDocuments = solrSpecialityRepository.findAll(searchTerm);
	    
	    List<SolrSymptomsDocument> solrSymptomsDocuments = solrSymptomRepository.findAll(searchTerm);
	    
	    List<SolrDoctorDocument> solrDoctorDocuments = null;
	    if(!DPDoctorUtils.anyStringEmpty(searchTerm)){
	    	if(DPDoctorUtils.allStringsEmpty(city, location)){
	    		if(DPDoctorUtils.allStringsEmpty(latitude, longitude))solrDoctorDocuments = solrDoctorRepository.findAll(searchTerm);
	    		else{
	    			if(latitude != null && longitude != null)solrDoctorDocuments = solrDoctorRepository.findByLatitudeLongitude(latitude, longitude, searchTerm);
	    		}
	    	}else{
	    		if(city != null && location != null)solrDoctorDocuments = solrDoctorRepository.findByCityLocation(city, location, searchTerm);
	    		else if(city != null)solrDoctorDocuments = solrDoctorRepository.findByCity(city, searchTerm);
	    		else if(location != null)solrDoctorDocuments = solrDoctorRepository.findByLocation(location, searchTerm);
	    	}
	    }
	    else{
	    	if(DPDoctorUtils.allStringsEmpty(city, location)){
	    		if(latitude != null && longitude != null)solrDoctorDocuments = solrDoctorRepository.findByLatitudeLongitude(latitude, longitude);
	    
	    	}else{
	    		if(city != null && location != null)
	    			solrDoctorDocuments = solrDoctorRepository.findByCityLocation(city, location);
	    		else if(city != null)solrDoctorDocuments = solrDoctorRepository.findByCity(city);
	    		else if(location != null)solrDoctorDocuments = solrDoctorRepository.findByLocation(location);
	    	}
	    }
	    		
	    List<SolrDoctorDocument> solrLocationDocuments = null;
	    if(!DPDoctorUtils.anyStringEmpty(searchTerm)){
	    	if(DPDoctorUtils.allStringsEmpty(city, location)){
	    		if(DPDoctorUtils.allStringsEmpty(latitude, longitude))solrLocationDocuments = solrDoctorRepository.findByLocationName(searchTerm);
	    		else{
	    			if(latitude != null && longitude != null)solrLocationDocuments = solrDoctorRepository.findByLatitudeLongitudeLocation(latitude, longitude, searchTerm);
	    		}
	    	}else{
	    		if(city != null && location != null)solrLocationDocuments = solrDoctorRepository.findByCityLocationName(city, location, searchTerm);
	    		else if(city != null)solrDoctorDocuments = solrDoctorRepository.findByCityLocationName(city, searchTerm);
	    		else if(location != null)solrDoctorDocuments = solrDoctorRepository.findByLocationLocationName(location, searchTerm);
	    	}
	    }
	    else{
	    	if(DPDoctorUtils.allStringsEmpty(city, location)){
	    		if(latitude != null && longitude != null)solrLocationDocuments = solrDoctorRepository.findByLatitudeLongitude(latitude, longitude);
	    
	    	}else{
	    		if(city != null && location != null)
	    			solrDoctorDocuments = solrDoctorRepository.findByCityLocation(city, location);
	    		else if(city != null)solrDoctorDocuments = solrDoctorRepository.findByCity(city);
	    		else if(location != null)solrDoctorDocuments = solrDoctorRepository.findByLocation(location);
	    	}
	    }

	    List<SolrDiagnosticTestDocument> diagnosticTestDocuments = solrDiagnosticTestRepository.findAll(searchTerm);
	    
	    response = new ArrayList<AppointmentSearchResponse>();
	    for (SolrSpecialityDocument speciality : solrSpecialityDocuments) {
		AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
		appointmentSearchResponse.setId(speciality.getId());
		appointmentSearchResponse.setResponse(speciality.getSpeciality());
		appointmentSearchResponse.setResponseType(AppointmentResponseType.SPECIALITY);
		response.add(appointmentSearchResponse);
	    }

	    for (SolrSymptomsDocument symptom : solrSymptomsDocuments) {
			AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
			appointmentSearchResponse.setId(symptom.getId());
			appointmentSearchResponse.setResponse(symptom.getSymptom());
			appointmentSearchResponse.setResponseType(AppointmentResponseType.SYMPTOM);
			response.add(appointmentSearchResponse);
		 }

	    for (SolrDiagnosticTestDocument diagnosticTest : diagnosticTestDocuments) {
			AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
			appointmentSearchResponse.setId(diagnosticTest.getId());
			appointmentSearchResponse.setResponse(diagnosticTest.getTestName());
			appointmentSearchResponse.setResponseType(AppointmentResponseType.LABTEST);
			response.add(appointmentSearchResponse);
		 }
	    
	    for (SolrDoctorDocument doctor : solrDoctorDocuments) {
		AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
		appointmentSearchResponse.setId(doctor.getUserId());
		SolrDoctorDocument object = new SolrDoctorDocument();
		object.setFirstName(doctor.getFirstName());
		object.setLocationId(doctor.getLocationId());
		appointmentSearchResponse.setResponse(object);
		appointmentSearchResponse.setResponseType(AppointmentResponseType.DOCTOR);
		response.add(appointmentSearchResponse);
	    }

	    for (SolrDoctorDocument locationDocument : solrLocationDocuments) {
		if(!locationDocument.getIsLab()){
			AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
			appointmentSearchResponse.setId(locationDocument.getLocationId());
			appointmentSearchResponse.setResponse(locationDocument.getLocationName());
			appointmentSearchResponse.setResponseType(AppointmentResponseType.CLINIC);
			response.add(appointmentSearchResponse);
		}
	    }
	    
	    for (SolrDoctorDocument locationDocument : solrLocationDocuments) {
			if(locationDocument.getIsLab()){
				AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
				appointmentSearchResponse.setId(locationDocument.getLocationId());
				appointmentSearchResponse.setResponse(locationDocument.getLocationName());
				appointmentSearchResponse.setResponseType(AppointmentResponseType.LAB);
				response.add(appointmentSearchResponse);
			}
	    }
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return response;
    }

    @Override
    public List<SolrDoctorDocument> getDoctors(String city, String location, String keyword) {
	List<SolrDoctorDocument> response = null;
	try {
	    Criteria doctorSearchCriteria = Criteria.where("city").is(city);

	    if (!DPDoctorUtils.anyStringEmpty(location)) {
		doctorSearchCriteria = doctorSearchCriteria.and("locationName").is(location);
	    }

	    if (!DPDoctorUtils.anyStringEmpty(keyword)) {
		doctorSearchCriteria = doctorSearchCriteria.or("firstName").is(keyword).or("middleName").is(keyword).or("lastName")
			.is(keyword).or("emailAddress").is(keyword).or("specialization").is(keyword);
	    }

	    SimpleQuery query = new SimpleQuery(doctorSearchCriteria);

	    solrTemplate.setSolrCore("doctors");

	    response = solrTemplate.queryForPage(query, SolrDoctorDocument.class).getContent();
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error While Getting Doctor Details From Solr : " + e.getMessage());
	}
	return response;
    }

    @Override
    public List<SolrDoctorDocument> getDoctors(String city, String location, String speciality, String symptom, Boolean booking, Boolean calling,
	    String minFee, String maxFee, String minTime, String maxTime, List<String> days, String gender, String minExperience, String maxExperience) {
	List<SolrDoctorDocument> response = null;
	try {
	    Criteria doctorSearchCriteria = Criteria.where("city").is(city);

	    if (!DPDoctorUtils.anyStringEmpty(location)) {
		doctorSearchCriteria = doctorSearchCriteria.and("locationName").is(location);
	    }

	    if (!DPDoctorUtils.anyStringEmpty(speciality)) {
		doctorSearchCriteria = doctorSearchCriteria.or("specialities").is(speciality);
	    }

	    if (!DPDoctorUtils.anyStringEmpty(symptom)) {
			List<SolrSymptomsDocument> solrSymptomsDocuments = solrSymptomRepository.findAll(symptom);
			@SuppressWarnings("unchecked")
		    Collection<String> specialityIds = CollectionUtils.collect(solrSymptomsDocuments, new BeanToPropertyValueTransformer("specialityId"));
			List<SolrSpecialityDocument> solrSpecialityDocuments = solrSpecialityRepository.findByIds(specialityIds);
			
			@SuppressWarnings("unchecked")
		    Collection<String> specialities = CollectionUtils.collect(solrSpecialityDocuments, new BeanToPropertyValueTransformer("speciality"));
			doctorSearchCriteria = doctorSearchCriteria.or("specialities").in(specialities);
		}
	    
	    if (DPDoctorUtils.anyStringEmpty(minFee, maxFee)) {
		if (!DPDoctorUtils.anyStringEmpty(minFee))
		    doctorSearchCriteria = doctorSearchCriteria.or("consultationFee").greaterThanEqual(minFee);
		if (!DPDoctorUtils.anyStringEmpty(maxFee))
		    doctorSearchCriteria = doctorSearchCriteria.or("consultationFee").lessThanEqual(maxFee);
	    } else {
		doctorSearchCriteria = doctorSearchCriteria.or("consultationFee").greaterThanEqual(minFee).lessThanEqual(maxFee);
	    }

	    if (DPDoctorUtils.anyStringEmpty(minExperience, maxExperience)) {
		if (!DPDoctorUtils.anyStringEmpty(minExperience))
		    doctorSearchCriteria = doctorSearchCriteria.or("experience").greaterThanEqual(minExperience);
		if (!DPDoctorUtils.anyStringEmpty(maxExperience))
		    doctorSearchCriteria = doctorSearchCriteria.or("experience").lessThanEqual(maxExperience);
	    } else {
		doctorSearchCriteria = doctorSearchCriteria.or("experience").greaterThanEqual(minExperience).lessThanEqual(maxExperience);
	    }

	    if (!DPDoctorUtils.anyStringEmpty(gender)) {
		doctorSearchCriteria = doctorSearchCriteria.or("gender").is(gender);
	    }

	    if (DPDoctorUtils.anyStringEmpty(minTime, maxTime)) {
		if (!DPDoctorUtils.anyStringEmpty(minTime))
		    doctorSearchCriteria = doctorSearchCriteria.or("workingSchedules").greaterThanEqual(minTime);
		if (!DPDoctorUtils.anyStringEmpty(maxTime))
		    doctorSearchCriteria = doctorSearchCriteria.or("workingSchedules").lessThanEqual(maxTime);
	    } else {
		doctorSearchCriteria = doctorSearchCriteria.or("workingSchedules").greaterThanEqual(minTime).lessThanEqual(maxTime);
	    }

	    if (days != null && !days.isEmpty()) {
		doctorSearchCriteria = doctorSearchCriteria.or("workingSchedules").in(days);
	    }

	    SimpleQuery query = new SimpleQuery(doctorSearchCriteria);
	    solrTemplate.setSolrCore("doctors");

	    response = solrTemplate.queryForPage(query, SolrDoctorDocument.class).getContent();
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error While Getting Doctor Details From Solr : " + e.getMessage());
	}
	return response;
    }

	@Override
	public List<LabResponse> getLabs(String city, String location, String testId) {
		List<LabResponse> response = null;
		List<SolrLabTestDocument> solrLabTestDocuments = null;
		try {

		    if (!DPDoctorUtils.anyStringEmpty(testId)) {
		    	SolrDiagnosticTestDocument diagnosticTest = solrDiagnosticTestRepository.findOne(testId);
		    	if(diagnosticTest != null){
		    		solrLabTestDocuments = solrLabTestRepository.findByTestId(testId);
			    	for(SolrLabTestDocument solrLabTestDocument : solrLabTestDocuments){
			    		SolrDoctorDocument doctorDocument = null;
			    		if(!DPDoctorUtils.anyStringEmpty(city, location)){
			    			doctorDocument = solrDoctorRepository.findLabByCityLocationName(city, location, solrLabTestDocument.getLocationId(), true);
			    		}else if(!DPDoctorUtils.anyStringEmpty(city)){
			    			doctorDocument = solrDoctorRepository.findLabByCity(city, solrLabTestDocument.getLocationId(), true);
			    		}
			    		if(doctorDocument != null){
			    			LabResponse labResponse = new LabResponse();
			    			BeanUtil.map(doctorDocument, labResponse);
			    			SolrLabTest solrLabTest = new SolrLabTest();
			    			BeanUtil.map(solrLabTestDocument, solrLabTest);
			    			labResponse.setLabTest(solrLabTest);
			    			if(labResponse.getLabTest() != null){
			    				labResponse.getLabTest().setTestName(diagnosticTest.getTestName());
			    			}
			    			if(response == null)response = new ArrayList<LabResponse>();
			    			response.add(labResponse);
			    		}
			    	}
		    	}
		    }		
		    else{
		    	SolrDoctorDocument doctorDocument = null;
		    	if(!DPDoctorUtils.anyStringEmpty(city, location)){
	    			doctorDocument = solrDoctorRepository.findLabByCityLocationName(city, location, true);
	    		}else if(!DPDoctorUtils.anyStringEmpty(city)){
	    			doctorDocument = solrDoctorRepository.findLabByCity(city, true);
	    		}
	    		if(doctorDocument != null){
	    			LabResponse labResponse = new LabResponse();
	    			BeanUtil.map(doctorDocument, labResponse);
//	    			BeanUtil.map(solrLabTestDocument, labResponse.getLabTest());
//	    			if(labResponse.getLabTest() != null){
//	    				labResponse.getLabTest().setTestName(diagnosticTest.getTestName());
//	    			}
	    			if(response == null)response = new ArrayList<LabResponse>();
	    			response.add(labResponse);
	    		}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error While Getting Labs From Solr : " + e.getMessage());
		}
		return response;

	}
}
