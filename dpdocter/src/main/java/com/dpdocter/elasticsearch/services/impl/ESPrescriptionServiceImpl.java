package com.dpdocter.elasticsearch.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.elasticsearch.client.transport.TransportClient;
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
import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.InventoryItem;
import com.dpdocter.beans.LabTest;
import com.dpdocter.elasticsearch.beans.DrugDocument;
import com.dpdocter.elasticsearch.document.ESAdvicesDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDrugDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESLabTestDocument;
import com.dpdocter.elasticsearch.repository.ESAdvicesRepository;
import com.dpdocter.elasticsearch.repository.ESDiagnosticTestRepository;
import com.dpdocter.elasticsearch.repository.ESDoctorDrugRepository;
import com.dpdocter.elasticsearch.repository.ESDrugRepository;
import com.dpdocter.elasticsearch.repository.ESLabTestRepository;
import com.dpdocter.elasticsearch.services.ESPrescriptionService;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.response.InventoryItemLookupResposne;
import com.dpdocter.services.InventoryService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class ESPrescriptionServiceImpl implements ESPrescriptionService {

	private static Logger logger = Logger.getLogger(ESPrescriptionServiceImpl.class.getName());
	@Autowired
	private ESAdvicesRepository esAdvicesRepository;

	@Autowired
	private ESDrugRepository esDrugRepository;

	@Autowired
	private ESDoctorDrugRepository esDoctorDrugRepository;

	@Autowired
	private ESLabTestRepository esLabTestRepository;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private ESDiagnosticTestRepository esDiagnosticTestRepository;

	@Autowired
	ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	TransportClient transportClient;

	@Autowired
	InventoryService inventoryService;

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
			if (esDrugDocuments != null && !esDrugDocuments.isEmpty()) {
				for (ESDrugDocument esDrugDocument : esDrugDocuments) {
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
	public List<?> searchDrug(String range, int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm, String category, Boolean searchByGenericName) {
		List<?> response = null;
		List<ESDrugDocument> esDrugDocuments = null;
		List<DrugDocument> drugDocuments = null;
		//Please remove this in next release. Its an hack for IOS 
			searchByGenericName = false;
		//
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			searchTerm = searchTerm.toUpperCase();
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			esDrugDocuments = getGlobalDrugs(page, size, updatedTime, discarded, searchTerm, category,
					searchByGenericName);
			response = addStockToDrug(esDrugDocuments);
			break;
		case CUSTOM:
			esDrugDocuments = getCustomDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm, category, searchByGenericName);
			response = addStockToDrug(esDrugDocuments);
			break;
		case BOTH:
			esDrugDocuments = getCustomGlobalDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm, category, searchByGenericName);
			response = addStockToDrug(esDrugDocuments);
			break;
		case FAVOURITES:
			drugDocuments = getFavouritesDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm, category, searchByGenericName);
			response = addStockToDrugWeb(drugDocuments);
			break;
		case WEBBOTH:
			drugDocuments = getCustomGlobalDrugsForWeb(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm, category, searchByGenericName);
			response = addStockToDrugWeb(drugDocuments);
			break;
		default:
			break;
		}
		return response;

	}

	private List<DrugDocument> getCustomGlobalDrugsForWeb(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm, String category,
			Boolean searchByGenericName) {
		List<DrugDocument> response = null;
		try {
			if (page > 0)
				return new ArrayList<DrugDocument>();
			if (doctorId == null)
				response = new ArrayList<DrugDocument>();
			else {
				SearchQuery searchQuery = null;

				if (searchByGenericName) {
					searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DRUG, page, 0, doctorId, locationId,
							hospitalId, updatedTime, discarded, null, searchTerm, null, category, null,
							"genericNames.name");
				} else {
					searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DRUG, page, 0, doctorId, locationId,
							hospitalId, updatedTime, discarded, null, searchTerm, null, category, null, "drugName");
				}

				List<ESDrugDocument> esDrugDocuments = elasticsearchTemplate.queryForList(searchQuery,
						ESDrugDocument.class);

				if (esDrugDocuments != null) {
					esDrugDocuments = new ArrayList<ESDrugDocument>(new LinkedHashSet<ESDrugDocument>(esDrugDocuments));
				}
				response = new ArrayList<DrugDocument>();
				for (ESDrugDocument esDrugDocument : esDrugDocuments) {
					String drugTypeStr = esDrugDocument.getDrugType();
					esDrugDocument.setDrugType(null);
					DrugDocument drugDocument = new DrugDocument();
					BeanUtil.map(esDrugDocument, drugDocument);
					DrugType drugType = new DrugType();
					drugType.setId(esDrugDocument.getDrugTypeId());
					drugType.setType(drugTypeStr);
					drugDocument.setDrugType(drugType);
					response.add(drugDocument);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<DrugDocument> getFavouritesDrugs(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm, String category,
			Boolean searchByGenericName) {
		List<DrugDocument> response = null;
		try {
			if (page > 0)
				return new ArrayList<DrugDocument>();
			if (doctorId == null)
				response = new ArrayList<DrugDocument>();
			else {
				SearchQuery searchQuery = null;

				if (searchByGenericName) {
					searchQuery = DPDoctorUtils.createCustomQuery(page, 0, doctorId, locationId, hospitalId,
							updatedTime, discarded, "rankingCount", searchTerm, category, null, "genericNames.name");
				} else {
					searchQuery = DPDoctorUtils.createCustomQuery(page, 0, doctorId, locationId, hospitalId,
							updatedTime, discarded, "rankingCount", searchTerm, category, null, "drugName");
				}

				List<ESDrugDocument> esDrugDocuments = elasticsearchTemplate.queryForList(searchQuery,
						ESDrugDocument.class);

				response = new ArrayList<DrugDocument>();
				for (ESDrugDocument esDrugDocument : esDrugDocuments) {
					String drugTypeStr = esDrugDocument.getDrugType();
					esDrugDocument.setDrugType(null);
					DrugDocument drugDocument = new DrugDocument();
					BeanUtil.map(esDrugDocument, drugDocument);
					DrugType drugType = new DrugType();
					drugType.setId(esDrugDocument.getDrugTypeId());
					drugType.setType(drugTypeStr);
					drugDocument.setDrugType(drugType);
					response.add(drugDocument);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<ESDrugDocument> getCustomGlobalDrugs(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded, String searchTerm, String category,
			Boolean searchByGenericName) {
		List<ESDrugDocument> response = null;
		try {
			SearchQuery searchQuery = null;

			if (searchByGenericName) {
				searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DRUG, page, 0, doctorId, locationId,
						hospitalId, updatedTime, discarded, null, searchTerm, null, category, null,
						"genericNames.name");
			} else {
				searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DRUG, page, 0, doctorId, locationId,
						hospitalId, updatedTime, discarded, null, searchTerm, null, category, null, "drugName");
			}

			response = elasticsearchTemplate.queryForList(searchQuery, ESDrugDocument.class);

			if (response != null)
				response = new ArrayList<ESDrugDocument>(new LinkedHashSet<ESDrugDocument>(response));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<ESDrugDocument> getGlobalDrugs(int page, int size, String updatedTime, boolean discarded,
			String searchTerm, String category, Boolean searchByGenericName) {
		List<ESDrugDocument> response = null;
		try {
			SearchQuery searchQuery = null;
			if (searchByGenericName) {
				searchQuery = DPDoctorUtils.createGlobalQuery(Resource.DRUG, page, size, updatedTime, discarded, null,
						searchTerm, null, category, null, "genericNames.name");
			} else {
				searchQuery = DPDoctorUtils.createGlobalQuery(Resource.DRUG, page, size, updatedTime, discarded, null,
						searchTerm, null, category, null, "drugName");
			}
			response = elasticsearchTemplate.queryForList(searchQuery, ESDrugDocument.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<ESDrugDocument> getCustomDrugs(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded, String searchTerm, String category,
			Boolean searchByGenericName) {
		List<ESDrugDocument> response = null;
		try {
			if (page > 0)
				return new ArrayList<ESDrugDocument>();
			if (doctorId == null)
				response = new ArrayList<ESDrugDocument>();
			else {
				SearchQuery searchQuery = null;
				if (searchByGenericName) {
					searchQuery = DPDoctorUtils.createCustomQuery(page, 0, doctorId, locationId, hospitalId,
							updatedTime, discarded, "rankingCount", searchTerm, category, null, "genericNames.name");
				} else {
					searchQuery = DPDoctorUtils.createCustomQuery(page, 0, doctorId, locationId, hospitalId,
							updatedTime, discarded, "rankingCount", searchTerm, category, null, "drugName");
				}
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
	public List<LabTest> searchLabTest(String range, int page, int size, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm) {
		List<LabTest> response = null;
		List<ESLabTestDocument> labTestDocuments = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			labTestDocuments = getGlobalLabTests(page, size, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			labTestDocuments = getCustomLabTests(page, size, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			labTestDocuments = getCustomGlobalLabTests(page, size, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		if (labTestDocuments != null) {
			response = new ArrayList<LabTest>();
			for (ESLabTestDocument labTestDocument : labTestDocuments) {
				LabTest labTest = new LabTest();
				BeanUtil.map(labTestDocument, labTest);
				if (labTestDocument.getTestId() != null) {
					ESDiagnosticTestDocument diagnosticTestDocument = esDiagnosticTestRepository
							.findOne(labTestDocument.getTestId());
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
	private List<ESLabTestDocument> getGlobalLabTests(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm) {
		List<ESLabTestDocument> response = null;
		try {
			Collection<String> testIds = null;
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				SearchQuery searchQueryForTest = createGlobalQueryWithoutDoctorId(0, 0, updatedTime, false, "testName",
						searchTerm, null, true, ESDiagnosticTestDocument.class, "testName");
				List<ESDiagnosticTestDocument> diagnosticTestCollections = elasticsearchTemplate
						.queryForList(searchQueryForTest, ESDiagnosticTestDocument.class);
				testIds = CollectionUtils.collect(diagnosticTestCollections, new BeanToPropertyValueTransformer("id"));
				if (testIds == null || testIds.isEmpty())
					return response;
			}
			SearchQuery searchQuery = createGlobalQueryWithoutDoctorId(page, size, updatedTime, discarded, null, null,
					testIds, false, null, null);
			response = elasticsearchTemplate.queryForList(searchQuery, ESLabTestDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting LabTests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ESLabTestDocument> getCustomLabTests(int page, int size, String locationId, String hospitalId,
			String updatedTime, boolean discarded, String searchTerm) {
		List<ESLabTestDocument> response = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				;
			else {
				Collection<String> testIds = null;
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					SearchQuery searchQueryForTest = createCustomGlobalQueryWithoutDoctorId(0, 0, locationId,
							hospitalId, updatedTime, false, "testName", searchTerm, null, true,
							ESDiagnosticTestDocument.class, "testName");
					List<ESDiagnosticTestDocument> diagnosticTestCollections = elasticsearchTemplate
							.queryForList(searchQueryForTest, ESDiagnosticTestDocument.class);
					testIds = CollectionUtils.collect(diagnosticTestCollections,
							new BeanToPropertyValueTransformer("id"));
					if (testIds == null || testIds.isEmpty())
						return response;
				}
				SearchQuery searchQuery = createCustomQueryWithoutDoctorId(page, size, locationId, hospitalId,
						updatedTime, discarded, null, null, testIds, false, null, null);
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
	private List<ESLabTestDocument> getCustomGlobalLabTests(int page, int size, String locationId, String hospitalId,
			String updatedTime, boolean discarded, String searchTerm) {
		List<ESLabTestDocument> response = null;
		try {
			Collection<String> testIds = null;
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				SearchQuery searchQueryForTest = createCustomGlobalQueryWithoutDoctorId(0, 0, locationId, hospitalId,
						updatedTime, false, "testName", searchTerm, null, true, ESDiagnosticTestDocument.class,
						"testName");
				List<ESDiagnosticTestDocument> diagnosticTestCollections = elasticsearchTemplate
						.queryForList(searchQueryForTest, ESDiagnosticTestDocument.class);
				testIds = CollectionUtils.collect(diagnosticTestCollections, new BeanToPropertyValueTransformer("id"));
				if (testIds == null || testIds.isEmpty())
					return response;
			}
			SearchQuery searchQuery = createCustomGlobalQueryWithoutDoctorId(page, size, locationId, hospitalId,
					updatedTime, discarded, null, null, testIds, false, null, null);
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
			transnationalService.addResource(new ObjectId(ESDiagnosticTestDocument.getId()), Resource.DIAGNOSTICTEST,
					true);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Diagnostic Test in ES");
		}
		return response;
	}

	@Override
	public List<ESDiagnosticTestDocument> searchDiagnosticTest(String range, int page, int size, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESDiagnosticTestDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalDiagnosticTests(page, size, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomDiagnosticTests(page, size, locationId, hospitalId, updatedTime, discarded, searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalDiagnosticTests(page, size, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case PATIIENT:
			response = getDiagnosticTestsForPatients(page, size, updatedTime, discarded, searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	private List<ESDiagnosticTestDocument> getDiagnosticTestsForPatients(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm) {
		List<ESDiagnosticTestDocument> response = null;
		try {
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("testName", searchTerm));
			if (!discarded)
				boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

			SearchQuery searchQuery = null;

			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size))
						.withSort(SortBuilders.fieldSort("testName").order(SortOrder.ASC)).build();

			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("testName").order(SortOrder.ASC)).build();

			response = elasticsearchTemplate.queryForList(searchQuery, ESDiagnosticTestDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Diagnostic Tests For Patient");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Diagnostic Tests For Patient");
		}
		return response;
	}

	@Override
	public Integer getDiagnosticTestCount(String range, int page, int size, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Integer count = null;
		try {
			count = searchDiagnosticTest(range, page, size, locationId, hospitalId, updatedTime, discarded, searchTerm)
					.size();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting DiagnosticTests count");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting DiagnosticTests count");
		}
		return count;
	}

	private List<ESDiagnosticTestDocument> getGlobalDiagnosticTests(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm) {
		List<ESDiagnosticTestDocument> response = null;
		try {
			SearchQuery searchQuery = createGlobalQueryWithoutDoctorId(page, size, updatedTime, discarded, "testName",
					searchTerm, null, false, null, null);
			response = elasticsearchTemplate.queryForList(searchQuery, ESDiagnosticTestDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting DiagnosticTests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting DiagnosticTests");
		}
		return response;
	}

	private List<ESDiagnosticTestDocument> getCustomDiagnosticTests(int page, int size, String locationId,
			String hospitalId, String updatedTime, boolean discarded, String searchTerm) {
		List<ESDiagnosticTestDocument> response = null;
		try {
			if (locationId == null && hospitalId == null)
				;
			else {
				SearchQuery searchQuery = createCustomQueryWithoutDoctorId(page, size, locationId, hospitalId,
						updatedTime, discarded, "testName", searchTerm, null, false, null, null);
				response = elasticsearchTemplate.queryForList(searchQuery, ESDiagnosticTestDocument.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting LabTests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
		}
		return response;
	}

	private List<ESDiagnosticTestDocument> getCustomGlobalDiagnosticTests(int page, int size, String locationId,
			String hospitalId, String updatedTime, boolean discarded, String searchTerm) {
		List<ESDiagnosticTestDocument> response = null;
		try {
			SearchQuery searchQuery = createCustomGlobalQueryWithoutDoctorId(page, size, locationId, hospitalId,
					updatedTime, discarded, "testName", searchTerm, null, false, null, null);
			response = elasticsearchTemplate.queryForList(searchQuery, ESDiagnosticTestDocument.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting LabTests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	public SearchQuery createGlobalQueryWithoutDoctorId(int page, int size, String updatedTime, Boolean discarded,
			String searchTermFieldName, String searchTerm, Collection<String> testIds, Boolean calculateCount,
			Class classForCount, String sortBy) {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime))
						.to(new Date().getTime()))
				.mustNot(QueryBuilders.existsQuery("locationId")).mustNot(QueryBuilders.existsQuery("hospitalId"));

		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(searchTermFieldName, searchTerm));
		if (!discarded)
			boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));
		if (testIds != null && !testIds.isEmpty())
			boolQueryBuilder.must(QueryBuilders.termsQuery("testId", testIds));
		if (calculateCount)
			size = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
					classForCount);
		SearchQuery searchQuery = null;
		if (!DPDoctorUtils.anyStringEmpty(sortBy)) {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size, Direction.ASC, sortBy)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.ASC)).build();
		} else {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC))
						.withPageable(new PageRequest(page, size)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();
		}

		return searchQuery;
	}

	@SuppressWarnings("unchecked")
	public SearchQuery createCustomQueryWithoutDoctorId(int page, int size, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTermFieldName, String searchTerm,
			Collection<String> testIds, Boolean calculateCount, Class classForCount, String sortBy) {

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime))
						.to(new Date().getTime()))
				.must(QueryBuilders.termQuery("locationId", locationId))
				.must(QueryBuilders.termQuery("hospitalId", hospitalId));

		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(searchTermFieldName, searchTerm));
		if (!discarded)
			boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));
		if (testIds != null && !testIds.isEmpty())
			boolQueryBuilder.must(QueryBuilders.termsQuery("testId", testIds));
		if (calculateCount)
			size = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
					classForCount);
		SearchQuery searchQuery = null;
		if (!DPDoctorUtils.anyStringEmpty(sortBy)) {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size, Direction.ASC, sortBy)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.ASC)).build();
		} else {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size))
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();
		}

		return searchQuery;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public SearchQuery createCustomGlobalQueryWithoutDoctorId(int page, int size, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTermFieldName, String searchTerm,
			Collection<String> testIds, Boolean calculateCount, Class classForCount, String sortBy) {

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)).to(new Date().getTime()));

		if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			boolQueryBuilder
					.must(QueryBuilders.orQuery(
							QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("locationId")),
							QueryBuilders.termQuery("locationId", locationId)))
					.must(QueryBuilders.orQuery(
							QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("hospitalId")),
							QueryBuilders.termQuery("hospitalId", hospitalId)));
		}

		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(searchTermFieldName, searchTerm));
		if (!discarded)
			boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

		if (testIds != null && !testIds.isEmpty())
			boolQueryBuilder.must(QueryBuilders.termsQuery("testId", testIds));

		if (calculateCount)
			size = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
					classForCount);
		SearchQuery searchQuery = null;
		if (!DPDoctorUtils.anyStringEmpty(sortBy)) {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size, Direction.ASC, sortBy)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.ASC)).build();
		} else {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size))
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();
		}

		return searchQuery;
	}

	@Override
	public void addDoctorDrug(ESDoctorDrugDocument request, ObjectId resourceId) {
		try {
			esDoctorDrugRepository.save(request);
			transnationalService.addResource(resourceId, Resource.DOCTORDRUG, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Doctor Drug in ES");
		}
	}

	@Override
	public List<ESAdvicesDocument> searchAdvices(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String disease, String searchTerm) {
		List<ESAdvicesDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalAdvices(page, size, doctorId, updatedTime, discarded, disease, searchTerm);
			break;
		case CUSTOM:
			response = getCustomAdvices(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, disease,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalAdvices(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					disease, searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	private List<ESAdvicesDocument> getCustomGlobalAdvices(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String disease, String searchTerm) {
		List<ESAdvicesDocument> response = null;
		try {

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.ADVICE, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, null, null, disease, "advice");
			response = elasticsearchTemplate.queryForList(searchQuery, ESAdvicesDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Advices");
		}
		return response;

	}

	private List<ESAdvicesDocument> getGlobalAdvices(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String disease, String searchTerm) {
		List<ESAdvicesDocument> response = null;
		try {

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.ADVICE, page, size, updatedTime,
					discarded, null, searchTerm, null, null, disease, "advice");

			response = elasticsearchTemplate.queryForList(searchQuery, ESAdvicesDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Advices");
		}
		return response;
	}

	private List<ESAdvicesDocument> getCustomAdvices(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String disease, String searchTerm) {
		List<ESAdvicesDocument> response = null;
		try {
			SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
					updatedTime, discarded, null, searchTerm, null, disease, "advice");
			response = elasticsearchTemplate.queryForList(searchQuery, ESAdvicesDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Advices");
		}
		return response;
	}

	@Override
	public boolean addAdvices(ESAdvicesDocument request) {
		boolean response = false;
		try {
			esAdvicesRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.ADVICE, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Advices");
		}
		return response;
	}

	private List<ESDrugDocument> addStockToDrug(List<ESDrugDocument> drugs)
	{

		for (ESDrugDocument drug : drugs) {

			if (!DPDoctorUtils.anyStringEmpty(drug.getLocationId(), drug.getHospitalId(), drug.getId())) {
				InventoryItem inventoryItem = inventoryService.getInventoryItemByResourceId(drug.getLocationId(),
						drug.getHospitalId(), drug.getDrugCode());
				if (inventoryItem != null) {
					InventoryItemLookupResposne inventoryItemLookupResposne = inventoryService
							.getInventoryItem(inventoryItem.getId());
					drug.setTotalStock(inventoryItemLookupResposne.getTotalStock());
					drug.setRetailPrice(inventoryItemLookupResposne.getRetailPrice());
					drug.setStockingUnit(inventoryItemLookupResposne.getStockingUnit());
				}
			}
		}
		return drugs;
	}

	private List<DrugDocument> addStockToDrugWeb(List<DrugDocument> drugs) {
		List<DrugDocument> response= new ArrayList<>();

		for (DrugDocument drug : drugs) {

			if (!DPDoctorUtils.anyStringEmpty(drug.getLocationId(), drug.getHospitalId(), drug.getDrugCode())) {
				InventoryItem inventoryItem = inventoryService.getInventoryItemByResourceId(drug.getLocationId(),
						drug.getHospitalId(), drug.getDrugCode());
				if (inventoryItem != null) {
					InventoryItemLookupResposne inventoryItemLookupResposne = inventoryService
							.getInventoryItem(inventoryItem.getId());
					drug.setTotalStock(inventoryItemLookupResposne.getTotalStock());
					drug.setRetailPrice(inventoryItemLookupResposne.getRetailPrice());
					drug.setStockingUnit(inventoryItemLookupResposne.getStockingUnit());
				}
			}
			response.add(drug);
		}
		return response;
	}
}
