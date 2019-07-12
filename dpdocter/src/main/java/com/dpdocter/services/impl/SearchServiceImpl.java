package com.dpdocter.services.impl;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.Feedback;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.FeedbackCollection;
import com.dpdocter.elasticsearch.beans.ESDoctorWEbSearch;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;
import com.dpdocter.elasticsearch.document.ESSymptomDiseaseConditionDocument;
import com.dpdocter.elasticsearch.repository.ESCityRepository;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESSymptomDiseaseConditionRepository;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.response.DoctorClinicProfileBySlugUrlResponse;
import com.dpdocter.response.DoctorProfileBySlugUrlResponse;
import com.dpdocter.response.ResourcesCountResponse;
import com.dpdocter.response.SearchDoctorResponse;
import com.dpdocter.response.SearchLandmarkLocalityResponse;
import com.dpdocter.services.SearchService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class SearchServiceImpl implements SearchService {

//	@Autowired
//	private ESSpecialityRepository esSpecialityRepository;
//
//	@Autowired
//	private ESServicesRepository esServicesRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private ESDoctorRepository esDoctorRepository;
	
	@Autowired
	private ESCityRepository esCityRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	ESSymptomDiseaseConditionRepository esSymptomDiseaseConditionRepository;
	
	@Override
	public SearchDoctorResponse searchDoctors(int page, int size, String city, String location, String latitude,
			String longitude, String speciality, String symptom, Boolean booking, Boolean calling, int minFee,
			int maxFee, int minTime, int maxTime, List<String> days, String gender, int minExperience,
			int maxExperience, String service, String locality, Boolean otherArea, String expertIn, String symptomDiseaseCondition) {
		List<ESDoctorDocument> esDoctorDocuments = null;
		List<ESDoctorDocument> nearByDoctors = null;
		SearchDoctorResponse response = null;
		try {
			if (city.equalsIgnoreCase("undefined")) {
				return null;
			}


			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.matchQuery("isDoctorListed", true))
					.must(QueryBuilders.matchQuery("isClinic", true));
			BoolQueryBuilder boolQueryBuilderForNearByDoctors = new BoolQueryBuilder()
					.must(QueryBuilders.matchQuery("isDoctorListed", true))
					.must(QueryBuilders.matchQuery("isClinic", true));

			if (!DPDoctorUtils.anyStringEmpty(city)) {
				city = city.replaceAll("-", " ");
				city = WordUtils.capitalizeFully(city);
				long cityCount = elasticsearchTemplate.count(new CriteriaQuery(new Criteria("city").is(city).and("isActivated").is(true)),
						ESCityDocument.class);

				if (cityCount == 0) {
					throw new BusinessException(ServiceError.InvalidInput, "Invalid City");
				}
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("city", city));
				boolQueryBuilderForNearByDoctors.must(QueryBuilders.matchPhrasePrefixQuery("city", city));
			}

			if (!(DPDoctorUtils.allStringsEmpty(expertIn) || expertIn.equalsIgnoreCase("undefined") || expertIn.equalsIgnoreCase("DOCTOR"))) {
				
				if(expertIn.startsWith("doctors-for-")) {
					service = expertIn.replace("doctors-for-","").replaceAll("-", " ");
				}else if(expertIn.startsWith("treatments-for-")) {
					symptomDiseaseCondition = expertIn.replace("treatments-for-","").replaceAll("-", " ");
				}
				else {
					speciality = expertIn.replaceAll("-", " ");
				}
			}
			
			if (DPDoctorUtils.allStringsEmpty(speciality) || speciality.equalsIgnoreCase("undefined") || speciality.equalsIgnoreCase("DOCTOR")) {
				speciality = null;
			}else {
				speciality = speciality.replaceAll("-", " ");
				QueryBuilder specialityQueryBuilder = null;
				
				if (speciality.equalsIgnoreCase("GYNECOLOGIST")) {
					speciality = "GYNAECOLOGIST".toLowerCase();
				}
				
				if (speciality.equalsIgnoreCase("GENERAL PHYSICIAN") || speciality.equalsIgnoreCase("FAMILY PHYSICIAN")) {
					specialityQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("specialitiesValue", "GENERAL PHYSICIAN".toLowerCase(), "FAMILY PHYSICIAN".toLowerCase()))
					.should(QueryBuilders.termsQuery("parentSpecialities", "GENERAL PHYSICIAN".toLowerCase(), "FAMILY PHYSICIAN".toLowerCase())).minimumShouldMatch(1);
				}else {
					specialityQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("specialitiesValue", speciality.toLowerCase()))
							.should(QueryBuilders.termsQuery("parentSpecialities", speciality.toLowerCase())).minimumShouldMatch(1);
				}

						//createSpecialityFilter(speciality);
				if (specialityQueryBuilder != null) {
					boolQueryBuilder.must(specialityQueryBuilder);
					boolQueryBuilderForNearByDoctors.must(specialityQueryBuilder);
				}
			}

			if (DPDoctorUtils.allStringsEmpty(service) || service.equalsIgnoreCase("undefined") || service.equalsIgnoreCase("DOCTOR")) {
				service = null;
			} else {
				service = service.replace("doctors-for-","").replaceAll("-", " ");
				QueryBuilder serviceQueryBuilder = QueryBuilders.termsQuery("servicesValue", service.toLowerCase());
						
						//createServiceFilter(service);
				if (serviceQueryBuilder != null) {
					boolQueryBuilder.must(serviceQueryBuilder);
					boolQueryBuilderForNearByDoctors.must(serviceQueryBuilder);
				}
			}
			
			if (DPDoctorUtils.allStringsEmpty(symptomDiseaseCondition) || symptomDiseaseCondition.equalsIgnoreCase("undefined") || symptomDiseaseCondition.equalsIgnoreCase("DOCTOR")) {
				symptomDiseaseCondition = null;
			} else {
				symptomDiseaseCondition = symptomDiseaseCondition.replace("treatments-for-","").replaceAll("-", " ");
				
				List<ESSymptomDiseaseConditionDocument> documents = esSymptomDiseaseConditionRepository.findByQueryAnnotation(symptomDiseaseCondition);
				if(documents != null && !documents.isEmpty()) {
					List<String> specialities = new ArrayList<>();
					for(ESSymptomDiseaseConditionDocument document : documents) {
						if(document.getSpecialities()!= null)specialities.addAll(document.getSpecialities());
					}
					if(specialities != null && !specialities.isEmpty()) {
						List<String> lowerStringSpecialities = new ArrayList<String>();
						for(String lowerStringSpeciality : specialities)lowerStringSpecialities.add(lowerStringSpeciality.toLowerCase());
						
						QueryBuilder symptomDiseaseConditionBuilder = QueryBuilders.boolQuery()
								.should(QueryBuilders.termsQuery("specialitiesValue", lowerStringSpecialities))
								.should(QueryBuilders.termsQuery("parentSpecialities", lowerStringSpecialities)).minimumShouldMatch(1);
						
						boolQueryBuilder.must(symptomDiseaseConditionBuilder);
						boolQueryBuilderForNearByDoctors.must(symptomDiseaseConditionBuilder);
					}
				}
		
			}
			
			if (booking != null && calling != null && !(booking && calling)) { 
				QueryBuilder facilityQueryBuilder = createFacilityBuilder(booking, calling);
				if (facilityQueryBuilder != null) {
					boolQueryBuilder.must(facilityQueryBuilder);
					boolQueryBuilderForNearByDoctors.must(facilityQueryBuilder);
				}
			}

			if(!(maxFee == 0 && minFee == 0))createConsultationFeeFilter(boolQueryBuilder, maxFee, minFee, boolQueryBuilderForNearByDoctors);
			
			if(!(maxExperience == 0 && minExperience == 0))createExperienceFilter(boolQueryBuilder, maxExperience, minExperience, boolQueryBuilderForNearByDoctors);
			
			if (!DPDoctorUtils.anyStringEmpty(gender)) {
				boolQueryBuilder.must(QueryBuilders.matchQuery("gender", gender));
				boolQueryBuilderForNearByDoctors.must(QueryBuilders.matchQuery("gender", gender));
			}

			if(!((days == null || days.isEmpty()) && maxTime == 0 && minTime == 0))createTimeFilter(boolQueryBuilder, maxTime, minTime, days, boolQueryBuilderForNearByDoctors);

			Integer count = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), ESDoctorDocument.class);
			
			if(count > 0) {
				SearchQuery searchQuery = null;

				if (DPDoctorUtils.anyStringEmpty(locality) || locality.equalsIgnoreCase("undefined")) {
					if (size > 0)
						searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
								.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
								.withPageable(PageRequest.of(page, size)).build();
					else
						searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
								.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC)).build();

					esDoctorDocuments = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
				} else {
					locality = locality.replace("-", " ");
					if (!otherArea) {
						if (size > 0)
							searchQuery = new NativeSearchQueryBuilder()
									.withQuery(boolQueryBuilder.must(QueryBuilders
											.multiMatchQuery(locality, "landmarkDetails", "streetAddress", "locality")
											.type(Type.PHRASE_PREFIX)))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
									.withPageable(PageRequest.of(page, size)).build();
						else
							searchQuery = new NativeSearchQueryBuilder()
									.withQuery(boolQueryBuilder.must(QueryBuilders
											.multiMatchQuery(locality, "landmarkDetails", "streetAddress", "locality")
											.type(Type.PHRASE_PREFIX)))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC)).build();

						esDoctorDocuments = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
					}

					if (esDoctorDocuments == null || esDoctorDocuments.isEmpty()) {
						if (size > 0)
							searchQuery = new NativeSearchQueryBuilder()
									.withQuery(boolQueryBuilderForNearByDoctors.mustNot(QueryBuilders
											.multiMatchQuery(locality, "landmarkDetails", "streetAddress", "locality")
											.type(Type.PHRASE_PREFIX)))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
									.withPageable(PageRequest.of(page, size)).build();
						else
							searchQuery = new NativeSearchQueryBuilder()
									.withQuery(boolQueryBuilderForNearByDoctors.mustNot(QueryBuilders
											.multiMatchQuery(locality, "landmarkDetails", "streetAddress", "locality")
											.type(Type.PHRASE_PREFIX)))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC)).build();
						nearByDoctors = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
					} else {

						if (size > 0) {
							size = size - esDoctorDocuments.size();
							if (size > 0) {
								if (size > 0)
									searchQuery = new NativeSearchQueryBuilder()
											.withQuery(
													boolQueryBuilderForNearByDoctors
															.mustNot(QueryBuilders
																	.multiMatchQuery(locality, "landmarkDetails",
																			"streetAddress", "locality")
																	.type(Type.PHRASE_PREFIX)))
											.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
											.withPageable(PageRequest.of(page, size)).build();
								nearByDoctors = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
							}
						} else {
							searchQuery = new NativeSearchQueryBuilder()
									.withQuery(boolQueryBuilderForNearByDoctors.mustNot(QueryBuilders
											.multiMatchQuery(locality, "landmarkDetails", "streetAddress", "locality")
											.type(Type.PHRASE_PREFIX)))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC)).build();

							nearByDoctors = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
						}
					}
				}
			}
