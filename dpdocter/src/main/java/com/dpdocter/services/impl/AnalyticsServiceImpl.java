package com.dpdocter.services.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.collections.DoctorExpenseCollection;
import com.dpdocter.collections.DoctorPatientDueAmountCollection;
import com.dpdocter.collections.DoctorPatientInvoiceCollection;
import com.dpdocter.collections.DoctorPatientReceiptCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.enums.ModeOfPayment;
import com.dpdocter.enums.SearchType;
import com.dpdocter.enums.UnitType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.AmountDueAnalyticsDataResponse;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.DoctorVisitAnalyticResponse;
import com.dpdocter.response.ExpenseCountResponse;
import com.dpdocter.response.IncomeAnalyticsDataResponse;
import com.dpdocter.response.InvoiceAnalyticsDataDetailResponse;
import com.dpdocter.response.PaymentAnalyticsDataResponse;
import com.dpdocter.response.PaymentDetailsAnalyticsDataResponse;
import com.dpdocter.services.AnalyticsService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

//Billing analytic 
@Service
@Transactional
public class AnalyticsServiceImpl implements AnalyticsService {

	@Autowired
	private MongoTemplate mongoTemplate;

	Logger logger = Logger.getLogger(AnalyticsServiceImpl.class);

	private Criteria getCriteria(String doctorId, String locationId, String hospitalId) {
		Criteria criteria = new Criteria();

		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			criteria.and("locationId").is(new ObjectId(locationId));
		}
		if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
			criteria.and("hospitalId").is(new ObjectId(hospitalId));
		}
		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			criteria.and("doctorId").is(new ObjectId(doctorId));
		}
		return criteria;

	}

	@Override
	public DoctorVisitAnalyticResponse getVisitAnalytic(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate) {
		DoctorVisitAnalyticResponse data = null;
		try {
			Criteria criteria = null;
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			long date = 0l;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}
			criteria = getCriteria(doctorId, locationId, hospitalId).and("discarded").is(false);

			fromTime = new DateTime(from);
			toTime = new DateTime(to);

			data = new DoctorVisitAnalyticResponse();
			data.setTotalVisit((int) mongoTemplate.count(new Query(criteria), PatientVisitCollection.class));
			criteria.and("adminCreatedTime").gte(fromTime).lte(toTime);
			data.setTotalVisitCreated((int) mongoTemplate.count(new Query(criteria), PatientVisitCollection.class));
			// mongoTemplate.aggregate(aggregation,
			// DoctorClinicProfileCollection.class,
			// DoctorprescriptionAnalyticResponse.class).getUniqueMappedResult();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting visit analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting visit analytic");
		}
		return data;

	}

	@Override
	public Integer countIncomeDetailsAnalyticsData(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String searchTerm) {
		Integer response = 0;
		try {

			Criteria criteria = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;

			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				criteria2.and("patient.locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				criteria2.and("patient.hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
				fromTime = new DateTime(from);
				toTime = new DateTime(to);
				criteria.and("invoiceDate").gte(fromTime).lte(toTime);

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				to = new Date(Long.parseLong(fromDate));
				toTime = new DateTime(to);
				criteria.and("invoiceDate").gte(fromTime);
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				to = new Date(Long.parseLong(toDate));
				toTime = new DateTime(to);
				criteria.and("invoiceDate").lte(toTime);
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria2.orOperator(new Criteria("patient.localPatientName").regex(searchTerm, "i"),
						new Criteria("user.firstName").regex(searchTerm, "i"),
						new Criteria("user.mobileNumber").regex(searchTerm, "i"));
			}
			criteria.and("discarded").is(false);

			response = mongoTemplate
					.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							Aggregation.unwind("patient"), Aggregation.lookup("user_cl", "patientId", "_id", "user"),
							Aggregation.unwind("user"), Aggregation.lookup("user_cl", "doctorId", "_id", "docter"),
							Aggregation.unwind("docter"), Aggregation.match(criteria2)),
							DoctorPatientInvoiceCollection.class, InvoiceAnalyticsDataDetailResponse.class)
					.getMappedResults().size();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While count income analytics data");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While count income analytics data");
		}

		return response;
	}

	@Override
	public List<InvoiceAnalyticsDataDetailResponse> getIncomeDetailsAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String searchTerm, int page, int size) {
		List<InvoiceAnalyticsDataDetailResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;

			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				criteria2.and("patient.locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				criteria2.and("patient.hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
				fromTime = new DateTime(from);
				toTime = new DateTime(to);
				criteria.and("invoiceDate").gte(fromTime).lte(toTime);

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				to = new Date(Long.parseLong(fromDate));
				toTime = new DateTime(to);
				criteria.and("invoiceDate").gte(fromTime);
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				to = new Date(Long.parseLong(toDate));
				toTime = new DateTime(to);
				criteria.and("invoiceDate").lte(toTime);
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria2.orOperator(new Criteria("patient.localPatientName").regex(searchTerm, "i"),
						new Criteria("user.firstName").regex(searchTerm, "i"),
						new Criteria("user.mobileNumber").regex(searchTerm, "i"));
			}
			criteria.and("discarded").is(false);
			AggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id")
							.append("uniqueInvoiceId", new BasicDBObject("$first", "$uniqueInvoiceId"))
							.append("discount", new BasicDBObject("$first", "$resultantDiscount"))
							.append("cost", new BasicDBObject("$first", "$resultantCost"))
							.append("tax", new BasicDBObject("$first", "$resultantTax"))
							.append("invoiceAmount", new BasicDBObject("$first", "$resultantInvoiceAmount"))
							.append("doctorName", new BasicDBObject("$first", "$doctorName"))
							.append("localPatientName", new BasicDBObject("$first", "$localPatientName"))
							.append("firstName", new BasicDBObject("$first", "$firstName"))
							.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
							.append("balanceAmount", new BasicDBObject("$first", "$balanceAmount"))
							.append("date", new BasicDBObject("$first", "$invoiceDate"))));

			if (size > 0) {
				response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
						Aggregation.unwind("patient"), Aggregation.lookup("user_cl", "patientId", "_id", "user"),
						Aggregation.unwind("user"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"), Aggregation.match(criteria2),
						new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
								.append("resultantDiscount",
										new BasicDBObject("$cond",
												new BasicDBObject("if", new BasicDBObject("$eq",
														Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
																.append("then", new BasicDBObject("$multiply",
																		Arrays.asList(new BasicDBObject("$divide",
																				Arrays.asList("$totalDiscount.value",
																						100)),
																				"$totalCost")))
																.append("else", "$totalDiscount.value")))
								.append("resultantCost", "$totalCost").append("invoiceDate", "$invoiceDate")
								.append("resultantTax",
										new BasicDBObject("$cond",
												new BasicDBObject("if", new BasicDBObject("$eq",
														Arrays.asList("$totalTax.unit", UnitType.PERCENT.name())))
																.append("then", new BasicDBObject("$multiply",
																		Arrays.asList(new BasicDBObject("$divide",
																				Arrays.asList("$totalTax.value", 100)),
																				"$totalCost")))
																.append("else", "$totalTax.value")))
								.append("localPatientName", "$patient.localPatientName")
								.append("firstName", "$user.firstName").append("mobileNumber", "$user.mobileNumber")
								.append("doctorName", "$doctor.firstName").append("uniqueInvoiceId", "$uniqueInvoiceId")
								.append("balanceAmount", "$balanceAmount")
								.append("resultantInvoiceAmount", "$grandTotal"))),
						aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.DESC, "invoiceDate")),
						Aggregation.skip((long)(page) * size), Aggregation.limit(size)), DoctorPatientInvoiceCollection.class,
						InvoiceAnalyticsDataDetailResponse.class).getMappedResults();
			} else {
				response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
						Aggregation.unwind("patient"), Aggregation.lookup("user_cl", "patientId", "_id", "user"),
						Aggregation.unwind("user"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"), Aggregation.match(criteria2),
						new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
								.append("resultantDiscount",
										new BasicDBObject("$cond",
												new BasicDBObject("if", new BasicDBObject("$eq",
														Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
																.append("then", new BasicDBObject("$multiply",
																		Arrays.asList(new BasicDBObject("$divide",
																				Arrays.asList("$totalDiscount.value",
																						100)),
																				"$totalCost")))
																.append("else", "$totalDiscount.value")))
								.append("resultantCost", "$totalCost").append("invoiceDate", "$invoiceDate")
								.append("resultantTax",
										new BasicDBObject("$cond",
												new BasicDBObject("if", new BasicDBObject("$eq",
														Arrays.asList("$totalTax.unit", UnitType.PERCENT.name())))
																.append("then", new BasicDBObject("$multiply",
																		Arrays.asList(new BasicDBObject("$divide",
																				Arrays.asList("$totalTax.value", 100)),
																				"$totalCost")))
																.append("else", "$totalTax.value")))
								.append("localPatientName", "$patient.localPatientName")
								.append("firstName", "$user.firstName").append("mobileNumber", "$user.mobileNumber")
								.append("doctorName", "$doctor.firstName").append("uniqueInvoiceId", "$uniqueInvoiceId")
								.append("balanceAmount", "$balanceAmount")
								.append("resultantInvoiceAmount", "$grandTotal"))),
						aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.DESC, "invoiceDate"))),
						DoctorPatientInvoiceCollection.class, InvoiceAnalyticsDataDetailResponse.class)
						.getMappedResults();
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting income analytics data");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting income analytics data");
		}
		return response;

	}

	@Override
	public List<IncomeAnalyticsDataResponse> getIncomeAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page,
			int size) {
		List<IncomeAnalyticsDataResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {

				fromTime = new DateTime(new Date(Long.parseLong(fromDate)));
				toTime = new DateTime(new Date(Long.parseLong(toDate)));
				criteria.and("invoiceDate").gte(fromTime).lte(toTime);

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				fromTime = new DateTime(new Date(Long.parseLong(fromDate)));

				criteria.and("invoiceDate").gte(fromTime);
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				toTime = new DateTime(new Date(Long.parseLong(toDate)));
				criteria.and("invoiceDate").lte(toTime);
			}

			if (!DPDoctorUtils.anyStringEmpty(queryType)) {
				switch (queryType) {
				case "DOCTORS": {
					response = getInvoiceIncomeDataByDoctors(searchType, page, size, criteria);
					break;
				}
				case "SERVICES": {
					response = getInvoiceIncomeDataByServices(searchType, page, size, criteria);
					break;
				}
				case "PATIENTGROUP": {
					response = getInvoiceIncomeDataByPatientGroup(searchType, page, size, fromTime, toTime, doctorId,
							locationId, hospitalId);
					break;
				}
				default: {
					response = getInvoiceIncomeDataByDate(searchType, page, size, criteria);
					break;
				}
			  }
			} else {
				response = getInvoiceIncomeDataByDate(searchType, page, size, criteria);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting invoice income analytics data");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting invoice income analytics data");
		}
		return response;

	}

	private List<IncomeAnalyticsDataResponse> getInvoiceIncomeDataByPatientGroup(String searchType, int page, int size,
			DateTime start, DateTime end, String doctorId, String locationId, String hospitalId) {
		List<IncomeAnalyticsDataResponse> response = null;
		try {
			AggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", new BasicDBObject("groupName", "$groupName"))
							.append("count", new BasicDBObject("$sum", 1))
							.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
							.append("cost", new BasicDBObject("$sum", "$resultantCost"))
							.append("tax", new BasicDBObject("$sum", "$resultantTax"))
							.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
							.append("date", new BasicDBObject("$first", "$invoiceDate"))));

			Criteria criteria = new Criteria();
			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("group.doctorId").is(new ObjectId(doctorId));
				criteria2.and("invoice.doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("group.locationId").is(new ObjectId(locationId));
				criteria2.and("invoice.locationId").is(new ObjectId(locationId));
			}

			if (start != null) {
				criteria2.and("invoice.invoiceDate").gt(start).lte(end);
			}
			criteria2.and("invoice.discarded").is(false);
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("group_cl", "groupId", "_id", "group"), Aggregation.unwind("group"),
					Aggregation.match(criteria),
					Aggregation.lookup("doctor_patient_invoice_cl", "patientId", "patientId", "invoice"),
					Aggregation.unwind("invoice"), Aggregation.match(criteria2),
					Aggregation.unwind("invoice.invoiceItems"),
					new CustomAggregationOperation(new Document("$project", new BasicDBObject("resultantDiscount",
							new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
									Arrays.asList("$invoice.totalDiscount.unit", UnitType.PERCENT.name()))).append(
											"then",
											new BasicDBObject("$multiply", Arrays.asList(
													new BasicDBObject("$divide",
															Arrays.asList("$invoice.totalDiscount.value", 100)),
													"$invoice.totalCost")))
											.append("else", "$invoice.totalDiscount.value")))
													.append("resultantCost", "$invoice.totalCost")
													.append("invoiceDate", "$invoice.invoiceDate")
													.append("resultantTax", new BasicDBObject("$cond",
															new BasicDBObject("if", new BasicDBObject("$eq", Arrays
																	.asList("$invoice.totalTax.unit", UnitType.PERCENT
																			.name()))).append("then", new BasicDBObject(
																					"$multiply",
																					Arrays.asList(new BasicDBObject(
																							"$divide", Arrays.asList(
																									"$invoice.totalTax.value",
																									100)),
																							"$invoice.totalCost")))
																					.append("else",
																							"$invoice.totalTax.value")))
													.append("resultantInvoiceAmount", "$invoice.grandTotal")
													.append("groupId", "$group._id")
													.append("groupName", "$group.name"))),
					aggregationOperation);
			// , Aggregation.skip(page * size), Aggregation.limit(size)
			// new CustomAggregationOperation(new Document("$group", new
			// BasicDBObject("_id", new BasicDBObject("groupName",
			// "$group.name"))
			// .append("count", new BasicDBObject("$sum", 1)))));

			response = mongoTemplate
					.aggregate(aggregation, PatientGroupCollection.class, IncomeAnalyticsDataResponse.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting income analytics data by patient group");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting income analytics data by patient group");
		}
		return response;
	}

	private List<IncomeAnalyticsDataResponse> getInvoiceIncomeDataByServices(String searchType, long page, int size,
			Criteria criteria) {
		List<IncomeAnalyticsDataResponse> response = null;
		criteria.and("discarded").is(false);
		AggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", new BasicDBObject("itemId", "$itemId"))
						.append("serviceName", new BasicDBObject("$first", "$serviceName"))
						.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
						.append("cost", new BasicDBObject("$sum", "$resultantCost"))
						.append("tax", new BasicDBObject("$sum", "$resultantTax"))
						.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
						.append("date", new BasicDBObject("$first", "$invoiceDate"))));

		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("invoiceItems"),
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("resultantDiscount",
									new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then", new BasicDBObject("$multiply",
															Arrays.asList(new BasicDBObject("$divide",
																	Arrays.asList("$totalDiscount.value", 100)),
																	"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$totalTax.unit",
																					UnitType.PERCENT.name()))).append(
																							"then",
																							new BasicDBObject(
																									"$multiply",
																									Arrays.asList(
																											new BasicDBObject(
																													"$divide",
																													Arrays.asList(
																															"$totalTax.value",
																															100)),
																											"$totalCost")))
																							.append("else",
																									"$totalTax.value")))
															.append("resultantInvoiceAmount", "$grandTotal")
															.append("itemId", "$invoiceItems.itemId")
															.append("serviceName", "$invoiceItems.name"))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "invoiceDate"),
					Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("invoiceItems"),
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("resultantDiscount",
									new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then", new BasicDBObject("$multiply",
															Arrays.asList(new BasicDBObject("$divide",
																	Arrays.asList("$totalDiscount.value", 100)),
																	"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$totalTax.unit",
																					UnitType.PERCENT.name()))).append(
																							"then",
																							new BasicDBObject(
																									"$multiply",
																									Arrays.asList(
																											new BasicDBObject(
																													"$divide",
																													Arrays.asList(
																															"$totalTax.value",
																															100)),
																											"$totalCost")))
																							.append("else",
																									"$totalTax.value")))
															.append("resultantInvoiceAmount", "$grandTotal")
															.append("itemId", "$invoiceItems.itemId")
															.append("serviceName", "$invoiceItems.name"))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "invoiceDate"));
		}
		response = mongoTemplate
				.aggregate(aggregation, DoctorPatientInvoiceCollection.class, IncomeAnalyticsDataResponse.class)
				.getMappedResults();
		return response;

	}

	private List<IncomeAnalyticsDataResponse> getInvoiceIncomeDataByDoctors(String searchType, long page, int size,
			Criteria criteria) {
		List<IncomeAnalyticsDataResponse> response = null;
		criteria.and("discarded").is(false);
		AggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", new BasicDBObject("doctorId", "$doctorId"))
						.append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("title", new BasicDBObject("$first", "$title"))
						.append("doctorName", new BasicDBObject("$first", "$doctorName"))
						.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
						.append("cost", new BasicDBObject("$sum", "$resultantCost"))
						.append("tax", new BasicDBObject("$sum", "$resultantTax"))
						.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
						.append("date", new BasicDBObject("$first", "$invoiceDate"))));

		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("resultantDiscount",
									new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then", new BasicDBObject("$multiply",
															Arrays.asList(new BasicDBObject("$divide",
																	Arrays.asList("$totalDiscount.value", 100)),
																	"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$totalTax.unit",
																					UnitType.PERCENT.name()))).append(
																							"then",
																							new BasicDBObject(
																									"$multiply",
																									Arrays.asList(
																											new BasicDBObject(
																													"$divide",
																													Arrays.asList(
																															"$totalTax.value",
																															100)),
																											"$totalCost")))
																							.append("else",
																									"$totalTax.value")))
															.append("resultantInvoiceAmount", "$grandTotal")
															.append("doctorId", "$doctorId")
															.append("title", "$user.title")
															.append("doctorName", "$user.firstName"))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "invoiceDate"),
					Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("resultantDiscount",
									new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then", new BasicDBObject("$multiply",
															Arrays.asList(new BasicDBObject("$divide",
																	Arrays.asList("$totalDiscount.value", 100)),
																	"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$totalTax.unit",
																					UnitType.PERCENT.name()))).append(
																							"then",
																							new BasicDBObject(
																									"$multiply",
																									Arrays.asList(
																											new BasicDBObject(
																													"$divide",
																													Arrays.asList(
																															"$totalTax.value",
																															100)),
																											"$totalCost")))
																							.append("else",
																									"$totalTax.value")))
															.append("doctorId", "$doctorId")
															.append("title", "$user.title")
															.append("doctorName", "$user.firstName"))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "invoiceDate"));
		}
		response = mongoTemplate
				.aggregate(aggregation, DoctorPatientInvoiceCollection.class, IncomeAnalyticsDataResponse.class)
				.getMappedResults();
		return response;

	}

	private List<IncomeAnalyticsDataResponse> getInvoiceIncomeDataByDate(String searchType, long page, int size,
			Criteria criteria) {
		criteria.and("discarded").is(false);
		List<IncomeAnalyticsDataResponse> response = null;
		AggregationOperation aggregationOperation = null;
		if (!DPDoctorUtils.anyStringEmpty(searchType))
			switch (SearchType.valueOf(searchType.toUpperCase())) {

			case DAILY: {
				aggregationOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id",
								new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
										.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
										.append("cost", new BasicDBObject("$sum", "$resultantCost"))
										.append("tax", new BasicDBObject("$sum", "$resultantTax"))
										.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
										.append("date", new BasicDBObject("$first", "$invoiceDate"))));

				break;
			}

			case WEEKLY: {
				aggregationOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id",
								new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year"))
										.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
										.append("cost", new BasicDBObject("$sum", "$resultantCost"))
										.append("tax", new BasicDBObject("$sum", "$resultantTax"))
										.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
										.append("date", new BasicDBObject("$first", "$invoiceDate"))));

				break;
			}

			case MONTHLY: {
				aggregationOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
								.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
								.append("cost", new BasicDBObject("$sum", "$resultantCost"))
								.append("tax", new BasicDBObject("$sum", "$resultantTax"))
								.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
								.append("date", new BasicDBObject("$first", "$invoiceDate"))));
				break;
			}
			case YEARLY: {
				aggregationOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id", new BasicDBObject("year", "$year"))
								.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
								.append("cost", new BasicDBObject("$sum", "$resultantCost"))
								.append("tax", new BasicDBObject("$sum", "$resultantTax"))
								.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
								.append("date", new BasicDBObject("$first", "$invoiceDate"))));

				break;

			}
			default:
				break;
			}
		else {
			aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
									.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
									.append("cost", new BasicDBObject("$sum", "$resultantCost"))
									.append("tax", new BasicDBObject("$sum", "$resultantTax"))
									.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
									.append("date", new BasicDBObject("$first", "$invoiceDate"))));
		}

		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("resultantDiscount",
									new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then", new BasicDBObject("$multiply",
															Arrays.asList(new BasicDBObject("$divide",
																	Arrays.asList("$totalDiscount.value", 100)),
																	"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$totalTax.unit",
																					UnitType.PERCENT.name()))).append(
																							"then",
																							new BasicDBObject(
																									"$multiply",
																									Arrays.asList(
																											new BasicDBObject(
																													"$divide",
																													Arrays.asList(
																															"$totalTax.value",
																															100)),
																											"$totalCost")))
																							.append("else",
																									"$totalTax.value")))
															.append("resultantInvoiceAmount", "$grandTotal")
															.append("day",
																	new BasicDBObject("$dayOfMonth", "$invoiceDate"))
															.append("month",
																	new BasicDBObject("$month", "$invoiceDate"))
															.append("year", new BasicDBObject("$year", "$invoiceDate"))
															.append("week",
																	new BasicDBObject("$week", "$invoiceDate")))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "invoiceDate"),
					Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("resultantDiscount",
									new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then", new BasicDBObject("$multiply",
															Arrays.asList(new BasicDBObject("$divide",
																	Arrays.asList("$totalDiscount.value", 100)),
																	"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$totalTax.unit",
																					UnitType.PERCENT.name()))).append(
																							"then",
																							new BasicDBObject(
																									"$multiply",
																									Arrays.asList(
																											new BasicDBObject(
																													"$divide",
																													Arrays.asList(
																															"$totalTax.value",
																															100)),
																											"$totalCost")))
																							.append("else",
																									"$totalTax.value")))
															.append("resultantInvoiceAmount", "$grandTotal")
															.append("day",
																	new BasicDBObject("$dayOfMonth", "$invoiceDate"))
															.append("month",
																	new BasicDBObject("$month", "$invoiceDate"))
															.append("year", new BasicDBObject("$year", "$invoiceDate"))
															.append("week",
																	new BasicDBObject("$week", "$invoiceDate")))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "invoiceDate"));
		}
		response = mongoTemplate
				.aggregate(aggregation, DoctorPatientInvoiceCollection.class, IncomeAnalyticsDataResponse.class)
				.getMappedResults();
		return response;
	}

	@Override
	public List<PaymentDetailsAnalyticsDataResponse> getPaymentDetailsAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String searchTerm, String paymentMode, int page,
			int size) {
		List<PaymentDetailsAnalyticsDataResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			Criteria criteria2 = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				criteria2.and("patient.locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				criteria2.and("patient.hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
				fromTime = new DateTime(from);
				toTime = new DateTime(to);
				criteria.and("receivedDate").gte(fromTime).lte(toTime);

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				to = new Date(Long.parseLong(fromDate));
				toTime = new DateTime(to);
				criteria.and("receivedDate").gte(fromTime);
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				to = new Date(Long.parseLong(toDate));
				toTime = new DateTime(to);
				criteria.and("receivedDate").lte(toTime);
			}
			criteria.and("discarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria2.orOperator(new Criteria("patient.localPatientName").regex(searchTerm, "i"),
						new Criteria("user.firstName").regex(searchTerm, "i"),
						new Criteria("user.mobileNumber").regex(searchTerm, "i"));
			}

			if (!DPDoctorUtils.anyStringEmpty(paymentMode)) {
				criteria.and("modeOfPayment").is(paymentMode.toUpperCase());
			}

			if (size > 0) {
				response = mongoTemplate
						.aggregate(
								Aggregation
										.newAggregation(Aggregation.match(criteria),
												Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
												Aggregation.unwind("patient"),
												Aggregation.lookup("user_cl", "patientId", "_id", "user"),
												Aggregation.unwind("user"),
												Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
												Aggregation.unwind("doctor"), Aggregation.match(criteria2),
												new CustomAggregationOperation(
														new Document("$group",
																new BasicDBObject("_id", "$_id")
																		.append("date",
																				new BasicDBObject("$first",
																						"$receivedDate"))
																		.append("patientName",
																				new BasicDBObject("$first",
																						"$patient.localPatientName"))
																		.append("uniqueReceiptId",
																				new BasicDBObject("$first",
																						"$uniqueReceiptId"))
																		.append("uniqueInvoiceId",
																				new BasicDBObject("$first",
																						"$uniqueInvoiceId"))
																		.append("amountPaid",
																				new BasicDBObject("$first",
																						"$amountPaid"))
																		.append("modeOfPayment",
																				new BasicDBObject("$first",
																						"$modeOfPayment"))
																		.append("receiptType",
																				new BasicDBObject("$first",
																						"$receiptType"))
																		.append("localPatientName",
																				new BasicDBObject("$first",
																						"$patient.localPatientName"))
																		.append("firstName",
																				new BasicDBObject("$first",
																						"$user.firstName"))
																		.append("mobileNumber",
																				new BasicDBObject("$first",
																						"$user.mobileNumber"))
																		.append("doctorName",
																				new BasicDBObject("$first",
																						"$doctor.firstName"))
																		.append("usedAdvanceAmount",
																				new BasicDBObject("$first",
																						"$usedAdvanceAmount")))),
												Aggregation.sort(new Sort(Sort.Direction.DESC, "receivedDate")),
												Aggregation.skip((page) * size), Aggregation.limit(size)),
								DoctorPatientReceiptCollection.class, PaymentDetailsAnalyticsDataResponse.class)
						.getMappedResults();
			} else {
				response = mongoTemplate
						.aggregate(
								Aggregation
										.newAggregation(Aggregation.match(criteria),
												Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
												Aggregation.unwind("patient"),
												Aggregation.lookup("user_cl", "patientId", "_id", "user"),
												Aggregation.unwind("user"),
												Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
												Aggregation.unwind("doctor"), Aggregation.match(criteria2),
												new CustomAggregationOperation(
														new Document("$group",
																new BasicDBObject("_id", "$_id")
																		.append("date",
																				new BasicDBObject("$first",
																						"$receivedDate"))
																		.append("patientName",
																				new BasicDBObject("$first",
																						"$patient.localPatientName"))
																		.append("uniqueReceiptId",
																				new BasicDBObject("$first",
																						"$uniqueReceiptId"))
																		.append("uniqueInvoiceId",
																				new BasicDBObject("$first",
																						"$uniqueInvoiceId"))
																		.append("amountPaid",
																				new BasicDBObject("$first",
																						"$amountPaid"))
																		.append("modeOfPayment",
																				new BasicDBObject("$first",
																						"$modeOfPayment"))
																		.append("receiptType",
																				new BasicDBObject("$first",
																						"$receiptType"))
																		.append("localPatientName",
																				new BasicDBObject("$first",
																						"$patient.localPatientName"))
																		.append("firstName",
																				new BasicDBObject("$first",
																						"$user.firstName"))
																		.append("mobileNumber",
																				new BasicDBObject("$first",
																						"$user.mobileNumber"))
																		.append("doctorName",
																				new BasicDBObject("$first",
																						"$doctor.firstName"))
																		.append("usedAdvanceAmount",
																				new BasicDBObject("$first",
																						"$usedAdvanceAmount")))),
												Aggregation.sort(new Sort(Sort.Direction.DESC, "receivedDate"))),
								DoctorPatientReceiptCollection.class, PaymentDetailsAnalyticsDataResponse.class)
						.getMappedResults();

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting payment details analytics data");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting payment details analytics data");
		}
		return response;
	}

	@Override
	public Integer countPaymentDetailsAnalyticsData(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String searchTerm, String paymentMode) {
		Integer response = 0;
		try {
			Criteria criteria = new Criteria();

			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				criteria2.and("patient.locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				criteria2.and("patient.hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {

				criteria.and("receivedDate").gte(new DateTime(new Date(Long.parseLong(fromDate))))
						.lte(new DateTime(new Date(Long.parseLong(toDate))));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria.and("receivedDate").gte(new DateTime(new Date(Long.parseLong(fromDate))));
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {

				criteria.and("receivedDate").lte(new DateTime(new Date(Long.parseLong(toDate))));
			}
			criteria.and("discarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria2.orOperator(new Criteria("patient.localPatientName").regex(searchTerm, "i"),
						new Criteria("user.firstName").regex(searchTerm, "i"),
						new Criteria("user.mobileNumber").regex(searchTerm, "i"));
			}

			if (!DPDoctorUtils.anyStringEmpty(paymentMode)) {
				criteria.and("modeOfPayment").is(paymentMode.toUpperCase());
			}
			response = mongoTemplate
					.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteria),
									Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
									Aggregation.unwind("patient"),
									Aggregation.lookup("user_cl", "patientId", "_id", "user"),
									Aggregation.unwind("user"),
									Aggregation.lookup("user_cl", "doctorId", "_id", "docter"),
									Aggregation.unwind("docter"), Aggregation.match(criteria2),
									new CustomAggregationOperation(
											new Document("$group", new BasicDBObject("_id", "$_id")))),
							DoctorPatientReceiptCollection.class, PaymentDetailsAnalyticsDataResponse.class)
					.getMappedResults().size();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While count payment details analytics data");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While count payment details analytics data");
		}
		return response;
	}

	@Override
	public List<PaymentAnalyticsDataResponse> getPaymentAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page,
			int size) {
		List<PaymentAnalyticsDataResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
				fromTime = new DateTime(from);
				toTime = new DateTime(to);
				criteria.and("receivedDate").gte(fromTime).lte(toTime);

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				fromTime = new DateTime(from);
				criteria.and("receivedDate").gte(fromTime);
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				to = new Date(Long.parseLong(toDate));
				toTime = new DateTime(to);
				criteria.and("receivedDate").lte(toTime);
			}
			switch (queryType) {
			case "DOCTORS": {
				response = getPaymentDataByDoctors(page, size, criteria);
				break;
			}
			case "PAYMENTMODES": {
				response = getPaymentDataByPaymentModes(page, size, criteria);
				break;
			}
			default: {
				response = getPaymentByDate(searchType, page, size, criteria);
				break;
			}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting payment analytics data");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting payment analytics data");
		}
		return response;
	}

	private List<PaymentAnalyticsDataResponse> getPaymentDataByDoctors(int page, int size, Criteria criteria) {
		criteria.and("discarded").is(false);
		List<PaymentAnalyticsDataResponse> response = null;
		AggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", new BasicDBObject("doctorId", "$doctorId"))
						.append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("title", new BasicDBObject("$first", "$title"))
						.append("doctorName", new BasicDBObject("$first", "$doctorName"))
						.append("cash", new BasicDBObject("$sum", "$cash"))
						.append("card", new BasicDBObject("$sum", "$card"))
						.append("online", new BasicDBObject("$sum", "$online"))
						.append("wallet", new BasicDBObject("$sum", "$wallet"))
						.append("total", new BasicDBObject("$sum", "$total"))
						.append("date", new BasicDBObject("$first", "$receivedDate"))));

		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
							new CustomAggregationOperation(new Document("$project",
									new BasicDBObject("cash",
											new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
													Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name()))).append(
															"then", "$amountPaid")
															.append("else", 0))).append("card", new BasicDBObject(
																	"$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.CARD.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
																	.append("online", new BasicDBObject("$cond",
																			new BasicDBObject("if", new BasicDBObject(
																					"$eq",
																					Arrays.asList("$modeOfPayment",
																							ModeOfPayment.ONLINE
																									.name()))).append(
																											"then",
																											"$amountPaid")
																											.append("else",
																													0)))
																	.append("wallet", new BasicDBObject("$cond",
																			new BasicDBObject("if", new BasicDBObject(
																					"$eq",
																					Arrays.asList("$modeOfPayment",
																							ModeOfPayment.WALLET
																									.name()))).append(
																											"then",
																											"$amountPaid")
																											.append("else",
																													0)))
																	.append("total", "$amountPaid")
																	.append("doctorId", "$doctorId")
																	.append("date", "$receivedDate")
																	.append("title", "$user.title")
																	.append("doctorName", "$user.firstName"))),
							aggregationOperation, Aggregation.sort(Direction.DESC, "receivedDate"),
							Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
							new CustomAggregationOperation(new Document("$project",
									new BasicDBObject("cash",
											new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
													Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name()))).append(
															"then", "$amountPaid")
															.append("else", 0))).append("card", new BasicDBObject(
																	"$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.CARD.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
																	.append("online", new BasicDBObject("$cond",
																			new BasicDBObject("if", new BasicDBObject(
																					"$eq",
																					Arrays.asList("$modeOfPayment",
																							ModeOfPayment.ONLINE
																									.name()))).append(
																											"then",
																											"$amountPaid")
																											.append("else",
																													0)))
																	.append("wallet", new BasicDBObject("$cond",
																			new BasicDBObject("if", new BasicDBObject(
																					"$eq",
																					Arrays.asList("$modeOfPayment",
																							ModeOfPayment.WALLET
																									.name()))).append(
																											"then",
																											"$amountPaid")
																											.append("else",
																													0)))
																	.append("total", "$amountPaid")
																	.append("doctorId", "$doctorId")
																	.append("receivedDate", "$receivedDate")
																	.append("title", "$user.title")
																	.append("doctorName", "$user.firstName"))),
							aggregationOperation, Aggregation.sort(Direction.DESC, "receivedDate"));
		}
		response = mongoTemplate
				.aggregate(aggregation, DoctorPatientReceiptCollection.class, PaymentAnalyticsDataResponse.class)
				.getMappedResults();
		return response;
	}

	private List<PaymentAnalyticsDataResponse> getPaymentDataByPaymentModes(int page, int size, Criteria criteria) {
		List<PaymentAnalyticsDataResponse> response = null;
		AggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", new BasicDBObject("modeOfPayment", "$modeOfPayment"))
						.append("modeOfPayment", new BasicDBObject("$first", "$modeOfPayment"))
						.append("total", new BasicDBObject("$sum", "$total"))));

		Aggregation aggregation = null;
		criteria.and("discarded").is(false);
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("total", "$amountPaid").append("modeOfPayment", "$modeOfPayment"))),
					aggregationOperation, Aggregation.sort(Direction.ASC, "modeOfPayment"),
					Aggregation.skip((long)page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("total", "$amountPaid").append("modeOfPayment", "$modeOfPayment"))),
					aggregationOperation, Aggregation.sort(Direction.ASC, "modeOfPayment"));
		}
		response = mongoTemplate
				.aggregate(aggregation, DoctorPatientReceiptCollection.class, PaymentAnalyticsDataResponse.class)
				.getMappedResults();
		return response;

	}

	// this service uses for get payment mode info
	private List<PaymentAnalyticsDataResponse> getPaymentByDate(String searchType, int page, int size,
			Criteria criteria) {
		List<PaymentAnalyticsDataResponse> response = null;
		AggregationOperation aggregationOperation = null;
		if (!DPDoctorUtils.anyStringEmpty(searchType))
			switch (SearchType.valueOf(searchType.toUpperCase())) {

			case DAILY: {
				aggregationOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id",
								new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
										.append("cash", new BasicDBObject("$sum", "$cash"))
										.append("card", new BasicDBObject("$sum", "$card"))
										.append("online", new BasicDBObject("$sum", "$online"))
										.append("wallet", new BasicDBObject("$sum", "$wallet"))
										.append("total", new BasicDBObject("$sum", "$total"))
										.append("date", new BasicDBObject("$first", "$receivedDate"))));

				break;
			}

			case WEEKLY: {
				aggregationOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id",
								new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year"))
										.append("cash", new BasicDBObject("$sum", "$cash"))
										.append("card", new BasicDBObject("$sum", "$card"))
										.append("online", new BasicDBObject("$sum", "$online"))
										.append("wallet", new BasicDBObject("$sum", "$wallet"))
										.append("total", new BasicDBObject("$sum", "$total"))
										.append("date", new BasicDBObject("$first", "$receivedDate"))));

				break;
			}

			case MONTHLY: {
				aggregationOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
								.append("cash", new BasicDBObject("$sum", "$cash"))
								.append("card", new BasicDBObject("$sum", "$card"))
								.append("online", new BasicDBObject("$sum", "$online"))
								.append("wallet", new BasicDBObject("$sum", "$wallet"))
								.append("total", new BasicDBObject("$sum", "$total"))
								.append("date", new BasicDBObject("$first", "$receivedDate"))));
				break;
			}
			case YEARLY: {
				aggregationOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id", new BasicDBObject("year", "$year"))
								.append("cash", new BasicDBObject("$sum", "$cash"))
								.append("card", new BasicDBObject("$sum", "$card"))
								.append("online", new BasicDBObject("$sum", "$online"))
								.append("wallet", new BasicDBObject("$sum", "$wallet"))
								.append("total", new BasicDBObject("$sum", "$total"))
								.append("date", new BasicDBObject("$first", "$receivedDate"))));

				break;

			}
			default:
				break;
			}
		else {
			aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
									.append("cash", new BasicDBObject("$sum", "$cash"))
									.append("card", new BasicDBObject("$sum", "$card"))
									.append("online", new BasicDBObject("$sum", "$online"))
									.append("wallet", new BasicDBObject("$sum", "$wallet"))
									.append("total", new BasicDBObject("$sum", "$total"))
									.append("date", new BasicDBObject("$first", "$receivedDate"))));
		}

		Aggregation aggregation = null;
		criteria.and("discarded").is(false);
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), new CustomAggregationOperation(
					new Document("$project", new BasicDBObject("cash", new BasicDBObject("$cond",
							new BasicDBObject("if",
									new BasicDBObject("$eq",
											Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name())))
													.append("then", "$amountPaid").append("else", 0))).append(
															"card",
															new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.CARD.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
															.append("online", new BasicDBObject(
																	"$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.ONLINE.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
															.append("wallet", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.WALLET.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
															.append("day", "$receivedDate")
															.append("day",
																	new BasicDBObject("$dayOfMonth", "$receivedDate"))
															.append("month",
																	new BasicDBObject("$month", "$receivedDate"))
															.append("year", new BasicDBObject("$year", "$receivedDate"))
															.append("week",
																	new BasicDBObject("$week", "$receivedDate")))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "receivedDate"),
					Aggregation.skip((long)page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), new CustomAggregationOperation(
					new Document("$project", new BasicDBObject("cash", new BasicDBObject("$cond",
							new BasicDBObject("if",
									new BasicDBObject("$eq",
											Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name())))
													.append("then", "$amountPaid").append("else", 0))).append(
															"card",
															new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.CARD.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
															.append("online", new BasicDBObject(
																	"$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.ONLINE.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
															.append("wallet", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.WALLET.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
															.append("receivedDate", "$receivedDate")
															.append("day",
																	new BasicDBObject("$dayOfMonth", "$receivedDate"))
															.append("month",
																	new BasicDBObject("$month", "$receivedDate"))
															.append("year", new BasicDBObject("$year", "$receivedDate"))
															.append("week",
																	new BasicDBObject("$week", "$receivedDate")))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "receivedDate"));
		}
		response = mongoTemplate
				.aggregate(aggregation, DoctorPatientReceiptCollection.class, PaymentAnalyticsDataResponse.class)
				.getMappedResults();
		return response;
	}

	@Override
	public List<AmountDueAnalyticsDataResponse> getAmountDueAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page,
			int size) {
		List<AmountDueAnalyticsDataResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			Criteria criteria2 = new Criteria();
			Criteria criteria3 = new Criteria();
			Criteria criteria4 = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
				criteria3.and("invoice.doctorId").is(new ObjectId(doctorId));
				criteria4.and("receipt.doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				criteria2.and("patient.locationId").is(new ObjectId(locationId)).and("patient.discarded").is(false);
				criteria3.and("invoice.locationId").is(new ObjectId(locationId)).and("invoice.discarded").is(false);
				criteria4.and("receipt.locationId").is(new ObjectId(locationId)).and("receipt.discarded").is(false);
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				criteria2.and("patient.hospitalId").is(new ObjectId(hospitalId));
				criteria3.and("invoice.hospitalId").is(new ObjectId(hospitalId));
				criteria4.and("receipt.hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
				fromTime = new DateTime(from);
				toTime = new DateTime(to);
				criteria4.and("receipt.receivedDate").gte(fromTime).lte(toTime);
				criteria3.and("invoice.invoiceDate").gte(fromTime).lte(toTime);

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				fromTime = new DateTime(from);
				criteria4.and("receipt.receivedDate").gte(fromTime);
				criteria3.and("invoice.invoiceDate").gte(fromTime);

			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				to = new Date(Long.parseLong(toDate));
				toTime = new DateTime(to);
				criteria4.and("receipt.receivedDate").lte(toTime);
				criteria3.and("invoice.invoiceDate").lte(toTime);
			}

			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(queryType)) {
				if (queryType.equalsIgnoreCase("PATIENT")) {
					if (size > 0) {
						aggregation = Aggregation
								.newAggregation(Aggregation.match(criteria),
										Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
										Aggregation.unwind("patient"), Aggregation.match(criteria2),

										new CustomAggregationOperation(new Document("$group",
												new BasicDBObject("_id", "$patientId")

														.append("patientName",
																new BasicDBObject("$first",
																		"$patient.localPatientName"))
														.append("pid", new BasicDBObject("$first", "$patient.PID"))
														.append("patientId", new BasicDBObject("$first", "$patientId"))
														.append("doctorId", new BasicDBObject("$first", "$doctorId"))
														.append("dueAmount", new BasicDBObject("$sum", "$dueAmount")))),
										Aggregation.lookup(
												"doctor_patient_invoice_cl", "patientId", "patientId", "invoice"),
										Aggregation.unwind("invoice"), Aggregation.match(criteria3),
										new CustomAggregationOperation(
												new Document("$group",
														new BasicDBObject("_id", "$patientId")
																.append("invoiced",
																		new BasicDBObject("$sum",
																				"$invoice.grandTotal"))
																.append("patientName",
																		new BasicDBObject("$first", "$patientName"))
																.append("pid", new BasicDBObject("$first", "$pid"))
																.append("patientId",
																		new BasicDBObject("$first", "$patientId"))
																.append("doctorId",
																		new BasicDBObject("$first", "$doctorId"))
																.append("dueAmount",
																		new BasicDBObject("$first", "$dueAmount")))),
										Aggregation.lookup(
												"doctor_patient_receipt_cl", "patientId", "patientId", "receipt"),
										Aggregation.unwind("receipt"), Aggregation.match(criteria4),

										new CustomAggregationOperation(new Document("$group",
												new BasicDBObject("_id", "$patientId")
														.append("invoiced", new BasicDBObject("$first", "$invoiced"))
														.append("received",
																new BasicDBObject("$sum", "$receipt.amountPaid"))
														.append("amountDue", new BasicDBObject("$first", "$dueAmount"))
														.append("dueAmount", new BasicDBObject("$first", "$dueAmount"))
														.append("patientName",
																new BasicDBObject("$first", "$patientName"))
														.append("pid", new BasicDBObject("$first", "$pid")))),
										Aggregation.sort(Direction.DESC, "dueAmount"), Aggregation.skip(page * size),
										Aggregation.limit(size));
					} else {
						aggregation = Aggregation
								.newAggregation(Aggregation.match(criteria),
										Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
										Aggregation.unwind("patient"), Aggregation.match(criteria2),

										new CustomAggregationOperation(new Document("$group",
												new BasicDBObject("_id", "$patientId")

														.append("patientName",
																new BasicDBObject("$first",
																		"$patient.localPatientName"))
														.append("pid", new BasicDBObject("$first", "$patient.PID"))
														.append("patientId", new BasicDBObject("$first", "$patientId"))
														.append("doctorId", new BasicDBObject("$first", "$doctorId"))
														.append("dueAmount", new BasicDBObject("$sum", "$dueAmount")))),
										Aggregation.lookup(
												"doctor_patient_invoice_cl", "patientId", "patientId", "invoice"),
										Aggregation.unwind("invoice"), Aggregation.match(criteria3),
										new CustomAggregationOperation(
												new Document("$group",
														new BasicDBObject("_id", "$patientId")
																.append("invoiced",
																		new BasicDBObject("$sum",
																				"$invoice.grandTotal"))
																.append("patientName",
																		new BasicDBObject("$first", "$patientName"))
																.append("pid", new BasicDBObject("$first", "$pid"))
																.append("patientId",
																		new BasicDBObject("$first", "$patientId"))
																.append("doctorId",
																		new BasicDBObject("$first", "$doctorId"))
																.append("dueAmount",
																		new BasicDBObject("$first", "$dueAmount")))),
										Aggregation.lookup(
												"doctor_patient_receipt_cl", "patientId", "patientId", "receipt"),
										Aggregation.unwind("receipt"), Aggregation.match(criteria4),

										new CustomAggregationOperation(new Document("$group",
												new BasicDBObject("_id", "$patientId")
														.append("invoiced", new BasicDBObject("$first", "$invoiced"))
														.append("received",
																new BasicDBObject("$sum", "$receipt.amountPaid"))
														.append("amountDue", new BasicDBObject("$first", "$dueAmount"))
														.append("dueAmount", new BasicDBObject("$first", "$dueAmount"))
														.append("patientName",
																new BasicDBObject("$first", "$patientName"))
														.append("pid", new BasicDBObject("$first", "$pid")))),
										Aggregation.sort(Direction.DESC, "dueAmount"));
					}
				} else if (queryType.equalsIgnoreCase("DOCTORS")) {
					if (size > 0) {
						aggregation = Aggregation
								.newAggregation(Aggregation.match(criteria),
										Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
										Aggregation.unwind("doctor"),
										new CustomAggregationOperation(
												new Document("$group",
														new BasicDBObject("_id", "$doctorId")
																.append("doctorName",
																		new BasicDBObject("$first",
																				"$doctor.firstName"))
																.append("doctorId",
																		new BasicDBObject("$first", "$doctorId"))
																.append("locationId",
																		new BasicDBObject("$first", "$locationId"))
																.append("hospitalId",
																		new BasicDBObject("$first", "$hospitalId"))
																.append("dueAmount",
																		new BasicDBObject("$sum", "$dueAmount")))),
										Aggregation
												.lookup("doctor_patient_invoice_cl", "doctorId", "doctorId", "invoice"),
										Aggregation.unwind("invoice"), Aggregation.match(criteria3),
										new CustomAggregationOperation(
												new Document("$group",
														new BasicDBObject("_id", "$doctorId")
																.append("invoiced",
																		new BasicDBObject("$sum",
																				"$invoice.grandTotal"))
																.append("doctorName",
																		new BasicDBObject("$first", "$doctorName"))
																.append("doctorId",
																		new BasicDBObject("$first", "$doctorId"))
																.append("locationId",
																		new BasicDBObject("$first", "$locationId"))
																.append("hospitalId",
																		new BasicDBObject("$first", "$hospitalId"))
																.append("dueAmount",
																		new BasicDBObject("$first", "$dueAmount")))),
										Aggregation
												.lookup("doctor_patient_receipt_cl", "doctorId", "doctorId", "receipt"),
										Aggregation.unwind("receipt"), Aggregation.match(criteria4),
										new CustomAggregationOperation(new Document("$group",
												new BasicDBObject("_id", "$doctorId")
														.append("invoiced", new BasicDBObject("$first", "$invoiced"))
														.append("received",
																new BasicDBObject("$sum", "$receipt.amountPaid"))
														.append("amountDue", new BasicDBObject("$first", "$dueAmount"))
														.append("dueAmount", new BasicDBObject("$first", "$dueAmount"))
														.append("doctorName",
																new BasicDBObject("$first", "$doctorName")))),
										Aggregation.sort(Direction.DESC, "dueAmount"), Aggregation.skip(page * size),
										Aggregation.limit(size));
					} else {
						aggregation = Aggregation
								.newAggregation(Aggregation.match(criteria),
										Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
										Aggregation.unwind("doctor"),
										new CustomAggregationOperation(
												new Document("$group",
														new BasicDBObject("_id", "$doctorId")
																.append("doctorName",
																		new BasicDBObject("$first",
																				"$doctor.firstName"))
																.append("doctorId",
																		new BasicDBObject("$first", "$doctorId"))
																.append("locationId",
																		new BasicDBObject("$first", "$locationId"))
																.append("hospitalId",
																		new BasicDBObject("$first", "$hospitalId"))
																.append("dueAmount",
																		new BasicDBObject("$sum", "$dueAmount")))),
										Aggregation
												.lookup("doctor_patient_invoice_cl", "doctorId", "doctorId", "invoice"),
										Aggregation.unwind("invoice"), Aggregation.match(criteria3),
										new CustomAggregationOperation(
												new Document("$group",
														new BasicDBObject("_id", "$doctorId")
																.append("invoiced",
																		new BasicDBObject("$sum",
																				"$invoice.grandTotal"))
																.append("doctorName",
																		new BasicDBObject("$first", "$doctorName"))
																.append("doctorId",
																		new BasicDBObject("$first", "$doctorId"))
																.append("locationId",
																		new BasicDBObject("$first", "$locationId"))
																.append("hospitalId",
																		new BasicDBObject("$first", "$hospitalId"))
																.append("dueAmount",
																		new BasicDBObject("$first", "$dueAmount")))),
										Aggregation
												.lookup("doctor_patient_receipt_cl", "doctorId", "doctorId", "receipt"),
										Aggregation.unwind("receipt"), Aggregation.match(criteria4),
										new CustomAggregationOperation(new Document("$group",
												new BasicDBObject("_id", "$doctorId")
														.append("invoiced", new BasicDBObject("$first", "$invoiced"))
														.append("received",
																new BasicDBObject("$sum", "$receipt.amountPaid"))
														.append("amountDue", new BasicDBObject("$first", "$dueAmount"))
														.append("dueAmount", new BasicDBObject("$first", "$dueAmount"))
														.append("doctorName",
																new BasicDBObject("$first", "$doctorName")))),
										Aggregation.sort(Direction.DESC, "dueAmount"));
					}
				}
			} else {
				throw new BusinessException(ServiceError.Unknown, "Query Type cannot be null");
			}

			response = mongoTemplate.aggregate(aggregation, DoctorPatientDueAmountCollection.class,
					AmountDueAnalyticsDataResponse.class).getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting amount due analytics data");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting amount due analytics data");
		}
		return response;
	}

	@Override
	public List<ExpenseCountResponse> getDoctorExpenseAnalytic(String doctorId, String searchType, String locationId,
			String hospitalId, Boolean discarded, String fromDate, String toDate, String expenseType,
			String paymentMode) {
		List<ExpenseCountResponse> response = null;
		try {
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			long date = 0;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(fromDate));
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}

			fromTime = new DateTime(from);

			toTime = new DateTime(to);

			Criteria criteria = new Criteria("toDate").gte(fromTime).lte(toTime);

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				criteria.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			if (!discarded)
				criteria.and("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(expenseType)) {
				criteria.and("expenseType").is(expenseType.toUpperCase());
			}
			if (!DPDoctorUtils.anyStringEmpty(expenseType)) {
				criteria.and("modeOfPayment").is(paymentMode.toUpperCase());
			}
			AggregationOperation aggregationOperation = null;
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("cost", "$cost"), Fields.field("toDate", "$toDate")));
			if (!DPDoctorUtils.anyStringEmpty(searchType))
				switch (SearchType.valueOf(searchType.toUpperCase())) {

				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
											.append("cost", new BasicDBObject("$sum", "$cost"))
											.append("toDate", new BasicDBObject("$first", "$toDate"))));

					break;
				}

				case WEEKLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("cost", new BasicDBObject("$sum", "$cost"))
													.append("toDate", new BasicDBObject("$first", "$toDate"))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("cost", new BasicDBObject("$sum", "$cost"))
									.append("toDate", new BasicDBObject("$first", "$toDate"))));
					break;
				}
				case YEARLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("cost", new BasicDBObject("$sum", "$cost"))
									.append("toDate", new BasicDBObject("$first", "$toDate"))));

					break;

				}
				default:
					break;
				}
			projectList.and("toDate").extractDayOfMonth().as("day").and("toDate").extractMonth().as("month")
					.and("toDate").extractYear().as("year").and("toDate").extractWeek().as("week");
			response = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria), projectList, aggregationOperation),
					DoctorExpenseCollection.class, ExpenseCountResponse.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public List<AnalyticResponse> getReceiptAnalyticData(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String searchType, String searchTerm, String paymentMode) {
		List<AnalyticResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			long date = 0;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}
			fromTime = new DateTime(from);
			toTime = new DateTime(to);

			CustomAggregationOperation aggregationOperation = null;
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("count", "$patientId"), Fields.field("receivedDate", "$receivedDate")));

			criteria.and("receivedDate").gte(fromTime).lte(toTime);

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria = criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (!DPDoctorUtils.anyStringEmpty(paymentMode)) {
				criteria = criteria.and("modeOfPayment").is(paymentMode.toUpperCase());
			}
			criteria.and("discarded").is(false);
			if (!DPDoctorUtils.anyStringEmpty(searchType)) {
				switch (SearchType.valueOf(searchType.toUpperCase())) {
				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
											.append("day", new BasicDBObject("$first", "$day"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("week", new BasicDBObject("$first", "$week"))
											.append("date", new BasicDBObject("$first", "$receivedDate"))
											.append("receivedDate", new BasicDBObject("$first", "$receivedDate"))
											.append("count", new BasicDBObject("$sum", 1))));

					break;
				}

				case WEEKLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("week", new BasicDBObject("$first", "$week"))
													.append("date", new BasicDBObject("$first", "$receivedDate"))
													.append("receivedDate",
															new BasicDBObject("$first", "$receivedDate"))
													.append("count", new BasicDBObject("$sum", 1))));

					break;
				}
				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$receivedDate"))
									.append("receivedDate", new BasicDBObject("$first", "$receivedDate"))
									.append("count", new BasicDBObject("$sum", 1))));

					break;
				}
				case YEARLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$receivedDate"))
									.append("receivedDate", new BasicDBObject("$first", "$receivedDate"))
									.append("count", new BasicDBObject("$sum", 1))));

					break;

				}
				default:
					break;
				}
			}

			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria),
							projectList.and("receivedDate").as("date").and("receivedDate").extractDayOfMonth().as("day")
									.and("receivedDate").extractMonth().as("month").and("receivedDate").extractYear()
									.as("year").and("receivedDate").extractWeek().as("week"),
							aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "date")))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

			AggregationResults<AnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DoctorPatientReceiptCollection.class, AnalyticResponse.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting Invoice Graph analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting Invoice Graph analytic");
		}

		return response;

	}

	@Override
	public List<AnalyticResponse> getInvoiceAnalyticData(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String searchType, String searchTerm) {
		List<AnalyticResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			long date = 0;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}
			fromTime = new DateTime(from);
			toTime = new DateTime(to);

			CustomAggregationOperation aggregationOperation = null;
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("count", "$patientId"), Fields.field("invoiceDate", "$invoiceDate")));

			criteria.and("invoiceDate").gte(fromTime).lte(toTime);

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria = criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			criteria.and("discarded").is(false);
			if (!DPDoctorUtils.anyStringEmpty(searchType)) {
				switch (SearchType.valueOf(searchType.toUpperCase())) {
				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
											.append("day", new BasicDBObject("$first", "$day"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("week", new BasicDBObject("$first", "$week"))
											.append("date", new BasicDBObject("$first", "$invoiceDate"))
											.append("invoiceDate", new BasicDBObject("$first", "$invoiceDate"))
											.append("count", new BasicDBObject("$sum", 1))));

					break;
				}

				case WEEKLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("week", new BasicDBObject("$first", "$week"))
													.append("date", new BasicDBObject("$first", "$invoiceDate"))
													.append("invoiceDate", new BasicDBObject("$first", "$invoiceDate"))
													.append("count", new BasicDBObject("$sum", 1))));

					break;
				}
				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$invoiceDate"))
									.append("invoiceDate", new BasicDBObject("$first", "$invoiceDate"))
									.append("count", new BasicDBObject("$sum", 1))));

					break;
				}
				case YEARLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$invoiceDate"))
									.append("invoiceDate", new BasicDBObject("$first", "$invoiceDate"))
									.append("count", new BasicDBObject("$sum", 1))));

					break;

				}
				default:
					break;
				}
			}

			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria),
							projectList.and("invoiceDate").as("date").and("invoiceDate").extractDayOfMonth().as("day")
									.and("invoiceDate").extractMonth().as("month").and("invoiceDate").extractYear()
									.as("year").and("invoiceDate").extractWeek().as("week"),
							aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "date")))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

			AggregationResults<AnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DoctorPatientInvoiceCollection.class, AnalyticResponse.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting Invoice Graph analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting Invoice Graph analytic");
		}

		return response;

	}

	@Override
	public Integer countAmountDueAnalyticsData(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String queryType, String searchType) {
		Integer response = 0;
		try {
			Criteria criteria = new Criteria();
			Criteria criteria2 = new Criteria();
			Criteria criteria3 = new Criteria();
			Criteria criteria4 = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
				criteria3.and("invoice.doctorId").is(new ObjectId(doctorId));
				criteria4.and("receipt.doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				criteria2.and("patient.locationId").is(new ObjectId(locationId)).and("patient.discarded").is(false);
				criteria3.and("invoice.locationId").is(new ObjectId(locationId)).and("invoice.discarded").is(false);
				criteria4.and("receipt.locationId").is(new ObjectId(locationId)).and("receipt.discarded").is(false);
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				criteria2.and("patient.hospitalId").is(new ObjectId(hospitalId));
				criteria3.and("invoice.hospitalId").is(new ObjectId(hospitalId));
				criteria4.and("receipt.hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
				fromTime = new DateTime(from);
				toTime = new DateTime(to);
				criteria4.and("receipt.receivedDate").gte(fromTime).lte(toTime);
				criteria3.and("invoice.invoiceDate").gte(fromTime).lte(toTime);

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				fromTime = new DateTime(from);
				criteria4.and("receipt.receivedDate").gte(fromTime);
				criteria3.and("invoice.invoiceDate").gte(fromTime);

			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				to = new Date(Long.parseLong(toDate));
				toTime = new DateTime(to);
				criteria4.and("receipt.receivedDate").lte(toTime);
				criteria3.and("invoice.invoiceDate").lte(toTime);
			}

			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(queryType)) {
				if (queryType.equalsIgnoreCase("PATIENT")) {

					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							Aggregation.unwind("patient"), Aggregation.match(criteria2),

							new CustomAggregationOperation(new Document("$group",
									new BasicDBObject("_id", "$patientId")

											.append("patientName",
													new BasicDBObject("$first", "$patient.localPatientName"))
											.append("pid", new BasicDBObject("$first", "$patient.PID"))
											.append("patientId", new BasicDBObject("$first", "$patientId"))
											.append("doctorId", new BasicDBObject("$first", "$doctorId"))
											.append("dueAmount", new BasicDBObject("$sum", "$dueAmount")))),
							Aggregation.lookup("doctor_patient_invoice_cl", "patientId", "patientId", "invoice"),
							Aggregation.unwind("invoice"), Aggregation.match(criteria3),
							new CustomAggregationOperation(new Document("$group",
									new BasicDBObject("_id", "$patientId")
											.append("invoiced", new BasicDBObject("$sum", "$invoice.grandTotal"))
											.append("patientName", new BasicDBObject("$first", "$patientName"))
											.append("pid", new BasicDBObject("$first", "$pid"))
											.append("patientId", new BasicDBObject("$first", "$patientId"))
											.append("doctorId", new BasicDBObject("$first", "$doctorId"))
											.append("dueAmount", new BasicDBObject("$first", "$dueAmount")))),
							Aggregation.lookup("doctor_patient_receipt_cl", "patientId", "patientId", "receipt"),
							Aggregation.unwind("receipt"), Aggregation.match(criteria4),

							new CustomAggregationOperation(
									new Document("$group", new BasicDBObject("_id", "$patientId"))));

				} else if (queryType.equalsIgnoreCase("DOCTORS")) {

					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
							new CustomAggregationOperation(new Document("$group",
									new BasicDBObject("_id", "$doctorId")
											.append("doctorName", new BasicDBObject("$first", "$doctor.firstName"))
											.append("doctorId", new BasicDBObject("$first", "$doctorId"))
											.append("locationId", new BasicDBObject("$first", "$locationId"))
											.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
											.append("dueAmount", new BasicDBObject("$sum", "$dueAmount")))),
							Aggregation.lookup("doctor_patient_invoice_cl", "doctorId", "doctorId", "invoice"),
							Aggregation.unwind("invoice"), Aggregation.match(criteria3),
							new CustomAggregationOperation(new Document("$group",
									new BasicDBObject("_id", "$doctorId")
											.append("invoiced", new BasicDBObject("$sum", "$invoice.grandTotal"))
											.append("doctorName", new BasicDBObject("$first", "$doctorName"))
											.append("doctorId", new BasicDBObject("$first", "$doctorId"))
											.append("locationId", new BasicDBObject("$first", "$locationId"))
											.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
											.append("dueAmount", new BasicDBObject("$first", "$dueAmount")))),
							Aggregation.lookup("doctor_patient_receipt_cl", "doctorId", "doctorId", "receipt"),
							Aggregation.unwind("receipt"), Aggregation.match(criteria4), new CustomAggregationOperation(
									new Document("$group", new BasicDBObject("_id", "$doctorId"))));

				} else {
					throw new BusinessException(ServiceError.Unknown, "Query Type cannot be null");
				}

				response = mongoTemplate.aggregate(aggregation, DoctorPatientDueAmountCollection.class,
						AmountDueAnalyticsDataResponse.class).getMappedResults().size();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting amount due analytics data");
			throw new BusinessException(ServiceError.Unknown, "Error Ocnew ProjectionOperation(\n"
					+ "					Fields.from(Fields.field(\"count\", \"$patientId\"), Fields.field(\"receivedDate\", \"$receivedDate\")))curred While counting amount due analytics data");
		}
		return response;
	}

}