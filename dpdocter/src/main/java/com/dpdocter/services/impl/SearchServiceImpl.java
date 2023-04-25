package com.dpdocter.services.impl;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortMode;
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
import com.dpdocter.beans.DoctorRegistrationDetail;
import com.dpdocter.beans.Feedback;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.FeedbackCollection;
import com.dpdocter.elasticsearch.beans.ESDoctorWEbSearch;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;
import com.dpdocter.elasticsearch.document.ESSymptomDiseaseConditionDocument;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESLandmarkLocalityRepository;
import com.dpdocter.elasticsearch.repository.ESSymptomDiseaseConditionRepository;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.response.DoctorClinicProfileBySlugUrlResponse;
import com.dpdocter.response.DoctorProfileBySlugUrlResponse;
import com.dpdocter.response.ResourcesCountResponse;
import com.dpdocter.response.SearchDoctorResponse;
import com.dpdocter.services.SearchService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private ESLandmarkLocalityRepository esLandmarkLocalityRepository;

	@Autowired
	private ESDoctorRepository esDoctorRepository;

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
			int maxExperience, String service, String locality, Boolean otherArea, String expertIn,
			String symptomDiseaseCondition) {
		List<ESDoctorDocument> esDoctorDocuments = null;
		SearchDoctorResponse response = null;
		String cityId = null;
		Double latitudeDouble = null;
		Double longitudeDouble = null;
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
				List<ESCityDocument> cityDocs = elasticsearchTemplate.queryForList(
						new NativeSearchQueryBuilder().withQuery(
								new BoolQueryBuilder().must(QueryBuilders.matchPhrasePrefixQuery("city", city))
										.must(QueryBuilders.matchQuery("isActivated", true)))
								.build(),
						ESCityDocument.class);

				if (cityDocs == null || cityDocs.isEmpty()) {
					throw new BusinessException(ServiceError.InvalidInput, "Invalid City");
				}
				ESCityDocument cityDoc = cityDocs.get(0);
				cityId = cityDoc.getId();
				latitudeDouble = cityDoc.getLatitude();
				longitudeDouble = cityDoc.getLongitude();

				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("city", city));
				boolQueryBuilderForNearByDoctors.must(QueryBuilders.matchPhrasePrefixQuery("city", city));
			}

			if (!(DPDoctorUtils.allStringsEmpty(expertIn) || expertIn.equalsIgnoreCase("undefined")
					|| expertIn.equalsIgnoreCase("DOCTOR"))) {

				if (expertIn.startsWith("doctors-for-")) {
					service = expertIn.replace("doctors-for-", "").replaceAll("-", " ");
				} else if (expertIn.startsWith("treatments-for-")) {
					symptomDiseaseCondition = expertIn.replace("treatments-for-", "").replaceAll("-", " ");
				} else {
					speciality = expertIn.replaceAll("-", " ");
				}
			}

			if (DPDoctorUtils.allStringsEmpty(speciality) || speciality.equalsIgnoreCase("undefined")
					|| speciality.equalsIgnoreCase("DOCTOR")) {
				speciality = null;
			} else {
				speciality = speciality.replaceAll("-", " ");
				QueryBuilder specialityQueryBuilder = null;

				if (speciality.equalsIgnoreCase("GENERAL PHYSICIAN")
						|| speciality.equalsIgnoreCase("FAMILY PHYSICIAN")) {
					specialityQueryBuilder = QueryBuilders.boolQuery()
							.should(QueryBuilders.matchPhrasePrefixQuery("specialitiesValue", "GENERAL PHYSICIAN*"))
							.should(QueryBuilders.matchPhrasePrefixQuery("specialitiesValue", "FAMILY PHYSICIAN*"))
							.should(QueryBuilders.matchPhrasePrefixQuery("parentSpecialities", "GENERAL PHYSICIAN*"))
							.should(QueryBuilders.matchPhrasePrefixQuery("parentSpecialities", "FAMILY PHYSICIAN*"))
							.minimumShouldMatch(1);
				} else {
					specialityQueryBuilder = QueryBuilders.boolQuery()
							.should(QueryBuilders.matchPhrasePrefixQuery("specialitiesValue", speciality + "*"))
							.should(QueryBuilders.matchPhrasePrefixQuery("parentSpecialities", speciality + "*"))
							.minimumShouldMatch(1);
				}

				// createSpecialityFilter(speciality);
				if (specialityQueryBuilder != null) {
					boolQueryBuilder.must(specialityQueryBuilder);
					boolQueryBuilderForNearByDoctors.must(specialityQueryBuilder);
				}
			}

			if (DPDoctorUtils.allStringsEmpty(service) || service.equalsIgnoreCase("undefined")
					|| service.equalsIgnoreCase("DOCTOR")) {
				service = null;
			} else {
				service = service.replace("doctors-for-", "").replaceAll("-", " ");
				QueryBuilder serviceQueryBuilder = QueryBuilders.matchPhrasePrefixQuery("servicesValue", service + "*");

				// createServiceFilter(service);
				if (serviceQueryBuilder != null) {
					boolQueryBuilder.must(serviceQueryBuilder);
					boolQueryBuilderForNearByDoctors.must(serviceQueryBuilder);
				}
			}

			if (DPDoctorUtils.allStringsEmpty(symptomDiseaseCondition)
					|| symptomDiseaseCondition.equalsIgnoreCase("undefined")
					|| symptomDiseaseCondition.equalsIgnoreCase("DOCTOR")) {
				symptomDiseaseCondition = null;
			} else {
				symptomDiseaseCondition = symptomDiseaseCondition.replace("treatments-for-", "").replaceAll("-", " ");
				List<ESSymptomDiseaseConditionDocument> documents = esSymptomDiseaseConditionRepository
						.findByQueryAnnotation(symptomDiseaseCondition);
				if (documents != null && !documents.isEmpty()) {
					List<String> specialities = new ArrayList<>();
					for (ESSymptomDiseaseConditionDocument document : documents)
						if (document.getSpecialityIds() != null && !document.getSpecialityIds().isEmpty())
							specialities.addAll(document.getSpecialityIds());

					if (specialities != null && !specialities.isEmpty()) {
						QueryBuilder symptomDiseaseConditionBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("specialities", specialities));
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

			if (!(maxFee == 0 && minFee == 0))
				createConsultationFeeFilter(boolQueryBuilder, maxFee, minFee, boolQueryBuilderForNearByDoctors);

			if (!(maxExperience == 0 && minExperience == 0))
				createExperienceFilter(boolQueryBuilder, maxExperience, minExperience,
						boolQueryBuilderForNearByDoctors);

			if (!DPDoctorUtils.anyStringEmpty(gender)) {
				boolQueryBuilder.must(QueryBuilders.matchQuery("gender", gender));
				boolQueryBuilderForNearByDoctors.must(QueryBuilders.matchQuery("gender", gender));
			}

			if (!((days == null || days.isEmpty()) && maxTime == 0 && minTime == 0))
				createTimeFilter(boolQueryBuilder, maxTime, minTime, days, boolQueryBuilderForNearByDoctors);

			Integer count = (int) elasticsearchTemplate
					.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), ESDoctorDocument.class);

			if (count > 0) {
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

					List<ESLandmarkLocalityDocument> esLandmarkLocalityDocument = null;
					if (!DPDoctorUtils.anyStringEmpty(cityId))
						esLandmarkLocalityDocument = elasticsearchTemplate.queryForList(
								new CriteriaQuery(new Criteria("cityId").is(cityId)
										.and(new Criteria("landmark").is(locality)
												.or(new Criteria("locality").is(locality)))),
								ESLandmarkLocalityDocument.class);
					else
						esLandmarkLocalityDocument = esLandmarkLocalityRepository.findByQueryAnnotation(locality);

					if (esLandmarkLocalityDocument != null) {
						latitudeDouble = esLandmarkLocalityDocument.get(0).getLatitude();
						longitudeDouble = esLandmarkLocalityDocument.get(0).getLongitude();
					}

					if (!otherArea) {

						if (size > 0)
							searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withSort(SortBuilders.geoDistanceSort("geoPoint", latitudeDouble, longitudeDouble)
											.order(SortOrder.ASC).unit(DistanceUnit.KILOMETERS).sortMode(SortMode.MIN))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
									.withPageable(PageRequest.of(page, size)).build();
						else
							searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withSort(SortBuilders.geoDistanceSort("geoPoint", latitudeDouble, longitudeDouble)
											.order(SortOrder.ASC).unit(DistanceUnit.KILOMETERS).sortMode(SortMode.MIN))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC)).build();

						esDoctorDocuments = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
					}
				}
			}

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
				formatDoctorData(esDoctorDocuments, latitude, longitude, response, locality);
			}

			if (!DPDoctorUtils.anyStringEmpty(speciality) && !speciality.equalsIgnoreCase("NAGPUR")) {
				String unformattedSpeciality = StringUtils.capitalize(speciality);
				response.setUnformattedSpeciality(unformattedSpeciality);

				speciality = speciality.toLowerCase().replaceAll(" ", "-");
				response.setSpeciality(speciality);
				response.setMetaData(unformattedSpeciality + "s in ");

			} else if (!DPDoctorUtils.anyStringEmpty(service) && !service.equalsIgnoreCase("NAGPUR")) {

				String unformattedService = "Doctors for " + StringUtils.capitalize(service);

				response.setUnformattedService(unformattedService);

				service = "doctors-for-" + service.toLowerCase().replaceAll(" ", "-");
				response.setService(service);

				response.setMetaData(unformattedService + " in ");
			} else if (!DPDoctorUtils.anyStringEmpty(symptomDiseaseCondition)
					&& !symptomDiseaseCondition.equalsIgnoreCase("NAGPUR")) {

				String unformattedSymptomDiseaseCondition = "Treatments for "
						+ StringUtils.capitalize(symptomDiseaseCondition);

				response.setUnformattedSymptomDiseaseCondition(unformattedSymptomDiseaseCondition);

				symptomDiseaseCondition = "treatments-for-"
						+ unformattedSymptomDiseaseCondition.toLowerCase().replaceAll(" ", "-");
				response.setSymptomDiseaseCondition(symptomDiseaseCondition);

				response.setMetaData(unformattedSymptomDiseaseCondition + " in ");
			} else {
				response.setMetaData("Doctors in ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error While searching Doctor From ES : " + e.getMessage());
		}
		return response;
	}

	private void formatDoctorData(List<ESDoctorDocument> esDoctorDocuments, String latitude, String longitude,
			SearchDoctorResponse response, String locality) {
		List<ESDoctorWEbSearch> doctors = new ArrayList<ESDoctorWEbSearch>();
		List<ESDoctorWEbSearch> nearByDoctors = new ArrayList<ESDoctorWEbSearch>();

		if (esDoctorDocuments != null) {
			if (DPDoctorUtils.allStringsEmpty(locality) || locality.equalsIgnoreCase("undefined")) {
				for (ESDoctorDocument doctorDocument : esDoctorDocuments) {
					ESDoctorWEbSearch doctorWEbSearch = new ESDoctorWEbSearch();
					BeanUtil.map(doctorDocument, doctorWEbSearch);

					doctorWEbSearch.setSpecialities(doctorDocument.getSpecialitiesValue());
					if (doctorDocument.getServicesValue() != null && !doctorDocument.getServicesValue().isEmpty()
							&& doctorDocument.getServicesValue().size() > 3) {
						doctorWEbSearch.setServices(doctorDocument.getServicesValue().subList(0, 3));
					} else
						doctorWEbSearch.setServices(doctorDocument.getServicesValue());
					if (doctorWEbSearch.getThumbnailUrl() != null)
						doctorWEbSearch.setThumbnailUrl(getFinalImageURL(doctorWEbSearch.getThumbnailUrl()));

					doctors.add(doctorWEbSearch);
					response.setDoctors(doctors);
				}
			} else {
				for (ESDoctorDocument doctorDocument : esDoctorDocuments) {
					ESDoctorWEbSearch doctorWEbSearch = new ESDoctorWEbSearch();
					BeanUtil.map(doctorDocument, doctorWEbSearch);

					doctorWEbSearch.setSpecialities(doctorDocument.getSpecialitiesValue());
					if (doctorDocument.getServicesValue() != null && !doctorDocument.getServicesValue().isEmpty()
							&& doctorDocument.getServicesValue().size() > 3) {
						doctorWEbSearch.setServices(doctorDocument.getServicesValue().subList(0, 3));
					} else
						doctorWEbSearch.setServices(doctorDocument.getServicesValue());
					if (doctorWEbSearch.getThumbnailUrl() != null)
						doctorWEbSearch.setThumbnailUrl(getFinalImageURL(doctorWEbSearch.getThumbnailUrl()));

					if ((doctorDocument.getLocality() != null && org.apache.commons.lang3.StringUtils
							.containsIgnoreCase(doctorDocument.getLocality(), locality))
							|| (doctorDocument.getLandmarkDetails() != null && org.apache.commons.lang3.StringUtils
									.containsIgnoreCase(doctorDocument.getLandmarkDetails(), locality))
							|| (doctorDocument.getStreetAddress() != null && org.apache.commons.lang3.StringUtils
									.containsIgnoreCase(doctorDocument.getStreetAddress(), locality))) {
						doctors.add(doctorWEbSearch);
					} else {
						nearByDoctors.add(doctorWEbSearch);
					}

					response.setDoctors(doctors);
					response.setNearByDoctors(nearByDoctors);
				}
			}
		}
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
					.must(QueryBuilders
							.nestedQuery("workingSchedules",
									boolQuery().must(boolQuery()
											.must(QueryBuilders.nestedQuery("workingSchedules.workingHours", boolQuery()

													.should(QueryBuilders
															.rangeQuery("workingSchedules.workingHours.toTime")
															.gt(minTime).lt(maxTime))
													.should(QueryBuilders
															.rangeQuery("workingSchedules.workingHours.fromTime")
															.gt(minTime).lt(maxTime))
													.should(boolQuery()
															.must(QueryBuilders
																	.rangeQuery("workingSchedules.workingHours.toTime")
																	.gt(maxTime).lt(1439))
															.must(QueryBuilders
																	.rangeQuery(
																			"workingSchedules.workingHours.fromTime")
																	.gt(0).lt(minTime))
															.minimumShouldMatch(1)),
													ScoreMode.None))

											.must(QueryBuilders.termsQuery("workingSchedules.workingDay", days))),
									ScoreMode.None));

			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors.must(QueryBuilders.nestedQuery("workingSchedules",
						boolQuery().must(boolQuery()
								.must(QueryBuilders.nestedQuery("workingSchedules.workingHours", boolQuery()

										.should(QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime")
												.gt(minTime).lt(maxTime))
										.should(QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime")
												.gt(minTime).lt(maxTime))
										.should(boolQuery()
												.must(QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime")
														.gt(maxTime).lt(1439))
												.must(QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime")
														.gt(0).lt(minTime))
												.minimumShouldMatch(1)),
										ScoreMode.None))

								.must(QueryBuilders.termsQuery("workingSchedules.workingDay", days))),
						ScoreMode.None));

		} else {

			if (maxTime == 0) {
				maxTime = 1439;
			}

			boolQueryBuilder
					.must(QueryBuilders
							.nestedQuery("workingSchedules",
									boolQuery().must(boolQuery()
											.must(QueryBuilders.nestedQuery("workingSchedules.workingHours", boolQuery()

													.should(QueryBuilders
															.rangeQuery("workingSchedules.workingHours.toTime")
															.gt(minTime).lt(maxTime))
													.should(QueryBuilders
															.rangeQuery("workingSchedules.workingHours.fromTime")
															.gt(minTime).lt(maxTime))
													.should(boolQuery()
															.must(QueryBuilders
																	.rangeQuery("workingSchedules.workingHours.toTime")
																	.gt(maxTime).lt(1439))
															.must(QueryBuilders
																	.rangeQuery(
																			"workingSchedules.workingHours.fromTime")
																	.gt(0).lt(minTime))
															.minimumShouldMatch(1)),
													ScoreMode.None))

											.must(QueryBuilders.termsQuery("workingSchedules.workingDay", days))),
									ScoreMode.None));

			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors.must(QueryBuilders.nestedQuery("workingSchedules",
						boolQuery().must(boolQuery()
								.must(QueryBuilders.nestedQuery("workingSchedules.workingHours", boolQuery()

										.should(QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime")
												.gt(minTime).lt(maxTime))
										.should(QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime")
												.gt(minTime).lt(maxTime))
										.should(boolQuery()
												.must(QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime")
														.gt(maxTime).lt(1439))
												.must(QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime")
														.gt(0).lt(minTime))
												.minimumShouldMatch(1)),
										ScoreMode.None))

								.must(QueryBuilders.termsQuery("workingSchedules.workingDay", days))),
						ScoreMode.None));

		}
	}

	@SuppressWarnings("deprecation")
	private void createExperienceFilter(BoolQueryBuilder boolQueryBuilder, int maxExperience, int minExperience,
			BoolQueryBuilder boolQueryBuilderForNearByDoctors) {
		if (minExperience != 0 && maxExperience != 0) {
			boolQueryBuilder
					.must(boolQuery().should(QueryBuilders.nestedQuery("experience",
							boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(minExperience)
									.to(maxExperience)),
							ScoreMode.None)).should(
									QueryBuilders.boolQuery()
											.mustNot(QueryBuilders.nestedQuery("experience",
													QueryBuilders.existsQuery("experience"), ScoreMode.None))
											.minimumShouldMatch(1)));

			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors.must(boolQuery()
						.should(QueryBuilders.nestedQuery("experience",
								boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(minExperience)
										.to(maxExperience)),
								ScoreMode.None))
						.should(QueryBuilders.boolQuery()
								.mustNot(QueryBuilders.nestedQuery("experience",
										QueryBuilders.existsQuery("experience"), ScoreMode.None))
								.minimumShouldMatch(1)));
		}

		else if (minExperience != 0) {
			boolQueryBuilder.must(boolQuery().should(QueryBuilders.nestedQuery("experience",
					boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(minExperience)),
					ScoreMode.None)).should(
							QueryBuilders.boolQuery()
									.mustNot(QueryBuilders.nestedQuery("experience",
											QueryBuilders.existsQuery("experience"), ScoreMode.None))
									.minimumShouldMatch(1)));

			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors
						.must(boolQuery()
								.should(QueryBuilders.nestedQuery("experience",
										boolQuery().must(
												QueryBuilders.rangeQuery("experience.experience").from(minExperience)),
										ScoreMode.None))
								.should(QueryBuilders.boolQuery()
										.mustNot(QueryBuilders.nestedQuery("experience",
												QueryBuilders.existsQuery("experience"), ScoreMode.None))
										.minimumShouldMatch(1)));
		}

		else if (maxExperience != 0) {
			boolQueryBuilder.must(boolQuery().should(QueryBuilders.nestedQuery("experience",
					boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(0).to(maxExperience)),
					ScoreMode.None)).should(
							QueryBuilders.boolQuery()
									.mustNot(QueryBuilders.nestedQuery("experience",
											QueryBuilders.existsQuery("experience"), ScoreMode.None))
									.minimumShouldMatch(1)));

			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors
						.must(boolQuery()
								.should(QueryBuilders.nestedQuery("experience",
										boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(0)
												.to(maxExperience)),
										ScoreMode.None))
								.should(QueryBuilders.boolQuery()
										.mustNot(QueryBuilders.nestedQuery("experience",
												QueryBuilders.existsQuery("experience"), ScoreMode.None))
										.minimumShouldMatch(1)));
		}

	}

	@SuppressWarnings("deprecation")
	private void createConsultationFeeFilter(BoolQueryBuilder boolQueryBuilder, int maxFee, int minFee,
			BoolQueryBuilder boolQueryBuilderForNearByDoctors) {
		if (minFee != 0 && maxFee != 0) {
			boolQueryBuilder
					.must(boolQuery()
							.should(QueryBuilders.nestedQuery("consultationFee",
									boolQuery().must(
											QueryBuilders.rangeQuery("consultationFee.amount").from(minFee).to(maxFee)),
									ScoreMode.None))
							.should(QueryBuilders.boolQuery()
									.mustNot(QueryBuilders.nestedQuery("consultationFee",
											QueryBuilders.existsQuery("consultationFee"), ScoreMode.None))
									.minimumShouldMatch(1)));
			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors
						.must(boolQuery()
								.should(QueryBuilders.nestedQuery("consultationFee",
										boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(minFee)
												.to(maxFee)),
										ScoreMode.None))
								.should(QueryBuilders.boolQuery()
										.mustNot(QueryBuilders.nestedQuery("consultationFee",
												QueryBuilders.existsQuery("consultationFee"), ScoreMode.None))
										.minimumShouldMatch(1)));
		}

		else if (minFee != 0) {
			boolQueryBuilder.must(boolQuery().should(QueryBuilders.nestedQuery("consultationFee",
					boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(minFee)), ScoreMode.None))
					.should(QueryBuilders.boolQuery()
							.mustNot(QueryBuilders.nestedQuery("consultationFee",
									QueryBuilders.existsQuery("consultationFee"), ScoreMode.None))
							.minimumShouldMatch(1)));

			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors
						.must(boolQuery()
								.should(QueryBuilders.nestedQuery("consultationFee",
										boolQuery()
												.must(QueryBuilders.rangeQuery("consultationFee.amount").from(minFee)),
										ScoreMode.None))
								.should(QueryBuilders.boolQuery()
										.mustNot(QueryBuilders.nestedQuery("consultationFee",
												QueryBuilders.existsQuery("consultationFee"), ScoreMode.None))
										.minimumShouldMatch(1)));
		} else if (maxFee != 0) {
			boolQueryBuilder
					.must(boolQuery()
							.should(QueryBuilders.nestedQuery("consultationFee",
									boolQuery().must(
											QueryBuilders.rangeQuery("consultationFee.amount").from(0).to(maxFee)),
									ScoreMode.None))
							.should(QueryBuilders.boolQuery()
									.mustNot(QueryBuilders.nestedQuery("consultationFee",
											QueryBuilders.existsQuery("consultationFee"), ScoreMode.None))
									.minimumShouldMatch(1)));

			if (boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors
						.must(boolQuery()
								.should(QueryBuilders.nestedQuery("consultationFee",
										boolQuery().must(
												QueryBuilders.rangeQuery("consultationFee.amount").from(0).to(maxFee)),
										ScoreMode.None))
								.should(QueryBuilders.boolQuery()
										.mustNot(QueryBuilders.nestedQuery("consultationFee",
												QueryBuilders.existsQuery("consultationFee"), ScoreMode.None))
										.minimumShouldMatch(1)));
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
										new CustomAggregationOperation(
												new Document("$project",
														new BasicDBObject("fields.key", "$speciality.speciality")
																.append("fields.value", "$count")
																.append("resourceType",
																		new BasicDBObject("$concat",
																				Arrays.asList("DOCTOR")))
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
			}

			if (types.contains("PHARMACY")) {

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error While Resources Count" + e.getMessage());
		}
		return response;
	}

	@Override
	public DoctorProfileBySlugUrlResponse getDoctorProfileBySlugUrl(String userUId, String slugURL) {
		DoctorProfileBySlugUrlResponse doctorProfile = null;
		List<DoctorClinicProfileBySlugUrlResponse> clinicProfile = new ArrayList<DoctorClinicProfileBySlugUrlResponse>();
		try {

			List<ESDoctorDocument> doctorDocuments = esDoctorRepository.findbySlugUrl(slugURL, true);
			if (doctorDocuments == null || doctorDocuments.isEmpty()) {
				doctorDocuments = esDoctorRepository.findbyUserUId(userUId, true);
				;
			}

			if (doctorDocuments == null || doctorDocuments.isEmpty())
				return null;

			for (ESDoctorDocument doctorDocument : doctorDocuments) {

				if (doctorProfile == null) {
					doctorProfile = new DoctorProfileBySlugUrlResponse();
					BeanUtil.map(doctorDocument, doctorProfile);
					doctorProfile.setDoctorId(doctorDocument.getUserId());
					doctorProfile.setSpecialities(doctorDocument.getSpecialitiesValue());
					doctorProfile.setParentSpecialities(doctorDocument.getParentSpecialities());
					doctorProfile.setServices(doctorDocument.getServicesValue());
				}
				List<DoctorRegistrationDetail> registrationDetails = new ArrayList<DoctorRegistrationDetail>();
				if (doctorProfile.getRegistrationDetails() != null
						&& !doctorProfile.getRegistrationDetails().isEmpty()) {
					for (DoctorRegistrationDetail registrationDetail : doctorProfile.getRegistrationDetails()) {
						DoctorRegistrationDetail doctorRegistrationDetail = new DoctorRegistrationDetail();
						BeanUtil.map(registrationDetail, doctorRegistrationDetail);
						registrationDetails.add(doctorRegistrationDetail);
					}
				}
				doctorProfile.setRegistrationDetails(registrationDetails);

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
						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getCity()) ? doctorDocument.getCity() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getState()) ? doctorDocument.getState() + ", "
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
				org.springframework.data.mongodb.core.query.Criteria criteria = new org.springframework.data.mongodb.core.query.Criteria(
						"doctorId").is(new ObjectId(doctorDocument.getUserId())).and("locationId")
						.is(new ObjectId(doctorDocument.getLocationId())).and("hospitalId")
						.is(new ObjectId(doctorDocument.getHospitalId()));

				List<Feedback> feedbacks = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria.and("isVisible").is(true)),
								Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
								Aggregation.skip((0) * 5), Aggregation.limit(5)),
						FeedbackCollection.class, Feedback.class).getMappedResults();
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