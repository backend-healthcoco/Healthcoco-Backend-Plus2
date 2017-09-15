package com.dpdocter.scheduler;

import java.util.Date;

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
import com.dpdocter.collections.BlockUserCollection;
import com.dpdocter.collections.SearchRequestFromUserCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.BlockUserRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.SearchRequestFromUserResponse;
import com.dpdocter.response.UserFakeRequestDetailResponse;
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

	@Async
	public void checkFakeRequestCount(String userId, BlockUserCollection blockUserCollection)
			throws InterruptedException {
		System.out.println("task start");
		System.out.println("Execute method asynchronously. " + Thread.currentThread().getName());
		UserFakeRequestDetailResponse detailResponse = getUserFakeRequestCount(userId);
		if (detailResponse.getNoOfAttemptInHour() >= Integer.parseInt(requestLimitForday)
				|| detailResponse.getNoOfAttemptIn24Hour() >= Integer.parseInt(requestLimitForday)) {

			if (blockUserCollection != null) {
				if (detailResponse.getNoOfAttemptIn24Hour() >= Integer.parseInt(requestLimitForday)) {
					blockUserCollection.setIsForDay(true);

				} else {
					blockUserCollection.setIsForHour(true);
				}

			} else {
				blockUserCollection = new BlockUserCollection();
				if (detailResponse.getNoOfAttemptIn24Hour() >= Integer.parseInt(requestLimitForday)) {
					blockUserCollection.setIsForDay(true);

				} else {
					blockUserCollection.setIsForHour(true);
				}
				blockUserCollection.setCreatedTime(new Date());
			}
			blockUserCollection.setDiscarded(false);
			blockUserCollection.setUserIds(detailResponse.getUserIds());
			blockUserCollection.setUpdatedTime(new Date());
			System.out.println("Task completed");
			blockUserCollection = blockUserRepository.save(blockUserCollection);

		}

	}

	public UserFakeRequestDetailResponse getUserFakeRequestCount(String userId) {
		UserFakeRequestDetailResponse response = new UserFakeRequestDetailResponse();
		try {

			Integer countfor24Hour = 0;
			Integer countforHour = 0;
			Criteria criteria = new Criteria();
			UserCollection userCollection = userRepository.findOne(new ObjectId(userId));
			if (userCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid patient Id");
			}

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria("userName").regex("^" + userCollection.getMobileNumber(), "i")
							.and("userState").is("USERSTATECOMPLETE")),
					new CustomAggregationOperation(new BasicDBObject("$group",
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
							new CustomAggregationOperation(new BasicDBObject("$group",
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
							new CustomAggregationOperation(new BasicDBObject("$group",
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

			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting count user Fake Request " + e.getMessage());

		}
		return response;
	}

}
