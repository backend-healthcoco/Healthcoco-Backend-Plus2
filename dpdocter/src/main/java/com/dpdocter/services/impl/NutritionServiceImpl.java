package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.NutritionGoalAnalytics;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.NutritionGoalStatusStampingCollection;
import com.dpdocter.collections.NutritionReferenceCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientFeedbackCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.GoalStatus;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.NutritionGoalStatusStampingRepository;
import com.dpdocter.repository.NutritionReferenceRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditNutritionReferenceRequest;
import com.dpdocter.request.FeedbackGetRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.NutritionReferenceResponse;
import com.dpdocter.response.PatientFeedbackResponse;
import com.dpdocter.services.NutritionService;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class NutritionServiceImpl implements NutritionService{

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
	
	@Override
	@Transactional
	public NutritionReferenceResponse addEditNutritionReference(AddEditNutritionReferenceRequest request) {
		NutritionReferenceResponse response = null;
		NutritionReferenceCollection nutritionReferenceCollection = null;
		try {

			ObjectId doctorId = new ObjectId(request.getDoctorId()), locationId = new ObjectId(request.getLocationId()),
					hospitalId = new ObjectId(request.getHospitalId()), patientId = null;
			
			patientId = registerPatientIfNotRegistered(request, doctorId, locationId,hospitalId);
			response = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				nutritionReferenceCollection = nutritionReferenceRepository.findOne(new ObjectId(request.getId()));
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
				
				
				if(response.getLocationId() != null)
				{
					LocationCollection locationCollection = locationRepository.findOne(new ObjectId(response.getLocationId()));
					response.setLocationName(locationCollection.getLocationName());
				}
				if(response.getDoctorId() != null)
				{
					UserCollection userCollection = userRepository.findOne(new ObjectId(response.getDoctorId()));
					response.setDoctorName(userCollection.getFirstName());
				}
				
				PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(new ObjectId(response.getPatientId()), new ObjectId(response.getLocationId()), new ObjectId(response.getHospitalId())); 
				if(patientCollection != null)
				{
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
	public List<NutritionReferenceResponse> getNutritionReferenceList(String doctorId, String locationId, String role, int page , int size) {
		List<NutritionReferenceResponse> nutritionReferenceResponses= null;
		LocationCollection locationCollection = null;
		UserCollection userCollection = null;
		try {
			Criteria criteria = new Criteria();
			
			
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
			{
				if (role != null) {
					if (role.equals(RoleEnum.NUTRITIONIST.getRole())) {
						criteria.and("referredDoctorId").is(new ObjectId(doctorId));
					} else {
						criteria.and("doctorId").is(new ObjectId(doctorId));
					}
				}
				else {
					criteria.and("doctorId").is(new ObjectId(doctorId));
				}
				userCollection = userRepository.findOne(new ObjectId(doctorId));
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
				locationCollection = locationRepository.findOne(new ObjectId(locationId));
			}
			if (size > 0)
				nutritionReferenceResponses = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.skip(page * size), Aggregation.limit(size),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime"))), NutritionReferenceCollection.class,
						NutritionReferenceResponse.class).getMappedResults();
			else
				nutritionReferenceResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						NutritionReferenceCollection.class, NutritionReferenceResponse.class).getMappedResults();
			
			for (NutritionReferenceResponse nutritionReferenceResponse : nutritionReferenceResponses) {
				
				if(locationCollection != null)
				{
					nutritionReferenceResponse.setLocationName(locationCollection.getLocationName());
				}
				if(userCollection != null)
				{
					nutritionReferenceResponse.setDoctorName(userCollection.getFirstName());
				}
				
				PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(new ObjectId(nutritionReferenceResponse.getPatientId()), new ObjectId(nutritionReferenceResponse.getLocationId()), new ObjectId(nutritionReferenceResponse.getHospitalId())); 
				if(patientCollection != null)
				{
					UserCollection patient = userRepository.findOne(patientCollection.getUserId());
					PatientShortCard patientCard = new PatientShortCard();
					BeanUtil.map(patient,patientCard);
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
	public NutritionGoalAnalytics getGoalAnalytics(String doctorId, String locationId, String role, Long fromDate , Long toDate)
	{
		NutritionGoalAnalytics nutritionGoalAnalytics = null;
		try {
			nutritionGoalAnalytics = new NutritionGoalAnalytics();
			nutritionGoalAnalytics
					.setReferredCount(getGoalStatusCount(doctorId, locationId, role, GoalStatus.REFERRED.getType(),  fromDate , toDate));
			nutritionGoalAnalytics
					.setAcceptedCount(getGoalStatusCount(doctorId, locationId, role, GoalStatus.ADOPTED.getType(),fromDate , toDate));
			nutritionGoalAnalytics
					.setOnHoldCount(getGoalStatusCount(doctorId, locationId, role, GoalStatus.ON_HOLD.getType(),fromDate , toDate));
			nutritionGoalAnalytics
					.setRejectedCount(getGoalStatusCount(doctorId, locationId, role, GoalStatus.REJECTED.getType(),fromDate , toDate));
			nutritionGoalAnalytics
					.setCompletedCount(getGoalStatusCount(doctorId, locationId, role, GoalStatus.COMPLETED.getType(),fromDate , toDate));
			nutritionGoalAnalytics.setMetGoalCount(
					getGoalStatusCount(doctorId, locationId, role, GoalStatus.MET_GOALS.getType(),fromDate , toDate));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return nutritionGoalAnalytics;
	}
	
	public Boolean changeStatus(String id, String regularityStatus, String goalStatus)
	{
		Boolean response = false;
		NutritionReferenceCollection nutritionReferenceCollection = null;
		try {
			if(!DPDoctorUtils.anyStringEmpty(id))
			{
				nutritionReferenceCollection = nutritionReferenceRepository.findOne(new ObjectId(id));
				if(nutritionReferenceCollection != null)
				{
					if(!DPDoctorUtils.anyStringEmpty(regularityStatus))
					{
						nutritionReferenceCollection.setRegularityStatus(regularityStatus);
					}
					if(!DPDoctorUtils.anyStringEmpty(goalStatus))
					{
						nutritionReferenceCollection.setGoalStatus(goalStatus);
						NutritionGoalStatusStampingCollection nutritionGoalStatusStampingCollection = nutritionGoalStatusStampingRepository
								.getByPatientDoctorLocationHospitalandStatus(nutritionReferenceCollection.getPatientId(),
										nutritionReferenceCollection.getReferredDoctorId(),
										nutritionReferenceCollection.getReferredLocationId(),
										nutritionReferenceCollection.getReferredHospitalId(), goalStatus);
						
						if(nutritionGoalStatusStampingCollection != null)
						{
							nutritionGoalStatusStampingCollection.setUpdatedTime(new Date());
							nutritionGoalStatusStampingCollection = nutritionGoalStatusStampingRepository.save(nutritionGoalStatusStampingCollection);
						}
						else
						{
							nutritionGoalStatusStampingCollection = new NutritionGoalStatusStampingCollection();
							nutritionGoalStatusStampingCollection.setDoctorId(nutritionReferenceCollection.getDoctorId());
							nutritionGoalStatusStampingCollection.setLocationId(nutritionReferenceCollection.getLocationId());
							nutritionGoalStatusStampingCollection.setHospitalId(nutritionReferenceCollection.getHospitalId());
							nutritionGoalStatusStampingCollection.setReferredDoctorId(nutritionReferenceCollection.getReferredDoctorId());
							nutritionGoalStatusStampingCollection.setReferredLocationId(nutritionReferenceCollection.getReferredLocationId());
							nutritionGoalStatusStampingCollection.setReferredHospitalId(nutritionReferenceCollection.getReferredHospitalId());
							nutritionGoalStatusStampingCollection.setPatientId(nutritionReferenceCollection.getPatientId());
							nutritionGoalStatusStampingCollection.setGoalStatus(goalStatus);
							nutritionGoalStatusStampingCollection.setCreatedTime(new Date());
							nutritionGoalStatusStampingCollection.setUpdatedTime(new Date());
							UserCollection userCollection = userRepository.findOne(nutritionReferenceCollection.getReferredDoctorId());
							nutritionGoalStatusStampingCollection.setCreatedBy(userCollection.getCreatedBy());
							nutritionGoalStatusStampingCollection = nutritionGoalStatusStampingRepository.save(nutritionGoalStatusStampingCollection);
						}
					}
					response = true;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
	
	
	public NutritionReferenceResponse getNutritionReferenceResposneById(String id)
	{
		NutritionReferenceResponse response = null;
		NutritionReferenceCollection nutritionReferenceCollection = null;
		try {
			if(!DPDoctorUtils.anyStringEmpty(id))
			{
				nutritionReferenceCollection = nutritionReferenceRepository.findOne(new ObjectId(id));
				if(nutritionReferenceCollection != null)
				{
					response = new NutritionReferenceResponse();
					BeanUtil.map(nutritionReferenceCollection, response);
					LocationCollection locationCollection = locationRepository.findOne(new ObjectId(response.getHospitalId()));
					if(locationCollection != null)
					{
						response.setLocationName(locationCollection.getLocationName());
					}
					UserCollection userCollection = userRepository.findOne(new ObjectId(response.getLocationId()));
					if(userCollection != null)
					{
						response.setDoctorName(userCollection.getFirstName());
					}
					PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(new ObjectId(response.getPatientId()), new ObjectId(response.getLocationId()), new ObjectId(response.getHospitalId())); 
					if(patientCollection != null)
					{
						UserCollection patient = userRepository.findOne(patientCollection.getUserId());
						PatientShortCard patientCard = new PatientShortCard();
						BeanUtil.map(patient,patientCard);
						BeanUtil.map(patientCollection, patientCard);
						response.setPatient(patientCard);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return response;
	}
	
	private Long getGoalStatusCount(String doctorId, String locationId,String role, String status , Long fromDate , Long toDate)
	{
		
		Long count = 0l;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
			{
				if (role != null) {
					if (role.equals(RoleEnum.NUTRITIONIST.getRole())) {
						criteria.and("referredDoctorId").is(new ObjectId(doctorId));
					} else {
						criteria.and("doctorId").is(new ObjectId(doctorId));
					}
				}
				else {
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
			// TODO: handle exception
		}
		return count;
	}
	
	private ObjectId registerPatientIfNotRegistered(AddEditNutritionReferenceRequest request, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId) {
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
	
	
}
