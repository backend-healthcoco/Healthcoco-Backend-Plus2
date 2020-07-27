package com.dpdocter.services.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.TransactionalSmsReport;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.TransactionSmsServices;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class TransactionSmsServicesImpl implements TransactionSmsServices {

	private static Logger logger = LogManager.getLogger(TransactionSmsServicesImpl.class.getName());

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<TransactionalSmsReport> getSmsReport(int page, int size, String doctorId, String locationId) {
		List<TransactionalSmsReport> response = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				ObjectId doctorObjectId = new ObjectId(doctorId);
				criteria.and("doctorId").is(doctorObjectId);
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				ObjectId locationObjectId = new ObjectId(locationId);
				criteria.and("locationId").is(locationObjectId);
			}

			criteria.and("type").ne("BULK_SMS");
//			criteria.and("type").is("BULK_SMS");

			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id")

							.append("doctorId", "$doctorId").append("locationId", "$locationId")
							.append("smsDetails.sms.smsText", "$smsDetail.sms.smsText")
							.append("smsDetails.deliveryStatus", "$smsDetail.deliveryStatus")
							.append("smsDetails.sentTime", "$smsDetail.sentTime").append("type", "$type")
							.append("responseId", "$responseId").append("delivered", "$delivered")
							.append("undelivered", "$undelivered").append("totalCreditsSpent", "$totalCreditSpent")));
//			
			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("smsDetails", new BasicDBObject("$first", "$smsDetails"))
							.append("type", new BasicDBObject("$first", "$type"))
							.append("responseId", new BasicDBObject("$first", "$responseId"))
							.append("delivered", new BasicDBObject("$first", "$delivered"))
							.append("undelivered", new BasicDBObject("$first", "$undelivered"))
							.append("totalCreditsSpent", new BasicDBObject("$first", "$totalCreditsSpent"))));

//			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(

						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}

			System.out.println("Aggregation:" + aggregation);
			response = mongoTemplate.aggregate(aggregation, SMSTrackDetail.class, TransactionalSmsReport.class)
					.getMappedResults();


		} catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Bulksms package" + e.getMessage());
		}
		return response;
	}

}
