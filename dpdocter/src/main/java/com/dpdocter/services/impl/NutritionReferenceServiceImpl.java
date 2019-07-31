package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.NutritionGoalAnalytics;
import com.dpdocter.beans.NutritionPlan;
import com.dpdocter.beans.NutritionReference;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.NutritionGoalStatusStampingCollection;
import com.dpdocter.collections.NutritionPlanCollection;
import com.dpdocter.collections.NutritionReferenceCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.GoalStatus;
import com.dpdocter.enums.RegularityStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.NutritionGoalStatusStampingRepository;
import com.dpdocter.repository.NutritionPlanRepository;
import com.dpdocter.repository.NutritionReferenceRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditNutritionReferenceRequest;
import com.dpdocter.response.NutritionReferenceResponse;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.NutritionReferenceService;
import com.dpdocter.services.NutritionService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class NutritionReferenceServiceImpl implements NutritionReferenceService {

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
	private NutritionService nutritionService;

	@Autowired
	private NutritionPlanRepository nutritionPlanRepository;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;
	@Autowired
	private MailService mailService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${mail.nutrition.reference.to}")
	private String mailTo;

	@Value(value = "${mail.nutrition.reference.subject}")
	private String nutritionReferenceMailsubject;

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
		NutritionPlanCollection planCollection = null;
		String birthDate = "", profession = "", gender = "", address = "", city = "", pinCode = "", doctorName = "",
				planName = "", subPlan = "";
		NutritionPlan nutritionPlan = null;
		try {
			UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (userCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "doctor not found");
			}

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				nutritionReferenceCollection = nutritionReferenceRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			if (nutritionReferenceCollection == null) {
				nutritionReferenceCollection = new NutritionReferenceCollection();
				nutritionReferenceCollection.setCreatedTime(new Date());
				nutritionReferenceCollection
						.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
			}
			BeanUtil.map(request, nutritionReferenceCollection);
			nutritionReferenceCollection.setReports(request.getReports());
			nutritionReferenceCollection = nutritionReferenceRepository.save(nutritionReferenceCollection);
			if (nutritionReferenceCollection != null) {
				response = new NutritionReferenceResponse();
				BeanUtil.map(nutritionReferenceCollection, response);
				NutritionGoalStatusStampingCollection nutritionGoalStatusStampingCollection = null;

				response.setDoctorName(userCollection.getTitle() + " " + userCollection.getFirstName());

				nutritionGoalStatusStampingCollection = nutritionGoalStatusStampingRepository
						.findByPatientIdAndDoctorIdAndLocationIdAndHospitalIdAndGoalStatus(nutritionReferenceCollection.getPatientId(),
								nutritionReferenceCollection.getDoctorId(),
								nutritionReferenceCollection.getLocationId(),
								nutritionReferenceCollection.getHospitalId(),
								nutritionReferenceCollection.getGoalStatus().getType());

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
					nutritionGoalStatusStampingCollection.setGoalStatus(nutritionReferenceCollection.getGoalStatus());
					if (userCollection != null) {
						nutritionGoalStatusStampingCollection
								.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
					}
					nutritionGoalStatusStampingCollection.setCreatedTime(new Date());
					nutritionGoalStatusStampingCollection = nutritionGoalStatusStampingRepository
							.save(nutritionGoalStatusStampingCollection);
				}
				if (response.getLocationId() != null) {
					LocationCollection locationCollection = locationRepository
							.findById(new ObjectId(response.getLocationId())).orElse(null);
					response.setLocationName(locationCollection.getLocationName());
				}

				PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						new ObjectId(response.getPatientId()), new ObjectId(response.getLocationId()),
						new ObjectId(response.getHospitalId()));
				if (patientCollection != null) {
					PatientShortCard patientCard = new PatientShortCard();
					BeanUtil.map(patientCollection, patientCard);
					response.setPatient(patientCard);
				} else {

					throw new BusinessException(ServiceError.InvalidInput, "patient not found");

				}
				if (!DPDoctorUtils.anyStringEmpty(nutritionReferenceCollection.getNutritionPlanId())) {
					planCollection = nutritionPlanRepository.findById(nutritionReferenceCollection.getNutritionPlanId()).orElse(null);
					if (planCollection != null) {
						planName = planCollection.getTitle();
						nutritionPlan = new NutritionPlan();
						BeanUtil.map(planCollection, nutritionPlan);

						if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getPlanImage())) {
							nutritionPlan.setPlanImage(getFinalImageURL(nutritionPlan.getPlanImage()));
						}
						if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getBannerImage())) {
							nutritionPlan.setBannerImage(getFinalImageURL(nutritionPlan.getBannerImage()));
						}
						response.setNutritionPlan(nutritionPlan);

					}
					if (!DPDoctorUtils.anyStringEmpty(nutritionReferenceCollection.getSubscriptionPlanId())) {
						response.setSubscriptionPlan(nutritionService
								.getSubscritionPlan(nutritionReferenceCollection.getSubscriptionPlanId().toString()));

						subPlan = response.getSubscriptionPlan() != null ? response.getSubscriptionPlan().getTitle()
								: "";
					}

				}
				if (DPDoctorUtils.anyStringEmpty(request.getId())) {
					if (patientCollection != null) {
						if (patientCollection.getAddress() != null) {

							address = (!DPDoctorUtils.anyStringEmpty(patientCollection.getAddress().getStreetAddress())
									? patientCollection.getAddress().getStreetAddress() + ", "
									: "")
									+ (!DPDoctorUtils
											.anyStringEmpty(patientCollection.getAddress().getLandmarkDetails())
													? patientCollection.getAddress().getLandmarkDetails() + ", "
													: "")
									+ (!DPDoctorUtils.anyStringEmpty(patientCollection.getAddress().getLocality())
											? patientCollection.getAddress().getLocality() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(patientCollection.getAddress().getCity())
											? patientCollection.getAddress().getCity() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(patientCollection.getAddress().getState())
											? patientCollection.getAddress().getState() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(patientCollection.getAddress().getCountry())
											? patientCollection.getAddress().getCountry() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(patientCollection.getAddress().getPostalCode())
											? patientCollection.getAddress().getPostalCode()
											: "");
							city = patientCollection.getAddress().getCity();
							pinCode = patientCollection.getAddress().getPostalCode();

						}
						gender = patientCollection.getGender();
						profession = patientCollection.getProfession();
						birthDate = patientCollection.getDob() != null
								? patientCollection.getDob().getDays() + "/" + patientCollection.getDob().getMonths()
										+ "/" + patientCollection.getDob().getYears()
								: "";
					}
					nutritionReferenceMailsubject = nutritionReferenceMailsubject
							.replace("{patient}", request.getLocalPatientName())
							.replace("{doctor}", response.getDoctorName());
					String body = mailBodyGenerator.nutritionReferenceEmailBody(request.getLocalPatientName(),
							request.getMobileNumber(), birthDate, profession, gender, address, city, pinCode,
							userCollection.getTitle() + " " + userCollection.getFirstName(), planName, subPlan,
							"nutritionReferenceTemplate.vm");
					mailService.sendEmail(mailTo, nutritionReferenceMailsubject, body, null);

				}
			}
		} catch (

		Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while adding nutrition reference" + e.getMessage());
		}
		return response;

	}

	@Override
	@Transactional
	public List<NutritionReference> getNutritionReferenceList(int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String searchTerm, String updatedTime) {
		List<NutritionReference> nutritionReferenceResponses = null;

		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(patientId)) {

				criteria.and("patientId").is(new ObjectId(patientId));
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {

				criteria.and("locationId").is(new ObjectId(locationId));

			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {

				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (!DPDoctorUtils.anyStringEmpty(updatedTime)) {

				criteria.and("createdTime").is(new Date(Long.parseLong(updatedTime)));

			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("localPatientName").regex("^" + searchTerm, "i"),
						new Criteria("localPatientName").regex("^" + searchTerm));
			if (size > 0) {
				nutritionReferenceResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("nutrition_plan_cl", "nutritionPlanId", "_id", "nutritionPlan"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$nutritionPlan")
												.append("preserveNullAndEmptyArrays", true)
												.append("includeArrayIndex", "arrayIndex1"))),
								Aggregation.lookup("subscription_nutrition_plan_cl", "subscriptionPlanId", "_id",
										"subscriptionPlan"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$subscriptionPlan")
												.append("preserveNullAndEmptyArrays", true)
												.append("includeArrayIndex", "arrayIndex1"))),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
								Aggregation.skip((long)page * size), Aggregation.limit(size)),
						NutritionReferenceCollection.class, NutritionReference.class).getMappedResults();
			} else {
				nutritionReferenceResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("nutrition_plan_cl", "nutritionPlanId", "_id", "nutritionPlan"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$nutritionPlan")
												.append("preserveNullAndEmptyArrays", true)
												.append("includeArrayIndex", "arrayIndex1"))),
								Aggregation.lookup("subscription_nutrition_plan_cl", "subscriptionPlanId", "_id",
										"subscriptionPlan"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$subscriptionPlan")
												.append("preserveNullAndEmptyArrays", true)
												.append("includeArrayIndex", "arrayIndex1"))),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						NutritionReferenceCollection.class, NutritionReference.class).getMappedResults();
			}
			for (NutritionReference nutritionReferenceResponse : nutritionReferenceResponses) {

				if (nutritionReferenceResponse.getNutritionPlan() != null) {
					if (!DPDoctorUtils.anyStringEmpty(nutritionReferenceResponse.getNutritionPlan().getPlanImage())) {
						nutritionReferenceResponse.getNutritionPlan().setPlanImage(
								getFinalImageURL(nutritionReferenceResponse.getNutritionPlan().getPlanImage()));
					}
					if (!DPDoctorUtils.anyStringEmpty(nutritionReferenceResponse.getNutritionPlan().getBannerImage())) {
						nutritionReferenceResponse.getNutritionPlan().setBannerImage(
								getFinalImageURL(nutritionReferenceResponse.getNutritionPlan().getBannerImage()));
					}
				}
				if (nutritionReferenceResponse.getSubscriptionPlan() != null) {
					nutritionReferenceResponse.getSubscriptionPlan().setBackgroundImage(
							getFinalImageURL(nutritionReferenceResponse.getSubscriptionPlan().getBackgroundImage()));
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting nutrition refference " + e.getMessage());
		}
		return nutritionReferenceResponses;
	}

	@Override
	@Transactional
	public NutritionGoalAnalytics getGoalAnalytics(String doctorId, String locationId, Long fromDate, Long toDate) {
		NutritionGoalAnalytics nutritionGoalAnalytics = null;
		try {
			nutritionGoalAnalytics = new NutritionGoalAnalytics();
			nutritionGoalAnalytics.setReferredCount(
					getGoalStatusCount(doctorId, locationId, GoalStatus.REFERRED.getType(), fromDate, toDate));
			nutritionGoalAnalytics.setAcceptedCount(
					getGoalStatusCount(doctorId, locationId, GoalStatus.ADOPTED.getType(), fromDate, toDate));
			nutritionGoalAnalytics.setOnHoldCount(
					getGoalStatusCount(doctorId, locationId, GoalStatus.ON_HOLD.getType(), fromDate, toDate));
			nutritionGoalAnalytics.setRejectedCount(
					getGoalStatusCount(doctorId, locationId, GoalStatus.REJECTED.getType(), fromDate, toDate));
			nutritionGoalAnalytics.setCompletedCount(
					getGoalStatusCount(doctorId, locationId, GoalStatus.COMPLETED.getType(), fromDate, toDate));
			nutritionGoalAnalytics.setMetGoalCount(
					getGoalStatusCount(doctorId, locationId, GoalStatus.MET_GOALS.getType(), fromDate, toDate));
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting nutrition goal status " + e.getMessage());
		}
		return nutritionGoalAnalytics;
	}

	@Override
	public Boolean changeStatus(String id, String regularityStatus, String goalStatus) {
		Boolean response = false;
		NutritionReferenceCollection nutritionReferenceCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				nutritionReferenceCollection = nutritionReferenceRepository.findById(new ObjectId(id)).orElse(null);
				if (nutritionReferenceCollection != null) {
					if (!DPDoctorUtils.anyStringEmpty(regularityStatus)) {
						nutritionReferenceCollection.setRegularityStatus(RegularityStatus.valueOf(regularityStatus));
					}
					if (!DPDoctorUtils.anyStringEmpty(goalStatus)) {
						nutritionReferenceCollection.setGoalStatus(GoalStatus.valueOf(goalStatus));
						NutritionGoalStatusStampingCollection nutritionGoalStatusStampingCollection = nutritionGoalStatusStampingRepository
								.findByPatientIdAndDoctorIdAndLocationIdAndHospitalIdAndGoalStatus(
										nutritionReferenceCollection.getPatientId(),
										nutritionReferenceCollection.getDoctorId(),
										nutritionReferenceCollection.getLocationId(),
										nutritionReferenceCollection.getHospitalId(), goalStatus);

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
									.setPatientId(nutritionReferenceCollection.getPatientId());
							nutritionGoalStatusStampingCollection.setGoalStatus(GoalStatus.valueOf(goalStatus));
							nutritionGoalStatusStampingCollection.setCreatedTime(new Date());
							nutritionGoalStatusStampingCollection.setUpdatedTime(new Date());
							UserCollection userCollection = userRepository
									.findById(nutritionReferenceCollection.getDoctorId()).orElse(null);
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
			throw new BusinessException(ServiceError.Unknown,
					"Error while Change nutrition goal status " + e.getMessage());
		}
		return response;
	}

	@Override
	public NutritionReferenceResponse getNutritionReferenceById(String id) {
		NutritionReferenceResponse response = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				response = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(new Criteria("id").is(new ObjectId(id))),
								Aggregation.lookup("nutrition_plan_cl", "nutritionPlanId", "_id", "nutritionPlan"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$nutritionPlan")
												.append("preserveNullAndEmptyArrays", true)
												.append("includeArrayIndex", "arrayIndex1"))),
								Aggregation.lookup("subscription_nutrition_plan_cl", "subscriptionPlanId", "_id",
										"subscriptionPlan"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$subscriptionPlan")
												.append("preserveNullAndEmptyArrays", true)
												.append("includeArrayIndex", "arrayIndex1")))),
						NutritionReferenceCollection.class, NutritionReferenceResponse.class).getUniqueMappedResult();
				if (response != null) {
					LocationCollection locationCollection = locationRepository
							.findById(new ObjectId(response.getHospitalId())).orElse(null);
					if (locationCollection != null) {
						response.setLocationName(locationCollection.getLocationName());
					}
					UserCollection userCollection = userRepository.findById(new ObjectId(response.getDoctorId())).orElse(null);
					if (userCollection != null) {
						response.setDoctorName(userCollection.getTitle() + " " + userCollection.getFirstName());
					}
					PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
							new ObjectId(response.getPatientId()), new ObjectId(response.getLocationId()),
							new ObjectId(response.getHospitalId()));
					if (patientCollection != null) {
						UserCollection patient = userRepository.findById(patientCollection.getUserId()).orElse(null);
						PatientShortCard patientCard = new PatientShortCard();
						BeanUtil.map(patient, patientCard);
						BeanUtil.map(patientCollection, patientCard);
						response.setPatient(patientCard);
					}

					if (response.getNutritionPlan() != null) {
						if (!DPDoctorUtils.anyStringEmpty(response.getNutritionPlan().getPlanImage())) {
							response.getNutritionPlan()
									.setPlanImage(getFinalImageURL(response.getNutritionPlan().getPlanImage()));
						}
						if (!DPDoctorUtils.anyStringEmpty(response.getNutritionPlan().getBannerImage())) {
							response.getNutritionPlan()
									.setBannerImage(getFinalImageURL(response.getNutritionPlan().getBannerImage()));
						}
					}
					if (response.getSubscriptionPlan() != null) {
						response.getSubscriptionPlan().setBackgroundImage(
								getFinalImageURL(response.getSubscriptionPlan().getBackgroundImage()));
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting nutrition reference " + e.getMessage());
		}
		return response;
	}

	private Long getGoalStatusCount(String doctorId, String locationId, String status, Long fromDate, Long toDate) {

		Long count = 0l;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {

				criteria.and("doctorId").is(new ObjectId(doctorId));

			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {

				criteria.and("locationId").is(new ObjectId(locationId));

			}

			criteria.and("goalStatus").is(status);

			if (fromDate > 0 && toDate > 0) {

				criteria.and("updatedTime").gte(new Date(fromDate)).lte(new Date(toDate));
			} else if (toDate > 0) {
				criteria.and("updatedTime").lte(new Date(toDate));
			} else if (fromDate > 0) {
				criteria.and("updatedTime").gte(new Date(fromDate));
			}
			Query query = new Query();
			query.addCriteria(criteria);
			count = mongoOperations.count(query, NutritionGoalStatusStampingCollection.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting nutrition goal status " + e.getMessage());
		}
		return count;
	}

}
