package com.dpdocter.elasticsearch.services.impl;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.LocaleImage;
import com.dpdocter.elasticsearch.beans.AppointmentSearchResponse;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESLabTestDocument;
import com.dpdocter.elasticsearch.document.ESLocationDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceCostDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceDocument;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.elasticsearch.repository.ESCityRepository;
import com.dpdocter.elasticsearch.repository.ESComplaintsRepository;
import com.dpdocter.elasticsearch.repository.ESDiagnosticTestRepository;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESLocationRepository;
import com.dpdocter.elasticsearch.repository.ESSpecialityRepository;
import com.dpdocter.elasticsearch.repository.ESTreatmentServiceRepository;
import com.dpdocter.elasticsearch.repository.ESUserLocaleRepository;
import com.dpdocter.elasticsearch.response.LabResponse;
import com.dpdocter.elasticsearch.services.ESAppointmentService;
import com.dpdocter.enums.AppointmentResponseType;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;

import common.util.web.DPDoctorUtils;

@Service
public class ESAppointmentServiceImpl implements ESAppointmentService {
	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private ESCityRepository esCityRepository;

	@Autowired
	private ESDoctorRepository esDoctorRepository;

	@Autowired
	private ESLocationRepository esLocationRepository;

	@Autowired
	private ESUserLocaleRepository esUserLocaleRepository;

	@Autowired
	private ESSpecialityRepository esSpecialityRepository;

	@Autowired
	private ESComplaintsRepository esComplaintsRepository;

	@Autowired
	private ESDiagnosticTestRepository esDiagnosticTestRepository;

