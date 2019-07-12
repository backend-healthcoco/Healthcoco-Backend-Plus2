
package com.dpdocter.services.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.util.json.Jackson;
import com.dpdocter.beans.Address;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.PatientNumberAndUserIds;
import com.dpdocter.collections.BlockUserCollection;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.OrderDrugCollection;
import com.dpdocter.collections.SearchRequestFromUserCollection;
import com.dpdocter.collections.SearchRequestToPharmacyCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ReplyType;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BlockUserRepository;
import com.dpdocter.repository.LocaleRepository;
import com.dpdocter.repository.OrderDrugRepository;
import com.dpdocter.repository.SearchRequestFromUserRepository;
import com.dpdocter.repository.SearchRequestToPharmacyRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.OrderDrugsRequest;
import com.dpdocter.request.UserSearchRequest;
import com.dpdocter.response.OrderDrugsResponse;
import com.dpdocter.response.SearchRequestFromUserResponse;
import com.dpdocter.response.SearchRequestToPharmacyResponse;
import com.dpdocter.response.UserFakeRequestDetailResponse;
import com.dpdocter.scheduler.AsyncService;
import com.dpdocter.services.PharmacyService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.UserFavouriteService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class PharmacyServiceImpl implements PharmacyService {

	private static Logger logger = Logger.getLogger(PharmacyServiceImpl.class.getName());

	@Value(value = "${mail.aws.key.id}")
	private String AWS_KEY;

	@Value(value = "${mail.aws.secret.key}")
	private String AWS_SECRET_KEY;

	@Value(value = "${search.pharmacy.request.queue}")
	private String searchPharmacyRequestQueue;

	@Value(value = "${pharmacy.response.queue}")
	private String pharmacyResponseQueue;

	@Value(value = "${pharmacy.fakerequest.hour}")
	private String requestLimitForhour;

	@Value(value = "${pharmacy.fakerequest.day}")
	private String requestLimitForday;

	@Autowired
	private SearchRequestFromUserRepository searchRequestFromUserRepository;

	@Autowired
	private AsyncService asyncService;

	@Autowired
	private LocaleRepository localeRepository;

//	@Autowired
//	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PushNotificationServices pushNotificationServices;

	@Autowired
	private SearchRequestToPharmacyRepository searchRequestToPharmacyRepository;

	@Autowired
	private OrderDrugRepository orderDrugRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private BlockUserRepository blockUserRepository;

//	@Autowired
//	private LocaleService localeService;

	@Autowired
	private UserFavouriteService userFavouriteService;

	@Override
	@Transactional
	public UserSearchRequest addSearchRequest(UserSearchRequest request) {
		/*
		 * TODO : generate uniqueRequestId then add request in queue and
		 * collection. write two different methods addInQueue and addInDb so
		 * that it can be reuse or else it will be easy to change flow if
		 * required
		 */
		UserSearchRequest response = null;

		BlockUserCollection blockUserCollection = mongoTemplate.findById(
				new Query(new Criteria("userIds").is(new ObjectId(request.getUserId()))), BlockUserCollection.class);
		if (blockUserCollection != null) {
			if (!blockUserCollection.getDiscarded()) {
				DateTime dateTime = null;
				Date date = null;
				if (blockUserCollection.getIsForDay()) {
					dateTime = new DateTime().minusDays(1);
					date = dateTime.toDate();
					if (!date.after(blockUserCollection.getUpdatedTime())) {
						throw new BusinessException(ServiceError.NoRecord, "You have blocked for next 24 hour");
					}
				} else if (blockUserCollection.getIsForHour()) {
					dateTime = new DateTime().minusHours(1);
					date = dateTime.toDate();
					if (!date.after(blockUserCollection.getUpdatedTime())) {
						throw new BusinessException(ServiceError.NoRecord, "You have blocked for next 1 hours");
					}
				}

				blockUserCollection.setIsForDay(false);
				blockUserCollection.setIsForHour(false);
				blockUserCollection.setUpdatedTime(new Date());
				blockUserCollection = blockUserRepository.save(blockUserCollection);

			}

		} else {
			blockUserCollection = new BlockUserCollection();
			blockUserCollection.setCreatedTime(new Date());

		}
		try {
			if (!blockUserCollection.getDiscarded()) {
				asyncService.checkFakeRequestCount(request.getUserId(), blockUserCollection);
			}

			// Instead of calling before block user collection its better to

			if (DPDoctorUtils.anyStringEmpty(request.getLocaleId())) {
				addSearchRequestInQueue(request);
				response = addSearchRequestInCollection(request);
			} else {
				OrderDrugCollection orderDrugCollection = new OrderDrugCollection();
				String uniqueRequestId = UniqueIdInitial.PHARMACY_REQUEST.getInitial()
						+ DPDoctorUtils.generateRandomId();
				request.setUniqueRequestId(uniqueRequestId);
				response = addSearchRequestInCollection(request);
				BeanUtil.map(request, orderDrugCollection);
				orderDrugCollection.setCreatedTime(new Date());
				orderDrugRepository.save(orderDrugCollection);
				pushNotificationServices.notifyPharmacy(request.getLocaleId(), request.getUniqueRequestId(), "",
						RoleEnum.PHARMIST, "Keep my order ready");
				userFavouriteService.addRemoveFavourites(request.getUserId(), request.getLocaleId(),
						Resource.PHARMACY.getType(), null, false);

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While adding Search Request In Queue");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While adding  Search Request In Queue");
		}

		return response;
	}

	private UserSearchRequest addSearchRequestInCollection(UserSearchRequest request) {
		UserSearchRequest response = null;
		try {
			SearchRequestFromUserCollection searchRequestFromUserCollection = new SearchRequestFromUserCollection();
			BeanUtil.map(request, searchRequestFromUserCollection);
			searchRequestFromUserCollection.setCreatedTime(new Date());
			if (request.getPrescriptionRequest() != null) {
				searchRequestFromUserCollection.setPrescriptionRequest(request.getPrescriptionRequest());
			} else {
				throw new BusinessException(ServiceError.Unknown, "No prescription found ");
			}
			searchRequestFromUserCollection = searchRequestFromUserRepository.save(searchRequestFromUserCollection);
			response = new UserSearchRequest();
			BeanUtil.map(searchRequestFromUserCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While adding request");
			throw new BusinessException(ServiceError.Unknown, " Error Occurred While adding request");
		}
		return response;

	}

	private void addSearchRequestInQueue(UserSearchRequest request) {

		AWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonSQS sqs = new AmazonSQSClient(credentials);
		sqs.setRegion(Region.getRegion(Regions.US_EAST_1));

		CreateQueueRequest createQueueRequest = new CreateQueueRequest(searchPharmacyRequestQueue);
		String searchPharmacyRequestQueueURL = sqs.createQueue(createQueueRequest).getQueueUrl();

		String uniqueRequestId = UniqueIdInitial.PHARMACY_REQUEST.getInitial() + DPDoctorUtils.generateRandomId();
		request.setUniqueRequestId(uniqueRequestId);

		String messageBody = Jackson.toJsonString(request);

		sqs.sendMessage(new SendMessageRequest(searchPharmacyRequestQueueURL, messageBody));
	}

	@Override
	@Transactional
	public OrderDrugsRequest orderDrugs(OrderDrugsRequest request) {
		OrderDrugsRequest response = null;
		OrderDrugCollection orderDrugCollection = null;

		try {
			SearchRequestToPharmacyCollection requestToPharmacyCollection = searchRequestToPharmacyRepository
					.findByRequestIdandPharmacyId(request.getUniqueRequestId(), new ObjectId(request.getLocaleId()),
							new ObjectId(request.getUserId()));
			if (!requestToPharmacyCollection.getIsAlreadyRequested()) {
				requestToPharmacyCollection.setIsAlreadyRequested(true);
				searchRequestToPharmacyRepository.save(requestToPharmacyCollection);
				orderDrugCollection = new OrderDrugCollection();
				BeanUtil.map(request, orderDrugCollection);
				orderDrugCollection.setCreatedTime(new Date());
				orderDrugRepository.save(orderDrugCollection);
				pushNotificationServices.notifyPharmacy(request.getLocaleId(), request.getUniqueRequestId(),
						request.getUniqueResponseId(), RoleEnum.PHARMIST, "Keep my order ready");
				response = new OrderDrugsRequest();
				BeanUtil.map(request, response);

				userFavouriteService.addRemoveFavourites(request.getUserId(), request.getLocaleId(),
						Resource.PHARMACY.getType(), null, false);

			} else {
				throw new BusinessException(ServiceError.InvalidInput, "already requested");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While ordering drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While ordering drugs");
		}
		return response;
	}

	@Override
	@Transactional
	public List<SearchRequestFromUserResponse> getPatientOrderHistoryList(String userId, long page, int size) {
		List<SearchRequestFromUserResponse> response = null;
		try {
			Criteria criteria = new Criteria("userId").is(new ObjectId(userId));
			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<SearchRequestFromUserResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					SearchRequestFromUserCollection.class, SearchRequestFromUserResponse.class);
			response = aggregationResults.getMappedResults();
			for (SearchRequestFromUserResponse searchRequestFromUserResponse : response) {
				int countForYes = getPharmacyListCountbyOrderHistory(searchRequestFromUserResponse.getUniqueRequestId(),
						ReplyType.YES.toString());
				int countForNo = getPharmacyListCountbyOrderHistory(searchRequestFromUserResponse.getUniqueRequestId(),
						ReplyType.NO.toString());
				searchRequestFromUserResponse.setCountForYes(countForYes);
				searchRequestFromUserResponse.setCountForNo(countForNo);
				if (searchRequestFromUserResponse.getLocaleId() != null) {
					LocaleCollection localeCollection = localeRepository
							.findById(new ObjectId(searchRequestFromUserResponse.getLocaleId())).orElse(null);
					if (localeCollection != null) {
						searchRequestFromUserResponse.setPharmacyName(localeCollection.getLocaleName());
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error while getting locales " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting locale List " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<SearchRequestToPharmacyResponse> getPharmacyListbyOrderHistory(String userId, String uniqueRequestId,
			String replyType, long page, int size, Double latitude, Double longitude) {
		List<SearchRequestToPharmacyResponse> response = null;
		try {
			Criteria criteria = new Criteria("userId").is(new ObjectId(userId)).and("uniqueRequestId")
					.is(uniqueRequestId);

			if (!DPDoctorUtils.anyStringEmpty(replyType))
				criteria = criteria.and("replyType").is(replyType);
			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("locale_cl", "localeId", "_id", "locale"), Aggregation.unwind("locale"),
						Aggregation.skip((page) * size), Aggregation.limit(size),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("locale_cl", "localeId", "_id", "locale"), Aggregation.unwind("locale"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<SearchRequestToPharmacyResponse> aggregationResults = mongoTemplate.aggregate(
					aggregation, SearchRequestToPharmacyCollection.class, SearchRequestToPharmacyResponse.class);
			response = aggregationResults.getMappedResults();
			if (latitude != null && longitude != null) {
				for (SearchRequestToPharmacyResponse pharmacyResponse : response) {
					if (latitude != null && longitude != null
							&& pharmacyResponse.getLocale().getAddress().getLatitude() != null
							&& pharmacyResponse.getLocale().getAddress().getLongitude() != null) {
						pharmacyResponse.setDistance(DPDoctorUtils.distance(latitude, longitude,
								pharmacyResponse.getLocale().getAddress().getLatitude(),
								pharmacyResponse.getLocale().getAddress().getLongitude(), "K"));
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error while getting locales " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting locale List " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Integer getPharmacyListCountbyOrderHistory(String uniqueRequestId, String replyType) {
		Integer response = 0;
		// String searchTerm = null;
		try {
			response = searchRequestToPharmacyRepository.getCountByUniqueRequestId(uniqueRequestId, replyType);
		} catch (Exception e) {
			logger.error("Error while getting locales " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting locale List " + e.getMessage());
		}
		return response;
	}

	@Transactional
	@Override
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
									.append("userIds", new BasicDBObject("$push", "$_id")).append("mobileNumber",
											new BasicDBObject("$first", "$mobileNumber")))));

			PatientNumberAndUserIds user = mongoTemplate
					.aggregate(aggregation, UserCollection.class, PatientNumberAndUserIds.class)
					.getUniqueMappedResult();

			DateTime dateTime = new DateTime().minusHours(24);
			Date date = dateTime.toDate();
			criteria.and("createdTime").gt(date);
			criteria.and("orders").size(0).and("response.replyType").is("YES").and("userId").in(user.getUserIds());

			aggregation = Aggregation
					.newAggregation(
							Aggregation.lookup("search_request_to_pharmacy_cl", "uniqueRequestId", "uniqueRequestId",
									"response"),
							Aggregation.unwind("response"),
							Aggregation.lookup("order_drug_cl", "uniqueRequestId", "uniqueRequestId", "orders"),
							Aggregation.match(criteria),
							new CustomAggregationOperation(new Document("$group",
									new BasicDBObject("_id", new BasicDBObject("uniqueRequestId", "$uniqueRequestId"))
											.append("uniqueRequestId",
													new BasicDBObject("$first", "$uniqueRequestId")))));

			countfor24Hour = mongoTemplate
					.aggregate(aggregation, SearchRequestFromUserCollection.class, SearchRequestFromUserResponse.class)
					.getMappedResults().size();

			criteria = new Criteria();
			dateTime = new DateTime().minusHours(1);
			date = dateTime.toDate();
			criteria.and("createdTime").gt(date);

			criteria.and("orders").size(0).and("response.replyType").is("YES").and("userId").in(user.getUserIds());

			aggregation = Aggregation
					.newAggregation(
							Aggregation.lookup("search_request_to_pharmacy_cl", "uniqueRequestId", "uniqueRequestId",
									"response"),
							Aggregation.unwind("response"),
							Aggregation.lookup("order_drug_cl", "uniqueRequestId", "uniqueRequestId", "orders"),
							Aggregation.match(criteria),
							new CustomAggregationOperation(new Document("$group",
									new BasicDBObject("_id", new BasicDBObject("uniqueRequestId", "$uniqueRequestId"))
											.append("uniqueRequestId",
													new BasicDBObject("$first", "$uniqueRequestId")))));

			countforHour = mongoTemplate
					.aggregate(aggregation, SearchRequestFromUserCollection.class, SearchRequestFromUserResponse.class)
					.getMappedResults().size();
			response.setUserIds(user.getUserIds());
			response.setNoOfAttemptIn24Hour(countfor24Hour);
			response.setNoOfAttemptInHour(countforHour);

		} catch (Exception e) {
			logger.error("Error while count user Fake Request " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting count user Fake Request " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<OrderDrugsResponse> getPatientOrders(String userId, long page, int size, String updatedTime) {
		List<OrderDrugsResponse> response = null;
		try {
			Long updatedTImeLong = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("userId").is(new ObjectId(userId)).and("updatedTime")
					.gte(new Date(updatedTImeLong));
			Aggregation aggregation = null;
			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("localeId", "$localeId").append("userId", "$userId")
							.append("uniqueRequestId", "$uniqueRequestId")
							.append("uniqueResponseId", "$uniqueResponseId").append("wayOfOrder", "$wayOfOrder")
							.append("pickUpTime", "$pickUpTime").append("pickUpDay", "$pickUpDay")
							.append("pickUpDate", "$pickUpDate").append("pickUpAddress", "$pickUpAddress")
							.append("discount", "$searchRequestToPharmacy.discount")
							.append("discountedPrice", "$searchRequestToPharmacy.discountedPrice")
							.append("realPrice", "$searchRequestToPharmacy.realPrice")
							.append("prescriptionRequest", "$searchRequestFromUser.prescriptionRequest")
							.append("localeName", "$locale.localeName").append("localeAddress", "$locale.address")
							.append("createdTime", "$createdTime").append("updatedTime", "$updatedTime")
							.append("isCancelled", "$isCancelled")));

			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("id", "$_id").append("localeId", new BasicDBObject("$first", "$localeId"))
							.append("userId", new BasicDBObject("$first", "$userId"))
							.append("uniqueRequestId", new BasicDBObject("$first", "$uniqueRequestId"))
							.append("uniqueResponseId", new BasicDBObject("$first", "$uniqueResponseId"))
							.append("wayOfOrder", new BasicDBObject("$first", "$wayOfOrder"))
							.append("pickUpTime", new BasicDBObject("$first", "$pickUpTime"))
							.append("pickUpDay", new BasicDBObject("$first", "$pickUpDay"))
							.append("pickUpDate", new BasicDBObject("$first", "$pickUpDate"))
							.append("pickUpAddress", new BasicDBObject("$first", "$pickUpAddress"))
							.append("discount", new BasicDBObject("$first", "$discount"))
							.append("discountedPrice", new BasicDBObject("$first", "$discountedPrice"))
							.append("realPrice", new BasicDBObject("$first", "$realPrice"))
							.append("prescriptionRequest", new BasicDBObject("$first", "$prescriptionRequest"))
							.append("localeName", new BasicDBObject("$first", "$localeName"))
							.append("localeAddress", new BasicDBObject("$first", "$localeAddress"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("isCancelled", new BasicDBObject("$first", "$isCancelled"))));

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("search_request_from_user_cl", "uniqueRequestId", "uniqueRequestId",
								"searchRequestFromUser"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$searchRequestFromUser").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.lookup("search_request_to_pharmacy_cl", "uniqueRequestId", "uniqueRequestId",
								"searchRequestToPharmacy"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$searchRequestToPharmacy")
										.append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("locale_cl", "localeId", "_id", "locale"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$locale").append("preserveNullAndEmptyArrays", true))),
						project, group, Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("search_request_from_user_cl", "uniqueRequestId", "uniqueRequestId",
								"searchRequestFromUser"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$searchRequestFromUser").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.lookup("search_request_to_pharmacy_cl", "uniqueRequestId", "uniqueRequestId",
								"searchRequestToPharmacy"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$searchRequestToPharmacy")
										.append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("locale_cl", "localeId", "_id", "locale"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$locale").append("preserveNullAndEmptyArrays", true))),
						project, group, Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<OrderDrugsResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					OrderDrugCollection.class, OrderDrugsResponse.class);
			response = aggregationResults.getMappedResults();
			if (response != null && !response.isEmpty()) {
				for (OrderDrugsResponse drugsResponse : response) {
					Address localeAddress = drugsResponse.getLocaleAddress();
					if (localeAddress != null) {
						String localeFormattedAddress = (!DPDoctorUtils.anyStringEmpty(localeAddress.getStreetAddress())
								? localeAddress.getStreetAddress() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(localeAddress.getLandmarkDetails())
										? localeAddress.getLandmarkDetails() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(localeAddress.getLocality())
										? localeAddress.getLocality() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(localeAddress.getCity())
										? localeAddress.getCity() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(localeAddress.getState())
										? localeAddress.getState() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(localeAddress.getCountry())
										? localeAddress.getCountry() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(localeAddress.getPostalCode())
										? localeAddress.getPostalCode() : "");

						if (localeFormattedAddress.charAt(localeFormattedAddress.length() - 2) == ',') {
							localeFormattedAddress = localeFormattedAddress.substring(0,
									localeFormattedAddress.length() - 2);
						}
						drugsResponse.setLocaleFormattedAddress(localeFormattedAddress);
					}
					Address pickUpAddress = (drugsResponse.getPickUpAddress() != null
							&& drugsResponse.getPickUpAddress().getAddress() != null)
									? drugsResponse.getPickUpAddress().getAddress() : null;
					if (pickUpAddress != null) {
						String pickUpFormattedAddress = (!DPDoctorUtils.anyStringEmpty(pickUpAddress.getStreetAddress())
								? pickUpAddress.getStreetAddress() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(pickUpAddress.getLandmarkDetails())
										? pickUpAddress.getLandmarkDetails() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(pickUpAddress.getLocality())
										? pickUpAddress.getLocality() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(pickUpAddress.getCity())
										? pickUpAddress.getCity() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(pickUpAddress.getState())
										? pickUpAddress.getState() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(pickUpAddress.getCountry())
										? pickUpAddress.getCountry() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(pickUpAddress.getPostalCode())
										? pickUpAddress.getPostalCode() : "");

						if (!DPDoctorUtils.anyStringEmpty(pickUpFormattedAddress)) {
							if (pickUpFormattedAddress.charAt(pickUpFormattedAddress.length() - 2) == ',') {
								pickUpFormattedAddress = pickUpFormattedAddress.substring(0,
										pickUpFormattedAddress.length() - 2);
							}
							drugsResponse.setPickUpFormattedAddress(pickUpFormattedAddress);
						}

					}
				}
			}
		} catch (Exception e) {
			logger.error("Error while getting my orders " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting my orders " + e.getMessage());
		}
		return response;
	}

	@Override
	public List<SearchRequestFromUserResponse> getPatientRequests(String userId, long page, int size,
			String updatedTime) {
		List<SearchRequestFromUserResponse> response = null;
		try {
			Long updatedTImeLong = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("userId").is(new ObjectId(userId)).and("updatedTime")
					.gte(new Date(updatedTImeLong));
			Aggregation aggregation = null;
			CustomAggregationOperation project = new CustomAggregationOperation(
					new Document("$project",
							new BasicDBObject("localeId", "$localeId").append("userId", "$userId")
									.append("uniqueRequestId", "$uniqueRequestId")
									.append("prescriptionRequest", "$prescriptionRequest")
									.append("pharmacyName", "$pharmacyName").append("location", "$location")
									.append("latitude", "$latitude").append("longitude", "$longitude")
									.append("isCancelled", "$isCancelled").append("localeName", "$locale.localeName")
									.append("isTwentyFourSevenOpen", "$locale.isTwentyFourSevenOpen")
									.append("pharmacyType", "$locale.pharmacyType")
									.append("noOfLocaleRecommendation", "$locale.noOfLocaleRecommendation")
									.append("isHomeDeliveryAvailable", "$locale.isHomeDeliveryAvailable")
									.append("homeDeliveryRadius", "$locale.homeDeliveryRadius")
									.append("localeAddress", "$locale.address")
									.append("paymentInfo", "$locale.paymentInfo")
									.append("paymentInfos",
											"$locale.paymentInfos")
									.append("isOrdered",
											new BasicDBObject("$cond",
													new BasicDBObject("if",
															new BasicDBObject("$gt",
																	Arrays.asList(new BasicDBObject("$size", "$orders"),
																			0))).append("then", true).append("else",
																					false)))
									.append("createdTime", "$createdTime").append("updatedTime", "$updatedTime")));

			CustomAggregationOperation group = new CustomAggregationOperation(
					new Document("$group",
							new BasicDBObject("id", "$_id").append("localeId", new BasicDBObject("$first", "$localeId"))
									.append("userId", new BasicDBObject("$first", "$userId"))
									.append("uniqueRequestId", new BasicDBObject("$first", "$uniqueRequestId"))
									.append("prescriptionRequest", new BasicDBObject("$first", "$prescriptionRequest"))
									.append("pharmacyName", new BasicDBObject("$first", "$pharmacyName"))
									.append("location", new BasicDBObject("$first", "$location"))
									.append("latitude", new BasicDBObject("$first", "$latitude"))
									.append("longitude", new BasicDBObject("$first", "$longitude"))
									.append("localeName", new BasicDBObject("$first", "$localeName"))
									.append("isTwentyFourSevenOpen",
											new BasicDBObject("$first", "$isTwentyFourSevenOpen"))
									.append("pharmacyType", new BasicDBObject("$first", "$pharmacyType"))
									.append("noOfLocaleRecommendation",
											new BasicDBObject("$first", "$noOfLocaleRecommendation"))
									.append("isHomeDeliveryAvailable",
											new BasicDBObject("$first", "$isHomeDeliveryAvailable"))
									.append("homeDeliveryRadius", new BasicDBObject("$first", "$homeDeliveryRadius"))
									.append("localeAddress", new BasicDBObject("$first", "$localeAddress"))
									.append("paymentInfo", new BasicDBObject("$first", "$paymentInfo"))
									.append("paymentInfos", new BasicDBObject("$first", "$paymentInfos"))
									.append("isOrdered", new BasicDBObject("$first", "$isOrdered"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))));

			if (size > 0)
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("order_drug_cl", "uniqueRequestId", "uniqueRequestId", "orders"),
								Aggregation.lookup("locale_cl", "localeId", "localeId", "locale"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$locale").append("preserveNullAndEmptyArrays",
												true))),
								project, group, Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
								Aggregation.skip((page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("order_drug_cl", "uniqueRequestId", "uniqueRequestId", "orders"),
								Aggregation.lookup("locale_cl", "localeId", "localeId", "locale"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$locale").append("preserveNullAndEmptyArrays",
												true))),
								project, group, Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<SearchRequestFromUserResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					SearchRequestFromUserCollection.class, SearchRequestFromUserResponse.class);

			response = aggregationResults.getMappedResults();
			if (response != null && !response.isEmpty()) {
				for (SearchRequestFromUserResponse requestFromUserResponse : response) {
					requestFromUserResponse.setCountForYes(getPharmacyListCountbyOrderHistory(
							requestFromUserResponse.getUniqueRequestId(), ReplyType.YES.toString()));
					requestFromUserResponse.setCountForNo(getPharmacyListCountbyOrderHistory(
							requestFromUserResponse.getUniqueRequestId(), ReplyType.NO.toString()));

					Address address = requestFromUserResponse.getLocaleAddress();
					if (address != null) {
						String formattedAddress = (!DPDoctorUtils.anyStringEmpty(address.getStreetAddress())
								? address.getStreetAddress() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(address.getLandmarkDetails())
										? address.getLandmarkDetails() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(address.getLocality()) ? address.getLocality() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(address.getCity()) ? address.getCity() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(address.getState()) ? address.getState() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(address.getCountry()) ? address.getCountry() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(address.getPostalCode()) ? address.getPostalCode()
										: "");

						if (formattedAddress.charAt(formattedAddress.length() - 2) == ',') {
							formattedAddress = formattedAddress.substring(0, formattedAddress.length() - 2);
						}
						requestFromUserResponse.setLocaleFormattedAddress(formattedAddress);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error while getting my requests " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting my requests ");
		}
		return response;
	}

	@Override
	public OrderDrugsRequest cancelOrderDrug(String orderId, String userId) {
		OrderDrugsRequest response = null;
		try {
			OrderDrugCollection orderDrugCollection = orderDrugRepository.findByIdAndUserId(new ObjectId(orderId),
					new ObjectId(userId));
			if (orderDrugCollection == null)
				throw new BusinessException(ServiceError.InvalidInput, "Invalid orderId and userId");

			orderDrugCollection.setIsCancelled(true);
			orderDrugCollection.setUpdatedTime(new Date());
			orderDrugCollection = orderDrugRepository.save(orderDrugCollection);
			response = new OrderDrugsRequest();
			BeanUtil.map(orderDrugCollection, response);
		} catch (Exception e) {
			logger.error("Error while cancelling order drugs " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while cancelling order drugs");
		}
		return response;
	}

}