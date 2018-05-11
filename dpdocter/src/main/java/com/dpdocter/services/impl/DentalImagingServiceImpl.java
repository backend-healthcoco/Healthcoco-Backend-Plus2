package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ws.rs.MatrixParam;
import javax.ws.rs.QueryParam;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DentalDiagnosticService;
import com.dpdocter.beans.DentalImaging;
import com.dpdocter.beans.DentalImagingLocationServiceAssociation;
import com.dpdocter.beans.DentalImagingReports;
import com.dpdocter.beans.DentalImagingRequest;
import com.dpdocter.beans.DoctorHospitalDentalImagingAssociation;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LabReports;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.DentalDiagnosticServiceCollection;
import com.dpdocter.collections.DentalImagingCollection;
import com.dpdocter.collections.DentalImagingLocationServiceAssociationCollection;
import com.dpdocter.collections.DentalImagingReportsCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorHospitalDentalImagingAssociationCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LabReportsCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.RateCardCollection;
import com.dpdocter.collections.RateCardLabAssociationCollection;
import com.dpdocter.collections.RateCardTestAssociationCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DentalImagingLocationServiceAssociationRepository;
import com.dpdocter.repository.DentalImagingReportsRepository;
import com.dpdocter.repository.DentalImagingRepository;
import com.dpdocter.repository.DoctorHospitalDentalImagingAssociationRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DentalImagingReportsAddRequest;
import com.dpdocter.request.DoctorLabReportsAddRequest;
import com.dpdocter.response.DentalImagingLocationResponse;
import com.dpdocter.response.DentalImagingLocationServiceAssociationLookupResponse;
import com.dpdocter.response.DentalImagingResponse;
import com.dpdocter.response.DentalLabPickupLookupResponse;
import com.dpdocter.response.DoctorClinicProfileLookupResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.LabTestGroupResponse;
import com.dpdocter.response.ServiceLocationResponse;
import com.dpdocter.response.UserRoleLookupResponse;
import com.dpdocter.services.DentalImagingService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.SMSServices;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

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
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));

			if (request.getId() != null) {
				dentalImagingCollection = dentalImagingRepository.findOne(new ObjectId(request.getId()));
				if (dentalImagingCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Record not found");
				}
				BeanUtil.map(request, dentalImagingCollection);

				dentalImagingCollection.setUpdatedTime(new Date());
				dentalImagingCollection = dentalImagingRepository.save(dentalImagingCollection);
			} else {
				requestId = UniqueIdInitial.DENTAL_IMAGING.getInitial() + DPDoctorUtils.generateRandomId();
				dentalImagingCollection = new DentalImagingCollection();
				BeanUtil.map(request, dentalImagingCollection);
				dentalImagingCollection.setRequestId(requestId);
				dentalImagingCollection.setCreatedTime(new Date());
				dentalImagingCollection.setUpdatedTime(new Date());

				dentalImagingCollection = dentalImagingRepository.save(dentalImagingCollection);
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
			Long to, String searchTerm, int size, int page) {

		List<DentalImagingResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (to != null) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else {
				criteria.and("updatedTime").gte(new Date(from));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalImagingResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalImagingCollection.class, DentalImagingResponse.class);
			response = aggregationResults.getMappedResults();

			for (DentalImagingResponse dentalImagingResponse : response) {
				if (!DPDoctorUtils.allStringsEmpty(dentalImagingResponse.getLocationId(),
						dentalImagingResponse.getHospitalId(), dentalImagingResponse.getPatientId())) {

					PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(
							new ObjectId(dentalImagingResponse.getPatientId()),
							new ObjectId(dentalImagingResponse.getLocationId()),
							new ObjectId(dentalImagingResponse.getHospitalId()));
					if (patientCollection != null) {
						PatientCard patientCard = new PatientCard();
						BeanUtil.map(patientCollection, patientCard);
						dentalImagingResponse.setPatient(patientCard);
					}

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
		List<DentalImagingLocationServiceAssociationCollection> dentalImagingLocationServiceAssociationCollections = null;
		// List<RateCardTestAssociationLookupResponse> responses = null;
		Collection<ObjectId> hospitalIds = null;

		try {
			List<DoctorHospitalDentalImagingAssociation> doctorHospitalDentalImagingAssociations = getDoctorHospitalAssociation(
					doctorId);

			hospitalIds = CollectionUtils.collect(doctorHospitalDentalImagingAssociations,
					new BeanToPropertyValueTransformer("hospitalId"));
			HashSet<ObjectId> hospitalHashSet = new HashSet<>(hospitalIds);
			dentalImagingLocationServiceAssociationCollections = dentalImagingLocationServiceAssociationRepository
					.findbyHospital(new ArrayList<>(hospitalHashSet));
			if (dentalImagingLocationServiceAssociationCollections == null) {
				throw new BusinessException(ServiceError.NoRecord, "Association not found");
			}
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.and("service.serviceName").regex(searchTerm, "i");
			}
			aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("hospitalId").in(hospitalHashSet)),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					Aggregation.lookup("treatment_services_cl", "dentalDiagnosticServiceId", "_id", "service"),
					Aggregation.match(criteria),
					new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("id", "$locationId")
									.append("locationId", new BasicDBObject("$first", "$locationId")).append("location",
											new BasicDBObject("$first", "$location").append("dentalDiagnosticServiceId",
													new BasicDBObject("$push", "$dentalDiagnosticServiceId"))))),
					Aggregation.match(new Criteria("dentalDiagnosticServiceId").all(dentalImagingServiceId)));
			AggregationResults<DentalImagingLocationResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalImagingLocationServiceAssociationCollection.class, DentalImagingLocationResponse.class);
			System.out.println(aggregation);
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

			if (fileDetails != null) {
				fileDetails.setFileName(fileDetails.getFileName() + createdTime.getTime());

				String path = "dental-imaging-reports" + File.separator + request.getPatientId();

				imageURLResponse = fileManager.saveImageAndReturnImageUrl(fileDetails, path, true);
				if (imageURLResponse != null) {
					imageURLResponse.setImageUrl(imagePath + imageURLResponse.getImageUrl());
					imageURLResponse.setThumbnailUrl(imagePath + imageURLResponse.getThumbnailUrl());
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

			if (dentalImagingReportsCollection != null) {

				UserCollection doctor = userRepository.findOne(new ObjectId(request.getDoctorId()));

				if (request.getMobileNumber() != null) {
					LocationCollection daughterlocationCollection = locationRepository
							.findOne(dentalImagingReportsCollection.getLocationId());
					LocationCollection parentLocationCollection = locationRepository
							.findOne(dentalImagingReportsCollection.getUploadedByLocationId());
					String message = "";

					UserCollection userCollection = userRepository.findOne(new ObjectId(request.getPatientId()));
					SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

					smsTrackDetail.setType("LAB REPORT UPLOAD");
					SMSDetail smsDetail = new SMSDetail();
					smsDetail.setUserId(daughterlocationCollection.getId());
					SMS sms = new SMS();
					smsDetail.setUserName(daughterlocationCollection.getLocationName());
					message = message.replace("{patientName}", userCollection.getFirstName());
					message = message.replace("{specimenName}", "");
					message = message.replace("{parentLab}", parentLocationCollection.getLocationName());
					sms.setSmsText(message);
					SMSAddress smsAddress = new SMSAddress();
					smsAddress.setRecipient(request.getMobileNumber());
					sms.setSmsAddress(smsAddress);
					smsDetail.setSms(sms);
					smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
					List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
					smsDetails.add(smsDetail);
					smsTrackDetail.setSmsDetails(smsDetails);
					smsServices.sendSMS(smsTrackDetail, true);
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

}
