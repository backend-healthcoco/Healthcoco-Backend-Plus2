package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DentalImaging;
import com.dpdocter.beans.DentalImagingRequest;
import com.dpdocter.collections.DentalImagingCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DentalImagingRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.response.DentalLabPickupLookupResponse;
import com.dpdocter.services.DentalImagingService;

import common.util.web.DPDoctorUtils;

@Service
public class DentalImagingServiceImpl implements DentalImagingService{

	@Autowired
	LocationRepository locationRepository;
	
	@Autowired
	DentalImagingRepository dentalImagingRepository;
	
	@Autowired
	MongoTemplate mongoTemplate;
	private static Logger logger = Logger.getLogger(DentalImagingServiceImpl.class.getName());
	
	@Override
	@Transactional
	public DentalImaging addEditDentalImagingRequest(DentalImagingRequest request) {
		DentalImaging response = null;
		DentalImagingCollection dentalImagingCollection = null;
		String requestId = null;

		try {
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
			
			if (request.getId() != null) {
				dentalImagingCollection = dentalImagingRepository.findOne(new ObjectId(request.getId()));
				if (dentalImagingCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Record not found");
				}
				BeanUtil.map(request, dentalImagingCollection);
				

				dentalImagingCollection.setUpdatedTime(new Date());
				dentalImagingCollection = dentalImagingRepository.save(dentalImagingCollection);
			} else {
				requestId = UniqueIdInitial.DENTAL_IMAGING.getInitial() + DPDoctorUtils.generateRandomId();
				dentalImagingCollection = new DentalImagingCollection();
				BeanUtil.map(request, dentalImagingCollection);
				dentalImagingCollection.setRequestId(requestId);
				dentalImagingCollection.setCreatedTime(new Date());
				dentalImagingCollection.setUpdatedTime(new Date());
			

			
			dentalImagingCollection = dentalImagingRepository.save(dentalImagingCollection);
			}
			response = new DentalImaging();
			BeanUtil.map(dentalImagingCollection, response);
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}
	
	
	
	@Override
	@Transactional

	public List<DentalImaging> getRequests(String locationId, String hospitalId, String doctorId, Long from, Long to,
			String searchTerm,int size, int page) {

		List<DentalImaging> response = null;
		List<DentalLabPickupLookupResponse> lookupResponses = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (to != null) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else {
				criteria.and("updatedTime").gte(new Date(from));
			}
/*
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
*/
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

			
			/*if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
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
*/
			/* (SEVEN) */
			if (size > 0)
				aggregation = Aggregation.newAggregation(/*Aggregation.unwind("dentalWorksSamples"),*/
						// Aggregation.unwind("dentalWorksSamples.dentalStagesForDoctor"),
					/*	Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("collection_boy_cl", "collectionBoyId", "_id", "collectionBoy"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$collectionBoy").append("preserveNullAndEmptyArrays",
										true))),*/
						Aggregation.match(criteria), 
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(/*Aggregation.unwind("dentalWorksSamples"),*/
						// Aggregation.unwind("dentalWorksSamples.dentalStagesForDoctor"),
						/*Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("collection_boy_cl", "collectionBoyId", "_id", "collectionBoy"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$collectionBoy").append("preserveNullAndEmptyArrays",
										true))),*/
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			// System.out.println(aggregation);
			AggregationResults<DentalImaging> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalImagingCollection.class, DentalImaging.class);
			response = aggregationResults.getMappedResults();

			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	
}
