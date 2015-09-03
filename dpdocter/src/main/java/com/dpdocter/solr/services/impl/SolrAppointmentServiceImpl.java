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
    public boolean addDoctor(SolrDoctorDocument request) {
	boolean response = false;
	try {
	    solrDoctorRepository.save(request);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error While Saving Doctor Details to Solr : " + e.getMessage());
	}
	return response;
    }

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
	    List<SolrLocationDocument> solrLocationDocuments = solrLocationRepository.findAll(location, city);

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
		appointmentSearchResponse.setResponse(doctor.getFirstName() + doctor.getMiddleName() + doctor.getLastName());
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
	    Criteria doctorSearchCriteria = Criteria.where("locations").contains(city);

	    if (!DPDoctorUtils.anyStringEmpty(location)) {
		doctorSearchCriteria = doctorSearchCriteria.and("locations").contains(location);
	    }

	    if (!DPDoctorUtils.anyStringEmpty(keyword)) {
		doctorSearchCriteria = doctorSearchCriteria.and("firstName").contains(keyword).or("middleName").contains(keyword).or("lastName")
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
}
