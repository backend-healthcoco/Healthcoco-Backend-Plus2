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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.elasticsearch.document.ESBabyNoteDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESOperationNoteDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.document.EsLabourNoteDocument;
import com.dpdocter.elasticsearch.repository.ESBabyNoteRepository;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESLabourNoteRepository;
import com.dpdocter.elasticsearch.repository.ESOperationNoteRepository;
import com.dpdocter.elasticsearch.services.ESDischargeSummaryService;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class ESDischargeSummaryServiceImpl implements ESDischargeSummaryService {

	private static Logger logger = Logger.getLogger(ESDischargeSummaryServiceImpl.class.getName());

	@Autowired
	private ESOperationNoteRepository esOperationNoteRepository;

	@Autowired
	private ESBabyNoteRepository esBabyNoteRepository;

	@Autowired
	private ESLabourNoteRepository esLabourNoteRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private ESDoctorRepository esDoctorRepository;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Override
	public boolean addBabyNote(ESBabyNoteDocument request) {
		boolean response = false;
		try {
			esBabyNoteRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.BABY_NOTES, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Baby Note");
		}
		return response;
	}

	@Override
	public boolean addOperationNote(ESOperationNoteDocument request) {
		boolean response = false;
		try {
			esOperationNoteRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.OPERATION_NOTES, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Baby Note");
		}
		return response;
	}

	@Override
	public boolean addLabourNotes(EsLabourNoteDocument request) {
		boolean response = false;
		try {
			esLabourNoteRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.LABOUR_NOTES, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Labour Note");
		}
		return response;
	}

	@Override
	public List<ESBabyNoteDocument> searchBabyNotes(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESBabyNoteDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalBabyNote(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomBabyNote(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalBabyNote(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public List<ESOperationNoteDocument> searchOperationNotes(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESOperationNoteDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalOperationNote(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomOperationNote(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalOperationNote(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public List<EsLabourNoteDocument> searchLabourNotes(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<EsLabourNoteDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalLabourNote(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomLabourNote(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalLabourNote(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ESBabyNoteDocument> getGlobalBabyNote(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		List<ESBabyNoteDocument> response = null;
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

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.BABY_NOTES, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "babyNotes");
			response = elasticsearchTemplate.queryForList(searchQuery, ESBabyNoteDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Baby Note");
		}
		return response;
	}

	private List<ESBabyNoteDocument> getCustomBabyNote(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESBabyNoteDocument> response = null;
		try {
			if (doctorId == null)
				response = new ArrayList<ESBabyNoteDocument>();
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "babyNotes");
				response = elasticsearchTemplate.queryForList(searchQuery, ESBabyNoteDocument.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Baby Note");
		}
		return response;
	}

		private List<ESBabyNoteDocument> getCustomGlobalBabyNote(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESBabyNoteDocument> response = null;
		try {
			List<ESDoctorDocument> doctorCollections = null;
			@SuppressWarnings("unchecked")
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, new PageRequest(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

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

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.BABY_NOTES, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null,
					"babyNotes");
			response = elasticsearchTemplate.queryForList(searchQuery, ESBabyNoteDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Baby Note");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<ESOperationNoteDocument> getGlobalOperationNote(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		List<ESOperationNoteDocument> response = null;
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

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.OPERATION_NOTES, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "operationNotes");
			response = elasticsearchTemplate.queryForList(searchQuery, ESOperationNoteDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Operation Note");
		}
		return response;
	}

	private List<ESOperationNoteDocument> getCustomOperationNote(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESOperationNoteDocument> response = null;
		try {
			if (doctorId == null)
				response = new ArrayList<ESOperationNoteDocument>();
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "operationNotes");
				response = elasticsearchTemplate.queryForList(searchQuery, ESOperationNoteDocument.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Operation Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ESOperationNoteDocument> getCustomGlobalOperationNote(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESOperationNoteDocument> response = null;
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

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.OPERATION_NOTES, page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null,
					null, "operationNotes");
			response = elasticsearchTemplate.queryForList(searchQuery, ESOperationNoteDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting operation Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<EsLabourNoteDocument> getGlobalLabourNote(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		List<EsLabourNoteDocument> response = null;
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

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.LABOUR_NOTES, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "labourNotes");
			response = elasticsearchTemplate.queryForList(searchQuery, EsLabourNoteDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Labour Note");
		}
		return response;
	}

	private List<EsLabourNoteDocument> getCustomLabourNote(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<EsLabourNoteDocument> response = null;
		try {
			if (doctorId == null)
				response = new ArrayList<EsLabourNoteDocument>();
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "labourNotes");
				response = elasticsearchTemplate.queryForList(searchQuery, EsLabourNoteDocument.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Labour Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<EsLabourNoteDocument> getCustomGlobalLabourNote(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<EsLabourNoteDocument> response = null;
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

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.LABOUR_NOTES, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null,
					"labourNotes");
			response = elasticsearchTemplate.queryForList(searchQuery, EsLabourNoteDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting labour Note");
		}
		return response;
	}
}
