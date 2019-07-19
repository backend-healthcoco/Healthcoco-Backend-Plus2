package com.dpdocter.scheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringEscapeUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.PatientNumberAndUserIds;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.BlockUserCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.SearchRequestFromUserCollection;
import com.dpdocter.collections.SearchRequestToPharmacyCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.ReplyType;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.BlockUserRepository;
import com.dpdocter.repository.SearchRequestToPharmacyRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.SearchRequestFromUserResponse;
import com.dpdocter.response.UserFakeRequestDetailResponse;
import com.dpdocter.response.UserNutritionSubscriptionResponse;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.mongodb.BasicDBObject;

@Service
public class AsyncService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BlockUserRepository blockUserRepository;

	@Value(value = "${pharmacy.fakerequest.hour}")
	private String requestLimitForhour;

	@Value(value = "${pharmacy.fakerequest.day}")
	private String requestLimitForday;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${nutrition.suscription.plan.transaction.success.message}")
	private String successmassage;

	@Value(value = "${nutrition.suscription.plan.transaction.decline.message}")
	private String declinemassage;

	@Autowired
	private SearchRequestToPharmacyRepository searchRequestToPharmacyRepository;

	@Autowired
	private PushNotificationServices pushNotificationServices;

	@Autowired
	private SMSServices sMSServices;

	@Autowired
	private EmailTackService emailTackService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private MailService mailService;

	@Async
	public void checkFakeRequestCount(String userId, BlockUserCollection blockUserCollection)
			throws InterruptedException {
		UserFakeRequestDetailResponse detailResponse = getUserFakeRequestCount(userId);
		if (detailResponse.getNoOfAttemptInHour() >= Integer.parseInt(requestLimitForhour)
				|| detailResponse.getNoOfAttemptIn24Hour() >= Integer.parseInt(requestLimitForday)) {

			if (blockUserCollection != null) {
				if (detailResponse.getNoOfAttemptIn24Hour() >= Integer.parseInt(requestLimitForday)) {
					blockUserCollection.setIsForDay(true);

				} else {
					blockUserCollection.setIsForHour(true);
				}

			} /*
				 * else { blockUserCollection = new BlockUserCollection(); if
				 * (detailResponse.getNoOfAttemptIn24Hour() >=
				 * Integer.parseInt(requestLimitForday)) {
				 * blockUserCollection.setIsForDay(true);
				 * 
				 * } else { blockUserCollection.setIsForHour(true); }
				 * blockUserCollection.setCreatedTime(new Date()); }
				 */

			blockUserCollection.setUserIds(detailResponse.getUserIds());
			blockUserCollection.setUpdatedTime(new Date());
			blockUserCollection = blockUserRepository.save(blockUserCollection);

		}

	}

	public UserFakeRequestDetailResponse getUserFakeRequestCount(String userId) {
		UserFakeRequestDetailResponse response = new UserFakeRequestDetailResponse();
		try {

			Integer countfor24Hour = 0;
			Integer countforHour = 0;
			Criteria criteria = new Criteria();
			UserCollection userCollection = userRepository.findById(new ObjectId(userId)).orElse(null);
			if (userCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid patient Id");
			}

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria("userName").regex("^" + userCollection.getMobileNumber(), "i")
							.and("userState").is("USERSTATECOMPLETE")),
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$mobileNumber")
									.append("userIds", new BasicDBObject("$push", "$_id"))
									.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber")))));

			PatientNumberAndUserIds user = mongoTemplate
					.aggregate(aggregation, UserCollection.class, PatientNumberAndUserIds.class)
					.getUniqueMappedResult();

			DateTime dateTime = new DateTime().minusHours(24);
			Date date = dateTime.toDate();
			criteria.and("createdTime").gt(date);
			criteria.and("orders").size(0).and("response.replyType").is("YES").and("userId").in(user.getUserIds());

			aggregation = Aggregation.newAggregation(
					Aggregation.lookup("search_request_to_pharmacy_cl", "uniqueRequestId", "uniqueRequestId",
							"response"),
					Aggregation.unwind("response"),
					Aggregation.lookup("order_drug_cl", "uniqueRequestId", "uniqueRequestId", "orders"),
					Aggregation.match(criteria),
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("uniqueRequestId", "$uniqueRequestId"))
									.append("uniqueRequestId", new BasicDBObject("$first", "$uniqueRequestId")))));

			countfor24Hour = mongoTemplate
					.aggregate(aggregation, SearchRequestFromUserCollection.class, SearchRequestFromUserResponse.class)
					.getMappedResults().size();

			criteria = new Criteria();
			dateTime = new DateTime().minusHours(1);
			date = dateTime.toDate();
			criteria.and("createdTime").gt(date);

			criteria.and("orders").size(0).and("response.replyType").is("YES").and("userId").in(user.getUserIds());

			aggregation = Aggregation.newAggregation(
					Aggregation.lookup("search_request_to_pharmacy_cl", "uniqueRequestId", "uniqueRequestId",
							"response"),
					Aggregation.unwind("response"),
					Aggregation.lookup("order_drug_cl", "uniqueRequestId", "uniqueRequestId", "orders"),
					Aggregation.match(criteria),
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("uniqueRequestId", "$uniqueRequestId"))
									.append("uniqueRequestId", new BasicDBObject("$first", "$uniqueRequestId")))));

			countforHour = mongoTemplate
					.aggregate(aggregation, SearchRequestFromUserCollection.class, SearchRequestFromUserResponse.class)
					.getMappedResults().size();
			response.setUserIds(user.getUserIds());
			response.setNoOfAttemptIn24Hour(countfor24Hour);
			response.setNoOfAttemptInHour(countforHour);

		} catch (Exception e) {

			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting count user Fake Request " + e.getMessage());

		}
		return response;
	}

	@Async
	public void changeRequestStatus(String uniqueRequestId, ObjectId localeId) throws InterruptedException {
		List<ObjectId> LocaleIds = new ArrayList<ObjectId>();

		Criteria criteria = new Criteria().and("uniqueRequestId").is(uniqueRequestId)
				.orOperator(new Criteria("replyType").is(null), new Criteria("replyType").exists(false));
		List<SearchRequestToPharmacyCollection> searchRequestToPharmacyCollections = mongoTemplate
				.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
						SearchRequestToPharmacyCollection.class, SearchRequestToPharmacyCollection.class)
				.getMappedResults();
		for (SearchRequestToPharmacyCollection requestToPharmacyCollection : searchRequestToPharmacyCollections) {
			LocaleIds.add(requestToPharmacyCollection.getLocaleId());
			requestToPharmacyCollection.setReplyType(ReplyType.REQUEST_FULFILLED.getReplyType());
		}
		if (!LocaleIds.isEmpty() && LocaleIds != null) {
			searchRequestToPharmacyRepository.saveAll(searchRequestToPharmacyCollections);
			pushNotificationServices.notifyRefreshAll(RoleEnum.PHARMIST, LocaleIds, "refresh",
					ComponentType.REFRESH_REQUEST);
		}

	}

	@Async
	public void changeOrderStatus(String uniqueRequestId, ObjectId localeId, ObjectId userId)
			throws InterruptedException {

		List<SearchRequestToPharmacyCollection> searchRequestToPharmacyCollections = searchRequestToPharmacyRepository
				.findByUniqueRequestIdPharmacyIdAndReplyType(uniqueRequestId, "YES", localeId);

		for (SearchRequestToPharmacyCollection requestToPharmacyCollection : searchRequestToPharmacyCollections) {

			requestToPharmacyCollection.setReplyType(ReplyType.ORDER_FULFILLED.getReplyType());
		}
		if (searchRequestToPharmacyCollections != null && !searchRequestToPharmacyCollections.isEmpty()) {
			searchRequestToPharmacyRepository.saveAll(searchRequestToPharmacyCollections);
			pushNotificationServices.notifyRefresh(userId.toString(), "", "", RoleEnum.PATIENT, "refresh",
					ComponentType.REFRESH_RESPONSE);

		}

	}

	@Async
	public void sendNutritionTransactionStatusMessage(
			UserNutritionSubscriptionResponse userNutritionSubscriptionResponse, UserCollection userCollection) {
		try {

			if (userCollection != null) {
				String message = "";
				if (userNutritionSubscriptionResponse.getTransactionStatus().toLowerCase().equalsIgnoreCase("Success"))
					message = successmassage;
				else if (userNutritionSubscriptionResponse.getTransactionStatus().toLowerCase()
						.equalsIgnoreCase("Aborted")
						|| userNutritionSubscriptionResponse.getTransactionStatus().toLowerCase()
								.equalsIgnoreCase("Decline"))
					message = declinemassage;

				message = StringEscapeUtils.unescapeJava(message);
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

				smsTrackDetail.setType("SUBSCRIPTION_NUTRITION_PLAN_STATUS");
				SMSDetail smsDetail = new SMSDetail();
				smsDetail.setUserId(userCollection.getId());
				SMS sms = new SMS();
				smsDetail.setUserName(userCollection.getFirstName());
				sms.setSmsText(message
						.replace("{amount}", "Rs. " + userNutritionSubscriptionResponse.getAmount().toString())
						.replace("{PlanName}", userNutritionSubscriptionResponse.getSubscriptionPlan().getTitle()));

				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(userCollection.getMobileNumber());
				sms.setSmsAddress(smsAddress);
				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				sMSServices.sendSMS(smsTrackDetail, true);
				System.out.println("sms sent");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Async
	public void createMailNutritionTransactionStatus(
			UserNutritionSubscriptionResponse userNutritionSubscriptionResponse, UserCollection userCollection) {

		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {

			emailTrackCollection.setType(ComponentType.SUBSCRIPTION_NUTRITION_PLAN.getType());
			emailTrackCollection.setSubject("Your payment of {planName} for Rs {amount} is received.");

			emailTrackCollection.setPatientName(userCollection.getFirstName());
			emailTrackCollection.setPatientId(userCollection.getId());

			emailTackService.saveEmailTrack(emailTrackCollection);
			String template = "";
			if (userNutritionSubscriptionResponse.getTransactionStatus().toLowerCase().equalsIgnoreCase("Success"))
				template = "nutritionPaymentSuccess.vm";
			else if (userNutritionSubscriptionResponse.getTransactionStatus().toLowerCase().equalsIgnoreCase("Aborted")
					|| userNutritionSubscriptionResponse.getTransactionStatus().toLowerCase()
							.equalsIgnoreCase("Decline"))
				template = "nutritionPaymentFailed.vm";
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("IST"));

			String body = mailBodyGenerator.generatePaymentEmailBody(userNutritionSubscriptionResponse.getOrderId(),
					userNutritionSubscriptionResponse.getNutritionPlan().getTitle(),
					userNutritionSubscriptionResponse.getAmount().toString(), userCollection.getFirstName(),
					sdf.format(userNutritionSubscriptionResponse.getFromDate()), template);
			mailService.sendEmail(userCollection.getEmailAddress(), "Healthcoco sent you Transaction Status", body,
					null);

			System.out.println("mail sent");

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}
}