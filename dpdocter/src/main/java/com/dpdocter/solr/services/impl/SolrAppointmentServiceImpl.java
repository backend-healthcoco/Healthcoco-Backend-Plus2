package com.dpdocter.solr.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.solr.beans.AppointmentSearchResponse;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.solr.document.SolrLocationDocument;
import com.dpdocter.solr.document.SolrSpecialityDocument;
import com.dpdocter.solr.enums.AppointmentResponseType;
import com.dpdocter.solr.repository.SolrDoctorRepository;
import com.dpdocter.solr.repository.SolrLocationRepository;
import com.dpdocter.solr.repository.SolrSpecialityRepository;
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
    public List<AppointmentSearchResponse> search(String city, String location, String keyword) {
	List<AppointmentSearchResponse> response = null;
	try {
	    List<SolrSpecialityDocument> solrSpecialityDocuments = solrSpecialityRepository.findAll(keyword);
	    List<SolrDoctorDocument> solrDoctorDocuments = solrDoctorRepository.findAll(city, keyword);
	    List<SolrLocationDocument> solrLocationDocuments;
	    if (!DPDoctorUtils.anyStringEmpty(location)) {
		solrLocationDocuments = solrLocationRepository.findAll(location, city);
	    } else {
		solrLocationDocuments = solrLocationRepository.findAll(city);
	    }

	    response = new ArrayList<AppointmentSearchResponse>();
	    for (SolrSpecialityDocument speciality : solrSpecialityDocuments) {
		AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
		appointmentSearchResponse.setId(speciality.getId());
		appointmentSearchResponse.setResponse(speciality.getSpeciality());
		appointmentSearchResponse.setResponseType(AppointmentResponseType.SPECIALITY);
		response.add(appointmentSearchResponse);
	    }

	    for (SolrDoctorDocument doctor : solrDoctorDocuments) {
		AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
		appointmentSearchResponse.setId(doctor.getId());
		appointmentSearchResponse.setResponse(doctor.getFirstName());
		appointmentSearchResponse.setResponseType(AppointmentResponseType.DOCTOR);
		response.add(appointmentSearchResponse);
	    }

	    for (SolrLocationDocument locationDocument : solrLocationDocuments) {
		AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
		appointmentSearchResponse.setId(locationDocument.getId());
		appointmentSearchResponse.setResponse(locationDocument.getLocationName());
		appointmentSearchResponse.setResponseType(AppointmentResponseType.CLINIC);
		response.add(appointmentSearchResponse);
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
	    Criteria doctorSearchCriteria = Criteria.where("city").contains(city);

	    if (!DPDoctorUtils.anyStringEmpty(location)) {
		doctorSearchCriteria = doctorSearchCriteria.and("locationName").contains(location);
	    }

	    if (!DPDoctorUtils.anyStringEmpty(keyword)) {
		doctorSearchCriteria = doctorSearchCriteria.or("firstName").contains(keyword).or("middleName").contains(keyword).or("lastName")
			.contains(keyword).or("emailAddress").contains(keyword).or("specialization").contains(keyword);
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
	public List<SolrDoctorDocument> getDoctors(String city, String location, String speciality, String symptom,
			Boolean booking, Boolean calling, String minFee, String maxFee, String minTime, String maxTime, List<String> days,
			String gender, String minExperience, String maxExperience) {
		List<SolrDoctorDocument> response = null;
		try {
		    Criteria doctorSearchCriteria = Criteria.where("city").contains(city);

		    if (!DPDoctorUtils.anyStringEmpty(location)) {
			doctorSearchCriteria = doctorSearchCriteria.and("locationName").contains(location);
		    }

		    if (!DPDoctorUtils.anyStringEmpty(speciality)) {
			doctorSearchCriteria = doctorSearchCriteria.or("specialities").contains(speciality).or("specialization").contains(speciality);
		    }
		    
		    if (DPDoctorUtils.anyStringEmpty(minFee,maxFee)) {
				if(!DPDoctorUtils.anyStringEmpty(minFee))doctorSearchCriteria = doctorSearchCriteria.or("consultationFee").greaterThanEqual(minFee);
				if(!DPDoctorUtils.anyStringEmpty(maxFee))doctorSearchCriteria = doctorSearchCriteria.or("consultationFee").lessThanEqual(maxFee);
			}
		    else{
		    	doctorSearchCriteria = doctorSearchCriteria.or("consultationFee").greaterThanEqual(minFee).lessThanEqual(maxFee);
		    }
		    
		    if (DPDoctorUtils.anyStringEmpty(minExperience,maxExperience)) {
				if(!DPDoctorUtils.anyStringEmpty(minExperience))doctorSearchCriteria = doctorSearchCriteria.or("experience").greaterThanEqual(minExperience);
				if(!DPDoctorUtils.anyStringEmpty(maxExperience))doctorSearchCriteria = doctorSearchCriteria.or("experience").lessThanEqual(maxExperience);
			}
		    else{
		    	doctorSearchCriteria = doctorSearchCriteria.or("experience").greaterThanEqual(minExperience).lessThanEqual(maxExperience);
		    }
		    
		    if (!DPDoctorUtils.anyStringEmpty(gender)) {
				doctorSearchCriteria = doctorSearchCriteria.or("gender").is(gender);
			}
		    
		    if (DPDoctorUtils.anyStringEmpty(minTime,maxTime)) {
				if(!DPDoctorUtils.anyStringEmpty(minTime))doctorSearchCriteria = doctorSearchCriteria.or("workingSchedules").greaterThanEqual(minTime);
				if(!DPDoctorUtils.anyStringEmpty(maxTime))doctorSearchCriteria = doctorSearchCriteria.or("workingSchedules").lessThanEqual(maxTime);
			}
		    else{
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
}