	@Autowired
	private ESTreatmentServiceRepository esTreatmentServiceRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Override
	public List<AppointmentSearchResponse> search(String city, String location, String latitude, String longitude,
			String searchTerm) {
		List<AppointmentSearchResponse> response = null;
		try {

			response = new ArrayList<AppointmentSearchResponse>();

			response = searchSpeciality(response, searchTerm);
			// response = searchSymptons(response, searchTerm);
			response = searchTests(response, searchTerm);
			response = searchTreatmentService(response, searchTerm);
			response = searchDoctors(response, city, location, latitude, longitude, searchTerm);
			response = searchLocations(response, city, location, latitude, longitude, searchTerm);
			response = searchPharmacy(response, city, location, latitude, longitude, searchTerm);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	private List<AppointmentSearchResponse> searchLocations(List<AppointmentSearchResponse> response, String city,
			String location, String latitude, String longitude, String searchTerm) {
		if (response.size() < 50) {
			List<ESLocationDocument> esLocationDocuments = null;
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				if (DPDoctorUtils.allStringsEmpty(city, location)) {
					if (DPDoctorUtils.allStringsEmpty(latitude, longitude))
						esLocationDocuments = esLocationRepository.findByLocationName(searchTerm);
					else {
						if (latitude != null && longitude != null) {
							BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
									.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude))
											.lon(Double.parseDouble(longitude)).distance("30km"))
									.must(QueryBuilders.matchPhrasePrefixQuery("locationName", searchTerm))
									.must(QueryBuilders.matchPhrasePrefixQuery("isLocationListed", true));
							esLocationDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
									.withQuery(boolQueryBuilder).withPageable(new PageRequest(0, 50 - response.size()))
									.withSort(SortBuilders.fieldSort("clinicRankingCount").order(SortOrder.DESC))
									.build(), ESLocationDocument.class);
						}
					}
				} else {
					if (city != null && location != null)
						esLocationDocuments = esLocationRepository.findByCityLocationName(city, location, searchTerm,
								true, new PageRequest(0, 50 - response.size(), Direction.DESC, "clinicRankingCount"));
					else if (city != null)
						esLocationDocuments = esLocationRepository.findByCityLocationName(city, searchTerm, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "clinicRankingCount"));
					else if (location != null)
						esLocationDocuments = esLocationRepository.findByLocationLocationName(location, searchTerm,
								true, new PageRequest(0, 50 - response.size(), Direction.DESC, "clinicRankingCount"));
				}
			} else {
				if (DPDoctorUtils.allStringsEmpty(city, location)) {
					if (latitude != null && longitude != null) {
						BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
								.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude))
										.lon(Double.parseDouble(longitude)).distance("30km"))
								.must(QueryBuilders.matchPhrasePrefixQuery("isLocationListed", true));
						esLocationDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
								.withQuery(boolQueryBuilder).withPageable(new PageRequest(0, 50 - response.size()))
								.withSort(SortBuilders.fieldSort("clinicRankingCount").order(SortOrder.DESC)).build(),
								ESLocationDocument.class);
					} else {
					}
					if (city != null && location != null)
						esLocationDocuments = esLocationRepository.findLocationByCityLocation(city, location, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "clinicRankingCount"));
					else if (city != null)
						esLocationDocuments = esLocationRepository.findLocationByCity(city, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "clinicRankingCount"));
					else if (location != null)
						esLocationDocuments = esLocationRepository.findLocationByLocation(location, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "clinicRankingCount"));
				}
			}

			if (esLocationDocuments != null)
				for (ESLocationDocument locationDocument : esLocationDocuments) {
					if (locationDocument.getIsClinic()) {
						if (response.size() >= 50)
							break;
						AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
						appointmentSearchResponse.setId(locationDocument.getLocationId());
						appointmentSearchResponse.setResponse(locationDocument.getLocationName());
						appointmentSearchResponse.setResponseType(AppointmentResponseType.CLINIC);
						response.add(appointmentSearchResponse);
					}
				}

			if (esLocationDocuments != null)
				for (ESLocationDocument locationDocument : esLocationDocuments) {
					if (response.size() >= 50)
						break;
					if (locationDocument.getIsLab()) {
						AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
						appointmentSearchResponse.setId(locationDocument.getLocationId());
						appointmentSearchResponse.setResponse(locationDocument.getLocationName());
						appointmentSearchResponse.setResponseType(AppointmentResponseType.LAB);
						response.add(appointmentSearchResponse);
					}
				}
		}
		return response;
	}

	@SuppressWarnings("deprecation")
	private List<AppointmentSearchResponse> searchPharmacy(List<AppointmentSearchResponse> response, String city,
			String location, String latitude, String longitude, String searchTerm) {
		if (response.size() < 50) {
			List<ESUserLocaleDocument> esUserLocaleDocuments = null;
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				if (DPDoctorUtils.allStringsEmpty(city, location)) {
					if (DPDoctorUtils.allStringsEmpty(latitude, longitude))
						esUserLocaleDocuments = esUserLocaleRepository.findByLocaleName(searchTerm);
					else {
						if (latitude != null && longitude != null) {
							BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
									.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude))
											.lon(Double.parseDouble(longitude)).distance("30km"))
									.must(QueryBuilders.matchPhrasePrefixQuery("localeName", searchTerm))
									.must(QueryBuilders.matchPhrasePrefixQuery("isLocaleListed", true));
							esUserLocaleDocuments = elasticsearchTemplate
									.queryForList(
											new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
													.withPageable(new PageRequest(0, 50 - response.size()))
													.withSort(SortBuilders.fieldSort("localeRankingCount")
															.order(SortOrder.DESC))
													.build(),
											ESUserLocaleDocument.class);
						}
					}
				} else {
					BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
							.must(QueryBuilders.matchPhrasePrefixQuery("localeName", searchTerm))
							.must(QueryBuilders.matchQuery("isLocaleListed", true));
					if (city != null) {
						boolQueryBuilder.must(QueryBuilders.nestedQuery("address",
								boolQuery().must(QueryBuilders.matchQuery("address.city", city))));
					}
					if (location != null) {
						boolQueryBuilder.must(QueryBuilders.nestedQuery("address",
								boolQuery().must(QueryBuilders.orQuery(
										QueryBuilders.matchPhrasePrefixQuery("address.streetAddress", location),
										QueryBuilders.matchPhrasePrefixQuery("address.locality", location)))));
					}
					esUserLocaleDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
							.withQuery(boolQueryBuilder).withPageable(new PageRequest(0, 50 - response.size()))
							.withSort(SortBuilders.fieldSort("localeRankingCount").order(SortOrder.DESC)).build(),
							ESUserLocaleDocument.class);
				}
			} else {
				if (DPDoctorUtils.allStringsEmpty(city, location)) {
					if (latitude != null && longitude != null) {
						BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
								.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude))
										.lon(Double.parseDouble(longitude)).distance("30km"))
								.must(QueryBuilders.matchPhrasePrefixQuery("isLocaleListed", true));
						esUserLocaleDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
								.withQuery(boolQueryBuilder).withPageable(new PageRequest(0, 50 - response.size()))
								.withSort(SortBuilders.fieldSort("localeRankingCount").order(SortOrder.DESC)).build(),
								ESUserLocaleDocument.class);
					}
				} else {
					BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
							.must(QueryBuilders.matchQuery("isLocaleListed", true));
					if (city != null) {
						boolQueryBuilder.must(QueryBuilders.nestedQuery("address",
								boolQuery().must(QueryBuilders.matchQuery("address.city", city))));
					}
					if (location != null) {
						boolQueryBuilder.must(QueryBuilders.nestedQuery("address",
								boolQuery().must(QueryBuilders.orQuery(
										QueryBuilders.matchPhrasePrefixQuery("address.streetAddress", location),
										QueryBuilders.matchPhrasePrefixQuery("address.locality", location)))));
					}
					esUserLocaleDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
							.withQuery(boolQueryBuilder).withPageable(new PageRequest(0, 50 - response.size()))
							.withSort(SortBuilders.fieldSort("localeRankingCount").order(SortOrder.DESC)).build(),
							ESUserLocaleDocument.class);
				}
			}

			if (esUserLocaleDocuments != null)
				for (ESUserLocaleDocument esUserLocaleDocument : esUserLocaleDocuments) {
					if (response.size() >= 50)
						break;
					AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
					appointmentSearchResponse.setId(esUserLocaleDocument.getLocaleId());
					appointmentSearchResponse.setResponse(esUserLocaleDocument.getLocaleName());
					appointmentSearchResponse.setResponseType(AppointmentResponseType.PHARMACY);
					response.add(appointmentSearchResponse);
				}
		}
		return response;
	}

	private List<AppointmentSearchResponse> searchDoctors(List<AppointmentSearchResponse> response, String city,
			String location, String latitude, String longitude, String searchTerm) {
		if (response.size() < 50) {
			List<ESDoctorDocument> esDoctorDocuments = null;
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				if (DPDoctorUtils.allStringsEmpty(city, location)) {
					if (DPDoctorUtils.allStringsEmpty(latitude, longitude))
						esDoctorDocuments = esDoctorRepository.findByFirstName(searchTerm, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "rankingCount"));
					else {
						if (latitude != null && longitude != null) {
							BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
									.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude))
											.lon(Double.parseDouble(longitude)).distance("30km"))
									.must(QueryBuilders.matchPhrasePrefixQuery("firstName", searchTerm))
									.must(QueryBuilders.matchPhrasePrefixQuery("isDoctorListed", true))
									.must(QueryBuilders.matchPhrasePrefixQuery("isActive", true))
									.must(QueryBuilders.matchPhrasePrefixQuery("isActivate", true));
							esDoctorDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
									.withQuery(boolQueryBuilder).withPageable(new PageRequest(0, 50 - response.size()))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.DESC)).build(),
									ESDoctorDocument.class);
						}

					}
				} else {
					if (city != null && location != null)
						esDoctorDocuments = esDoctorRepository.findByCityLocation(city, location, searchTerm, true, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "rankingCount"));
					else if (city != null)
						esDoctorDocuments = esDoctorRepository.findByCity(city, searchTerm, true, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "rankingCount"));
					else if (location != null)
						esDoctorDocuments = esDoctorRepository.findByLocation(location, searchTerm, true, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "rankingCount"));
				}
			} else {
				if (DPDoctorUtils.allStringsEmpty(city, location)) {
					if (latitude != null && longitude != null) {
						BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
								.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude))
										.lon(Double.parseDouble(longitude)).distance("30km"))
								.must(QueryBuilders.matchPhrasePrefixQuery("isDoctorListed", true))
								.must(QueryBuilders.matchPhrasePrefixQuery("isActive", true))
								.must(QueryBuilders.matchPhrasePrefixQuery("isActivate", true));
						esDoctorDocuments = elasticsearchTemplate.queryForList(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
										.withPageable(new PageRequest(0, 50 - response.size()))
										.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.DESC)).build(),
								ESDoctorDocument.class);
					}

				} else {
					if (city != null && location != null)
						esDoctorDocuments = esDoctorRepository.findByCityLocation(city, location, true, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "rankingCount"));
					else if (city != null)
						esDoctorDocuments = esDoctorRepository.findByCity(city, true, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "rankingCount"));
					else if (location != null)
						esDoctorDocuments = esDoctorRepository.findByLocation(location, true, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "rankingCount"));
				}
			}

			if (esDoctorDocuments != null)
				for (ESDoctorDocument doctor : esDoctorDocuments) {
					if (response.size() >= 50)
						break;
					AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
					appointmentSearchResponse.setId(doctor.getUserId());
					ESDoctorDocument object = new ESDoctorDocument();
					object.setTitle(doctor.getTitle());
					object.setUserId(doctor.getUserId());
					object.setFirstName(doctor.getFirstName());
					object.setLocationId(doctor.getLocationId());
					object.setHospitalId(doctor.getHospitalId());
					appointmentSearchResponse.setResponse(object);
					appointmentSearchResponse.setResponseType(AppointmentResponseType.DOCTOR);
					response.add(appointmentSearchResponse);
				}

		}
		return response;
	}

	private List<AppointmentSearchResponse> searchTreatmentService(List<AppointmentSearchResponse> response,
			String searchTerm) {
		if (response.size() < 50) {
			List<ESTreatmentServiceDocument> treatmentServiceDocuments = esTreatmentServiceRepository
					.findByName(searchTerm);
			if (treatmentServiceDocuments != null)
				for (ESTreatmentServiceDocument esTreatmentServiceDocument : treatmentServiceDocuments) {
					if (response.size() >= 50)
						break;
					AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
					appointmentSearchResponse.setId(esTreatmentServiceDocument.getId());
					appointmentSearchResponse.setResponse(esTreatmentServiceDocument.getName());
					appointmentSearchResponse.setResponseType(AppointmentResponseType.SERVICE);
					response.add(appointmentSearchResponse);
				}
		}
		return response;
	}

	private List<AppointmentSearchResponse> searchTests(List<AppointmentSearchResponse> response, String searchTerm) {
		if (response.size() < 50) {
			List<ESDiagnosticTestDocument> diagnosticTestDocuments = esDiagnosticTestRepository
					.findByTestName(searchTerm);
			if (diagnosticTestDocuments != null)
				for (ESDiagnosticTestDocument diagnosticTest : diagnosticTestDocuments) {
					if (response.size() >= 50)
						break;
					AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
					appointmentSearchResponse.setId(diagnosticTest.getId());
					appointmentSearchResponse.setResponse(diagnosticTest.getTestName());
					appointmentSearchResponse.setResponseType(AppointmentResponseType.LABTEST);
					response.add(appointmentSearchResponse);
				}
		}
		return response;
	}

	// private List<AppointmentSearchResponse>
	// searchSymptons(List<AppointmentSearchResponse> response,
	// String searchTerm) {
	// if (response.size() < 50) {
	// List<ESComplaintsDocument> complaintsDocuments =
	// esComplaintsRepository.findByComplaint(searchTerm);
	// if (complaintsDocuments != null)
	// for (ESComplaintsDocument esComplaintsDocument : complaintsDocuments) {
	// if (response.size() >= 50)
	// break;
	// AppointmentSearchResponse appointmentSearchResponse = new
	// AppointmentSearchResponse();
	// appointmentSearchResponse.setId(esComplaintsDocument.getId());
	// appointmentSearchResponse.setResponse(esComplaintsDocument);
	// appointmentSearchResponse.setResponseType(AppointmentResponseType.SYMPTOM);
	// response.add(appointmentSearchResponse);
	// }
	// }
	// return response;
	// }

	private List<AppointmentSearchResponse> searchSpeciality(List<AppointmentSearchResponse> response,
			String searchTerm) {
		List<ESSpecialityDocument> esSpecialityDocuments = esSpecialityRepository.findByQueryAnnotation(searchTerm);
		if (esSpecialityDocuments != null)
			for (ESSpecialityDocument speciality : esSpecialityDocuments) {
				if (response.size() >= 50)
					break;
				AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
				appointmentSearchResponse.setId(speciality.getId());
				appointmentSearchResponse.setResponse(speciality.getSuperSpeciality());
				appointmentSearchResponse.setResponseType(AppointmentResponseType.SPECIALITY);
				response.add(appointmentSearchResponse);
			}
		return response;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<ESDoctorDocument> getDoctors(int page, int size, String city, String location, String latitude,
			String longitude, String speciality, String symptom, Boolean booking, Boolean calling, int minFee,
			int maxFee, int minTime, int maxTime, List<String> days, String gender, int minExperience,
			int maxExperience, String service) {
		List<ESDoctorDocument> esDoctorDocuments = null;
		List<ESTreatmentServiceCostDocument> esTreatmentServiceCostDocuments = null;
		try {
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.matchQuery("isDoctorListed", true))
					.must(QueryBuilders.matchQuery("isClinic", true))
					.must(QueryBuilders.matchQuery("isActivate", true))
			        .must(QueryBuilders.matchPhrasePrefixQuery("isActive", true));
			if (DPDoctorUtils.anyStringEmpty(longitude, latitude) && !DPDoctorUtils.anyStringEmpty(city)) {
				ESCityDocument esCityDocument = esCityRepository.findByName(city);
				if (esCityDocument != null) {
					latitude = esCityDocument.getLatitude() + "";
					longitude = esCityDocument.getLongitude() + "";
				}
			}
			if (!DPDoctorUtils.anyStringEmpty(service)) {
				List<ESTreatmentServiceDocument> esTreatmentServiceDocuments = esTreatmentServiceRepository
						.findByName(service);
				if (esTreatmentServiceDocuments != null) {
					Collection<String> serviceIds = CollectionUtils.collect(esTreatmentServiceDocuments,
							new BeanToPropertyValueTransformer("id"));
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
				Set<String> locationIds = new HashSet<>(CollectionUtils.collect(esTreatmentServiceCostDocuments,
						new BeanToPropertyValueTransformer("locationId")));
				Set<String> doctorIds = new HashSet<>(CollectionUtils.collect(esTreatmentServiceCostDocuments,
						new BeanToPropertyValueTransformer("doctorId")));

				locationIds.remove(null);
				doctorIds.remove(null);
				if ((locationIds == null || locationIds.isEmpty()) && (doctorIds == null || doctorIds.isEmpty())) {
					return null;
				}
				boolQueryBuilder.must(QueryBuilders.termsQuery("userId", doctorIds))
						.must(QueryBuilders.termsQuery("locationId", locationIds));
			}

			/*
			 * if (!DPDoctorUtils.anyStringEmpty(symptom)) {
			 * List<ESComplaintsDocument> esComplaintsDocuments =
			 * esComplaintsRepository.findByComplaint(symptom); if
			 * (esComplaintsDocuments == null ||
			 * esComplaintsDocuments.isEmpty()) { return null; } Set<String>
			 * locationIds = new
			 * HashSet<>(CollectionUtils.collect(esComplaintsDocuments, new
			 * BeanToPropertyValueTransformer("locationId"))); Set<String>
			 * doctorIds = new HashSet<>(
			 * CollectionUtils.collect(esComplaintsDocuments, new
			 * BeanToPropertyValueTransformer("doctorId")));
			 * 
			 * locationIds.remove(null); doctorIds.remove(null); if
			 * ((locationIds == null || locationIds.isEmpty()) && (doctorIds ==
			 * null || doctorIds.isEmpty())) { return null; }
			 * boolQueryBuilder.must(QueryBuilders.termsQuery("userId",
			 * doctorIds)) .must(QueryBuilders.termsQuery("locationId",
			 * locationIds)); }
			 */

			if (!DPDoctorUtils.anyStringEmpty(speciality)) {
				List<ESSpecialityDocument> esSpecialityDocuments = esSpecialityRepository
						.findByQueryAnnotation(speciality);
				if (esSpecialityDocuments != null) {
					Collection<String> specialityIds = CollectionUtils.collect(esSpecialityDocuments,
							new BeanToPropertyValueTransformer("id"));
					if (specialityIds == null)
						specialityIds = CollectionUtils.EMPTY_COLLECTION;
					boolQueryBuilder.must(QueryBuilders.termsQuery("specialities", specialityIds));
				}
			}

			if (booking != null && calling != null) {
				if (booking && calling)
					;
				else if (booking && !calling) {
					boolQueryBuilder
							.must(QueryBuilders.termsQuery("facility", DoctorFacility.BOOK.getType().toLowerCase(),
									DoctorFacility.IBS.getType().toLowerCase()))
							.mustNot(QueryBuilders.matchQuery("facility", DoctorFacility.CALL.getType()));
				} else if (!booking && calling) {
					boolQueryBuilder.must(QueryBuilders.matchQuery("facility", DoctorFacility.CALL.getType()))
							.mustNot(QueryBuilders.termsQuery("facility", DoctorFacility.BOOK.getType().toLowerCase(),
									DoctorFacility.IBS.getType().toLowerCase()));
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(location)) {
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("locationName", location));
			}

			if (minFee != 0 && maxFee != 0)
				boolQueryBuilder.must(QueryBuilders.nestedQuery("consultationFee",
						boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(minFee).to(maxFee))));
			else if (minFee != 0)
				boolQueryBuilder.must(QueryBuilders.nestedQuery("consultationFee",
						boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(minFee))));
			else if (maxFee != 0)
				boolQueryBuilder
						.must(QueryBuilders
								.orQuery(
										QueryBuilders.nestedQuery("consultationFee",
												boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount")
														.from(0).to(maxFee))),
										QueryBuilders.notQuery(QueryBuilders.existsQuery("consultationFee"))));

			if (minExperience != 0 && maxExperience != 0)
				boolQueryBuilder.must(QueryBuilders.nestedQuery("experience", boolQuery().must(
						QueryBuilders.rangeQuery("experience.experience").from(minExperience).to(maxExperience))));
			else if (minExperience != 0)
				boolQueryBuilder.must(QueryBuilders.nestedQuery("experience",
						boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(minExperience))));
			else if (maxExperience != 0)
				boolQueryBuilder
						.must(QueryBuilders.orQuery(
								QueryBuilders.nestedQuery("experience",
										boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(0)
												.to(maxExperience))),
								QueryBuilders.notQuery(QueryBuilders.existsQuery("experience"))));

			if (!DPDoctorUtils.anyStringEmpty(gender)) {
				boolQueryBuilder.must(QueryBuilders.matchQuery("gender", gender));
			}
			if (days != null && !days.isEmpty()) {
				for (int i = 0; i < days.size(); i++) {
					days.set(i, days.get(i).toLowerCase());
				}
				boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules",						
								boolQuery().must(QueryBuilders.termsQuery("workingSchedules.workingDay", days))));

			}
			if (maxTime != 0 || minTime != 0) {
				if (maxTime == 0) {
					maxTime = 1439;
				}
				boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules", boolQuery()
						.must(nestedQuery("workingSchedules.workingHours", boolQuery().must(QueryBuilders.orQuery(

								QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(minTime)
										.lt(maxTime),

								QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(minTime)
										.lt(maxTime)))))));
			}
			// if (minTime != 0 || maxTime != 0) {
			// if (maxTime == 0) {
			// maxTime = 1439;
			// }
			// boolQueryBuilder.mustNot(QueryBuilders.nestedQuery("workingSchedules",
			// boolQuery().must(nestedQuery(
			// "workingSchedules.workingHours",
			// boolQuery().must(QueryBuilders.orQuery(
			// QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").lte(minTime),
			// QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gte(maxTime)))))));
			// }

			if (latitude != null && longitude != null)
				boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude))
						.lon(Double.parseDouble(longitude)).distance("30km"));

			SearchQuery searchQuery = null;
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size))
						.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.DESC)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.DESC)).build();
			esDoctorDocuments = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);

			if (esDoctorDocuments != null) {
				for (ESDoctorDocument doctorDocument : esDoctorDocuments) {
					if (doctorDocument.getSpecialities() != null) {
						List<String> specialities = new ArrayList<>();
						for (String specialityId : doctorDocument.getSpecialities()) {
							ESSpecialityDocument specialityCollection = esSpecialityRepository.findOne(specialityId);
							if (specialityCollection != null)
								specialities.add(specialityCollection.getSuperSpeciality());
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

					if (address.charAt(address.length() - 2) == ',') {
						address = address.substring(0, address.length() - 2);
					}
					doctorDocument.setClinicAddress(address);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error While Getting Doctor Details From ES : " + e.getMessage());
		}
		return esDoctorDocuments;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<LabResponse> getLabs(int page, int size, String city, String location, String latitude,
			String longitude, String test, Boolean booking, Boolean calling, int minTime, int maxTime,
			List<String> days, Boolean onlineReports, Boolean homeService, Boolean nabl) {
		List<LabResponse> response = null;
		List<ESLabTestDocument> esLabTestDocuments = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(longitude, latitude) && !DPDoctorUtils.anyStringEmpty(city)) {
				ESCityDocument esCityDocument = esCityRepository.findByName(city);
				if (esCityDocument != null) {
					latitude = esCityDocument.getLatitude() + "";
					longitude = esCityDocument.getLongitude() + "";
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(test)) {
				List<ESDiagnosticTestDocument> diagnosticTests = esDiagnosticTestRepository.findByTestName(test);
				if (diagnosticTests != null) {
					@SuppressWarnings("unchecked")
					Collection<String> testIds = CollectionUtils.collect(diagnosticTests,
							new BeanToPropertyValueTransformer("id"));
					int count = (int) elasticsearchTemplate.count(new CriteriaQuery(new Criteria("testId").in(testIds)),
							ESLabTestDocument.class);
					if (count > 0)
						esLabTestDocuments = elasticsearchTemplate.queryForList(
								new NativeSearchQueryBuilder().withQuery(QueryBuilders.termsQuery("testId", testIds))
										.withPageable(new PageRequest(0, count)).build(),
								ESLabTestDocument.class);
				}
			}
			if (esLabTestDocuments == null || esLabTestDocuments.isEmpty()) {
				return null;
			}
			List<ESLocationDocument> esLocationDocuments = null;

			@SuppressWarnings("unchecked")
			Collection<String> locationIds = CollectionUtils.collect(esLabTestDocuments,
					new BeanToPropertyValueTransformer("locationId"));

			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.matchQuery("isLocationListed", true))
					.must(QueryBuilders.termsQuery("locationId", locationIds))
					.must(QueryBuilders.termQuery("isLab", true));
			if (booking != null && booking)
				boolQueryBuilder.must(QueryBuilders.termQuery("facility", DoctorFacility.BOOK.getType()));
			if (calling != null && calling)
				boolQueryBuilder.must(QueryBuilders.termQuery("facility", DoctorFacility.CALL.getType()));

			if (onlineReports != null)
				boolQueryBuilder.must(QueryBuilders.termQuery("isOnlineReportsAvailable", onlineReports));
			if (homeService != null)
				boolQueryBuilder.must(QueryBuilders.termQuery("isHomeServiceAvailable", homeService));
			if (nabl != null)
				boolQueryBuilder.must(QueryBuilders.termQuery("isNABLAccredited", nabl));

			if (days != null && !days.isEmpty()) {
				for (int i = 0; i < days.size(); i++) {
					days.set(i, days.get(i).toLowerCase());
				}

				boolQueryBuilder.must(QueryBuilders.nestedQuery("clinicWorkingSchedules",
						boolQuery().must(QueryBuilders.termsQuery("clinicWorkingSchedules.workingDay", days))));

				if (maxTime == 0) {
					maxTime = 1439;
				}
				boolQueryBuilder.must(QueryBuilders.nestedQuery("clinicWorkingSchedules", boolQuery().must(
						nestedQuery("clinicWorkingSchedules.workingHours", boolQuery().must((QueryBuilders.orQuery(

								QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.toTime").gt(minTime)
										.lt(maxTime),

								QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.fromTime").gt(minTime)
										.lt(maxTime))))))));
			}

			boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude))
					.lon(Double.parseDouble(longitude)).distance("30km"));

			SearchQuery searchQuery = null;
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size))
						.withSort(SortBuilders.fieldSort("clinicRankingCount").order(SortOrder.DESC)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("clinicRankingCount").order(SortOrder.DESC)).build();
			esLocationDocuments = elasticsearchTemplate.queryForList(searchQuery, ESLocationDocument.class);

			if (esLocationDocuments != null && !esLocationDocuments.isEmpty()) {
				for (ESLocationDocument document : esLocationDocuments) {
					LabResponse labResponse = new LabResponse();
					BeanUtil.map(document, labResponse);
					List<String> images = new ArrayList<String>();
					if (document.getImages() != null)
						for (String clinicImage : document.getImages()) {
							images.add(getFinalImageURL(clinicImage));
						}
					labResponse.setImages(images);
					if (document.getLogoUrl() != null)
						labResponse.setLogoUrl(getFinalImageURL(document.getLogoUrl()));
					if (latitude != null && longitude != null && document.getLatitude() != null
							&& document.getLongitude() != null) {
						labResponse.setDistance(DPDoctorUtils.distance(Double.parseDouble(latitude),
								Double.parseDouble(longitude), document.getLatitude(), document.getLongitude(), "K"));
					}
					String address = (!DPDoctorUtils.anyStringEmpty(document.getStreetAddress())
							? document.getStreetAddress() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(document.getLandmarkDetails())
									? document.getLandmarkDetails() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(document.getLocality()) ? document.getLocality() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(document.getCity()) ? document.getCity() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(document.getState()) ? document.getState() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(document.getCountry()) ? document.getCountry() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(document.getPostalCode()) ? document.getPostalCode() : "");

					if (address.charAt(address.length() - 2) == ',') {
						address = address.substring(0, address.length() - 2);
					}
					labResponse.setClinicAddress(address);
					if (response == null)
						response = new ArrayList<LabResponse>();
					response.add(labResponse);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error While Getting Labs From ES : " + e.getMessage());
		}
		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;

	}

	@SuppressWarnings("deprecation")
	@Override
	public List<ESUserLocaleDocument> getPharmacies(int page, int size, String city, String location, String latitude,
			String longitude, String paymentType, Boolean homeService, Boolean isTwentyFourSevenOpen, long minTime,
			long maxTime, List<String> days) {
		List<ESUserLocaleDocument> esUserLocaleDocuments = null;
		try {
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.matchQuery("isLocaleListed", true))
					.must(QueryBuilders.matchQuery("isActivate", true));

			if (DPDoctorUtils.anyStringEmpty(longitude, latitude) && !DPDoctorUtils.anyStringEmpty(city)) {
				ESCityDocument esCityDocument = esCityRepository.findByName(city);
				if (esCityDocument != null) {
					latitude = esCityDocument.getLatitude() + "";
					longitude = esCityDocument.getLongitude() + "";
				}
			}
			if (!DPDoctorUtils.anyStringEmpty(location)) {
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("localeName", location));
			}
			if (!DPDoctorUtils.anyStringEmpty(paymentType)) {
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("paymentInfo", paymentType));
			}
			if (homeService != null) {
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("isHomeDeliveryAvailable", homeService));
			}
			if (isTwentyFourSevenOpen != null) {
				boolQueryBuilder
						.must(QueryBuilders.matchPhrasePrefixQuery("isTwentyFourSevenOpen", isTwentyFourSevenOpen));
			}
			if (days != null && !days.isEmpty()) {
				for (int i = 0; i < days.size(); i++)
					days.set(i, days.get(i).toLowerCase());

				boolQueryBuilder.must(QueryBuilders.nestedQuery("localeWorkingSchedules",
						boolQuery().must(QueryBuilders.termsQuery("localeWorkingSchedules.workingDay", days))));
			}

			if (maxTime == 0) {
				maxTime = 86399999;
				boolQueryBuilder.must(QueryBuilders.orQuery(QueryBuilders.nestedQuery("localeWorkingSchedules",
						boolQuery().must(nestedQuery("localeWorkingSchedules.workingHours",
								boolQuery().must(QueryBuilders.orQuery(

										QueryBuilders.rangeQuery("localeWorkingSchedules.workingHours.toTime")
												.gt(minTime).lt(maxTime),

										QueryBuilders.rangeQuery("localeWorkingSchedules.workingHours.fromTime")
												.gt(minTime).lt(maxTime)))))),
						QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("localeWorkingSchedules"))));

			} else {
				boolQueryBuilder.must(QueryBuilders.nestedQuery("localeWorkingSchedules", boolQuery()
						.must(nestedQuery("localeWorkingSchedules.workingHours", boolQuery().must(QueryBuilders.orQuery(

								QueryBuilders.rangeQuery("localeWorkingSchedules.workingHours.toTime").gt(minTime)
										.lt(maxTime),

								QueryBuilders.rangeQuery("localeWorkingSchedules.workingHours.fromTime").gt(minTime)
										.lt(maxTime)))))));
			}

			if (latitude != null && longitude != null)
				boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude))
						.lon(Double.parseDouble(longitude)).distance("30km"));

			SearchQuery searchQuery = null;
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size))
						.withSort(SortBuilders.fieldSort("localeRankingCount").order(SortOrder.DESC)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("localeRankingCount").order(SortOrder.DESC)).build();
			esUserLocaleDocuments = elasticsearchTemplate.queryForList(searchQuery, ESUserLocaleDocument.class);

			if (esUserLocaleDocuments != null) {
				for (ESUserLocaleDocument esUserLocaleDocument : esUserLocaleDocuments) {
					if (esUserLocaleDocument.getImageUrl() != null)
						esUserLocaleDocument.setImageUrl(getFinalImageURL(esUserLocaleDocument.getImageUrl()));

					if (esUserLocaleDocument.getThumbnailUrl() != null)
						esUserLocaleDocument.setThumbnailUrl(getFinalImageURL(esUserLocaleDocument.getThumbnailUrl()));

					if (esUserLocaleDocument.getLocaleImages() != null
							&& !esUserLocaleDocument.getLocaleImages().isEmpty()) {
						for (LocaleImage localeImage : esUserLocaleDocument.getLocaleImages()) {
							localeImage.setImageUrl(getFinalImageURL(localeImage.getImageUrl()));
							localeImage.setThumbnailUrl(getFinalImageURL(localeImage.getThumbnailUrl()));
						}
					}
					if (esUserLocaleDocument.getLogoUrl() != null)
						esUserLocaleDocument.setLogoUrl(getFinalImageURL(esUserLocaleDocument.getLogoUrl()));

					if (latitude != null && longitude != null && esUserLocaleDocument.getAddress() != null
							&& esUserLocaleDocument.getAddress().getLatitude() != null
							&& esUserLocaleDocument.getAddress().getLongitude() != null) {
						esUserLocaleDocument.setDistance(DPDoctorUtils.distance(Double.parseDouble(latitude),
								Double.parseDouble(longitude), esUserLocaleDocument.getAddress().getLatitude(),
								esUserLocaleDocument.getAddress().getLongitude(), "K"));
					}
					if (esUserLocaleDocument.getAddress() != null) {
						String address = (!DPDoctorUtils
								.anyStringEmpty(esUserLocaleDocument.getAddress().getStreetAddress())
										? esUserLocaleDocument.getAddress().getStreetAddress() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(esUserLocaleDocument.getAddress().getLocality())
										? esUserLocaleDocument.getAddress().getLocality() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(esUserLocaleDocument.getAddress().getCity())
										? esUserLocaleDocument.getAddress().getCity() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(esUserLocaleDocument.getAddress().getState())
										? esUserLocaleDocument.getAddress().getState() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(esUserLocaleDocument.getAddress().getCountry())
										? esUserLocaleDocument.getAddress().getCountry() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(esUserLocaleDocument.getAddress().getPostalCode())
										? esUserLocaleDocument.getAddress().getPostalCode() : "");

						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}
						esUserLocaleDocument.setLocaleAddress(address);
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error While Getting Doctor Details From ES : " + e.getMessage());
		}
		return esUserLocaleDocuments;
	}
}
