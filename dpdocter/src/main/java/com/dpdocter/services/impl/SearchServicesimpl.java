package com.dpdocter.services.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.LabSearchResponse;
import com.dpdocter.services.SearchService;
import com.mongodb.BasicDBObject;

@Service
public class SearchServicesimpl implements SearchService {

	private static Logger logger = Logger.getLogger(SearchServicesimpl.class.getName());
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<LabSearchResponse> searchLabsByTest(String city, String location, String latitude, String longitude, String searchTerm, List<String> testNames) {
		List<LabSearchResponse> response = null;
		try{
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria("testName").in(testNames).and("locationId").ne(null)),
					new CustomAggregationOperation(new BasicDBObject("$project", 
							new BasicDBObject("locationId", "$locationId")
							.append("test.testName", "$testName")
							.append("test.id", "$id")
							.append("test.locationId", "$locationId")
							.append("test.diagnosticTestCost", "$diagnosticTestCost")
							.append("test.diagnosticTestCostForPatient", "$diagnosticTestCostForPatient")
							.append("totalCost", "$diagnosticTestCost")
							.append("totalCostForPatient", "$diagnosticTestCostForPatient"))),
					
					new CustomAggregationOperation(new BasicDBObject("$group", 
							new BasicDBObject("_id", "$locationId")
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("diagnosticTests", new BasicDBObject("$push", "$test"))
							.append("totalCost", new BasicDBObject("$sum", "$totalCost"))
							.append("totalCostForPatient", new BasicDBObject("$sum", "$totalCostForPatient")))),
					
					new CustomAggregationOperation(new BasicDBObject("$project", 
							new BasicDBObject("id", "$locationId")
							.append("locationId", "$locationId")
							.append("diagnosticTests", "$diagnosticTests")
							.append("isLocationRequired", new BasicDBObject("$cond", new BasicDBObject(
							          "if", new BasicDBObject("$gte", Arrays.asList(new BasicDBObject("$size", "$diagnosticTests"), testNames.size())))
							        .append("then", true)
							        .append("else", false)))
							.append("totalCost", "$totalCost")
							.append("totalCostForPatient", "$totalCostForPatient")
							.append("totalSavingInPercentage", new BasicDBObject("$multiply", 
									Arrays.asList(new BasicDBObject("$divide", Arrays.asList(new BasicDBObject("$subtract", Arrays.asList("$totalCost","$totalCostForPatient")),"$totalCost")), 100))))),
					
					Aggregation.match(new Criteria("isLocationRequired").is(true)),
					
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					
					new CustomAggregationOperation(new BasicDBObject("$project", 
							new BasicDBObject("id", "$locationId")
							.append("locationId", "$locationId")
							.append("locationName", "$location.locationName")
							.append("isNABLAccredited", "$location.isNABLAccredited")
							.append("localeRankingCount", "$location.localeRankingCount")
							.append("diagnosticTests", "$diagnosticTests")
							.append("totalCost", "$totalCost")
							.append("totalCostForPatient", "$totalCostForPatient")
							.append("totalSavingInPercentage", "$totalSavingInPercentage"))),
					
					new CustomAggregationOperation(new BasicDBObject("$group", 
							new BasicDBObject("_id", "$locationId")
							.append("locationName", new BasicDBObject("$first", "$locationName"))
							.append("isNABLAccredited", new BasicDBObject("$first", "$isNABLAccredited"))
							.append("localeRankingCount", new BasicDBObject("$first", "$localeRankingCount"))
							.append("diagnosticTests", new BasicDBObject("$first", "$diagnosticTests"))
							.append("totalCost", new BasicDBObject("$first", "$totalCost"))
							.append("totalCostForPatient", new BasicDBObject("$first", "$totalCostForPatient"))
							.append("totalSavingInPercentage", new BasicDBObject("$first", "$totalSavingInPercentage")))),
					
					new CustomAggregationOperation(new BasicDBObject("$sort", new BasicDBObject("localeRankingCount", -1)))
					);
			
			response  = mongoTemplate.aggregate(aggregation, DiagnosticTestCollection.class, LabSearchResponse.class).getMappedResults();
		}catch(Exception e){
			logger.error("Error while searching labss "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while searching labs.");
		}
		return response;
	}
}
