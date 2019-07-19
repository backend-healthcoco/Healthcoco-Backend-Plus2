package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.RankingCount;
import com.dpdocter.beans.RankingCountParametersWithValueInPercentage;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.RankingCountCollection;
import com.dpdocter.collections.RecommendationsCollection;
import com.dpdocter.collections.SearchRequestFromUserCollection;
import com.dpdocter.collections.SearchRequestToPharmacyCollection;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESUserLocaleRepository;
import com.dpdocter.enums.DoctorExperienceUnit;
import com.dpdocter.enums.LocaleType;
import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.RankingCountParatmeter;
import com.dpdocter.enums.ReplyType;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.LocaleRepository;
import com.dpdocter.repository.RankingCountRepository;
import com.dpdocter.response.DoctorWithRankingDetailResponse;
import com.dpdocter.response.PharmacyWithRankingDetailResponse;
import com.dpdocter.services.RankingAlgorithmsServices;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class RankingAlgorithmsServiceImpl implements RankingAlgorithmsServices{

	private static Logger logger = Logger.getLogger(RankingAlgorithmsServiceImpl.class.getName());
	
	//For Doctors
//	private double doctorExperienceWeightageInPercentage = 0.2;
	
	private double noOfPatientsOfDoctorWeightageInPercentage = 0.2;
	
	private double noOfRxByDoctorWeightageInPercentage = 0.2;
	
	private double noOfLikesToDoctorWeightageInPercentage = 0.1;
	
//	private double feedbackForDoctorWeightageInPercentage = 0.1;
//	
//	private double doctorAppointmentsWeightageInPercentage = 0.1;
//	
//	private double doctorRecomdationsWeightageInPercentage = 0.1;
//	
//	
//	//For Pharmacies
//	private double genericMedicineAvailabilityInPharmacy = 0.1;
//	
	private double noOfResponseFromPharmacyWeightageInPercentage = 0.1;
	
	private double noOfRequestToPharmacyWeightageInPercentage = 0.1;
	
	private double noOfLikesToPharmacyWeightageInPercentage = 0.1;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private RankingCountRepository rankingCountRepository;
	
	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;
	
	@Autowired
	private ESDoctorRepository esDoctorRepository;
	
	@Autowired
	private LocaleRepository localeRepository;
	
	@Autowired
	private ESUserLocaleRepository esUserLocaleRepository;
	
	@Scheduled(cron = "0 0/30 2 * * MON,WED,FRI", zone = "IST")
	@Transactional
	@Override
	public void calculateRankingOfResources() {
		try {
			rankingAlgoForDoctors();
			rankingAlgoForPharmacies();			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	private void rankingAlgoForPharmacies() {
		try {
			
			long directRequestCount = mongoTemplate.count(new Query(new Criteria("localeId").exists(true)), SearchRequestFromUserCollection.class);
			long indirectRequestCount = mongoTemplate.count(new Query(new Criteria()), SearchRequestToPharmacyCollection.class);
			long requestCount = directRequestCount + indirectRequestCount;
			long responseCount = mongoTemplate.count(new Query(new Criteria().orOperator(new Criteria("replyType").ne(null), new Criteria("replyType").exists(true))), SearchRequestToPharmacyCollection.class);
			long likesCount = mongoTemplate.count(new Query(new Criteria("discarded").is(false).and("doctorId").is(null)), RecommendationsCollection.class);
			
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("localeType").is(LocaleType.PHARMACY.getType()).and("isActivate").is(true).and("isVerified").is(true)),
					Aggregation.lookup("search_request_from_user_cl", "id", "localeId", "directSearchRequestToPharmacy"),
					
					Aggregation.lookup("search_request_to_pharmacy_cl", "_id", "localeId", "indirectSearchRequestToPharmacy"),
					
					Aggregation.lookup("search_request_to_pharmacy_cl", "_id", "localeId", "responseFromPharmacy"),
					new CustomAggregationOperation(new Document("$unwind", new BasicDBObject("path", "$responseFromPharmacy").append("preserveNullAndEmptyArrays", true))),
					
					new CustomAggregationOperation(new Document("$project",
						    new BasicDBObject("genericMedicineCount", new BasicDBObject(
						        "$cond", new BasicDBObject(
						          "if", new BasicDBObject("$eq", Arrays.asList("$isGenericMedicineAvailable", true)))
						        .append("then", 0.1)
						        .append("else", 0)))
						    
						    .append("localeId", "$_id")
						    .append("resourceName", "$localeName")
						    .append("directRequestCount", new BasicDBObject("$size", "$directSearchRequestToPharmacy"))
						    .append("indirectRequestCount", new BasicDBObject("$size", "$indirectSearchRequestToPharmacy"))
						    .append("responseCount", new BasicDBObject(
							        "$cond", new BasicDBObject(
									          "if", new BasicDBObject("$eq", Arrays.asList("$responseFromPharmacy.replyType", ReplyType.YES.name())))
									        .append("then", 1)
									        .append("else", 0))))),
										
					new CustomAggregationOperation(new Document("$group",
						    new BasicDBObject("_id", new BasicDBObject("localeId","$localeId"))
						    .append("localeId", new BasicDBObject("$first","$localeId"))
						    .append("genericMedicineCount", new BasicDBObject("$first","$genericMedicineCount"))
						    .append("resourceName", new BasicDBObject("$first","$resourceName"))
						    .append("directRequestCount", new BasicDBObject("$first","$directRequestCount"))
						    .append("indirectRequestCount", new BasicDBObject("$first","$indirectRequestCount"))
						    .append("responseCount", new BasicDBObject("$sum","$responseCount")))),
					
					Aggregation.lookup("recommendation_cl", "localeId", "localeId", "likes"),
					new CustomAggregationOperation(new Document("$unwind", new BasicDBObject("path", "$likes").append("preserveNullAndEmptyArrays", true))),
					
					new CustomAggregationOperation(new Document("$project",
						    new BasicDBObject("genericMedicineCount", "$genericMedicineCount")
						    .append("localeId", "$localeId")
						    .append("resourceName", "$resourceName")
						    .append("directRequestCount", "$directRequestCount")
						    .append("indirectRequestCount", "$indirectRequestCount")
						    .append("responseCount", "$responseCount")
						    .append("noOfLikes", new BasicDBObject(
							        "$cond", new BasicDBObject(
									          "if", new BasicDBObject("$eq", Arrays.asList("$likes.discarded", false)))
									        .append("then", 1)
									        .append("else", 0))))),
										
					new CustomAggregationOperation(new Document("$group",
						    new BasicDBObject("_id", new BasicDBObject("localeId","$localeId"))
						    .append("localeId", new BasicDBObject("$first","$localeId"))
						    .append("genericMedicineCount", new BasicDBObject("$first","$genericMedicineCount"))
						    .append("resourceName", new BasicDBObject("$first","$resourceName"))
						    .append("directRequestCount", new BasicDBObject("$first","$directRequestCount"))
						    .append("indirectRequestCount", new BasicDBObject("$first","$indirectRequestCount"))
						    .append("responseCount", new BasicDBObject("$first","$responseCount"))
						    .append("noOfLikes", new BasicDBObject("$sum","$noOfLikes")))),
					
					new CustomAggregationOperation(new Document("$project",
						    new BasicDBObject("localeId", "$localeId")
						    .append("genericMedicineCount", "$genericMedicineCount")
						    .append("resourceName", "$resourceName")
						    .append("requestCount", new BasicDBObject("$multiply", Arrays.asList(new BasicDBObject("$divide", Arrays.asList(new BasicDBObject("$add", Arrays.asList("$directRequestCount","$indirectRequestCount")), requestCount)), noOfRequestToPharmacyWeightageInPercentage)))
						    .append("responseCount", new BasicDBObject("$multiply", Arrays.asList(new BasicDBObject("$divide", Arrays.asList("$responseCount", responseCount)), noOfResponseFromPharmacyWeightageInPercentage)))
						    .append("noOfLikes", new BasicDBObject("$multiply", Arrays.asList(new BasicDBObject("$divide", Arrays.asList("$noOfLikes", likesCount)), noOfLikesToPharmacyWeightageInPercentage))))),
					
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("localeId","$localeId"))
						    .append("localeId", new BasicDBObject("$first","$localeId"))
						    .append("genericMedicineCount", new BasicDBObject("$first","$genericMedicineCount"))
						    .append("resourceName", new BasicDBObject("$first","$resourceName"))
						    .append("requestCount", new BasicDBObject("$first","$requestCount"))
						    .append("responseCount", new BasicDBObject("$first","$responseCount"))
						    .append("noOfLikes", new BasicDBObject("$first","$noOfLikes")))),
					
					new CustomAggregationOperation(new Document("$project",
						    new BasicDBObject("localeId", "$localeId")
						    .append("genericMedicineCount", "$genericMedicineCount")
						    .append("resourceName", "$resourceName")
						    .append("requestCount", "$requestCount")
						    .append("responseCount", "$responseCount")
						    .append("noOfLikes", "$noOfLikes")
						    .append("totalCount", new BasicDBObject("$add", Arrays.asList("$genericMedicineCount", "$requestCount", "$responseCount", "$noOfLikes"))))),
					
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("localeId","$localeId"))
							.append("genericMedicineCount", new BasicDBObject("$first","$genericMedicineCount"))
						    .append("resourceName", new BasicDBObject("$first","$resourceName"))
						    .append("requestCount", new BasicDBObject("$first","$requestCount"))
						    .append("responseCount", new BasicDBObject("$first","$responseCount"))
						    .append("noOfLikes", new BasicDBObject("$first","$noOfLikes"))
						    .append("totalCount", new BasicDBObject("$first","$totalCount")))),
					
					new CustomAggregationOperation(new Document("$sort", new BasicDBObject("totalCount", -1))));
					
			List<PharmacyWithRankingDetailResponse> pharmacyWithRankingDetailResponses = mongoTemplate.aggregate(aggregation, LocaleCollection.class, PharmacyWithRankingDetailResponse.class).getMappedResults();
			int i = 1;
			for(PharmacyWithRankingDetailResponse detailResponse : pharmacyWithRankingDetailResponses) {
				RankingCountCollection rankingCountCollection = rankingCountRepository.findByResourceIdAndLocationId(new ObjectId(detailResponse.getLocaleId()), null, Resource.PHARMACY.getType());
				if(rankingCountCollection == null) {
					rankingCountCollection = new RankingCountCollection();
					rankingCountCollection.setCreatedTime(new Date());
				}
				rankingCountCollection.setRankingCount(i++);
				rankingCountCollection.setResourceType(Resource.PHARMACY);
				rankingCountCollection.setUpdatedTime(new Date());
				rankingCountCollection.setResourceId(new ObjectId(detailResponse.getLocaleId()));
				rankingCountCollection.setTotalCountInPercentage(detailResponse.getTotalCount());
				rankingCountCollection.setResourceName(detailResponse.getResourceName());
				List<RankingCountParametersWithValueInPercentage> parameters = new ArrayList<RankingCountParametersWithValueInPercentage>();
				
				parameters.add(new RankingCountParametersWithValueInPercentage(RankingCountParatmeter.GENERIC_MEDICINE_AVAILABILITY, detailResponse.getGenericMedicineCount()));
				parameters.add(new RankingCountParametersWithValueInPercentage(RankingCountParatmeter.NUMBER_OF_LIKES, detailResponse.getNoOfLikes()));
				parameters.add(new RankingCountParametersWithValueInPercentage(RankingCountParatmeter.NUMBER_OF_REQUESTS, detailResponse.getRequestCount()));
				parameters.add(new RankingCountParametersWithValueInPercentage(RankingCountParatmeter.NUMBER_OF_RESPONSES, detailResponse.getResponseCount()));
				rankingCountCollection.setParameters(parameters);
				
				rankingCountCollection = rankingCountRepository.save(rankingCountCollection);
				
				LocaleCollection localeCollection = localeRepository.findById(rankingCountCollection.getResourceId()).orElse(null);
				localeCollection.setLocaleRankingCount(rankingCountCollection.getRankingCount());
				localeCollection.setUpdatedTime(new Date());
				localeCollection = localeRepository.save(localeCollection);
				
				ESUserLocaleDocument esUserLocaleDocument = esUserLocaleRepository.findById(detailResponse.getLocaleId()).orElse(null);
				esUserLocaleDocument.setLocaleRankingCount(localeCollection.getLocaleRankingCount());
				esUserLocaleRepository.save(esUserLocaleDocument);
			}		
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void rankingAlgoForDoctors() {
		try {
			
			long rxCount = mongoTemplate.count(new Query(new Criteria()), PrescriptionCollection.class);
			long patientCount = mongoTemplate.count(new Query(new Criteria()), PatientCollection.class);
			long likesCount = mongoTemplate.count(new Query(new Criteria("discarded").is(false)), RecommendationsCollection.class);
			
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
					Aggregation.unwind("user"),
					
					Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
					Aggregation.unwind("doctor"),
					
					Aggregation.match(new Criteria("isDoctorListed").is(true)),
					
					Aggregation.lookup("prescription_cl", "doctorId", "doctorId", "prescription"),
					new CustomAggregationOperation(new Document("$unwind", new BasicDBObject("path", "$prescription").append("preserveNullAndEmptyArrays", true))),
					
					new CustomAggregationOperation(new Document("$project",
						    new BasicDBObject("doctor.experience.experience", new BasicDBObject(
						        "$cond", new BasicDBObject(
						          "if", new BasicDBObject("$eq", Arrays.asList("$experience.period", DoctorExperienceUnit.MONTH.name())))
						        .append("then", new BasicDBObject("$divide", Arrays.asList("$doctor.experience.experience", 12)))
						        .append("else", "$doctor.experience.experience")))
						    .append("doctorId", "$doctorId")
						    .append("locationId", "$locationId")
						    .append("isActive", "$user.isActive")
						    .append("packageType", "$packageType")
						    .append("resourceName", "$user.firstName")
						    .append("rxCount", new BasicDBObject(
							        "$cond", new BasicDBObject(
									          "if", new BasicDBObject("$eq", Arrays.asList("$prescription.locationId", "$locationId")))
									        .append("then", 1)
									        .append("else", 0))))),
					
					new CustomAggregationOperation(new Document("$group",
						    new BasicDBObject("_id", new BasicDBObject("doctorId","$doctorId").append("locationId", "$locationId"))
						    .append("experienceInYear", new BasicDBObject("$first","$doctor.experience.experience"))
						    .append("doctorId", new BasicDBObject("$first","$doctorId"))
						    .append("locationId", new BasicDBObject("$first","$locationId"))
						    .append("resourceName", new BasicDBObject("$first","$resourceName"))
						    .append("isActive", new BasicDBObject("$first","$isActive"))
						    .append("packageType", new BasicDBObject("$first","$packageType"))
						    .append("rxCount", new BasicDBObject("$sum","$rxCount")))),
					
					Aggregation.lookup("patient_cl", "doctorId", "doctorId", "patient"),
					new CustomAggregationOperation(new Document("$unwind", new BasicDBObject("path", "$patient").append("preserveNullAndEmptyArrays", true))),
					
					new CustomAggregationOperation(new Document("$project",
						    new BasicDBObject("experienceInYear", new BasicDBObject(
							        "$cond", new BasicDBObject(
									          "if", new BasicDBObject("$gte", Arrays.asList("$experienceInYear", 20)))
									        .append("then", 0.2)
									        .append("else",  new BasicDBObject("$cond", new BasicDBObject(
													          "if", new BasicDBObject("$gte", Arrays.asList("$experienceInYear", 10)))
									        					  .append("then", 0.175)
									        					  .append("else", new BasicDBObject("$cond", new BasicDBObject(
									        								      "if", new BasicDBObject("$gte", Arrays.asList("$experienceInYear", 5)))
																			   .append("then", 0.15)
																			   .append("else",new BasicDBObject("$cond", new BasicDBObject(
																							  "if", new BasicDBObject("$gte", Arrays.asList("$experienceInYear", 0)))
																							  .append("then", 0.1)
																							  .append("else",0)))))))))
						    .append("doctorId", "$doctorId")
						    .append("locationId", "$locationId")
						    .append("resourceName", "$resourceName")
						    .append("pointIfAdvance", new BasicDBObject(
							        "$cond", new BasicDBObject(
									          "if", new BasicDBObject("$eq", Arrays.asList("$packageType", PackageType.ADVANCE.getType())))
									        .append("then", 20)
									        .append("else", 0)))
						    .append("pointIfActive", new BasicDBObject(
							        "$cond", new BasicDBObject(
									          "if", new BasicDBObject("$eq", Arrays.asList("$isActive", true)))
									        .append("then", 10)
									        .append("else", 0)))
						    .append("rxCount", "$rxCount")
						    .append("patientCount", new BasicDBObject(
							        "$cond", new BasicDBObject(
									          "if", new BasicDBObject("$eq", Arrays.asList("$patient.locationId", "$locationId")))
									        .append("then", 1)
									        .append("else", 0))))),
					
					new CustomAggregationOperation(new Document("$group",
						    new BasicDBObject("_id", new BasicDBObject("doctorId","$doctorId").append("locationId", "$locationId"))
						    .append("experienceInYear", new BasicDBObject("$first","$experienceInYear"))
						    .append("doctorId", new BasicDBObject("$first","$doctorId"))
						    .append("locationId", new BasicDBObject("$first","$locationId"))
						    .append("resourceName", new BasicDBObject("$first","$resourceName"))
						    .append("pointIfActive", new BasicDBObject("$first","$pointIfActive"))
						    .append("pointIfAdvance", new BasicDBObject("$first","$pointIfAdvance"))
						    .append("rxCount", new BasicDBObject("$first","$rxCount"))
						    .append("patientCount", new BasicDBObject("$sum","$patientCount")))),
					
					Aggregation.lookup("recommendation_cl", "doctorId", "doctorId", "likes"),
					new CustomAggregationOperation(new Document("$unwind", new BasicDBObject("path", "$likes").append("preserveNullAndEmptyArrays", true))),
					
					new CustomAggregationOperation(new Document("$project",
						    new BasicDBObject("experienceInYear", "$experienceInYear")
						    .append("doctorId", "$doctorId")
						    .append("locationId", "$locationId")
						    .append("resourceName", "$resourceName")
						    .append("pointIfActive", "$pointIfActive")
						    .append("pointIfAdvance", "$pointIfAdvance")
						    .append("rxCount", "$rxCount")
						    .append("patientCount", "$patientCount")
						    .append("patientCountAdditionalIncrement", new BasicDBObject(
							        "$cond", new BasicDBObject(
									          "if", new BasicDBObject("$gte", Arrays.asList("$patientCount", 50)))
									        .append("then", 0.1)
									        .append("else", 0)))
						    .append("noOfLikes", new BasicDBObject(
							        "$cond", new BasicDBObject(
									          "if", new BasicDBObject("$eq", Arrays.asList("$likes.discarded", false)))
									        .append("then", 1)
									        .append("else", 0))))),
					
					new CustomAggregationOperation(new Document("$group",
						    new BasicDBObject("_id", new BasicDBObject("doctorId","$doctorId").append("locationId", "$locationId"))
						    .append("experienceInYear", new BasicDBObject("$first","$experienceInYear"))
						    .append("doctorId", new BasicDBObject("$first","$doctorId"))
						    .append("locationId", new BasicDBObject("$first","$locationId"))
						    .append("resourceName", new BasicDBObject("$first","$resourceName"))
						    .append("pointIfActive", new BasicDBObject("$first","$pointIfActive"))
						    .append("pointIfAdvance", new BasicDBObject("$first","$pointIfAdvance"))
						    .append("rxCount", new BasicDBObject("$first","$rxCount"))
						    .append("patientCount", new BasicDBObject("$first","$patientCount"))
						    .append("patientCountAdditionalIncrement", new BasicDBObject("$first","$patientCountAdditionalIncrement"))
						    .append("noOfLikes", new BasicDBObject("$sum","$noOfLikes")))),				
					
					new CustomAggregationOperation(new Document("$project",
						    new BasicDBObject("experienceInYear", "$experienceInYear")
						    	.append("doctorId", "$doctorId")
						    .append("locationId", "$locationId")
						    .append("resourceName", "$resourceName")
						    .append("pointIfActive", "$pointIfActive")
						    .append("pointIfAdvance", "$pointIfAdvance")
						    .append("rxCount", new BasicDBObject("$multiply", Arrays.asList(new BasicDBObject("$divide", Arrays.asList("$rxCount", rxCount)), noOfRxByDoctorWeightageInPercentage)))
						    .append("patientCount", new BasicDBObject("$add", Arrays.asList("$patientCountAdditionalIncrement", new BasicDBObject("$multiply", Arrays.asList(new BasicDBObject("$divide", Arrays.asList("$patientCount", patientCount)), noOfPatientsOfDoctorWeightageInPercentage)))))
						    .append("noOfLikes", new BasicDBObject("$multiply", Arrays.asList(new BasicDBObject("$divide", Arrays.asList("$noOfLikes", likesCount)), noOfLikesToDoctorWeightageInPercentage))))),
					
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("doctorId","$doctorId").append("locationId", "$locationId"))
						    .append("experienceInYear", new BasicDBObject("$first","$experienceInYear"))
						    .append("doctorId", new BasicDBObject("$first","$doctorId"))
						    .append("locationId", new BasicDBObject("$first","$locationId"))
						    .append("resourceName", new BasicDBObject("$first","$resourceName"))
						    .append("pointIfActive", new BasicDBObject("$first","$pointIfActive"))
						    .append("pointIfAdvance", new BasicDBObject("$first","$pointIfAdvance"))
						    .append("rxCount", new BasicDBObject("$first","$rxCount"))
						    .append("patientCount", new BasicDBObject("$first","$patientCount"))
						    .append("noOfLikes", new BasicDBObject("$first","$noOfLikes")))),
					
					new CustomAggregationOperation(new Document("$project",
						    new BasicDBObject("experienceInYear", "$experienceInYear")
						    .append("doctorId", "$doctorId")
						    .append("locationId", "$locationId")
						    .append("resourceName", "$resourceName")
						    .append("pointIfActive", "$pointIfActive")
						    .append("pointIfAdvance", "$pointIfAdvance")
						    .append("rxCount", "$rxCount")
						    .append("patientCount", "$patientCount")
						    .append("noOfLikes", "$noOfLikes")
						    .append("totalCount", new BasicDBObject("$add", Arrays.asList("$experienceInYear", "$rxCount", "$patientCount", "$noOfLikes", "$pointIfActive", "$pointIfAdvance"))))),
					
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("doctorId","$doctorId").append("locationId", "$locationId"))
						    .append("experienceInYear", new BasicDBObject("$first","$experienceInYear"))
						    .append("doctorId", new BasicDBObject("$first","$doctorId"))
						    .append("locationId", new BasicDBObject("$first","$locationId"))
						    .append("resourceName",  new BasicDBObject("$first","$resourceName"))
						    .append("rankingCountResponse", new BasicDBObject("$first","$rankingCountResponse"))
						    .append("pointIfActive", new BasicDBObject("$first","$pointIfActive"))
						    .append("pointIfAdvance", new BasicDBObject("$first","$pointIfAdvance"))
						    .append("rxCount", new BasicDBObject("$first","$rxCount"))
						    .append("patientCount", new BasicDBObject("$first","$patientCount"))
						    .append("noOfLikes", new BasicDBObject("$first","$noOfLikes"))
						    .append("totalCount", new BasicDBObject("$first","$totalCount")))),
					
					new CustomAggregationOperation(new Document("$sort", new BasicDBObject("totalCount", -1))));
			
			List<DoctorWithRankingDetailResponse> doctorWithRankingDetailResponses = mongoTemplate.aggregate(aggregation, DoctorClinicProfileCollection.class, DoctorWithRankingDetailResponse.class).getMappedResults();
			int i = 1;
			for(DoctorWithRankingDetailResponse detailResponse : doctorWithRankingDetailResponses) {
				
				if(!DPDoctorUtils.anyStringEmpty(detailResponse.getDoctorId(), detailResponse.getLocationId())) {
					RankingCountCollection rankingCountCollection = rankingCountRepository.findByResourceIdAndLocationId(new ObjectId(detailResponse.getDoctorId()), new ObjectId(detailResponse.getLocationId()), Resource.DOCTOR.getType());
					if(rankingCountCollection == null) {
						rankingCountCollection = new RankingCountCollection();
						rankingCountCollection.setCreatedTime(new Date());
					}
					rankingCountCollection.setRankingCount(i++);
					rankingCountCollection.setResourceType(Resource.DOCTOR);
					rankingCountCollection.setUpdatedTime(new Date());
					rankingCountCollection.setResourceId(new ObjectId(detailResponse.getDoctorId()));
					rankingCountCollection.setLocationId(new ObjectId(detailResponse.getLocationId()));
					rankingCountCollection.setTotalCountInPercentage(detailResponse.getTotalCount());
					rankingCountCollection.setResourceName(detailResponse.getResourceName());
					List<RankingCountParametersWithValueInPercentage> parameters = new ArrayList<RankingCountParametersWithValueInPercentage>();
					
					parameters.add(new RankingCountParametersWithValueInPercentage(RankingCountParatmeter.APPOINTMENT, 0));
					parameters.add(new RankingCountParametersWithValueInPercentage(RankingCountParatmeter.EXPERIENCE, detailResponse.getExperienceInYear()));
					parameters.add(new RankingCountParametersWithValueInPercentage(RankingCountParatmeter.FEEDBACK, 0));
					parameters.add(new RankingCountParametersWithValueInPercentage(RankingCountParatmeter.NUMBER_OF_LIKES, detailResponse.getNoOfLikes()));
					parameters.add(new RankingCountParametersWithValueInPercentage(RankingCountParatmeter.NUMBER_OF_PATIENTS, detailResponse.getPatientCount()));
					parameters.add(new RankingCountParametersWithValueInPercentage(RankingCountParatmeter.NUMBER_OF_RX, detailResponse.getRxCount()));
					parameters.add(new RankingCountParametersWithValueInPercentage(RankingCountParatmeter.POINT_FOR_PAYMENT_TYPE, detailResponse.getPointIfAdvance()));
					parameters.add(new RankingCountParametersWithValueInPercentage(RankingCountParatmeter.POINT_IF_ACTIVE, detailResponse.getPointIfActive()));
					parameters.add(new RankingCountParametersWithValueInPercentage(RankingCountParatmeter.RECOMMENDATIONS, 0));
					rankingCountCollection.setParameters(parameters);
					
					rankingCountCollection = rankingCountRepository.save(rankingCountCollection);
					
					DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdLocationId(rankingCountCollection.getResourceId(), rankingCountCollection.getLocationId());
					clinicProfileCollection.setRankingCount(rankingCountCollection.getRankingCount());
					clinicProfileCollection.setUpdatedTime(new Date());
					clinicProfileCollection = doctorClinicProfileRepository.save(clinicProfileCollection);
					
					ESDoctorDocument doctorDocument = esDoctorRepository.findByUserIdAndLocationId(detailResponse.getDoctorId(), detailResponse.getLocationId());
					if(doctorDocument != null) {
						doctorDocument.setRankingCount(clinicProfileCollection.getRankingCount());
						esDoctorRepository.save(doctorDocument);
					}
				}
			}		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	public List<RankingCount> getDoctorsRankingCount(long page, int size) {
		List<RankingCount> response = null;
		try {
			Aggregation aggregation = null;
			if(size > 0)aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Direction.DESC, "rankingCount")), Aggregation.skip(page * size), Aggregation.limit(size));
			else aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Direction.DESC, "rankingCount")));
			
			response = mongoTemplate.aggregate(aggregation, RankingCountCollection.class, RankingCount.class).getMappedResults();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting doctors with ranking " + e.getMessage());
		}
		return response;
	}
}
