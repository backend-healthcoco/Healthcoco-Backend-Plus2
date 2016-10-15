package com.dpdocter.elasticsearch.services.impl;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.elasticsearch.beans.AppointmentSearchResponse;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESLabTestDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceCostDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceDocument;
import com.dpdocter.elasticsearch.repository.ESCityRepository;
import com.dpdocter.elasticsearch.repository.ESComplaintsRepository;
import com.dpdocter.elasticsearch.repository.ESDiagnosticTestRepository;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESSpecialityRepository;
import com.dpdocter.elasticsearch.repository.ESTreatmentServiceRepository;
import com.dpdocter.elasticsearch.response.LabResponse;
import com.dpdocter.elasticsearch.services.ESAppointmentService;
import com.dpdocter.enums.AppointmentResponseType;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;

import common.util.web.DPDoctorUtils;

@Service
public class ESAppointmentServiceImpl implements ESAppointmentService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ESCityRepository esCityRepository;

    @Autowired
    private ESDoctorRepository esDoctorRepository;

    @Autowired
    private ESSpecialityRepository esSpecialityRepository;

    @Autowired
    private ESComplaintsRepository esComplaintsRepository;

    @Autowired
    private ESDiagnosticTestRepository esDiagnosticTestRepository;

    @Autowired
    private ESTreatmentServiceRepository esTreatmentServiceRepository;

    @Value(value = "${image.path}")
    private String imagePath;

    @Override
    public List<AppointmentSearchResponse> search(String city, String location, String latitude, String longitude, String searchTerm) {
	List<AppointmentSearchResponse> response = null;
	try {
	    List<ESSpecialityDocument> esSpecialityDocuments = esSpecialityRepository.findByQueryAnnotation(searchTerm);

	    response = new ArrayList<AppointmentSearchResponse>();
	    if (esSpecialityDocuments != null)
		for (ESSpecialityDocument speciality : esSpecialityDocuments) {
			if(response.size() >= 50)break;
		    AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
		    appointmentSearchResponse.setId(speciality.getId());
		    appointmentSearchResponse.setResponse(speciality.getSuperSpeciality());
		    appointmentSearchResponse.setResponseType(AppointmentResponseType.SPECIALITY);
		    response.add(appointmentSearchResponse);
		}

	    if(response.size() < 50){
	    	List<ESComplaintsDocument> complaintsDocuments = esComplaintsRepository.findByComplaint(searchTerm);
	    	if (complaintsDocuments != null)
	    		for (ESComplaintsDocument esComplaintsDocument : complaintsDocuments) {
	    			if(response.size() >= 50)break;
	    		    AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
	    		    appointmentSearchResponse.setId(esComplaintsDocument.getId());
	    		    appointmentSearchResponse.setResponse(esComplaintsDocument);
	    		    appointmentSearchResponse.setResponseType(AppointmentResponseType.SYMPTOM);
	    		    response.add(appointmentSearchResponse);
	    		}
	    }

	    if(response.size() < 50){
	    	List<ESDiagnosticTestDocument> diagnosticTestDocuments = esDiagnosticTestRepository.findByTestName(searchTerm);
	    	if (diagnosticTestDocuments != null)
	    		for (ESDiagnosticTestDocument diagnosticTest : diagnosticTestDocuments) {
	    			if(response.size() >= 50)break;
	    		    AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
	    		    appointmentSearchResponse.setId(diagnosticTest.getId());
	    		    appointmentSearchResponse.setResponse(diagnosticTest.getTestName());
	    		    appointmentSearchResponse.setResponseType(AppointmentResponseType.LABTEST);
	    		    response.add(appointmentSearchResponse);
	    		}
	    }

	    if(response.size() < 50){
	    	List<ESTreatmentServiceDocument> treatmentServiceDocuments = esTreatmentServiceRepository.findByName(searchTerm);
	    	if (treatmentServiceDocuments != null)
	    		for (ESTreatmentServiceDocument esTreatmentServiceDocument : treatmentServiceDocuments) {
	    			if(response.size() >= 50)break;
	    		    AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
	    		    appointmentSearchResponse.setId(esTreatmentServiceDocument.getId());
	    		    appointmentSearchResponse.setResponse(esTreatmentServiceDocument.getName());
	    		    appointmentSearchResponse.setResponseType(AppointmentResponseType.SERVICE);
	    		    response.add(appointmentSearchResponse);
	    		}
	    }

	    if(response.size() < 50){
		    List<ESDoctorDocument> esDoctorDocuments = null;
		    if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			if (DPDoctorUtils.allStringsEmpty(city, location)) {
			    if (DPDoctorUtils.allStringsEmpty(latitude, longitude))
				esDoctorDocuments = esDoctorRepository.findByFirstName(searchTerm);
			    else {
				if (latitude != null && longitude != null){
					BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude)).lon(Double.parseDouble(longitude)).distance("30km"))
							.must(QueryBuilders.matchPhrasePrefixQuery("firstName", searchTerm));
					esDoctorDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(0, 50 - response.size())).build(), ESDoctorDocument.class);
				}
				    
			    }
			} else {
			    if (city != null && location != null)
				esDoctorDocuments = esDoctorRepository.findByCityLocation(city, location, searchTerm);
			    else if (city != null)
				esDoctorDocuments = esDoctorRepository.findByCity(city, searchTerm);
			    else if (location != null)
				esDoctorDocuments = esDoctorRepository.findByLocation(location, searchTerm);
			}
		    } else {
			if (DPDoctorUtils.allStringsEmpty(city, location)) {
			    if (latitude != null && longitude != null){
			    	BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude)).lon(Double.parseDouble(longitude)).distance("30km"));
			    	esDoctorDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(0, 50 - response.size())).build(), ESDoctorDocument.class);
			    }

			} else {
			    if (city != null && location != null)
				esDoctorDocuments = esDoctorRepository.findByCityLocation(city, location);
			    else if (city != null)
				esDoctorDocuments = esDoctorRepository.findByCity(city);
			    else if (location != null)
				esDoctorDocuments = esDoctorRepository.findByLocation(location);
			}
		    }

		    if (esDoctorDocuments != null)
				for (ESDoctorDocument doctor : esDoctorDocuments) {
					if(response.size() >= 50)break;
				    AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
				    appointmentSearchResponse.setId(doctor.getUserId());
				    ESDoctorDocument object = new ESDoctorDocument();
				    object.setUserId(doctor.getUserId());
				    object.setFirstName(doctor.getFirstName());
				    object.setLocationId(doctor.getLocationId());
				    object.setHospitalId(doctor.getHospitalId());
				    appointmentSearchResponse.setResponse(object);
				    appointmentSearchResponse.setResponseType(AppointmentResponseType.DOCTOR);
				    response.add(appointmentSearchResponse);
				}

	    }

	    if(response.size() < 50){
	    	List<ESDoctorDocument> esLocationDocuments = null;
		    if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			if (DPDoctorUtils.allStringsEmpty(city, location)) {
			    if (DPDoctorUtils.allStringsEmpty(latitude, longitude))
				esLocationDocuments = esDoctorRepository.findByLocationName(searchTerm);
			    else {
				if (latitude != null && longitude != null){
					BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude)).lon(Double.parseDouble(longitude)).distance("30km"))
							.must(QueryBuilders.matchPhrasePrefixQuery("locationName", searchTerm));
					esLocationDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(0, 50 - response.size())).build(), ESDoctorDocument.class);
				}
			    }
			} else {
			    if (city != null && location != null)
				esLocationDocuments = esDoctorRepository.findByCityLocationName(city, location, searchTerm);
			    else if (city != null)
			    	esLocationDocuments = esDoctorRepository.findByCityLocationName(city, searchTerm);
			    else if (location != null)
			    	esLocationDocuments = esDoctorRepository.findByLocationLocationName(location, searchTerm);
			}
		    } else {
			if (DPDoctorUtils.allStringsEmpty(city, location)) {
			    if (latitude != null && longitude != null){
			    	BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude)).lon(Double.parseDouble(longitude)).distance("30km"));
					esLocationDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(0, 50 - response.size())).build(), ESDoctorDocument.class);	
			    } else {
			    }
			    if (city != null && location != null)
			    	esLocationDocuments = esDoctorRepository.findByCityLocation(city, location);
			    else if (city != null)
			    	esLocationDocuments = esDoctorRepository.findByCity(city);
			    else if (location != null)
			    	esLocationDocuments = esDoctorRepository.findByLocation(location);
			}
		    }	    

		    if (esLocationDocuments != null)
				for (ESDoctorDocument locationDocument : esLocationDocuments) {
				    if (locationDocument.getIsClinic()) {
				    	if(response.size() >= 50)break;
					AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
					appointmentSearchResponse.setId(locationDocument.getLocationId());
					appointmentSearchResponse.setResponse(locationDocument.getLocationName());
					appointmentSearchResponse.setResponseType(AppointmentResponseType.CLINIC);
					response.add(appointmentSearchResponse);
				    }
				}

			    if (esLocationDocuments != null)
				for (ESDoctorDocument locationDocument : esLocationDocuments) {
					if(response.size() >= 50)break;
				    if (locationDocument.getIsLab()) {
					AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
					appointmentSearchResponse.setId(locationDocument.getLocationId());
					appointmentSearchResponse.setResponse(locationDocument.getLocationName());
					appointmentSearchResponse.setResponseType(AppointmentResponseType.LAB);
					response.add(appointmentSearchResponse);
				    }
				}
	    }
	    	
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return response;
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<ESDoctorDocument> getDoctors(int page, int size, String city, String location, String latitude, String longitude, String speciality, String symptom, 
    		Boolean booking, Boolean calling, int minFee, int maxFee, int minTime, int maxTime, List<String> days, String gender, int minExperience, int maxExperience, String service) {
	List<ESDoctorDocument> esDoctorDocuments = null;
	List<ESTreatmentServiceCostDocument> esTreatmentServiceCostDocuments = null;
	try {
	    BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
	    if(DPDoctorUtils.anyStringEmpty(longitude, latitude) && !DPDoctorUtils.anyStringEmpty(city)){
	    	ESCityDocument esCityDocument = esCityRepository.findByName(city);
	    	if(esCityDocument != null){
	    		latitude = esCityDocument.getLatitude()+"";
	    		longitude = esCityDocument.getLongitude()+"";
	    	}
	    }
	    if (!DPDoctorUtils.anyStringEmpty(service)) {
			List<ESTreatmentServiceDocument> esTreatmentServiceDocuments = esTreatmentServiceRepository.findByName(service);
			if (esTreatmentServiceDocuments != null) {
				Collection<String> serviceIds = CollectionUtils.collect(esTreatmentServiceDocuments, new BeanToPropertyValueTransformer("id"));
				int count = (int) elasticsearchTemplate.count(new CriteriaQuery(new Criteria("treatmentServiceId").in(serviceIds)), ESTreatmentServiceCostDocument.class);
			    if(count > 0)esTreatmentServiceCostDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder().withQuery(QueryBuilders.termsQuery("treatmentServiceId", serviceIds)).withPageable(new PageRequest(0, count)).build(), ESTreatmentServiceCostDocument.class); 
			}
		    if(esTreatmentServiceCostDocuments == null || esTreatmentServiceCostDocuments.isEmpty()){return null;}		
	        Collection<String> locationIds = CollectionUtils.collect(esTreatmentServiceCostDocuments, new BeanToPropertyValueTransformer("locationId"));
	        
	        Collection<String> doctorIds = CollectionUtils.collect(esTreatmentServiceCostDocuments, new BeanToPropertyValueTransformer("doctorId"));
	    	boolQueryBuilder.must(QueryBuilders.termQuery("userId", doctorIds)).must(QueryBuilders.termsQuery("locationId", locationIds));
		 }
	    
	    if (!DPDoctorUtils.anyStringEmpty(symptom)) {
			List<ESComplaintsDocument> esComplaintsDocuments = esComplaintsRepository.findByComplaint(symptom);
		    if(esComplaintsDocuments == null || esComplaintsDocuments.isEmpty()){return null;}		
	        Collection<String> locationIds = CollectionUtils.collect(esComplaintsDocuments, new BeanToPropertyValueTransformer("locationId"));
	        
	        Collection<String> doctorIds = CollectionUtils.collect(esComplaintsDocuments, new BeanToPropertyValueTransformer("doctorId"));
	    	boolQueryBuilder.must(QueryBuilders.termQuery("userId", doctorIds)).must(QueryBuilders.termsQuery("locationId", locationIds));
		 }
	    
	    if (!DPDoctorUtils.anyStringEmpty(speciality)) {
			List<ESSpecialityDocument> esSpecialityDocuments = esSpecialityRepository.findByQueryAnnotation(speciality);
			if (esSpecialityDocuments != null) {
			    Collection<String> specialityIds = CollectionUtils.collect(esSpecialityDocuments, new BeanToPropertyValueTransformer("id"));
			    if (specialityIds == null) specialityIds = CollectionUtils.EMPTY_COLLECTION;
			    	boolQueryBuilder.must(QueryBuilders.termsQuery("specialities", specialityIds));
//				}
			}
		}
	    
	    if (!DPDoctorUtils.anyStringEmpty(location)) {
	    	boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("locationName", location));
		   }
	    if(booking != null && booking){
	    	boolQueryBuilder.must(QueryBuilders.termsQuery("facility", DoctorFacility.BOOK.getType().toLowerCase(), DoctorFacility.IBS.getType().toLowerCase()));
	    }
	    if(calling != null && calling)boolQueryBuilder.must(QueryBuilders.matchQuery("facility", DoctorFacility.CALL.getType()));

	    if (minFee != 0 && maxFee != 0)
	    	boolQueryBuilder.must(QueryBuilders.nestedQuery("consultationFee", boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(minFee).to(maxFee))));
	    else if (minFee != 0)
			boolQueryBuilder.must(QueryBuilders.nestedQuery("consultationFee", boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(minFee))));
	    else if (maxFee != 0)
			boolQueryBuilder.must(QueryBuilders.nestedQuery("consultationFee", boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").to(maxFee))));

	    if (minExperience != 0 && maxExperience != 0) 
	    	boolQueryBuilder.must(QueryBuilders.nestedQuery("experience", boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(minExperience).to(maxExperience))));
	    else if (minExperience != 0)
			boolQueryBuilder.must(QueryBuilders.nestedQuery("experience", boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(minExperience))));
	    else if(maxExperience != 0)
			boolQueryBuilder.must(QueryBuilders.nestedQuery("experience", boolQuery().must(QueryBuilders.rangeQuery("experience.experience").to(maxExperience))));

	    if (!DPDoctorUtils.anyStringEmpty(gender)) {
	    	boolQueryBuilder.must(QueryBuilders.matchQuery("gender", gender));
	    }

	    if(minTime != 0 && maxTime != 0) 
	    	boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules", boolQuery().must(nestedQuery("workingSchedules.workingHours", boolQuery().must(QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").from(minTime)).must(QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").to(maxTime))))));
	    else if(minTime != 0)
			boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules", boolQuery().must(nestedQuery("workingSchedules.workingHours", boolQuery().must(QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").from(minTime))))));
	    else if(maxTime != 0)
				boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules", boolQuery().must(nestedQuery("workingSchedules.workingHours", boolQuery().must(QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").to(maxTime))))));

	    if (days != null && !days.isEmpty()) {
	    	for(int i = 0; i < days.size();i++)days.set(i, days.get(i).toLowerCase());
	    	boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules", boolQuery().must(QueryBuilders.termsQuery("workingSchedules.workingDay", "monday"))));
		}

	    if(latitude != null && longitude != null)boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude)).lon(Double.parseDouble(longitude)).distance("30km"));
	    
	    SearchQuery searchQuery = null;
	    if (size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size)).build();
	    else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
		esDoctorDocuments = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
	    
	    if (esDoctorDocuments != null) {
		for (ESDoctorDocument doctorDocument : esDoctorDocuments) {
		    if (doctorDocument.getSpecialities() != null) {
			List<String> specialities = new ArrayList<>();
			for (String specialityId : doctorDocument.getSpecialities()) {
			    ESSpecialityDocument specialityCollection = esSpecialityRepository.findOne(specialityId);
			    if (specialityCollection != null)
				specialities.add(specialityCollection.getSuperSpeciality());
			}
			doctorDocument.setSpecialities(specialities);
		    }
		    if (doctorDocument.getImageUrl() != null)
			doctorDocument.setImageUrl(getFinalImageURL(doctorDocument.getImageUrl()));
		    if (doctorDocument.getImages() != null && !doctorDocument.getImages().isEmpty()) {
			List<String> images = new ArrayList<String>();
			for (String clinicImage : doctorDocument.getImages()) {
			    images.add(clinicImage);
			}
			doctorDocument.setImages(images);
		    }
		    if (doctorDocument.getLogoUrl() != null)
			doctorDocument.setLogoUrl(getFinalImageURL(doctorDocument.getLogoUrl()));

		    if (latitude != null && longitude != null && doctorDocument.getLatitude() != null && doctorDocument.getLongitude() != null) {
			doctorDocument.setDistance(DPDoctorUtils.distance(Double.parseDouble(latitude), Double.parseDouble(longitude),
				doctorDocument.getLatitude(), doctorDocument.getLongitude(), "K"));
		    }
		    doctorDocument.getDob();
		  }
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error While Getting Doctor Details From ES : " + e.getMessage());
	}
	return esDoctorDocuments;
    }

    @Override
    public List<LabResponse> getLabs(int page, int size, String city, String location, String latitude, String longitude, String test, Boolean booking, Boolean calling,
    		int minTime, int maxTime, List<String> days, Boolean onlineReports, Boolean homeService, Boolean nabl) {
	List<LabResponse> response = null;
	List<ESLabTestDocument> esLabTestDocuments = null;
	try {
	    if(DPDoctorUtils.anyStringEmpty(longitude, latitude) && !DPDoctorUtils.anyStringEmpty(city)){
	    	ESCityDocument esCityDocument = esCityRepository.findByName(city);
	    	if(esCityDocument != null){
	    		latitude = esCityDocument.getLatitude()+"";
	    		longitude = esCityDocument.getLongitude()+"";
	    	}
	    }    	    
        
	    if (!DPDoctorUtils.anyStringEmpty(test)) {
		List<ESDiagnosticTestDocument> diagnosticTests = esDiagnosticTestRepository.findByTestName(test);
		if (diagnosticTests != null) {
			@SuppressWarnings("unchecked")
		    Collection<String> testIds = CollectionUtils.collect(diagnosticTests, new BeanToPropertyValueTransformer("id"));
			int count = (int) elasticsearchTemplate.count(new CriteriaQuery(new Criteria("testId").in(testIds)), ESLabTestDocument.class);
		    if(count > 0)esLabTestDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder().withQuery(QueryBuilders.termsQuery("testId", testIds)).withPageable(new PageRequest(0, count)).build(), ESLabTestDocument.class); 
		}
	    }
	    if(esLabTestDocuments == null || esLabTestDocuments.isEmpty()){return null;}
	    List<ESDoctorDocument> doctorDocument = null;
		
        @SuppressWarnings("unchecked")
    	Collection<String> locationIds = CollectionUtils.collect(esLabTestDocuments, new BeanToPropertyValueTransformer("locationId"));
    	
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.termsQuery("locationId", locationIds)).must(QueryBuilders.termQuery("isLab", true));
        if(booking != null && booking)boolQueryBuilder.must(QueryBuilders.termQuery("facility", DoctorFacility.BOOK.getType()));
	    if(calling != null && calling)boolQueryBuilder.must(QueryBuilders.termQuery("facility", DoctorFacility.CALL.getType()));

        if(onlineReports != null)boolQueryBuilder.must(QueryBuilders.termQuery("isOnlineReportsAvailable", onlineReports));
	    if(homeService != null)boolQueryBuilder.must(QueryBuilders.termQuery("isHomeServiceAvailable", homeService));
        if(nabl != null)boolQueryBuilder.must(QueryBuilders.termQuery("isNABLAccredited", nabl));

	    if(minTime != 0 && maxTime != 0) 
	    	boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules", boolQuery().must(nestedQuery("workingSchedules.workingHours", boolQuery().must(termQuery("workingSchedules.workingHours.fromTime", minTime)).must(termQuery("workingSchedules.workingHours.toTime", maxTime))))));
	    else if(minTime != 0)
			boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules", boolQuery().must(nestedQuery("workingSchedules.workingHours", boolQuery().must(termQuery("workingSchedules.workingHours.fromTime", minTime))))));
	    else if(maxTime != 0)
				boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules", boolQuery().must(nestedQuery("workingSchedules.workingHours", boolQuery().must(termQuery("workingSchedules.workingHours.toTime", maxTime))))));

	    if (days != null && !days.isEmpty()) {
	    	boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules", boolQuery().must(QueryBuilders.termsQuery("workingSchedules.workingDay",days))));
		}

        boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude)).lon(Double.parseDouble(longitude)).distance("30km"));
	    
	    SearchQuery searchQuery = null;
	    if (size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
	    		.addAggregation(AggregationBuilders.terms("keys").field("locationId").size(Integer.MAX_VALUE)
	    				.subAggregation(AggregationBuilders.topHits("locations").setSize(1))).withPageable(new PageRequest(page, size)).build();
	    /*
	     * SearchQuery searchQuery = new NativeSearchQueryBuilder()
    .withQuery(boolQueryBuilder)
    .addAggregation(AggregationBuilders
    .terms("addressId")
    .field("addressId")
    .size(Integer.MAX_VALUE)
    .subaggregation(AggregationBuilders.topHits("justOneUserPerAddressId)
         .setSize(1)
    )
    .withPageable(new PageRequest(page, size)).build();
	     */
	    
	    else searchQuery = new NativeSearchQueryBuilder()
	    		
	    		.addAggregation(AggregationBuilders
	    				.terms("keys")
	    				.field("locationId")
	    				.size(Integer.MAX_VALUE)
	    				.subAggregation(AggregationBuilders.topHits("hits").setSize(1)))
	    		.withQuery(boolQueryBuilder)
	    		.build();
			
	    if (searchQuery.getQuery() != null) {
	        System.out.println(searchQuery.getQuery().toString());
	    }
	    if (searchQuery.getAggregations() != null) {
	            XContentBuilder builder = XContentFactory.contentBuilder(XContentType.JSON);
	            builder.startObject();
	            for (AbstractAggregationBuilder subAgg : searchQuery.getAggregations()) {
	                subAgg.toXContent(builder, ToXContent.EMPTY_PARAMS);
	            }
	            builder.endObject();
	            System.out.println(builder.string());

	    }
	    doctorDocument = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
//	    for(ESLabTestDocument labTestDocument : esLabTestDocuments){
	    	if (doctorDocument != null && !doctorDocument.isEmpty()) {
				for (ESDoctorDocument document : doctorDocument) {
//					if(labTestDocument.getLocationId().equalsIgnoreCase(document.getLocationId())){
						LabResponse labResponse = new LabResponse();
						BeanUtil.map(document, labResponse);
//						LabTest esLabTest = new LabTest();
//						BeanUtil.map(labTestDocument, esLabTest);
//						labResponse.setLabTest(esLabTest);
//						if (labResponse.getLabTest() != null) {
//						    if (labTestDocument.getTestId() != null) {
//							ESDiagnosticTestDocument testDocument = esDiagnosticTestRepository.findOne(labTestDocument.getTestId());
//							DiagnosticTest diagnosticTest = new DiagnosticTest();
//							if(testDocument != null){
//								BeanUtil.map(testDocument, diagnosticTest);
//							}
//							labResponse.getLabTest().setTest(diagnosticTest);
//						    }
//						}
						List<String> images = new ArrayList<String>();
						if(document.getImages() != null)
						for (String clinicImage : document.getImages()) {
							    images.add(getFinalImageURL(clinicImage));
						}
						labResponse.setImages(images);
						 if (document.getLogoUrl() != null)
							 labResponse.setLogoUrl(getFinalImageURL(document.getLogoUrl()));

						    if (latitude != null && longitude != null && document.getLatitude() != null && document.getLongitude() != null) {
							labResponse.setDistance(DPDoctorUtils.distance(Double.parseDouble(latitude), Double.parseDouble(longitude),
								document.getLatitude(), document.getLongitude(), "K"));
						    }
						if (response == null)response = new ArrayList<LabResponse>();
						response.add(labResponse);
					    }	
					}
//			}
//	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error While Getting Labs From ES : " + e.getMessage());
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
