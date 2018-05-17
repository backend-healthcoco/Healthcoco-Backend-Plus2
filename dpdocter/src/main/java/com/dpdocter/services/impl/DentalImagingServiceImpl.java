package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DentalDiagnosticService;
import com.dpdocter.beans.DentalDiagnosticServiceRequest;
import com.dpdocter.beans.DentalImaging;
import com.dpdocter.beans.DentalImagingLocationServiceAssociation;
import com.dpdocter.beans.DentalImagingReports;
import com.dpdocter.beans.DentalImagingRequest;
import com.dpdocter.beans.DoctorHospitalDentalImagingAssociation;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.collections.DentalDiagnosticServiceCollection;
import com.dpdocter.collections.DentalImagingCollection;
import com.dpdocter.collections.DentalImagingLocationServiceAssociationCollection;
import com.dpdocter.collections.DentalImagingReportsCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorHospitalDentalImagingAssociationCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DentalImagingLocationServiceAssociationRepository;
import com.dpdocter.repository.DentalImagingReportsRepository;
import com.dpdocter.repository.DentalImagingRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorHospitalDentalImagingAssociationRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DentalImagingReportsAddRequest;
import com.dpdocter.response.DentalImagingLocationResponse;
import com.dpdocter.response.DentalImagingLocationServiceAssociationLookupResponse;
import com.dpdocter.response.DentalImagingResponse;
import com.dpdocter.response.DoctorClinicProfileLookupResponse;
import com.dpdocter.response.DoctorHospitalDentalImagingAssociationResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.DentalImagingService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class DentalImagingServiceImpl implements DentalImagingService {

	@Autowired
	LocationRepository locationRepository;

	@Autowired
	DentalImagingRepository dentalImagingRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PatientRepository patientRepository;

	@Autowired
	DentalImagingLocationServiceAssociationRepository dentalImagingLocationServiceAssociationRepository;

	@Autowired
	FileManager fileManager;

	@Autowired
	DentalImagingReportsRepository dentalImagingReportsRepository;

	@Autowired
	DoctorHospitalDentalImagingAssociationRepository doctorHospitalDentalImagingAssociationRepository;

	@Autowired
	SMSServices smsServices;
	
	@Autowired
	PushNotificationServices pushNotificationServices;
	
	@Autowired
	DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	MongoTemplate mongoTemplate;
	private static Logger logger = Logger.getLogger(DentalImagingServiceImpl.class.getName());

	@Value(value = "${image.path}")
	private String imagePath;

	@Override
	@Transactional
	public DentalImaging addEditDentalImagingRequest(DentalImagingRequest request) {
		DentalImaging response = null;
		DentalImagingCollection dentalImagingCollection = null;
		String requestId = null;

		try {
			List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository.findByLocationId(new ObjectId(request.getLocationId()));
			if (request.getId() != null) {
				
				dentalImagingCollection = dentalImagingRepository.findOne(new ObjectId(request.getId()));
				if (dentalImagingCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Record not found");
				}
				BeanUtil.map(request, dentalImagingCollection);
				dentalImagingCollection.setServices(request.getServices());
				dentalImagingCollection.setUpdatedTime(new Date());
				dentalImagingCollection = dentalImagingRepository.save(dentalImagingCollection);
				for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
					pushNotificationServices.notifyUser(String.valueOf(doctorClinicProfileCollection.getDoctorId()), "You have new dental imaging request.", ComponentType.REFRESH_DENTAL_IMAGING.getType(),String.valueOf(dentalImagingCollection.getId()), null);
					
				}
			} else {
				UserCollection userCollection = userRepository.findOne(new ObjectId(request.getUploadedByDoctorId()));
				requestId = UniqueIdInitial.DENTAL_IMAGING.getInitial() + DPDoctorUtils.generateRandomId();
				dentalImagingCollection = new DentalImagingCollection();
				BeanUtil.map(request, dentalImagingCollection);
				dentalImagingCollection.setServices(request.getServices());
				dentalImagingCollection.setRequestId(requestId);
				dentalImagingCollection.setCreatedTime(new Date());
				dentalImagingCollection.setUpdatedTime(new Date());
				dentalImagingCollection.setCreatedBy(userCollection.getFirstName());
				dentalImagingCollection = dentalImagingRepository.save(dentalImagingCollection);
				for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
					pushNotificationServices.notifyUser(String.valueOf(doctorClinicProfileCollection.getDoctorId()), "Request Has been updated.", ComponentType.DENTAL_IMAGING_REQUEST.getType(), String.valueOf(dentalImagingCollection.getId()), null);
				}
			}
			response = new DentalImaging();
			BeanUtil.map(dentalImagingCollection, response);
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional

	public List<DentalImagingResponse> getRequests(String locationId, String hospitalId, String doctorId, Long from,
			Long to, String searchTerm, int size, int page, String type) {

		List<DentalImagingResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (type.equalsIgnoreCase("DOCTOR")) {
				
				if (!DPDoctorUtils.anyStringEmpty(locationId)) {
					criteria.and("uploadedByLocationId").is(new ObjectId(locationId));
				}

				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria.and("uploadedByDoctorId").is(new ObjectId(doctorId));
				}
				
			} else {
				if (!DPDoctorUtils.anyStringEmpty(locationId)) {
					criteria.and("locationId").is(new ObjectId(locationId));
				}

				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria.and("hospitalId").is(new ObjectId(hospitalId));
				}
			}
			
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("patient.firstName").regex("^" + searchTerm, "i"),
						new Criteria("patient.firstName").regex("^" + searchTerm),
						new Criteria("patient.firstName").regex(searchTerm + ".*"),
						new Criteria("location.locationName").regex("^" + searchTerm, "i"),
						new Criteria("location.locationName").regex("^" + searchTerm),
						new Criteria("location.locationName").regex(searchTerm + ".*"),
						new Criteria("services.serviceName").regex("^" + searchTerm, "i"),
						new Criteria("services.serviceName").regex("^" + searchTerm),
						new Criteria("services.serviceName").regex(searchTerm + ".*"));
			}

			if (to != null) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else {
				criteria.and("updatedTime").gte(new Date(from));
			}
			
			
			CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", "$_id").append("patientId", new BasicDBObject("$first", "$patientId"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("uploadedByDoctorId", new BasicDBObject("$first", "$uploadedByDoctorId"))
							.append("uploadedByHospitalId", new BasicDBObject("$first", "$uploadedByHospitalId"))
							.append("uploadedByLocationId", new BasicDBObject("$first", "$uploadedByLocationId"))
							.append("services", new BasicDBObject("$push", "$services"))
							.append("referringDoctor", new BasicDBObject("$first", "$referringDoctor"))
							.append("clinicalNotes", new BasicDBObject("$first", "$clinicalNotes"))
							.append("reportsRequired", new BasicDBObject("$first", "$reportsRequired"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("patient", new BasicDBObject("$first", "$patient"))
							.append("location", new BasicDBObject("$first", "$location"))
							.append("reports", new BasicDBObject("$first", "$reports"))
							.append("adminCreatedTime", new BasicDBObject("$first", "$adminCreatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))
							));
			
		/*	CustomAggregationOperation aggregationOperation2 = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", "$_id").append("type", new BasicDBObject("$first", "$type"))
							.append("dentalDiagnosticServiceId", new BasicDBObject("$first", "$dentalDiagnosticServiceId"))
							.append("serviceName", new BasicDBObject("$first", "$serviceName"))
							.append("toothNumber", new BasicDBObject("$first", "$toothNumber"))
							.append("CBCTQuadrant", new BasicDBObject("$first", "$CBCTQuadrant"))
							.append("CBCTArch", new BasicDBObject("$first", "$CBCTArch"))
							));*/
			
		/*	
			private String type;
			private String dentalDiagnosticServiceId;
			private String serviceName;
			private List<String> toothNumber;
			private String CBCTQuadrant;
			private String toothNumber;*/
			
			/*private String patientId;
			private String doctorId;
			private String hospitalId;
			private String locationId;
			private String uploadedByDoctorId;
			private String uploadedByHospitalId;
			private String uploadedByLocationId;
			private String referringDoctor;
			private String clinicalNotes;
			private Boolean reportsRequired;
			private String specialInstructions;
			private List<DentalDiagnosticServiceRequest> services;
			private Boolean discarded;
			private PatientCard patient;
			private Location location;
			private List<DentalImagingReports> reports;*/
			
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.unwind("services"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.match(criteria),aggregationOperation,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.unwind("services"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.match(criteria),aggregationOperation,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalImagingResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalImagingCollection.class, DentalImagingResponse.class);
			response = aggregationResults.getMappedResults();

			for (DentalImagingResponse dentalImagingResponse : response) {
				if (!DPDoctorUtils.allStringsEmpty(dentalImagingResponse.getUploadedByLocationId(),
						dentalImagingResponse.getUploadedByHospitalId(), dentalImagingResponse.getPatientId())) {

					PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(
							new ObjectId(dentalImagingResponse.getPatientId()),
							new ObjectId(dentalImagingResponse.getUploadedByLocationId()),
							new ObjectId(dentalImagingResponse.getUploadedByHospitalId()));
					if (patientCollection != null) {
						PatientCard patientCard = new PatientCard();
						BeanUtil.map(patientCollection, patientCard);
						dentalImagingResponse.setPatient(patientCard);
					}
				}
				List<DentalImagingReportsCollection> dentalImagingReportsCollections = dentalImagingReportsRepository.getReportsByRequestId(new ObjectId(dentalImagingResponse.getId()));
				if(dentalImagingReportsCollections != null)
				{
					List<DentalImagingReports> dentalImagingReports = new ArrayList<>();
					for (DentalImagingReportsCollection dentalImagingReportsCollection : dentalImagingReportsCollections) {
						DentalImagingReports reports = new DentalImagingReports();
						BeanUtil.map(dentalImagingReportsCollection, reports);
						dentalImagingReports.add(reports);
					}
					dentalImagingResponse.setReports(dentalImagingReports);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalDiagnosticService> getServices(String searchTerm, String type, int page, int size) {
		List<DentalDiagnosticService> response = null;

		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("type").is(type);
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("serviceName").regex("^" + searchTerm, "i"),
						new Criteria("serviceName").regex("^" + searchTerm),
						new Criteria("serviceName").regex(searchTerm + ".*"));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalDiagnosticService> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalDiagnosticServiceCollection.class, DentalDiagnosticService.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditDentalImagingLocationServiceAssociation(
			List<DentalImagingLocationServiceAssociation> request) {
		Boolean response = false;
		ObjectId oldId = null;
		DentalImagingLocationServiceAssociationCollection dentalImagingLocationServiceAssociationCollection = null;
		try {
			for (DentalImagingLocationServiceAssociation dentalImagingLocationServiceAssociation : request) {
				dentalImagingLocationServiceAssociationCollection = dentalImagingLocationServiceAssociationRepository
						.findbyServiceLocationHospital(
								new ObjectId(dentalImagingLocationServiceAssociation.getDentalDiagnosticServiceId()),
								new ObjectId(dentalImagingLocationServiceAssociation.getLocationId()),
								new ObjectId(dentalImagingLocationServiceAssociation.getHospitalId()));
				if (dentalImagingLocationServiceAssociationCollection == null) {
					dentalImagingLocationServiceAssociationCollection = new DentalImagingLocationServiceAssociationCollection();
				} else {
					oldId = dentalImagingLocationServiceAssociationCollection.getId();
				}
				BeanUtil.map(dentalImagingLocationServiceAssociation,
						dentalImagingLocationServiceAssociationCollection);
				dentalImagingLocationServiceAssociationCollection.setId(oldId);
				dentalImagingLocationServiceAssociationCollection = dentalImagingLocationServiceAssociationRepository
						.save(dentalImagingLocationServiceAssociationCollection);
			}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalImagingLocationServiceAssociationLookupResponse> getLocationAssociatedServices(String locationId,
			String hospitalId, String searchTerm, String type, int page, int size, Boolean discarded) {
		List<DentalImagingLocationServiceAssociationLookupResponse> response = null;

		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("service.type").is(type);
			}

			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("service.name").regex("^" + searchTerm, "i"),
						new Criteria("service.name").regex("^" + searchTerm),
						new Criteria("service.name").regex(searchTerm + ".*"));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("dental_diagnostic_service_cl", "dentalDiagnosticServiceId", "_id",
								"service"),
						Aggregation.unwind("service"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("dental_diagnostic_service_cl", "dentalDiagnosticServiceId", "_id",
								"service"),
						Aggregation.unwind("service"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalImagingLocationServiceAssociationLookupResponse> aggregationResults = mongoTemplate
					.aggregate(aggregation, DentalImagingLocationServiceAssociationCollection.class,
							DentalImagingLocationServiceAssociationLookupResponse.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<Hospital> getHospitalList(String doctorId, String hospitalId) {
		List<Hospital> hospitals = null;

		try {
			UserCollection userCollection = userRepository.findOne(new ObjectId(doctorId));
			Criteria criteria = new Criteria("doctorId").is(userCollection.getId());
			criteria.and("location.hospitalId").is(new ObjectId(hospitalId));
			List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"),
							Aggregation.lookup("hospital_cl", "$location.hospitalId", "_id", "hospital"),
							Aggregation.unwind("hospital"), Aggregation.match(criteria)),
					DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class).getMappedResults();
			if (doctorClinicProfileLookupResponses == null || doctorClinicProfileLookupResponses.isEmpty()) {
				logger.warn("None of your clinic is active");
				// user.setUserState(UserState.NOTACTIVATED);
				throw new BusinessException(ServiceError.NotAuthorized, "None of your clinic is active");
			}
			if (doctorClinicProfileLookupResponses != null && !doctorClinicProfileLookupResponses.isEmpty()) {
				hospitals = new ArrayList<Hospital>();
				Map<String, Hospital> checkHospitalId = new HashMap<String, Hospital>();
				for (DoctorClinicProfileLookupResponse doctorClinicProfileLookupResponse : doctorClinicProfileLookupResponses) {
					LocationCollection locationCollection = doctorClinicProfileLookupResponse.getLocation();
					HospitalCollection hospitalCollection = doctorClinicProfileLookupResponse.getHospital();
					LocationAndAccessControl locationAndAccessControl = new LocationAndAccessControl();
					BeanUtil.map(locationCollection, locationAndAccessControl);
					locationAndAccessControl.setIsActivate(doctorClinicProfileLookupResponse.getIsActivate());
					locationAndAccessControl.setIsVerified(doctorClinicProfileLookupResponse.getIsVerified());
					locationAndAccessControl.setLogoUrl(getFinalImageURL(locationAndAccessControl.getLogoUrl()));
					locationAndAccessControl
							.setLogoThumbnailUrl(getFinalImageURL(locationAndAccessControl.getLogoThumbnailUrl()));
					locationAndAccessControl.setImages(getFinalClinicImages(locationAndAccessControl.getImages()));
					Hospital hospital = new Hospital();
					BeanUtil.map(hospitalCollection, hospital);

					hospital.setHospitalImageUrl(getFinalImageURL(hospital.getHospitalImageUrl()));
					hospital.getLocationsAndAccessControl().add(locationAndAccessControl);
					checkHospitalId.put(locationCollection.getHospitalId().toString(), hospital);
					hospitals.add(hospital);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Exception occured :: " + e);
		}
		return hospitals;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;

	}

	private List<ClinicImage> getFinalClinicImages(List<ClinicImage> clinicImages) {
		if (clinicImages != null && !clinicImages.isEmpty())
			for (ClinicImage clinicImage : clinicImages) {
				if (clinicImage.getImageUrl() != null) {
					clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
				}
				if (clinicImage.getThumbnailUrl() != null) {
					clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
				}
			}
		return clinicImages;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<DentalImagingLocationResponse> getServiceLocations(List<String> dentalImagingServiceId, String doctorId,
			String searchTerm, int size, int page) {

		List<DentalImagingLocationResponse> dentalImagingLocationResponses = null;
		List<ObjectId> serviceIds = new ArrayList<ObjectId>();
		List<DentalImagingLocationServiceAssociationCollection> dentalImagingLocationServiceAssociationCollections = null;
		// List<RateCardTestAssociationLookupResponse> responses = null;
		List<ObjectId> hospitalObjectIds = new ArrayList<ObjectId>();

		try {
			List<DoctorHospitalDentalImagingAssociation> doctorHospitalDentalImagingAssociations = getDoctorHospitalAssociation(
					doctorId);

			for (DoctorHospitalDentalImagingAssociation doctorHospitalDentalImagingAssociation : doctorHospitalDentalImagingAssociations) {
				hospitalObjectIds.add(new ObjectId(doctorHospitalDentalImagingAssociation.getHospitalId()));
			}

			for (String id : dentalImagingServiceId) {
				serviceIds.add(new ObjectId(id));
			}
			dentalImagingLocationServiceAssociationCollections = dentalImagingLocationServiceAssociationRepository
					.findbyHospital(hospitalObjectIds);
			if (dentalImagingLocationServiceAssociationCollections == null) {
				throw new BusinessException(ServiceError.NoRecord, "Association not found");
			}
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.and("service.serviceName").regex(searchTerm, "i");
			}
			aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria("hospitalId").in(hospitalObjectIds).and("discarded").is(false)),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					Aggregation.lookup("dental_diagnostic_service_cl", "dentalDiagnosticServiceId", "_id", "service"),
					Aggregation.unwind("service"), Aggregation.match(criteria),
					new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", "$locationId")
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("location", new BasicDBObject("$first", "$location"))
									.append("dentalDiagnosticServiceId",
											new BasicDBObject("$push", "$dentalDiagnosticServiceId")))),
					Aggregation.match(new Criteria("dentalDiagnosticServiceId").all(serviceIds)));
			AggregationResults<DentalImagingLocationResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalImagingLocationServiceAssociationCollection.class, DentalImagingLocationResponse.class);
			dentalImagingLocationResponses = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return dentalImagingLocationResponses;

	}

	@Override
	@Transactional
	public DentalImagingReports addDentalImagingReportBase64(FileDetails fileDetails,
			DentalImagingReportsAddRequest request) {
		DentalImagingReports response = null;
		DentalImagingReportsCollection dentalImagingReportsCollection = null;
		ImageURLResponse imageURLResponse = null;
		try {
			Date createdTime = new Date();
			UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
			if (fileDetails != null) {
				fileDetails.setFileName(fileDetails.getFileName() + createdTime.getTime());

				String path = "dental-imaging-reports" + File.separator + request.getPatientId();

				imageURLResponse = fileManager.saveImageAndReturnImageUrl(fileDetails, path, true);
				if (imageURLResponse != null) {
					imageURLResponse.setImageUrl(imagePath + imageURLResponse.getImageUrl());
					imageURLResponse.setThumbnailUrl(imagePath + imageURLResponse.getThumbnailUrl());
					pushNotificationServices.notifyUser(String.valueOf(userCollection.getId()), "Report have been uploaded.", ComponentType.DENTAL_IMAGING_REQUEST.getType(), null, null);
				}
			}

			if (dentalImagingReportsCollection == null) {
				dentalImagingReportsCollection = new DentalImagingReportsCollection();
			}

			BeanUtil.map(request, dentalImagingReportsCollection);
			dentalImagingReportsCollection.setReport(imageURLResponse);
			dentalImagingReportsCollection = dentalImagingReportsRepository.save(dentalImagingReportsCollection);
			response = new DentalImagingReports();
			BeanUtil.map(dentalImagingReportsCollection, response);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditDoctorHospitalDentalImagingAssociation(List<DoctorHospitalDentalImagingAssociation> request) {
		Boolean response = false;
		ObjectId oldId = null;
		DoctorHospitalDentalImagingAssociationCollection doctorHospitalDentalImagingAssociationCollection = null;
		try {
			for (DoctorHospitalDentalImagingAssociation doctorHospitalDentalImagingAssociation : request) {
				doctorHospitalDentalImagingAssociationCollection = doctorHospitalDentalImagingAssociationRepository
						.findbyDoctorHospital(new ObjectId(doctorHospitalDentalImagingAssociation.getDoctorId()),
								new ObjectId(doctorHospitalDentalImagingAssociation.getHospitalId()));

				if (doctorHospitalDentalImagingAssociationCollection == null) {
					doctorHospitalDentalImagingAssociationCollection = new DoctorHospitalDentalImagingAssociationCollection();
				} else {
					oldId = doctorHospitalDentalImagingAssociationCollection.getId();
				}
				BeanUtil.map(doctorHospitalDentalImagingAssociation, doctorHospitalDentalImagingAssociationCollection);
				doctorHospitalDentalImagingAssociationCollection.setId(oldId);
				doctorHospitalDentalImagingAssociationCollection = doctorHospitalDentalImagingAssociationRepository
						.save(doctorHospitalDentalImagingAssociationCollection);
			}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<DoctorHospitalDentalImagingAssociation> getDoctorHospitalAssociation(String doctorId) {
		List<DoctorHospitalDentalImagingAssociation> response = null;

		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DoctorHospitalDentalImagingAssociation> aggregationResults = mongoTemplate.aggregate(
					aggregation, DoctorHospitalDentalImagingAssociationCollection.class,
					DoctorHospitalDentalImagingAssociation.class);

			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public DentalImaging discardRequest(String id, boolean discarded) {
		DentalImaging response = null;
		DentalImagingCollection dentalImagingCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				dentalImagingCollection = dentalImagingRepository.findOne(new ObjectId(id));
			}
			
			if (dentalImagingCollection != null) {
				UserCollection userCollection = userRepository.findOne(dentalImagingCollection.getUploadedByDoctorId());
				dentalImagingCollection.setDiscarded(discarded);
				dentalImagingCollection = dentalImagingRepository.save(dentalImagingCollection);
				pushNotificationServices.notifyUser(String.valueOf(userCollection.getId()), "Request has been discarded.", ComponentType.DENTAL_IMAGING_REQUEST.getType(), String.valueOf(dentalImagingCollection.getId()), null);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}
			response = new DentalImaging();
			BeanUtil.map(dentalImagingCollection, response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	
	@Override
	@Transactional
	public DentalImagingReports discardReport(String id, boolean discarded) {
		DentalImagingReports response = null;
		DentalImagingReportsCollection dentalImagingReportsCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				dentalImagingReportsCollection = dentalImagingReportsRepository.findOne(new ObjectId(id));
			}
			if (dentalImagingReportsCollection != null) {
				dentalImagingReportsCollection.setDiscarded(discarded);
				dentalImagingReportsCollection = dentalImagingReportsRepository.save(dentalImagingReportsCollection);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}
			response = new DentalImagingReports();
			BeanUtil.map(dentalImagingReportsCollection, response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<DoctorHospitalDentalImagingAssociationResponse> getHospitalAssociatedDoctor(String hospitalId,
			String searchTerm, int size, int page) {
		
		List<DoctorHospitalDentalImagingAssociationResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;
			
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			criteria.and("discarded").is(false);
			
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("service.name").regex("^" + searchTerm, "i"),
						new Criteria("service.name").regex("^" + searchTerm),
						new Criteria("service.name").regex(searchTerm + ".*"));
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DoctorHospitalDentalImagingAssociationResponse> aggregationResults = mongoTemplate.aggregate(
					aggregation, DoctorHospitalDentalImagingAssociationCollection.class,
					DoctorHospitalDentalImagingAssociationResponse.class);

			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}


}
