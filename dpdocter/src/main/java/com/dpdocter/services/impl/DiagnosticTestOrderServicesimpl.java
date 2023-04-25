package com.dpdocter.services.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.DiagnosticTestPackage;
import com.dpdocter.beans.DiagnosticTestSamplePickUpSlot;
import com.dpdocter.beans.OrderDiagnosticTest;
import com.dpdocter.beans.PickUpSlot;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DiagnosticTestPackageCollection;
import com.dpdocter.collections.DiagnosticTestPickUpSlotCollection;
import com.dpdocter.collections.LocationCollection;
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
	public List<LabSearchResponse> searchLabs(String city, String location, String latitude, String longitude,
			String searchTerm, List<String> testNames, int page, int size, Boolean havePackage) {
		List<LabSearchResponse> response = null;
		try {
			if (havePackage) {
				response = getLabsHavingTestPackages(city, location, latitude, longitude, page, size);
			} else {
				response = serachLabsByTest(city, location, latitude, longitude, searchTerm, testNames, page, size);
			}
		} catch (Exception e) {
			logger.error("Error while searching labss " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while searching labs.");
		}
		return response;
	}

	private List<LabSearchResponse> getLabsHavingTestPackages(String city, String location, String latitude,
			String longitude, long page, int size) {
		List<LabSearchResponse> response = null;
		try {
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("isLab").is(true)),

					Aggregation.lookup("diagnostic_test_package_cl", "_id", "locationId", "package"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$package").append("preserveNullAndEmptyArrays", true))),
					Aggregation.match(new Criteria("package.discarded").is(false)),

					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("_id", "$_id").append("hospitalId", "$hospitalId")
									.append("locationName", "$locationName")
									.append("isNABLAccredited", "$isNABLAccredited").append("package", "$package"))),

					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$_id")
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("locationName", new BasicDBObject("$first", "$locationName"))
									.append("isNABLAccredited", new BasicDBObject("$first", "$isNABLAccredited"))
									.append("packages", new BasicDBObject("$push", "$package")))),

					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("_id", "$_id").append("hospitalId", "$hospitalId")
									.append("locationName", "$locationName")
									.append("isNABLAccredited", "$isNABLAccredited").append("isLocationRequired",
											new BasicDBObject("$cond", new BasicDBObject("if",
													new BasicDBObject("$gt",
															Arrays.asList(new BasicDBObject("$size", "$packages"), 0)))
													.append("then", 1).append("else", 0))))),

					Aggregation.match(new Criteria("isLocationRequired").is(1)),

					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$_id")
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("locationName", new BasicDBObject("$first", "$locationName"))
									.append("isNABLAccredited", new BasicDBObject("$first", "$isNABLAccredited")))),
					(size > 0) ? Aggregation.skip(page * size) : Aggregation.match(new Criteria()),
					(size > 0) ? Aggregation.limit(size) : Aggregation.match(new Criteria()),
					new CustomAggregationOperation(new Document("$sort", new BasicDBObject("localeRankingCount", -1))));

			response = mongoTemplate.aggregate(aggregation, LocationCollection.class, LabSearchResponse.class)
					.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while searching labs having test packages " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while searching labs having test packages");
		}
		return response;
	}

	private List<LabSearchResponse> serachLabsByTest(String city, String location, String latitude, String longitude,
			String searchTerm, List<String> testNames, long page, int size) {
		List<LabSearchResponse> response = null;
		try {
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria("testName").in(testNames).and("locationId").ne(null)),
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("locationId", "$locationId").append("hospitalId", "$hospitalId")
									.append("test.testName", "$testName").append("test._id", "$_id")
									.append("test.locationId", "$locationId")
									.append("test.diagnosticTestCost", "$diagnosticTestCost")
									.append("test.diagnosticTestCostForPatient", "$diagnosticTestCostForPatient")
									.append("totalCost", "$diagnosticTestCost")
									.append("totalCostForPatient", "$diagnosticTestCostForPatient"))),

					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$locationId")
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("diagnosticTests", new BasicDBObject("$push", "$test"))
									.append("totalCost", new BasicDBObject("$sum", "$totalCost"))
									.append("totalCostForPatient", new BasicDBObject("$sum", "$totalCostForPatient")))),

					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("_id", "$locationId").append("locationId", "$locationId")
									.append("hospitalId", "$hospitalId").append("diagnosticTests", "$diagnosticTests")
									.append("isLocationRequired",
											new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$gte",
													Arrays.asList(new BasicDBObject("$size", "$diagnosticTests"),
															testNames.size())))
													.append("then", true).append("else", false)))
									.append("totalCost", "$totalCost")
									.append("totalCostForPatient", "$totalCostForPatient")
									.append("totalSavingInPercentage",
											new BasicDBObject("$cond", new BasicDBObject("if",
													new BasicDBObject("$gt", Arrays.asList("$totalCost", 0)))
													.append("then", new BasicDBObject("$multiply",
															Arrays.asList(new BasicDBObject("$divide",
																	Arrays.asList(
																			new BasicDBObject("$subtract",
																					Arrays.asList("$totalCost",
																							"$totalCostForPatient")),
																			"$totalCost")),
																	100)))
													.append("else", 0)))

					)

					),

					Aggregation.match(new Criteria("isLocationRequired").is(true)),

					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),

					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("_id", "$locationId").append("locationId", "$locationId")
									.append("hospitalId", "$hospitalId")
									.append("locationName", "$location.locationName")
									.append("isNABLAccredited", "$location.isNABLAccredited")
									.append("localeRankingCount", "$location.localeRankingCount")
									.append("diagnosticTests", "$diagnosticTests").append("totalCost", "$totalCost")
									.append("totalCostForPatient", "$totalCostForPatient")
									.append("totalSavingInPercentage", "$totalSavingInPercentage"))),

					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$locationId")
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("locationName", new BasicDBObject("$first", "$locationName"))
									.append("isNABLAccredited", new BasicDBObject("$first", "$isNABLAccredited"))
									.append("localeRankingCount", new BasicDBObject("$first", "$localeRankingCount"))
									.append("diagnosticTests", new BasicDBObject("$first", "$diagnosticTests"))
									.append("totalCost", new BasicDBObject("$first", "$totalCost"))
									.append("totalCostForPatient", new BasicDBObject("$first", "$totalCostForPatient"))
									.append("totalSavingInPercentage",
											new BasicDBObject("$first", "$totalSavingInPercentage")))),

					(size > 0) ? Aggregation.skip(page * size) : Aggregation.match(new Criteria()),
					(size > 0) ? Aggregation.limit(size) : Aggregation.match(new Criteria()),
					new CustomAggregationOperation(new Document("$sort", new BasicDBObject("localeRankingCount", -1))));

			response = mongoTemplate.aggregate(aggregation, DiagnosticTestCollection.class, LabSearchResponse.class)
					.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while searching labs by tests" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while searching labs by tests");
		}
		return response;
	}

	@Override
	public List<DiagnosticTestSamplePickUpSlot> getDiagnosticTestSamplePickUpTimeSlots(String dateStr) {
		List<DiagnosticTestSamplePickUpSlot> response = new ArrayList<DiagnosticTestSamplePickUpSlot>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("EEEEE");
			sdf.setTimeZone(TimeZone.getTimeZone("IST"));

			Date date = new Date();
			if (!DPDoctorUtils.anyStringEmpty(dateStr))
				date = new Date(Long.parseLong(dateStr));

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			localCalendar.setTime(date);
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			DiagnosticTestPickUpSlotCollection pickUpSlotCollection = diagnosticTestPickUpSlotRepository.findAll()
					.get(0);
			if (pickUpSlotCollection != null)
				for (int i = 0; i < 7; i++) {

					DateTime slotStartDateTime = new DateTime(currentYear, currentMonth, currentDay + i, 0, 0, 0,
							DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

					DateTime slotEndDateTime = new DateTime(currentYear, currentMonth, currentDay + i, 23, 59, 59,
							DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

					DiagnosticTestSamplePickUpSlot slot = new DiagnosticTestSamplePickUpSlot();
					slot.setDay(Day.valueOf(slotStartDateTime.dayOfWeek().getAsText().toUpperCase()));
					slot.setSlotDate(slotStartDateTime.getMillis());

					List<PickUpSlot> pickUpSlots = pickUpSlotCollection.getSlots().get(slot.getDay());

					if (pickUpSlots != null && !pickUpSlots.isEmpty()) {
						for (PickUpSlot pickUpSlot : pickUpSlots) {
							Integer noOfBookedSlots = orderDiagnosticTestRepository.countByDateAndTime(
									slotStartDateTime.getMillis(), slotEndDateTime.getMillis(),
									pickUpSlot.getFromTime());
							if (noOfBookedSlots != null) {
								pickUpSlot.setNoOfAppointmentsAllowed(
										pickUpSlot.getNoOfAppointmentsAllowed() - noOfBookedSlots);
								if ((noOfBookedSlots >= pickUpSlot.getNoOfAppointmentsAllowed()))
									pickUpSlot.setIsAvailable(false);
							}
						}

						slot.setSlot(pickUpSlots);
						response.add(slot);
					}
					slotStartDateTime = slotStartDateTime.plusDays(1);
					slotEndDateTime = slotEndDateTime.plusDays(1);
				}
		} catch (Exception e) {
			logger.error("Error while getting Diagnostic Test Sample Pick Up Slots " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Diagnostic Test Sample Pick Up Slots.");
		}
		return response;
	}

	@Override
	public OrderDiagnosticTest placeDiagnosticTestOrder(OrderDiagnosticTest request) {
		OrderDiagnosticTest response = new OrderDiagnosticTest();
		try {
			OrderDiagnosticTestCollection orderDiagnosticTestCollection = new OrderDiagnosticTestCollection();
			BeanUtil.map(request, orderDiagnosticTestCollection);

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				OrderDiagnosticTestCollection oldOrder = orderDiagnosticTestRepository
						.findById(new ObjectId(request.getId())).orElse(null);
				orderDiagnosticTestCollection.setCreatedTime(oldOrder.getCreatedTime());
				orderDiagnosticTestCollection.setUniqueOrderId(oldOrder.getUniqueOrderId());
			} else {
				orderDiagnosticTestCollection.setUniqueOrderId(
						UniqueIdInitial.ORDER_DIAGNOSTIC_TEST.getInitial() + DPDoctorUtils.generateRandomId());
				orderDiagnosticTestCollection.setCreatedTime(new Date());
				orderDiagnosticTestCollection.setOrderStatus(OrderStatus.PLACED);
			}
			orderDiagnosticTestCollection = orderDiagnosticTestRepository.save(orderDiagnosticTestCollection);
			BeanUtil.map(orderDiagnosticTestCollection, response);

		} catch (Exception e) {
			logger.error("Error while placing Diagnostic Test Sample Order " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while placing Diagnostic Test Sample Order");
		}
		return response;
	}

	@Override
	public List<OrderDiagnosticTest> getPatientOrders(String userId, int page, int size) {
		List<OrderDiagnosticTest> response = null;
		try {
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(new Criteria("userId").is(new ObjectId(userId))),
							Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$location").append("preserveNullAndEmptyArrays", true))),
							new CustomAggregationOperation(new Document("$project",
									new BasicDBObject("_id", "$_id").append("locationId", "$locationId")
											.append("userId", "$userId").append("uniqueOrderId", "$uniqueOrderId")
											.append("pickUpTime", "$pickUpTime").append("pickUpDate", "$pickUpDate")
											.append("testsPackageId", "$testsPackageId")
											.append("diagnosticTests", "$diagnosticTests")
											.append("pickUpAddress", "$pickUpAddress")
											.append("orderStatus", "$orderStatus").append("totalCost", "$totalCost")
											.append("totalCostForPatient", "$totalCostForPatient")
											.append("totalSavingInPercentage", "$totalSavingInPercentage")
											.append("isCancelled", "$isCancelled").append("createdTime", "$createdTime")
											.append("updatedTime", "$updatedTime")
											.append("locationName", "$location.locationName").append("isNABLAccredited",
													"$location.isNABLAccredited"))),
							new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("userId", new BasicDBObject("$first", "$userId"))
									.append("uniqueOrderId", new BasicDBObject("$first", "$uniqueOrderId"))
									.append("pickUpTime", new BasicDBObject("$first", "$pickUpTime"))
									.append("pickUpDate", new BasicDBObject("$first", "$pickUpDate"))
									.append("testsPackageId", new BasicDBObject("$first", "$testsPackageId"))
									.append("diagnosticTests", new BasicDBObject("$first", "$diagnosticTests"))
									.append("pickUpAddress", new BasicDBObject("$first", "$pickUpAddress"))
									.append("orderStatus", new BasicDBObject("$first", "$orderStatus"))
									.append("totalCost", new BasicDBObject("$first", "$totalCost"))
									.append("totalCostForPatient", new BasicDBObject("$first", "$totalCostForPatient"))
									.append("totalSavingInPercentage",
											new BasicDBObject("$first", "$totalSavingInPercentage"))
									.append("isCancelled", new BasicDBObject("$first", "$isCancelled"))
									.append("locationName", new BasicDBObject("$first", "$locationName"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("isNABLAccredited", new BasicDBObject("$first", "$isNABLAccredited")))),
							(size > 0) ? Aggregation.skip(page * size) : Aggregation.match(new Criteria()),
							(size > 0) ? Aggregation.limit(size) : Aggregation.match(new Criteria()),
							new CustomAggregationOperation(
									new Document("$sort", new BasicDBObject("updatedTime", -1))));

			response = mongoTemplate
					.aggregate(aggregation, OrderDiagnosticTestCollection.class, OrderDiagnosticTest.class)
					.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting patient orders" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting patient orders");
		}
		return response;
	}

	@Override
	public List<OrderDiagnosticTest> getLabOrders(String locationId, int page, int size) {
		List<OrderDiagnosticTest> response = null;
		try {
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(new Criteria("locationId").is(new ObjectId(locationId))),
							Aggregation.lookup("patient_cl", "userId", "userId", "patient"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$patient").append("preserveNullAndEmptyArrays", true))),
							new CustomAggregationOperation(new Document("$project",
									new BasicDBObject("_id", "$_id").append("locationId", "$locationId")
											.append("userId", "$userId").append("uniqueOrderId", "$uniqueOrderId")
											.append("pickUpTime", "$pickUpTime").append("pickUpDate", "$pickUpDate")
											.append("testsPackageId", "$testsPackageId")
											.append("diagnosticTests", "$diagnosticTests")
											.append("pickUpAddress", "$pickUpAddress")
											.append("orderStatus", "$orderStatus").append("totalCost", "$totalCost")
											.append("totalCostForPatient", "$totalCostForPatient")
											.append("totalSavingInPercentage", "$totalSavingInPercentage")
											.append("isCancelled", "$isCancelled").append("createdTime", "$createdTime")
											.append("updatedTime", "$updatedTime").append("patientName",
													"$patient.localPatientName"))),
							new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("userId", new BasicDBObject("$first", "$userId"))
									.append("uniqueOrderId", new BasicDBObject("$first", "$uniqueOrderId"))
									.append("pickUpTime", new BasicDBObject("$first", "$pickUpTime"))
									.append("pickUpDate", new BasicDBObject("$first", "$pickUpDate"))
									.append("testsPackageId", new BasicDBObject("$first", "$testsPackageId"))
									.append("diagnosticTests", new BasicDBObject("$first", "$diagnosticTests"))
									.append("pickUpAddress", new BasicDBObject("$first", "$pickUpAddress"))
									.append("orderStatus", new BasicDBObject("$first", "$orderStatus"))
									.append("totalCost", new BasicDBObject("$first", "$totalCost"))
									.append("totalCostForPatient", new BasicDBObject("$first", "$totalCostForPatient"))
									.append("totalSavingInPercentage",
											new BasicDBObject("$first", "$totalSavingInPercentage"))
									.append("isCancelled", new BasicDBObject("$first", "$isCancelled"))
									.append("patientName", new BasicDBObject("$first", "$patientName"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime")))),
							(size > 0) ? Aggregation.skip(page * size) : Aggregation.match(new Criteria()),
							(size > 0) ? Aggregation.limit(size) : Aggregation.match(new Criteria()),
							new CustomAggregationOperation(
									new Document("$sort", new BasicDBObject("updatedTime", -1))));

			response = mongoTemplate
					.aggregate(aggregation, OrderDiagnosticTestCollection.class, OrderDiagnosticTest.class)
					.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting lab orders" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting lab orders");
		}
		return response;
	}

	@Override
	public OrderDiagnosticTest cancelOrderDiagnosticTest(String orderId, String userId) {
		OrderDiagnosticTest response = null;
		try {
			OrderDiagnosticTestCollection orderDiagnosticTestCollection = orderDiagnosticTestRepository
					.findByIdAndUserId(new ObjectId(orderId), new ObjectId(userId));
			if (orderDiagnosticTestCollection == null)
				throw new BusinessException(ServiceError.InvalidInput, "Invalid orderId and userId");

			orderDiagnosticTestCollection.setIsCancelled(true);
			orderDiagnosticTestCollection.setUpdatedTime(new Date());
			orderDiagnosticTestCollection = orderDiagnosticTestRepository.save(orderDiagnosticTestCollection);
			response = new OrderDiagnosticTest();
			BeanUtil.map(orderDiagnosticTestCollection, response);
		} catch (Exception e) {
			logger.error("Error while cancelling order test " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while cancelling order test");
		}
		return response;
	}

	@Override
	public OrderDiagnosticTest getDiagnosticTestOrderById(String orderId, Boolean isLab, Boolean isUser) {
		OrderDiagnosticTest response = null;
		try {
			OrderDiagnosticTestCollection orderDiagnosticTestCollection = null;

			CustomAggregationOperation projectListForTestPackage = null, groupListForTestPackage = null,
					projectList = null, groupList = null;

			if (isLab || isUser) {
				projectList = new CustomAggregationOperation(new Document("$project",
						new BasicDBObject("_id", "$_id").append("locationId", "$locationId").append("userId", "$userId")
								.append("uniqueOrderId", "$uniqueOrderId").append("pickUpTime", "$pickUpTime")
								.append("pickUpDate", "$pickUpDate").append("testsPackageIds", "$testsPackageIds")
								.append("diagnosticTests", "$diagnosticTests").append("pickUpAddress", "$pickUpAddress")
								.append("orderStatus", "$orderStatus").append("totalCost", "$totalCost")
								.append("totalCostForPatient", "$totalCostForPatient")
								.append("totalSavingInPercentage", "$totalSavingInPercentage")
								.append("isCancelled", "$isCancelled").append("createdTime", "$createdTime")
								.append("updatedTime", "$updatedTime").append("patientName", "$user.firstName")
								.append("locationName", "$location.locationName")
								.append("isNABLAccredited", "$location.isNABLAccredited")
								.append("testsPackage.packageName", "$testsPackage.packageName")
								.append("testsPackage._id", "$testsPackage._id")
								.append("testsPackage.explanation", "$testsPackage.explanation")
								.append("testId", "$tests._id").append("testName", "$tests.testName")
								.append("testsPackage.diagnosticTestPackageCost",
										"$testsPackage.diagnosticTestPackageCost")
								.append("testsPackage.diagnosticTestCostPackageForPatient",
										"$testsPackage.diagnosticTestCostPackageForPatient")));

				groupList = new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
						.append("locationId", new BasicDBObject("$first", "$locationId"))
						.append("userId", new BasicDBObject("$first", "$userId"))
						.append("uniqueOrderId", new BasicDBObject("$first", "$uniqueOrderId"))
						.append("pickUpTime", new BasicDBObject("$first", "$pickUpTime"))
						.append("pickUpDate", new BasicDBObject("$first", "$pickUpDate"))
						.append("testsPackageId", new BasicDBObject("$first", "$testsPackageId"))
						.append("diagnosticTests", new BasicDBObject("$first", "$diagnosticTests"))
						.append("pickUpAddress", new BasicDBObject("$first", "$pickUpAddress"))
						.append("orderStatus", new BasicDBObject("$first", "$orderStatus"))
						.append("totalCost", new BasicDBObject("$first", "$totalCost"))
						.append("totalCostForPatient", new BasicDBObject("$first", "$totalCostForPatient"))
						.append("totalSavingInPercentage", new BasicDBObject("$first", "$totalSavingInPercentage"))
						.append("isCancelled", new BasicDBObject("$first", "$isCancelled"))
						.append("patientName", new BasicDBObject("$first", "$patientName"))
						.append("locationName", new BasicDBObject("$first", "$locationName"))
						.append("isNABLAccredited", new BasicDBObject("$first", "$isNABLAccredited"))
						.append("testsPackage", new BasicDBObject("$first", "$testsPackage"))
						.append("testIds", new BasicDBObject("$push", "$testId"))
						.append("testNames", new BasicDBObject("$push", "$testName"))
						.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
						.append("createdTime", new BasicDBObject("$first", "$createdTime"))));

				projectListForTestPackage = new CustomAggregationOperation(new Document("$project",
						new BasicDBObject("_id", "$_id").append("locationId", "$locationId").append("userId", "$userId")
								.append("uniqueOrderId", "$uniqueOrderId").append("pickUpTime", "$pickUpTime")
								.append("pickUpDate", "$pickUpDate").append("testsPackageIds", "$testsPackageIds")
								.append("diagnosticTests", "$diagnosticTests").append("pickUpAddress", "$pickUpAddress")
								.append("orderStatus", "$orderStatus").append("totalCost", "$totalCost")
								.append("totalCostForPatient", "$totalCostForPatient")
								.append("totalSavingInPercentage", "$totalSavingInPercentage")
								.append("isCancelled", "$isCancelled").append("createdTime", "$createdTime")
								.append("updatedTime", "$updatedTime").append("patientName", "$patientName")
								.append("locationName", "$locationName").append("isNABLAccredited", "$isNABLAccredited")
								.append("testsPackage.packageName", "$testsPackage.packageName")
								.append("testsPackage._id", "$testsPackage._id")
								.append("testsPackage.explanation", "$testsPackage.explanation")
								.append("testsPackage.testIds", "$testIds")
								.append("testsPackage.testNames", "$testNames")
								.append("testsPackage.diagnosticTestPackageCost",
										"$testsPackage.diagnosticTestPackageCost")
								.append("testsPackage.diagnosticTestCostPackageForPatient",
										"$testsPackage.diagnosticTestCostPackageForPatient")));

				groupListForTestPackage = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id", "$_id")
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("userId", new BasicDBObject("$first", "$userId"))
								.append("uniqueOrderId", new BasicDBObject("$first", "$uniqueOrderId"))
								.append("pickUpTime", new BasicDBObject("$first", "$pickUpTime"))
								.append("pickUpDate", new BasicDBObject("$first", "$pickUpDate"))
								.append("testsPackageId", new BasicDBObject("$first", "$testsPackageId"))
								.append("diagnosticTests", new BasicDBObject("$first", "$diagnosticTests"))
								.append("pickUpAddress", new BasicDBObject("$first", "$pickUpAddress"))
								.append("orderStatus", new BasicDBObject("$first", "$orderStatus"))
								.append("totalCost", new BasicDBObject("$first", "$totalCost"))
								.append("totalCostForPatient", new BasicDBObject("$first", "$totalCostForPatient"))
								.append("totalSavingInPercentage",
										new BasicDBObject("$first", "$totalSavingInPercentage"))
								.append("isCancelled", new BasicDBObject("$first", "$isCancelled"))
								.append("patientName", new BasicDBObject("$first", "$patientName"))
								.append("locationName", new BasicDBObject("$first", "$locationName"))
								.append("isNABLAccredited", new BasicDBObject("$first", "$isNABLAccredited"))
								.append("testsPackages", new BasicDBObject("$push", "$testsPackage"))
								.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))));
			}

			if (isLab) {
				Aggregation aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("id").is(new ObjectId(orderId))),
						Aggregation.lookup("user_cl", "userId", "_id", "user"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$testsPackageIds").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.lookup("diagnostic_test_package_cl", "testsPackageIds", "_id", "testsPackage"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$testsPackage").append("preserveNullAndEmptyArrays", true))),

						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$testsPackage.testIds").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.lookup("diagnostic_test_cl", "testsPackage.testIds", "_id", "tests"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$tests").append("preserveNullAndEmptyArrays", true))),

						projectList, groupList, projectListForTestPackage, groupListForTestPackage);

				List<OrderDiagnosticTest> orderDiagnosticTests = mongoTemplate
						.aggregate(aggregation, OrderDiagnosticTestCollection.class, OrderDiagnosticTest.class)
						.getMappedResults();
				if (orderDiagnosticTests != null && !orderDiagnosticTests.isEmpty()) {
					response = orderDiagnosticTests.get(0);
					if (response.getDiagnosticTests() != null && !response.getDiagnosticTests().isEmpty())
						response.setTestsPackages(null);
				} else {
					throw new BusinessException(ServiceError.InvalidInput, "Invalid orderId");
				}
			} else if (isUser) {
				Aggregation aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("id").is(new ObjectId(orderId))),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$location").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$testsPackageIds").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.lookup("diagnostic_test_package_cl", "testsPackageIds", "_id", "testsPackage"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$testsPackage").append("preserveNullAndEmptyArrays", true))),

						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$testsPackage.testIds").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.lookup("diagnostic_test_cl", "testsPackage.testIds", "_id", "tests"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$tests").append("preserveNullAndEmptyArrays", true))),

						projectList, groupList, projectListForTestPackage, groupListForTestPackage);

				List<OrderDiagnosticTest> orderDiagnosticTests = mongoTemplate
						.aggregate(aggregation, OrderDiagnosticTestCollection.class, OrderDiagnosticTest.class)
						.getMappedResults();
				if (orderDiagnosticTests != null && !orderDiagnosticTests.isEmpty()) {
					response = orderDiagnosticTests.get(0);
					if (response.getDiagnosticTests() != null && !response.getDiagnosticTests().isEmpty())
						response.setTestsPackages(null);
				} else {
					throw new BusinessException(ServiceError.InvalidInput, "Invalid orderId");
				}
			} else {
				orderDiagnosticTestCollection = orderDiagnosticTestRepository.findById(new ObjectId(orderId))
						.orElse(null);
				if (orderDiagnosticTestCollection == null)
					throw new BusinessException(ServiceError.InvalidInput, "Invalid orderId");

				response = new OrderDiagnosticTest();
				BeanUtil.map(orderDiagnosticTestCollection, response);
			}

		} catch (Exception e) {
			logger.error("Error while getting order test " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting order test");
		}
		return response;
	}

	@Override
	public List<DiagnosticTestPackage> getDiagnosticTestPackages(String locationId, String hospitalId,
			Boolean discarded, int page, int size) {
		List<DiagnosticTestPackage> response = null;
		try {
			Criteria criteria = new Criteria("locationId").is(new ObjectId(locationId)).and("hospitalId")
					.is(new ObjectId(hospitalId));
			if (!discarded)
				criteria.and("discarded").is(false);

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.unwind("testIds"), Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					Aggregation.unwind("location"), Aggregation.lookup("diagnostic_test_cl", "testIds", "_id", "tests"),
					Aggregation.unwind("tests"),
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("id", "$_id").append("locationId", "$locationId")
									.append("locationName", "$location.locationName")
									.append("isNABLAccredited", "$location.isNABLAccredited")
									.append("hospitalId", "$hospitalId").append("packageName", "$packageName")
									.append("explanation", "$explanation").append("discarded", "$discarded")
									.append("diagnosticTestPackageCost", "$diagnosticTestPackageCost")
									.append("diagnosticTestCostPackageForPatient",
											"$diagnosticTestCostPackageForPatient")
									.append("totalSavingInPercentage",
											new BasicDBObject("$multiply",
													Arrays.asList(new BasicDBObject("$divide", Arrays.asList(
															new BasicDBObject("$subtract",
																	Arrays.asList("$diagnosticTestPackageCost",
																			"$diagnosticTestCostPackageForPatient")),
															"$diagnosticTestPackageCost")), 100)))
									.append("testId", "$tests._id").append("testName", "$tests.testName")
									.append("createdTime", "$createdTime").append("updatedTime",
											"$updatedTime")
									.append("createdBy", "$createdBy"))),
					new CustomAggregationOperation(new Document("$group", new BasicDBObject("id", "$_id")
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("packageName", new BasicDBObject("$first", "$packageName"))
							.append("locationName", new BasicDBObject("$first", "$locationName"))
							.append("isNABLAccredited", new BasicDBObject("$first", "$isNABLAccredited"))
							.append("explanation", new BasicDBObject("$first", "$explanation"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("diagnosticTestPackageCost",
									new BasicDBObject("$first", "$diagnosticTestPackageCost"))
							.append("diagnosticTestCostPackageForPatient",
									new BasicDBObject("$first", "$diagnosticTestCostPackageForPatient"))
							.append("totalSavingInPercentage", new BasicDBObject("$first", "$totalSavingInPercentage"))
							.append("testIds", new BasicDBObject("$push", "$testId"))
							.append("testNames", new BasicDBObject("$push", "$testName"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy")))),
					(size > 0) ? Aggregation.skip(page * size) : Aggregation.match(new Criteria()),
					(size > 0) ? Aggregation.limit(size) : Aggregation.match(new Criteria()));

			response = mongoTemplate
					.aggregate(aggregation, DiagnosticTestPackageCollection.class, DiagnosticTestPackage.class)
					.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting diagnostic test package" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting diagnostic test package");
		}
		return response;
	}

	@Override
	public List<DiagnosticTest> searchDiagnosticTest(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm) {
		List<DiagnosticTest> response = null;
		try {

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = new Criteria("testName").regex("^" + searchTerm + "*", "i");

			if (size > 0) {
				response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),

						new CustomAggregationOperation(new Document("$project", new BasicDBObject("testId", "$_id")
								.append("testName", "$testName")
								.append("insensitiveTestName", new BasicDBObject("$toLower", "$testName"))
								.append("explanation", "$explanation").append("discarded", "$discarded")
								.append("specimen", "$specimen").append("diagnosticTestCode", "$diagnosticTestCode")
								.append("diagnosticTestCost", "$diagnosticTestCost")
								.append("diagnosticTestComission", "$diagnosticTestComission")
								.append("diagnosticTestCostForPatient", "$diagnosticTestCostForPatient")
								.append("createdTime", "$createdTime").append("updatedTime", "$updatedTime")
								.append("adminCreatedTime", "$adminCreatedTime").append("createdBy", "$createdBy"))),

						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", new BasicDBObject("testName", "$testName"))
										.append("testId", new BasicDBObject("$first", "$testId"))
										.append("testName", new BasicDBObject("$first", "$testName"))
										.append("insensitiveTestName", new BasicDBObject("$first", "$testName"))
										.append("explanation", new BasicDBObject("$first", "$explanation"))
										.append("discarded", new BasicDBObject("$first", "$discarded"))
										.append("specimen", new BasicDBObject("$first", "$specimen"))
										.append("diagnosticTestCode",
												new BasicDBObject("$first", "$diagnosticTestCode"))
										.append("diagnosticTestCost",
												new BasicDBObject("$first", "$diagnosticTestCost"))
										.append("diagnosticTestComission",
												new BasicDBObject("$first", "$diagnosticTestComission"))
										.append("diagnosticTestCostForPatient",
												new BasicDBObject("$first", "$diagnosticTestCostForPatient"))
										.append("createdTime", new BasicDBObject("$first", "$createdTime"))
										.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
										.append("adminCreatedTime", new BasicDBObject("$first", "$adminCreatedTime"))
										.append("createdBy", new BasicDBObject("$first", "$createdBy")))),

						new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$testId")
								.append("testName", "$testName").append("insensitiveTestName", "$insensitiveTestName")
								.append("explanation", "$explanation").append("discarded", "$discarded")
								.append("specimen", "$specimen").append("diagnosticTestCode", "$diagnosticTestCode")
								.append("diagnosticTestCost", "$diagnosticTestCost")
								.append("diagnosticTestComission", "$diagnosticTestComission")
								.append("diagnosticTestCostForPatient", "$diagnosticTestCostForPatient")
								.append("createdTime", "$createdTime").append("updatedTime", "$updatedTime")
								.append("adminCreatedTime", "$adminCreatedTime").append("createdBy", "$createdBy"))),

						new CustomAggregationOperation(
								new Document("$sort", new BasicDBObject("insensitiveTestName", 1))),
						Aggregation.skip(page * size), Aggregation.limit(size)), DiagnosticTestCollection.class,
						DiagnosticTest.class).getMappedResults();
			} else {
				response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),

						new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
								.append("testName", "$testName")
								.append("insensitiveTestName", new BasicDBObject("$toLower", "$testName"))
								.append("explanation", "$explanation").append("discarded", "$discarded")
								.append("specimen", "$specimen").append("diagnosticTestCode", "$diagnosticTestCode")
								.append("diagnosticTestCost", "$diagnosticTestCost")
								.append("diagnosticTestComission", "$diagnosticTestComission")
								.append("diagnosticTestCostForPatient", "$diagnosticTestCostForPatient")
								.append("createdTime", "$createdTime").append("updatedTime", "$updatedTime")
								.append("adminCreatedTime", "$adminCreatedTime").append("createdBy", "$createdBy"))),

						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", new BasicDBObject("testName", "$testName"))
										.append("id", new BasicDBObject("$first", "$id"))
										.append("insensitiveTestName", new BasicDBObject("$first", "$testName"))
										.append("explanation", new BasicDBObject("$first", "$explanation"))
										.append("discarded", new BasicDBObject("$first", "$discarded"))
										.append("specimen", new BasicDBObject("$first", "$specimen"))
										.append("diagnosticTestCode",
												new BasicDBObject("$first", "$diagnosticTestCode"))
										.append("diagnosticTestCost",
												new BasicDBObject("$first", "$diagnosticTestCost"))
										.append("diagnosticTestComission",
												new BasicDBObject("$first", "$diagnosticTestComission"))
										.append("diagnosticTestCostForPatient",
												new BasicDBObject("$first", "$diagnosticTestCostForPatient"))
										.append("createdTime", new BasicDBObject("$first", "$createdTime"))
										.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
										.append("adminCreatedTime", new BasicDBObject("$first", "$adminCreatedTime"))
										.append("createdBy", new BasicDBObject("$first", "$createdBy")))),

						new CustomAggregationOperation(
								new Document("$sort", new BasicDBObject("insensitiveTestName", 1)))),
						DiagnosticTestCollection.class, DiagnosticTest.class).getMappedResults();
			}

		} catch (Exception e) {
			logger.error("Error while getting diagnostic tests" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting diagnostic tests");
		}
		return response;
	}
}
