package com.dpdocter.services.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
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

import com.dpdocter.beans.CollectionBoyDoctorAssociation;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DentalCardValueJasperBean;
import com.dpdocter.beans.DentalLabDoctorAssociation;
import com.dpdocter.beans.DentalLabPickup;
import com.dpdocter.beans.DentalStage;
import com.dpdocter.beans.DentalStagejasperBean;
import com.dpdocter.beans.DentalToothNumber;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.beans.DentalWorkCardValue;
import com.dpdocter.beans.DentalWorksSample;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.InseptionReportJasperBean;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCardDentalWorkAssociation;
import com.dpdocter.beans.RateCardDoctorAssociation;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.User;
import com.dpdocter.collections.CRNCollection;
import com.dpdocter.collections.CollectionBoyCollection;
import com.dpdocter.collections.CollectionBoyDoctorAssociationCollection;
import com.dpdocter.collections.DentalLabDoctorAssociationCollection;
import com.dpdocter.collections.DentalLabPickupCollection;
import com.dpdocter.collections.DentalWorkCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DynamicCollectionBoyAllocationCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.RateCardDentalWorkAssociationCollection;
import com.dpdocter.collections.RateCardDoctorAssociationCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserDeviceCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.LabType;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CRNRepository;
import com.dpdocter.repository.CollectionBoyDoctorAssociationRepository;
import com.dpdocter.repository.CollectionBoyRepository;
import com.dpdocter.repository.DentalLabDoctorAssociationRepository;
import com.dpdocter.repository.DentalLabTestPickupRepository;
import com.dpdocter.repository.DentalWorkRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DynamicCollectionBoyAllocationRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.RateCardDentalWorkAssociationRepository;
import com.dpdocter.repository.RateCardDoctorAssociationRepository;
import com.dpdocter.repository.UserDeviceRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.request.DentalLabPickupChangeStatusRequest;
import com.dpdocter.request.DentalLabPickupRequest;
import com.dpdocter.request.DentalStageRequest;
import com.dpdocter.request.DentalWorksSampleRequest;
import com.dpdocter.request.UpdateDentalStagingRequest;
import com.dpdocter.request.UpdateETARequest;
import com.dpdocter.response.CBDoctorAssociationLookupResponse;
import com.dpdocter.response.DentalLabDoctorAssociationLookupResponse;
import com.dpdocter.response.DentalLabPickupLookupResponse;
import com.dpdocter.response.DentalLabPickupResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.services.DentalLabService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
public class DentalLabServiceImpl implements DentalLabService {

	@Autowired
	private DentalWorkRepository dentalWorkRepository;

	@Autowired
	private DentalLabTestPickupRepository dentalLabTestPickupRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private CRNRepository crnRepository;

	@Autowired
	private RateCardDentalWorkAssociationRepository rateCardDentalWorkAssociationRepository;

	@Autowired
	private RateCardDoctorAssociationRepository rateCardDoctorAssociationRepository;

	@Autowired
	private DentalLabDoctorAssociationRepository dentalLabDoctorAssociationRepository;

	@Autowired
	private CollectionBoyDoctorAssociationRepository collectionBoyDoctorAssociationRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private CollectionBoyRepository collectionBoyRepository;

	@Autowired
	private PushNotificationServices pushNotificationServices;

	@Autowired
	private LocationServices locationServices;

	@Autowired
	private UserDeviceRepository userDeviceRepository;

	@Autowired
	private DynamicCollectionBoyAllocationRepository dynamicCollectionBoyAllocationRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private SMSServices smsServices;

	@Value("${collection.boy.notification}")
	private String COLLECTION_BOY_NOTIFICATION;

	@Value("${dental.lab.coping.trial.message}")
	private String COPING_TRIAL_NOTIFICATION;

	@Value("${dental.lab.bisque.trial.message}")
	private String BISQUE_TRIAL_NOTIFICATION;

	@Value("${dental.lab.finished.message}")
	private String FINISHED_LAB_NOTIFICATION;

	@Value("${dental.lab.cancelled.message}")
	private String CANCELLED_NOTIFICATION;

	@Value("${dental.lab.accepted.message}")
	private String ACCEPTED_NOTIFICATION;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${pdf.footer.text}")
	private String footerText;

	@Value(value = "${jasper.print.dental.works.reports.fileName}")
	private String dentalWorksFormA4FileName;

	@Value(value = "${dental.lab.add.request.to.doctor}")
	private String dentalLabSMSToDoctor;

	@Value("${dental.lab.coping.trial.message.cb}")
	private String COPING_TRIAL_NOTIFICATION_CB;

	@Value("${dental.lab.bisque.trial.message.cb}")
	private String BISQUE_TRIAL_NOTIFICATION_CB;

	@Value("${dental.lab.finished.message.cb}")
	private String FINISHED_LAB_NOTIFICATION_CB;

	private static Logger logger = Logger.getLogger(DentalLabServiceImpl.class.getName());

