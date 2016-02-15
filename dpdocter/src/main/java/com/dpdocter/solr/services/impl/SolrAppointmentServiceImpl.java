package com.dpdocter.solr.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.LabTest;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.solr.beans.AppointmentSearchResponse;
import com.dpdocter.solr.document.SolrCityDocument;
import com.dpdocter.solr.document.SolrDiagnosticTestDocument;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.solr.document.SolrLabTestDocument;
import com.dpdocter.solr.document.SolrLocationDocument;
import com.dpdocter.solr.document.SolrSpecialityDocument;
import com.dpdocter.solr.document.SolrSymptomsDocument;
import com.dpdocter.solr.enums.AppointmentResponseType;
import com.dpdocter.solr.repository.SolrCityRepository;
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
    private SolrCityRepository solrCityRepository;

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
    
    @Autowired
    private SpecialityRepository specialityRepository;

    @Value(value = "${IMAGE_PATH}")
    private String imagePath;

    @Override
    public boolean addLocation(List<SolrLocationDocument> request) {
	boolean response = false;
	try {
	    solrLocationRepository.save(request);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Forbidden, "Error While Saving Location Details to Solr : " + e.getMessage());
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
	    throw new BusinessException(ServiceError.Forbidden, "Error While Saving Speciality Details to Solr : " + e.getMessage());
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
	    if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (DPDoctorUtils.allStringsEmpty(city, location)) {
		    if (DPDoctorUtils.allStringsEmpty(latitude, longitude))
			solrDoctorDocuments = solrDoctorRepository.findAll(searchTerm);
		    else {
			if (latitude != null && longitude != null)
			    solrDoctorDocuments = solrDoctorRepository.findByLatitudeLongitude(latitude, longitude, searchTerm);
		    }
		} else {
		    if (city != null && location != null)
			solrDoctorDocuments = solrDoctorRepository.findByCityLocation(city, location, searchTerm);
		    else if (city != null)
			solrDoctorDocuments = solrDoctorRepository.findByCity(city, searchTerm);
		    else if (location != null)
			solrDoctorDocuments = solrDoctorRepository.findByLocation(location, searchTerm);
		}
	    } else {
		if (DPDoctorUtils.allStringsEmpty(city, location)) {
		    if (latitude != null && longitude != null)
			solrDoctorDocuments = solrDoctorRepository.findByLatitudeLongitude(latitude, longitude);

		} else {
		    if (city != null && location != null)
			solrDoctorDocuments = solrDoctorRepository.findByCityLocation(city, location);
		    else if (city != null)
			solrDoctorDocuments = solrDoctorRepository.findByCity(city);
		    else if (location != null)
			solrDoctorDocuments = solrDoctorRepository.findByLocation(location);
		}
	    }

	    List<SolrDoctorDocument> solrLocationDocuments = null;
	    if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
		if (DPDoctorUtils.allStringsEmpty(city, location)) {
		    if (DPDoctorUtils.allStringsEmpty(latitude, longitude))
			solrLocationDocuments = solrDoctorRepository.findByLocationName(searchTerm);
		    else {
			if (latitude != null && longitude != null)
			    solrLocationDocuments = solrDoctorRepository.findByLatitudeLongitudeLocation(latitude, longitude, searchTerm);
		    }
		} else {
		    if (city != null && location != null)
			solrLocationDocuments = solrDoctorRepository.findByCityLocationName(city, location, searchTerm);
		    else if (city != null)
			solrDoctorDocuments = solrDoctorRepository.findByCityLocationName(city, searchTerm);
		    else if (location != null)
			solrDoctorDocuments = solrDoctorRepository.findByLocationLocationName(location, searchTerm);
		}
	    } else {
		if (DPDoctorUtils.allStringsEmpty(city, location)) {
		    if (latitude != null && longitude != null)
			solrLocationDocuments = solrDoctorRepository.findByLatitudeLongitude(latitude, longitude);

		} else {
		    if (city != null && location != null)
			solrDoctorDocuments = solrDoctorRepository.findByCityLocation(city, location);
		    else if (city != null)
			solrDoctorDocuments = solrDoctorRepository.findByCity(city);
		    else if (location != null)
			solrDoctorDocuments = solrDoctorRepository.findByLocation(location);
		}
	    }

	    List<SolrDiagnosticTestDocument> diagnosticTestDocuments = solrDiagnosticTestRepository.findAll(searchTerm);

	    response = new ArrayList<AppointmentSearchResponse>();
	    if (solrSpecialityDocuments != null)
		for (SolrSpecialityDocument speciality : solrSpecialityDocuments) {
		    AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
		    appointmentSearchResponse.setId(speciality.getId());
		    appointmentSearchResponse.setResponse(speciality.getSpeciality());
		    appointmentSearchResponse.setResponseType(AppointmentResponseType.SPECIALITY);
		    response.add(appointmentSearchResponse);
		}

	    if (solrSymptomsDocuments != null)
		for (SolrSymptomsDocument symptom : solrSymptomsDocuments) {
		    AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
		    appointmentSearchResponse.setId(symptom.getId());
		    appointmentSearchResponse.setResponse(symptom.getSymptom());
		    appointmentSearchResponse.setResponseType(AppointmentResponseType.SYMPTOM);
		    response.add(appointmentSearchResponse);
		}

	    if (diagnosticTestDocuments != null)
		for (SolrDiagnosticTestDocument diagnosticTest : diagnosticTestDocuments) {
		    AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
		    appointmentSearchResponse.setId(diagnosticTest.getId());
		    appointmentSearchResponse.setResponse(diagnosticTest.getTestName());
		    appointmentSearchResponse.setResponseType(AppointmentResponseType.LABTEST);
		    response.add(appointmentSearchResponse);
		}

	    if (solrDoctorDocuments != null)
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

	    if (solrLocationDocuments != null)
		for (SolrDoctorDocument locationDocument : solrLocationDocuments) {
		    if (!locationDocument.getIsLab()) {
			AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
			appointmentSearchResponse.setId(locationDocument.getLocationId());
			appointmentSearchResponse.setResponse(locationDocument.getLocationName());
			appointmentSearchResponse.setResponseType(AppointmentResponseType.CLINIC);
			response.add(appointmentSearchResponse);
		    }
		}

	    if (solrLocationDocuments != null)
		for (SolrDoctorDocument locationDocument : solrLocationDocuments) {
		    if (locationDocument.getIsLab()) {
			AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
			appointmentSearchResponse.setId(locationDocument.getLocationId());
			appointmentSearchResponse.setResponse(locationDocument.getLocationName());
			appointmentSearchResponse.setResponseType(AppointmentResponseType.LAB);
			response.add(appointmentSearchResponse);
		    }
		}
	    
	    if(solrLocationDocuments != null)
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
    public List<SolrDoctorDocument> getDoctors(String city, String location, String latitude, String longitude, String speciality, String symptom, Boolean booking, Boolean calling,
	    String minFee, String maxFee, String minTime, String maxTime, List<String> days, String gender, String minExperience, String maxExperience) {
	List<SolrDoctorDocument> solrDoctorDocuments = null;
	try {
	    Criteria doctorSearchCriteria = null;
	    
	    if(DPDoctorUtils.anyStringEmpty(longitude, latitude) && !DPDoctorUtils.anyStringEmpty(city)){
	    	SolrCityDocument solrCityDocument = solrCityRepository.findByName(city);
	    	if(solrCityDocument != null){
	    		latitude = solrCityDocument.getLatitude()+"";
	    		longitude = solrCityDocument.getLongitude()+"";
	    	}
	    }
	    
	    if(!DPDoctorUtils.anyStringEmpty(longitude, latitude)){
	    	doctorSearchCriteria = Criteria.where("geoLocation").near(new Point(Double.parseDouble(latitude), Double.parseDouble(longitude)), new Distance(10));
	    }
	    
	    if(doctorSearchCriteria == null){
	    	doctorSearchCriteria = new Criteria();
	    }
	    if (!DPDoctorUtils.anyStringEmpty(location)) {
		doctorSearchCriteria = doctorSearchCriteria.and("locationName").is(location);
	    }

	    if (!DPDoctorUtils.anyStringEmpty(speciality)) {
		doctorSearchCriteria = doctorSearchCriteria.and("specialities").is(speciality);
	    }

	    if (!DPDoctorUtils.anyStringEmpty(symptom)) {
			List<SolrSymptomsDocument> solrSymptomsDocuments = solrSymptomRepository.findAll(symptom);
			@SuppressWarnings("unchecked")
		    Collection<String> specialityIds = CollectionUtils.collect(solrSymptomsDocuments, new BeanToPropertyValueTransformer("specialityId"));
//			List<SolrSpecialityDocument> solrSpecialityDocuments = solrSpecialityRepository.findByIds(specialityIds);
//			
//			@SuppressWarnings("unchecked")
//		    Collection<String> specialities = CollectionUtils.collect(solrSpecialityDocuments, new BeanToPropertyValueTransformer("speciality"));
			doctorSearchCriteria = doctorSearchCriteria.and("specialities").in(specialityIds);
		}
	    
	    if (DPDoctorUtils.anyStringEmpty(minFee, maxFee)) {
		if (!DPDoctorUtils.anyStringEmpty(minFee))
		    doctorSearchCriteria = doctorSearchCriteria.and("consultationFeeAmount").greaterThanEqual(minFee);
		if (!DPDoctorUtils.anyStringEmpty(maxFee))
		    doctorSearchCriteria = doctorSearchCriteria.and("consultationFeeAmount").lessThanEqual(maxFee);
	    } else {
		doctorSearchCriteria = doctorSearchCriteria.and("consultationFeeAmount").greaterThanEqual(minFee).lessThanEqual(maxFee);
	    }

	    if (DPDoctorUtils.anyStringEmpty(minExperience, maxExperience)) {
		if (!DPDoctorUtils.anyStringEmpty(minExperience))
		    doctorSearchCriteria = doctorSearchCriteria.and("experienceNum").greaterThanEqual(minExperience);
		if (!DPDoctorUtils.anyStringEmpty(maxExperience))
		    doctorSearchCriteria = doctorSearchCriteria.and("experienceNum").lessThanEqual(maxExperience);
	    } else {
		doctorSearchCriteria = doctorSearchCriteria.and("experienceNum").greaterThanEqual(minExperience).lessThanEqual(maxExperience);
	    }

	    if (!DPDoctorUtils.anyStringEmpty(gender)) {
		doctorSearchCriteria = doctorSearchCriteria.and("gender").is(gender);
	    }

	    if (DPDoctorUtils.anyStringEmpty(minTime, maxTime)) {
		if (!DPDoctorUtils.anyStringEmpty(minTime))
		    doctorSearchCriteria = doctorSearchCriteria.and("workingSchedules").greaterThanEqual(minTime);
		if (!DPDoctorUtils.anyStringEmpty(maxTime))
		    doctorSearchCriteria = doctorSearchCriteria.and("workingSchedules").lessThanEqual(maxTime);
	    } else {
		doctorSearchCriteria = doctorSearchCriteria.and("workingSchedules").greaterThanEqual(minTime).lessThanEqual(maxTime);
	    }

	    if (days != null && !days.isEmpty()) {
		doctorSearchCriteria = doctorSearchCriteria.and("workingSchedules").in(days);
	    }

	    SimpleQuery query = new SimpleQuery(doctorSearchCriteria);
	    solrTemplate.setSolrCore("doctors");

	    solrDoctorDocuments = solrTemplate.queryForPage(query, SolrDoctorDocument.class).getContent();
	    if(solrDoctorDocuments != null){
	    	for(SolrDoctorDocument doctorDocument : solrDoctorDocuments){
	    		if(doctorDocument.getSpecialities() != null){
	    			List<String> specialities = new ArrayList<>();
	    			for(String specialityId : doctorDocument.getSpecialities()){
	    				SpecialityCollection specialityCollection = specialityRepository.findOne(specialityId);
	    				if(specialityCollection != null)specialities.add(specialityCollection.getSpeciality());
	    	         }
	    			doctorDocument.setSpecialities(specialities);
	            }
	    		if(doctorDocument.getImageUrl() != null)doctorDocument.setImageUrl(getFinalImageURL(doctorDocument.getImageUrl()));
	    	    if (doctorDocument.getImages() != null && !doctorDocument.getImages().isEmpty()) {
	    	    	List<String> images = new ArrayList<String>();
	    			for (String clinicImage : doctorDocument.getImages()) {
	    			    images.add(clinicImage);
	    			}
	    			doctorDocument.setImages(images);
	    		    }
	    		    if (doctorDocument.getLogoUrl() != null)doctorDocument.setLogoUrl(getFinalImageURL(doctorDocument.getLogoUrl()));
	    		    
	    		    if(latitude != null && longitude != null && doctorDocument.getLatitude() != null && doctorDocument.getLongitude() != null){
	    		    	doctorDocument.setDistance(DPDoctorUtils.distance(Double.parseDouble(latitude), Double.parseDouble(longitude), doctorDocument.getLatitude(), doctorDocument.getLongitude(), "K"));
	    		    }
	    		    doctorDocument.getExperience();doctorDocument.getDob();doctorDocument.getConsultationFee();
	  }
	 }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Forbidden, "Error While Getting Doctor Details From Solr : " + e.getMessage());
	}
	return solrDoctorDocuments;
    }

	@Override
	public List<LabResponse> getLabs(String city, String location, String latitude, String longitude, String test) {
		List<LabResponse> response = null;
		List<SolrLabTestDocument> solrLabTestDocuments = null;
		try {
		    if (!DPDoctorUtils.anyStringEmpty(test)) {
		    	List<SolrDiagnosticTestDocument> diagnosticTests = solrDiagnosticTestRepository.findAll(test);
		    	if(diagnosticTests != null){
		    		@SuppressWarnings("unchecked")
		    	    Collection<String> testIds = CollectionUtils.collect(diagnosticTests, new BeanToPropertyValueTransformer("id"));
		    		solrLabTestDocuments = solrLabTestRepository.findByTestIds(testIds);
			    	for(SolrLabTestDocument solrLabTestDocument : solrLabTestDocuments){
			    		List<SolrDoctorDocument> doctorDocument = null;
			    		if(!DPDoctorUtils.anyStringEmpty(longitude, latitude)){
			    			doctorDocument = solrDoctorRepository.findLabByLatLong(latitude, longitude, solrLabTestDocument.getLocationId(), true);
			    	    }
			    		else if(!DPDoctorUtils.anyStringEmpty(city, location)){
			    			doctorDocument = solrDoctorRepository.findLabByCityLocationName(city, location, solrLabTestDocument.getLocationId(), true);
			    		}else if(!DPDoctorUtils.anyStringEmpty(city)){
			    			doctorDocument = solrDoctorRepository.findLabByCity(city, solrLabTestDocument.getLocationId(), true);
			    		}
			    		if(doctorDocument != null && !doctorDocument.isEmpty()){
			    			for(SolrDoctorDocument document : doctorDocument){
			    				LabResponse labResponse = new LabResponse();
				    			BeanUtil.map(document, labResponse);
				    		    LabTest solrLabTest = new LabTest();
				    			BeanUtil.map(solrLabTestDocument, solrLabTest);
				    			labResponse.setLabTest(solrLabTest);
				    			if(labResponse.getLabTest() != null){
				    				if(solrLabTestDocument.getTestId() != null){
				    					SolrDiagnosticTestDocument testDocument = solrDiagnosticTestRepository.findOne(solrLabTest.getId());
				    					DiagnosticTest diagnosticTest = new DiagnosticTest();
				    					BeanUtil.map(testDocument, diagnosticTest);
				    					labResponse.getLabTest().setTest(diagnosticTest);
				    				}
				    			}
				    			if(response == null)response = new ArrayList<LabResponse>();
				    			response.add(labResponse);
			    			}
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
		    throw new BusinessException(ServiceError.Forbidden, "Error While Getting Labs From Solr : " + e.getMessage());
		}
		return response;

	}
	
    private String getFinalImageURL(String imageURL) {
	if (imageURL != null) {
	    return imagePath + imageURL;
	} else
	    return null;

    }
}
