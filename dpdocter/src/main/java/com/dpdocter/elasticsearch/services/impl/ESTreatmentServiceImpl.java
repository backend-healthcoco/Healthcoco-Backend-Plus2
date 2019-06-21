package com.dpdocter.elasticsearch.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

import com.dpdocter.beans.TreatmentService;
import com.dpdocter.beans.TreatmentServiceCost;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceCostDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceDocument;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESTreatmentServiceCostRepository;
import com.dpdocter.elasticsearch.repository.ESTreatmentServiceRepository;
import com.dpdocter.elasticsearch.services.ESTreatmentService;
import com.dpdocter.enums.PatientTreatmentService;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class ESTreatmentServiceImpl implements ESTreatmentService {

	private static Logger logger = Logger.getLogger(ESTreatmentServiceImpl.class.getName());

	@Autowired
	private ESTreatmentServiceRepository esTreatmentServiceRepository;

	@Autowired
	private ESTreatmentServiceCostRepository esTreatmentServiceCostRepository;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Autowired
	private ESDoctorRepository esDoctorRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Override
	public void addEditService(ESTreatmentServiceDocument esTreatmentServiceDocument) {
		try {
			esTreatmentServiceRepository.save(esTreatmentServiceDocument);
			transactionalManagementService.addResource(new ObjectId(esTreatmentServiceDocument.getId()),
					Resource.TREATMENTSERVICE, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Treatment Service in ES");
		}
	}

	@Override
	public void addEditServiceCost(ESTreatmentServiceCostDocument esTreatmentServiceDocument) {
		try {
			esTreatmentServiceCostRepository.save(esTreatmentServiceDocument);
			transactionalManagementService.addResource(new ObjectId(esTreatmentServiceDocument.getId()),
					Resource.TREATMENTSERVICECOST, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Treatment Service Cost in ES");
		}
	}

	@Override
	public Response<Object> search(String type, String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		
		Response<Object> response = new Response<Object>();
		List<?> dataList = new ArrayList<Object>();

		switch (PatientTreatmentService.valueOf(type.toUpperCase())) {

		case SERVICE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalTreatmentServices(page, size, doctorId, updatedTime, discarded, searchTerm);
				break;
			case CUSTOM:
				response = getCustomTreatmentServices(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded, searchTerm);
				break;
			case BOTH:
				response = getCustomGlobalTreatmentServices(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded, searchTerm);
				break;
			default:
				break;
			}
			break;
		}
		case SERVICECOST: {
			dataList = getCustomTreatmentServicesCost(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			response.setDataList(dataList);
			break;
		}
		default:
			break;
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<Object> getGlobalTreatmentServices(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		Response<Object> response = new Response<Object>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, new PageRequest(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));
						if (!DPDoctorUtils.anyStringEmpty(searchTerm))
							boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(new PageRequest(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.TREATMENTSERVICE, page, size,
					updatedTime, discarded, null, searchTerm, specialities, null, null, "name");
			
			Integer count = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESTreatmentServiceDocument.class);
			if(count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESTreatmentServiceDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;
	}

	private Response<Object> getCustomTreatmentServices(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<Object> response = new Response<Object>();
		try {
			SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
					updatedTime, discarded, "rankingCount", searchTerm, null, null, "name");
			Integer count = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESTreatmentServiceDocument.class);
			if(count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESTreatmentServiceDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<Object> getCustomGlobalTreatmentServices(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<Object> response = new Response<Object>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, new PageRequest(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));
						// if (!DPDoctorUtils.anyStringEmpty(searchTerm))
						// boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality",
						// searchTerm));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(new PageRequest(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {

								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");

							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.TREATMENTSERVICE, page, 0,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null,
					null, "name");
			Integer count = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESTreatmentServiceDocument.class);
			if(count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESTreatmentServiceDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting service");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<?> getCustomTreatmentServicesCost(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<TreatmentServiceCost> response = null;
		try {
			Collection<String> serviceIds = null;
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				SearchQuery searchQuery = createCustomGlobalQuery(0, 0, doctorId, locationId, hospitalId, updatedTime,
						discarded, "name", searchTerm, null, true, ESTreatmentServiceDocument.class, null);
				List<ESTreatmentServiceDocument> treatmentServiceDocuments = elasticsearchTemplate
						.queryForList(searchQuery, ESTreatmentServiceDocument.class);
				serviceIds = CollectionUtils.collect(treatmentServiceDocuments,
						new BeanToPropertyValueTransformer("id"));
				if (serviceIds == null || serviceIds.isEmpty())
					return response;
			}
			SearchQuery searchQuery = createCustomQuery(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, null, null, serviceIds, false, null, null);
			List<ESTreatmentServiceCostDocument> esTreatmentServiceCostDocuments = elasticsearchTemplate
					.queryForList(searchQuery, ESTreatmentServiceCostDocument.class);
			if (esTreatmentServiceCostDocuments != null && !esTreatmentServiceCostDocuments.isEmpty()) {
				response = new ArrayList<TreatmentServiceCost>();
				for (ESTreatmentServiceCostDocument esTreatmentServiceCostDocument : esTreatmentServiceCostDocuments) {
					ESTreatmentServiceDocument esTreatmentServiceDocument = esTreatmentServiceRepository
							.findOne(esTreatmentServiceCostDocument.getTreatmentServiceId());
					TreatmentService treatmentService = new TreatmentService();
					if (esTreatmentServiceDocument != null)
						BeanUtil.map(esTreatmentServiceDocument, treatmentService);

					TreatmentServiceCost treatmentServiceCost = new TreatmentServiceCost();
					BeanUtil.map(esTreatmentServiceCostDocument, treatmentServiceCost);
					treatmentServiceCost.setTreatmentService(treatmentService);
					response.add(treatmentServiceCost);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting LabTests");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Treatment Services with cost");
		}
		return response;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public SearchQuery createCustomGlobalQuery(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTermFieldName, String searchTerm,
			Collection<String> serviceIds, Boolean calculateCount, Class classForCount, String sortBy) {

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)));

		if (!DPDoctorUtils.anyStringEmpty(doctorId))
			boolQueryBuilder.must(
					QueryBuilders.orQuery(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("doctorId")),
							QueryBuilders.termQuery("doctorId", doctorId)));

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

		if (serviceIds != null && !serviceIds.isEmpty())
			boolQueryBuilder.must(QueryBuilders.termsQuery("treatmentServiceId", serviceIds));

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
						.withPageable(new PageRequest(page, size, Direction.DESC, "updatedTime")).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();
		}
		return searchQuery;
	}

	@SuppressWarnings("unchecked")
	public SearchQuery createCustomQuery(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTermFieldName, String searchTerm,
			Collection<String> serviceIds, Boolean calculateCount, Class classForCount, String sortBy) {

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)))
				.must(QueryBuilders.termQuery("doctorId", doctorId))
				.must(QueryBuilders.termQuery("locationId", locationId))
				.must(QueryBuilders.termQuery("hospitalId", hospitalId));

		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(searchTermFieldName, searchTerm));
		if (!discarded)
			boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));
		if (serviceIds != null && !serviceIds.isEmpty())
			boolQueryBuilder.must(QueryBuilders.termsQuery("treatmentServiceId", serviceIds));
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
						.withPageable(new PageRequest(page, size, Direction.DESC, "updatedTime")).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();
		}

		return searchQuery;
	}

}
