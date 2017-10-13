package com.dpdocter.services.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DiagnosticTestSamplePickUpSlot;
import com.dpdocter.beans.OrderDiagnosticTest;
import com.dpdocter.beans.PickUpSlot;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DiagnosticTestPickUpSlotCollection;
import com.dpdocter.collections.OrderDiagnosticTestCollection;
import com.dpdocter.enums.Day;
import com.dpdocter.enums.OrderStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DiagnosticTestPickUpSlotRepository;
import com.dpdocter.repository.OrderDiagnosticTestRepository;
import com.dpdocter.response.LabSearchResponse;
import com.dpdocter.services.DiagnosticTestOrderService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class DiagnosticTestOrderServicesimpl implements DiagnosticTestOrderService {

	private static Logger logger = Logger.getLogger(DiagnosticTestOrderServicesimpl.class.getName());
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private DiagnosticTestPickUpSlotRepository diagnosticTestPickUpSlotRepository;
	
	@Autowired
	private OrderDiagnosticTestRepository orderDiagnosticTestRepository;
	
	@Override
	public List<LabSearchResponse> searchLabsByTest(String city, String location, String latitude, String longitude, String searchTerm, List<String> testNames) {
		List<LabSearchResponse> response = null;
		try{
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria("testName").in(testNames).and("locationId").ne(null)),
					new CustomAggregationOperation(new BasicDBObject("$project", 
							new BasicDBObject("locationId", "$locationId")
							.append("test.testName", "$testName")
							.append("test._id", "$_id")
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

	@Override
	public List<DiagnosticTestSamplePickUpSlot> getDiagnosticTestSamplePickUpTimeSlots() {
		List<DiagnosticTestSamplePickUpSlot> response = new ArrayList<DiagnosticTestSamplePickUpSlot>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("EEEEE");
			sdf.setTimeZone(TimeZone.getTimeZone("IST"));
			
			Date date = new Date();
			
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			localCalendar.setTime(date);
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			DateTime slotStartDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			
			DateTime slotEndDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			
			DiagnosticTestPickUpSlotCollection pickUpSlotCollection = diagnosticTestPickUpSlotRepository.findAll().get(0);
			
			for(int i=0 ; i<7; i++) {
				
				DiagnosticTestSamplePickUpSlot slot = new DiagnosticTestSamplePickUpSlot();
				slot.setDay(Day.valueOf(slotStartDateTime.dayOfWeek().getAsText().toUpperCase()));
				slot.setSlotDate(slotStartDateTime.getMillis());
				
				List<PickUpSlot> pickUpSlots = pickUpSlotCollection.getSlots().get(slot.getDay());
				
				if(pickUpSlots != null && !pickUpSlots.isEmpty()) {
					for(PickUpSlot pickUpSlot : pickUpSlots) {
						Integer noOfBookedSlots = orderDiagnosticTestRepository.countByDateAndTime(slotStartDateTime.getMillis(), slotEndDateTime.getMillis(), pickUpSlot.getFromTime());
						if(noOfBookedSlots != null && noOfBookedSlots >= pickUpSlot.getNoOfAppointmentsAllowed())pickUpSlot.setIsAvailable(false);
					}
					
					slot.setSlot(pickUpSlots);
					response.add(slot);
				}
				slotStartDateTime = slotStartDateTime.plusDays(1);
				slotEndDateTime = slotEndDateTime.plusDays(1);
			}
		}catch(Exception e){
			logger.error("Error while getting Diagnostic Test Sample Pick Up Slots "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting Diagnostic Test Sample Pick Up Slots.");
		}
		return response;
	}

	@Override
	public OrderDiagnosticTest placeDiagnosticTestOrder(OrderDiagnosticTest request) {
		OrderDiagnosticTest response = new OrderDiagnosticTest();
		try {
			OrderDiagnosticTestCollection orderDiagnosticTestCollection = new OrderDiagnosticTestCollection();
			BeanUtil.map(request, orderDiagnosticTestCollection);
			
			if(!DPDoctorUtils.anyStringEmpty(request.getId())) {
				OrderDiagnosticTestCollection oldOrder = orderDiagnosticTestRepository.findOne(new ObjectId(request.getId()));
				orderDiagnosticTestCollection.setCreatedTime(oldOrder.getCreatedTime());
				orderDiagnosticTestCollection.setUniqueOrderId(oldOrder.getUniqueOrderId());
			}else {
				orderDiagnosticTestCollection.setUniqueOrderId(UniqueIdInitial.ORDER_DIAGNOSTIC_TEST.getInitial() + DPDoctorUtils.generateRandomId());
				orderDiagnosticTestCollection.setCreatedTime(new Date());
				orderDiagnosticTestCollection.setOrderStatus(OrderStatus.PLACED);
			}
			orderDiagnosticTestCollection = orderDiagnosticTestRepository.save(orderDiagnosticTestCollection);
			BeanUtil.map(orderDiagnosticTestCollection, response);
			
		}catch(Exception e){
			logger.error("Error while placing Diagnostic Test Sample Order "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while placing Diagnostic Test Sample Order");
		}
		return response;
	}
}
