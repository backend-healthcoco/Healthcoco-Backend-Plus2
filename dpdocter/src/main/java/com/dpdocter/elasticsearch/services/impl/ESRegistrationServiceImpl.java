package com.dpdocter.elasticsearch.services.impl;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.beans.AdvancedSearch;
import com.dpdocter.elasticsearch.beans.AdvancedSearchParameter;
import com.dpdocter.elasticsearch.beans.DoctorLocation;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESPatientRepository;
import com.dpdocter.elasticsearch.repository.ESReferenceRepository;
import com.dpdocter.elasticsearch.response.ESPatientResponse;
import com.dpdocter.elasticsearch.response.ESPatientResponseDetails;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.AdvancedSearchType;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class ESRegistrationServiceImpl implements ESRegistrationService {

    private static Logger logger = Logger.getLogger(ESRegistrationServiceImpl.class.getName());

    @Autowired
    private ESDoctorRepository esDoctorRepository;

    @Autowired
    private ESPatientRepository esPatientRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Autowired
    private UserRepository userRepository;

    @Value(value = "${image.path}")
    private String imagePath;

    @Autowired
    private ReferenceRepository referrenceRepository;

    @Autowired
    private ESReferenceRepository esReferenceRepository;

    @Override
    public boolean addPatient(ESPatientDocument request) {
	boolean response = false;
	try {
	    esPatientRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getUserId(), Resource.PATIENT, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Patient");
	}
	return response;
    }

    @Override
    public ESPatientResponseDetails searchPatient(String doctorId, String locationId, String hospitalId, String searchTerm, int page, int size) {

	List<ESPatientDocument> patients = new ArrayList<ESPatientDocument>();
	List<ESPatientResponse> patientsResponse = null;
	ESPatientResponseDetails patientResponseDetails = null;
	try {

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.termQuery("doctorId", doctorId))
				.must(QueryBuilders.termQuery("locationId", locationId))
				.must(QueryBuilders.termQuery("hospitalId", hospitalId))
				.must(QueryBuilders.multiMatchQuery(searchTerm, AdvancedSearchType.FIRST_NAME.getSearchType(),AdvancedSearchType.EMAIL_ADDRESS.getSearchType(),AdvancedSearchType.MOBILE_NUMBER.getSearchType(),AdvancedSearchType.PID.getSearchType()));

		SearchQuery searchQuery = null;
		if (size > 0) searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size)).build();
	    else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();

		patients = elasticsearchTemplate.queryForList(searchQuery, ESPatientDocument.class);
	    if (patients != null && !patients.isEmpty()) {
		patientsResponse = new ArrayList<ESPatientResponse>();
		for (ESPatientDocument patient : patients) {
		    ESPatientResponse patientResponse = new ESPatientResponse();

		    patient.setImageUrl(getFinalImageURL(patient.getImageUrl()));
		    patient.setThumbnailUrl(getFinalImageURL(patient.getThumbnailUrl()));

		    BeanUtil.map(patient, patientResponse);
		    ESReferenceDocument esReferenceDocument = esReferenceRepository.findOne(patient.getId());
		    if (esReferenceDocument != null)
			patientResponse.setReferredBy(esReferenceDocument.getReference());
		    patientsResponse.add(patientResponse);
		}
		patientResponseDetails = new ESPatientResponseDetails();
		patientResponseDetails.setPatients(patientsResponse);
		patientResponseDetails.setTotalSize(elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), ESPatientDocument.class));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Searching Patient");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Patients");
	}
	return patientResponseDetails;
    }

    @Override
    public ESPatientResponseDetails searchPatient(AdvancedSearch request) {
	List<ESPatientDocument> patients = null;
	List<ESPatientResponse> response = new ArrayList<ESPatientResponse>();
	ESPatientResponseDetails responseDetails = null;
	try {
		BoolQueryBuilder boolQueryBuilder = createAdvancedSearchCriteria(request);
		SearchQuery searchQuery = null;
	    if (request.getSize() > 0)
	    	searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(request.getPage(), request.getSize(), Direction.DESC, "createdTime")).build();
	    else
	    	searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort("createdTime").order(SortOrder.DESC)).build();

	    patients = elasticsearchTemplate.queryForList(searchQuery, ESPatientDocument.class);
	    
	    if (patients != null && !patients.isEmpty()) {
		response = new ArrayList<ESPatientResponse>();
		for (ESPatientDocument patient : patients) {
		    ESPatientResponse patientResponse = new ESPatientResponse();

		    patient.setImageUrl(getFinalImageURL(patient.getImageUrl()));
		    patient.setThumbnailUrl(getFinalImageURL(patient.getThumbnailUrl()));

		    BeanUtil.map(patient, patientResponse);
		    ESReferenceDocument esReferenceDocument = esReferenceRepository.findOne(patient.getId());
		    if (esReferenceDocument != null)
			patientResponse.setReferredBy(esReferenceDocument.getReference());
		    response.add(patientResponse);
		}
		responseDetails = new ESPatientResponseDetails();
		responseDetails.setPatients(response);
		responseDetails.setTotalSize(elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), ESPatientDocument.class));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Searching Patients");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Patients");
	}
	return responseDetails;
    }

    private BoolQueryBuilder createAdvancedSearchCriteria(AdvancedSearch request) throws ParseException {
	String doctorId = request.getDoctorId();
	String locationId = request.getLocationId();
	String hospitalId = request.getHospitalId();

	BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
			.must(QueryBuilders.termQuery("doctorId", doctorId))
			.must(QueryBuilders.termQuery("locationId", locationId))
			.must(QueryBuilders.termQuery("hospitalId", hospitalId));

	if (request.getSearchParameters() != null && !request.getSearchParameters().isEmpty()) {
	    for (AdvancedSearchParameter searchParameter : request.getSearchParameters()) {
		String searchValue = searchParameter.getSearchValue();
		String searchType = searchParameter.getSearchType().getSearchType();
		if (!DPDoctorUtils.anyStringEmpty(searchValue, searchType)) {
			QueryBuilder builder = null;
		    if (searchType.equalsIgnoreCase(AdvancedSearchType.DOB.getSearchType())) {
		    	
		    	String[] dob = searchValue.split("/");
		    	builder = nestedQuery(AdvancedSearchType.DOB.getSearchType(), boolQuery().must(termQuery("dob.years",dob[2])).must(termQuery("dob.months",dob[0])).must(termQuery("dob.days",dob[1])));
	    			
		    } else if (searchType.equalsIgnoreCase(AdvancedSearchType.REGISTRATION_DATE.getSearchType())) {
		    	
		    	String[] dob = searchValue.split("/");
		    	DateTime start = new DateTime(Integer.parseInt(dob[2]), Integer.parseInt(dob[0]), Integer.parseInt(dob[1]), 0, 0, 0);
		    	DateTime end = new DateTime(Integer.parseInt(dob[2]), Integer.parseInt(dob[0]), Integer.parseInt(dob[1]), 23, 59, 59);
		    	builder = QueryBuilders.rangeQuery(AdvancedSearchType.REGISTRATION_DATE.getSearchType()).from(start).to(end);
		    	
		    } else if (searchType.equalsIgnoreCase(AdvancedSearchType.REFERRED_BY.getSearchType())){
		    	
		    	BoolQueryBuilder boolQueryBuilderForRefr = new BoolQueryBuilder();
		    	
				if(!DPDoctorUtils.anyStringEmpty(doctorId))boolQueryBuilder.must(QueryBuilders.orQuery(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("doctorId")) , QueryBuilders.termQuery("doctorId", doctorId)));
				else
					boolQueryBuilder.mustNot(QueryBuilders.existsQuery("doctorId"));
		    	if(!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)){
		    		boolQueryBuilder.must(QueryBuilders.orQuery(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("locationId")) , QueryBuilders.termQuery("locationId", locationId)))
		    		.must(QueryBuilders.orQuery(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("hospitalId")) , QueryBuilders.termQuery("hospitalId", hospitalId)));
		    	}
			    if(!DPDoctorUtils.anyStringEmpty(searchValue))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("reference", searchValue));
			    int size = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), ESReferenceDocument.class);
		        SearchQuery query = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(0, size)).build();
		        List<ESReferenceDocument> referenceDocuments = elasticsearchTemplate.queryForList(query, ESReferenceDocument.class);
		    	@SuppressWarnings("unchecked")
		    	Collection<String> referenceIds = CollectionUtils.collect(referenceDocuments, new BeanToPropertyValueTransformer("id"));
		    	builder = QueryBuilders.termsQuery(searchType, referenceIds);		    	
		    } else {
		    	builder = QueryBuilders.termQuery(searchType, searchValue);
		    }
		    boolQueryBuilder.filter(builder);
		}
	    }
	}
	return boolQueryBuilder;
    }

    @Override
    public boolean addDoctor(ESDoctorDocument request) {
	boolean response = false;
	try {
	    ESDoctorDocument doctorDocument = esDoctorRepository.findByUserIdAndLocationId(request.getUserId(), request.getLocationId());
	    if (doctorDocument != null)
		request.setId(doctorDocument.getId());
	    else
		request.setId(request.getUserId() + request.getLocationId());

	    if(request.getLatitude()!= null && request.getLongitude() != null)request.setGeoPoint(new GeoPoint(request.getLatitude(), request.getLongitude()));
	    esDoctorRepository.save(request);
	    transnationalService.addResource(request.getUserId(), Resource.DOCTOR, true);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error("Error While Saving Doctor Details to Solr : " + e.getMessage());
	}
	return response;
    }

 
    private String getFinalImageURL(String imageURL) {
	if (imageURL != null) {
	    return imagePath + imageURL;
	} else
	    return null;
    }
 
    @Override
    public void editLocation(DoctorLocation doctorLocation) {
	try {
	    List<ESDoctorDocument> doctorDocuments = esDoctorRepository.findByLocationId(doctorLocation.getLocationId());
	    for (ESDoctorDocument doctorDocument : doctorDocuments) {
		String id = doctorDocument.getId();
		BeanUtil.map(doctorLocation, doctorDocument);
		doctorDocument.setId(id);
		if(doctorDocument.getLatitude()!= null && doctorDocument.getLongitude() != null)doctorDocument.setGeoPoint(new GeoPoint(doctorDocument.getLatitude(), doctorDocument.getLongitude()));
		esDoctorRepository.save(doctorDocument);
		transnationalService.addResource(doctorLocation.getLocationId(), Resource.LOCATION, true);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error("Error while editing location " + e.getMessage());
	}
    }

    @Override
    public void addEditReference(ESReferenceDocument esReferenceDocument) {
	try {
	    esReferenceRepository.save(esReferenceDocument);
		transnationalService.addResource(esReferenceDocument.getId(), Resource.REFERENCE, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error("Error while editing reference " + e.getMessage());
	}
    }

	@Override
	public void activateUser(String userId) {
		try {
		    List<ESDoctorDocument> doctorDocument = esDoctorRepository.findByUserId(userId);
		    if (doctorDocument != null){
		    	UserCollection userCollection = userRepository.findOne(userId);
		    	for(ESDoctorDocument esDoctorDocument :  doctorDocument){
		    		esDoctorDocument.setIsActive(userCollection.getIsActive());
		    		esDoctorDocument.setIsVerified(userCollection.getIsVerified());
		    		esDoctorDocument.setUserState(userCollection.getUserState().getState());
		    		esDoctorRepository.save(esDoctorDocument);
		    	}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error("Error While Saving Doctor Details to Solr : " + e.getMessage());
		}
	}
}