	@Override
	@Transactional
	public DentalWork addEditCustomWork(AddEditCustomWorkRequest request) {
		DentalWork response = null;
		try {
			DentalWorkCollection dentalWorkCollection = new DentalWorkCollection();
			BeanUtil.map(request, dentalWorkCollection);
			if (DPDoctorUtils.anyStringEmpty(dentalWorkCollection.getId())) {
				dentalWorkCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(dentalWorkCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(dentalWorkCollection.getDoctorId());
					if (userCollection != null) {
						dentalWorkCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					dentalWorkCollection.setCreatedBy("ADMIN");
				}
			} else {
				DentalWorkCollection oldDentalWorkCollection = dentalWorkRepository
						.findOne(dentalWorkCollection.getId());
				dentalWorkCollection.setCreatedBy(oldDentalWorkCollection.getCreatedBy());
				dentalWorkCollection.setCreatedTime(oldDentalWorkCollection.getCreatedTime());
				dentalWorkCollection.setDiscarded(oldDentalWorkCollection.getDiscarded());
			}
			dentalWorkCollection = dentalWorkRepository.save(dentalWorkCollection);
			response = new DentalWork();
			BeanUtil.map(dentalWorkCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalWork> getCustomWorks(int page, int size, String searchTerm) {
		List<DentalWork> customWorks = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("workName").regex("^" + searchTerm, "i"),
						new Criteria("workName").regex("^" + searchTerm));
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<DentalWork> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalWorkCollection.class, DentalWork.class);
			customWorks = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting custom works");
			throw new BusinessException(ServiceError.Unknown, "Error Getting custom works");
		}
		return customWorks;
	}

	@Override
	@Transactional
	public DentalWork deleteCustomWork(String id, boolean discarded) {
		DentalWork response = null;
		DentalWorkCollection customWorkCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				customWorkCollection = dentalWorkRepository.findOne(new ObjectId(id));
			}
			if (customWorkCollection != null) {
				customWorkCollection.setDiscarded(discarded);
				customWorkCollection = dentalWorkRepository.save(customWorkCollection);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}
			response = new DentalWork();
			BeanUtil.map(customWorkCollection, response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean changeLabType(String doctorId, String locationId, LabType labType) {
		Boolean response = false;
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository
					.findByDoctorIdLocationId(new ObjectId(doctorId), new ObjectId(locationId));
			if (doctorClinicProfileCollection != null) {
				doctorClinicProfileCollection.setLabType(labType.getType());
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public DentalLabDoctorAssociation addEditDentalLabDoctorAssociation(DentalLabDoctorAssociation request) {
		DentalLabDoctorAssociation response = null;
		try {
			DentalLabDoctorAssociationCollection dentalLabDoctorAssociationCollection = new DentalLabDoctorAssociationCollection();
			BeanUtil.map(request, dentalLabDoctorAssociationCollection);
			if (DPDoctorUtils.anyStringEmpty(dentalLabDoctorAssociationCollection.getId())) {
				dentalLabDoctorAssociationCollection.setCreatedTime(new Date());

			} else {
				DentalLabDoctorAssociationCollection oldDentalLabDoctorAssociation = dentalLabDoctorAssociationRepository
						.findOne(dentalLabDoctorAssociationCollection.getId());
				dentalLabDoctorAssociationCollection.setCreatedBy(oldDentalLabDoctorAssociation.getCreatedBy());
				dentalLabDoctorAssociationCollection.setCreatedTime(oldDentalLabDoctorAssociation.getCreatedTime());
			}
			dentalLabDoctorAssociationCollection = dentalLabDoctorAssociationRepository
					.save(dentalLabDoctorAssociationCollection);
			response = new DentalLabDoctorAssociation();
			BeanUtil.map(dentalLabDoctorAssociationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditDentalLabDoctorAssociation(List<DentalLabDoctorAssociation> request) {
		Boolean response = null;
		try {
			for (DentalLabDoctorAssociation dentalLabDoctorAssociation : request) {
				DentalLabDoctorAssociationCollection dentalLabDoctorAssociationCollection = new DentalLabDoctorAssociationCollection();
				BeanUtil.map(dentalLabDoctorAssociation, dentalLabDoctorAssociationCollection);
				if (DPDoctorUtils.anyStringEmpty(dentalLabDoctorAssociationCollection.getId())) {
					dentalLabDoctorAssociationCollection.setCreatedTime(new Date());

				} else {
					DentalLabDoctorAssociationCollection oldDentalLabDoctorAssociation = dentalLabDoctorAssociationRepository
							.findOne(dentalLabDoctorAssociationCollection.getId());
					dentalLabDoctorAssociationCollection.setCreatedBy(oldDentalLabDoctorAssociation.getCreatedBy());
					dentalLabDoctorAssociationCollection.setCreatedTime(oldDentalLabDoctorAssociation.getCreatedTime());
				}
				dentalLabDoctorAssociationCollection = dentalLabDoctorAssociationRepository
						.save(dentalLabDoctorAssociationCollection);
				/*
				 * response = new DentalLabDoctorAssociation();
				 * BeanUtil.map(dentalLabDoctorAssociationCollection, response);
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<User> getDentalLabDoctorAssociations(String locationId, String doctorId, int page, int size,
			String searchTerm) {
		List<DentalLabDoctorAssociationLookupResponse> customWorks = null;
		List<User> users = new ArrayList<>();
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria = new Criteria().and("dentalLabId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = new Criteria().and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm));
			}
			criteria.and("isActive").is(true);

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<DentalLabDoctorAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(
					aggregation, DentalLabDoctorAssociationCollection.class,
					DentalLabDoctorAssociationLookupResponse.class);
			customWorks = aggregationResults.getMappedResults();

			for (DentalLabDoctorAssociationLookupResponse doctorAssociationLookupResponse : customWorks) {
				users.add(doctorAssociationLookupResponse.getDoctor());
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error getting dental lab doctor association");
			throw new BusinessException(ServiceError.Unknown, "Error getting dental lab doctor association");
		}
		return users;
	}

	@Override
	@Transactional
	public List<Location> getDentalLabDoctorAssociationsForDoctor(String doctorId, int page, int size,
			String searchTerm) {
		List<DentalLabDoctorAssociationLookupResponse> customWorks = null;
		List<Location> locations = new ArrayList<>();
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = new Criteria().and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm));
			}
			criteria.and("dentalLab.isDentalWorksLab").is(true);
			criteria.and("isActive").is(true);
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<DentalLabDoctorAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(
					aggregation, DentalLabDoctorAssociationCollection.class,
					DentalLabDoctorAssociationLookupResponse.class);
			customWorks = aggregationResults.getMappedResults();

			for (DentalLabDoctorAssociationLookupResponse doctorAssociationLookupResponse : customWorks) {
				locations.add(doctorAssociationLookupResponse.getDentalLab());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error getting dental lab doctor association");
			throw new BusinessException(ServiceError.Unknown, "Error getting dental lab doctor association");
		}
		return locations;
	}

	@Override
	@Transactional
	public DentalLabPickup addEditDentalLabPickupRequest(DentalLabPickupRequest request) {
		DentalLabPickup response = null;
		DentalLabPickupCollection dentalLabPickupCollection = null;
		String requestId = null;
		String locationInitials = "";
		Integer serialNo = null;
		Integer count = 1;
		List<DentalWorksSample> dentalWorksSamples = new ArrayList<>();
		List<DentalStage> dentalStages = null;

		try {
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getDentalLabId()));
			if (locationCollection != null) {
				String locationName = locationCollection.getLocationName();
				for (String firstChar : locationName.split(" ")) {
					if (!firstChar.isEmpty()) {
						locationInitials += firstChar.charAt(0);
					}
				}
			}
			if (request.getId() != null) {
				dentalLabPickupCollection = dentalLabTestPickupRepository.findOne(new ObjectId(request.getId()));
				if (dentalLabPickupCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Record not found");
				}
				BeanUtil.map(request, dentalLabPickupCollection);
				for (DentalWorksSampleRequest dentalWorksSampleRequest : request.getDentalWorksSamples()) {
					DentalWorksSample dentalWorksSample = new DentalWorksSample();
					BeanUtil.map(dentalWorksSampleRequest, dentalWorksSample);
					dentalWorksSample.setDentalToothNumbers(dentalWorksSampleRequest.getDentalToothNumbers());
					if (dentalWorksSampleRequest.getDentalStagesForDoctor() != null) {
						dentalStages = new ArrayList<>();
						for (DentalStageRequest dentalStageRequest : dentalWorksSampleRequest
								.getDentalStagesForDoctor()) {
							DentalStage dentalStage = new DentalStage();
							BeanUtil.map(dentalStageRequest, dentalStage);
							dentalStages.add(dentalStage);
						}
						dentalWorksSample.setDentalStagesForDoctor(dentalStages);
					}

					if (dentalWorksSampleRequest.getDentalStagesForLab() != null) {
						dentalStages = new ArrayList<>();
						for (DentalStageRequest dentalStageRequest : dentalWorksSampleRequest.getDentalStagesForLab()) {
							DentalStage dentalStage = new DentalStage();
							BeanUtil.map(dentalStageRequest, dentalStage);
							dentalStages.add(dentalStage);
						}
						dentalWorksSample.setDentalStagesForLab(dentalStages);
					}
					dentalWorksSamples.add(dentalWorksSample);
				}
				dentalLabPickupCollection.setDentalWorksSamples(dentalWorksSamples);
				dentalLabPickupCollection.setUpdatedTime(new Date());
				dentalLabPickupCollection = dentalLabTestPickupRepository.save(dentalLabPickupCollection);
			} else {
				requestId = UniqueIdInitial.DENTAL_LAB_PICKUP_REQUEST.getInitial() + DPDoctorUtils.generateRandomId();
				request.setCrn(saveCRN(request.getDentalLabId(), requestId, 5));
				dentalLabPickupCollection = new DentalLabPickupCollection();
				BeanUtil.map(request, dentalLabPickupCollection);
				dentalLabPickupCollection.setRequestId(requestId);

				serialNo = dentalLabTestPickupRepository.findTodaysCompletedReport(
						new ObjectId(request.getDentalLabId()), DPDoctorUtils.getFormTime(new Date()),
						DPDoctorUtils.getToTime(new Date()));
				for (DentalWorksSampleRequest dentalWorksSampleRequest : request.getDentalWorksSamples()) {
					String uniqueWorkId = locationInitials + getInitials(request.getPatientName())
							+ currentDateGenerator() + DPDoctorUtils.getPrefixedNumber(serialNo + 1)
							+ DPDoctorUtils.getPrefixedNumber(count);
					dentalWorksSampleRequest.setUniqueWorkId(uniqueWorkId);
					DentalWorksSample dentalWorksSample = new DentalWorksSample();
					BeanUtil.map(dentalWorksSampleRequest, dentalWorksSample);
					dentalWorksSample.setDentalToothNumbers(dentalWorksSampleRequest.getDentalToothNumbers());
					if (dentalWorksSampleRequest.getDentalStagesForDoctor() != null) {
						dentalStages = new ArrayList<>();
						for (DentalStageRequest dentalStageRequest : dentalWorksSampleRequest
								.getDentalStagesForDoctor()) {
							DentalStage dentalStage = new DentalStage();
							BeanUtil.map(dentalStageRequest, dentalStage);
							dentalStages.add(dentalStage);
						}
						dentalWorksSample.setDentalStagesForDoctor(dentalStages);
					}

					if (dentalWorksSampleRequest.getDentalStagesForLab() != null) {
						dentalStages = new ArrayList<>();
						for (DentalStageRequest dentalStageRequest : dentalWorksSampleRequest.getDentalStagesForLab()) {
							DentalStage dentalStage = new DentalStage();
							BeanUtil.map(dentalStageRequest, dentalStage);
							dentalStages.add(dentalStage);
						}
						dentalWorksSample.setDentalStagesForLab(dentalStages);
					}
					dentalWorksSamples.add(dentalWorksSample);

				}
				if (request.getRequestCreatedBy() != null) {
					if (request.getRequestCreatedBy().equals("DENTAL_LAB")) {
						List<UserDeviceCollection> userDeviceCollections = userDeviceRepository
								.findByUserId(new ObjectId(request.getDoctorId()));
						if (userDeviceCollections != null) {
							String message = locationCollection.getLocationName() + " has created a request for you.";
							pushNotificationServices.notifyUser(request.getDoctorId(), message,
									ComponentType.DENTAL_WORKS.getType(), null, userDeviceCollections);
						} else {
							sendDownloadAppMessage(new ObjectId(request.getDoctorId()),
									locationCollection.getLocationName());
						}
					}
				}

				dentalLabPickupCollection.setDentalWorksSamples(dentalWorksSamples);
				dentalLabPickupCollection.setCrn(saveCRN(request.getDentalLabId(), requestId, 5));
				dentalLabPickupCollection.setSerialNumber(String.valueOf(serialNo + 1));
				dentalLabPickupCollection.setCreatedTime(new Date());
				dentalLabPickupCollection.setIsCompleted(false);
				dentalLabPickupCollection.setStatus(request.getStatus());
				dentalLabPickupCollection.setUpdatedTime(new Date());
			}
			DynamicCollectionBoyAllocationCollection dynamicCollectionBoyAllocationCollection = dynamicCollectionBoyAllocationRepository
					.getByAssignorAssignee(new ObjectId(request.getDentalLabId()), new ObjectId(request.getDoctorId()));
			if (dynamicCollectionBoyAllocationCollection != null
					&& (dynamicCollectionBoyAllocationCollection.getFromTime() <= System.currentTimeMillis()
							&& System.currentTimeMillis() <= dynamicCollectionBoyAllocationCollection.getToTime())) {
				dentalLabPickupCollection
						.setCollectionBoyId(dynamicCollectionBoyAllocationCollection.getCollectionBoyId());
				if (dentalLabPickupCollection.getCollectionBoyId() != null && (!dentalLabPickupCollection
						.getCollectionBoyId().equals(dynamicCollectionBoyAllocationCollection.getCollectionBoyId()))) {
					CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
							.findOne(dynamicCollectionBoyAllocationCollection.getCollectionBoyId());
					pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(), null, null,
							RoleEnum.DENTAL_COLLECTION_BOY, COLLECTION_BOY_NOTIFICATION);
				}
			} else {
				CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
						.getByLocationDoctorIsActive(new ObjectId(request.getDentalLabId()),
								new ObjectId(request.getDoctorId()), true);
				if (collectionBoyDoctorAssociationCollection != null) {
					dentalLabPickupCollection
							.setCollectionBoyId(collectionBoyDoctorAssociationCollection.getCollectionBoyId());
					if (dentalLabPickupCollection.getCollectionBoyId() != null
							&& (!dentalLabPickupCollection.getCollectionBoyId()
									.equals(collectionBoyDoctorAssociationCollection.getCollectionBoyId()))) {
						CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
								.findOne(collectionBoyDoctorAssociationCollection.getCollectionBoyId());
						pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(), null,
								null, RoleEnum.DENTAL_COLLECTION_BOY, COLLECTION_BOY_NOTIFICATION);
					}
				}
			}

			dentalLabPickupCollection = dentalLabTestPickupRepository.save(dentalLabPickupCollection);
			response = new DentalLabPickup();
			BeanUtil.map(dentalLabPickupCollection, response);
			if(locationCollection != null)
			{
				Location location = new Location();
				BeanUtil.map(locationCollection, location);
				response.setDentalLab(location);
			}
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	private String saveCRN(String locationId, String requestId, Integer length) {
		CRNCollection crnCollection = new CRNCollection();
		String crnNumber = DPDoctorUtils.randomString(length);
		crnCollection.setCrnNumber(crnNumber);
		crnCollection.setLocationId(new ObjectId(locationId));
		crnCollection.setRequestId(requestId);
		crnCollection.setCreatedAt(System.currentTimeMillis());
		crnCollection.setIsUsed(false);
		crnCollection = crnRepository.save(crnCollection);
		return crnNumber;
	}

	@Override
	@Transactional
	public Boolean addEditRateCardDentalWorkAssociation(List<RateCardDentalWorkAssociation> request) {
		Boolean response = false;
		ObjectId oldId = null;
		RateCardDentalWorkAssociationCollection rateCardDentalWorkAssociationCollection = null;
		try {
			for (RateCardDentalWorkAssociation rateCardDentalWorkAssociation : request) {
				rateCardDentalWorkAssociationCollection = rateCardDentalWorkAssociationRepository
						.getByLocationWorkRateCard(new ObjectId(rateCardDentalWorkAssociation.getLocationId()),
								new ObjectId(rateCardDentalWorkAssociation.getDentalWorkId()),
								new ObjectId(rateCardDentalWorkAssociation.getRateCardId()));
				if (rateCardDentalWorkAssociationCollection == null) {
					rateCardDentalWorkAssociationCollection = new RateCardDentalWorkAssociationCollection();
				} else {
					oldId = rateCardDentalWorkAssociationCollection.getId();
				}
				BeanUtil.map(rateCardDentalWorkAssociation, rateCardDentalWorkAssociationCollection);
				rateCardDentalWorkAssociationCollection.setId(oldId);
				rateCardDentalWorkAssociationCollection = rateCardDentalWorkAssociationRepository
						.save(rateCardDentalWorkAssociationCollection);
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
	public List<RateCardDentalWorkAssociation> getRateCardWorks(int page, int size, String searchTerm,
			String dentalLabId, String doctorId, Boolean discarded) {
		List<RateCardDentalWorkAssociation> rateCardTests = null;

		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			RateCardDoctorAssociationCollection rateCardDoctorAssociationCollection = rateCardDoctorAssociationRepository
					.getByLocationDoctor(new ObjectId(dentalLabId), new ObjectId(doctorId));
			if (rateCardDoctorAssociationCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Association not found");
			} else {
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					criteria = criteria.orOperator(new Criteria("dentalWork.workName").regex("^" + searchTerm, "i"),
							new Criteria("dentalWork.workName").regex("^" + searchTerm));
				}
				criteria.and("rateCardId").is(rateCardDoctorAssociationCollection.getRateCardId());
				criteria.and("isAvailable").is(true);
				// criteria.and("discarded").is(discarded);

			}
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("dental_work_cl", "dentalWorkId", "_id", "dentalWork"),
						Aggregation.unwind("dentalWork"),
						/*
						 * Aggregation.lookup("specimen_cl", "diagnosticTest.specimenId", "_id",
						 * "specimen"), Aggregation.unwind("specimen"),
						 */
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("dental_work_cl", "dentalWorkId", "_id", "dentalWork"),
						Aggregation.unwind("dentalWork"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			}
			AggregationResults<RateCardDentalWorkAssociation> aggregationResults = mongoTemplate.aggregate(aggregation,
					RateCardDentalWorkAssociationCollection.class, RateCardDentalWorkAssociation.class);
			rateCardTests = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate card works");
			throw new BusinessException(ServiceError.Unknown, "Error Getting rate card works");
		}
		return rateCardTests;
	}

	@Override
	@Transactional
	public List<RateCardDentalWorkAssociation> getRateCardWorks(int page, int size, String searchTerm,
			String rateCardId, Boolean discarded) {
		List<RateCardDentalWorkAssociation> rateCardTests = null;

		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("dentalWork.workName").regex("^" + searchTerm, "i"),
						new Criteria("dentalWork.workName").regex("^" + searchTerm),
						new Criteria("dentalWork.workName").regex(searchTerm + ".*"));
			}
			criteria.and("rateCardId").is(new ObjectId(rateCardId));
			criteria.and("isAvailable").is(true);
			criteria.and("discarded").is(discarded);

			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("dental_work_cl", "dentalWorkId", "_id", "dentalWork"),
						Aggregation.unwind("dentalWork"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("dental_work_cl", "dentalWorkId", "_id", "dentalWork"),
						Aggregation.unwind("dentalWork"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			}

			AggregationResults<RateCardDentalWorkAssociation> aggregationResults = mongoTemplate.aggregate(aggregation,
					RateCardDentalWorkAssociationCollection.class, RateCardDentalWorkAssociation.class);

			rateCardTests = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate card works");
			throw new BusinessException(ServiceError.Unknown, "Error Getting rate card works");
		}
		return rateCardTests;
	}

	@Override
	@Transactional
	public RateCardDoctorAssociation addEditRateCardDoctorAssociation(RateCardDoctorAssociation request) {
		RateCardDoctorAssociation response = null;
		ObjectId oldId = null;
		RateCardDoctorAssociationCollection rateCardDoctorAssociationCollection = null;
		try {

			rateCardDoctorAssociationCollection = rateCardDoctorAssociationRepository
					.getByLocationDoctor(new ObjectId(request.getDentalLabId()), new ObjectId(request.getDoctorId()));
			if (rateCardDoctorAssociationCollection == null) {
				rateCardDoctorAssociationCollection = new RateCardDoctorAssociationCollection();
			} else {
				oldId = rateCardDoctorAssociationCollection.getId();
				// rateCardLabAssociationCollection.setId(rateCardLabAssociationCollection.getId());
			}

			BeanUtil.map(request, rateCardDoctorAssociationCollection);
			rateCardDoctorAssociationCollection.setId(oldId);
			rateCardDoctorAssociationCollection = rateCardDoctorAssociationRepository
					.save(rateCardDoctorAssociationCollection);
			response = new RateCardDoctorAssociation();
			BeanUtil.map(rateCardDoctorAssociationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<RateCardDoctorAssociation> getRateCards(int page, int size, String searchTerm, String doctorId,
			String dentalLabId, Boolean discarded) {
		List<RateCardDoctorAssociation> rateCardTests = null;

		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("rateCard.name").regex("^" + searchTerm, "i"),
						new Criteria("rateCard.name").regex("^" + searchTerm));
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalLabId)) {
				criteria.and("dentalLabId").is(new ObjectId(dentalLabId));
			}
			criteria.and("discarded").is(discarded);

			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("rate_card_cl", "rateCardId", "_id", "rateCard"),
						Aggregation.unwind("rateCard"),
						/*
						 * Aggregation.lookup("specimen_cl", "diagnosticTest.specimenId", "_id",
						 * "specimen"), Aggregation.unwind("specimen"),
						 */
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("rate_card_cl", "rateCardId", "_id", "rateCard"),
						Aggregation.unwind("rateCard"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			}
			AggregationResults<RateCardDoctorAssociation> aggregationResults = mongoTemplate.aggregate(aggregation,
					RateCardDoctorAssociationCollection.class, RateCardDoctorAssociation.class);
			rateCardTests = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate card works");
			throw new BusinessException(ServiceError.Unknown, "Error Getting rate card works");
		}
		return rateCardTests;
	}

	@Override
	@Transactional
	public Boolean addEditCollectionBoyDoctorAssociation(List<CollectionBoyDoctorAssociation> request) {
		Boolean response = null;
		CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = null;
		try {
			for (CollectionBoyDoctorAssociation collectionBoyDoctorAssociation : request) {
				if (DPDoctorUtils.anyStringEmpty(collectionBoyDoctorAssociation.getCollectionBoyId(),
						collectionBoyDoctorAssociation.getDentalLabId(),
						collectionBoyDoctorAssociation.getDoctorId())) {
					throw new BusinessException(ServiceError.InvalidInput,
							"Invalid Input - Doctor & Dental Lab ID cannot be null");
				}
				collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository.getByLocationDoctor(
						new ObjectId(collectionBoyDoctorAssociation.getDentalLabId()),
						new ObjectId(collectionBoyDoctorAssociation.getDoctorId()));
				if (collectionBoyDoctorAssociationCollection == null) {
					collectionBoyDoctorAssociationCollection = new CollectionBoyDoctorAssociationCollection();
					BeanUtil.map(collectionBoyDoctorAssociation, collectionBoyDoctorAssociationCollection);
				} else {
					if (!collectionBoyDoctorAssociationCollection.getCollectionBoyId()
							.equals(new ObjectId(collectionBoyDoctorAssociation.getCollectionBoyId()))
							&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
						CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
								.findOne(collectionBoyDoctorAssociationCollection.getCollectionBoyId());
						UserCollection userCollection = userRepository
								.findOne(collectionBoyDoctorAssociationCollection.getDoctorId());
						throw new BusinessException(ServiceError.Unknown,
								"Collection boy " + collectionBoyCollection.getName() + " is already assigned to Dr. "
										+ userCollection.getFirstName()
										+ ". Please select another lab / collection boy");
					}
					ObjectId oldId = collectionBoyDoctorAssociationCollection.getId();
					BeanUtil.map(collectionBoyDoctorAssociation, collectionBoyDoctorAssociationCollection);
					collectionBoyDoctorAssociationCollection.setId(oldId);
				}
				collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
						.save(collectionBoyDoctorAssociationCollection);
			}

			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<CBDoctorAssociationLookupResponse> getCBAssociatedDoctors(String doctorId, String dentalLabId,
			String collectionBoyId, int size, int page) {
		List<User> users = null;
		List<CBDoctorAssociationLookupResponse> lookupResponses = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalLabId)) {
				criteria.and("dentalLabId").is(new ObjectId(dentalLabId));
			}
			if (!DPDoctorUtils.anyStringEmpty(collectionBoyId)) {
				criteria.and("collectionBoyId").is(new ObjectId(collectionBoyId));
			}
			criteria.and("isActive").is(true);
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<CBDoctorAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(
					aggregation, CollectionBoyDoctorAssociationCollection.class,
					CBDoctorAssociationLookupResponse.class);
			lookupResponses = aggregationResults.getMappedResults();
			/*
			 * if (lookupResponses != null) { users = new ArrayList<User>(); for
			 * (CBDoctorAssociationLookupResponse lookupResponse : lookupResponses) {
			 * users.add(lookupResponse.getDoctor()); } }
			 */

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.warn(e);
		}
		return lookupResponses;
	}

	@Override
	@Transactional
	public List<DentalLabPickupResponse> getRequests(String dentalLabId, String doctorId, Long from, Long to,
			String searchTerm, String status, Boolean isAcceptedAtLab, Boolean isCompleted, Boolean isCollectedAtDoctor,
			int size, int page, Long fromETA, Long toETA, Boolean isTrailsRequired) {

		List<DentalLabPickupResponse> response = null;
		List<DentalLabPickupLookupResponse> lookupResponses = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(dentalLabId)) {
				criteria.and("dentalLabId").is(new ObjectId(dentalLabId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (isAcceptedAtLab != null) {
				criteria.and("isAcceptedAtLab").is(isAcceptedAtLab);
			}

			if (isCollectedAtDoctor != null) {
				criteria.and("isCollectedAtDoctor").is(isCollectedAtDoctor);
			}

			if (isCompleted != null) {
				criteria.and("isCompleted").is(isCompleted);
			}

			if (!DPDoctorUtils.anyStringEmpty(status)) {
				criteria.and("status").is(status);
			}

			if (to != null) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else {
				criteria.and("updatedTime").gte(new Date(from));
			}

			if (fromETA != null && toETA != null) {
				criteria.and("dentalWorksSamples.etaInDate").gte(fromETA).lte(toETA);
			}

			/*
			 * if (from != 0 && {
			 * 
			 * } else if (from != 0) { criteria.and("updatedTime").gte(new Date(from)); }
			 * else if (to != null) {
			 * criteria.and("updatedTime").lt(DPDoctorUtils.getEndTime(new Date(to))); }
			 */

			CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", "$_id").append("patientId", new BasicDBObject("$first", "$patientId"))
							.append("patientName", new BasicDBObject("$first", "$patientName"))
							.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
							.append("dentalWorksSamples", new BasicDBObject("$push", "$dentalWorksSamples"))
							.append("gender", new BasicDBObject("$first", "$gender"))
							.append("age", new BasicDBObject("$first", "$age"))
							.append("crn", new BasicDBObject("$first", "$crn"))
							.append("pickupTime", new BasicDBObject("$first", "$pickupTime"))
							.append("deliveryTime", new BasicDBObject("$first", "$deliveryTime"))
							.append("status", new BasicDBObject("$first", "$status"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("dentalLabId", new BasicDBObject("$first", "$dentalLabId"))
							.append("numberOfSamplesRequested",
									new BasicDBObject("$first", "$numberOfSamplesRequested"))
							.append("numberOfSamplesPicked", new BasicDBObject("$first", "$numberOfSamplesPicked"))
							.append("requestId", new BasicDBObject("$first", "$requestId"))
							.append("isCompleted", new BasicDBObject("$first", "$isCompleted"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))
							.append("isAcceptedAtLab", new BasicDBObject("$first", "$isAcceptedAtLab"))
							.append("isCollectedAtDoctor", new BasicDBObject("$first", "$isCollectedAtDoctor"))
							.append("collectionBoyId", new BasicDBObject("$first", "$collectionBoyId"))
							.append("serialNumber", new BasicDBObject("$first", "$serialNumber"))
							.append("reasonForCancel", new BasicDBObject("$first", "$reasonForCancel"))
							.append("cancelledBy", new BasicDBObject("$first", "$cancelledBy"))
							.append("collectionBoy", new BasicDBObject("$first", "$collectionBoy"))
							.append("dentalLab", new BasicDBObject("$first", "$dentalLab"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("doctor", new BasicDBObject("$first", "$doctor"))
							.append("feedBackRating", new BasicDBObject("$first", "$feedBackRating"))
							.append("feedBackComment", new BasicDBObject("$first", "$feedBackComment"))));

			/*
			 * private DentalWork dentalWork; private List<DentalToothNumber>
			 * dentalToothNumbers; private List<DentalStage> dentalStagesForLab; private
			 * Long etaInDate; private Integer etaInHour; private Boolean isCompleted =
			 * false; private Boolean isUrgent = false; private String instructions; private
			 * String occlusalStaining; private String ponticDesign; private String
			 * collarAndMetalDesign; private String uniqueWorkId; private
			 * List<ImageURLResponse> dentalImages; private List<DentalWorkCardValue>
			 * dentalWorkCardValues; private String shade; private List<String> material;
			 * private List<DentalStage> dentalStagesForDoctor; private
			 * RateCardDentalWorkAssociation rateCardDentalWorkAssociation; private String
			 * processStatus;
			 */

			CustomAggregationOperation aggregationOperation2 = new CustomAggregationOperation(new BasicDBObject(
					"$group",
					new BasicDBObject("uniqueWorkId", "$uniqueWorkId")
							.append("dentalWork", new BasicDBObject("$first", "$dentalWork"))
							.append("dentalToothNumbers", new BasicDBObject("$first", "$dentalToothNumbers"))
							.append("dentalStagesForLab", new BasicDBObject("$first", "$dentalStagesForLab"))
							.append("dentalStagesForDoctor", new BasicDBObject("$push", "$dentalStagesForDoctor"))
							.append("etaInDate", new BasicDBObject("$first", "$etaInDate"))
							.append("etaInHour", new BasicDBObject("$first", "$etaInHour"))
							.append("instructions", new BasicDBObject("$first", "$instructions"))
							.append("occlusalStaining", new BasicDBObject("$first", "$occlusalStaining"))
							.append("ponticDesign", new BasicDBObject("$first", "$ponticDesign"))
							.append("collarAndMetalDesign", new BasicDBObject("$first", "$collarAndMetalDesign"))
							.append("dentalImages", new BasicDBObject("$first", "$dentalImages"))
							.append("dentalWorkCardValues", new BasicDBObject("$first", "$dentalWorkCardValues"))
							.append("shade", new BasicDBObject("$first", "$shade"))
							.append("material", new BasicDBObject("$first", "$material"))
							.append("rateCardDentalWorkAssociation",
									new BasicDBObject("$first", "$rateCardDentalWorkAssociation"))
							.append("processStatus", new BasicDBObject("$first", "$processStatus"))));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("dentalLab.locationName").regex("^" + searchTerm, "i"),
						new Criteria("dentalLab.locationName").regex("^" + searchTerm),
						new Criteria("dentalLab.locationName").regex(searchTerm + ".*"),
						new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm),
						new Criteria("doctor.firstName").regex(searchTerm + ".*"),
						new Criteria("patientName").regex("^" + searchTerm, "i"),
						new Criteria("patientName").regex("^" + searchTerm),
						new Criteria("patientName").regex(searchTerm + ".*"),
						new Criteria("dentalWorksSamples.uniqueWorkId").regex("^" + searchTerm, "i"),
						new Criteria("dentalWorksSamples.uniqueWorkId").regex("^" + searchTerm),
						new Criteria("dentalWorksSamples.uniqueWorkId").regex(searchTerm + "$", "i"),
						new Criteria("dentalWorksSamples.uniqueWorkId").regex(searchTerm + "$"),
						new Criteria("dentalWorksSamples.uniqueWorkId").regex(searchTerm + ".*"));
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.unwind("dentalWorksSamples"),
						// Aggregation.unwind("dentalWorksSamples.dentalStagesForDoctor"),
						Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("collection_boy_cl", "collectionBoyId", "_id", "collectionBoy"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$collectionBoy").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.match(criteria), aggregationOperation,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.unwind("dentalWorksSamples"),
						// Aggregation.unwind("dentalWorksSamples.dentalStagesForDoctor"),
						Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("collection_boy_cl", "collectionBoyId", "_id", "collectionBoy"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",

								new BasicDBObject("path", "$collectionBoy").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.match(criteria), aggregationOperation,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalLabPickupLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalLabPickupCollection.class, DentalLabPickupLookupResponse.class);
			lookupResponses = aggregationResults.getMappedResults();

			if (lookupResponses != null) {
				response = new ArrayList<>();

				for (DentalLabPickupLookupResponse dentalLabPickupLookupResponse : lookupResponses) {
					List<DentalStageRequest> dentalStageRequestsForDoctor = null;
					List<DentalStageRequest> dentalStageRequestsForLab = null;
					List<DentalWorksSampleRequest> dentalWorksSampleRequests = new ArrayList<>();
					DentalLabPickupResponse dentalLabPickupResponse = new DentalLabPickupResponse();
					BeanUtil.map(dentalLabPickupLookupResponse, dentalLabPickupResponse);

					for (DentalWorksSample dentalWorksSample : dentalLabPickupLookupResponse.getDentalWorksSamples()) {
						DentalWorksSampleRequest dentalWorksSampleRequest = new DentalWorksSampleRequest();
						BeanUtil.map(dentalWorksSample, dentalWorksSampleRequest);
						if (dentalWorksSample.getDentalStagesForDoctor() != null) {
							dentalStageRequestsForDoctor = new ArrayList<>();
							for (DentalStage dentalStage : dentalWorksSample.getDentalStagesForDoctor()) {
								DentalStageRequest dentalStageRequest = new DentalStageRequest();
								BeanUtil.map(dentalStage, dentalStageRequest);
								dentalStageRequestsForDoctor.add(dentalStageRequest);
							}
						}
						if (dentalWorksSample.getDentalStagesForLab() != null) {
							dentalStageRequestsForLab = new ArrayList<>();
							for (DentalStage dentalStage : dentalWorksSample.getDentalStagesForLab()) {
								DentalStageRequest dentalStageRequest = new DentalStageRequest();
								BeanUtil.map(dentalStage, dentalStageRequest);
								dentalStageRequestsForLab.add(dentalStageRequest);
							}
						}
						dentalWorksSampleRequest.setDentalStagesForDoctor(dentalStageRequestsForDoctor);
						dentalWorksSampleRequest.setDentalStagesForLab(dentalStageRequestsForLab);
						dentalWorksSampleRequests.add(dentalWorksSampleRequest);
						dentalLabPickupResponse.setDentalWorksSamples(dentalWorksSampleRequests);
						response.add(dentalLabPickupResponse);
					}

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	private String currentDateGenerator() {

		String currentDate = null;
		try {
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			localCalendar.setTime(new Date());
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			currentDate = DPDoctorUtils.getPrefixedNumber(currentDay) + DPDoctorUtils.getPrefixedNumber(currentMonth)
					+ DPDoctorUtils.getPrefixedNumber(currentYear % 100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return currentDate;
	}

	private String getInitials(String inputString) {
		String response = "";
		for (String firstChar : inputString.split(" ")) {
			response += firstChar.charAt(0);
		}
		return response;
	}

	@Override
	@Transactional

	public Boolean changeStatus(DentalLabPickupChangeStatusRequest request) {
		DentalLabPickupCollection dentalLabPickupCollection = null;
		Boolean response = null;

		try {
			dentalLabPickupCollection = dentalLabTestPickupRepository
					.findOne(new ObjectId(request.getDentalLabPickupId()));
			if (dentalLabPickupCollection != null) {

				if (request.getStatus() != null) {
					dentalLabPickupCollection.setStatus(request.getStatus());

					if (request.getStatus().equals("ACCEPTED")) {
						if (dentalLabPickupCollection.getDoctorId() != null) {

							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									ACCEPTED_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);

						}
						if (dentalLabPickupCollection.getCollectionBoyId() != null) {
							CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
									.getByLocationDoctorCollectionBoy(dentalLabPickupCollection.getDentalLabId(),
											dentalLabPickupCollection.getDoctorId(),
											dentalLabPickupCollection.getCollectionBoyId());
							if (collectionBoyDoctorAssociationCollection != null
									&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
								CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
										.findOne(dentalLabPickupCollection.getCollectionBoyId());
								if (collectionBoyCollection != null) {
									pushNotificationServices.notifyPharmacy(
											collectionBoyCollection.getUserId().toString(),
											dentalLabPickupCollection.getId().toString(), null,
											RoleEnum.DENTAL_COLLECTION_BOY, COLLECTION_BOY_NOTIFICATION);
								}
							}
						}
					} else if (request.getStatus().equals("OUT_FOR_COLLECTION")) {

						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									"Work is out for collection!", ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

					} else if (request.getStatus().equals("COPING_TRIAL")) {
						if (dentalLabPickupCollection.getDoctorId() != null) {

							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									COPING_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}
						if (dentalLabPickupCollection.getCollectionBoyId() != null) {
							CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
									.findOne(dentalLabPickupCollection.getCollectionBoyId());
							CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
									.getByLocationDoctorCollectionBoy(dentalLabPickupCollection.getDentalLabId(),
											dentalLabPickupCollection.getDoctorId(),
											dentalLabPickupCollection.getCollectionBoyId());
							if (collectionBoyDoctorAssociationCollection != null
									&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
								if (collectionBoyCollection != null) {
									pushNotificationServices.notifyPharmacy(
											collectionBoyCollection.getUserId().toString(), null, null,
											RoleEnum.DENTAL_COLLECTION_BOY, COPING_TRIAL_NOTIFICATION_CB);
								}
							}
						}
					} else if (request.getStatus().equals("BISQUE_TRIAL")) {
						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									BISQUE_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}
						if (dentalLabPickupCollection.getCollectionBoyId() != null) {
							CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
									.findOne(dentalLabPickupCollection.getCollectionBoyId());
							CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
									.getByLocationDoctorCollectionBoy(dentalLabPickupCollection.getDentalLabId(),
											dentalLabPickupCollection.getDoctorId(),
											dentalLabPickupCollection.getCollectionBoyId());
							if (collectionBoyDoctorAssociationCollection != null
									&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
								if (collectionBoyCollection != null) {
									pushNotificationServices.notifyPharmacy(
											collectionBoyCollection.getUserId().toString(), null, null,
											RoleEnum.DENTAL_COLLECTION_BOY, BISQUE_TRIAL_NOTIFICATION_CB);
								}
							}
						}
					} else if (request.getStatus().equals("FINISHED_LAB")) {
						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									FINISHED_LAB_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						if (dentalLabPickupCollection.getCollectionBoyId() != null) {
							CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
									.findOne(dentalLabPickupCollection.getCollectionBoyId());
							CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
									.getByLocationDoctorCollectionBoy(dentalLabPickupCollection.getDentalLabId(),
											dentalLabPickupCollection.getDoctorId(),
											dentalLabPickupCollection.getCollectionBoyId());
							if (collectionBoyDoctorAssociationCollection != null
									&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
								if (collectionBoyCollection != null) {
									pushNotificationServices.notifyPharmacy(
											collectionBoyCollection.getUserId().toString(), null, null,
											RoleEnum.DENTAL_WORK_REFRESH, FINISHED_LAB_NOTIFICATION_CB);
								}
							}
						}
					} else if (request.getStatus().equals("WORK_RECEIVED")) {
						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									"work received", ComponentType.REFRESH.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						if (dentalLabPickupCollection.getCollectionBoyId() != null) {
							CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
									.findOne(dentalLabPickupCollection.getCollectionBoyId());
							CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
									.getByLocationDoctorCollectionBoy(dentalLabPickupCollection.getDentalLabId(),
											dentalLabPickupCollection.getDoctorId(),
											dentalLabPickupCollection.getCollectionBoyId());
							if (collectionBoyDoctorAssociationCollection != null
									&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
								if (collectionBoyCollection != null) {
									pushNotificationServices.notifyPharmacy(
											collectionBoyCollection.getUserId().toString(), null, null,
											RoleEnum.DENTAL_WORK_REFRESH, "work received");
								}
							}
						}
					}

				}

				if (request.getIsCollectedAtDoctor() != null) {
					dentalLabPickupCollection.setIsCollectedAtDoctor(request.getIsCollectedAtDoctor());
				}
				if (request.getIsCompleted() != null) {
					dentalLabPickupCollection.setIsCompleted(request.getIsCompleted());
				}
				if (request.getIsAcceptedAtLab() != null) {
					dentalLabPickupCollection.setIsAcceptedAtLab(request.getIsAcceptedAtLab());
				}
				if (request.getFeedbackRating() != null) {
					dentalLabPickupCollection.setFeedBackRating(request.getFeedbackRating());
				}
				if (request.getFeedbackComment() != null) {
					dentalLabPickupCollection.setFeedBackComment(request.getFeedbackComment());
				}
				if (request.getDiscarded() != null) {
					dentalLabPickupCollection.setDiscarded(request.getDiscarded());
				}
				dentalLabPickupCollection.setUpdatedTime(new Date());
				dentalLabPickupCollection = dentalLabTestPickupRepository.save(dentalLabPickupCollection);
				response = true;
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public ImageURLResponse addDentalImage(FormDataBodyPart file) {
		ImageURLResponse imageURLResponse = null;
		try {
			if (file != null) {
				String path = "dental-images";
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
				String recordPath = path + File.separator + fileName + System.currentTimeMillis() + "." + fileExtension;
				imageURLResponse = fileManager.saveImage(file, recordPath, true);
				/*
				 * if (imageURLResponse != null) { imageURLResponse.setImageUrl(imagePath +
				 * imageURLResponse.getImageUrl()); imageURLResponse.setThumbnailUrl(imagePath +
				 * imageURLResponse.getThumbnailUrl()); }
				 */
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return imageURLResponse;
	}

	@Override
	@Transactional
	public ImageURLResponse addDentalImageBase64(FileDetails fileDetails) {
		ImageURLResponse imageURLResponse = null;
		try {
			Date createdTime = new Date();

			if (fileDetails != null) {
				// String path = "lab-reports";
				// String recordLabel = fileDetails.getFileName();
				fileDetails.setFileName(fileDetails.getFileName() + createdTime.getTime());
				String path = "dental-images";
				String recordPath = path + File.separator + fileDetails.getFileName() + System.currentTimeMillis() + "."
						+ fileDetails.getFileExtension();
				imageURLResponse = fileManager.saveImageAndReturnImageUrl(fileDetails, recordPath, true);
				if (imageURLResponse != null) {
					imageURLResponse.setImageUrl(imagePath + imageURLResponse.getImageUrl());
					imageURLResponse.setThumbnailUrl(imagePath + imageURLResponse.getThumbnailUrl());
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return imageURLResponse;
	}

	@Override
	@Transactional
	public Boolean updateDentalStageForDoctor(UpdateDentalStagingRequest request) {
		Boolean response = false;

		try {
			DentalLabPickupCollection dentalLabPickupCollection = dentalLabTestPickupRepository
					.findOne(new ObjectId(request.getRequestId()));
			if (dentalLabPickupCollection != null) {
				List<DentalWorksSample> dentalWorksSamples = dentalLabPickupCollection.getDentalWorksSamples();

				for (DentalWorksSample dentalWorksSample : dentalWorksSamples) {

					if (dentalWorksSample.getUniqueWorkId().equals(request.getUniqueWorkId())) {
						List<DentalStage> dentalStages = new ArrayList<>();
						if (request.getDentalStages() != null) {
							for (DentalStageRequest dentalStageRequest : request.getDentalStages()) {
								DentalStage dentalStage = new DentalStage();
								BeanUtil.map(dentalStageRequest, dentalStage);
								dentalStages.add(dentalStage);
							}
							dentalWorksSample.setDentalStagesForDoctor(dentalStages);
						}
						dentalWorksSample.setProcessStatus(request.getProcessStatus());

					}
				}
				dentalLabPickupCollection.setStatus(request.getStatus());
				dentalLabPickupCollection.setDentalWorksSamples(dentalWorksSamples);
				if (request.getIsCompleted() != null) {
					dentalLabPickupCollection.setIsCompleted(request.getIsCompleted());
				}
				dentalLabTestPickupRepository.save(dentalLabPickupCollection);
				if (request.getStatus() != null) {
					if (request.getStatus().equals("COPING_TRIAL")) {
						List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
								.findByLocationId(dentalLabPickupCollection.getDentalLabId());
						for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
							pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
									COPING_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
								.findOne(dentalLabPickupCollection.getCollectionBoyId());
						CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
								.getByLocationDoctorCollectionBoy(dentalLabPickupCollection.getDentalLabId(),
										dentalLabPickupCollection.getDoctorId(),
										dentalLabPickupCollection.getCollectionBoyId());
						if (collectionBoyDoctorAssociationCollection != null
								&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
							if (collectionBoyCollection != null) {
								pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(),
										null, null, RoleEnum.DENTAL_COLLECTION_BOY, COPING_TRIAL_NOTIFICATION_CB);
							}
						}
					} else if (request.getStatus().equals("BISQUE_TRIAL")) {
						List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
								.findByLocationId(dentalLabPickupCollection.getDentalLabId());
						for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
							pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
									BISQUE_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
								.findOne(dentalLabPickupCollection.getCollectionBoyId());
						CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
								.getByLocationDoctorCollectionBoy(dentalLabPickupCollection.getDentalLabId(),
										dentalLabPickupCollection.getDoctorId(),
										dentalLabPickupCollection.getCollectionBoyId());
						if (collectionBoyDoctorAssociationCollection != null
								&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
							if (collectionBoyCollection != null) {
								pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(),
										null, null, RoleEnum.DENTAL_COLLECTION_BOY, BISQUE_TRIAL_NOTIFICATION_CB);
							}
						}
					} else if (request.getStatus().equals("FINISHED_LAB")) {
						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									FINISHED_LAB_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						if (dentalLabPickupCollection.getCollectionBoyId() != null) {
							CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
									.findOne(dentalLabPickupCollection.getCollectionBoyId());
							CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
									.getByLocationDoctorCollectionBoy(dentalLabPickupCollection.getDentalLabId(),
											dentalLabPickupCollection.getDoctorId(),
											dentalLabPickupCollection.getCollectionBoyId());
							if (collectionBoyDoctorAssociationCollection != null
									&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
								if (collectionBoyCollection != null) {
									pushNotificationServices.notifyPharmacy(
											collectionBoyCollection.getUserId().toString(), null, null,
											RoleEnum.DENTAL_COLLECTION_BOY, FINISHED_LAB_NOTIFICATION_CB);
								}
							}
						}
					}
				}
				response = true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean updateDentalStageForLab(UpdateDentalStagingRequest request) {
		Boolean response = false;
		try {
			DentalLabPickupCollection dentalLabPickupCollection = dentalLabTestPickupRepository
					.findOne(new ObjectId(request.getRequestId()));
			if (dentalLabPickupCollection != null) {
				List<DentalWorksSample> dentalWorksSamples = dentalLabPickupCollection.getDentalWorksSamples();
				dentalLabPickupCollection.setStatus(request.getStatus());

				for (DentalWorksSample dentalWorksSample : dentalWorksSamples) {
					if (dentalWorksSample.getUniqueWorkId().equals(request.getUniqueWorkId())) {
						List<DentalStage> dentalStages = new ArrayList<>();
						if (request.getDentalStages() != null) {
							for (DentalStageRequest dentalStageRequest : request.getDentalStages()) {
								DentalStage dentalStage = new DentalStage();
								BeanUtil.map(dentalStageRequest, dentalStage);
								dentalStages.add(dentalStage);
							}
							dentalWorksSample.setDentalStagesForLab(dentalStages);
						}
						dentalWorksSample.setProcessStatus(request.getProcessStatus());

					}
				}

				dentalLabPickupCollection.setDentalWorksSamples(dentalWorksSamples);
				if (request.getIsCompleted() != null) {
					dentalLabPickupCollection.setIsCompleted(request.getIsCompleted());
				}
				dentalLabTestPickupRepository.save(dentalLabPickupCollection);
				if (request.getStatus() != null && request.getIsTrialChanged() == true) {
					if (request.getStatus().equals("COPING_TRIAL")) {
						List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
								.findByLocationId(dentalLabPickupCollection.getDentalLabId());
						for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
							pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
									COPING_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									COPING_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
								.findOne(dentalLabPickupCollection.getCollectionBoyId());
						CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
								.getByLocationDoctorCollectionBoy(dentalLabPickupCollection.getDentalLabId(),
										dentalLabPickupCollection.getDoctorId(),
										dentalLabPickupCollection.getCollectionBoyId());
						if (collectionBoyDoctorAssociationCollection != null
								&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
							if (collectionBoyCollection != null) {
								pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(),
										null, null, RoleEnum.DENTAL_COLLECTION_BOY, COPING_TRIAL_NOTIFICATION_CB);
							}
						}
					} else if (request.getStatus().equals("BISQUE_TRIAL")) {
						List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
								.findByLocationId(dentalLabPickupCollection.getDentalLabId());
						for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
							pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
									BISQUE_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									BISQUE_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
								.findOne(dentalLabPickupCollection.getCollectionBoyId());
						CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
								.getByLocationDoctorCollectionBoy(dentalLabPickupCollection.getDentalLabId(),
										dentalLabPickupCollection.getDoctorId(),
										dentalLabPickupCollection.getCollectionBoyId());
						if (collectionBoyDoctorAssociationCollection != null
								&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
							if (collectionBoyCollection != null) {
								pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(),
										null, null, RoleEnum.DENTAL_COLLECTION_BOY, BISQUE_TRIAL_NOTIFICATION_CB);
							}
						}
					} else if (request.getStatus().equals("FINISHED_LAB")) {
						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									FINISHED_LAB_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						if (dentalLabPickupCollection.getCollectionBoyId() != null) {
							CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
									.findOne(dentalLabPickupCollection.getCollectionBoyId());
							CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
									.getByLocationDoctorCollectionBoy(dentalLabPickupCollection.getDentalLabId(),
											dentalLabPickupCollection.getDoctorId(),
											dentalLabPickupCollection.getCollectionBoyId());
							if (collectionBoyDoctorAssociationCollection != null
									&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
								if (collectionBoyCollection != null) {
									pushNotificationServices.notifyPharmacy(
											collectionBoyCollection.getUserId().toString(), null, null,
											RoleEnum.DENTAL_COLLECTION_BOY, FINISHED_LAB_NOTIFICATION_CB);
								}
							}
						}
					}
				}
				response = true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean cancelRequest(String requestId, String reasonOfCancellation, String cancelledBy) {
		Boolean response = false;
		try {
			DentalLabPickupCollection dentalLabPickupCollection = dentalLabTestPickupRepository
					.findOne(new ObjectId(requestId));
			if (dentalLabPickupCollection != null) {
				dentalLabPickupCollection.setReasonForCancel(reasonOfCancellation);
				dentalLabPickupCollection.setCancelledBy(cancelledBy);
				dentalLabPickupCollection.setDiscarded(true);
				dentalLabTestPickupRepository.save(dentalLabPickupCollection);

				if (cancelledBy.equalsIgnoreCase("DOCTOR")) {
					List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
							.findByLocationId(dentalLabPickupCollection.getDentalLabId());

					for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
						pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
								CANCELLED_NOTIFICATION, ComponentType.DENTAL_WORKS_CANCELLATION.getType(),
								dentalLabPickupCollection.getId().toString(), null);
					}
				} else if (cancelledBy.equalsIgnoreCase("DENTAL_WORKS_LAB")) {
					pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
							CANCELLED_NOTIFICATION, ComponentType.DENTAL_WORKS_CANCELLATION.getType(),
							dentalLabPickupCollection.getId().toString(), null);
				}

				if (dentalLabPickupCollection.getCollectionBoyId() != null) {
					CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
							.findOne(dentalLabPickupCollection.getCollectionBoyId());
					CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
							.getByLocationDoctorCollectionBoy(dentalLabPickupCollection.getDentalLabId(),
									dentalLabPickupCollection.getDoctorId(),
									dentalLabPickupCollection.getCollectionBoyId());
					if (collectionBoyDoctorAssociationCollection != null
							&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
						if (collectionBoyCollection != null) {
							pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(),
									null, null, RoleEnum.DENTAL_WORKS_CANCELLATION, CANCELLED_NOTIFICATION);
						}
					}
				}

				response = true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean updateETA(UpdateETARequest request) {
		Boolean response = false;

		try {
			DentalLabPickupCollection dentalLabPickupCollection = dentalLabTestPickupRepository
					.findOne(new ObjectId(request.getRequestId()));
			if (dentalLabPickupCollection != null) {
				List<DentalWorksSample> dentalWorksSamples = dentalLabPickupCollection.getDentalWorksSamples();
				for (DentalWorksSample dentalWorksSample : dentalWorksSamples) {
					if (dentalWorksSample.getUniqueWorkId().equals(request.getUniqueWorkId())) {
						dentalWorksSample.setEtaInHour(request.getEtaInHour());
						dentalWorksSample.setEtaInDate(request.getEtaInDate());

					}
				}
				dentalLabPickupCollection.setDentalWorksSamples(dentalWorksSamples);
				dentalLabTestPickupRepository.save(dentalLabPickupCollection);

				pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
						"ETA for work has been updated.", ComponentType.DENTAL_WORKS.getType(),
						dentalLabPickupCollection.getId().toString(), null);

				response = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean discardRequest(String requestId, Boolean discarded) {
		Boolean response = false;
		try {
			DentalLabPickupCollection dentalLabPickupCollection = dentalLabTestPickupRepository
					.findOne(new ObjectId(requestId));
			if (dentalLabPickupCollection != null) {
				dentalLabPickupCollection.setDiscarded(discarded);
				response = true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public DentalLabPickupResponse getRequestById(String id) {
		DentalLabPickupResponse dentalLabPickupResponse = null;
		DentalLabPickupLookupResponse dentalLabPickupLookupResponse = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			criteria.and("_id").in(new ObjectId(id));

			aggregation = Aggregation.newAggregation(
					Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
					Aggregation.unwind("dentalLab"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					Aggregation.unwind("doctor"),
					Aggregation.lookup("collection_boy_cl", "collectionBoyId", "_id", "collectionBoy"),
					new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$collectionBoy").append("preserveNullAndEmptyArrays", true))),
					Aggregation.match(criteria));
			AggregationResults<DentalLabPickupLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalLabPickupCollection.class, DentalLabPickupLookupResponse.class);
			dentalLabPickupLookupResponse = aggregationResults.getUniqueMappedResult();

			if (dentalLabPickupLookupResponse != null) {
				List<DentalStageRequest> dentalStageRequestsForDoctor = null;
				List<DentalStageRequest> dentalStageRequestsForLab = null;
				List<DentalWorksSampleRequest> dentalWorksSampleRequests = new ArrayList<>();
				dentalLabPickupResponse = new DentalLabPickupResponse();
				BeanUtil.map(dentalLabPickupLookupResponse, dentalLabPickupResponse);

				for (DentalWorksSample dentalWorksSample : dentalLabPickupLookupResponse.getDentalWorksSamples()) {
					DentalWorksSampleRequest dentalWorksSampleRequest = new DentalWorksSampleRequest();
					BeanUtil.map(dentalWorksSample, dentalWorksSampleRequest);
					if (dentalWorksSample.getDentalStagesForDoctor() != null) {
						dentalStageRequestsForDoctor = new ArrayList<>();
						for (DentalStage dentalStage : dentalWorksSample.getDentalStagesForDoctor()) {
							DentalStageRequest dentalStageRequest = new DentalStageRequest();
							BeanUtil.map(dentalStage, dentalStageRequest);
							dentalStageRequestsForDoctor.add(dentalStageRequest);
						}
					}
					if (dentalWorksSample.getDentalStagesForLab() != null) {
						dentalStageRequestsForLab = new ArrayList<>();
						for (DentalStage dentalStage : dentalWorksSample.getDentalStagesForLab()) {
							DentalStageRequest dentalStageRequest = new DentalStageRequest();
							BeanUtil.map(dentalStage, dentalStageRequest);
							dentalStageRequestsForLab.add(dentalStageRequest);
						}
					}
					dentalWorksSampleRequest.setDentalStagesForDoctor(dentalStageRequestsForDoctor);
					dentalWorksSampleRequest.setDentalStagesForLab(dentalStageRequestsForLab);
					dentalWorksSampleRequests.add(dentalWorksSampleRequest);
					dentalLabPickupResponse.setDentalWorksSamples(dentalWorksSampleRequests);

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, " Error while getting Dental Lab Report By Ids");
		}
		return dentalLabPickupResponse;
	}

	@Override
	@Transactional
	public List<DentalLabPickupResponse> getRequestByIds(List<ObjectId> ids) {
		List<DentalLabPickupResponse> dentalLabPickupResponses = null;
		List<DentalLabPickupLookupResponse> dentalLabPickupLookupResponse = null;
		DentalLabPickupResponse dentalLabPickupResponse = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			criteria.and("_id").in(ids);

			aggregation = Aggregation.newAggregation(
					Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
					Aggregation.unwind("dentalLab"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					Aggregation.unwind("doctor"),
					Aggregation.lookup("collection_boy_cl", "collectionBoyId", "_id", "collectionBoy"),
					new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$collectionBoy").append("preserveNullAndEmptyArrays", true))),
					Aggregation.match(criteria));
			AggregationResults<DentalLabPickupLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalLabPickupCollection.class, DentalLabPickupLookupResponse.class);
			dentalLabPickupLookupResponse = aggregationResults.getMappedResults();

			if (dentalLabPickupLookupResponse != null && !dentalLabPickupLookupResponse.isEmpty()) {
				List<DentalStageRequest> dentalStageRequestsForDoctor = null;
				List<DentalStageRequest> dentalStageRequestsForLab = null;
				List<DentalWorksSampleRequest> dentalWorksSampleRequests = null;
				dentalLabPickupResponses = new ArrayList<DentalLabPickupResponse>();
				for (DentalLabPickupLookupResponse labPickupLookupResponse : dentalLabPickupLookupResponse) {
					dentalWorksSampleRequests = new ArrayList<DentalWorksSampleRequest>();
					dentalLabPickupResponse = new DentalLabPickupResponse();
					BeanUtil.map(labPickupLookupResponse, dentalLabPickupResponse);

					for (DentalWorksSample dentalWorksSample : labPickupLookupResponse.getDentalWorksSamples()) {
						dentalStageRequestsForDoctor = null;
						DentalWorksSampleRequest dentalWorksSampleRequest = new DentalWorksSampleRequest();
						BeanUtil.map(dentalWorksSample, dentalWorksSampleRequest);
						if (dentalWorksSample.getDentalStagesForDoctor() != null) {
							dentalStageRequestsForDoctor = new ArrayList<DentalStageRequest>();
							for (DentalStage dentalStage : dentalWorksSample.getDentalStagesForDoctor()) {
								DentalStageRequest dentalStageRequest = new DentalStageRequest();
								BeanUtil.map(dentalStage, dentalStageRequest);
								dentalStageRequestsForDoctor.add(dentalStageRequest);
							}
						}
						if (dentalWorksSample.getDentalStagesForLab() != null) {
							dentalStageRequestsForLab = new ArrayList<DentalStageRequest>();
							for (DentalStage dentalStage : dentalWorksSample.getDentalStagesForLab()) {
								DentalStageRequest dentalStageRequest = new DentalStageRequest();
								BeanUtil.map(dentalStage, dentalStageRequest);
								dentalStageRequestsForLab.add(dentalStageRequest);
							}
						}
						dentalWorksSampleRequest.setDentalStagesForDoctor(dentalStageRequestsForDoctor);
						dentalWorksSampleRequest.setDentalStagesForLab(dentalStageRequestsForLab);
						dentalWorksSampleRequests.add(dentalWorksSampleRequest);
						dentalLabPickupResponse.setDentalWorksSamples(dentalWorksSampleRequests);

					}
					dentalLabPickupResponses.add(dentalLabPickupResponse);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, " Error while getting Dental Lab Report By Ids");
		}
		return dentalLabPickupResponses;
	}

	@Override
	public String downloadDentalLabReportPrint(String id, Boolean isInspectionReport) {
		String response = null;
		DentalLabPickupResponse dentalLabPickupResponse = null;
		JasperReportResponse jasperReportResponse = null;
		try {

			dentalLabPickupResponse = getRequestById(id);
			if (dentalLabPickupResponse == null) {
				throw new BusinessException(ServiceError.NoRecord, " No Lab Report found with ids");
			}
			if (isInspectionReport)
				jasperReportResponse = createInspectionReportJasper(dentalLabPickupResponse);
			else
				jasperReportResponse = createJasper(dentalLabPickupResponse);

			if (jasperReportResponse != null)
				response = getFinalImageURL(jasperReportResponse.getPath());
			if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
				if (jasperReportResponse.getFileSystemResource().getFile().exists())
					jasperReportResponse.getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Lab Report PDF for Parent");
			throw new BusinessException(ServiceError.Unknown, " Error while getting Lab Report PDF for Parent");
		}
		return response;

	}

	@Override
	public String downloadMultipleInspectionReportPrint(List<String> requestId) {
		String response = null;
		List<DentalLabPickupResponse> dentalLabPickupResponse = null;
		JasperReportResponse jasperReportResponse = null;
		List<ObjectId> objectIdList = null;
		try {
			if (requestId != null && !requestId.isEmpty()) {
				objectIdList = new ArrayList<ObjectId>();
				for (String id : requestId) {
					if (!DPDoctorUtils.anyStringEmpty(id)) {
						objectIdList.add(new ObjectId(id));
					}
				}
			}
			dentalLabPickupResponse = getRequestByIds(objectIdList);
			if (dentalLabPickupResponse == null || dentalLabPickupResponse.isEmpty()) {
				throw new BusinessException(ServiceError.NoRecord, " No Lab Report found with ids");
			}

			jasperReportResponse = createMultipleInspectionReportJasper(dentalLabPickupResponse);

			if (jasperReportResponse != null)
				response = getFinalImageURL(jasperReportResponse.getPath());
			if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
				if (jasperReportResponse.getFileSystemResource().getFile().exists())
					jasperReportResponse.getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Lab Report PDF for Parent");
			throw new BusinessException(ServiceError.Unknown, " Error while getting Lab Report PDF for Parent");
		}
		return response;

	}

	private JasperReportResponse createJasper(DentalLabPickupResponse dentalLabPickupResponse)
			throws IOException, ParseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		String pattern = "dd/MM/yyyy";
		String labName = "";
		String locationId = null, hospitalId = null;
		int sNo = 0;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		UserCollection userCollection = null;
		List<DentalStagejasperBean> dentalStage = null;
		List<DentalCardValueJasperBean> dentalCardValues = null;

		DBObject labReportItems = null;
		List<DBObject> labreports = new ArrayList<DBObject>();

		labReportItems = new BasicDBObject();

		if (dentalLabPickupResponse.getDentalLab() != null) {
			locationId = dentalLabPickupResponse.getDentalLab().getId();
			hospitalId = dentalLabPickupResponse.getDentalLab().getHospitalId();
			labName = dentalLabPickupResponse.getDentalLab().getLocationName();
			parameters.put("dentalLab", "<b>Dental Lab :- </b> " + labName);
		} else {
			parameters.put("dentalLab", "<b>Dental Lab :- </b> ");
		}
		if (dentalLabPickupResponse.getDoctor() != null) {
			parameters.put("doctor", "<b>Doctor :- </b>Dr. " + dentalLabPickupResponse.getDoctor().getFirstName());
		} else if (!DPDoctorUtils.anyStringEmpty(dentalLabPickupResponse.getDoctorId())) {
			userCollection = userRepository.findOne(new ObjectId(dentalLabPickupResponse.getDoctorId()));
			if (userCollection != null)
				parameters.put("doctor", "<b>Doctor :- </b>Dr. " + userCollection.getFirstName());
			else
				parameters.put("doctor", "<b>Doctor :- </b> ");
		} else {
			parameters.put("doctor", "<b>Doctor :- </b> ");

		}

		if (dentalLabPickupResponse.getPatientName() != null) {
			parameters.put("patientName", dentalLabPickupResponse.getPatientName());
		} else {
			parameters.put("patientName", " -- ");
		}

		if (dentalLabPickupResponse.getGender() != null) {
			parameters.put("gender", "<b>Gender :- </b> " + dentalLabPickupResponse.getGender());
		} else {
			parameters.put("gender", "<b>Gender :- </b> --");
		}

		if (dentalLabPickupResponse.getAge() != null) {
			parameters.put("age", "<b>Age :- </b> " + dentalLabPickupResponse.getAge());
		} else {
			parameters.put("age", "<b>Age :- </b> --");
		}

		if (dentalLabPickupResponse.getRequestId() != null) {
			parameters.put("requestId", "<b>Id :- </b> " + dentalLabPickupResponse.getRequestId());
		} else {
			parameters.put("requestId", "<b>Id :- </b>  --");
		}

		if (dentalLabPickupResponse.getDentalWorksSamples() != null
				&& !dentalLabPickupResponse.getDentalWorksSamples().isEmpty()) {
			String toothNumbers = "";

			DentalWorksSampleRequest dentalWorksSample = dentalLabPickupResponse.getDentalWorksSamples().get(0);

			if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getShade())) {
				parameters.put("shade", dentalWorksSample.getShade());
			} else {
				parameters.put("shade", "--");
			}

			if (dentalWorksSample.getRateCardDentalWorkAssociation() != null) {
				if (dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork() != null) {
					parameters.put("dentalWork",
							!DPDoctorUtils.anyStringEmpty(
									dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork().getWorkName())
											? dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork()
													.getWorkName()
											: "--");
				} else {
					parameters.put("dentalWork", "--");
				}
			} else {
				parameters.put("dentalWork", "--");
			}
			if (dentalWorksSample.getMaterial() != null) {
				parameters.put("material", StringUtils.join(dentalWorksSample.getMaterial(), ','));
			} else {
				parameters.put("material", "--");
			}
			if (dentalWorksSample.getDentalToothNumbers() != null) {
				for (DentalToothNumber dentalToothNumber : dentalWorksSample.getDentalToothNumbers()) {
					toothNumbers = toothNumbers + StringUtils.join(dentalToothNumber.getToothNumber(), ',') + " - "
							+ dentalToothNumber.getType() + "<br>";
				}
				parameters.put("toothNumbers", toothNumbers);

			} else {
				parameters.put("toothNumbers", "--");
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalLabPickupResponse.getStatus())) {
				parameters.put("status", dentalLabPickupResponse.getStatus().replace('_', ' '));
			} else {
				parameters.put("status", "--");
			}

			if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getCollarAndMetalDesign())) {
				parameters.put("collarAndMetalDesign",
						"<b>Collar and Metal Design :- </b> " + dentalWorksSample.getCollarAndMetalDesign());
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getOcclusalStaining())) {
				parameters.put("occlusalStaining",
						"<b>Occlusal Design :- </b> " + dentalWorksSample.getOcclusalStaining());
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getInstructions())) {
				parameters.put("instructions", "<b>Instructions :- </b> " + dentalWorksSample.getInstructions());
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getPonticDesign())) {
				parameters.put("ponticDesign", "<b>Pontic Desing :- </b> " + dentalWorksSample.getPonticDesign());
			}

			if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getUniqueWorkId())) {
				parameters.put("uniqueWorkId", "<b>Unique Work Id :- </b> " + dentalWorksSample.getUniqueWorkId());
			}

			if (dentalWorksSample.getDentalImages() != null) {
				parameters.put("dentalImages", dentalWorksSample.getDentalImages());
			}
			if (dentalWorksSample.getDentalWorkCardValues() != null) {
				parameters.put("dentalWorkCards", dentalWorksSample.getDentalWorkCardValues());
			}
			if (dentalWorksSample.getDentalStagesForDoctor() != null) {
				parameters.put("dentalStages", dentalWorksSample.getDentalStagesForDoctor());
			}

			if (dentalWorksSample.getEtaInDate() != null) {
				parameters.put("eta",
						"<b>ETA :- </b> " + simpleDateFormat.format(new Date(dentalWorksSample.getEtaInDate())));
			} else {
				parameters.put("eta", "<b>ETA :- </b>  --");
			}

			if (dentalWorksSample.getDentalStagesForDoctor() != null
					&& !dentalWorksSample.getDentalStagesForDoctor().isEmpty()) {
				dentalStage = new ArrayList<DentalStagejasperBean>();
				DentalStagejasperBean stagejasperBean = null;
				for (DentalStageRequest dentalStageRequest : dentalWorksSample.getDentalStagesForDoctor()) {
					stagejasperBean = new DentalStagejasperBean();
					if (dentalStageRequest.getDeliveryTime() != null)
						stagejasperBean.setDate(simpleDateFormat.format(dentalStageRequest.getDeliveryTime()));

					if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getAuthorisedPerson()))
						stagejasperBean.setInspectedBy(dentalStageRequest.getAuthorisedPerson());

					if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getStage()))
						stagejasperBean.setProcess(dentalStageRequest.getStage());
					dentalStage.add(stagejasperBean);
				}

			}

			parameters.put("items", dentalStage);

			if (dentalWorksSample.getDentalWorkCardValues() != null
					&& !dentalWorksSample.getDentalWorkCardValues().isEmpty()) {
				dentalCardValues = new ArrayList<DentalCardValueJasperBean>();
				DentalCardValueJasperBean stagejasperBean = null;
				for (DentalWorkCardValue dentalWorkCardValue : dentalWorksSample.getDentalWorkCardValues()) {
					stagejasperBean = new DentalCardValueJasperBean();
					if (!DPDoctorUtils.anyStringEmpty(dentalWorkCardValue.getName()))
						stagejasperBean.setName(dentalWorkCardValue.getName());

					if (!DPDoctorUtils.anyStringEmpty(dentalWorkCardValue.getValue()))
						stagejasperBean.setValue(dentalWorkCardValue.getValue());

					if (dentalWorkCardValue.getQuantity() != null)
						stagejasperBean.setQuantity(dentalWorkCardValue.getQuantity());
					dentalCardValues.add(stagejasperBean);
				}

			}

			parameters.put("cardValues", dentalCardValues);

		}

		parameters.put("title", "DENTAL WORKS REPORT");
		parameters.put("date",
				"<b>Work Date :- </b>" + simpleDateFormat.format(dentalLabPickupResponse.getUpdatedTime()));
		parameters.put("PatientName", "Patient Name");
		parameters.put("ToothNo", "Tooth no.");
		parameters.put("Work", "Work");
		parameters.put("Shade", "shade");
		parameters.put("Material", "Material");
		parameters.put("Status", "Status");

		String pdfName = locationId + "DENTAL-WORKS" + new Date().getTime();

		String layout = "PORTRAIT";
		String pageSize = "A4";
		Integer topMargin = 20;
		Integer bottonMargin = 20;
		Integer leftMargin = 20;
		Integer rightMargin = 20;

		parameters.put("footerSignature", "");
		parameters.put("bottomSignText", "");
		parameters.put("contentFontSize", 11);
		parameters.put("headerLeftText", "");
		parameters.put("headerRightText", "");
		parameters.put("footerBottomText", "");
		parameters.put("logoURL", "");

		parameters.put("showTableOne", false);

		parameters.put("poweredBy", footerText);

		parameters.put("contentLineSpace", LineSpace.SMALL.name());
		response = jasperReportService.createPDF(ComponentType.DENTAL_WORKS, parameters, dentalWorksFormA4FileName,
				layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;

	}

	private JasperReportResponse createInspectionReportJasper(DentalLabPickupResponse dentalLabPickupResponse)
			throws IOException, ParseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		String pattern = "dd/MM/yyyy";
		String labName = "";
		String copingStage = "COPING";
		String bisqueStage = "BISQUE";
		String finalStage = "FINAL";
		String workId = " ";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		UserCollection userCollection = null;
		List<DentalStagejasperBean> dentalStage = null;
		if (dentalLabPickupResponse.getDentalLab() != null) {
			labName = dentalLabPickupResponse.getDentalLab().getLocationName();
			parameters.put("dentalLab", "<b>Dental Lab :- </b> " + labName);
		} else {
			parameters.put("dentalLab", "<b>Dental Lab :- </b> ");
		}

		if (dentalLabPickupResponse.getDoctor() != null) {
			parameters.put("doctor", "<b>Doctor :- </b>Dr. " + dentalLabPickupResponse.getDoctor().getFirstName());
		} else if (!DPDoctorUtils.anyStringEmpty(dentalLabPickupResponse.getDoctorId())) {
			userCollection = userRepository.findOne(new ObjectId(dentalLabPickupResponse.getDoctorId()));
			if (userCollection != null)
				parameters.put("doctor", "<b>Doctor :- </b>Dr. " + userCollection.getFirstName());
			else

				parameters.put("doctor", "<b>Doctor :- </b> ");
		} else {
			parameters.put("doctor", "<b>Doctor :- </b> ");

		}
		if (!DPDoctorUtils.anyStringEmpty(dentalLabPickupResponse.getPatientName()))
			parameters.put("patientName", dentalLabPickupResponse.getPatientName());
		else
			parameters.put("patientName", "--");

		if (dentalLabPickupResponse.getDentalWorksSamples() != null
				&& !dentalLabPickupResponse.getDentalWorksSamples().isEmpty()) {
			String toothNumbers = "";

			DentalWorksSampleRequest dentalWorksSample = dentalLabPickupResponse.getDentalWorksSamples().get(0);

			if (dentalWorksSample.getDentalStagesForDoctor() != null
					&& !dentalWorksSample.getDentalStagesForDoctor().isEmpty()) {
				for (DentalStageRequest dentalStageRequest : dentalWorksSample.getDentalStagesForDoctor()) {
					if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getStage())) {
						if (dentalStageRequest.getStage().equalsIgnoreCase("COPING")) {
							copingStage = dentalStageRequest.getStage();
							if (dentalStageRequest.getPickupTime() != null) {
								copingStage = copingStage + "("
										+ simpleDateFormat.format(dentalStageRequest.getPickupTime()) + ")";
							}
						}
						if (dentalStageRequest.getStage().equalsIgnoreCase("BISQUE")) {
							bisqueStage = dentalStageRequest.getStage();
							if (dentalStageRequest.getPickupTime() != null) {
								bisqueStage = bisqueStage + "("
										+ simpleDateFormat.format(dentalStageRequest.getPickupTime()) + ")";
							}
						}
						if (dentalStageRequest.getStage().equalsIgnoreCase("FINAL")) {
							finalStage = dentalStageRequest.getStage();
							if (dentalStageRequest.getPickupTime() != null) {
								finalStage = finalStage + "("
										+ simpleDateFormat.format(dentalStageRequest.getPickupTime()) + ")";
							}
						}

					}

				}

			}

			if (!DPDoctorUtils.allStringsEmpty(dentalWorksSample.getUniqueWorkId())) {
				workId = dentalWorksSample.getUniqueWorkId();
			}

			if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getShade())) {
				parameters.put("shade", dentalWorksSample.getShade());
			} else {
				parameters.put("shade", "--");
			}

			if (dentalWorksSample.getRateCardDentalWorkAssociation() != null) {
				if (dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork() != null) {
					parameters.put("dentalWork",
							!DPDoctorUtils.anyStringEmpty(
									dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork().getWorkName())
											? dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork()
													.getWorkName()
											: "--");
				} else {
					parameters.put("dentalWork", "--");
				}
			} else {
				parameters.put("dentalWork", "--");
			}
			if (dentalWorksSample.getMaterial() != null) {
				parameters.put("material", StringUtils.join(dentalWorksSample.getMaterial(), ','));
			} else {
				parameters.put("material", "--");
			}
			if (dentalWorksSample.getDentalToothNumbers() != null) {
				for (DentalToothNumber dentalToothNumber : dentalWorksSample.getDentalToothNumbers()) {
					toothNumbers = toothNumbers + StringUtils.join(dentalToothNumber.getToothNumber(), ',') + " - "
							+ dentalToothNumber.getType() + "<br>";
				}
				parameters.put("toothNumbers", toothNumbers);

			} else {
				parameters.put("toothNumbers", "--");
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalLabPickupResponse.getStatus())) {
				parameters.put("status", dentalLabPickupResponse.getStatus().replace('_', ' '));
			} else {
				parameters.put("status", "--");
			}
			dentalStage = new ArrayList<DentalStagejasperBean>();
			DentalStagejasperBean stagejasperBean = new DentalStagejasperBean();
			stagejasperBean.setProcess("Model Preparation");
			dentalStage.add(stagejasperBean);
			stagejasperBean = new DentalStagejasperBean();
			stagejasperBean.setProcess("Wax Pattern");
			dentalStage.add(stagejasperBean);
			stagejasperBean = new DentalStagejasperBean();
			stagejasperBean.setProcess("Casting/Metal Finishing");
			dentalStage.add(stagejasperBean);
			stagejasperBean = new DentalStagejasperBean();
			stagejasperBean.setProcess("Buildup");
			dentalStage.add(stagejasperBean);
			stagejasperBean = new DentalStagejasperBean();
			stagejasperBean.setProcess("Final Finishing");
			dentalStage.add(stagejasperBean);

			if (dentalWorksSample.getDentalStagesForLab() != null
					&& !dentalWorksSample.getDentalStagesForLab().isEmpty()) {
				for (DentalStagejasperBean dentalStagejasperBean : dentalStage) {
					for (DentalStageRequest dentalStageRequest : dentalWorksSample.getDentalStagesForLab()) {

						userCollection = null;

						if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getStage())) {

							if (dentalStagejasperBean.getProcess().equalsIgnoreCase(dentalStageRequest.getStage())) {

								if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getStaffId())) {
									userCollection = userRepository
											.findOne(new ObjectId(dentalStageRequest.getStaffId()));
									if (userCollection != null) {
										dentalStagejasperBean.setInspectedBy(
												(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle())
														? userCollection.getTitle()
														: "") + " " + userCollection.getFirstName());
									}
								}
								if (dentalStageRequest.getDeliveryTime() != null) {
									dentalStagejasperBean
											.setDate(simpleDateFormat.format(dentalStageRequest.getDeliveryTime()));
								}

							}
						}
					}
				}
			}

			parameters.put("items", dentalStage);
		}
		if (!DPDoctorUtils.anyStringEmpty(workId)) {
			parameters.put("requestId", "<b>Work Id :- </b> " + workId);
		} else {
			parameters.put("requestId", "");
		}
		parameters.put("title", "INSPECTION REPORT");
		parameters.put("copingStage", copingStage);
		parameters.put("bisqueStage", bisqueStage);
		parameters.put("finalStage", finalStage);
		parameters.put("date",
				"<b>Work Date :- </b>" + simpleDateFormat.format(dentalLabPickupResponse.getUpdatedTime()));

