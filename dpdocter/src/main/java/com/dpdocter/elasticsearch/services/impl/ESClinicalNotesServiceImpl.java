package com.dpdocter.elasticsearch.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.repository.ESComplaintsRepository;
import com.dpdocter.elasticsearch.repository.ESDiagnosesRepository;
import com.dpdocter.elasticsearch.repository.ESDiagramsRepository;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESInvestigationsRepository;
import com.dpdocter.elasticsearch.repository.ESNotesRepository;
import com.dpdocter.elasticsearch.repository.ESObservationsRepository;
import com.dpdocter.elasticsearch.repository.ESSpecialityRepository;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class ESClinicalNotesServiceImpl implements ESClinicalNotesService {

    private static Logger logger = Logger.getLogger(ESClinicalNotesServiceImpl.class.getName());

    @Autowired
    private ESComplaintsRepository esComplaintsRepository;

    @Autowired
    private ESDiagnosesRepository esDiagnosesRepository;

    @Autowired
    private ESNotesRepository esNotesRepository;

    @Autowired
    private ESDiagramsRepository esDiagramsRepository;

    @Autowired
    private ESInvestigationsRepository esInvestigationsRepository;

    @Autowired
    private ESObservationsRepository esObservationsRepository;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Autowired
    private ESDoctorRepository esDoctorRepository;

    @Autowired
    private ESSpecialityRepository esSpecialityRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public boolean addComplaints(ESComplaintsDocument request) {
	boolean response = false;
	try {
	    esComplaintsRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.COMPLAINT, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Complaints");
	}
	return response;
    }

    @Override
    public boolean addDiagnoses(ESDiagnosesDocument request) {
	boolean response = false;
	try {
	    esDiagnosesRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.DIAGNOSIS, true);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Diagnosis");
	}
	return response;
    }

    @Override
    public boolean addNotes(ESNotesDocument request) {
	boolean response = false;
	try {
	    esNotesRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.NOTES, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Notes");
	}
	return response;
    }


    @Override
    public boolean addDiagrams(ESDiagramsDocument request) {
	boolean response = false;
	try {
	    esDiagramsRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.DIAGRAM, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Diagrams");
	}
	return response;
    }

    @Override
    public boolean addInvestigations(ESInvestigationsDocument request) {
	boolean response = false;
	try {
	    esInvestigationsRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.INVESTIGATION, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Investigations");
	}
	return response;
    }


    @Override
    public boolean addObservations(ESObservationsDocument request) {
	boolean response = false;
	try {
	    esObservationsRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getId(), Resource.OBSERVATION, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Observations");
	}
	return response;
    }

    @Override
    public List<ESObservationsDocument> searchObservations(String range, int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<ESObservationsDocument> response = null;
	switch (Range.valueOf(range.toUpperCase())) {

	case GLOBAL:
	    response = getGlobalObservations(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomObservations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalObservations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}

	return response;
    }

    @Override
    public List<ESInvestigationsDocument> searchInvestigations(String range, int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<ESInvestigationsDocument> response = null;
	switch (Range.valueOf(range.toUpperCase())) {

	case GLOBAL:
	    response = getGlobalInvestigations(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}

	return response;
    }

    @Override
    public List<ESDiagramsDocument> searchDiagrams(String range, int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<ESDiagramsDocument> response = null;
	switch (Range.valueOf(range.toUpperCase())) {
	case GLOBAL:
	    response = getGlobalDiagrams(page, size, doctorId, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}
	return response;
    }

    @Override
    public List<ESNotesDocument> searchNotes(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<ESNotesDocument> response = null;
	switch (Range.valueOf(range.toUpperCase())) {
	case GLOBAL:
	    response = getGlobalNotes(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}
	return response;
    }

    @Override
    public List<ESDiagnosesDocument> searchDiagnoses(String range, int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<ESDiagnosesDocument> response = null;
	switch (Range.valueOf(range.toUpperCase())) {
	case GLOBAL:
	    response = getGlobalDiagnosis(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}
	return response;
    }

    @Override
    public List<ESComplaintsDocument> searchComplaints(String range, int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<ESComplaintsDocument> response = null;
	switch (Range.valueOf(range.toUpperCase())) {

	case GLOBAL:
	    response = getGlobalComplaints(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomComplaints(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalComplaints(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}
	return response;
    }

    private List<ESComplaintsDocument> getCustomGlobalComplaints(int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<ESComplaintsDocument> response = null;
	try {
		SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "complaint", searchTerm, "complaint");
		response = elasticsearchTemplate.queryForList(searchQuery, ESComplaintsDocument.class);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return response;

    }

    private List<ESComplaintsDocument> getGlobalComplaints(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<ESComplaintsDocument> response = null;
	try {
		SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(page, size, updatedTime, discarded, "complaint", searchTerm, "complaint");
		response = elasticsearchTemplate.queryForList(searchQuery, ESComplaintsDocument.class);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return response;
    }

    private List<ESComplaintsDocument> getCustomComplaints(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<ESComplaintsDocument> response = null;
	try {

	    if (doctorId == null)response = new ArrayList<ESComplaintsDocument>();
	    else {
	    	SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "complaint", searchTerm, "complaint");
			response = elasticsearchTemplate.queryForList(searchQuery, ESComplaintsDocument.class);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
	}
	return response;
    }

    @SuppressWarnings({ "unchecked", "unused", "deprecation" })
	private List<ESDiagramsDocument> getCustomGlobalDiagrams(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<ESDiagramsDocument> response = null;

	try {
	    List<ESDoctorDocument> doctorCollections = null;
	    if(!DPDoctorUtils.anyStringEmpty(doctorId))esDoctorRepository.findByUserId(doctorId);
	    Collection<String> specialities = null;
	    if(doctorCollections != null && !doctorCollections.isEmpty()){
	 		@SuppressWarnings("unchecked")
		    Collection<String> specialitiesId = CollectionUtils.collect(doctorCollections, new BeanToPropertyValueTransformer("specialities"));
	 		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("id", specialitiesId));
	 		if(!DPDoctorUtils.anyStringEmpty(searchTerm))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));
	 		
	 		int count = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), ESSpecialityDocument.class);
	 		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(0, count)).build();
	 		List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate.queryForList(searchQuery, ESSpecialityDocument.class);
	 		if(resultsSpeciality != null && !resultsSpeciality.isEmpty()){
	 			specialities = CollectionUtils.collect(resultsSpeciality, new BeanToPropertyValueTransformer("speciality"));
	 		}
	 	}
	 	
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)));
    	
		if(!DPDoctorUtils.anyStringEmpty(doctorId))
			boolQueryBuilder.must(QueryBuilders.orQuery(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("doctorId")) , QueryBuilders.termQuery("doctorId", doctorId)));
		else
			boolQueryBuilder.mustNot(QueryBuilders.existsQuery("doctorId"));
    	if(!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)){
    		boolQueryBuilder.must(QueryBuilders.orQuery(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("locationId")) , QueryBuilders.termQuery("locationId", locationId)))
    		.must(QueryBuilders.orQuery(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("hospitalId")) , QueryBuilders.termQuery("hospitalId", hospitalId)));
    	}
