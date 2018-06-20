package com.dpdocter.services.impl;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceCostDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceDocument;
import com.dpdocter.elasticsearch.repository.ESSpecialityRepository;
import com.dpdocter.elasticsearch.repository.ESTreatmentServiceRepository;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
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
	private ESTreatmentServiceRepository esTreatmentServiceRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Value(value = "${image.path}")
	private String imagePath;
	
	@SuppressWarnings("unchecked")
	@Override
	public SearchDoctorResponse searchDoctors(int page, int size, String city, String location, String latitude, String longitude,
			String speciality,
			String symptom, Boolean booking, Boolean calling, int minFee, int maxFee, int minTime, int maxTime,
			List<String> days, String gender, int minExperience, int maxExperience, String service, String locality, Boolean otherArea) {
		List<ESDoctorDocument> esDoctorDocuments = null;
		List<ESDoctorDocument> nearByDoctors = null;
		List<ESTreatmentServiceCostDocument> esTreatmentServiceCostDocuments = null;
		SearchDoctorResponse response = null;
		try {
			Set<String> specialityIdSet = new HashSet<String>();
			Set<String> locationIds = null, doctorIds = null;

			if(city.equalsIgnoreCase("undefined")) {
				return null;
			}
			if (!DPDoctorUtils.anyStringEmpty(service)) {
				List<ESTreatmentServiceDocument> esTreatmentServiceDocuments = esTreatmentServiceRepository
						.findByName(service);
				if (esTreatmentServiceDocuments != null) {
					Collection<String> serviceIds = CollectionUtils.collect(esTreatmentServiceDocuments,
							new BeanToPropertyValueTransformer("id"));
					Collection<String> specialities = CollectionUtils.collect(esTreatmentServiceDocuments,
							new BeanToPropertyValueTransformer("speciality"));

					for (String specialitySTR : specialities) {
						List<ESSpecialityDocument> esSpecialityDocuments = esSpecialityRepository
								.findByQueryAnnotation(specialitySTR);
						if (esSpecialityDocuments != null && !esSpecialityDocuments.isEmpty()) {
							Collection<String> specialityIds = CollectionUtils.collect(esSpecialityDocuments,
									new BeanToPropertyValueTransformer("id"));
							if (specialityIds != null) {
								specialityIdSet.addAll(specialityIds);
							}
						}

					}

					int count = (int) elasticsearchTemplate.count(
							new CriteriaQuery(new Criteria("treatmentServiceId").in(serviceIds)),
							ESTreatmentServiceCostDocument.class);
					if (count > 0)
						esTreatmentServiceCostDocuments = elasticsearchTemplate.queryForList(
								new NativeSearchQueryBuilder()
										.withQuery(QueryBuilders.termsQuery("treatmentServiceId", serviceIds))
										.withPageable(new PageRequest(0, count)).build(),
								ESTreatmentServiceCostDocument.class);

				}
				if (esTreatmentServiceCostDocuments == null || esTreatmentServiceCostDocuments.isEmpty()) {
					return null;
				}
				locationIds = new HashSet<>(CollectionUtils.collect(esTreatmentServiceCostDocuments,
						new BeanToPropertyValueTransformer("locationId")));
				doctorIds = new HashSet<>(CollectionUtils.collect(esTreatmentServiceCostDocuments,
						new BeanToPropertyValueTransformer("doctorId")));

				locationIds.remove(null);
				doctorIds.remove(null);
			}

			QueryBuilder specialityQueryBuilder = createSpecialityFilter(speciality);
			QueryBuilder facilityQueryBuilder = createFacilityBuilder(booking, calling);
		
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
						.must(QueryBuilders.matchQuery("isDoctorListed", true))
						.must(QueryBuilders.matchQuery("isClinic", true));
			BoolQueryBuilder boolQueryBuilderForNearByDoctors = new BoolQueryBuilder()
					.must(QueryBuilders.matchQuery("isDoctorListed", true))
					.must(QueryBuilders.matchQuery("isClinic", true));
			
				if (!DPDoctorUtils.anyStringEmpty(city)) {
					boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("city", city));
					boolQueryBuilderForNearByDoctors.must(QueryBuilders.matchPhrasePrefixQuery("city", city));
				}
				
				if (specialityIdSet != null && !specialityIdSet.isEmpty()) {
					boolQueryBuilder.must(QueryBuilders.termsQuery("specialities", specialityIdSet));
					boolQueryBuilderForNearByDoctors.must(QueryBuilders.termsQuery("specialities", specialityIdSet));
				}

				if ((locationIds != null && !locationIds.isEmpty()) && (doctorIds != null && !doctorIds.isEmpty())) {
					boolQueryBuilder.must(QueryBuilders.termsQuery("userId", doctorIds))
							.must(QueryBuilders.termsQuery("locationId", locationIds));
					boolQueryBuilderForNearByDoctors.must(QueryBuilders.termsQuery("userId", doctorIds))
					.must(QueryBuilders.termsQuery("locationId", locationIds));
				}

				if (specialityQueryBuilder != null) {
					boolQueryBuilder.must(specialityQueryBuilder);
					boolQueryBuilderForNearByDoctors.must(specialityQueryBuilder);
				}
					
				if (facilityQueryBuilder != null) {
					boolQueryBuilder.must(facilityQueryBuilder);
					boolQueryBuilderForNearByDoctors.must(facilityQueryBuilder);
				}
					
				createConsultationFeeFilter(boolQueryBuilder, maxFee, minFee, boolQueryBuilderForNearByDoctors);
				createExperienceFilter(boolQueryBuilder, maxExperience, minExperience, boolQueryBuilderForNearByDoctors);
				if (!DPDoctorUtils.anyStringEmpty(gender)) {
					boolQueryBuilder.must(QueryBuilders.matchQuery("gender", gender));
					boolQueryBuilderForNearByDoctors.must(QueryBuilders.matchQuery("gender", gender));
				}

				createTimeFilter(boolQueryBuilder, maxTime, minTime, days, boolQueryBuilderForNearByDoctors);
				
				Integer count = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), ESDoctorDocument.class);
				SearchQuery searchQuery = null;
				
				if(!DPDoctorUtils.anyStringEmpty(locality) && !locality.equalsIgnoreCase("undefined")) {
					if (size > 0)
						searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
								.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
								.withPageable(new PageRequest(page, size)).build();
					else
						searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
								.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC)).build();

					esDoctorDocuments = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
				}else {
					if(!otherArea) {
						if (size > 0)
							searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder.must(QueryBuilders.multiMatchQuery(locality, "landmarkDetails", "streetAddress", "locality").type(MatchQueryBuilder.Type.PHRASE_PREFIX)))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
									.withPageable(new PageRequest(page, size)).build();
						else
							searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder.must(QueryBuilders.multiMatchQuery(locality, "landmarkDetails", "streetAddress", "locality").type(MatchQueryBuilder.Type.PHRASE_PREFIX)))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC)).build();

						esDoctorDocuments = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
					}

					if (esDoctorDocuments == null || esDoctorDocuments.isEmpty()) {					
						if (size > 0)
							searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilderForNearByDoctors.mustNot(QueryBuilders.multiMatchQuery(locality, "landmarkDetails", "streetAddress", "locality").type(MatchQueryBuilder.Type.PHRASE_PREFIX)))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
									.withPageable(new PageRequest(page, size)).build();
						else
							searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilderForNearByDoctors.mustNot(QueryBuilders.multiMatchQuery(locality, "landmarkDetails", "streetAddress", "locality").type(MatchQueryBuilder.Type.PHRASE_PREFIX)))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC)).build();
						nearByDoctors = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
					}else {
						if (size > 0) {
							size = size - esDoctorDocuments.size();
							if (size > 0) {
								if (size > 0)
									searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilderForNearByDoctors.mustNot(QueryBuilders.multiMatchQuery(locality, "landmarkDetails", "streetAddress", "locality").type(MatchQueryBuilder.Type.PHRASE_PREFIX)))
											.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
											.withPageable(new PageRequest(page, size)).build();
								nearByDoctors = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);	
							}
						}else {
							searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilderForNearByDoctors.mustNot(QueryBuilders.multiMatchQuery(locality, "landmarkDetails", "streetAddress", "locality").type(MatchQueryBuilder.Type.PHRASE_PREFIX)))
										.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC)).build();
							nearByDoctors = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
						}
					}
				}
			if(!(esDoctorDocuments == null && nearByDoctors == null)) {
				response = new SearchDoctorResponse();
				
				
				if (!DPDoctorUtils.anyStringEmpty(speciality)) {
					response.setSpeciality(StringUtils.capitalize(speciality));

					response.setMetaData(StringUtils.capitalize(speciality) + "s in ");
				} else {
					response.setMetaData("Doctors in ");

					response.setSpeciality("ALL Specialities");

				}
				if (!DPDoctorUtils.allStringsEmpty(locality) && !locality.equalsIgnoreCase("undefined")) {

					response.setMetaData(response.getMetaData() + StringUtils.capitalize(locality) + ", ");
				}
				if (DPDoctorUtils.anyStringEmpty(city)) {
					city = "Nagpur";
				}
				response.setMetaData(response.getMetaData() + StringUtils.capitalize(city));
				response.setCity(StringUtils.capitalize(city));
				response.setCount(count);
				
				if(esDoctorDocuments != null) {
					esDoctorDocuments = formatDoctorData(esDoctorDocuments, latitude, longitude);
					response.setDoctors(esDoctorDocuments);
				}
				if(nearByDoctors != null) {
					nearByDoctors = formatDoctorData(nearByDoctors, latitude, longitude);
					response.setNearByDoctors(nearByDoctors);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error While Getting Doctor Details From ES : " + e.getMessage());
		}
		return response;
	}

	private List<ESDoctorDocument> formatDoctorData(List<ESDoctorDocument> esDoctorDocuments, String latitude, String longitude) {
		
		if (esDoctorDocuments != null) {

			List<String> specialities = null;
			for (ESDoctorDocument doctorDocument : esDoctorDocuments) {
System.out.println(doctorDocument.getSpecialities());
				if (doctorDocument.getSpecialities() != null) {
					specialities = new ArrayList<String>();
					for (String specialityId : doctorDocument.getSpecialities()) {
						ESSpecialityDocument specialityCollection = esSpecialityRepository.findOne(specialityId);
						if (specialityCollection != null) {
							specialities.add(specialityCollection.getSuperSpeciality());

						}
					}
					doctorDocument.setSpecialities(specialities);
				}

				if (doctorDocument.getImageUrl() != null)
					doctorDocument.setImageUrl(getFinalImageURL(doctorDocument.getImageUrl()));
				if (doctorDocument.getImages() != null && !doctorDocument.getImages().isEmpty()) {
					List<String> images = new ArrayList<String>();
					for (String clinicImage : doctorDocument.getImages()) {
						images.add(getFinalImageURL(clinicImage));
					}
					doctorDocument.setImages(images);
				}
				if (doctorDocument.getLogoUrl() != null)
					doctorDocument.setLogoUrl(getFinalImageURL(doctorDocument.getLogoUrl()));

				if (doctorDocument.getCoverImageUrl() != null)
					doctorDocument.setCoverImageUrl(getFinalImageURL(doctorDocument.getCoverImageUrl()));

				if (latitude != null && longitude != null && doctorDocument.getLatitude() != null
						&& doctorDocument.getLongitude() != null) {
					doctorDocument.setDistance(
							DPDoctorUtils.distance(Double.parseDouble(latitude), Double.parseDouble(longitude),
									doctorDocument.getLatitude(), doctorDocument.getLongitude(), "K"));
				}
				doctorDocument.getDob();
				String address = (!DPDoctorUtils.anyStringEmpty(doctorDocument.getStreetAddress())
						? doctorDocument.getStreetAddress() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getLandmarkDetails())
								? doctorDocument.getLandmarkDetails() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getLocality())
								? doctorDocument.getLocality() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getCity()) ? doctorDocument.getCity() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getState())
								? doctorDocument.getState() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getCountry())
								? doctorDocument.getCountry() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getPostalCode())
								? doctorDocument.getPostalCode() : "");

				if (address.length()>1 && address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}
				doctorDocument.setClinicAddress(address);

			}
		}
		return esDoctorDocuments;
	}
	
	@SuppressWarnings("unchecked")
	private QueryBuilder createSpecialityFilter(String speciality) {
		QueryBuilder queryBuilder = null;
		if (!DPDoctorUtils.anyStringEmpty(speciality)) {
			if (speciality.equalsIgnoreCase("GYNECOLOGIST")) {
				speciality = "GYNAECOLOGIST";
			}
			List<ESSpecialityDocument> esSpecialityDocuments = esSpecialityRepository.findByQueryAnnotation(speciality);
			if (speciality.equalsIgnoreCase("GENERAL PHYSICIAN")) {
				speciality = "FAMILY PHYSICIAN";
			} else if (speciality.equalsIgnoreCase("FAMILY PHYSICIAN")) {
				speciality = "GENERAL PHYSICIAN";
			}
			List<ESSpecialityDocument> esSpecialityDocuments2 = new LinkedList<ESSpecialityDocument>(
					esSpecialityDocuments);

			if (speciality.equalsIgnoreCase("GENERAL PHYSICIAN") || speciality.equalsIgnoreCase("FAMILY PHYSICIAN")) {

				esSpecialityDocuments = esSpecialityRepository.findByQueryAnnotation(speciality);
				for (ESSpecialityDocument esSpecialityDocument : esSpecialityDocuments) {
					if (esSpecialityDocument != null) {
						esSpecialityDocuments2.add(esSpecialityDocuments2.size(), esSpecialityDocument);
					}
				}
			}
			if (esSpecialityDocuments2 != null) {
				Collection<String> specialityIds = CollectionUtils.collect(esSpecialityDocuments2,
						new BeanToPropertyValueTransformer("id"));
				if (specialityIds == null)
					specialityIds = CollectionUtils.EMPTY_COLLECTION;
				queryBuilder = QueryBuilders.termsQuery("specialities", specialityIds);
			}
		}
		return queryBuilder;
	}
	
	private QueryBuilder createFacilityBuilder(Boolean booking, Boolean calling) {
		QueryBuilder queryBuilder = null;
		if (booking != null && calling != null) {
			if (booking && calling)
				;
			else if (booking && !calling) {
				queryBuilder = QueryBuilders.termsQuery("facility", DoctorFacility.BOOK.getType().toLowerCase(),
						DoctorFacility.IBS.getType().toLowerCase());

			} else if (!booking && calling) {
				queryBuilder = QueryBuilders.matchQuery("facility", DoctorFacility.CALL.getType());
			}
		}
		return queryBuilder;
	}

	@SuppressWarnings("deprecation")
	private void createTimeFilter(BoolQueryBuilder boolQueryBuilder, int maxTime, int minTime, List<String> days, BoolQueryBuilder boolQueryBuilderForNearByDoctors) {
		if (days != null && !days.isEmpty()) {
			for (int i = 0; i < days.size(); i++) {
				days.set(i, days.get(i).toLowerCase());
			}

			if (maxTime == 0) {
				maxTime = 1439;
			}
			boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules", boolQuery()
					.must(QueryBuilders.andQuery(nestedQuery("workingSchedules.workingHours", QueryBuilders.orQuery(

							QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(minTime).lt(maxTime),

							QueryBuilders
									.rangeQuery("workingSchedules.workingHours.fromTime").gt(
											minTime)
									.lt(maxTime),
							QueryBuilders.andQuery(
									QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(maxTime)
											.lt(1439),
									QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(0)
											.lt(minTime)))),
							QueryBuilders.termsQuery("workingSchedules.workingDay", days)))));
			
			if(boolQueryBuilderForNearByDoctors != null)boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules", boolQuery()
					.must(QueryBuilders.andQuery(nestedQuery("workingSchedules.workingHours", QueryBuilders.orQuery(

							QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(minTime).lt(maxTime),

							QueryBuilders
									.rangeQuery("workingSchedules.workingHours.fromTime").gt(
											minTime)
									.lt(maxTime),
							QueryBuilders.andQuery(
									QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(maxTime)
											.lt(1439),
									QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(0)
											.lt(minTime)))),
							QueryBuilders.termsQuery("workingSchedules.workingDay", days)))));

		} else {

			if (maxTime == 0) {
				maxTime = 1439;
			}
			boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules",
					boolQuery().must(nestedQuery("workingSchedules.workingHours", QueryBuilders.orQuery(

							QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(minTime).lt(maxTime),

							QueryBuilders
									.rangeQuery("workingSchedules.workingHours.fromTime").gt(
											minTime)
									.lt(maxTime),
							QueryBuilders.andQuery(
									QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(maxTime)
											.lt(1439),
									QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(0)
											.lt(minTime)))))));
			
			if(boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors.must(QueryBuilders.nestedQuery("workingSchedules",
					boolQuery().must(nestedQuery("workingSchedules.workingHours", QueryBuilders.orQuery(

							QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(minTime).lt(maxTime),

							QueryBuilders
									.rangeQuery("workingSchedules.workingHours.fromTime").gt(
											minTime)
									.lt(maxTime),
							QueryBuilders.andQuery(
									QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(maxTime)
											.lt(1439),
									QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(0)
											.lt(minTime)))))));
		}
	}

	@SuppressWarnings("deprecation")
	private void createExperienceFilter(BoolQueryBuilder boolQueryBuilder, int maxExperience, int minExperience, BoolQueryBuilder boolQueryBuilderForNearByDoctors) {
		if (minExperience != 0 && maxExperience != 0) {
			boolQueryBuilder.must(QueryBuilders.orQuery(
					QueryBuilders.nestedQuery("experience",
							boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(minExperience)
									.to(maxExperience))),
					QueryBuilders.boolQuery().mustNot(
							QueryBuilders.nestedQuery("experience", QueryBuilders.existsQuery("experience")))));
			
			if(boolQueryBuilderForNearByDoctors != null)boolQueryBuilderForNearByDoctors.must(QueryBuilders.orQuery(
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
			
			if(boolQueryBuilderForNearByDoctors != null)boolQueryBuilderForNearByDoctors.must(QueryBuilders
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
			
			if(boolQueryBuilderForNearByDoctors != null)boolQueryBuilderForNearByDoctors.must(QueryBuilders.orQuery(
					QueryBuilders.nestedQuery("experience",
							boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(0)
									.to(maxExperience))),
					QueryBuilders.boolQuery().mustNot(
							QueryBuilders.nestedQuery("experience", QueryBuilders.existsQuery("experience")))));
		}
			
	}

	@SuppressWarnings("deprecation")
	private void createConsultationFeeFilter(BoolQueryBuilder boolQueryBuilder, int maxFee, int minFee, BoolQueryBuilder boolQueryBuilderForNearByDoctors) {
		if (minFee != 0 && maxFee != 0) {
			boolQueryBuilder
			.must(QueryBuilders.orQuery(
					nestedQuery("consultationFee",
							boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(minFee)
									.to(maxFee))),
					QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
							QueryBuilders.existsQuery("consultationFee")))));
			if(boolQueryBuilderForNearByDoctors != null)boolQueryBuilderForNearByDoctors.must(QueryBuilders.orQuery(
					nestedQuery("consultationFee",
							boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(minFee)
									.to(maxFee))),
					QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
							QueryBuilders.existsQuery("consultationFee")))));
		}
			

		else if (minFee != 0) {
			boolQueryBuilder.must(QueryBuilders
					.orQuery(
							QueryBuilders.nestedQuery("consultationFee",
									boolQuery().must(
											QueryBuilders.rangeQuery("consultationFee.amount").from(minFee))),
							QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
									QueryBuilders.existsQuery("consultationFee")))));
			
			if(boolQueryBuilderForNearByDoctors != null)boolQueryBuilderForNearByDoctors.must(QueryBuilders
					.orQuery(
							QueryBuilders.nestedQuery("consultationFee",
									boolQuery().must(
											QueryBuilders.rangeQuery("consultationFee.amount").from(minFee))),
							QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
									QueryBuilders.existsQuery("consultationFee")))));
		}
		else if (maxFee != 0) {
			boolQueryBuilder
			.must(QueryBuilders
					.orQuery(
							QueryBuilders.nestedQuery("consultationFee",
									boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(0)
											.to(maxFee))),
							QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
									QueryBuilders.existsQuery("consultationFee")))));
			
			if(boolQueryBuilderForNearByDoctors != null)
				boolQueryBuilderForNearByDoctors.must(QueryBuilders
					.orQuery(
							QueryBuilders.nestedQuery("consultationFee",
									boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(0)
											.to(maxFee))),
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
			if(types == null || types.isEmpty()) {
				types = new ArrayList<String>();
				types.add("DOCTOR");
				types.add("PHARMACY");
			}else {
				for(String type: types)type=type.toUpperCase();
			}
			
			if(types.contains("DOCTOR")) {
				response = mongoTemplate.aggregate(Aggregation.newAggregation(
						Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
						Aggregation.match(new org.springframework.data.mongodb.core.query.Criteria("location.city").is(city)),
						Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"), Aggregation.unwind("doctor"),
						Aggregation.unwind("doctor.specialities"),
															new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("_id", "$doctor.specialities")
																	.append("count", new BasicDBObject("$sum", 1)))),
															Aggregation.lookup("speciality_cl", "_id", "_id", "speciality"),
															Aggregation.unwind("speciality"),
															new CustomAggregationOperation(new BasicDBObject("$project", 
																	new BasicDBObject("fields.key", "$speciality.speciality").append("fields.value", "$count")
																	.append("resourceType", new BasicDBObject("$concat", Arrays.asList("DOCTOR"))).append("totalCount", "$count"))),
															new CustomAggregationOperation(new BasicDBObject("$sort", new BasicDBObject("fields.value", -1))), 
															new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("_id", "$resourceType")
																	.append("resourceType", new BasicDBObject("$first","$resourceType"))
																	.append("totalCount", new BasicDBObject("$sum","$totalCount"))
																	.append("fields", new BasicDBObject("$addToSet","$fields"))))),
						DoctorClinicProfileCollection.class, ResourcesCountResponse.class).getMappedResults();
				
				
//				ResourcesCountResponse doctors = new ResourcesCountResponse();
//				doctors.setResourceType("DOCTORS");
//				doctors.setFields(fields);
			}
			
			if(types.contains("PHARMACY")) {
				
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error While Resources Count" + e.getMessage());
		}
		return response;
	}
}