		String pdfName = dentalLabPickupResponse.getId() + "-DENTAL-INSPECTION-REPORT-" + new Date().getTime();
		String layout = "PORTRAIT";
		String pageSize = "A4";
		Integer topMargin = 20;
		Integer bottonMargin = 20;
		Integer leftMargin = 20;
		Integer rightMargin = 20;
		parameters.put("footerSignature", "");
		parameters.put("bottomSignText", "");
		parameters.put("contentFontSize", 11);
		parameters.put("headerLeftText", "");
		parameters.put("headerRightText", "");
		parameters.put("footerBottomText", "");
		parameters.put("logoURL", "");
		parameters.put("showTableOne", false);
		parameters.put("poweredBy", footerText);
		parameters.put("contentLineSpace", LineSpace.SMALL.name());
		response = jasperReportService.createPDF(ComponentType.DENTAL_LAB_INSPECTION_REPORT, parameters,
				dentalWorksFormA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;

	}

	private JasperReportResponse createMultipleInspectionReportJasper(
			List<DentalLabPickupResponse> dentalLabPickupResponses) throws IOException, ParseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		String pattern = "dd/MM/yyyy";
		String labName = "";
		String locationId = null, hospitalId = null;
		String workId = " ";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		UserCollection userCollection = null;
		List<DentalStagejasperBean> dentalStage = null;
		List<InseptionReportJasperBean> jasperBeans = new ArrayList<InseptionReportJasperBean>();
		InseptionReportJasperBean jasperBean = null;
		for (DentalLabPickupResponse dentalLabPickupResponse : dentalLabPickupResponses) {
			jasperBean = new InseptionReportJasperBean();

			if (dentalLabPickupResponse.getDentalLab() != null) {
				locationId = dentalLabPickupResponse.getDentalLab().getId();
				hospitalId = dentalLabPickupResponse.getDentalLab().getHospitalId();
				labName = dentalLabPickupResponse.getDentalLab().getLocationName();
				jasperBean.setDentalLab("<b>Dental Lab :- </b> " + labName);
			} else {
				jasperBean.setDentalLab("<b>Dental Lab :- </b> ");
			}

			if (dentalLabPickupResponse.getDoctor() != null) {
				jasperBean.setDoctor("<b>Doctor :- </b>Dr. " + dentalLabPickupResponse.getDoctor().getFirstName());
			} else if (!DPDoctorUtils.anyStringEmpty(dentalLabPickupResponse.getDoctorId())) {
				userCollection = userRepository.findOne(new ObjectId(dentalLabPickupResponse.getDoctorId()));
				if (userCollection != null)
					jasperBean.setDoctor("<b>Doctor :- </b>Dr. " + userCollection.getFirstName());
				else

					jasperBean.setDoctor("<b>Doctor :- </b> ");
			} else {
				jasperBean.setDoctor("<b>Doctor :- </b> ");

			}
			if (!DPDoctorUtils.anyStringEmpty(dentalLabPickupResponse.getPatientName()))
				jasperBean.setPatientName(dentalLabPickupResponse.getPatientName());
			else
				jasperBean.setPatientName("--");

			if (dentalLabPickupResponse.getDentalWorksSamples() != null
					&& !dentalLabPickupResponse.getDentalWorksSamples().isEmpty()) {
				String toothNumbers = "";

				DentalWorksSampleRequest dentalWorksSample = dentalLabPickupResponse.getDentalWorksSamples().get(0);

				if (dentalWorksSample.getDentalStagesForDoctor() != null
						&& !dentalWorksSample.getDentalStagesForDoctor().isEmpty()) {
					for (DentalStageRequest dentalStageRequest : dentalWorksSample.getDentalStagesForDoctor()) {

						if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getStage())) {
							if (dentalStageRequest.getStage().equalsIgnoreCase("COPING")) {
								jasperBean.setCopingStage(dentalStageRequest.getStage());
								if (dentalStageRequest.getPickupTime() != null) {
									jasperBean.setCopingStage(jasperBean.getCopingStage() + "("
											+ simpleDateFormat.format(dentalStageRequest.getPickupTime()) + ")");
								}
							}
							if (dentalStageRequest.getStage().equalsIgnoreCase("BISQUE")) {
								jasperBean.setBisqueStage(dentalStageRequest.getStage());
								if (dentalStageRequest.getPickupTime() != null) {
									jasperBean.setBisqueStage(jasperBean.getBisqueStage() + "("
											+ simpleDateFormat.format(dentalStageRequest.getPickupTime()) + ")");
								}
							}
							if (dentalStageRequest.getStage().equalsIgnoreCase("FINAL")) {
								jasperBean.setFinalStage(dentalStageRequest.getStage());
								if (dentalStageRequest.getPickupTime() != null) {
									jasperBean.setFinalStage(jasperBean.getFinalStage() + "("
											+ simpleDateFormat.format(dentalStageRequest.getPickupTime()) + ")");
								}
							}

						}

					}

				}