//			if (!(esDoctorDocuments == null && nearByDoctors == null)) {
				response = new SearchDoctorResponse();

				if (!DPDoctorUtils.allStringsEmpty(locality) && !locality.equalsIgnoreCase("undefined")) {

					response.setMetaData(response.getMetaData() + StringUtils.capitalize(locality) + ", ");
					response.setLocality(StringUtils.capitalize(locality));
					response.setSlugLocality(locality.toLowerCase().replaceAll(" ", "-"));
				}
				if (DPDoctorUtils.anyStringEmpty(city)) {
					city = "Nagpur";
				}
				response.setMetaData(response.getMetaData() + StringUtils.capitalize(city));
				response.setCity(StringUtils.capitalize(city));
				response.setSlugCity(city.toLowerCase().replaceAll(" ", "-"));
				response.setCount(count);

				if (esDoctorDocuments != null) {
					response.setDoctors(formatDoctorData(esDoctorDocuments, latitude, longitude));
				}
				if (nearByDoctors != null) {
					response.setNearByDoctors(formatDoctorData(nearByDoctors, latitude, longitude));
				}

				if (!DPDoctorUtils.anyStringEmpty(speciality) && !speciality.equalsIgnoreCase("NAGPUR")) {
					String unformattedSpeciality = StringUtils.capitalize(speciality);
					response.setUnformattedSpeciality(unformattedSpeciality);
					
					speciality = speciality.toLowerCase().replaceAll(" ", "-");
					response.setSpeciality(speciality);
					response.setMetaData(unformattedSpeciality + "s in ");
				
				} else if (!DPDoctorUtils.anyStringEmpty(service) && !service.equalsIgnoreCase("NAGPUR")) {
					
					String unformattedService = "Doctors for "+StringUtils.capitalize(service);
					
					response.setUnformattedService(unformattedService);
					
					service = "doctors-for-"+service.toLowerCase().replaceAll(" ", "-");
					response.setService(service);
					
					
					response.setMetaData(unformattedService + " in ");
				} else if (!DPDoctorUtils.anyStringEmpty(symptomDiseaseCondition) && !symptomDiseaseCondition.equalsIgnoreCase("NAGPUR")) {
					
					String unformattedSymptomDiseaseCondition = "Treatments for "+StringUtils.capitalize(symptomDiseaseCondition);
					
					response.setUnformattedSymptomDiseaseCondition(unformattedSymptomDiseaseCondition);
					
					symptomDiseaseCondition = "treatments-for-"+unformattedSymptomDiseaseCondition.toLowerCase().replaceAll(" ", "-");
					response.setSymptomDiseaseCondition(symptomDiseaseCondition);
					
					
					response.setMetaData(unformattedSymptomDiseaseCondition + " in ");
				} else {
					response.setMetaData("Doctors in ");
				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error While searching Doctor From ES : " + e.getMessage());
		}
		return response;
	}

	private List<ESDoctorWEbSearch> formatDoctorData(List<ESDoctorDocument> esDoctorDocuments, String latitude,
			String longitude) {
		List<ESDoctorWEbSearch> response = new ArrayList<ESDoctorWEbSearch>();

		if (esDoctorDocuments != null) {
			for (ESDoctorDocument doctorDocument : esDoctorDocuments) {
				ESDoctorWEbSearch doctorWEbSearch = new ESDoctorWEbSearch();
				BeanUtil.map(doctorDocument, doctorWEbSearch);
				
				doctorWEbSearch.setSpecialities(doctorDocument.getSpecialitiesValue());
				if(doctorDocument.getServicesValue() != null && !doctorDocument.getServicesValue().isEmpty() && doctorDocument.getServicesValue().size()>3) {
					doctorWEbSearch.setServices(doctorDocument.getServicesValue().subList(0, 3));
				}else doctorWEbSearch.setServices(doctorDocument.getServicesValue());
				if (doctorWEbSearch.getThumbnailUrl() != null)
					doctorWEbSearch.setThumbnailUrl(getFinalImageURL(doctorWEbSearch.getThumbnailUrl()));

				response.add(doctorWEbSearch);
			}
		}
		return response;
	}
	