//    	else{
//    		boolQueryBuilder.mustNot(QueryBuilders.existsQuery("locationId")).mustNot(QueryBuilders.existsQuery("hospitalId"));
//    	}
    	if(specialities != null)boolQueryBuilder.must(QueryBuilders.termsQuery("speciality", specialities));
//	    if(!DPDoctorUtils.anyStringEmpty(searchTerm))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));
	    if(!discarded)boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

        SearchQuery searchQuery = null;
        if(size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size, Direction.DESC, "updatedTime")).build();
        else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();

        response = elasticsearchTemplate.queryForList(searchQuery, ESDiagramsDocument.class);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
	}
	return response;
    }

    @SuppressWarnings({ "unchecked", "unused" })
	private List<ESDiagramsDocument> getGlobalDiagrams(int page, int size, String doctorId, String updatedTime, Boolean discarded, String searchTerm) {
	List<ESDiagramsDocument> response = null;
	try {
		List<ESDoctorDocument> doctorCollections = null;
	    if(!DPDoctorUtils.anyStringEmpty(doctorId))esDoctorRepository.findByUserId(doctorId);
	    Collection<String> specialities = null;
	    if(doctorCollections != null && !doctorCollections.isEmpty()){
	 		Collection<String> specialitiesId = CollectionUtils.collect(doctorCollections, new BeanToPropertyValueTransformer("specialities"));
	 		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("id", specialitiesId));
	 		if(!DPDoctorUtils.anyStringEmpty(searchTerm))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));
	 		
	 		int count = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), ESSpecialityDocument.class);
	 		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(0, count)).build();
	 		
	 		List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate.queryForList(searchQuery, ESSpecialityDocument.class);
	 		if(resultsSpeciality != null && !resultsSpeciality.isEmpty()){
	 			specialities = CollectionUtils.collect(resultsSpeciality, new BeanToPropertyValueTransformer("speciality"));
	 		}
	 	}
	 	
	    BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)))
				.mustNot(QueryBuilders.existsQuery("doctorId"))
    			.mustNot(QueryBuilders.existsQuery("locationId"))
    			.mustNot(QueryBuilders.existsQuery("hospitalId"));
 	    
		if(specialities != null)boolQueryBuilder.must(QueryBuilders.termsQuery("speciality", specialities));
	    if(!DPDoctorUtils.anyStringEmpty(searchTerm))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));
	    if(!discarded)boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

        SearchQuery searchQuery = null;
        if(size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size, Direction.DESC, "updatedTime")).build();
        else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();

        response = elasticsearchTemplate.queryForList(searchQuery, ESDiagramsDocument.class);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
	}
	return response;
    }

    @SuppressWarnings({ "unused", "unchecked" })
	private List<ESDiagramsDocument> getCustomDiagrams(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<ESDiagramsDocument> response = null;
	try {
	    if (doctorId == null)
	    	response = new ArrayList<ESDiagramsDocument>();
	    else {
	    	List<ESDoctorDocument> doctorCollections = null;
		    if(!DPDoctorUtils.anyStringEmpty(doctorId))esDoctorRepository.findByUserId(doctorId);
		    Collection<String> specialities = null;
		    if(doctorCollections != null && !doctorCollections.isEmpty()){
		 		Collection<String> specialitiesId = CollectionUtils.collect(doctorCollections, new BeanToPropertyValueTransformer("specialities"));
		 		
		 		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("id", specialitiesId));
		 		if(!DPDoctorUtils.anyStringEmpty(searchTerm))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));
		 		
		 		int count = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), ESSpecialityDocument.class);
		 		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(0, count)).build();
		 		
		 		List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate.queryForList(searchQuery, ESSpecialityDocument.class);
		 		if(resultsSpeciality != null && !resultsSpeciality.isEmpty()){
		 			specialities = CollectionUtils.collect(resultsSpeciality, new BeanToPropertyValueTransformer("speciality"));
		 		}
		 	}
		 	
		    BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)))
	    			.must(QueryBuilders.termQuery("doctorId", doctorId));
	    	
			if(!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))boolQueryBuilder.must(QueryBuilders.termQuery("locationId", locationId)).must(QueryBuilders.termQuery("hospitalId", hospitalId));
		    if(specialities != null)boolQueryBuilder.must(QueryBuilders.termsQuery("speciality", specialities));
		    if(!DPDoctorUtils.anyStringEmpty(searchTerm))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));
		    if(!discarded)boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

	        SearchQuery searchQuery = null;
	        if(size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size, Direction.DESC, "updatedTime")).build();
	        else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();

	        response = elasticsearchTemplate.queryForList(searchQuery, ESDiagramsDocument.class);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
	}
	return response;
    }

    private List<ESInvestigationsDocument> getCustomGlobalInvestigations(int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<ESInvestigationsDocument> response = null;
	try {
		SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "investigation", searchTerm, "investigation");
		response = elasticsearchTemplate.queryForList(searchQuery, ESInvestigationsDocument.class);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return response;
    }

    private List<ESInvestigationsDocument> getGlobalInvestigations(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<ESInvestigationsDocument> response = null;
	try {
		SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(page, size, updatedTime, discarded, "investigation", searchTerm, "investigation");
		response = elasticsearchTemplate.queryForList(searchQuery, ESInvestigationsDocument.class);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return response;
    }

    private List<ESInvestigationsDocument> getCustomInvestigations(int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<ESInvestigationsDocument> response = null;
	try {
	    if (doctorId == null)
	    	response = new ArrayList<ESInvestigationsDocument>();
	    else {
	    	SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "investigation", searchTerm, "investigation");
			response = elasticsearchTemplate.queryForList(searchQuery, ESInvestigationsDocument.class);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
	}
	return response;
    }

    private List<ESObservationsDocument> getCustomGlobalObservations(int page, int size, String doctorId, String locationId, String hospitalId,
	    String updatedTime, Boolean discarded, String searchTerm) {
	List<ESObservationsDocument> response = null;
	try {
		SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "observation", searchTerm, "observation");
		response = elasticsearchTemplate.queryForList(searchQuery, ESObservationsDocument.class);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return response;

    }

    private List<ESObservationsDocument> getGlobalObservations(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<ESObservationsDocument> response = null;
	try {
		SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(page, size, updatedTime, discarded, "observation", searchTerm, "observation");
		response = elasticsearchTemplate.queryForList(searchQuery, ESObservationsDocument.class);
		} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return response;
    }

    private List<ESObservationsDocument> getCustomObservations(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<ESObservationsDocument> response = null;
	try {
	    if (doctorId == null)response = new ArrayList<ESObservationsDocument>();
	    else {
	    	SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "observation", searchTerm, "observation");
			response = elasticsearchTemplate.queryForList(searchQuery, ESObservationsDocument.class);
		}
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
	}
	return response;
    }

    private List<ESDiagnosesDocument> getCustomGlobalDiagnosis(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<ESDiagnosesDocument> response = null;
	try {
	      SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "diagnosis", searchTerm, "diagnosis");
	      response = elasticsearchTemplate.queryForList(searchQuery, ESDiagnosesDocument.class);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return response;
    }

    private List<ESDiagnosesDocument> getGlobalDiagnosis(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<ESDiagnosesDocument> response = null;
	try {
		SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(page, size, updatedTime, discarded, "diagnosis", searchTerm, "diagnosis");
		response = elasticsearchTemplate.queryForList(searchQuery, ESDiagnosesDocument.class);
		
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return response;
    }

    private List<ESDiagnosesDocument> getCustomDiagnosis(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<ESDiagnosesDocument> response = null;
	try {
	    if(doctorId == null)response = new ArrayList<ESDiagnosesDocument>();
	    else {
	    	SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "diagnosis", searchTerm, "diagnosis");
			response = elasticsearchTemplate.queryForList(searchQuery, ESDiagnosesDocument.class);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
	}
	return response;
    }

    private List<ESNotesDocument> getCustomGlobalNotes(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {

	List<ESNotesDocument> response = null;
	try {
		SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "note", searchTerm, "note");
		response = elasticsearchTemplate.queryForList(searchQuery, ESNotesDocument.class);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
	}
	return response;

    }

    private List<ESNotesDocument> getGlobalNotes(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<ESNotesDocument> response = null;
	try {
		SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(page, size, updatedTime, discarded, "note", searchTerm, "note");
		response = elasticsearchTemplate.queryForList(searchQuery, ESNotesDocument.class);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
	}
	return response;
    }

    private List<ESNotesDocument> getCustomNotes(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<ESNotesDocument> response = null;
	try {
	    if (doctorId == null)response = new ArrayList<ESNotesDocument>();
	    else {
	    	SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "note", searchTerm, "note");
			response = elasticsearchTemplate.queryForList(searchQuery, ESNotesDocument.class);	
	    }	
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
	}
	return response;
    }

//    @Override
//    public List<ESDiagramsDocument> searchDiagramsBySpeciality(String searchTerm) {
//	List<ESDiagramsDocument> response = null;
//	try {
//	    response = esDiagramsRepository.findBySpeciality(searchTerm);
//	} catch (Exception e) {
//	    e.printStackTrace();
//	    logger.error(e + " Error Occurred While Searching Diagrams");
//	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Diagrams");
//	}
//	return response;
//    }
}
