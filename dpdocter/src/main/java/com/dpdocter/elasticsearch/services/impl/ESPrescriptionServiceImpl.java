package com.dpdocter.elasticsearch.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
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

import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.LabTest;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESLabTestDocument;
import com.dpdocter.elasticsearch.repository.ESDiagnosticTestRepository;
import com.dpdocter.elasticsearch.repository.ESDrugRepository;
import com.dpdocter.elasticsearch.repository.ESLabTestRepository;
import com.dpdocter.elasticsearch.services.ESPrescriptionService;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class ESPrescriptionServiceImpl implements ESPrescriptionService {

    private static Logger logger = Logger.getLogger(ESPrescriptionServiceImpl.class.getName());

    @Autowired
    private ESDrugRepository esDrugRepository;

    @Autowired
    private ESLabTestRepository esLabTestRepository;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Autowired
    private ESDiagnosticTestRepository esDiagnosticTestRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;
    
    @Override
    public boolean addDrug(ESDrugDocument request) {
	boolean response = false;
	try {
	    esDrugRepository.save(request);
	    response = true;
	    transnationalService.addResource(new ObjectId(request.getId()), Resource.DRUG, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Drug in ES");
	}
	return response;
    }
    
	@Override
	public Boolean editDrugTypeInDrugs(String drugTypeId) {
		Boolean response = false;
		try {
		    List<ESDrugDocument> esDrugDocuments = esDrugRepository.findBydrugTypeId(drugTypeId);
		    if(esDrugDocuments != null && !esDrugDocuments.isEmpty()){
		    	for(ESDrugDocument esDrugDocument : esDrugDocuments){
		    		esDrugRepository.save(esDrugDocument);
		    	}
		    }
		    response = true;
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e + " Error Occurred While Saving Drug in ES");
		}
		return response;
	}

    @Override
    public boolean addLabTest(ESLabTestDocument request) {
	boolean response = false;
	try {
	    esLabTestRepository.save(request);
	    response = true;
	    transnationalService.addResource(new ObjectId(request.getId()), Resource.LABTEST, true);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Lab Test in ES");
	}
	return response;
    }

    @Override
    public List<ESDrugDocument> searchDrug(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<ESDrugDocument> response = new ArrayList<ESDrugDocument>();
	if(!DPDoctorUtils.anyStringEmpty(searchTerm))searchTerm = searchTerm.toUpperCase();
	switch (Range.valueOf(range.toUpperCase())) {

	case GLOBAL:
	    response = getGlobalDrugs(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}
	return response;

    }

    private List<ESDrugDocument> getCustomGlobalDrugs(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded, String searchTerm) {
	List<ESDrugDocument> response = null;
	try {
	    SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DRUG, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, null, "drugName");
	    response = elasticsearchTemplate.queryForList(searchQuery, ESDrugDocument.class);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drugs");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
	}
	return response;
    }

    private List<ESDrugDocument> getGlobalDrugs(int page, int size, String updatedTime, boolean discarded, String searchTerm) {
	List<ESDrugDocument> response = null;
	try {
		SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.DRUG, page, size, updatedTime, discarded, null, searchTerm, null, "drugName");
	    response = elasticsearchTemplate.queryForList(searchQuery, ESDrugDocument.class);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drugs");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
	}
	return response;
    }

    private List<ESDrugDocument> getCustomDrugs(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded, String searchTerm) {
	List<ESDrugDocument> response = null;
	try {
	    if (doctorId == null)response = new ArrayList<ESDrugDocument>();
	    else {
	    	SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, "drugName");
		    response = elasticsearchTemplate.queryForList(searchQuery, ESDrugDocument.class);
			}
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Drugs");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
	}
	return response;
    }

    @Override
    public List<LabTest> searchLabTest(String range, int page, int size, String locationId, String hospitalId, String updatedTime, Boolean discarded,
	    String searchTerm) {
	List<LabTest> response = null;
	List<ESLabTestDocument> labTestDocuments = null;
	switch (Range.valueOf(range.toUpperCase())) {

	case GLOBAL:
	    labTestDocuments = getGlobalLabTests(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    labTestDocuments = getCustomLabTests(page, size, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    labTestDocuments = getCustomGlobalLabTests(page, size, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}
	if (labTestDocuments != null) {
	    response = new ArrayList<LabTest>();
	    for (ESLabTestDocument labTestDocument : labTestDocuments) {
		LabTest labTest = new LabTest();
		BeanUtil.map(labTestDocument, labTest);
		if (labTestDocument.getTestId() != null) {
		    ESDiagnosticTestDocument diagnosticTestDocument = esDiagnosticTestRepository.findOne(labTestDocument.getTestId());
		    if (diagnosticTestDocument != null) {
			DiagnosticTest test = new DiagnosticTest();
			BeanUtil.map(diagnosticTestDocument, test);
			labTest.setTest(test);
		    }
		}
		response.add(labTest);
	    }
	}
	return response;
    }

    @SuppressWarnings("unchecked")
	private List<ESLabTestDocument> getGlobalLabTests(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<ESLabTestDocument> response = null;
	try {
		Collection<String> testIds = null;
		if(!DPDoctorUtils.anyStringEmpty(searchTerm)){
			SearchQuery searchQueryForTest = createGlobalQueryWithoutDoctorId(0, 0, updatedTime, false, "testName", searchTerm, null, true, ESDiagnosticTestDocument.class, "testName");
			List<ESDiagnosticTestDocument> diagnosticTestCollections = elasticsearchTemplate.queryForList(searchQueryForTest, ESDiagnosticTestDocument.class);
		    testIds = CollectionUtils.collect(diagnosticTestCollections, new BeanToPropertyValueTransformer("id"));
		    if(testIds == null || testIds.isEmpty())return response;
		}
		SearchQuery searchQuery = createGlobalQueryWithoutDoctorId(page, size, updatedTime, discarded, null, null, testIds, false, null, null);
	    response = elasticsearchTemplate.queryForList(searchQuery, ESLabTestDocument.class);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting LabTests");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
	}
	return response;
    }

    @SuppressWarnings("unchecked")
	private List<ESLabTestDocument> getCustomLabTests(int page, int size, String locationId, String hospitalId, String updatedTime, boolean discarded,
	    String searchTerm) {
	List<ESLabTestDocument> response = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId));
	    else {
	    	Collection<String> testIds = null;
			if(!DPDoctorUtils.anyStringEmpty(searchTerm)){
		    	SearchQuery searchQueryForTest = createCustomGlobalQueryWithoutDoctorId(0, 0, locationId, hospitalId, updatedTime, false, "testName", searchTerm, null, true, ESDiagnosticTestDocument.class, "testName");
				List<ESDiagnosticTestDocument> diagnosticTestCollections = elasticsearchTemplate.queryForList(searchQueryForTest, ESDiagnosticTestDocument.class);
			    testIds = CollectionUtils.collect(diagnosticTestCollections, new BeanToPropertyValueTransformer("id"));
			    if(testIds == null || testIds.isEmpty())return response;
			}
			SearchQuery searchQuery = createCustomQueryWithoutDoctorId(page, size, locationId, hospitalId, updatedTime, discarded, null, null, testIds, false, null, null);
		    response = elasticsearchTemplate.queryForList(searchQuery, ESLabTestDocument.class);
	    }
	    } catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting LabTests");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
	}
	return response;
    }

    @SuppressWarnings("unchecked")
	private List<ESLabTestDocument> getCustomGlobalLabTests(int page, int size, String locationId, String hospitalId, String updatedTime, boolean discarded,
	    String searchTerm) {
	List<ESLabTestDocument> response = null;
	try {
		Collection<String> testIds = null;
		if(!DPDoctorUtils.anyStringEmpty(searchTerm)){
			SearchQuery searchQueryForTest = createCustomGlobalQueryWithoutDoctorId(0, 0, locationId, hospitalId, updatedTime, false, "testName", searchTerm, null, true, ESDiagnosticTestDocument.class, "testName");
			List<ESDiagnosticTestDocument> diagnosticTestCollections = elasticsearchTemplate.queryForList(searchQueryForTest, ESDiagnosticTestDocument.class);
		    testIds = CollectionUtils.collect(diagnosticTestCollections, new BeanToPropertyValueTransformer("id"));
		    if(testIds == null || testIds.isEmpty())return response;
		}
		SearchQuery searchQuery = createCustomGlobalQueryWithoutDoctorId(page, size, locationId, hospitalId, updatedTime, discarded, null, null, testIds, false, null, null);
	    response = elasticsearchTemplate.queryForList(searchQuery, ESLabTestDocument.class);

		} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting LabTests");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
	}
	return response;
    }

    @Override
    public Boolean addEditDiagnosticTest(ESDiagnosticTestDocument ESDiagnosticTestDocument) {
	boolean response = false;
	try {
	    esDiagnosticTestRepository.save(ESDiagnosticTestDocument);
	    response = true;
	    transnationalService.addResource(new ObjectId(ESDiagnosticTestDocument.getId()), Resource.DIAGNOSTICTEST, true);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Diagnostic Test in ES");
	}
	return response;
    }

    @Override
    public List<ESDiagnosticTestDocument> searchDiagnosticTest(String range, int page, int size, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm) {
	List<ESDiagnosticTestDocument> response = null;
	switch (Range.valueOf(range.toUpperCase())) {

	case GLOBAL:
	    response = getGlobalDiagnosticTests(page, size, updatedTime, discarded, searchTerm);
	    break;
	case CUSTOM:
	    response = getCustomDiagnosticTests(page, size, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	case BOTH:
	    response = getCustomGlobalDiagnosticTests(page, size, locationId, hospitalId, updatedTime, discarded, searchTerm);
	    break;
	}
	return response;
    }

    private List<ESDiagnosticTestDocument> getGlobalDiagnosticTests(int page, int size, String updatedTime, Boolean discarded, String searchTerm) {
	List<ESDiagnosticTestDocument> response = null;
	try {
		SearchQuery searchQuery = createGlobalQueryWithoutDoctorId(page, size, updatedTime, discarded, "testName", searchTerm, null, false, null, null);
	    response = elasticsearchTemplate.queryForList(searchQuery, ESDiagnosticTestDocument.class);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting DiagnosticTests");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting DiagnosticTests");
	}
	return response;
    }

    private List<ESDiagnosticTestDocument> getCustomDiagnosticTests(int page, int size, String locationId, String hospitalId, String updatedTime,
	    boolean discarded, String searchTerm) {
	List<ESDiagnosticTestDocument> response = null;
	try {
	    if (locationId == null && hospitalId == null);
	    else {
	    	SearchQuery searchQuery = createCustomQueryWithoutDoctorId(page, size, locationId, hospitalId, updatedTime, discarded, "testName", searchTerm, null, false, null, null);
		    response = elasticsearchTemplate.queryForList(searchQuery, ESDiagnosticTestDocument.class);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting LabTests");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
	}
	return response;
    }

    private List<ESDiagnosticTestDocument> getCustomGlobalDiagnosticTests(int page, int size, String locationId, String hospitalId, String updatedTime,
	    boolean discarded, String searchTerm) {
	List<ESDiagnosticTestDocument> response = null;
	try {
		SearchQuery searchQuery = createCustomGlobalQueryWithoutDoctorId(page, size, locationId, hospitalId, updatedTime, discarded, "testName", searchTerm, null, false, null, null);
	    response = elasticsearchTemplate.queryForList(searchQuery, ESDiagnosticTestDocument.class);
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting LabTests");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
	}
	return response;
    }

	@SuppressWarnings("unchecked")
	public SearchQuery createGlobalQueryWithoutDoctorId(int page, int size, String updatedTime, Boolean discarded, String searchTermFieldName, String searchTerm, Collection<String> testIds,  Boolean calculateCount, Class classForCount, String sortBy){
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)))
				.mustNot(QueryBuilders.existsQuery("locationId"))
    			.mustNot(QueryBuilders.existsQuery("hospitalId"));
 	    
	   if(!DPDoctorUtils.anyStringEmpty(searchTerm))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(searchTermFieldName, searchTerm));
 	   if(!discarded)boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));
 	   if(testIds != null && !testIds.isEmpty())boolQueryBuilder.must(QueryBuilders.termsQuery("testId", testIds));
 	   if(calculateCount)size = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), classForCount);
        SearchQuery searchQuery = null;
        if(!DPDoctorUtils.anyStringEmpty(sortBy)){
        	if(size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size, Direction.ASC, sortBy)).build();
            else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.ASC)).build();
        }
        else{
        	if(size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size)).build();
            else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        }
        
        return searchQuery;
	}
	
	@SuppressWarnings("unchecked")
	public SearchQuery createCustomQueryWithoutDoctorId(int page, int size, String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTermFieldName, String searchTerm, Collection<String> testIds,  Boolean calculateCount, Class classForCount, String sortBy){
		
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)))
    	.must(QueryBuilders.termQuery("locationId", locationId)).must(QueryBuilders.termQuery("hospitalId", hospitalId));
		
 	    if (!DPDoctorUtils.anyStringEmpty(searchTerm))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(searchTermFieldName, searchTerm));
 	    if(!discarded)boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));
 	    if(testIds != null && !testIds.isEmpty())boolQueryBuilder.must(QueryBuilders.termsQuery("testId", testIds));
 	    if(calculateCount)size = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), classForCount);
        SearchQuery searchQuery = null;
        if(!DPDoctorUtils.anyStringEmpty(sortBy)){
        	if(size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size, Direction.ASC, sortBy)).build();
            else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.ASC)).build();
        }
        else{
        	if(size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size)).build();
            else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        }
          
        return searchQuery;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public SearchQuery createCustomGlobalQueryWithoutDoctorId(int page, int size, String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTermFieldName, String searchTerm, Collection<String> testIds, Boolean calculateCount, Class classForCount, String sortBy){

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)));
    	
		if(!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)){
    		boolQueryBuilder.must(QueryBuilders.orQuery(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("locationId")) , QueryBuilders.termQuery("locationId", locationId)))
    		.must(QueryBuilders.orQuery(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("hospitalId")) , QueryBuilders.termQuery("hospitalId", hospitalId)));
    	}
    	
	    if(!DPDoctorUtils.anyStringEmpty(searchTerm))boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(searchTermFieldName, searchTerm));
	    if(!discarded)boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

	    if(testIds != null && !testIds.isEmpty())boolQueryBuilder.must(QueryBuilders.termsQuery("testId", testIds));
	    
	    if(calculateCount)size = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), classForCount);
        SearchQuery searchQuery = null;
        if(!DPDoctorUtils.anyStringEmpty(sortBy)){
        	if(size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size, Direction.ASC, sortBy)).build();
            else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.ASC)).build();
        }
        else{
        	if(size > 0)searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(new PageRequest(page, size)).build();
            else searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        }
        

        return searchQuery;
	}
}
