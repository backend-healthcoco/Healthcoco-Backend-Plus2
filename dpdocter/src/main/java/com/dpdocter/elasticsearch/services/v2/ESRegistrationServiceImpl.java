package com.dpdocter.elasticsearch.services.v2;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.beans.AdvancedSearch;
import com.dpdocter.elasticsearch.beans.AdvancedSearchParameter;
import com.dpdocter.elasticsearch.beans.DoctorLocation;
import com.dpdocter.elasticsearch.document.ESCollectionBoyDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESLocationDocument;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.elasticsearch.repository.ESCollectionBoyRepository;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESLocationRepository;
import com.dpdocter.elasticsearch.repository.ESPatientRepository;
import com.dpdocter.elasticsearch.repository.ESReferenceRepository;
import com.dpdocter.elasticsearch.response.v2.ESPatientResponse;
import com.dpdocter.elasticsearch.response.v2.ESPatientResponseDetails;
import com.dpdocter.enums.AdvancedSearchType;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service(value = "ESRegistrationServiceImplV2")
public class ESRegistrationServiceImpl implements ESRegistrationService {

	private static Logger logger = Logger.getLogger(ESRegistrationServiceImpl.class.getName());

	@Autowired
	private ESDoctorRepository esDoctorRepository;

	@Autowired
	private ESLocationRepository esLocationRepository;

	@Autowired
	private ESPatientRepository esPatientRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ESCollectionBoyRepository esCollectionBoyRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private ESReferenceRepository esReferenceRepository;