				if (!DPDoctorUtils.allStringsEmpty(dentalWorksSample.getUniqueWorkId())) {
					workId = dentalWorksSample.getUniqueWorkId();
				}

				if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getShade())) {
					jasperBean.setShade(dentalWorksSample.getShade());
				} else {
					jasperBean.setShade("--");
				}

				if (dentalWorksSample.getRateCardDentalWorkAssociation() != null) {
					if (dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork() != null) {
						jasperBean.setDentalWork(!DPDoctorUtils.anyStringEmpty(
								dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork().getWorkName())
										? dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork()
												.getWorkName()
										: "--");
					} else {
						jasperBean.setDentalWork("--");
					}
				} else {
					jasperBean.setDentalWork("--");
				}
				if (dentalWorksSample.getMaterial() != null) {
					jasperBean.setMaterial(StringUtils.join(dentalWorksSample.getMaterial(), ','));
				} else {
					jasperBean.setMaterial("--");
				}
				if (dentalWorksSample.getDentalToothNumbers() != null) {
					for (DentalToothNumber dentalToothNumber : dentalWorksSample.getDentalToothNumbers()) {
						toothNumbers = toothNumbers + StringUtils.join(dentalToothNumber.getToothNumber(), ',') + " - "
								+ dentalToothNumber.getType();
					}
					jasperBean.setToothNumbers(toothNumbers);

				} else {
					jasperBean.setToothNumbers("--");
				}
				if (!DPDoctorUtils.anyStringEmpty(dentalLabPickupResponse.getStatus())) {
					jasperBean.setStatus(dentalLabPickupResponse.getStatus().replace('_', ' '));
				} else {
					jasperBean.setStatus("--");
				}
				dentalStage = new ArrayList<DentalStagejasperBean>();
				DentalStagejasperBean stagejasperBean = new DentalStagejasperBean();
				stagejasperBean.setProcess("Model Preparation");
				dentalStage.add(stagejasperBean);
				stagejasperBean = new DentalStagejasperBean();
				stagejasperBean.setProcess("Wax Pattern");
				dentalStage.add(stagejasperBean);
				stagejasperBean = new DentalStagejasperBean();
				stagejasperBean.setProcess("Casting/Metal Finishing");
				dentalStage.add(stagejasperBean);
				stagejasperBean = new DentalStagejasperBean();
				stagejasperBean.setProcess("Buildup");
				dentalStage.add(stagejasperBean);
				stagejasperBean = new DentalStagejasperBean();
				stagejasperBean.setProcess("Final Finishing");
				dentalStage.add(stagejasperBean);

				if (dentalWorksSample.getDentalStagesForLab() != null
						&& !dentalWorksSample.getDentalStagesForLab().isEmpty()) {
					for (DentalStagejasperBean dentalStagejasperBean : dentalStage) {
						for (DentalStageRequest dentalStageRequest : dentalWorksSample.getDentalStagesForLab()) {

							userCollection = null;

							if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getStage())) {

								if (dentalStagejasperBean.getProcess()
										.equalsIgnoreCase(dentalStageRequest.getStage())) {

									if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getStaffId())) {
										userCollection = userRepository
												.findOne(new ObjectId(dentalStageRequest.getStaffId()));
										if (userCollection != null) {
											dentalStagejasperBean.setInspectedBy(
													(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle())
															? userCollection.getTitle()
															: "") + " " + userCollection.getFirstName());
										}
									}
									if (dentalStageRequest.getDeliveryTime() != null) {
										dentalStagejasperBean
												.setDate(simpleDateFormat.format(dentalStageRequest.getDeliveryTime()));
									}

								}
							}
						}
					}
				}

				jasperBean.setItems(dentalStage);
			}
			if (!DPDoctorUtils.anyStringEmpty(workId)) {
				jasperBean.setRequestId("<b>Work Id :- </b> " + workId);
			} else {
				jasperBean.setRequestId("");
			}

			jasperBean.setDate(
					"<b>Work Date :- </b>" + simpleDateFormat.format(dentalLabPickupResponse.getUpdatedTime()));
			jasperBeans.add(jasperBean);

		}
		String pdfName = locationId + "DENTAL-INSPECTION-REPORT-" + new Date().getTime();
		String layout = "PORTRAIT";
		String pageSize = "A4";
		Integer topMargin = 20;
		Integer bottonMargin = 20;
		Integer leftMargin = 20;
		Integer rightMargin = 20;
		parameters.put("details", jasperBeans);
		parameters.put("footerSignature", "");
		parameters.put("bottomSignText", "");
		parameters.put("contentFontSize", 11);
		parameters.put("headerLeftText", "");
		parameters.put("headerRightText", "");
		parameters.put("footerBottomText", "");
		parameters.put("logoURL", "");
		parameters.put("showTableOne", false);
		parameters.put("poweredBy", footerText);
		parameters.put("contentLineSpace", LineSpace.SMALL.name());
		response = jasperReportService.createPDF(ComponentType.MULTIPLE_INSPECTION_REPORT, parameters,
				dentalWorksFormA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	private void sendDownloadAppMessage(ObjectId doctorId, String locationName) {
		try {
			UserCollection userCollection = userRepository.findOne(doctorId);

			if (userCollection != null) {
				String message = dentalLabSMSToDoctor;
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				smsTrackDetail.setDoctorId(doctorId);
				// smsTrackDetail.setLocationId(locationId);
				// smsTrackDetail.setHospitalId(hospitalId);
				smsTrackDetail.setType("APP_LINK_THROUGH_DENTAL_LAB");
				SMSDetail smsDetail = new SMSDetail();
				smsDetail.setUserId(userCollection.getId());
				SMS sms = new SMS();
				smsDetail.setUserName(userCollection.getFirstName());
				sms.setSmsText(message.replace("{clinicName}", locationName));

				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(userCollection.getMobileNumber());
				sms.setSmsAddress(smsAddress);

				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				smsServices.sendSMS(smsTrackDetail, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
