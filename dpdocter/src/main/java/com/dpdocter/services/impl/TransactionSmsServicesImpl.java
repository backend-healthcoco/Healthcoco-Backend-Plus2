package com.dpdocter.services.impl;


import java.util.Date;
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
import com.dpdocter.collections.MessageCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.MessageResponse;
import com.dpdocter.services.TransactionSmsServices;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class TransactionSmsServicesImpl implements TransactionSmsServices {

	private static Logger logger = LogManager.getLogger(TransactionSmsServicesImpl.class.getName());

	@Autowired
	private MongoTemplate mongoTemplate;
	
//	@Override
//	public List<TransactionalSmsReport> getSmsReport(int page, int size, String doctorId, String locationId,String fromDate, String toDate) {
//		List<TransactionalSmsReport> response = null;
//		try {
//			
//			Date from = null;
//			Date to = null;
//			Criteria criteria = new Criteria();
//
//			long date = 0l;
//			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
//				from = new Date(Long.parseLong(fromDate));
//				to = new Date(Long.parseLong(toDate));
//				criteria.and("smsDetails.sentTime").gte(from).lte(to);
//			} 
//
//			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
//				ObjectId doctorObjectId = new ObjectId(doctorId);
//				criteria.and("doctorId").is(doctorObjectId);
//			}
//
//			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
//				ObjectId locationObjectId = new ObjectId(locationId);
//				criteria.and("locationId").is(locationObjectId);
//			}
//
//			criteria.and("type").ne("BULK_SMS");
////			criteria.and("type").is("BULK_SMS");
//
//			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
//					new BasicDBObject("_id", "$_id")
//
//							.append("doctorId", "$doctorId").append("locationId", "$locationId")
//							.append("smsDetails.sms.smsText", "$smsDetail.sms.smsText")
//							.append("smsDetails.deliveryStatus", "$smsDetail.deliveryStatus")
//							.append("smsDetails.sentTime", "$smsDetail.sentTime").append("type", "$type")
//							.append("responseId", "$responseId").append("delivered", "$delivered")
//							.append("undelivered", "$undelivered").append("totalCreditsSpent", "$totalCreditSpent")));
////			
//			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
//					new BasicDBObject("_id", "$_id").append("doctorId", new BasicDBObject("$first", "$doctorId"))
//							.append("locationId", new BasicDBObject("$first", "$locationId"))
//							.append("smsDetails", new BasicDBObject("$first", "$smsDetails"))
//							.append("type", new BasicDBObject("$first", "$type"))
//							.append("responseId", new BasicDBObject("$first", "$responseId"))
//							.append("delivered", new BasicDBObject("$first", "$delivered"))
//							.append("undelivered", new BasicDBObject("$first", "$undelivered"))
//							.append("totalCreditsSpent", new BasicDBObject("$first", "$totalCreditsSpent"))));
//
////			
//			Aggregation aggregation = null;
//			if (size > 0) {
//				aggregation = Aggregation.newAggregation(
//
//						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
//
//						Aggregation.skip((page) * size), Aggregation.limit(size));
//
//			} else {
//				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//
//						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
//
//			}
//
//			System.out.println("Aggregation:" + aggregation);
//			response = mongoTemplate.aggregate(aggregation, SMSTrackDetail.class, TransactionalSmsReport.class)
//					.getMappedResults();
//
//
//		} catch (BusinessException e) {
//			e.printStackTrace();
//			throw new BusinessException(ServiceError.Unknown, "Error while getting Bulksms package" + e.getMessage());
//		}
//		return response;
//	}
	
	@Override
	public List<MessageResponse> getSmsReport(int page, int size, String doctorId, String locationId,String fromDate, String toDate) {
		List<MessageResponse> response=null;
		try {
	Criteria criteria = new Criteria();
			
			if(!DPDoctorUtils.anyStringEmpty(doctorId))
			{
				ObjectId doctorObjectId=new ObjectId(doctorId);
				criteria.and("doctorId").is(doctorObjectId);
			}
			
			if(!DPDoctorUtils.anyStringEmpty(locationId))
			{
				ObjectId locationObjectId=new ObjectId(locationId);
				criteria.and("locationId").is(locationObjectId);
			}
			
			
				criteria.and("messageType").ne("BULK_SMS");
			
				Date from = null;
				Date to = null;
				
	
				long date = 0l;
				if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
					from = new Date(Long.parseLong(fromDate));
					to = new Date(Long.parseLong(toDate));
					criteria.and("createdTime").gte(from).lte(to);
				} 
			
	
			
			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id")
										
					.append("doctorId", "$doctorId")
					.append("locationId", "$locationId")					
					.append("smsDetails.sms.smsText", "$smsDetail.sms.smsText")
					.append("smsDetails.deliveryStatus", "$smsDetail.deliveryStatus")
					.append("smsDetails.sentTime", "$smsDetail.sentTime")
					.append("type", "$type")					
					.append("responseId", "$responseId")
					.append("delivered", "$delivered")
					.append("undelivered", "$undelivered")	
					.append("totalCreditsSpent", "$totalCreditSpent")));
//			
			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id")
					.append("doctorId", new BasicDBObject("$first", "$doctorId"))
					.append("locationId", new BasicDBObject("$first", "$locationId"))
					.append("smsDetails", new BasicDBObject("$first", "$smsDetails"))
						.append("type", new BasicDBObject("$first", "$type"))
						.append("responseId", new BasicDBObject("$first", "$responseId"))
						.append("delivered", new BasicDBObject("$first", "$delivered"))
						.append("undelivered", new BasicDBObject("$first", "$undelivered"))
						.append("totalCreditsSpent", new BasicDBObject("$first", "$totalCreditsSpent"))));

//			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
//				criteria = criteria.orOperator(new Criteria("smsPackage.packageName").regex("^" + searchTerm, "i"),
//						new Criteria("smsPackage.packageName").regex("^" + searchTerm));
			
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						
						Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
				
				} else {
					aggregation = Aggregation.newAggregation( 
							Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
				}
			
				response = mongoTemplate.aggregate(aggregation, MessageCollection.class, MessageResponse.class).getMappedResults();

				
		
//				CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
//						new BasicDBObject("_id", "$_id")
//						.append("smsDetails", new BasicDBObject("$addToSet", "$smsDetails"))
//						));

				
	//			for(BulkSmsReport credit:response)
	//			{
	//				Long total=(long) credit.getSmsDetails().size();
				//
//					Integer totalLength=160; 
//					String message=null;
//					  Integer messageLength=null;
//					if(credit.getSmsDetails().equals(null))
//						messageLength=0;
//					else {
//					 message=credit.getSmsDetails().get(1).getSms().getSmsText();
//					messageLength=message.length();
//					}
//					  System.out.println("messageLength:"+messageLength);
//					  long credits=(messageLength/totalLength);
//					  
//					  long temp=messageLength%totalLength;
//					  if(credits==0 || temp!=0) 
//					  credits=credits+1;
//					
//					  long subCredits=credits*(total);
					
//					Long count= mongoTemplate.count(new Query(new Criteria("smsDetails.deliveryStatus").is("DELIVERED").andOperator(criteria)),SMSTrackDetail.class);
//					credit.setDelivered(count);
//					credit.setUndelivered(total-count);
//					
//				}
//			
			
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
		}
		return response;
	}

}
