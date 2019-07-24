package com.dpdocter.elasticsearch.services.impl;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
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
import org.springframework.util.StringUtils;

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
import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;
import com.dpdocter.elasticsearch.document.ESLocationDocument;
import com.dpdocter.elasticsearch.document.ESServicesDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.document.ESSymptomDiseaseConditionDocument;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.elasticsearch.repository.ESCityRepository;
import com.dpdocter.elasticsearch.repository.ESDiagnosticTestRepository;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESLandmarkLocalityRepository;
import com.dpdocter.elasticsearch.repository.ESLocationRepository;
import com.dpdocter.elasticsearch.repository.ESServicesRepository;
import com.dpdocter.elasticsearch.repository.ESSpecialityRepository;
import com.dpdocter.elasticsearch.repository.ESSymptomDiseaseConditionRepository;
import com.dpdocter.elasticsearch.repository.ESUserLocaleRepository;
import com.dpdocter.elasticsearch.response.ESDoctorCardResponse;
import com.dpdocter.elasticsearch.response.ESWEBResponse;
import com.dpdocter.elasticsearch.response.LabResponse;
import com.dpdocter.elasticsearch.services.ESAppointmentService;
import com.dpdocter.enums.AppointmentResponseType;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.response.SearchLandmarkLocalityResponse;
import com.dpdocter.services.SMSServices;
import com.google.common.collect.Lists;

import common.util.web.DPDoctorUtils;

@Service
public class ESAppointmentServiceImpl implements ESAppointmentService {
	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private ESLandmarkLocalityRepository esLandmarkLocalityRepository;

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
	private ESDiagnosticTestRepository esDiagnosticTestRepository;

	@Autowired
	private ESServicesRepository esServicesRepository;