	@Override
	public boolean addPatient(ESPatientDocument request) {
		boolean response = false;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getLocalPatientName())) {
				String localPatientNameFormatted = request.getLocalPatientName().replaceAll("[^a-zA-Z0-9]", "");
				request.setLocalPatientNameFormatted(localPatientNameFormatted.toLowerCase());
			}

			esPatientRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getUserId()), Resource.PATIENT, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Patient");
		}
		return response;
	}

	@Override
	public ESPatientResponseDetails searchPatient(String locationId, String hospitalId, String searchTerm, int page,
			int size, String doctorId, String role) {

		List<ESPatientDocument> patients = new ArrayList<ESPatientDocument>();
		List<ESPatientResponse> patientsResponse = null;
		ESPatientResponseDetails patientResponseDetails = null;
		try {
//			AdvancedSearchType advancedSearchTypeForPID = AdvancedSearchType.PID;
//			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));
//			if(locationCollection != null && locationCollection.getIsPidHasDate()!= null) {
//				if(!locationCollection.getIsPidHasDate())advancedSearchTypeForPID = AdvancedSearchType.PNUM;
//			}
			searchTerm = searchTerm.toLowerCase();
			String patientName = searchTerm.replaceAll("[^a-zA-Z0-9]", "");
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.termQuery("locationId", locationId))
					.must(QueryBuilders.termQuery("hospitalId", hospitalId))
					.mustNot(QueryBuilders.termQuery("isPatientDiscarded", true))

					.should(QueryBuilders.queryStringQuery("localPatientNameFormatted:" + patientName + "*").boost(4))
					.should(QueryBuilders
							.matchPhrasePrefixQuery(AdvancedSearchType.EMAIL_ADDRESS.getSearchType(), searchTerm)
							.boost(1.3f))
					.should(QueryBuilders
							.matchPhrasePrefixQuery(AdvancedSearchType.MOBILE_NUMBER.getSearchType(), searchTerm)
							.boost(1.2f))
					.should(QueryBuilders.matchPhrasePrefixQuery(AdvancedSearchType.PID.getSearchType(), searchTerm)
							.boost(1.0f))
					.should(QueryBuilders.matchPhrasePrefixQuery(AdvancedSearchType.PNUM.getSearchType(), searchTerm)
							.boost(1.0f))
					.minimumNumberShouldMatch(1);
			if (RoleEnum.CONSULTANT_DOCTOR.getRole().equalsIgnoreCase(role)) {
				boolQueryBuilder.must(QueryBuilders.termQuery("consultantDoctorIds", doctorId));
			}
			SearchQuery searchQuery = null;
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(new PageRequest(page, size, Direction.ASC, "localPatientName")).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("localPatientName").order(SortOrder.ASC)).build();

			patients = elasticsearchTemplate.queryForList(searchQuery, ESPatientDocument.class);
			if (patients != null && !patients.isEmpty()) {
				patientsResponse = new ArrayList<ESPatientResponse>();
				for (ESPatientDocument patient : patients) {
					ESPatientResponse patientResponse = new ESPatientResponse();

					patient.setImageUrl(getFinalImageURL(patient.getImageUrl()));
					patient.setThumbnailUrl(getFinalImageURL(patient.getThumbnailUrl()));

					BeanUtil.map(patient, patientResponse);
					//ESReferenceDocument esReferenceDocument = esReferenceRepository.findOne(patient.getId());
				/*	if (esReferenceDocument != null)
						patientResponse.setReferredBy(esReferenceDocument.getReference());*/
					patientsResponse.add(patientResponse);
				}
				patientResponseDetails = new ESPatientResponseDetails();
				patientResponseDetails.setPatients(patientsResponse);
				patientResponseDetails.setTotalSize(elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), ESPatientDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Searching Patient");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Patients");
		}
		return patientResponseDetails;
	}

	@Override
	public ESPatientResponseDetails searchPatient(AdvancedSearch request) {
		List<ESPatientDocument> patients = null;
		List<ESPatientResponse> response = new ArrayList<ESPatientResponse>();
		ESPatientResponseDetails responseDetails = null;
		try {
			BoolQueryBuilder boolQueryBuilder = createAdvancedSearchCriteria(request);
			SearchQuery searchQuery = null;
			if (request.getSize() > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(
								new PageRequest(request.getPage(), request.getSize(), Direction.DESC, "createdTime"))
						.build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("createdTime").order(SortOrder.DESC)).build();

			patients = elasticsearchTemplate.queryForList(searchQuery, ESPatientDocument.class);

			if (patients != null && !patients.isEmpty()) {
				response = new ArrayList<ESPatientResponse>();
				for (ESPatientDocument patient : patients) {
					ESPatientResponse patientResponse = new ESPatientResponse();

					patient.setImageUrl(getFinalImageURL(patient.getImageUrl()));
					patient.setThumbnailUrl(getFinalImageURL(patient.getThumbnailUrl()));

					BeanUtil.map(patient, patientResponse);
					/*ESReferenceDocument esReferenceDocument = esReferenceRepository.findOne(patient.getId());
					if (esReferenceDocument != null)
						patientResponse.setReferredBy(esReferenceDocument.getReference());*/
					response.add(patientResponse);
				}
				responseDetails = new ESPatientResponseDetails();
				responseDetails.setPatients(response);
				responseDetails.setTotalSize(elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), ESPatientDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Searching Patients");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Patients");
		}
		return responseDetails;
	}

	@SuppressWarnings("deprecation")
	private BoolQueryBuilder createAdvancedSearchCriteria(AdvancedSearch request) throws ParseException {
		String locationId = request.getLocationId();
		String hospitalId = request.getHospitalId();

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.termQuery("locationId", locationId))
				.must(QueryBuilders.termQuery("hospitalId", hospitalId))
				.mustNot(QueryBuilders.termQuery("isPatientDiscarded", true));

		if (RoleEnum.CONSULTANT_DOCTOR.getRole().equalsIgnoreCase(request.getRole())) {
			boolQueryBuilder.must(QueryBuilders.termQuery("consultantDoctorIds", request.getDoctorId()));
		}
		if (request.getSearchParameters() != null && !request.getSearchParameters().isEmpty()) {
			for (AdvancedSearchParameter searchParameter : request.getSearchParameters()) {
				String searchValue = searchParameter.getSearchValue();
				String searchType = searchParameter.getSearchType().getSearchType();
				if (!DPDoctorUtils.anyStringEmpty(searchValue, searchType)) {
					QueryBuilder builder = null;
					if (searchType.equalsIgnoreCase(AdvancedSearchType.DOB.getSearchType())) {

						String[] dob = searchValue.split("/");
						builder = QueryBuilders.nestedQuery(AdvancedSearchType.DOB.getSearchType(),
								QueryBuilders.boolQuery().must(QueryBuilders.termQuery("dob.years", dob[2])).must(QueryBuilders.termQuery("dob.months", dob[0]))
										.must(QueryBuilders.termQuery("dob.days", dob[1])));

					} else if (searchType.equalsIgnoreCase(AdvancedSearchType.REGISTRATION_DATE.getSearchType())) {

						String[] dob = searchValue.split("/");
						DateTime start = new DateTime(Integer.parseInt(dob[2]), Integer.parseInt(dob[0]),
								Integer.parseInt(dob[1]), 0, 0, 0);
						DateTime end = new DateTime(Integer.parseInt(dob[2]), Integer.parseInt(dob[0]),
								Integer.parseInt(dob[1]), 23, 59, 59);
						builder = QueryBuilders.rangeQuery(AdvancedSearchType.CREATED_TIME.getSearchType()).from(start)
								.to(end);

					} else if (searchType.equalsIgnoreCase(AdvancedSearchType.REFERRED_BY.getSearchType())) {
						BoolQueryBuilder queryBuilderForReference = new BoolQueryBuilder();

						if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
							queryBuilderForReference
									.must(QueryBuilders.orQuery(
											QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("locationId")),
											QueryBuilders.termQuery("locationId", locationId)))
									.must(QueryBuilders.orQuery(
											QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("hospitalId")),
											QueryBuilders.termQuery("hospitalId", hospitalId)));
						}

						if (!DPDoctorUtils.anyStringEmpty(searchValue))
							queryBuilderForReference
									.must(QueryBuilders.matchPhrasePrefixQuery("reference", searchValue));
						int size = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(queryBuilderForReference).build(),
								ESReferenceDocument.class);
						if (size > 0) {
							SearchQuery query = new NativeSearchQueryBuilder().withQuery(queryBuilderForReference)
									.withPageable(new PageRequest(0, size)).build();
							List<ESReferenceDocument> referenceDocuments = elasticsearchTemplate.queryForList(query,
									ESReferenceDocument.class);
							@SuppressWarnings("unchecked")
							Collection<String> referenceIds = CollectionUtils.collect(referenceDocuments,
									new BeanToPropertyValueTransformer("id"));
							builder = QueryBuilders.termsQuery(searchType, referenceIds);
						}
					} else if (searchType.equalsIgnoreCase(AdvancedSearchType.PID.getSearchType())){
//						AdvancedSearchType advancedSearchTypeForPID = AdvancedSearchType.PID;
						
//						LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));
//						if(locationCollection != null && locationCollection.getIsPidHasDate()!= null) {
//							if(!locationCollection.getIsPidHasDate())advancedSearchTypeForPID = AdvancedSearchType.PNUM;
//						}
						builder = QueryBuilders.matchPhrasePrefixQuery(AdvancedSearchType.PID.getSearchType(), searchValue);
					 }else if (searchType.equalsIgnoreCase(AdvancedSearchType.PNUM.getSearchType())){
							builder = QueryBuilders.matchPhrasePrefixQuery(AdvancedSearchType.PNUM.getSearchType(), searchValue);
				     }else {
						builder = QueryBuilders.matchPhrasePrefixQuery(searchType, searchValue);
					}
					boolQueryBuilder.must(builder);
				}
			}
		}
		return boolQueryBuilder;
	}

	@Override
	public boolean addDoctor(ESDoctorDocument request) {
		boolean response = false;
		try {
			ESDoctorDocument doctorDocument = esDoctorRepository.findByUserIdAndLocationId(request.getUserId(),
					request.getLocationId());
			if (doctorDocument != null)
				request.setId(doctorDocument.getId());
			else
				request.setId(request.getUserId() + request.getLocationId());

			if (request.getLatitude() != null && request.getLongitude() != null)
				request.setGeoPoint(new GeoPoint(request.getLatitude(), request.getLongitude()));
			esDoctorRepository.save(request);
			transnationalService.addResource(new ObjectId(request.getUserId()), Resource.DOCTOR, true);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error While Saving Doctor Details to ES : " + e.getMessage());
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	public void editLocation(DoctorLocation doctorLocation) {
		try {
			Boolean isActivate;
			GeoPoint geoPoint = null;
			if (doctorLocation.getLatitude() != null && doctorLocation.getLongitude() != null)
				geoPoint = new GeoPoint(doctorLocation.getLatitude(), doctorLocation.getLongitude());
			ESLocationDocument esLocationDocument = new ESLocationDocument();
			BeanUtil.map(doctorLocation, esLocationDocument);
			esLocationDocument.setGeoPoint(geoPoint);
			esLocationDocument.setId(doctorLocation.getLocationId());
			esLocationRepository.save(esLocationDocument);
			List<ESDoctorDocument> doctorDocuments = esDoctorRepository
					.findByLocationId(doctorLocation.getLocationId());
			for (ESDoctorDocument doctorDocument : doctorDocuments) {
				String id = doctorDocument.getId();
				isActivate = doctorDocument.getIsActivate();
				BeanUtil.map(doctorLocation, doctorDocument);

				doctorDocument.setImages(null);
				doctorDocument.setImages(doctorLocation.getImages());

				doctorDocument.setClinicWorkingSchedules(null);
				doctorDocument.setClinicWorkingSchedules(doctorLocation.getClinicWorkingSchedules());

				doctorDocument.setAlternateClinicNumbers(null);
				doctorDocument.setAlternateClinicNumbers(doctorLocation.getAlternateClinicNumbers());

				doctorDocument.setGeoPoint(geoPoint);
				doctorDocument.setId(id);
				doctorDocument.setIsActivate(isActivate);
				esDoctorRepository.save(doctorDocument);
				transnationalService.addResource(new ObjectId(doctorLocation.getLocationId()), Resource.LOCATION, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while editing location " + e.getMessage());
		}
	}

	@Override
	public void addEditReference(ESReferenceDocument esReferenceDocument) {
		try {
			esReferenceRepository.save(esReferenceDocument);
			transnationalService.addResource(new ObjectId(esReferenceDocument.getId()), Resource.REFERENCE, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while editing reference " + e.getMessage());
		}
	}

	@Override
	public void activateUser(String userId) {
		try {
			List<ESDoctorDocument> doctorDocument = esDoctorRepository.findByUserId(userId);
			if (doctorDocument != null) {
				UserCollection userCollection = userRepository.findOne(new ObjectId(userId));
				for (ESDoctorDocument esDoctorDocument : doctorDocument) {
					esDoctorDocument.setIsActive(userCollection.getIsActive());
					esDoctorDocument.setIsVerified(userCollection.getIsVerified());
					esDoctorDocument.setUserState(userCollection.getUserState().getState());
					esDoctorRepository.save(esDoctorDocument);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error While Saving Doctor Details to ES : " + e.getMessage());
		}
	}

	@Override
	public boolean addCollectionBoy(ESCollectionBoyDocument request) {
		boolean response = false;
		try {
			esCollectionBoyRepository.save(request);
			transnationalService.addResource(new ObjectId(request.getId()), Resource.COLLECTION_BOY, true);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error While Saving Locale Details to ES : " + e.getMessage());
		}
		return response;
	}
}
