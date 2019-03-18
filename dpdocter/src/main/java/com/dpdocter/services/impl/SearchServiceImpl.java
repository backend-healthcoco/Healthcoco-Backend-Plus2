package com.dpdocter.services.impl;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.elasticsearch.beans.ESDoctorWEbSearch;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESServicesDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.repository.ESServicesRepository;
import com.dpdocter.elasticsearch.repository.ESSpecialityRepository;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.response.ResourcesCountResponse;
import com.dpdocter.response.SearchDoctorResponse;
import com.dpdocter.services.SearchService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private ESSpecialityRepository esSpecialityRepository;

	@Autowired
	private ESServicesRepository esServicesRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Value(value = "${image.path}")
	private String imagePath;

	@Override
	public SearchDoctorResponse searchDoctors(int page, int size, String city, String location, String latitude,
			String longitude, String speciality, String symptom, Boolean booking, Boolean calling, int minFee,
			int maxFee, int minTime, int maxTime, List<String> days, String gender, int minExperience,
			int maxExperience, String service, String locality, Boolean otherArea, String expertIn) {
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
					.should(QueryBuilders.termsQuery("parentSpecialities", "GENERAL PHYSICIAN".toLowerCase(), "FAMILY PHYSICIAN".toLowerCase())).minimumNumberShouldMatch(1);
				}else {
					specialityQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("specialitiesValue", speciality.toLowerCase()))
							.should(QueryBuilders.termsQuery("parentSpecialities", speciality.toLowerCase())).minimumNumberShouldMatch(1);
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
								.withPageable(new PageRequest(page, size)).build();
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
											.type(MatchQueryBuilder.Type.PHRASE_PREFIX)))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
									.withPageable(new PageRequest(page, size)).build();
						else
							searchQuery = new NativeSearchQueryBuilder()
									.withQuery(boolQueryBuilder.must(QueryBuilders
											.multiMatchQuery(locality, "landmarkDetails", "streetAddress", "locality")
											.type(MatchQueryBuilder.Type.PHRASE_PREFIX)))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC)).build();

						esDoctorDocuments = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
					}

					if (esDoctorDocuments == null || esDoctorDocuments.isEmpty()) {
						if (size > 0)
							searchQuery = new NativeSearchQueryBuilder()
									.withQuery(boolQueryBuilderForNearByDoctors.mustNot(QueryBuilders
											.multiMatchQuery(locality, "landmarkDetails", "streetAddress", "locality")
											.type(MatchQueryBuilder.Type.PHRASE_PREFIX)))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
									.withPageable(new PageRequest(page, size)).build();
						else
							searchQuery = new NativeSearchQueryBuilder()
									.withQuery(boolQueryBuilderForNearByDoctors.mustNot(QueryBuilders
											.multiMatchQuery(locality, "landmarkDetails", "streetAddress", "locality")
											.type(MatchQueryBuilder.Type.PHRASE_PREFIX)))
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
																	.type(MatchQueryBuilder.Type.PHRASE_PREFIX)))
											.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
											.withPageable(new PageRequest(page, size)).build();
								nearByDoctors = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
							}
						} else {
							searchQuery = new NativeSearchQueryBuilder()
									.withQuery(boolQueryBuilderForNearByDoctors.mustNot(QueryBuilders
											.multiMatchQuery(locality, "landmarkDetails", "streetAddress", "locality")
											.type(MatchQueryBuilder.Type.PHRASE_PREFIX)))
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
				}
				if (DPDoctorUtils.anyStringEmpty(city)) {
					city = "Nagpur";
				}
				response.setMetaData(response.getMetaData() + StringUtils.capitalize(city));
				response.setCity(StringUtils.capitalize(city));
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
					
					String unformattedService = "Doctor for "+StringUtils.capitalize(service)+" treatment";
					response.setUnformattedService(unformattedService);
					
					service = "doctor-for-"+service.toLowerCase().replaceAll(" ", "-")+"-treatment";
					response.setService(service);
					
					
					response.setMetaData(unformattedService + " in ");
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

	@SuppressWarnings("unchecked")
	private List<ESDoctorWEbSearch> formatDoctorData(List<ESDoctorDocument> esDoctorDocuments, String latitude,
			String longitude) {
		List<ESDoctorWEbSearch> response = new ArrayList<ESDoctorWEbSearch>();

		if (esDoctorDocuments != null) {
			for (ESDoctorDocument doctorDocument : esDoctorDocuments) {
				ESDoctorWEbSearch doctorWEbSearch = new ESDoctorWEbSearch();
				BeanUtil.map(doctorDocument, doctorWEbSearch);
//				if (doctorDocument.getSpecialities() != null) {
//					if (doctorDocument.getSpecialities() != null) {
//						Iterable<ESSpecialityDocument> specialities = esSpecialityRepository.findAll(doctorDocument.getSpecialities());
//						if(specialities != null) {
//							doctorWEbSearch.setSpecialities((List<String>)CollectionUtils.collect(specialities.iterator(), new BeanToPropertyValueTransformer("superSpeciality")));
//							doctorWEbSearch.setParentSpecialities((List<String>)CollectionUtils.collect(specialities.iterator(), new BeanToPropertyValueTransformer("speciality")));
//						}
//					}
//				}
//
//				if (doctorDocument.getServices() != null) {
//					Iterable<ESServicesDocument> services = esServicesRepository.findAll(doctorDocument.getServices());
//					if(services != null) {
//						doctorWEbSearch.setServices((List<String>)CollectionUtils.collect(services.iterator(), new BeanToPropertyValueTransformer("service")));
//					}					
//				}
				
				doctorWEbSearch.setSpecialities(doctorDocument.getSpecialitiesValue());
				doctorWEbSearch.setServices(doctorDocument.getServicesValue());
				if (doctorWEbSearch.getThumbnailUrl() != null)
					doctorWEbSearch.setThumbnailUrl(getFinalImageURL(doctorWEbSearch.getThumbnailUrl()));

//				String address = (!DPDoctorUtils.anyStringEmpty(doctorDocument.getStreetAddress())
//						? doctorDocument.getStreetAddress() + ", " : "")
//						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getLandmarkDetails())
//								? doctorDocument.getLandmarkDetails() + ", " : "")
//						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getLocality())
//								? doctorDocument.getLocality() + ", " : "")
//						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getCity()) ? doctorDocument.getCity() + ", "
//								: "")
//						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getState())
//								? doctorDocument.getState() + ", " : "")
//						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getCountry())
//								? doctorDocument.getCountry() + ", " : "")
//						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getPostalCode())
//								? doctorDocument.getPostalCode() : "");
//
//				if (address.length()>1 && address.charAt(address.length() - 2) == ',') {
//					address = address.substring(0, address.length() - 2);
//				}
//				doctorWEbSearch.setClinicAddress(address);
				response.add(doctorWEbSearch);
			}
		}
		return response;
	}
	
	@SuppressWarnings("unchecked")
	private QueryBuilder createServiceFilter(String service) {
		QueryBuilder queryBuilder = null;
			List<ESServicesDocument> esServicesDocuments = esServicesRepository.findByQueryAnnotation(service);
			
			if (esServicesDocuments != null) {
				Collection<String> serviceIds = CollectionUtils.collect(esServicesDocuments,
						new BeanToPropertyValueTransformer("id"));
				if (serviceIds == null)
					serviceIds = CollectionUtils.EMPTY_COLLECTION;
				queryBuilder = QueryBuilders.termsQuery("services", serviceIds);
			}
		return queryBuilder;
	}

	@SuppressWarnings("unchecked")
	private QueryBuilder createSpecialityFilter(String speciality) {
		QueryBuilder queryBuilder = null;
			if (speciality.equalsIgnoreCase("GYNECOLOGIST")) {
				speciality = "GYNAECOLOGIST";
			}else if (speciality.equalsIgnoreCase("GENERAL PHYSICIAN")) {
				speciality = "FAMILY PHYSICIAN";
			} else if (speciality.equalsIgnoreCase("FAMILY PHYSICIAN")) {
				speciality = "GENERAL PHYSICIAN";
			}
			List<ESSpecialityDocument> esSpecialityDocuments = esSpecialityRepository.findByQueryAnnotation(speciality);
			
			List<ESSpecialityDocument> esSpecialityDocuments2 = new LinkedList<ESSpecialityDocument>(esSpecialityDocuments);

//			if (speciality.equalsIgnoreCase("GENERAL PHYSICIAN") || speciality.equalsIgnoreCase("FAMILY PHYSICIAN")) {
//
//				esSpecialityDocuments = esSpecialityRepository.findByQueryAnnotation(speciality);
//				for (ESSpecialityDocument esSpecialityDocument : esSpecialityDocuments) {
//					if (esSpecialityDocument != null) {
//						esSpecialityDocuments2.add(esSpecialityDocuments2.size(), esSpecialityDocument);
//					}
//				}
//			}
			if (esSpecialityDocuments2 != null) {
				Collection<String> specialityIds = CollectionUtils.collect(esSpecialityDocuments2, new BeanToPropertyValueTransformer("id"));
				if (specialityIds == null)specialityIds = CollectionUtils.EMPTY_COLLECTION;
				queryBuilder = QueryBuilders.termsQuery("specialities", specialityIds);
			}
		return queryBuilder;
	}

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

	@SuppressWarnings("deprecation")
	private void createTimeFilter(BoolQueryBuilder boolQueryBuilder, int maxTime, int minTime, List<String> days,
			BoolQueryBuilder boolQueryBuilderForNearByDoctors) {
		if (days != null && !days.isEmpty()) {
			for (int i = 0; i < days.size(); i++) {
				days.set(i, days.get(i).toLowerCase());
			}

			if (maxTime == 0) {
				maxTime = 1439;
			}
			boolQueryBuilder
					.must(QueryBuilders.nestedQuery("workingSchedules",
							boolQuery().must(QueryBuilders.andQuery(
									nestedQuery("workingSchedules.workingHours", QueryBuilders.orQuery(

											QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(minTime)
													.lt(maxTime),

											QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime")
													.gt(minTime).lt(maxTime),
											QueryBuilders.andQuery(
													QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime")
															.gt(maxTime).lt(1439),
													QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime")
															.gt(0).lt(minTime)))),
									QueryBuilders.termsQuery("workingSchedules.workingDay", days)))));

			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilder
						.must(QueryBuilders.nestedQuery("workingSchedules",
								boolQuery().must(QueryBuilders.andQuery(
										nestedQuery("workingSchedules.workingHours", QueryBuilders.orQuery(

												QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime")
														.gt(minTime).lt(maxTime),

												QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime")
														.gt(minTime).lt(maxTime),
												QueryBuilders.andQuery(
														QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime")
																.gt(maxTime).lt(1439),
														QueryBuilders
																.rangeQuery("workingSchedules.workingHours.fromTime")
																.gt(0).lt(minTime)))),
										QueryBuilders.termsQuery("workingSchedules.workingDay", days)))));

		} else {

			if (maxTime == 0) {
				maxTime = 1439;
			}
			boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules",
					boolQuery().must(nestedQuery("workingSchedules.workingHours", QueryBuilders.orQuery(

							QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(minTime).lt(maxTime),

							QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(minTime).lt(maxTime),
							QueryBuilders.andQuery(
									QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(maxTime)
											.lt(1439),
									QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(0)
											.lt(minTime)))))));

			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors.must(QueryBuilders.nestedQuery("workingSchedules",
						boolQuery().must(nestedQuery("workingSchedules.workingHours", QueryBuilders.orQuery(

								QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(minTime)
										.lt(maxTime),

								QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(minTime)
										.lt(maxTime),
								QueryBuilders.andQuery(
										QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(maxTime)
												.lt(1439),
										QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(0)
												.lt(minTime)))))));
		}
	}

	@SuppressWarnings("deprecation")
	private void createExperienceFilter(BoolQueryBuilder boolQueryBuilder, int maxExperience, int minExperience,
			BoolQueryBuilder boolQueryBuilderForNearByDoctors) {
		if (minExperience != 0 && maxExperience != 0) {
			boolQueryBuilder.must(QueryBuilders.orQuery(
					QueryBuilders.nestedQuery("experience",
							boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(minExperience)
									.to(maxExperience))),
					QueryBuilders.boolQuery().mustNot(
							QueryBuilders.nestedQuery("experience", QueryBuilders.existsQuery("experience")))));

			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors.must(QueryBuilders.orQuery(
						QueryBuilders.nestedQuery("experience",
								boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(minExperience)
										.to(maxExperience))),
						QueryBuilders.boolQuery().mustNot(
								QueryBuilders.nestedQuery("experience", QueryBuilders.existsQuery("experience")))));
		}

		else if (minExperience != 0) {
			boolQueryBuilder
					.must(QueryBuilders
							.orQuery(
									QueryBuilders.nestedQuery("experience",
											boolQuery().must(QueryBuilders.rangeQuery("experience.experience")
													.from(minExperience))),
									QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("experience",
											QueryBuilders.existsQuery("experience")))));

			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors
						.must(QueryBuilders
								.orQuery(
										QueryBuilders.nestedQuery("experience",
												boolQuery().must(QueryBuilders.rangeQuery("experience.experience")
														.from(minExperience))),
										QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("experience",
												QueryBuilders.existsQuery("experience")))));
		}

		else if (maxExperience != 0) {
			boolQueryBuilder
					.must(QueryBuilders.orQuery(
							QueryBuilders.nestedQuery("experience",
									boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(0)
											.to(maxExperience))),
							QueryBuilders.boolQuery().mustNot(
									QueryBuilders.nestedQuery("experience", QueryBuilders.existsQuery("experience")))));

			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors
						.must(QueryBuilders.orQuery(
								QueryBuilders.nestedQuery("experience",
										boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(0)
												.to(maxExperience))),
								QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("experience",
										QueryBuilders.existsQuery("experience")))));
		}

	}

	@SuppressWarnings("deprecation")
	private void createConsultationFeeFilter(BoolQueryBuilder boolQueryBuilder, int maxFee, int minFee,
			BoolQueryBuilder boolQueryBuilderForNearByDoctors) {
		if (minFee != 0 && maxFee != 0) {
			boolQueryBuilder
					.must(QueryBuilders.orQuery(
							nestedQuery("consultationFee",
									boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(minFee)
											.to(maxFee))),
							QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
									QueryBuilders.existsQuery("consultationFee")))));
			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors
						.must(QueryBuilders.orQuery(
								nestedQuery("consultationFee",
										boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(minFee)
												.to(maxFee))),
								QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
										QueryBuilders.existsQuery("consultationFee")))));
		}

		else if (minFee != 0) {
			boolQueryBuilder
					.must(QueryBuilders
							.orQuery(
									QueryBuilders.nestedQuery("consultationFee",
											boolQuery().must(
													QueryBuilders.rangeQuery("consultationFee.amount").from(minFee))),
									QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
											QueryBuilders.existsQuery("consultationFee")))));

			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors
						.must(QueryBuilders
								.orQuery(
										QueryBuilders.nestedQuery("consultationFee",
												boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount")
														.from(minFee))),
										QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
												QueryBuilders.existsQuery("consultationFee")))));
		} else if (maxFee != 0) {
			boolQueryBuilder
					.must(QueryBuilders
							.orQuery(
									QueryBuilders.nestedQuery("consultationFee",
											boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(0)
													.to(maxFee))),
									QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
											QueryBuilders.existsQuery("consultationFee")))));

			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors
						.must(QueryBuilders
								.orQuery(
										QueryBuilders.nestedQuery("consultationFee",
												boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount")
														.from(0).to(maxFee))),
										QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
												QueryBuilders.existsQuery("consultationFee")))));
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
										new CustomAggregationOperation(new BasicDBObject("$group",
												new BasicDBObject("_id", "$doctor.specialities").append("count",
														new BasicDBObject("$sum", 1)))),
										Aggregation.lookup("speciality_cl", "_id", "_id", "speciality"),
										Aggregation.unwind("speciality"),
										new CustomAggregationOperation(new BasicDBObject("$project",
												new BasicDBObject("fields.key", "$speciality.speciality")
														.append("fields.value", "$count")
														.append("resourceType",
																new BasicDBObject("$concat", Arrays.asList("DOCTOR")))
														.append("totalCount", "$count"))),
										new CustomAggregationOperation(new BasicDBObject("$sort",
												new BasicDBObject("fields.value", -1))),
										new CustomAggregationOperation(new BasicDBObject("$group",
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
}