	@Autowired
	private ESSymptomDiseaseConditionRepository esSymptomDiseaseConditionRepository;
	
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
			// response = searchSymptoms(response, searchTerm);
			response = searchTests(response, searchTerm);
			response = searchService(response, searchTerm);
			response = searchSymptomDiseaseCondition(response, searchTerm);
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
									.filter(QueryBuilders.geoDistanceQuery("geoPoint").point(Double.parseDouble(latitude), Double.parseDouble(longitude)).distance("30km"))
									.must(QueryBuilders.matchPhrasePrefixQuery("locationName", searchTerm))
									.must(QueryBuilders.matchPhrasePrefixQuery("isLocationListed", true));
							esLocationDocuments = elasticsearchTemplate
									.queryForList(
											new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
													.withSort(SortBuilders.fieldSort("clinicRankingCount")
															.order(SortOrder.ASC))
													.withPageable(PageRequest.of(0, 50 - response.size())).build(),
											ESLocationDocument.class);
						}
					}
				} else {
					if (city != null && location != null)
						esLocationDocuments = esLocationRepository.findByCityLocationName(city, location, searchTerm,
								true, PageRequest.of(0, 50 - response.size(), Direction.DESC, "clinicRankingCount"));
					else if (city != null)
						esLocationDocuments = esLocationRepository.findByCityLocationName(city, searchTerm, true,
								PageRequest.of(0, 50 - response.size(), Direction.DESC, "clinicRankingCount"));
					else if (location != null)
						esLocationDocuments = esLocationRepository.findByLocationLocationName(location, searchTerm,
								true, PageRequest.of(0, 50 - response.size(), Direction.DESC, "clinicRankingCount"));
				}
			} else {
				if (DPDoctorUtils.allStringsEmpty(city, location)) {
					if (latitude != null && longitude != null) {
						BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
								.filter(QueryBuilders.geoDistanceQuery("geoPoint").point(Double.parseDouble(latitude), Double.parseDouble(longitude)).distance("30km"))
								.must(QueryBuilders.matchPhrasePrefixQuery("isLocationListed", true));
						esLocationDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
								.withQuery(boolQueryBuilder).withPageable(PageRequest.of(0, 50 - response.size()))
								.withSort(SortBuilders.fieldSort("clinicRankingCount").order(SortOrder.DESC)).build(),
								ESLocationDocument.class);
					} else {
					}
					if (city != null && location != null)
						esLocationDocuments = esLocationRepository.findLocationByCityLocation(city, location, true,
								PageRequest.of(0, 50 - response.size(), Direction.DESC, "clinicRankingCount"));
					else if (city != null)
						esLocationDocuments = esLocationRepository.findLocationByCity(city, true,
								PageRequest.of(0, 50 - response.size(), Direction.DESC, "clinicRankingCount"));
					else if (location != null)
						esLocationDocuments = esLocationRepository.findLocationByLocation(location, true,
								PageRequest.of(0, 50 - response.size(), Direction.DESC, "clinicRankingCount"));
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
						appointmentSearchResponse.setSlugUrl(locationDocument.getLocationSlugUrl());
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
						appointmentSearchResponse.setSlugUrl(locationDocument.getLocationSlugUrl());
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
									.filter(QueryBuilders.geoDistanceQuery("geoPoint").point(Double.parseDouble(latitude), Double.parseDouble(longitude)).distance("30km"))
									.must(QueryBuilders.matchPhrasePrefixQuery("localeName", searchTerm))
									.must(QueryBuilders.matchPhrasePrefixQuery("isLocaleListed", true));
							esUserLocaleDocuments = elasticsearchTemplate
									.queryForList(
											new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
													.withSort(SortBuilders.fieldSort("localeRankingCount")
															.order(SortOrder.ASC))
													.withPageable(PageRequest.of(0, 50 - response.size())).build(),
											ESUserLocaleDocument.class);
						}
					}
				} else {
					BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
							.must(QueryBuilders.matchPhrasePrefixQuery("localeName", searchTerm))
							.must(QueryBuilders.matchQuery("isLocaleListed", true));
					if (city != null) {
						boolQueryBuilder.must(QueryBuilders.nestedQuery("address",
								boolQuery().must(QueryBuilders.matchQuery("address.city", city)), ScoreMode.None));
					}
					if (location != null) {
						boolQueryBuilder.must(QueryBuilders.nestedQuery("address",
								boolQuery().must(boolQuery().should(QueryBuilders.matchPhrasePrefixQuery("address.streetAddress", location))
										.should(QueryBuilders.matchPhrasePrefixQuery("address.locality", location)).minimumShouldMatch(1)), ScoreMode.None));
					}
					esUserLocaleDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
							.withSort(SortBuilders.fieldSort("localeRankingCount").order(SortOrder.ASC))
							.withQuery(boolQueryBuilder).withPageable(PageRequest.of(0, 50 - response.size())).build(),
							ESUserLocaleDocument.class);
				}
			} else {
				if (DPDoctorUtils.allStringsEmpty(city, location)) {
					if (latitude != null && longitude != null) {
						BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
								.filter(QueryBuilders.geoDistanceQuery("geoPoint").point(Double.parseDouble(latitude), Double.parseDouble(longitude)).distance("30km"))
								.must(QueryBuilders.matchPhrasePrefixQuery("isLocaleListed", true));
						esUserLocaleDocuments = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withSort(SortBuilders.fieldSort("localeRankingCount")
														.order(SortOrder.ASC))
												.withQuery(boolQueryBuilder)
												.withPageable(PageRequest.of(0, 50 - response.size())).build(),
										ESUserLocaleDocument.class);
					}
				} else {
					BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
							.must(QueryBuilders.matchQuery("isLocaleListed", true));
					if (city != null) {
						boolQueryBuilder.must(QueryBuilders.nestedQuery("address",
								boolQuery().must(QueryBuilders.matchQuery("address.city", city)), ScoreMode.None));
					}
					if (location != null) {
						boolQueryBuilder.must(QueryBuilders.nestedQuery("address",
								boolQuery().must(boolQuery().should(QueryBuilders.matchPhrasePrefixQuery("address.streetAddress", location))
										.should(QueryBuilders.matchPhrasePrefixQuery("address.locality", location)).minimumShouldMatch(1)), ScoreMode.None));
					}
					esUserLocaleDocuments = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
							.withSort(SortBuilders.fieldSort("localeRankingCount").order(SortOrder.ASC))
							.withQuery(boolQueryBuilder).withPageable(PageRequest.of(0, 50 - response.size())).build(),
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
					appointmentSearchResponse.setSlugUrl(esUserLocaleDocument.getPharmacySlugUrl());
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
								PageRequest.of(0, 50 - response.size(), Direction.ASC, "rankingCount"));
					else {
						if (latitude != null && longitude != null) {
							BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
									.filter(QueryBuilders.geoDistanceQuery("geoPoint").point(Double.parseDouble(latitude),
											Double.parseDouble(longitude)).distance("30km"))
									.must(QueryBuilders.matchPhrasePrefixQuery("firstName", searchTerm))
									.must(QueryBuilders.matchPhrasePrefixQuery("isDoctorListed", true));
							esDoctorDocuments = elasticsearchTemplate
									.queryForList(
											new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
													.withSort(
															SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
													.withPageable(PageRequest.of(0, 50 - response.size())).build(),
											ESDoctorDocument.class);
						}

					}
				} else {
					if (city != null && location != null)
						esDoctorDocuments = esDoctorRepository.findByCityLocation(city, location, searchTerm, true,
								PageRequest.of(0, 50 - response.size(), Direction.ASC, "rankingCount"));
					else if (city != null)
						esDoctorDocuments = esDoctorRepository.findByCity(city, searchTerm, true,
								PageRequest.of(0, 50 - response.size(), Direction.ASC, "rankingCount"));
					else if (location != null)
						esDoctorDocuments = esDoctorRepository.findByLocation(location, searchTerm, true,
								PageRequest.of(0, 50 - response.size(), Direction.ASC, "rankingCount"));
				}
			} else {
				if (DPDoctorUtils.allStringsEmpty(city, location)) {
					if (latitude != null && longitude != null) {
						BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
								.filter(QueryBuilders.geoDistanceQuery("geoPoint").point(Double.parseDouble(latitude),
										Double.parseDouble(longitude)).distance("30km"))
								.must(QueryBuilders.matchPhrasePrefixQuery("isDoctorListed", true));

						esDoctorDocuments = elasticsearchTemplate.queryForList(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
										.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
										.withPageable(PageRequest.of(0, 50 - response.size())).build(),
								ESDoctorDocument.class);
					}

				} else {
					if (city != null && location != null)
						esDoctorDocuments = esDoctorRepository.findByCityLocation(city, location, true,
								PageRequest.of(0, 50 - response.size(), Direction.ASC, "rankingCount"));
					else if (city != null)
						esDoctorDocuments = esDoctorRepository.findByCity(city, true,
								PageRequest.of(0, 50 - response.size(), Direction.ASC, "rankingCount"));
					else if (location != null)
						esDoctorDocuments = esDoctorRepository.findByLocation(location, true,
								PageRequest.of(0, 50 - response.size(), Direction.ASC, "rankingCount"));
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

					List<String> specialities = new ArrayList<String>();
					if (doctor.getSpecialities() != null) {
						for (String specialityId : doctor.getSpecialities()) {
							ESSpecialityDocument specialityCollection = esSpecialityRepository.findById(specialityId).orElse(null);
							if (specialityCollection != null) {
								specialities.add(specialityCollection.getSuperSpeciality());

							}
						}
						object.setSpecialities(specialities);
					}
					object.setDoctorSlugURL(doctor.getDoctorSlugURL());
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

	private List<AppointmentSearchResponse> searchService(List<AppointmentSearchResponse> response,
			String searchTerm) {
		if (response.size() < 50) {
			List<ESServicesDocument> serviceDocuments = esServicesRepository
					.findByQueryAnnotation(searchTerm);
			Set<String> esServiceset = null;
			if (serviceDocuments != null) {
				esServiceset = new HashSet<String>();
				for (ESServicesDocument esServiceDocument : serviceDocuments) {
					if (esServiceset.size() >= 50)
						break;
					esServiceset.add(esServiceDocument.getService());

				}
				for (String serviceName : esServiceset) {
					AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
					appointmentSearchResponse.setResponse(serviceName);
					appointmentSearchResponse.setResponseType(AppointmentResponseType.SERVICE);
					String slugUrl = serviceName.toLowerCase().trim().replaceAll("[^a-zA-Z0-9-]", "-");
					
					slugUrl = slugUrl.replaceAll("-*-","-");	
					if(slugUrl.endsWith("-")) {
						slugUrl = slugUrl.substring(0, slugUrl.length()-1);
					}
					appointmentSearchResponse.setSlugUrl("doctors-for-"+slugUrl);
					response.add(appointmentSearchResponse);
				}
			}
		}

		return response;
	}

	private List<AppointmentSearchResponse> searchSymptomDiseaseCondition(List<AppointmentSearchResponse> response,
			String searchTerm) {
		if (response.size() < 50) {
			List<ESSymptomDiseaseConditionDocument> symptomDiseaseConditionDocuments = esSymptomDiseaseConditionRepository.findByQueryAnnotation(searchTerm);
			Set<String> esSymptomDiseaseConditionSet = null;
			if (symptomDiseaseConditionDocuments != null) {
				esSymptomDiseaseConditionSet = new HashSet<String>();
				for (ESSymptomDiseaseConditionDocument esSymptomDiseaseConditionDocument : symptomDiseaseConditionDocuments) {
					if (esSymptomDiseaseConditionSet.size() >= 50)
						break;
					esSymptomDiseaseConditionSet.add(esSymptomDiseaseConditionDocument.getName());

					AppointmentSearchResponse appointmentSearchResponse = new AppointmentSearchResponse();
					appointmentSearchResponse.setResponse(esSymptomDiseaseConditionDocument.getName());
					
					if(esSymptomDiseaseConditionDocument.getType() != null) {
						if(esSymptomDiseaseConditionDocument.getType().equalsIgnoreCase("SYMPTOM"))
							appointmentSearchResponse.setResponseType(AppointmentResponseType.SYMPTOM);
						else if(esSymptomDiseaseConditionDocument.getType().equalsIgnoreCase("DISEASE"))
							appointmentSearchResponse.setResponseType(AppointmentResponseType.DISEASE);
						else if(esSymptomDiseaseConditionDocument.getType().equalsIgnoreCase("CONDITION"))
							appointmentSearchResponse.setResponseType(AppointmentResponseType.CONDITION);

					}
					String slugUrl = esSymptomDiseaseConditionDocument.getName().toLowerCase().trim().replaceAll("[^a-zA-Z0-9-]", "-");
					
					slugUrl = slugUrl.replaceAll("-*-","-");
					if(slugUrl.endsWith("-")) {
						slugUrl = slugUrl.substring(0, slugUrl.length()-1);
					}
					appointmentSearchResponse.setSlugUrl("treatments-for-"+slugUrl);
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
	// searchSymptoms(List<AppointmentSearchResponse> response,
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
				String slugUrl = speciality.getSuperSpeciality().toLowerCase().trim().replaceAll("[^a-zA-Z0-9-]", "-");
				slugUrl = slugUrl.replaceAll("-*-","-");
				appointmentSearchResponse.setSlugUrl(slugUrl);
				response.add(appointmentSearchResponse);
			}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ESDoctorDocument> getDoctors(int page, int size, String city, String location, String latitude,
			String longitude, String speciality, String symptom, Boolean booking, Boolean calling, int minFee,
			int maxFee, int minTime, int maxTime, List<String> days, String gender, int minExperience,
			int maxExperience, String service) {
		List<ESDoctorDocument> esDoctorDocuments = null;
		List<ESDoctorDocument> response = null;
		if (size == 0)
			size = 10;
		try {

			if (DPDoctorUtils.anyStringEmpty(longitude, latitude) && !DPDoctorUtils.anyStringEmpty(city)) {
				city = city.trim().replace("-", " ");
				ESCityDocument esCityDocument = esCityRepository.findByName(city);
				if (esCityDocument != null) {
					latitude = esCityDocument.getLatitude() + "";
					longitude = esCityDocument.getLongitude() + "";
				} else {
					return esDoctorDocuments;
				}
			}
			if (DPDoctorUtils.anyStringEmpty(longitude, latitude))
				return esDoctorDocuments;

			QueryBuilder specialityQueryBuilder = createSpecialityFilter(speciality);
			QueryBuilder serviceQueryBuilder = createServiceFilter(service);
			QueryBuilder facilityQueryBuilder = createFacilityBuilder(booking, calling);
			Integer distance = 4;
			String citylongitude = null, citylatitude = null;
			do {

				BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
						.must(QueryBuilders.matchQuery("isDoctorListed", true))
						.must(QueryBuilders.matchQuery("isClinic", true));
				if (specialityQueryBuilder != null)
					boolQueryBuilder.must(specialityQueryBuilder);
				if (serviceQueryBuilder != null)
					boolQueryBuilder.must(serviceQueryBuilder);
				if (facilityQueryBuilder != null)
					boolQueryBuilder.must(facilityQueryBuilder);

				if (!DPDoctorUtils.anyStringEmpty(location)) {
					boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("locationName", location));
				}

				createConsultationFeeFilter(boolQueryBuilder, maxFee, minFee);
				createExperienceFilter(boolQueryBuilder, maxExperience, minExperience);
				if (!DPDoctorUtils.anyStringEmpty(gender)) {
					boolQueryBuilder.must(QueryBuilders.matchQuery("gender", gender));
				}

				createTimeFilter(boolQueryBuilder, maxTime, minTime, days);

				if (!DPDoctorUtils.allStringsEmpty(city)) {
					citylatitude = latitude;
					citylongitude = longitude;
					boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint").point(Double.parseDouble(latitude),
							Double.parseDouble(longitude)).distance("30km"));

				} else if (!DPDoctorUtils.anyStringEmpty(latitude) && !DPDoctorUtils.anyStringEmpty(longitude)) {
					boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint").point(Double.parseDouble(latitude),
							Double.parseDouble(longitude)).distance(distance + "km"));
					distance = distance + 26;
				}

				SearchQuery searchQuery = null;
				if (size > 0)
					searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
							.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
							.withPageable(PageRequest.of(page, size)).build();
				else
					searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
							.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC)).build();

				response = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
				if (response != null && esDoctorDocuments == null)
					esDoctorDocuments = new ArrayList<ESDoctorDocument>();
				if (esDoctorDocuments != null) {
					esDoctorDocuments.addAll(response);
					if (size > 0) {
						size = size - esDoctorDocuments.size();
						if (size == 0)
							break;
					}
				}

			} while (citylatitude == null && citylongitude == null && distance <= 30
					&& esDoctorDocuments.size() < size);

			if (esDoctorDocuments != null) {

				for (ESDoctorDocument doctorDocument : esDoctorDocuments) {

					if (doctorDocument.getSpecialities() != null) {
						Iterable<ESSpecialityDocument> specialities = esSpecialityRepository.findAllById(doctorDocument.getSpecialities());
						if(specialities != null) {
							doctorDocument.setSpecialities((List<String>)CollectionUtils.collect(specialities.iterator(), new BeanToPropertyValueTransformer("superSpeciality")));
						}
					}

					if (doctorDocument.getServices() != null) {
						Iterable<ESServicesDocument> services = esServicesRepository.findAllById(doctorDocument.getServices());
						if(services != null) {
							doctorDocument.setServices((List<String>)CollectionUtils.collect(services.iterator(), new BeanToPropertyValueTransformer("service")));
						}					
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
							+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getState())
									? doctorDocument.getState() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getCountry())
									? doctorDocument.getCountry() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(doctorDocument.getPostalCode())
									? doctorDocument.getPostalCode()
									: "");

					if (!DPDoctorUtils.anyStringEmpty(address)) {
						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}
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

	@SuppressWarnings("unchecked")
	private QueryBuilder createServiceFilter(String service) {
		QueryBuilder queryBuilder = null;
		if (!DPDoctorUtils.anyStringEmpty(service)) {
			
			List<ESServicesDocument> esServicesDocuments = esServicesRepository.findByQueryAnnotation(service);
			
			if (esServicesDocuments != null) {
				Collection<String> serviceIds = CollectionUtils.collect(esServicesDocuments,
						new BeanToPropertyValueTransformer("id"));
				if (serviceIds == null)
					serviceIds = CollectionUtils.EMPTY_COLLECTION;
				queryBuilder = QueryBuilders.termsQuery("services", serviceIds);
			}
		}
		return queryBuilder;
	}

	@Override
	public Integer getDoctorCount(String city, String location, String latitude, String longitude, String speciality,
			String symptom, Boolean booking, Boolean calling, int minFee, int maxFee, int minTime, int maxTime,
			List<String> days, String gender, int minExperience, int maxExperience, String service) {
		Integer response = 0;
		try {

			if (DPDoctorUtils.anyStringEmpty(longitude, latitude) && !DPDoctorUtils.anyStringEmpty(city)) {
				city = city.trim().replace("-", " ");
				ESCityDocument esCityDocument = esCityRepository.findByName(city);
				if (esCityDocument != null) {
					latitude = esCityDocument.getLatitude() + "";
					longitude = esCityDocument.getLongitude() + "";
				} else {
					return 0;
				}
			}

			Set<String> specialityIdSet = new HashSet<String>();
			Set<String> locationIds = null, doctorIds = null;

			QueryBuilder specialityQueryBuilder = createSpecialityFilter(speciality);
			QueryBuilder serviceQueryBuilder = createServiceFilter(service);
			QueryBuilder facilityQueryBuilder = createFacilityBuilder(booking, calling);
			Integer distance = 4;
			String citylongitude = null, citylatitude = null;
			do {
				BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
						.must(QueryBuilders.matchQuery("isDoctorListed", true))
						.must(QueryBuilders.matchQuery("isClinic", true));
				if (specialityIdSet != null && !specialityIdSet.isEmpty()) {
					boolQueryBuilder.must(QueryBuilders.termsQuery("specialities", specialityIdSet));
				}

				if ((locationIds != null && !locationIds.isEmpty()) && (doctorIds != null && !doctorIds.isEmpty())) {
					boolQueryBuilder.must(QueryBuilders.termsQuery("userId", doctorIds))
							.must(QueryBuilders.termsQuery("locationId", locationIds));
				}

				/*
				 * <<<<<<< HEAD if (!DPDoctorUtils.anyStringEmpty(symptom)) {
				 * List<ESComplaintsDocument> esComplaintsDocuments =
				 * esComplaintsRepository.findByComplaint(symptom); if (esComplaintsDocuments ==
				 * null || esComplaintsDocuments.isEmpty()) { return null; } Set<String>
				 * locationIds = new HashSet<>(CollectionUtils.collect(esComplaintsDocuments,
				 * new BeanToPropertyValueTransformer("locationId"))); Set<String> doctorIds =
				 * new HashSet<>( CollectionUtils.collect(esComplaintsDocuments, new
				 * BeanToPropertyValueTransformer("doctorId")));
				 * 
				 * locationIds.remove(null); doctorIds.remove(null); if ((locationIds == null ||
				 * locationIds.isEmpty()) && (doctorIds == null || doctorIds.isEmpty())) {
				 * return null; } boolQueryBuilder.must(QueryBuilders.termsQuery("userId",
				 * doctorIds)) .must(QueryBuilders.termsQuery("locationId", locationIds)); }
				 * ======= if (!DPDoctorUtils.anyStringEmpty(symptom)) {
				 * List<ESComplaintsDocument> esComplaintsDocuments =
				 * esComplaintsRepository.findByComplaint(symptom); if (esComplaintsDocuments ==
				 * null || esComplaintsDocuments.isEmpty()) { return null; } Set<String>
				 * locationIds = new HashSet<>(CollectionUtils.collect(esComplaintsDocuments,
				 * new BeanToPropertyValueTransformer("locationId"))); Set<String> doctorIds =
				 * new HashSet<>( CollectionUtils.collect(esComplaintsDocuments, new
				 * BeanToPropertyValueTransformer("doctorId")));
				 * 
				 * locationIds.remove(null); doctorIds.remove(null); if ((locationIds == null ||
				 * locationIds.isEmpty()) && (doctorIds == null || doctorIds.isEmpty())) {
				 * return null; } boolQueryBuilder.must(QueryBuilders.termsQuery("userId",
				 * doctorIds)) .must(QueryBuilders.termsQuery("locationId", locationIds)); }
				 * >>>>>>> e5408604ee47a0585cf6f3af38163a2902fe3750
				 */

				if (specialityQueryBuilder != null)
					boolQueryBuilder.must(specialityQueryBuilder);
				if (serviceQueryBuilder != null)
					boolQueryBuilder.must(serviceQueryBuilder);
				if (facilityQueryBuilder != null)
					boolQueryBuilder.must(facilityQueryBuilder);

				if (!DPDoctorUtils.anyStringEmpty(location)) {
					boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("locationName", location));
				}

				createConsultationFeeFilter(boolQueryBuilder, maxFee, minFee);
				createExperienceFilter(boolQueryBuilder, maxExperience, minExperience);
				if (!DPDoctorUtils.anyStringEmpty(gender)) {
					boolQueryBuilder.must(QueryBuilders.matchQuery("gender", gender));
				}

				createTimeFilter(boolQueryBuilder, maxTime, minTime, days);

				if (!DPDoctorUtils.allStringsEmpty(city)) {
					citylatitude = latitude;
					citylongitude = longitude;
					boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint").point(Double.parseDouble(latitude),
							Double.parseDouble(longitude)).distance("30km"));

				} else if (!DPDoctorUtils.anyStringEmpty(latitude) && !DPDoctorUtils.anyStringEmpty(longitude)) {
					boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint").point(Double.parseDouble(latitude),
							Double.parseDouble(longitude)).distance(distance + "km"));
					distance = distance + 26;
				}

				SearchQuery searchQuery = null;

				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC)).build();

				response = (int) elasticsearchTemplate.count(searchQuery, ESDoctorDocument.class);

			} while (citylatitude == null && citylongitude == null && distance <= 30 && response < 10);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error While Getting Doctor count From ES : " + e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("deprecation")
	private void createTimeFilter(BoolQueryBuilder boolQueryBuilder, int maxTime, int minTime, List<String> days) {
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
			
		}
	}

	private void createExperienceFilter(BoolQueryBuilder boolQueryBuilder, int maxExperience, int minExperience) {
		if (minExperience != 0 && maxExperience != 0)
			boolQueryBuilder.must(boolQuery().should(QueryBuilders.nestedQuery("experience",
													boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(minExperience).to(maxExperience)), ScoreMode.None))
											.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("experience", QueryBuilders.existsQuery("experience"), ScoreMode.None)))
											.minimumShouldMatch(1));

		else if (minExperience != 0)
			boolQueryBuilder
					.must(boolQuery().should(QueryBuilders.nestedQuery("experience",
											boolQuery().must(QueryBuilders.rangeQuery("experience.experience")
													.from(minExperience)), ScoreMode.None))
									.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("experience",
											QueryBuilders.existsQuery("experience"), ScoreMode.None)))
									.minimumShouldMatch(1));

		else if (maxExperience != 0)
			boolQueryBuilder
					.must(boolQuery().should(
							QueryBuilders.nestedQuery("experience",
									boolQuery().must(QueryBuilders.rangeQuery("experience.experience").from(0)
											.to(maxExperience)), ScoreMode.None))
							.should(QueryBuilders.boolQuery().mustNot(
									QueryBuilders.nestedQuery("experience", QueryBuilders.existsQuery("experience"), ScoreMode.None)))
							.minimumShouldMatch(1));
	}

	private void createConsultationFeeFilter(BoolQueryBuilder boolQueryBuilder, int maxFee, int minFee) {
		if (minFee != 0 && maxFee != 0)
			boolQueryBuilder
					.must(boolQuery().should(
							QueryBuilders.nestedQuery("consultationFee",
									boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(minFee)
											.to(maxFee)), ScoreMode.None))
							.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
									QueryBuilders.existsQuery("consultationFee"), ScoreMode.None)))
							.minimumShouldMatch(1));
		

		else if (minFee != 0)
			boolQueryBuilder
					.must(boolQuery()
							.should(QueryBuilders.nestedQuery("consultationFee",
											boolQuery().must(
													QueryBuilders.rangeQuery("consultationFee.amount").from(minFee)), ScoreMode.None))
							.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
											QueryBuilders.existsQuery("consultationFee"), ScoreMode.None)))
							.minimumShouldMatch(1));
		else if (maxFee != 0)
			boolQueryBuilder
					.must(boolQuery()
							.should(
									QueryBuilders.nestedQuery("consultationFee",
											boolQuery().must(QueryBuilders.rangeQuery("consultationFee.amount").from(0)
													.to(maxFee)), ScoreMode.None))
							.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("consultationFee",
											QueryBuilders.existsQuery("consultationFee"), ScoreMode.None)))
							.minimumShouldMatch(1));
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

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<LabResponse> getLabs(int page, int size, String city, String location, String latitude,
			String longitude, String test, Boolean booking, Boolean calling, int minTime, int maxTime,
			List<String> days, Boolean onlineReports, Boolean homeService, Boolean nabl) {
		List<LabResponse> response = null;
		List<ESLabTestDocument> esLabTestDocuments = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(longitude, latitude) && !DPDoctorUtils.anyStringEmpty(city)) {
				city.trim().replace("-", " ");
				ESCityDocument esCityDocument = esCityRepository.findByName(city);
				if (esCityDocument != null) {

					latitude = esCityDocument.getLatitude() + "";
					longitude = esCityDocument.getLongitude() + "";
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(test)) {
				List<ESDiagnosticTestDocument> diagnosticTests = esDiagnosticTestRepository.findByTestName(test);
				if (diagnosticTests != null) {
					Collection<String> testIds = CollectionUtils.collect(diagnosticTests,
							new BeanToPropertyValueTransformer("id"));
					int count = (int) elasticsearchTemplate.count(new CriteriaQuery(new Criteria("testId").in(testIds)),
							ESLabTestDocument.class);
					if (count > 0)
						esLabTestDocuments = elasticsearchTemplate.queryForList(
								new NativeSearchQueryBuilder().withQuery(QueryBuilders.termsQuery("testId", testIds))
										.withPageable(PageRequest.of(0, count)).build(),
								ESLabTestDocument.class);
				}
			}
			List<ESLocationDocument> esLocationDocuments = null;
			Collection<String> locationIds = null;

			if (esLabTestDocuments != null && !esLabTestDocuments.isEmpty()) {
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
					.must(QueryBuilders.nestedQuery("clinicWorkingSchedules",
							boolQuery().must(boolQuery().should(
									QueryBuilders.nestedQuery("clinicWorkingSchedules.workingHours", 
											boolQuery().should(QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.toTime").gt(minTime).lt(maxTime))
													   .should(QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.fromTime").gt(minTime).lt(maxTime))
													   .should(boolQuery().should(QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.toTime").gt(maxTime).lt(1439))
															              .should(QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.fromTime").gt(0).lt(minTime))
															              .minimumShouldMatch(2))
													   .minimumShouldMatch(1),
											ScoreMode.None))
									                 .should(QueryBuilders.termsQuery("clinicWorkingSchedules.workingDay", days)).minimumShouldMatch(2)), ScoreMode.None));
					
				} else {
					boolQueryBuilder.must(QueryBuilders.nestedQuery("clinicWorkingSchedules",
							boolQuery().must(QueryBuilders.termsQuery("clinicWorkingSchedules.workingDay", days)), ScoreMode.None));
				}
			} else {
				if (maxTime != 0 || minTime != 0) {
					if (maxTime == 0) {
						maxTime = 1439;
					}
					boolQueryBuilder.must(QueryBuilders.nestedQuery("clinicWorkingSchedules.workingHours", 
							boolQuery().should(QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.toTime").gt(minTime).lt(maxTime))
							   .should(QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.fromTime").gt(minTime).lt(maxTime))
							   .should(boolQuery().should(QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.toTime").gt(maxTime).lt(1439))
									              .should(QueryBuilders.rangeQuery("clinicWorkingSchedules.workingHours.fromTime").gt(0).lt(minTime))
									              .minimumShouldMatch(2))
							   .minimumShouldMatch(1),
					                      ScoreMode.None));
				}
			}
			boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint").point(Double.parseDouble(latitude), Double.parseDouble(longitude)).distance("30km"));

			SearchQuery searchQuery = null;
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(PageRequest.of(page, size))
						.withSort(SortBuilders.geoDistanceSort("geoPoint", Double.parseDouble(latitude), Double.parseDouble(longitude)).order(SortOrder.ASC)
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
							? document.getStreetAddress() + ", "
							: "")
							+ (!DPDoctorUtils.anyStringEmpty(document.getLandmarkDetails())
									? document.getLandmarkDetails() + ", "
									: "")
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
				city.trim().replace("-", " ");
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
						boolQuery().must(QueryBuilders.termsQuery("localeWorkingSchedules.workingDay", days)), ScoreMode.None));
			}
			if (pharmacyType != null && !pharmacyType.isEmpty()) {
				for (String type : pharmacyType)
					boolQueryBuilder.should(QueryBuilders.matchQuery("pharmacyType", type.toUpperCase()));

				boolQueryBuilder.minimumShouldMatch(1);
			}

			if (maxTime == 0) {
				maxTime = 86399999;
				boolQueryBuilder
						.must(boolQuery().should(
								QueryBuilders.nestedQuery("localeWorkingSchedules",
										boolQuery().must(QueryBuilders.nestedQuery("localeWorkingSchedules.workingHours",
												boolQuery().must(boolQuery().should(

														QueryBuilders
																.rangeQuery(
																		"localeWorkingSchedules.workingHours.toTime")
																.gt(minTime).lt(maxTime))
														.should(QueryBuilders
																.rangeQuery(
																		"localeWorkingSchedules.workingHours.fromTime")
																.gt(minTime).lt(maxTime)).minimumShouldMatch(1)), ScoreMode.None)), ScoreMode.None))
								.should(QueryBuilders.boolQuery()
										.mustNot(QueryBuilders.existsQuery("localeWorkingSchedules"))).minimumShouldMatch(1));

			} else {
				boolQueryBuilder
						.must(QueryBuilders
								.nestedQuery("localeWorkingSchedules",
										boolQuery().must(QueryBuilders.nestedQuery("localeWorkingSchedules.workingHours",
												boolQuery().must(boolQuery().should(

														QueryBuilders
																.rangeQuery(
																		"localeWorkingSchedules.workingHours.toTime")
																.gt(minTime).lt(maxTime))

														.should(QueryBuilders
																.rangeQuery(
																		"localeWorkingSchedules.workingHours.fromTime")
																.gt(minTime).lt(maxTime)).minimumShouldMatch(1)), ScoreMode.None)), ScoreMode.None));
			}
			if (latitude != null && longitude != null) {
				boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint")
						.point(Double.parseDouble(latitude), Double.parseDouble(longitude)).distance("30km"));
			}

			SearchQuery searchQuery = null;
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.geoDistanceSort("geoPoint", Double.parseDouble(latitude), Double.parseDouble(longitude)).order(SortOrder.ASC)
								.unit(DistanceUnit.KILOMETERS))
						.withSort(SortBuilders.fieldSort("localeRankingCount").order(SortOrder.ASC))
						.withPageable(PageRequest.of(page, size)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("localeRankingCount").order(SortOrder.ASC)).build();

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
										? esUserLocaleDocument.getAddress().getStreetAddress() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(esUserLocaleDocument.getAddress().getLocality())
										? esUserLocaleDocument.getAddress().getLocality() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(esUserLocaleDocument.getAddress().getCity())
										? esUserLocaleDocument.getAddress().getCity() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(esUserLocaleDocument.getAddress().getState())
										? esUserLocaleDocument.getAddress().getState() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(esUserLocaleDocument.getAddress().getCountry())
										? esUserLocaleDocument.getAddress().getCountry() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(esUserLocaleDocument.getAddress().getPostalCode())
										? esUserLocaleDocument.getAddress().getPostalCode()
										: "");

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
	public ESWEBResponse getDoctorForWeb(int page, int size, String city, String location, String latitude,
			String longitude, String speciality, String symptom, Boolean booking, Boolean calling, int minFee,
			int maxFee, int minTime, int maxTime, List<String> days, String gender, int minExperience,
			int maxExperience, String service, String locality) {
		ESWEBResponse doctorResponse = null;
		try {
			if (!DPDoctorUtils.allStringsEmpty(locality) && !locality.equalsIgnoreCase("undefined")) {
				locality = locality.trim().replace("-", " ");
				List<ESLandmarkLocalityDocument> localities = esLandmarkLocalityRepository.findByLocality(locality,
						PageRequest.of(0, 1));
				if (localities != null && !localities.isEmpty()) {
					latitude = localities.get(0).getLatitude() != null ? localities.get(0).getLatitude().toString()
							: null;
				}
				if (localities != null && !localities.isEmpty()) {
					longitude = localities.get(0).getLongitude() != null ? localities.get(0).getLongitude().toString()
							: null;
				}
			}
			if (DPDoctorUtils.allStringsEmpty(speciality) || speciality.equalsIgnoreCase("undefined")) {
				speciality = null;
			} else {
				speciality = speciality.replaceAll("-", " ");
			}
			if (DPDoctorUtils.allStringsEmpty(service) || service.equalsIgnoreCase("undefined")) {
				service = null;
			} else {
				service = service.replaceAll("-", " ");
			}
			
			List<ESDoctorDocument> doctors = getDoctors(page, size, city, location, latitude, longitude, speciality,
					symptom, booking, calling, minFee, maxFee, minTime, maxTime, days, gender, minExperience,
					maxExperience, service);
			doctorResponse = new ESWEBResponse();
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
				doctorResponse.setSpeciality(speciality.replace(" ", "-"));

				doctorResponse.setMetaData(StringUtils.capitalize(speciality) + "s in ");
			} else {
				doctorResponse.setMetaData("Doctors in ");
				doctorResponse.setSpeciality("ALL Specialities");

			}
			if (!DPDoctorUtils.allStringsEmpty(locality) && !locality.equalsIgnoreCase("undefined")) {

				doctorResponse.setMetaData(doctorResponse.getMetaData() + StringUtils.capitalize(locality) + ", ");
				doctorResponse.setLocality(StringUtils.capitalize(locality));
			}
			if (DPDoctorUtils.anyStringEmpty(city)) {
				city = "Nagpur";
			}
			doctorResponse.setMetaData(doctorResponse.getMetaData() + StringUtils.capitalize(city));
			doctorResponse.setCity(city);
			doctorResponse.setCount(getDoctorCount(city, location, latitude, longitude, speciality, symptom, booking,
					calling, minFee, maxFee, minTime, maxTime, days, gender, minExperience, maxExperience, service));

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error While Getting Doctor Details From ES for Web : " + e.getMessage());

		}
		return doctorResponse;
	}

	@Override
	@Transactional
	public ESWEBResponse getPharmacyForWeb(int page, int size, String city, String localeName, String latitude,
			String longitude, String paymentType, Boolean homeService, Boolean isTwentyFourSevenOpen, long minTime,
			long maxTime, List<String> days, List<String> pharmacyType, Boolean isGenericMedicineAvailable,
			String locality) {
		ESWEBResponse response = null;
		try {

			if (!DPDoctorUtils.allStringsEmpty(locality) && !locality.equalsIgnoreCase("undefined")) {
				List<ESLandmarkLocalityDocument> localities = esLandmarkLocalityRepository.findByLocality(locality,
						PageRequest.of(0, 1));
				if (localities != null && !localities.isEmpty()) {
					latitude = localities.get(0).getLatitude() != null ? localities.get(0).getLatitude().toString()
							: null;
				}
				if (localities != null && !localities.isEmpty()) {
					longitude = localities.get(0).getLongitude() != null ? localities.get(0).getLongitude().toString()
							: null;
				}
			}
			List<ESUserLocaleDocument> pharmacies = getPharmacies(page, size, city, localeName, latitude, longitude,
					paymentType, homeService, isTwentyFourSevenOpen, minTime, maxTime, days, pharmacyType,
					isGenericMedicineAvailable);
			response = new ESWEBResponse();
			if (pharmacies != null && !pharmacies.isEmpty()) {
				response.setPharmacies(pharmacies);
			}
			response.setCity(city);
			response.setMetaData("pharmacy in " + city);
			if (!pharmacyType.isEmpty() && pharmacyType != null) {
				for (String type : pharmacyType) {
					if (!DPDoctorUtils.anyStringEmpty(type)) {
						response.setMetaData(type + "," + response);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error While Getting Pharmacy Details From ES for Web : " + e.getMessage());

		}
		return response;
	}

	@Override
	@Transactional
	public ESWEBResponse getLabForWeb(int page, int size, String city, String location, String latitude,
			String longitude, String test, Boolean booking, Boolean calling, int minTime, int maxTime,
			List<String> days, Boolean onlineReports, Boolean homeService, Boolean nabl, String locality) {
		ESWEBResponse response = null;
		try {
			if (!DPDoctorUtils.allStringsEmpty(locality) && !locality.equalsIgnoreCase("undefined")) {
				List<ESLandmarkLocalityDocument> localities = esLandmarkLocalityRepository.findByLocality(locality,
						PageRequest.of(0, 1));
				if (localities != null && !localities.isEmpty()) {
					latitude = localities.get(0).getLatitude() != null ? localities.get(0).getLatitude().toString()
							: null;
				}
				if (localities != null && !localities.isEmpty()) {
					longitude = localities.get(0).getLongitude() != null ? localities.get(0).getLongitude().toString()
							: null;
				}
			}
			List<LabResponse> labs = getLabs(page, size, city, location, latitude, longitude, test, booking, calling,
					minTime, maxTime, days, onlineReports, homeService, nabl);
			response = new ESWEBResponse();
			if (labs != null && !labs.isEmpty()) {
				response.setLabs(labs);
			}
			response.setMetaData("lab in " + city);
			response.setCity(city);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error While Getting lab Details From ES for Web : " + e.getMessage());

		}
		return response;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public List<ESDoctorCardResponse> getDoctorsShortCard(int page, int size, String city, String location,
			String latitude, String longitude, String speciality, String searchTerm) {
		List<ESDoctorDocument> esDoctorDocuments = null;
		List<ESDoctorCardResponse> esDoctorCardResponses = null;
		try {

			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.matchQuery("isClinic", true));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("firstName", searchTerm));
			}
			if (DPDoctorUtils.anyStringEmpty(longitude, latitude) && !DPDoctorUtils.anyStringEmpty(city)) {
				city.trim().replace("-", " ");
				ESCityDocument esCityDocument = esCityRepository.findByName(city);
				if (esCityDocument != null) {
					latitude = esCityDocument.getLatitude() + "";
					longitude = esCityDocument.getLongitude() + "";
				}
			}
			if (!DPDoctorUtils.anyStringEmpty(speciality)) {
				if (speciality.equalsIgnoreCase("GYNECOLOGIST")) {
					speciality = "GYNAECOLOGIST";
				}
				List<ESSpecialityDocument> esSpecialityDocuments = esSpecialityRepository
						.findByQueryAnnotation(speciality);
				if (speciality.equalsIgnoreCase("GENERAL PHYSICIAN")) {
					speciality = "FAMILY PHYSICIAN";
				} else if (speciality.equalsIgnoreCase("FAMILY PHYSICIAN")) {
					speciality = "GENERAL PHYSICIAN";
				}
				List<ESSpecialityDocument> esSpecialityDocuments2 = new LinkedList<ESSpecialityDocument>(
						esSpecialityDocuments);

				if (speciality.equalsIgnoreCase("GENERAL PHYSICIAN")
						|| speciality.equalsIgnoreCase("FAMILY PHYSICIAN")) {

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
					boolQueryBuilder.must(QueryBuilders.termsQuery("specialities", specialityIds));
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(location)) {
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("locationName", location));
			}

			if (latitude != null && longitude != null)
				boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint").point(Double.parseDouble(latitude), Double.parseDouble(longitude)).distance("30km"));

			SearchQuery searchQuery = null;
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						// .withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
						.withSort(SortBuilders.geoDistanceSort("geoPoint", Double.parseDouble(latitude), Double.parseDouble(longitude)).order(SortOrder.ASC)
								.unit(DistanceUnit.KILOMETERS))
						.withPageable(PageRequest.of(page, size)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						// .withSort(SortBuilders.fieldSort("rankingCount").order(SortOrder.ASC))
						.withSort(SortBuilders.geoDistanceSort("geoPoint", Double.parseDouble(latitude), Double.parseDouble(longitude)).order(SortOrder.ASC)
								.unit(DistanceUnit.KILOMETERS))
						.build();

			esDoctorDocuments = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);

			if (esDoctorDocuments != null) {
				esDoctorCardResponses = new ArrayList<ESDoctorCardResponse>();
				// Collections.sort(esDoctorDocuments);
				List<String> specialities = null;
				for (ESDoctorDocument doctorDocument : esDoctorDocuments) {

					if (doctorDocument.getSpecialities() != null) {
						specialities = new ArrayList<String>();
						for (String specialityId : doctorDocument.getSpecialities()) {
							ESSpecialityDocument specialityCollection = esSpecialityRepository.findById(specialityId).orElse(null);
							if (specialityCollection != null) {
								specialities.add(specialityCollection.getSuperSpeciality());

							}
						}
						doctorDocument.setSpecialities(specialities);
					}

					ESDoctorCardResponse doctorCardResponse = new ESDoctorCardResponse();
					BeanUtil.map(doctorDocument, doctorCardResponse);
					esDoctorCardResponses.add(doctorCardResponse);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error While Getting Doctor Details From ES : " + e.getMessage());
		}
		return esDoctorCardResponses;
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
				
				size = size - response.size();
				SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(PageRequest.of(page, size)).build();
				
				List<ESLandmarkLocalityDocument> esLandmarkLocalityDocuments = elasticsearchTemplate.queryForList(searchQuery, ESLandmarkLocalityDocument.class);
				if(esLandmarkLocalityDocuments != null) {
					//response = new ArrayList<SearchLandmarkLocalityResponse>();
					
					for(ESLandmarkLocalityDocument document : esLandmarkLocalityDocuments) {
						searchLandmarkLocalityResponse = new SearchLandmarkLocalityResponse();
						BeanUtil.map(document, searchLandmarkLocalityResponse);
						searchLandmarkLocalityResponse.setCity(cityMap.get(document.getCityId()));
						if(DPDoctorUtils.anyStringEmpty(searchLandmarkLocalityResponse.getCity())) {
							ESCityDocument esCityDocument = esCityRepository.findById(document.getCityId()).orElse(null);
							cityMap.put(esCityDocument.getId(), esCityDocument.getCity());
							searchLandmarkLocalityResponse.setCity(esCityDocument.getCity());
						}
						
						if(!DPDoctorUtils.anyStringEmpty(document.getLocality()))searchLandmarkLocalityResponse.setName(document.getLocality());
						else if(!DPDoctorUtils.anyStringEmpty(document.getLandmark()))searchLandmarkLocalityResponse.setName(document.getLandmark());
						
						String slugUrl = searchLandmarkLocalityResponse.getName().toLowerCase().trim().replaceAll("[^a-zA-Z0-9-]", "-");
						
						slugUrl = slugUrl.replaceAll("-*-","-");	
						searchLandmarkLocalityResponse.setName(searchLandmarkLocalityResponse.getName()+", "+searchLandmarkLocalityResponse.getCity());
						searchLandmarkLocalityResponse.setResponseType("LOCALITY");
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
}
