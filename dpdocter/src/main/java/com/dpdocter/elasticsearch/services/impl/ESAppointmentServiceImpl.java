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
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.DistanceUnit;
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
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.LocaleImage;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.elasticsearch.beans.AppointmentSearchResponse;
import com.dpdocter.elasticsearch.beans.ESDoctorWEbSearch;
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
import com.dpdocter.elasticsearch.response.ESDoctorResponse;
import com.dpdocter.elasticsearch.response.LabResponse;
import com.dpdocter.elasticsearch.services.ESAppointmentService;
import com.dpdocter.enums.AppointmentResponseType;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.services.SMSServices;
import com.google.common.collect.Lists;

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

	@Autowired
	TransportClient transportClient;

	@Autowired
	private SMSServices smsServices;

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
					appointmentSearchResponse.setId(esUserLocaleDocument.getId());
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
			String slugUrl = null;
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
									.must(QueryBuilders.matchPhrasePrefixQuery("isDoctorListed", true));
							esDoctorDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
									.withQuery(boolQueryBuilder).withPageable(new PageRequest(0, 50 - response.size()))
									.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.DESC)).build(),
									ESDoctorDocument.class);
						}

					}
				} else {
					if (city != null && location != null)
						esDoctorDocuments = esDoctorRepository.findByCityLocation(city, location, searchTerm, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "rankingCount"));
					else if (city != null)
						esDoctorDocuments = esDoctorRepository.findByCity(city, searchTerm, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "rankingCount"));
					else if (location != null)
						esDoctorDocuments = esDoctorRepository.findByLocation(location, searchTerm, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "rankingCount"));
				}
			} else {
				if (DPDoctorUtils.allStringsEmpty(city, location)) {
					if (latitude != null && longitude != null) {
						BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
								.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude))
										.lon(Double.parseDouble(longitude)).distance("30km"))
								.must(QueryBuilders.matchPhrasePrefixQuery("isDoctorListed", true));

						esDoctorDocuments = elasticsearchTemplate.queryForList(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
										.withPageable(new PageRequest(0, 50 - response.size()))
										.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.DESC)).build(),
								ESDoctorDocument.class);
					}

				} else {
					if (city != null && location != null)
						esDoctorDocuments = esDoctorRepository.findByCityLocation(city, location, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "rankingCount"));
					else if (city != null)
						esDoctorDocuments = esDoctorRepository.findByCity(city, true,
								new PageRequest(0, 50 - response.size(), Direction.DESC, "rankingCount"));
					else if (location != null)
						esDoctorDocuments = esDoctorRepository.findByLocation(location, true,
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
					if (!DPDoctorUtils.anyStringEmpty(doctor.getFirstName())) {
						slugUrl = "dr-" + doctor.getFirstName().toLowerCase().trim();
					}
					List<String> specialities = new ArrayList<>();
					for (String specialityId : doctor.getSpecialities()) {
						ESSpecialityDocument specialityCollection = esSpecialityRepository.findOne(specialityId);
						if (specialityCollection != null) {
							specialities.add(specialityCollection.getSuperSpeciality());
							if (!DPDoctorUtils.anyStringEmpty(slugUrl)) {
								slugUrl = slugUrl + "-" + specialityCollection.getSuperSpeciality().toLowerCase();
							} else {
								slugUrl = specialityCollection.getSuperSpeciality().toLowerCase();
							}
						}
					}
					doctor.setSpecialities(specialities);
					if (!DPDoctorUtils.anyStringEmpty(slugUrl))
						object.setDoctorSlugURL(slugUrl.trim().replaceAll(" ", "-").replaceAll("/", "-"));
					object.setFirstName(doctor.getFirstName());
					object.setUserUId(doctor.getUserUId());
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
			Set<String> esTreatmentServiceset = null;
			if (treatmentServiceDocuments != null) {
				esTreatmentServiceset = new HashSet<String>();
				for (ESTreatmentServiceDocument esTreatmentServiceDocument : treatmentServiceDocuments) {
					if (esTreatmentServiceset.size() >= 50)
						break;
					esTreatmentServiceset.add(esTreatmentServiceDocument.getName());

				}
				for (String serviceName : esTreatmentServiceset) {
					AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
					appointmentSearchResponse.setResponse(serviceName);
					appointmentSearchResponse.setResponseType(AppointmentResponseType.SERVICE);
					response.add(appointmentSearchResponse);
				}
			}
		}

		return response;
	}

	private List<AppointmentSearchResponse> searchTests(List<AppointmentSearchResponse> response, String searchTerm) {
		if (response.size() < 50) {
			List<ESDiagnosticTestDocument> diagnosticTestDocuments = esDiagnosticTestRepository
					.findByTestName(searchTerm);
			Set<String> esTestSet = null;
			if (diagnosticTestDocuments != null) {
				esTestSet = new HashSet<String>();
				for (ESDiagnosticTestDocument diagnosticTest : diagnosticTestDocuments) {
					if (esTestSet.size() >= 50)
						break;
					esTestSet.add(diagnosticTest.getTestName());

				}
				for (String testName : esTestSet) {
					AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
					appointmentSearchResponse.setResponse(testName);
					appointmentSearchResponse.setResponseType(AppointmentResponseType.LABTEST);
					response.add(appointmentSearchResponse);
				}
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
			String slugUrl = null;
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.matchQuery("isDoctorListed", true))
					.must(QueryBuilders.matchQuery("isClinic", true));
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
					Collection<String> specialities = CollectionUtils.collect(esTreatmentServiceDocuments,
							new BeanToPropertyValueTransformer("speciality"));
					Set<String> specialityIdSet = new HashSet<String>();
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
					boolQueryBuilder.must(QueryBuilders.termsQuery("specialities", specialityIdSet));

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
				if (speciality.equalsIgnoreCase("GYNECOLOGIST")) {
					speciality = "GYNAECOLOGIST";
				}
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
				boolQueryBuilder
						.must(QueryBuilders.orQuery(
								nestedQuery("consultationFee",
										boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(minFee)
												.to(maxFee))),
								QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
										QueryBuilders.existsQuery("consultationFee")))));

			else if (minFee != 0)
				boolQueryBuilder
						.must(QueryBuilders
								.orQuery(
										QueryBuilders.nestedQuery("consultationFee",
												boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount")
														.from(minFee))),
										QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
												QueryBuilders.existsQuery("consultationFee")))));
			else if (maxFee != 0)
				boolQueryBuilder
						.must(QueryBuilders
								.orQuery(
										QueryBuilders.nestedQuery("consultationFee",
												boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount")
														.from(0).to(maxFee))),
										QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
												QueryBuilders.existsQuery("consultationFee")))));

			if (minExperience != 0 && maxExperience != 0)
				boolQueryBuilder.must(QueryBuilders.orQuery(
						QueryBuilders.nestedQuery("experience",
								boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(minExperience)
										.to(maxExperience))),
						QueryBuilders.boolQuery().mustNot(
								QueryBuilders.nestedQuery("experience", QueryBuilders.existsQuery("experience")))));

			else if (minExperience != 0)
				boolQueryBuilder.must(QueryBuilders
						.orQuery(
								QueryBuilders.nestedQuery(
										"experience",
										boolQuery().must(
												QueryBuilders.rangeQuery("experience.experience").from(minExperience))),
								QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("experience",
										QueryBuilders.existsQuery("experience")))));

			else if (maxExperience != 0)
				boolQueryBuilder
						.must(QueryBuilders.orQuery(
								QueryBuilders.nestedQuery("experience",
										boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(0)
												.to(maxExperience))),
								QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("experience",
										QueryBuilders.existsQuery("experience")))));

			if (!DPDoctorUtils.anyStringEmpty(gender)) {
				boolQueryBuilder.must(QueryBuilders.matchQuery("gender", gender));
			}
			if (days != null && !days.isEmpty()) {
				for (int i = 0; i < days.size(); i++) {
					days.set(i, days.get(i).toLowerCase());
				}

				if (maxTime == 0) {
					maxTime = 1439;
				}
				boolQueryBuilder.must(QueryBuilders.nestedQuery("workingSchedules", boolQuery()
						.must(QueryBuilders.andQuery(nestedQuery("workingSchedules.workingHours", QueryBuilders.orQuery(

								QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(minTime)
										.lt(maxTime),

								QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime")
										.gt(minTime).lt(
												maxTime),
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

								QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(minTime)
										.lt(maxTime),

								QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime")
										.gt(minTime).lt(
												maxTime),
								QueryBuilders.andQuery(
										QueryBuilders.rangeQuery("workingSchedules.workingHours.toTime").gt(maxTime)
												.lt(1439),
										QueryBuilders.rangeQuery("workingSchedules.workingHours.fromTime").gt(0)
												.lt(minTime)))))));
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
						.withSort(SortBuilders.geoDistanceSort("geoPoint")
								.point(Double.parseDouble(latitude), Double.parseDouble(longitude)).order(SortOrder.ASC)
								.unit(DistanceUnit.KILOMETERS))
						.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.DESC)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.DESC)).build();
			System.out.println(searchQuery);
			esDoctorDocuments = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);

			if (esDoctorDocuments != null) {
				for (ESDoctorDocument doctorDocument : esDoctorDocuments) {
					if (doctorDocument.getSpecialities() != null) {
						if (!DPDoctorUtils.anyStringEmpty(doctorDocument.getFirstName())) {
							slugUrl = "dr-" + doctorDocument.getFirstName().toLowerCase().trim();
						}

						List<String> specialities = new ArrayList<>();
						for (String specialityId : doctorDocument.getSpecialities()) {
							ESSpecialityDocument specialityCollection = esSpecialityRepository.findOne(specialityId);
							if (specialityCollection != null) {
								specialities.add(specialityCollection.getSuperSpeciality());
								if (!DPDoctorUtils.anyStringEmpty(slugUrl)) {
									slugUrl = slugUrl + "-" + specialityCollection.getSuperSpeciality().toLowerCase();
								} else {
									slugUrl = specialityCollection.getSuperSpeciality().toLowerCase();
								}
							}
						}
						doctorDocument.setSpecialities(specialities);
					}
					if (!DPDoctorUtils.anyStringEmpty(slugUrl))
						doctorDocument.setDoctorSlugURL(slugUrl.trim().replaceAll(" ", "-").replaceAll("/", "-"));

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

	@SuppressWarnings({ "deprecation", "unchecked" })
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
			/*
			 * if (esLabTestDocuments == null || esLabTestDocuments.isEmpty()) {
			 * return null; }
			 */
			List<ESLocationDocument> esLocationDocuments = null;
			Collection<String> locationIds = null;
			if (esLabTestDocuments != null) {
				locationIds = CollectionUtils.collect(esLabTestDocuments,
						new BeanToPropertyValueTransformer("locationId"));
			}
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.matchQuery("isLocationListed", true))
					.must(QueryBuilders.termQuery("isLab", true));
			if (locationIds != null && !locationIds.isEmpty()) {
				boolQueryBuilder.must(QueryBuilders.termsQuery("locationId", locationIds));
			}
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

				if (maxTime != 0 || minTime != 0) {
					if (maxTime == 0) {
						maxTime = 1439;
					}
					boolQueryBuilder
							.must(QueryBuilders.nestedQuery("clinicWorkingSchedules", boolQuery().must(QueryBuilders
									.andQuery(nestedQuery("clinicWorkingSchedules.workingHours", QueryBuilders.orQuery(

											QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.toTime")
													.gt(minTime).lt(maxTime),

											QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.fromTime")
													.gt(minTime).lt(maxTime),
											QueryBuilders
													.andQuery(
															QueryBuilders
																	.rangeQuery(
																			"clinicWorkingSchedules.workingHours.toTime")
																	.gt(maxTime).lt(1439),
															QueryBuilders
																	.rangeQuery(
																			"clinicWorkingSchedules.workingHours.fromTime")
																	.gt(0).lt(minTime)))),
											QueryBuilders.termsQuery("clinicWorkingSchedules.workingDay", days)))));
				} else {
					boolQueryBuilder.must(QueryBuilders.nestedQuery("clinicWorkingSchedules",
							boolQuery().must(QueryBuilders.termsQuery("clinicWorkingSchedules.workingDay", days))));
				}
			} else {
				if (maxTime != 0 || minTime != 0) {
					if (maxTime == 0) {
						maxTime = 1439;
					}
					boolQueryBuilder.must(QueryBuilders.nestedQuery("clinicWorkingSchedules",
							boolQuery().must(nestedQuery("clinicWorkingSchedules.workingHours", QueryBuilders.orQuery(

									QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.toTime").gt(minTime)
											.lt(maxTime),

									QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.fromTime").gt(minTime)
											.lt(maxTime),
									QueryBuilders.andQuery(
											QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.toTime")
													.gt(maxTime).lt(1439),
											QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.fromTime")
													.gt(0).lt(minTime)))))));
				}
			}

			boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude))
					.lon(Double.parseDouble(longitude)).distance("30km"));

			SearchQuery searchQuery = null;
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size))
						.withSort(SortBuilders.geoDistanceSort("geoPoint")
								.point(Double.parseDouble(latitude), Double.parseDouble(longitude)).order(SortOrder.ASC)
								.unit(DistanceUnit.KILOMETERS))
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
	public List<ESUserLocaleDocument> getPharmacies(int page, int size, String city, String localeName, String latitude,
			String longitude, String paymentType, Boolean homeService, Boolean isTwentyFourSevenOpen, long minTime,
			long maxTime, List<String> days, List<String> pharmacyType, Boolean isGenericMedicineAvailable) {
		List<ESUserLocaleDocument> esUserLocaleDocuments = null;

		try {

			// do {
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.matchQuery("isLocaleListed", true));

			if (DPDoctorUtils.anyStringEmpty(longitude, latitude) && !DPDoctorUtils.anyStringEmpty(city)) {
				ESCityDocument esCityDocument = esCityRepository.findByName(city);
				if (esCityDocument != null) {
					latitude = esCityDocument.getLatitude() + "";
					longitude = esCityDocument.getLongitude() + "";
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(localeName)) {
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("localeName", localeName));
			}
			if (!DPDoctorUtils.anyStringEmpty(paymentType)) {
				boolQueryBuilder.must(QueryBuilders.matchQuery("paymentInfos", paymentType));
			}
			if (homeService != null) {

				boolQueryBuilder.must(QueryBuilders.termQuery("isHomeDeliveryAvailable", homeService));
			}
			if (isTwentyFourSevenOpen != null) {

				boolQueryBuilder.must(QueryBuilders.termQuery("isTwentyFourSevenOpen", isTwentyFourSevenOpen));
			}
			if (isGenericMedicineAvailable != null) {

				boolQueryBuilder
						.must(QueryBuilders.termQuery("isGenericMedicineAvailable", isGenericMedicineAvailable));
			}

			if (days != null && !days.isEmpty()) {
				for (int i = 0; i < days.size(); i++)
					days.set(i, days.get(i).toLowerCase());

				boolQueryBuilder.must(QueryBuilders.nestedQuery("localeWorkingSchedules",
						boolQuery().must(QueryBuilders.termsQuery("localeWorkingSchedules.workingDay", days))));
			}
			if (pharmacyType != null && !pharmacyType.isEmpty()) {
				for (String type : pharmacyType)
					boolQueryBuilder.should(QueryBuilders.matchQuery("pharmacyType", type.toUpperCase()));

				boolQueryBuilder.minimumNumberShouldMatch(1);
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
			if (latitude != null && longitude != null) {
				boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude))
						.lon(Double.parseDouble(longitude)).distance("30km"));
			}

			SearchQuery searchQuery = null;
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size))
						.withSort(SortBuilders.geoDistanceSort("geoPoint")
								.point(Double.parseDouble(latitude), Double.parseDouble(longitude)).order(SortOrder.ASC)
								.unit(DistanceUnit.KILOMETERS))
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

					if (!DPDoctorUtils.anyStringEmpty(latitude) && !DPDoctorUtils.anyStringEmpty(longitude)
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

	@Override
	@Transactional
	public Boolean sendSMSToDoctors() {
		List<ESDoctorDocument> esDoctorDocuments = null;
		Boolean status = false;
		Set<String> mobileNumberSet = new HashSet<String>();
		List<String> mobileNumbers = null;
		try {
			esDoctorDocuments = Lists.newArrayList(esDoctorRepository.findAll());
			SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
			smsTrackDetail.setType("DOCTOR's DAY SMS");
			String message = "Its Doctors day! And we prescribe you a day full of Happiness for all the silent efforts that you put in. Thank you "
					+ "from " + "Healthcoco " + "Stay healthy, Stay happy!";
			for (ESDoctorDocument esDoctorDocument : esDoctorDocuments) {
				mobileNumberSet.add(esDoctorDocument.getMobileNumber());
			}
			if (mobileNumberSet != null) {
				mobileNumbers = new ArrayList<String>(mobileNumberSet);
			}
			if (mobileNumbers != null) {
				for (String mobileNumber : mobileNumbers) {
					SMSDetail smsDetail = new SMSDetail();
					SMS sms = new SMS();
					smsDetail.setUserName(mobileNumber);
					SMSAddress smsAddress = new SMSAddress();
					smsAddress.setRecipient(mobileNumber);
					sms.setSmsAddress(smsAddress);
					sms.setSmsText(message);
					smsDetail.setSms(sms);
					smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
					List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
					smsDetails.add(smsDetail);
					smsTrackDetail.setSmsDetails(smsDetails);
					smsServices.sendSMS(smsTrackDetail, true);
				}
			}
			status = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error While Getting Doctor Details From ES : " + e.getMessage());
		}
		return status;
	}

	@Override
	@Transactional
	public ESDoctorResponse getDoctorForWeb(int page, int size, String city, String location, String latitude,
			String longitude, String speciality, String symptom, Boolean booking, Boolean calling, int minFee,
			int maxFee, int minTime, int maxTime, List<String> days, String gender, int minExperience,
			int maxExperience, String service) {
		ESDoctorResponse doctorResponse = null;
		try {
			List<ESDoctorDocument> doctors = getDoctors(page, size, city, location, latitude, longitude, speciality,
					symptom, booking, calling, minFee, maxFee, minTime, maxTime, days, gender, minExperience,
					maxExperience, service);
			doctorResponse = new ESDoctorResponse();
			List<ESDoctorWEbSearch> doctorList = new ArrayList<ESDoctorWEbSearch>();
			ESDoctorWEbSearch doctorWEbSearch = null;
			if (doctors != null && !doctors.isEmpty()) {
				for (ESDoctorDocument doctor : doctors) {
					doctorWEbSearch = new ESDoctorWEbSearch();
					BeanUtil.map(doctor, doctorWEbSearch);
					doctorList.add(doctorWEbSearch);
				}
				doctorResponse.setDoctors(doctorList);

			}
			if (!DPDoctorUtils.anyStringEmpty(speciality)) {
				doctorResponse.setMetaData(speciality + " in " + city);
			} else {
				doctorResponse.setMetaData("doctor in " + city);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error While Getting Doctor Details From ES for Web : " + e.getMessage());

		}
		return doctorResponse;
	}

}