//	@SuppressWarnings("unchecked")
//	private QueryBuilder createServiceFilter(String service) {
//		QueryBuilder queryBuilder = null;
//			List<ESServicesDocument> esServicesDocuments = esServicesRepository.findByQueryAnnotation(service);
//			
//			if (esServicesDocuments != null) {
//				Collection<String> serviceIds = CollectionUtils.collect(esServicesDocuments,
//						new BeanToPropertyValueTransformer("id"));
//				if (serviceIds == null)
//					serviceIds = CollectionUtils.EMPTY_COLLECTION;
//				queryBuilder = QueryBuilders.termsQuery("services", serviceIds);
//			}
//		return queryBuilder;
//	}
//
//	@SuppressWarnings("unchecked")
//	private QueryBuilder createSpecialityFilter(String speciality) {
//		QueryBuilder queryBuilder = null;
//			if (speciality.equalsIgnoreCase("GYNECOLOGIST")) {
//				speciality = "GYNAECOLOGIST";
//			}else if (speciality.equalsIgnoreCase("GENERAL PHYSICIAN")) {
//				speciality = "FAMILY PHYSICIAN";
//			} else if (speciality.equalsIgnoreCase("FAMILY PHYSICIAN")) {
//				speciality = "GENERAL PHYSICIAN";
//			}
//			List<ESSpecialityDocument> esSpecialityDocuments = esSpecialityRepository.findByQueryAnnotation(speciality);
//			
//			List<ESSpecialityDocument> esSpecialityDocuments2 = new LinkedList<ESSpecialityDocument>(esSpecialityDocuments);
//
////			if (speciality.equalsIgnoreCase("GENERAL PHYSICIAN") || speciality.equalsIgnoreCase("FAMILY PHYSICIAN")) {
////
////				esSpecialityDocuments = esSpecialityRepository.findByQueryAnnotation(speciality);
////				for (ESSpecialityDocument esSpecialityDocument : esSpecialityDocuments) {
////					if (esSpecialityDocument != null) {
////						esSpecialityDocuments2.add(esSpecialityDocuments2.size(), esSpecialityDocument);
////					}
////				}
////			}
//			if (esSpecialityDocuments2 != null) {
//				Collection<String> specialityIds = CollectionUtils.collect(esSpecialityDocuments2, new BeanToPropertyValueTransformer("id"));
//				if (specialityIds == null)specialityIds = CollectionUtils.EMPTY_COLLECTION;
//				queryBuilder = QueryBuilders.termsQuery("specialities", specialityIds);
//			}
//		return queryBuilder;
//	}

	private QueryBuilder createFacilityBuilder(Boolean booking, Boolean calling) {
		QueryBuilder queryBuilder = null;
			if (booking && !calling) {
				queryBuilder = QueryBuilders.termsQuery("facility", DoctorFacility.BOOK.getType().toLowerCase(),
						DoctorFacility.IBS.getType().toLowerCase());

			} else if (!booking && calling) {
				queryBuilder = QueryBuilders.matchQuery("facility", DoctorFacility.CALL.getType());
			}
		return queryBuilder;
	}

	private void createTimeFilter(BoolQueryBuilder boolQueryBuilder, int maxTime, int minTime, List<String> days, BoolQueryBuilder boolQueryBuilderForNearByDoctors) {
		if (days != null && !days.isEmpty()) {
			for (int i = 0; i < days.size(); i++) {
				days.set(i, days.get(i).toLowerCase());
			}

			if (maxTime == 0) {
				maxTime = 1439;
			}
			boolQueryBuilder
			.must(QueryBuilders.nestedQuery("workingSchedules",
					boolQuery().must(boolQuery().should(
							QueryBuilders.nestedQuery("workingSchedules.workingHours", 
									boolQuery().should(QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(minTime).lt(maxTime))
											   .should(QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(minTime).lt(maxTime))
											   .should(boolQuery().should(QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(maxTime).lt(1439))
													              .should(QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(0).lt(minTime))
													              .minimumShouldMatch(2))
											   .minimumShouldMatch(1),
									ScoreMode.None))
							                 .should(QueryBuilders.termsQuery("workingSchedules.workingDay", days)).minimumShouldMatch(2)), ScoreMode.None));
			
			if(boolQueryBuilderForNearByDoctors != null)boolQueryBuilderForNearByDoctors
			.must(QueryBuilders.nestedQuery("workingSchedules",
					boolQuery().must(boolQuery().should(
							QueryBuilders.nestedQuery("workingSchedules.workingHours", 
									boolQuery().should(QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(minTime).lt(maxTime))
											   .should(QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(minTime).lt(maxTime))
											   .should(boolQuery().should(QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(maxTime).lt(1439))
													              .should(QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(0).lt(minTime))
													              .minimumShouldMatch(2))
											   .minimumShouldMatch(1),
									ScoreMode.None))
							                 .should(QueryBuilders.termsQuery("workingSchedules.workingDay", days)).minimumShouldMatch(2)), ScoreMode.None));
		} else {

			if (maxTime == 0) {
				maxTime = 1439;
			}
			boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules.workingHours", 
					boolQuery().should(QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(minTime).lt(maxTime))
					   .should(QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(minTime).lt(maxTime))
					   .should(boolQuery().should(QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(maxTime).lt(1439))
							              .should(QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(0).lt(minTime))
							              .minimumShouldMatch(2))
					   .minimumShouldMatch(1),
			                      ScoreMode.None));
			
			if(boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors.must(QueryBuilders.nestedQuery("workingSchedules.workingHours", 
						boolQuery().should(QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(minTime).lt(maxTime))
						   .should(QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(minTime).lt(maxTime))
						   .should(boolQuery().should(QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(maxTime).lt(1439))
								              .should(QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(0).lt(minTime))
								              .minimumShouldMatch(2))
						   .minimumShouldMatch(1),
				                      ScoreMode.None));
		}
	}

	private void createExperienceFilter(BoolQueryBuilder boolQueryBuilder, int maxExperience, int minExperience, BoolQueryBuilder boolQueryBuilderForNearByDoctors) {
		if (minExperience != 0 && maxExperience != 0) {
			boolQueryBuilder.must(boolQuery().should(QueryBuilders.nestedQuery("experience",
					boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(minExperience).to(maxExperience)), ScoreMode.None))
			.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("experience", QueryBuilders.existsQuery("experience"), ScoreMode.None)))
			.minimumShouldMatch(1));
			
			if(boolQueryBuilderForNearByDoctors != null)boolQueryBuilderForNearByDoctors.must(boolQuery().should(QueryBuilders.nestedQuery("experience",
					boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(minExperience).to(maxExperience)), ScoreMode.None))
			.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("experience", QueryBuilders.existsQuery("experience"), ScoreMode.None)))
			.minimumShouldMatch(1));
		}

		else if (minExperience != 0) {
			boolQueryBuilder.must(boolQuery().should(QueryBuilders.nestedQuery("experience",
					boolQuery().must(QueryBuilders.rangeQuery("experience.experience")
							.from(minExperience)), ScoreMode.None))
			.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("experience",
					QueryBuilders.existsQuery("experience"), ScoreMode.None)))
			.minimumShouldMatch(1));
			
			if(boolQueryBuilderForNearByDoctors != null)boolQueryBuilderForNearByDoctors.must(boolQuery().should(QueryBuilders.nestedQuery("experience",
					boolQuery().must(QueryBuilders.rangeQuery("experience.experience")
							.from(minExperience)), ScoreMode.None))
			.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("experience",
					QueryBuilders.existsQuery("experience"), ScoreMode.None)))
			.minimumShouldMatch(1));
		}

		else if (maxExperience != 0) {
			boolQueryBuilder.must(boolQuery().should(
					QueryBuilders.nestedQuery("experience",
							boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(0)
									.to(maxExperience)), ScoreMode.None))
					.should(QueryBuilders.boolQuery().mustNot(
							QueryBuilders.nestedQuery("experience", QueryBuilders.existsQuery("experience"), ScoreMode.None)))
					.minimumShouldMatch(1));
			
			if(boolQueryBuilderForNearByDoctors != null)boolQueryBuilderForNearByDoctors.must(boolQuery().should(
					QueryBuilders.nestedQuery("experience",
							boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(0)
									.to(maxExperience)), ScoreMode.None))
					.should(QueryBuilders.boolQuery().mustNot(
							QueryBuilders.nestedQuery("experience", QueryBuilders.existsQuery("experience"), ScoreMode.None)))
					.minimumShouldMatch(1));
		}

	}

	private void createConsultationFeeFilter(BoolQueryBuilder boolQueryBuilder, int maxFee, int minFee, BoolQueryBuilder boolQueryBuilderForNearByDoctors) {
		if (minFee != 0 && maxFee != 0) {
			boolQueryBuilder.must(boolQuery().should(
					QueryBuilders.nestedQuery("consultationFee",
							boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(minFee)
									.to(maxFee)), ScoreMode.None))
					.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
							QueryBuilders.existsQuery("consultationFee"), ScoreMode.None)))
					.minimumShouldMatch(1));
			
			if(boolQueryBuilderForNearByDoctors != null)boolQueryBuilderForNearByDoctors.must(boolQuery().should(
					QueryBuilders.nestedQuery("consultationFee",
							boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(minFee)
									.to(maxFee)), ScoreMode.None))
					.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
							QueryBuilders.existsQuery("consultationFee"), ScoreMode.None)))
					.minimumShouldMatch(1));
		}
			

		else if (minFee != 0) {
			boolQueryBuilder.must(boolQuery()
					.should(QueryBuilders.nestedQuery("consultationFee",
							boolQuery().must(
									QueryBuilders.rangeQuery("consultationFee.amount").from(minFee)), ScoreMode.None))
			.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
							QueryBuilders.existsQuery("consultationFee"), ScoreMode.None)))
			.minimumShouldMatch(1));
			
			if(boolQueryBuilderForNearByDoctors != null)boolQueryBuilderForNearByDoctors.must(boolQuery()
					.should(QueryBuilders.nestedQuery("consultationFee",
							boolQuery().must(
									QueryBuilders.rangeQuery("consultationFee.amount").from(minFee)), ScoreMode.None))
			.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
							QueryBuilders.existsQuery("consultationFee"), ScoreMode.None)))
			.minimumShouldMatch(1));
		}else if (maxFee != 0) {
			boolQueryBuilder.must(boolQuery()
					.should(
							QueryBuilders.nestedQuery("consultationFee",
									boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(0)
											.to(maxFee)), ScoreMode.None))
					.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
									QueryBuilders.existsQuery("consultationFee"), ScoreMode.None)))
					.minimumShouldMatch(1));
			
			if(boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors.must(boolQuery()
						.should(
								QueryBuilders.nestedQuery("consultationFee",
										boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(0)
												.to(maxFee)), ScoreMode.None))
						.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
										QueryBuilders.existsQuery("consultationFee"), ScoreMode.None)))
						.minimumShouldMatch(1));
		}

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;

	}

	@Override
	public List<ResourcesCountResponse> getResourcesCountByCity(String city, List<String> types) {
		List<ResourcesCountResponse> response = null;
		try {
			if (types == null || types.isEmpty()) {
				types = new ArrayList<String>();
				types.add("DOCTOR");
				types.add("PHARMACY");
			} else {
				for (String type : types)
					type = type.toUpperCase();
			}

			if (types.contains("DOCTOR")) {
				response = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(
										Aggregation.lookup("location_cl", "locationId", "_id", "location"),
										Aggregation.unwind("location"),
										Aggregation.match(new org.springframework.data.mongodb.core.query.Criteria(
												"location.city").is(city)),
										Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
										Aggregation.unwind("doctor"), Aggregation.unwind("doctor.specialities"),
										new CustomAggregationOperation(new Document("$group",
												new BasicDBObject("_id", "$doctor.specialities").append("count",
														new BasicDBObject("$sum", 1)))),
										Aggregation.lookup("speciality_cl", "_id", "_id", "speciality"),
										Aggregation.unwind("speciality"),
										new CustomAggregationOperation(new Document("$project",
												new BasicDBObject("fields.key", "$speciality.speciality")
														.append("fields.value", "$count")
														.append("resourceType",
																new BasicDBObject("$concat", Arrays.asList("DOCTOR")))
														.append("totalCount", "$count"))),
										new CustomAggregationOperation(new Document("$sort",
												new BasicDBObject("fields.value", -1))),
										new CustomAggregationOperation(new Document("$group",
												new BasicDBObject("_id", "$resourceType")
														.append("resourceType",
																new BasicDBObject("$first", "$resourceType"))
														.append("totalCount", new BasicDBObject("$sum", "$totalCount"))
														.append("fields", new BasicDBObject("$addToSet", "$fields"))))),
								DoctorClinicProfileCollection.class, ResourcesCountResponse.class)
						.getMappedResults();

//				ResourcesCountResponse doctors = new ResourcesCountResponse();
//				doctors.setResourceType("DOCTORS");
//				doctors.setFields(fields);
			}

			if (types.contains("PHARMACY")) {

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error While Resources Count" + e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SearchLandmarkLocalityResponse> getLandmarksAndLocalitiesByCity(String city, int page, int size, String searchTerm) {
		List<SearchLandmarkLocalityResponse> response = new ArrayList<SearchLandmarkLocalityResponse>();
		try {
			List<ESCityDocument> cityDocument = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder().withQuery(
					new BoolQueryBuilder().must(QueryBuilders.matchPhrasePrefixQuery("city", searchTerm)).must(QueryBuilders.matchQuery("isActivated", true))).build(), ESCityDocument.class);
			
			Map<String, String> cityMap = new HashMap<String, String>();
			SearchLandmarkLocalityResponse searchLandmarkLocalityResponse = null;
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			if (cityDocument != null && !cityDocument.isEmpty()) {
				List<String> cityIds = (List<String>) CollectionUtils.collect(cityDocument, new BeanToPropertyValueTransformer("id"));
				boolQueryBuilder.must(QueryBuilders.termsQuery("cityId", cityIds));	
				
				for(ESCityDocument esCityDocument : cityDocument) {
					searchLandmarkLocalityResponse = new SearchLandmarkLocalityResponse();
					BeanUtil.map(esCityDocument, searchLandmarkLocalityResponse);
					searchLandmarkLocalityResponse.setName(esCityDocument.getCity());
					searchLandmarkLocalityResponse.setResponseType("CITY");
					response.add(searchLandmarkLocalityResponse);
					cityMap.put(esCityDocument.getId(), esCityDocument.getCity());
				}
			}
				if(!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					boolQueryBuilder.should(QueryBuilders.matchPhrasePrefixQuery("locality", searchTerm)).should(QueryBuilders.matchPhrasePrefixQuery("landmark", searchTerm)).minimumShouldMatch(1);
				}
				
//				if() {
//					size = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), ESLandmarkLocalityDocument.class);	
//				}
				size = size - response.size();
				SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(PageRequest.of(page, size)).build();
				
				List<ESLandmarkLocalityDocument> esLandmarkLocalityDocuments = elasticsearchTemplate.queryForList(searchQuery, ESLandmarkLocalityDocument.class);
				if(esLandmarkLocalityDocuments != null) {
					response = new ArrayList<SearchLandmarkLocalityResponse>();
					
					for(ESLandmarkLocalityDocument document : esLandmarkLocalityDocuments) {
						searchLandmarkLocalityResponse = new SearchLandmarkLocalityResponse();
						BeanUtil.map(document, searchLandmarkLocalityResponse);
						searchLandmarkLocalityResponse.setCity(cityMap.get(document.getCityId()));
						if(!DPDoctorUtils.anyStringEmpty(searchLandmarkLocalityResponse.getCity())) {
							ESCityDocument esCityDocument = esCityRepository.findById(document.getCityId()).orElse(null);
							cityMap.put(esCityDocument.getId(), esCityDocument.getCity());
							searchLandmarkLocalityResponse.setCity(esCityDocument.getCity());
						}
						
						if(!DPDoctorUtils.anyStringEmpty(document.getLocality()))searchLandmarkLocalityResponse.setName(document.getLocality());
						else if(!DPDoctorUtils.anyStringEmpty(document.getLandmark()))searchLandmarkLocalityResponse.setName(document.getLandmark());
						
						String slugUrl = searchLandmarkLocalityResponse.getName().toLowerCase().trim().replaceAll("[^a-zA-Z0-9-]", "-");
						
						slugUrl = slugUrl.replaceAll("-*-","-");	
						
						searchLandmarkLocalityResponse.setSlugUrl(slugUrl);
						response.add(searchLandmarkLocalityResponse);
					}
				}
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,"Error While searching landmak and localities : " + e.getMessage());
		}
		return response;
	}

	@Override
	public DoctorProfileBySlugUrlResponse getDoctorProfileBySlugUrl(String userUId, String slugURL) {
		DoctorProfileBySlugUrlResponse doctorProfile = null;
		List<DoctorClinicProfileBySlugUrlResponse> clinicProfile = new ArrayList<DoctorClinicProfileBySlugUrlResponse>();
		try {
			
			List<ESDoctorDocument> doctorDocuments = esDoctorRepository.findbySlugUrl(slugURL, true);
			if(doctorDocuments == null || doctorDocuments.isEmpty()) {
				doctorDocuments = esDoctorRepository.findbyUserUId(userUId, true);;
			}
			
			if(doctorDocuments == null || doctorDocuments.isEmpty()) return null;
				
			for (ESDoctorDocument doctorDocument : doctorDocuments) {
				
				if(doctorProfile == null) {
					doctorProfile = new DoctorProfileBySlugUrlResponse();
					BeanUtil.map(doctorDocument, doctorProfile);
					doctorProfile.setDoctorId(doctorDocument.getUserId());
					doctorProfile.setSpecialities(doctorDocument.getSpecialitiesValue());
					doctorProfile.setParentSpecialities(doctorDocument.getParentSpecialities());
					doctorProfile.setServices(doctorDocument.getServicesValue());
				}
							
				DoctorClinicProfileBySlugUrlResponse doctorClinic = new DoctorClinicProfileBySlugUrlResponse();
				BeanUtil.map(doctorDocument, doctorClinic);
				String address = (!DPDoctorUtils.anyStringEmpty(doctorDocument.getStreetAddress())
						? doctorDocument.getStreetAddress() + ", "
						: "")
						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getLandmarkDetails())
								? doctorDocument.getLandmarkDetails() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getLocality())
								? doctorDocument.getLocality() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getCity())
								? doctorDocument.getCity() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getState())
								? doctorDocument.getState() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getCountry())
								? doctorDocument.getCountry() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getPostalCode())
								? doctorDocument.getPostalCode()
								: "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}

				doctorClinic.setClinicAddress(address);
				doctorClinic.setDoctorId(doctorDocument.getUserId());
				org.springframework.data.mongodb.core.query.Criteria criteria = new org.springframework.data.mongodb.core.query.Criteria("doctorId")
						.is(new ObjectId(doctorDocument.getUserId()))
						.and("locationId").is(new ObjectId(doctorDocument.getLocationId()))
						.and("hospitalId").is(new ObjectId(doctorDocument.getHospitalId()));
				
				List<Feedback> feedbacks = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteria.and("isVisible").is(true)),
										Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
//										Aggregation.skip((0) * 5),
										Aggregation.limit(5)),
								FeedbackCollection.class, Feedback.class)
						.getMappedResults();
				doctorClinic.setFeedbacks(feedbacks);
				doctorClinic.setNoOfFeedbacks((int) mongoTemplate.count(new Query(criteria), FeedbackCollection.class));
				
				clinicProfile.add(doctorClinic);	
			}	
			doctorProfile.setClinicProfile(clinicProfile);
			
			} catch (BusinessException be) {
				throw be;
			} catch (Exception e) {
				e.printStackTrace();
				throw new BusinessException(ServiceError.Unknown, "Error Getting Doctor Profile by userUId");
			}
			return doctorProfile;
	}
}
