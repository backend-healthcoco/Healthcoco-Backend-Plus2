package com.dpdocter.services.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.NutritionGoalAnalytics;
import com.dpdocter.beans.NutritionPlan;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.SubscriptionNutritionPlan;
import com.dpdocter.beans.User;
import com.dpdocter.beans.UserNutritionSubscription;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.NutritionGoalStatusStampingCollection;
import com.dpdocter.collections.NutritionPlanCollection;
import com.dpdocter.collections.NutritionReferenceCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.SubscriptionNutritionPlanCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserNutritionSubscriptionCollection;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.GoalStatus;
import com.dpdocter.enums.NutritionPlanType;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.NutritionGoalStatusStampingRepository;
import com.dpdocter.repository.NutritionPlanRepository;
import com.dpdocter.repository.NutritionReferenceRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.SubscritptionNutritionPlanRepository;
import com.dpdocter.repository.UserNutritionSubscriptionRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditNutritionReferenceRequest;
import com.dpdocter.request.NutritionPlanRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.NutritionPlanResponse;
import com.dpdocter.response.NutritionPlanWithCategoryResponse;
import com.dpdocter.response.NutritionReferenceResponse;
import com.dpdocter.response.UserNutritionSubscriptionResponse;
import com.dpdocter.scheduler.AsyncService;
import com.dpdocter.services.NutritionService;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.TransactionalManagementService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class NutritionServiceImpl implements NutritionService {

	private static Logger logger = Logger.getLogger(NutritionServiceImpl.class.getName());

	@Autowired
	private NutritionReferenceRepository nutritionReferenceRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private MongoOperations mongoOperations;

	@Autowired
	private NutritionGoalStatusStampingRepository nutritionGoalStatusStampingRepository;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private NutritionPlanRepository nutritionPlanRepository;

	@Autowired
	private SubscritptionNutritionPlanRepository subscritptionNutritionPlanRepository;

	@Autowired
	private UserNutritionSubscriptionRepository userNutritionSubscriptionRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private AsyncService asyncService;

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

	@Override
	@Transactional
	public NutritionReferenceResponse addEditNutritionReference(AddEditNutritionReferenceRequest request) {
		NutritionReferenceResponse response = null;
		NutritionReferenceCollection nutritionReferenceCollection = null;
		try {

			ObjectId doctorId = new ObjectId(request.getDoctorId()), locationId = new ObjectId(request.getLocationId()),
					hospitalId = new ObjectId(request.getHospitalId()), patientId = null;

			patientId = registerPatientIfNotRegistered(request, doctorId, locationId, hospitalId);
			response = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				nutritionReferenceCollection = nutritionReferenceRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			if (nutritionReferenceCollection == null) {
				nutritionReferenceCollection = new NutritionReferenceCollection();
			}
			BeanUtil.map(request, nutritionReferenceCollection);
			nutritionReferenceCollection.setPatientId(patientId);
			nutritionReferenceCollection.setReports(request.getReports());
			nutritionReferenceCollection = nutritionReferenceRepository.save(nutritionReferenceCollection);
			if (nutritionReferenceCollection != null) {
				response = new NutritionReferenceResponse();
				BeanUtil.map(nutritionReferenceCollection, response);
				NutritionGoalStatusStampingCollection nutritionGoalStatusStampingCollection = null;
				UserCollection userCollection = null;
				if(response.getDoctorId() != null)
				{
					userCollection = userRepository.findById(new ObjectId(response.getDoctorId())).orElse(null);
					response.setDoctorName(userCollection.getFirstName());
				}
				nutritionGoalStatusStampingCollection = nutritionGoalStatusStampingRepository
						.getByPatientDoctorLocationHospitalandStatus(patientId, doctorId, locationId, hospitalId,
								nutritionReferenceCollection.getGoalStatus());

				if (nutritionGoalStatusStampingCollection != null) {
					nutritionGoalStatusStampingCollection.setGoalStatus(nutritionReferenceCollection.getGoalStatus());
					nutritionGoalStatusStampingCollection = nutritionGoalStatusStampingRepository
							.save(nutritionGoalStatusStampingCollection);
				} else {
					nutritionGoalStatusStampingCollection = new NutritionGoalStatusStampingCollection();
					nutritionGoalStatusStampingCollection.setDoctorId(nutritionReferenceCollection.getDoctorId());
					nutritionGoalStatusStampingCollection.setLocationId(nutritionReferenceCollection.getLocationId());
					nutritionGoalStatusStampingCollection.setHospitalId(nutritionReferenceCollection.getHospitalId());
					nutritionGoalStatusStampingCollection.setPatientId(nutritionReferenceCollection.getPatientId());
					nutritionGoalStatusStampingCollection
							.setReferredDoctorId(nutritionReferenceCollection.getReferredDoctorId());
					nutritionGoalStatusStampingCollection
							.setReferredHospitalId(nutritionReferenceCollection.getReferredHospitalId());
					nutritionGoalStatusStampingCollection
							.setReferredLocationId(nutritionReferenceCollection.getReferredLocationId());
					nutritionGoalStatusStampingCollection.setGoalStatus(nutritionReferenceCollection.getGoalStatus());
					if (userCollection != null) {
						nutritionGoalStatusStampingCollection.setCreatedBy(userCollection.getFirstName());
					}
					nutritionGoalStatusStampingCollection.setCreatedTime(new Date());
					nutritionGoalStatusStampingCollection = nutritionGoalStatusStampingRepository
							.save(nutritionGoalStatusStampingCollection);
				}
				if(response.getLocationId() != null)
				{
					LocationCollection locationCollection = locationRepository.findById(new ObjectId(response.getLocationId())).orElse(null);
					response.setLocationName(locationCollection.getLocationName());
				}
				if(response.getDoctorId() != null)
				{
					userCollection = userRepository.findById(new ObjectId(response.getDoctorId())).orElse(null);
					response.setDoctorName(userCollection.getFirstName());
				}

				PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(
						new ObjectId(response.getPatientId()), new ObjectId(response.getLocationId()),
						new ObjectId(response.getHospitalId()));
				if (patientCollection != null) {
					PatientShortCard patientCard = new PatientShortCard();
					BeanUtil.map(patientCollection, patientCard);
					response.setPatient(patientCard);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}

	@Override
	@Transactional
	public List<NutritionReferenceResponse> getNutritionReferenceList(String doctorId, String locationId, String role, long page , int size) {
		List<NutritionReferenceResponse> nutritionReferenceResponses= null;
		LocationCollection locationCollection = null;
		UserCollection userCollection = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				if (role != null) {
					if (role.equals(RoleEnum.NUTRITIONIST.getRole())) {
						criteria.and("referredDoctorId").is(new ObjectId(doctorId));
					} else {
						criteria.and("doctorId").is(new ObjectId(doctorId));
					}
				} else {
					criteria.and("doctorId").is(new ObjectId(doctorId));
				}
				userCollection = userRepository.findById(new ObjectId(doctorId)).orElse(null);
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				if (role != null) {
					if (role.equals(RoleEnum.NUTRITIONIST.getRole())) {
						criteria.and("referredLocationId").is(new ObjectId(locationId));
					} else {
						criteria.and("locationId").is(new ObjectId(locationId));
					}
				} else {
					criteria.and("locationId").is(new ObjectId(locationId));
				}
				locationCollection = locationRepository.findById(new ObjectId(locationId)).orElse(null);
			}
			if (size > 0)
				nutritionReferenceResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.skip(page * size),
								Aggregation.limit(size), Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						NutritionReferenceCollection.class, NutritionReferenceResponse.class).getMappedResults();
			else
				nutritionReferenceResponses = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteria),
										Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
								NutritionReferenceCollection.class, NutritionReferenceResponse.class)
						.getMappedResults();

			for (NutritionReferenceResponse nutritionReferenceResponse : nutritionReferenceResponses) {

				if (locationCollection != null) {
					nutritionReferenceResponse.setLocationName(locationCollection.getLocationName());
				}
				if (userCollection != null) {
					nutritionReferenceResponse.setDoctorName(userCollection.getFirstName());
				}
				PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(new ObjectId(nutritionReferenceResponse.getPatientId()), new ObjectId(nutritionReferenceResponse.getLocationId()), new ObjectId(nutritionReferenceResponse.getHospitalId())); 
				if(patientCollection != null)
				{
					UserCollection patient = userRepository.findById(patientCollection.getUserId()).orElse(null);
					PatientShortCard patientCard = new PatientShortCard();
					BeanUtil.map(patient, patientCard);
					BeanUtil.map(patientCollection, patientCard);
					nutritionReferenceResponse.setPatient(patientCard);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return nutritionReferenceResponses;
	}

	@Override
	@Transactional
	public NutritionGoalAnalytics getGoalAnalytics(String doctorId, String locationId, String role, Long fromDate,
			Long toDate) {
		NutritionGoalAnalytics nutritionGoalAnalytics = null;
		try {
			nutritionGoalAnalytics = new NutritionGoalAnalytics();
			nutritionGoalAnalytics.setReferredCount(
					getGoalStatusCount(doctorId, locationId, role, GoalStatus.REFERRED.getType(), fromDate, toDate));
			nutritionGoalAnalytics.setAcceptedCount(
					getGoalStatusCount(doctorId, locationId, role, GoalStatus.ADOPTED.getType(), fromDate, toDate));
			nutritionGoalAnalytics.setOnHoldCount(
					getGoalStatusCount(doctorId, locationId, role, GoalStatus.ON_HOLD.getType(), fromDate, toDate));
			nutritionGoalAnalytics.setRejectedCount(
					getGoalStatusCount(doctorId, locationId, role, GoalStatus.REJECTED.getType(), fromDate, toDate));
			nutritionGoalAnalytics.setCompletedCount(
					getGoalStatusCount(doctorId, locationId, role, GoalStatus.COMPLETED.getType(), fromDate, toDate));
			nutritionGoalAnalytics.setMetGoalCount(
					getGoalStatusCount(doctorId, locationId, role, GoalStatus.MET_GOALS.getType(), fromDate, toDate));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nutritionGoalAnalytics;
	}

	public Boolean changeStatus(String id, String regularityStatus, String goalStatus) {
		Boolean response = false;
		NutritionReferenceCollection nutritionReferenceCollection = null;
		try {
			if(!DPDoctorUtils.anyStringEmpty(id))
			{
				nutritionReferenceCollection = nutritionReferenceRepository.findById(new ObjectId(id)).orElse(null);
				if(nutritionReferenceCollection != null)
				{
					if(!DPDoctorUtils.anyStringEmpty(regularityStatus))
					{
						nutritionReferenceCollection.setRegularityStatus(regularityStatus);
					}
					if (!DPDoctorUtils.anyStringEmpty(goalStatus)) {
						nutritionReferenceCollection.setGoalStatus(goalStatus);
						NutritionGoalStatusStampingCollection nutritionGoalStatusStampingCollection = nutritionGoalStatusStampingRepository
								.getByPatientDoctorLocationHospitalandStatus(
										nutritionReferenceCollection.getPatientId(),
										nutritionReferenceCollection.getReferredDoctorId(),
										nutritionReferenceCollection.getReferredLocationId(),
										nutritionReferenceCollection.getReferredHospitalId(), goalStatus);

						if (nutritionGoalStatusStampingCollection != null) {
							nutritionGoalStatusStampingCollection.setUpdatedTime(new Date());
							nutritionGoalStatusStampingCollection = nutritionGoalStatusStampingRepository
									.save(nutritionGoalStatusStampingCollection);
						} else {
							nutritionGoalStatusStampingCollection = new NutritionGoalStatusStampingCollection();
							nutritionGoalStatusStampingCollection
									.setDoctorId(nutritionReferenceCollection.getDoctorId());
							nutritionGoalStatusStampingCollection
									.setLocationId(nutritionReferenceCollection.getLocationId());
							nutritionGoalStatusStampingCollection
									.setHospitalId(nutritionReferenceCollection.getHospitalId());
							nutritionGoalStatusStampingCollection
									.setReferredDoctorId(nutritionReferenceCollection.getReferredDoctorId());
							nutritionGoalStatusStampingCollection
									.setReferredLocationId(nutritionReferenceCollection.getReferredLocationId());
							nutritionGoalStatusStampingCollection
									.setReferredHospitalId(nutritionReferenceCollection.getReferredHospitalId());
							nutritionGoalStatusStampingCollection
									.setPatientId(nutritionReferenceCollection.getPatientId());
							nutritionGoalStatusStampingCollection.setGoalStatus(goalStatus);
							nutritionGoalStatusStampingCollection.setCreatedTime(new Date());
							nutritionGoalStatusStampingCollection.setUpdatedTime(new Date());
							UserCollection userCollection = userRepository.findById(nutritionReferenceCollection.getReferredDoctorId()).orElse(null);
							nutritionGoalStatusStampingCollection.setCreatedBy(userCollection.getCreatedBy());
							nutritionGoalStatusStampingCollection = nutritionGoalStatusStampingRepository
									.save(nutritionGoalStatusStampingCollection);
						}
					}
					response = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public NutritionReferenceResponse getNutritionReferenceResposneById(String id) {
		NutritionReferenceResponse response = null;
		NutritionReferenceCollection nutritionReferenceCollection = null;
		try {
			if(!DPDoctorUtils.anyStringEmpty(id))
			{
				nutritionReferenceCollection = nutritionReferenceRepository.findById(new ObjectId(id)).orElse(null);
				if(nutritionReferenceCollection != null)
				{
					response = new NutritionReferenceResponse();
					BeanUtil.map(nutritionReferenceCollection, response);
					LocationCollection locationCollection = locationRepository.findById(new ObjectId(response.getHospitalId())).orElse(null);
					if(locationCollection != null)
					{
						response.setLocationName(locationCollection.getLocationName());
					}
					UserCollection userCollection = userRepository.findById(new ObjectId(response.getLocationId())).orElse(null);
					if(userCollection != null)
					{
						response.setDoctorName(userCollection.getFirstName());
					}
					PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(new ObjectId(response.getPatientId()), new ObjectId(response.getLocationId()), new ObjectId(response.getHospitalId())); 
					if(patientCollection != null)
					{
						UserCollection patient = userRepository.findById(patientCollection.getUserId()).orElse(null);
						PatientShortCard patientCard = new PatientShortCard();
						BeanUtil.map(patient, patientCard);
						BeanUtil.map(patientCollection, patientCard);
						response.setPatient(patientCard);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	private Long getGoalStatusCount(String doctorId, String locationId, String role, String status, Long fromDate,
			Long toDate) {

		Long count = 0l;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				if (role != null) {
					if (role.equals(RoleEnum.NUTRITIONIST.getRole())) {
						criteria.and("referredDoctorId").is(new ObjectId(doctorId));
					} else {
						criteria.and("doctorId").is(new ObjectId(doctorId));
					}
				} else {
					criteria.and("doctorId").is(new ObjectId(doctorId));
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				if (role != null) {
					if (role.equals(RoleEnum.NUTRITIONIST.getRole())) {
						criteria.and("referredLocationId").is(new ObjectId(locationId));
					} else {
						criteria.and("locationId").is(new ObjectId(locationId));
					}
				} else {
					criteria.and("locationId").is(new ObjectId(locationId));
				}
			}

			criteria.and("goalStatus").is(status);

			if (toDate != null) {
				criteria.and("updatedTime").gte(new Date(fromDate)).lte(DPDoctorUtils.getEndTime(new Date(toDate)));
			} else {
				criteria.and("updatedTime").gte(new Date(fromDate));
			}

			Query query = new Query();
			query.addCriteria(criteria);
			count = mongoOperations.count(query, NutritionGoalStatusStampingCollection.class);
		}
			catch (Exception e) {
		}
		return count;
	}

	private ObjectId registerPatientIfNotRegistered(AddEditNutritionReferenceRequest request, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId) {
		ObjectId patientId = null;
		if (request.getPatientId() == null || request.getPatientId().isEmpty()) {

			if (DPDoctorUtils.anyStringEmpty(request.getLocalPatientName())) {
				throw new BusinessException(ServiceError.InvalidInput, "Patient not selected");
			}
			PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
			patientRegistrationRequest.setFirstName(request.getLocalPatientName());
			patientRegistrationRequest.setLocalPatientName(request.getLocalPatientName());
			patientRegistrationRequest.setMobileNumber(request.getMobileNumber());
			patientRegistrationRequest.setDoctorId(request.getDoctorId());
			patientRegistrationRequest.setLocationId(request.getLocationId());
			patientRegistrationRequest.setHospitalId(request.getHospitalId());
			RegisteredPatientDetails patientDetails = null;
			patientDetails = registrationService.registerNewPatient(patientRegistrationRequest);
			if (patientDetails != null) {
				request.setPatientId(patientDetails.getUserId());
			}
			transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
			esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
			patientId = new ObjectId(request.getPatientId());
		} else if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {

			patientId = new ObjectId(request.getPatientId());
			PatientCollection patient = patientRepository.findByUserIdLocationIdAndHospitalId(patientId, locationId,
					hospitalId);
			if (patient == null) {
				PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
				patientRegistrationRequest.setDoctorId(request.getDoctorId());
				patientRegistrationRequest.setLocalPatientName(request.getLocalPatientName());
				patientRegistrationRequest.setFirstName(request.getLocalPatientName());
				patientRegistrationRequest.setUserId(request.getPatientId());
				patientRegistrationRequest.setLocationId(request.getLocationId());
				patientRegistrationRequest.setHospitalId(request.getHospitalId());
				RegisteredPatientDetails patientDetails = registrationService
						.registerExistingPatient(patientRegistrationRequest, null);
				transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
				esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
			} else {
				List<ObjectId> consultantDoctorIds = patient.getConsultantDoctorIds();
				if (consultantDoctorIds == null)
					consultantDoctorIds = new ArrayList<ObjectId>();
				if (!consultantDoctorIds.contains(doctorId))
					consultantDoctorIds.add(doctorId);
				patient.setConsultantDoctorIds(consultantDoctorIds);
				patient.setUpdatedTime(new Date());
				patientRepository.save(patient);
			}
		}

		return patientId;
	}

	@Override
	public List<NutritionPlanType> getPlanType() {

		return Arrays.asList(NutritionPlanType.values());
	}

	@Override
	public NutritionPlanResponse getNutritionPlan(String id) {
		NutritionPlanResponse response = null;
		try {

			Aggregation aggregation = null;

			Criteria criteria = new Criteria("id").is(new ObjectId(id)).and("subscriptionNutritionPlan.discarded").is(false);

			aggregation = Aggregation.newAggregation(
					Aggregation.lookup("subscription_nutrition_plan_cl", "_id", "nutritionPlanId",
							"subscriptionNutritionPlan"),
					Aggregation.match(criteria),

					Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<NutritionPlanResponse> results = mongoTemplate.aggregate(aggregation,
					NutritionPlanCollection.class, NutritionPlanResponse.class);
			response = results.getUniqueMappedResult();
			if (response != null) {
				if (!DPDoctorUtils.anyStringEmpty(response.getPlanImage())) {
					response.setPlanImage(getFinalImageURL(response.getPlanImage()));
				}
				if (!DPDoctorUtils.anyStringEmpty(response.getBannerImage())) {
					response.setBannerImage(getFinalImageURL(response.getBannerImage()));
				}
			}

		} catch (BusinessException e) {

			logger.error("Error while getting nutrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<NutritionPlan> getNutritionPlans(int page, int size, String type, long updatedTime,
			boolean discareded) {
		List<NutritionPlan> response = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria = criteria.and("type").is(type);
			}
			if (updatedTime > 0) {
				criteria = criteria.and("createdTime").gte(new Date(updatedTime));
			}

			criteria.and("discarded").is(discareded);

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.skip((page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

			AggregationResults<NutritionPlan> results = mongoTemplate.aggregate(aggregation,
					NutritionPlanCollection.class, NutritionPlan.class);
			response = results.getMappedResults();
			for (NutritionPlan nutritionPlan : response) {
				if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getPlanImage())) {
					nutritionPlan.setPlanImage(getFinalImageURL(nutritionPlan.getPlanImage()));
				}
				if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getBannerImage())) {
					nutritionPlan.setBannerImage(getFinalImageURL(nutritionPlan.getBannerImage()));
				}
			}

		} catch (BusinessException e) {

			logger.error("Error while getting nutrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public SubscriptionNutritionPlan getSubscritionPlan(String id) {
		SubscriptionNutritionPlan response = null;
		try {
			SubscriptionNutritionPlanCollection subscriptionNutritionPlanCollection = subscritptionNutritionPlanRepository
					.findById(new ObjectId(id)).orElse(null);
			response = new SubscriptionNutritionPlan();
			if (!DPDoctorUtils.anyStringEmpty(subscriptionNutritionPlanCollection.getBackgroundImage())) {
				subscriptionNutritionPlanCollection
						.setBackgroundImage(getFinalImageURL(subscriptionNutritionPlanCollection.getBackgroundImage()));
			}
			BeanUtil.map(subscriptionNutritionPlanCollection, response);

		} catch (BusinessException e) {

			logger.error("Error while getting Subscrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Subscrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<SubscriptionNutritionPlan> getSubscritionPlans(int page, int size, String nutritionplanId,
			Boolean discarded) {
		List<SubscriptionNutritionPlan> response = null;
		try {

			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(nutritionplanId)) {
				criteria = criteria.and("nutritionPlanId").is(new ObjectId(nutritionplanId));
			}

			criteria.and("discarded").is(discarded);

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.skip((page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

			AggregationResults<SubscriptionNutritionPlan> results = mongoTemplate.aggregate(aggregation,
					SubscriptionNutritionPlanCollection.class, SubscriptionNutritionPlan.class);
			response = results.getMappedResults();
			for (SubscriptionNutritionPlan nutritionPlan : response) {
				if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getBackgroundImage())) {
					nutritionPlan.setBackgroundImage(getFinalImageURL(nutritionPlan.getBackgroundImage()));
				}
			}

		} catch (BusinessException e) {

			logger.error("Error while getting Subscrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Subscrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<UserNutritionSubscriptionResponse> getUserSubscritionPlans(int page, int size, long updatedTime,
			boolean discarded, String userId) {
		List<UserNutritionSubscriptionResponse> response = null;
		try {

			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (updatedTime > 0) {
				criteria = criteria.and("updatedTime").gte(updatedTime);
			}
			if (DPDoctorUtils.anyStringEmpty(userId)) {
				criteria = criteria.and("userId").is(new ObjectId(userId));
			}
			criteria.and("discarded").is(discarded);
			criteria.and("transactionStatus").is("Success");

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("subscription_nutrition_plan_cl", "subscriptionPlanId", "_id",
								"subscriptionPlan"),
						Aggregation.unwind("subscriptionPlan"),
						Aggregation.lookup("nutrition_plan_cl", "nutritionPlanId", "_id", "NutritionPlan"),
						Aggregation.unwind("NutritionPlan"), Aggregation.lookup("user_cl", "userId", "_id", "user"),
						Aggregation.unwind("user"), Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.skip((page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("subscription_nutrition_plan_cl", "subscriptionPlanId", "_id",
								"subscriptionPlan"),
						Aggregation.unwind("subscriptionPlan"),
						Aggregation.lookup("nutrition_plan_cl", "nutritionPlanId", "_id", "NutritionPlan"),
						Aggregation.unwind("NutritionPlan"), Aggregation.lookup("user_cl", "userId", "_id", "user"),
						Aggregation.unwind("user"), Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

			AggregationResults<UserNutritionSubscriptionResponse> results = mongoTemplate.aggregate(aggregation,
					UserNutritionSubscriptionCollection.class, UserNutritionSubscriptionResponse.class);
			response = results.getMappedResults();
			NutritionPlan nutritionPlan = null;
			SubscriptionNutritionPlan subscriptionNutritionPlan = null;
			for (UserNutritionSubscriptionResponse nutritionSubscriptionResponse : response) {
				nutritionPlan = nutritionSubscriptionResponse.getNutritionPlan();
				if (nutritionPlan != null) {
					if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getBannerImage())) {
						nutritionPlan.setBannerImage(getFinalImageURL(nutritionPlan.getBannerImage()));
					}
					if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getPlanImage())) {
						nutritionPlan.setPlanImage(getFinalImageURL(nutritionPlan.getPlanImage()));
					}
				}
				subscriptionNutritionPlan = new SubscriptionNutritionPlan();
				if (subscriptionNutritionPlan != null) {
					if (!DPDoctorUtils.anyStringEmpty(subscriptionNutritionPlan.getBackgroundImage())) {
						subscriptionNutritionPlan
								.setBackgroundImage(getFinalImageURL(subscriptionNutritionPlan.getBackgroundImage()));
					}
				}
			}

		} catch (

		BusinessException e) {

			logger.error("Error while getting Subscrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Subscrition Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public UserNutritionSubscriptionResponse getUserSubscritionPlan(String id) {
		UserNutritionSubscriptionResponse response = null;
		try {

			Aggregation aggregation = null;

			Criteria criteria = new Criteria();

			criteria.and("id").is(new ObjectId(id));

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

					Aggregation.lookup("subscription_nutrition_plan_cl", "subscriptionPlanId", "_id",
							"subscriptionPlan"),
					Aggregation.unwind("subscriptionPlan"),
					Aggregation.lookup("nutrition_plan_cl", "nutritionPlanId", "_id", "NutritionPlan"),
					Aggregation.unwind("NutritionPlan"), Aggregation.lookup("user_cl", "userId", "_id", "user"),
					Aggregation.unwind("user"), Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<UserNutritionSubscriptionResponse> results = mongoTemplate.aggregate(aggregation,
					UserNutritionSubscriptionCollection.class, UserNutritionSubscriptionResponse.class);
			response = results.getUniqueMappedResult();
			NutritionPlan nutritionPlan = null;
			SubscriptionNutritionPlan subscriptionNutritionPlan = null;
			if (response != null) {
				nutritionPlan = response.getNutritionPlan();
				if (nutritionPlan != null) {
					if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getBannerImage())) {
						response.getNutritionPlan().setBannerImage(getFinalImageURL(nutritionPlan.getBannerImage()));
					}
					if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getPlanImage())) {
						response.getNutritionPlan().setPlanImage(getFinalImageURL(nutritionPlan.getPlanImage()));
					}
				}
				subscriptionNutritionPlan = response.getSubscriptionPlan();
				if (subscriptionNutritionPlan != null) {

					if (!DPDoctorUtils.anyStringEmpty(subscriptionNutritionPlan.getBackgroundImage())) {
						response.getSubscriptionPlan()
								.setBackgroundImage(getFinalImageURL(subscriptionNutritionPlan.getBackgroundImage()));
					}
				}
			}
		} catch (BusinessException e) {

			logger.error("Error while getting User Nutrition Subscrition " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting User Nutrition Subscrition " + e.getMessage());

		}
		return response;
	}

	@Override
	public UserNutritionSubscriptionResponse addEditUserSubscritionPlan(UserNutritionSubscription request) {

		UserNutritionSubscriptionResponse response = null;
		try {

			UserNutritionSubscriptionCollection nutritionSubscriptionCollection = new UserNutritionSubscriptionCollection();
			BeanUtil.map(request, nutritionSubscriptionCollection);
			UserCollection userCollection = userRepository.findById(nutritionSubscriptionCollection.getUserId()).orElse(null);
			if (userCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "user not found By Id ");
			}
			NutritionPlanCollection nutritionPlanCollection = nutritionPlanRepository
					.findById(nutritionSubscriptionCollection.getNutritionPlanId()).orElseGet(null);
			if (nutritionPlanCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Nutrition Plan not found By Id ");
			}
			SubscriptionNutritionPlanCollection subscriptionNutritionPlanCollection = subscritptionNutritionPlanRepository
					.findById(nutritionSubscriptionCollection.getSubscriptionPlanId()).orElseGet(null);

			if (subscriptionNutritionPlanCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "subscription Plan not found By Id ");
			}
			if (subscriptionNutritionPlanCollection.getDuration() != null) {
				Calendar cal = Calendar.getInstance();

				if (subscriptionNutritionPlanCollection.getDuration().getDurationUnit().toString()
						.equalsIgnoreCase("YEAR"))
					cal.add(Calendar.YEAR, subscriptionNutritionPlanCollection.getDuration().getValue().intValue()); // to
																														// get
																														// next
																														// year
																														// add
																														// 1
				if (subscriptionNutritionPlanCollection.getDuration().getDurationUnit().toString()
						.equalsIgnoreCase("MONTH"))
					cal.add(Calendar.MONTH, subscriptionNutritionPlanCollection.getDuration().getValue().intValue()); // to
																														// get
																														// next
																														// month
																														// add
																														// 1
				if (subscriptionNutritionPlanCollection.getDuration().getDurationUnit().toString()
						.equalsIgnoreCase("DAY"))
					cal.add(Calendar.DAY_OF_MONTH,
							subscriptionNutritionPlanCollection.getDuration().getValue().intValue()); // to get next
																										// day add 1
				if (subscriptionNutritionPlanCollection.getDuration().getDurationUnit().toString()
						.equalsIgnoreCase("WEEK"))
					cal.add(Calendar.WEEK_OF_MONTH,
							subscriptionNutritionPlanCollection.getDuration().getValue().intValue()); // to get next
																										// week add 1
				nutritionSubscriptionCollection.setToDate(cal.getTime());
			}
			nutritionSubscriptionCollection.setAdminCreatedTime(new Date());
			nutritionSubscriptionCollection.setCreatedTime(new Date());
			nutritionSubscriptionCollection.setCreatedBy(
					(DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? "" : userCollection.getTitle())
							+ userCollection.getFirstName());
			nutritionSubscriptionCollection.setDiscount(subscriptionNutritionPlanCollection.getDiscount());
			nutritionSubscriptionCollection.setAmount(subscriptionNutritionPlanCollection.getAmount());
			nutritionSubscriptionCollection
					.setDiscountAmount(subscriptionNutritionPlanCollection.getDiscountedAmount());
			nutritionSubscriptionCollection = userNutritionSubscriptionRepository.save(nutritionSubscriptionCollection);

			response = new UserNutritionSubscriptionResponse();
			NutritionPlan nutritionPlan = new NutritionPlan();
			SubscriptionNutritionPlan subscriptionNutritionPlan = new SubscriptionNutritionPlan();
			User user = new User();
			BeanUtil.map(subscriptionNutritionPlanCollection, subscriptionNutritionPlan);
			BeanUtil.map(nutritionPlanCollection, nutritionPlan);
			BeanUtil.map(nutritionSubscriptionCollection, response);
			BeanUtil.map(userCollection, user);

			if (response != null) {
				if (DPDoctorUtils.anyStringEmpty(request.getId())) {
					asyncService.sendMessage(response, userCollection);
					if (DPDoctorUtils.anyStringEmpty(userCollection.getEmailAddress()))
						asyncService.createMailNutritionTransactionStatus(response, userCollection);
				}
				if (nutritionPlan != null) {
					if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getBannerImage())) {
						nutritionPlan.setBannerImage(getFinalImageURL(nutritionPlan.getBannerImage()));
					}
					if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getPlanImage())) {
						nutritionPlan.setPlanImage(getFinalImageURL(nutritionPlan.getPlanImage()));
					}

				}

				if (subscriptionNutritionPlan != null) {

					if (!DPDoctorUtils.anyStringEmpty(subscriptionNutritionPlan.getBackgroundImage())) {
						subscriptionNutritionPlan
								.setBackgroundImage(getFinalImageURL(subscriptionNutritionPlan.getBackgroundImage()));
					}
				}
			}
			response.setNutritionPlan(nutritionPlan);
			response.setSubscriptionPlan(subscriptionNutritionPlan);
			response.setUser(user);

		} catch (BusinessException e) {
			logger.error("Error while adding User Nutrition Subscrition " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while adding User Nutrition Subscrition  " + e.getMessage());

		}
		return response;
	}

	@Override
	public UserNutritionSubscription deleteUserSubscritionPlan(String id) {
		UserNutritionSubscription response = null;
		try {
			UserNutritionSubscriptionCollection nutritionSubscriptionCollection = userNutritionSubscriptionRepository
					.findById(new ObjectId(id)).orElseGet(null);
			if (nutritionSubscriptionCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Subscrition Plan not found By Id ");
			}
			nutritionSubscriptionCollection.setUpdatedTime(new Date());
			nutritionSubscriptionCollection.setDiscarded(nutritionSubscriptionCollection.getDiscarded());
			nutritionSubscriptionCollection = userNutritionSubscriptionRepository.save(nutritionSubscriptionCollection);
			response = new UserNutritionSubscription();
			BeanUtil.map(nutritionSubscriptionCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while delete User Nutrition Subscrition  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while delete User Nutrition Subscrition " + e.getMessage());
		}
		return response;
	}

	@Override
	public List<NutritionPlanWithCategoryResponse> getNutritionPlanByCategory(NutritionPlanRequest request) {
		List<NutritionPlanWithCategoryResponse> response = null;
		try {
			Aggregation aggregation = null;

			CustomAggregationOperation projectOperation = new CustomAggregationOperation(new BasicDBObject("$project",
					new BasicDBObject("nutritionPlan.title", "$title").append("nutritionPlan._id", "$_id")
							.append("nutritionPlan.id", "$_id")
							.append("nutritionPlan.planImage", new BasicDBObject("$cond",
									new BasicDBObject("if", new BasicDBObject("eq", Arrays.asList("$planImage", null)))
											.append("then",
													new BasicDBObject("$concat",
															Arrays.asList(imagePath, "$planImage")))
											.append("else", null)))
							.append("nutritionPlan.bannerImage",
									new BasicDBObject("$cond",
											new BasicDBObject("if",
													new BasicDBObject("eq", Arrays.asList("$bannerImage", null)))
															.append("then",
																	new BasicDBObject("$concat",
																			Arrays.asList(imagePath, "$bannerImage")))
															.append("else", null)))
							.append("category", "$type").append("nutritionPlan.type", "$type").append("rank", "$rank")
							.append("nutritionPlan.backgroundColor", "$backgroundColor")
							.append("nutritionPlan.planDescription", "$planDescription")
							.append("nutritionPlan.nutrientDescriptions", "$nutrientDescriptions")
							.append("nutritionPlan.recommendedFoods", "$recommendedFoods")
							.append("nutritionPlan.amount", "$amount").append("nutritionPlan.discarded", "$discarded")
							.append("nutritionPlan.adminCreatedTime", "$adminCreatedTime")
							.append("nutritionPlan.createdTime", "$createdTime")
							.append("nutritionPlan.updatedTime", "$updatedTime")
							.append("nutritionPlan.createdBy", "$createdBy")));

			CustomAggregationOperation groupOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", "$category").append("category", new BasicDBObject("$first", "$category"))
							.append("rank", new BasicDBObject("$first", "$rank"))
							.append("nutritionPlan", new BasicDBObject("$push", "$nutritionPlan"))));
			Criteria criteria = new Criteria();
			if (request != null) {
				if (request.getTypes() != null && !request.getTypes().isEmpty()) {
					criteria = criteria.and("type").in(request.getTypes());
				}
				if (request.getUpdatedTime() > 0) {
					criteria = criteria.and("createdTime").gte(new Date(request.getUpdatedTime()));
				}

				criteria.and("discarded").is(request.getDiscarded());
			}

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), projectOperation, groupOperation,
					Aggregation.sort(Sort.Direction.ASC, "rank"));

			AggregationResults<NutritionPlanWithCategoryResponse> results = mongoTemplate.aggregate(aggregation,
					NutritionPlanCollection.class, NutritionPlanWithCategoryResponse.class);
			response = results.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting nutrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrition Plan " + e.getMessage());

		}
		return response;
	}

	@Scheduled(cron = "00 00 2 * * *", zone = "IST")
//	@Scheduled(fixedDelay = 1800000)
	@Override
	@Transactional
	public void updateUserSubscritionPlan() {
		try {

			Criteria criteria = new Criteria();

			criteria.and("id").lt(new Date());
			criteria.and("isExpired").is(false);
			Update update = new Update();
			update.set("isExpired", true);

			mongoTemplate.updateMulti(new Query(criteria), update, UserNutritionSubscriptionCollection.class);

		} catch (BusinessException e) {

			logger.error("Error while update User Nutrition Subscrition " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while update User Nutrition Subscrition  " + e.getMessage());

		}

	}

}
