package com.dpdocter.services.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
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
import com.dpdocter.beans.NutritionPlan;
import com.dpdocter.beans.SubscriptionNutritionPlan;
import com.dpdocter.beans.SugarSetting;
import com.dpdocter.beans.Testimonial;
import com.dpdocter.beans.User;
import com.dpdocter.beans.UserNutritionSubscription;
import com.dpdocter.collections.NutritionPlanCollection;
import com.dpdocter.collections.SubscriptionNutritionPlanCollection;
import com.dpdocter.collections.SugarSettingCollection;
import com.dpdocter.collections.TestimonialCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserNutritionSubscriptionCollection;
import com.dpdocter.elasticsearch.response.NutritionPlanWithCategoryShortResponse;
import com.dpdocter.enums.NutritionPlanType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.NutritionPlanRepository;
import com.dpdocter.repository.SubscritptionNutritionPlanRepository;
import com.dpdocter.repository.SugarSettingRepository;
import com.dpdocter.repository.UserNutritionSubscriptionRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.NutritionPlanRequest;
import com.dpdocter.response.NutritionPlanResponse;
import com.dpdocter.response.NutritionPlanWithCategoryResponse;
import com.dpdocter.response.UserNutritionSubscriptionResponse;
import com.dpdocter.scheduler.AsyncService;
import com.dpdocter.services.NutritionService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class NutritionServiceImpl implements NutritionService {

	private static Logger logger = Logger.getLogger(NutritionServiceImpl.class.getName());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

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
	
	@Autowired
	private SugarSettingRepository sugarSettingRepository;

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
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

			Criteria criteria = new Criteria("id").is(new ObjectId(id)).and("subscriptionNutritionPlan.discarded")
					.is(false);

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
					.findOne(new ObjectId(id));
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
			UserCollection userCollection = userRepository.findOne(nutritionSubscriptionCollection.getUserId());
			if (userCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "user not found By Id ");
			}
			NutritionPlanCollection nutritionPlanCollection = nutritionPlanRepository
					.findOne(nutritionSubscriptionCollection.getNutritionPlanId());
			if (nutritionPlanCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Nutrition Plan not found By Id ");
			}
			SubscriptionNutritionPlanCollection subscriptionNutritionPlanCollection = subscritptionNutritionPlanRepository
					.findOne(nutritionSubscriptionCollection.getSubscriptionPlanId());

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
					asyncService.sendNutritionTransactionStatusMessage(response, userCollection);
					if (!DPDoctorUtils.anyStringEmpty(userCollection.getEmailAddress()))
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
					.findOne(new ObjectId(id));
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

	@Override
	public List<NutritionPlan> getNutritionPlans(List<ObjectId> idList) {
		List<NutritionPlan> response = null;
		try {

			Aggregation aggregation = null;

			Criteria criteria = new Criteria("id").in(idList).and("discarded").is(false);

			aggregation = Aggregation.newAggregation(

					Aggregation.match(criteria), Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<NutritionPlan> results = mongoTemplate.aggregate(aggregation,
					NutritionPlanCollection.class, NutritionPlan.class);
			response = results.getMappedResults();
			for (NutritionPlan plan : response) {
				if (!DPDoctorUtils.anyStringEmpty(plan.getPlanImage())) {
					plan.setPlanImage(getFinalImageURL(plan.getPlanImage()));
				}
				if (!DPDoctorUtils.anyStringEmpty(plan.getBannerImage())) {
					plan.setBannerImage(getFinalImageURL(plan.getBannerImage()));
				}
			}
		} catch (BusinessException e) {

			logger.error("Error while getting nutrition Plan List" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Err" + "or while getting nutrition Plan List " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<SubscriptionNutritionPlan> getSubscritionPlans(List<ObjectId> idList) {
		List<SubscriptionNutritionPlan> response = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria("id").in(idList).and("discarded").is(false);

			aggregation = Aggregation.newAggregation(

					Aggregation.match(criteria), Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<SubscriptionNutritionPlan> results = mongoTemplate.aggregate(aggregation,
					SubscriptionNutritionPlanCollection.class, SubscriptionNutritionPlan.class);
			response = results.getMappedResults();
			for (SubscriptionNutritionPlan subscriptionNutritionPlan : response) {
				if (!DPDoctorUtils.anyStringEmpty(subscriptionNutritionPlan.getBackgroundImage())) {
					subscriptionNutritionPlan
							.setBackgroundImage(getFinalImageURL(subscriptionNutritionPlan.getBackgroundImage()));
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
	public List<NutritionPlanWithCategoryShortResponse> getNutritionPlanDetailsByCategory(NutritionPlanRequest request) {
		List<NutritionPlanWithCategoryShortResponse> response = null;
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
							.append("nutritionPlan.shortPlanDescription", "$shortPlanDescription")
							.append("nutritionPlan.amount", "$amount")
							.append("nutritionPlan.discountedAmount", "$discountedAmount")
							.append("nutritionPlan.discarded", "$discarded")
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

			AggregationResults<NutritionPlanWithCategoryShortResponse> results = mongoTemplate.aggregate(aggregation,
					NutritionPlanCollection.class, NutritionPlanWithCategoryShortResponse.class);
			response = results.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting nutrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrition Plan " + e.getMessage());

		}
		return response;
	}
	
	@Override
	public NutritionPlan getNutritionPlanById(String id) {
		NutritionPlan response = null;
		try {

			Aggregation aggregation = null;

			Criteria criteria = new Criteria("id").is(new ObjectId(id));

			aggregation = Aggregation.newAggregation(
					Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<NutritionPlan> results = mongoTemplate.aggregate(aggregation,
					NutritionPlanCollection.class, NutritionPlan.class);
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
	public List<Testimonial> getTestimonialsByPlanId(String planId, int size , int page) {
		List<Testimonial> response = null;
		Aggregation aggregation = null;
		try {
			Criteria criteria = new Criteria("planId").is(new ObjectId(planId));
			
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						 Aggregation.sort(Sort.Direction.DESC, "createdTime"),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						 Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

			AggregationResults<Testimonial> results = mongoTemplate.aggregate(aggregation,
					TestimonialCollection.class, Testimonial.class);
			response = results.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting nutrition Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrition Plan " + e.getMessage());

		}
		return response;
	}
	
	public SugarSetting addEditSugarSetting(SugarSetting request)
	{
		SugarSettingCollection sugarSettingCollection = null;
		SugarSetting response = null;
		
		try {
			if(!DPDoctorUtils.anyStringEmpty(request.getId()))
			{
				sugarSettingCollection = sugarSettingRepository.findOne(new ObjectId(request.getId()));
			}
			else
			{
				sugarSettingCollection = new SugarSettingCollection();
				sugarSettingCollection.setCreatedTime(new Date());
			}
			
			if(sugarSettingCollection == null)
			{
				throw new BusinessException(ServiceError.NoRecord,"Record not found");
			}
			
			BeanUtil.map(request, sugarSettingCollection);
			sugarSettingCollection = sugarSettingRepository.save(sugarSettingCollection);
			if(sugarSettingCollection != null)
			{
				response = new SugarSetting();
				BeanUtil.map(sugarSettingCollection, response);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
	
	@Override
	public SugarSetting getSugarSettingById(String id) {
		SugarSetting response = null;
		try {

			Aggregation aggregation = null;

			Criteria criteria = new Criteria("id").is(new ObjectId(id));

			aggregation = Aggregation.newAggregation(
					Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<SugarSetting> results = mongoTemplate.aggregate(aggregation,
					SugarSettingCollection.class, SugarSetting.class);
			response = results.getUniqueMappedResult();
		
		} catch (BusinessException e) {

			logger.error("Error while getting Sugar Setting " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Sugar Setting " + e.getMessage());

		}
		return response;
	}
	
	
}


