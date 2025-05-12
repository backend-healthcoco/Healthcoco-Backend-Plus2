package com.dpdocter.services.impl;

import java.io.ByteArrayOutputStream;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.DoctorExpenseCollection;
import com.dpdocter.collections.DoctorPatientDueAmountCollection;
import com.dpdocter.collections.DoctorPatientInvoiceCollection;
import com.dpdocter.collections.DoctorPatientReceiptCollection;
import com.dpdocter.collections.EmailListCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.ModeOfPayment;
import com.dpdocter.enums.PaymentStatusType;
import com.dpdocter.enums.SearchType;
import com.dpdocter.enums.UnitType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.EmailListRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.response.AllAnalyticResponse;
import com.dpdocter.response.AmountDueAnalyticsDataResponse;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.DailyReportAnalyticItem;
import com.dpdocter.response.DailyReportAnalyticResponse;
import com.dpdocter.response.DischargeSummaryAnalyticsDataResponse;
import com.dpdocter.response.DoctorVisitAnalyticResponse;
import com.dpdocter.response.ExpenseAnalyticsTypeDataResponse;
import com.dpdocter.response.ExpenseCountResponse;
import com.dpdocter.response.IncomeAnalyticsDataResponse;
import com.dpdocter.response.InvoiceAnalyticsDataDetailResponse;
import com.dpdocter.response.PaymentAnalyticsDataResponse;
import com.dpdocter.response.PaymentDetailsAnalyticsDataResponse;
import com.dpdocter.services.AnalyticsService;
import com.dpdocter.services.MailService;
import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;
import common.util.web.DateUtil;

//Billing analytic 
@Service
@Transactional
public class AnalyticsServiceImpl implements AnalyticsService {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private MailService mailService;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private EmailListRepository emailListRepository;

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

			data = new DoctorVisitAnalyticResponse();
			data.setTotalVisit((int) mongoTemplate.count(new Query(criteria), PatientVisitCollection.class));
			applyDateCriteria(criteria, "adminCreatedTime", fromDate, toDate);

			data.setTotalVisitCreated((int) mongoTemplate.count(new Query(criteria), PatientVisitCollection.class));

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
			applyDateCriteria(criteria, "createdTime", fromDate, toDate);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria2.orOperator(new Criteria("patient.localPatientName").regex(searchTerm, "i"),
						new Criteria("user.firstName").regex(searchTerm, "i"),
						new Criteria("user.mobileNumber").regex(searchTerm, "i"));
			}
			criteria.and("discarded").is(false);
			Aggregation aggregation = null;
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("patient_cl", "patientId", "userId", "patient"), Aggregation.unwind("patient"),
					Aggregation.lookup("user_cl", "patientId", "_id", "user"), Aggregation.unwind("user"),
					Aggregation.lookup("user_cl", "doctorId", "_id", "docter"), Aggregation.unwind("docter"),
					Aggregation.match(criteria2));
			response = mongoTemplate.aggregate(aggregation, DoctorPatientInvoiceCollection.class,
					InvoiceAnalyticsDataDetailResponse.class).getMappedResults().size();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While count income analytics data");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While count income analytics data");
		}

		return response;
	}

	public static void applyDateCriteria(Criteria criteria, String field, String fromDate, String toDate) {
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

		fromTime = new DateTime(DateUtil.getStartOfDay(from));
		toTime = new DateTime(DateUtil.getEndOfDay(to));
//		fromTime = new DateTime(from);
//		toTime = new DateTime(to);

		criteria.and(field).gte(fromTime).lte(toTime);
	}

	@Override
	public List<InvoiceAnalyticsDataDetailResponse> getIncomeDetailsAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String searchTerm, int page, int size) {
		List<InvoiceAnalyticsDataDetailResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate))
				fromTime = new DateTime(DateUtil.getStartOfDay(new Date(Long.parseLong(fromDate))));
			if (!DPDoctorUtils.anyStringEmpty(toDate))
				toTime = new DateTime(DateUtil.getEndOfDay(new Date(Long.parseLong(toDate))));
			Criteria criteria2 = new Criteria();
			Criteria criteria3 = new Criteria();

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

			applyDateCriteria(criteria, "createdTime", fromDate, toDate);
			applyDateCriteria(criteria3, "createdTime", fromDate, toDate);

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
							.append("referedBy", new BasicDBObject("$first", "$referedBy"))
							.append("patientAnalyticType", new BasicDBObject("$first", "$patientAnalyticType"))
							.append("date", new BasicDBObject("$first", "$invoiceDate"))));

			Aggregation aggregation = null;
			if (size > 0) {

				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
								Aggregation.unwind("patient"),
								Aggregation.lookup("patient_visit_cl", "patientId", "patientId", "visitData"),
								Aggregation.unwind("visitData", true),
								Aggregation.lookup("user_cl", "patientId", "_id", "user"), Aggregation.unwind("user"),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								Aggregation.unwind("doctor"),
								Aggregation.lookup("referrences_cl", "patient.referredBy", "_id", "refer"),
								Aggregation.unwind("refer", true), Aggregation.match(criteria2),
								new CustomAggregationOperation(
										new Document("$project", new BasicDBObject("_id", "$_id")
												.append("resultantDiscount",
														new BasicDBObject("$cond",
																new BasicDBObject("if", new BasicDBObject("$eq",
																		Arrays.asList("$totalDiscount.unit",
																				UnitType.PERCENT.name())))
																		.append("then", new BasicDBObject("$multiply",
																				Arrays.asList(new BasicDBObject(
																						"$divide",
																						Arrays.asList(
																								"$totalDiscount.value",
																								100)),
																						"$totalCost")))
																		.append("else", "$totalDiscount.value")))
												.append("resultantCost", "$totalCost")
												.append("invoiceDate", "$invoiceDate")
												.append("resultantTax",
														new BasicDBObject("$cond",
																new BasicDBObject("if", new BasicDBObject("$eq",
																		Arrays.asList("$totalTax.unit",
																				UnitType.PERCENT.name())))
																		.append("then", new BasicDBObject("$multiply",
																				Arrays.asList(new BasicDBObject(
																						"$divide",
																						Arrays.asList("$totalTax.value",
																								100)),
																						"$totalCost")))
																		.append("else", "$totalTax.value")))
												.append("localPatientName", "$patient.localPatientName")
												.append("firstName", "$user.firstName")
												.append("mobileNumber", "$user.mobileNumber")
												.append("doctorName", "$doctor.firstName")
												.append("uniqueInvoiceId", "$uniqueInvoiceId")
												.append("balanceAmount", "$balanceAmount")
												.append("patientAnalyticType", new BasicDBObject("$cond", Arrays.asList(
														new BasicDBObject("$and", Arrays.asList(new BasicDBObject(
																"$gt",
																Arrays.asList(new BasicDBObject("$cond", Arrays.asList(
																		new BasicDBObject("$eq",
																				Arrays.asList(new BasicDBObject("$type",
																						"$visitData"), "array")),
																		new BasicDBObject("$size", "$visitData"), 0)),
																		0)),
																new BasicDBObject(
																		"$gte",
																		Arrays.asList("$visitData.createdTime",
																				fromTime)),
																new BasicDBObject(
																		"$lte", Arrays.asList("$visitData.createdTime",
																				toTime)))),
														"VISITED_PATIENT",
														new BasicDBObject("$cond", Arrays.asList(new BasicDBObject(
																"$and",
																Arrays.asList(new BasicDBObject("$gt", Arrays.asList(
																		new BasicDBObject("$cond", Arrays.asList(
																				new BasicDBObject("$eq",
																						Arrays.asList(new BasicDBObject(
																								"$type", "$patient"),
																								"array")),
																				new BasicDBObject("$size", "$patient"),
																				0)),
																		0)),
																		new BasicDBObject("$gte",
																				Arrays.asList("$patient.createdTime",
																						fromTime)),
																		new BasicDBObject("$lte",
																				Arrays.asList("$patient.createdTime",
																						toTime)))),
																"NEW_PATIENT", "NEW_PATIENT")))))
												.append("referedBy", "$refer.reference")
												.append("resultantInvoiceAmount", "$grandTotal"))),
								aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.DESC, "invoiceDate")),
								Aggregation.skip((long) (page) * size), Aggregation.limit(size));
				response = mongoTemplate.aggregate(aggregation, DoctorPatientInvoiceCollection.class,
						InvoiceAnalyticsDataDetailResponse.class).getMappedResults();
			} else {

				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
								Aggregation.unwind("patient"),
								Aggregation.lookup("patient_visit_cl", "patientId", "patientId", "visitData"),
								Aggregation.unwind("visitData", true),
								Aggregation.lookup("user_cl", "patientId", "_id", "user"), Aggregation.unwind("user"),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								Aggregation.unwind("doctor"),
								Aggregation.lookup("referrences_cl", "patient.referredBy", "_id", "refer"),
								Aggregation.unwind("refer", true), Aggregation.match(criteria2),
								new CustomAggregationOperation(
										new Document("$project", new BasicDBObject("_id", "$_id")
												.append("resultantDiscount",
														new BasicDBObject("$cond",
																new BasicDBObject("if", new BasicDBObject("$eq",
																		Arrays.asList("$totalDiscount.unit",
																				UnitType.PERCENT.name())))
																		.append("then", new BasicDBObject("$multiply",
																				Arrays.asList(new BasicDBObject(
																						"$divide",
																						Arrays.asList(
																								"$totalDiscount.value",
																								100)),
																						"$totalCost")))
																		.append("else", "$totalDiscount.value")))
												.append("resultantCost", "$totalCost")
												.append("invoiceDate", "$invoiceDate")
												.append("resultantTax",
														new BasicDBObject("$cond",
																new BasicDBObject("if", new BasicDBObject("$eq",
																		Arrays.asList("$totalTax.unit",
																				UnitType.PERCENT.name())))
																		.append("then", new BasicDBObject("$multiply",
																				Arrays.asList(new BasicDBObject(
																						"$divide",
																						Arrays.asList("$totalTax.value",
																								100)),
																						"$totalCost")))
																		.append("else", "$totalTax.value")))
												.append("localPatientName", "$patient.localPatientName")
												.append("firstName", "$user.firstName")
												.append("mobileNumber", "$user.mobileNumber")
												.append("doctorName", "$doctor.firstName")
												.append("uniqueInvoiceId", "$uniqueInvoiceId")
												.append("balanceAmount", "$balanceAmount")
												.append("patientAnalyticType", new BasicDBObject("$cond", Arrays.asList(
														new BasicDBObject("$and", Arrays.asList(new BasicDBObject(
																"$gt",
																Arrays.asList(new BasicDBObject("$cond", Arrays.asList(
																		new BasicDBObject("$eq",
																				Arrays.asList(new BasicDBObject("$type",
																						"$visitData"), "array")),
																		new BasicDBObject("$size", "$visitData"), 0)),
																		0)),
																new BasicDBObject(
																		"$gte",
																		Arrays.asList("$visitData.createdTime",
																				fromTime)),
																new BasicDBObject(
																		"$lte", Arrays.asList("$visitData.createdTime",
																				toTime)))),
														"VISITED_PATIENT",
														new BasicDBObject("$cond", Arrays.asList(new BasicDBObject(
																"$and",
																Arrays.asList(new BasicDBObject("$gt", Arrays.asList(
																		new BasicDBObject("$cond", Arrays.asList(
																				new BasicDBObject("$eq",
																						Arrays.asList(new BasicDBObject(
																								"$type", "$patient"),
																								"array")),
																				new BasicDBObject("$size", "$patient"),
																				0)),
																		0)),
																		new BasicDBObject("$gte",
																				Arrays.asList("$patient.createdTime",
																						fromTime)),
																		new BasicDBObject("$lte",
																				Arrays.asList("$patient.createdTime",
																						toTime)))),
																"NEW_PATIENT", "NEW_PATIENT")))))
												.append("referedBy", "$refer.reference")
												.append("resultantInvoiceAmount", "$grandTotal"))),
								aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.DESC, "invoiceDate")));
				response = mongoTemplate.aggregate(aggregation, DoctorPatientInvoiceCollection.class,
						InvoiceAnalyticsDataDetailResponse.class).getMappedResults();
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
			if (!DPDoctorUtils.anyStringEmpty(fromDate))
				fromTime = new DateTime(DateUtil.getStartOfDay(new Date(Long.parseLong(fromDate))));
			if (!DPDoctorUtils.anyStringEmpty(toDate))
				toTime = new DateTime(DateUtil.getEndOfDay(new Date(Long.parseLong(toDate))));
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			applyDateCriteria(criteria, "createdTime", fromDate, toDate);

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
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("resultantDiscount", new BasicDBObject("$cond", new BasicDBObject("if",
									new BasicDBObject("$eq",
											Arrays.asList("$invoice.totalDiscount.unit", UnitType.PERCENT.name())))
									.append("then",
											new BasicDBObject("$multiply",
													Arrays.asList(
															new BasicDBObject("$divide",
																	Arrays.asList("$invoice.totalDiscount.value", 100)),
															"$invoice.totalCost")))
									.append("else", "$invoice.totalDiscount.value")))
									.append("resultantCost", "$invoice.totalCost")
									.append("invoiceDate", "$invoice.invoiceDate")
									.append("resultantTax", new BasicDBObject("$cond", new BasicDBObject("if",
											new BasicDBObject("$eq",
													Arrays.asList("$invoice.totalTax.unit", UnitType.PERCENT.name())))
											.append("then",
													new BasicDBObject("$multiply",
															Arrays.asList(new BasicDBObject("$divide",
																	Arrays.asList("$invoice.totalTax.value", 100)),
																	"$invoice.totalCost")))
											.append("else", "$invoice.totalTax.value")))
									.append("resultantInvoiceAmount", "$invoice.grandTotal")
									.append("groupId", "$group._id").append("groupName", "$group.name"))),
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
							new BasicDBObject("resultantDiscount", new BasicDBObject("$cond", new BasicDBObject("if",
									new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
									.append("then", new BasicDBObject("$multiply",
											Arrays.asList(new BasicDBObject("$divide",
													Arrays.asList("$totalDiscount.value", 100)), "$totalCost")))
									.append("else", "$totalDiscount.value"))).append("resultantCost", "$totalCost")
									.append("invoiceDate", "$invoiceDate")
									.append("resultantTax",
											new BasicDBObject("$cond", new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$totalTax.unit", UnitType.PERCENT.name())))
													.append("then",
															new BasicDBObject("$multiply",
																	Arrays.asList(new BasicDBObject("$divide",
																			Arrays.asList("$totalTax.value", 100)),
																			"$totalCost")))
													.append("else", "$totalTax.value")))
									.append("resultantInvoiceAmount", "$grandTotal")
									.append("itemId", "$invoiceItems.itemId")
									.append("serviceName", "$invoiceItems.name"))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "invoiceDate"),
					Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("invoiceItems"),
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("resultantDiscount", new BasicDBObject("$cond", new BasicDBObject("if",
									new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
									.append("then", new BasicDBObject("$multiply",
											Arrays.asList(new BasicDBObject("$divide",
													Arrays.asList("$totalDiscount.value", 100)), "$totalCost")))
									.append("else", "$totalDiscount.value"))).append("resultantCost", "$totalCost")
									.append("invoiceDate", "$invoiceDate")
									.append("resultantTax",
											new BasicDBObject("$cond", new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$totalTax.unit", UnitType.PERCENT.name())))
													.append("then",
															new BasicDBObject("$multiply",
																	Arrays.asList(new BasicDBObject("$divide",
																			Arrays.asList("$totalTax.value", 100)),
																			"$totalCost")))
													.append("else", "$totalTax.value")))
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
							new BasicDBObject("resultantDiscount", new BasicDBObject("$cond", new BasicDBObject("if",
									new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
									.append("then", new BasicDBObject("$multiply",
											Arrays.asList(new BasicDBObject("$divide",
													Arrays.asList("$totalDiscount.value", 100)), "$totalCost")))
									.append("else", "$totalDiscount.value"))).append("resultantCost", "$totalCost")
									.append("invoiceDate", "$invoiceDate")
									.append("resultantTax",
											new BasicDBObject("$cond", new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$totalTax.unit", UnitType.PERCENT.name())))
													.append("then",
															new BasicDBObject("$multiply",
																	Arrays.asList(new BasicDBObject("$divide",
																			Arrays.asList("$totalTax.value", 100)),
																			"$totalCost")))
													.append("else", "$totalTax.value")))
									.append("resultantInvoiceAmount", "$grandTotal").append("doctorId", "$doctorId")
									.append("title", "$user.title").append("doctorName", "$user.firstName"))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "invoiceDate"),
					Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("resultantDiscount", new BasicDBObject("$cond", new BasicDBObject("if",
									new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
									.append("then", new BasicDBObject("$multiply",
											Arrays.asList(new BasicDBObject("$divide",
													Arrays.asList("$totalDiscount.value", 100)), "$totalCost")))
									.append("else", "$totalDiscount.value"))).append("resultantCost", "$totalCost")
									.append("invoiceDate", "$invoiceDate")
									.append("resultantTax",
											new BasicDBObject("$cond", new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$totalTax.unit", UnitType.PERCENT.name())))
													.append("then",
															new BasicDBObject("$multiply",
																	Arrays.asList(new BasicDBObject("$divide",
																			Arrays.asList("$totalTax.value", 100)),
																			"$totalCost")))
													.append("else", "$totalTax.value")))
									.append("doctorId", "$doctorId").append("title", "$user.title")
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
							new BasicDBObject("resultantDiscount", new BasicDBObject("$cond", new BasicDBObject("if",
									new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
									.append("then", new BasicDBObject("$multiply",
											Arrays.asList(new BasicDBObject("$divide",
													Arrays.asList("$totalDiscount.value", 100)), "$totalCost")))
									.append("else", "$totalDiscount.value"))).append("resultantCost", "$totalCost")
									.append("invoiceDate", "$invoiceDate")
									.append("resultantTax",
											new BasicDBObject("$cond", new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$totalTax.unit", UnitType.PERCENT.name())))
													.append("then",
															new BasicDBObject("$multiply",
																	Arrays.asList(new BasicDBObject("$divide",
																			Arrays.asList("$totalTax.value", 100)),
																			"$totalCost")))
													.append("else", "$totalTax.value")))
									.append("resultantInvoiceAmount", "$grandTotal")
									.append("day", new BasicDBObject("$dayOfMonth", "$invoiceDate"))
									.append("month", new BasicDBObject("$month", "$invoiceDate"))
									.append("year", new BasicDBObject("$year", "$invoiceDate"))
									.append("week", new BasicDBObject("$week", "$invoiceDate")))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "invoiceDate"),
					Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("resultantDiscount", new BasicDBObject("$cond", new BasicDBObject("if",
									new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
									.append("then", new BasicDBObject("$multiply",
											Arrays.asList(new BasicDBObject("$divide",
													Arrays.asList("$totalDiscount.value", 100)), "$totalCost")))
									.append("else", "$totalDiscount.value"))).append("resultantCost", "$totalCost")
									.append("invoiceDate", "$invoiceDate")
									.append("resultantTax",
											new BasicDBObject("$cond", new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$totalTax.unit", UnitType.PERCENT.name())))
													.append("then",
															new BasicDBObject("$multiply",
																	Arrays.asList(new BasicDBObject("$divide",
																			Arrays.asList("$totalTax.value", 100)),
																			"$totalCost")))
													.append("else", "$totalTax.value")))
									.append("resultantInvoiceAmount", "$grandTotal")
									.append("day", new BasicDBObject("$dayOfMonth", "$invoiceDate"))
									.append("month", new BasicDBObject("$month", "$invoiceDate"))
									.append("year", new BasicDBObject("$year", "$invoiceDate"))
									.append("week", new BasicDBObject("$week", "$invoiceDate")))),
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

			applyDateCriteria(criteria, "receivedDate", fromDate, toDate);
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

			applyDateCriteria(criteria, "receivedDate", fromDate, toDate);

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
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			applyDateCriteria(criteria, "receivedDate", fromDate, toDate);

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
									new BasicDBObject("cash", new BasicDBObject("$cond",
											new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name())))
													.append("then", "$amountPaid").append("else", 0)))
											.append("card", new BasicDBObject("$cond", new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$modeOfPayment", ModeOfPayment.CARD.name())))
													.append("then", "$amountPaid").append("else", 0)))
											.append("online", new BasicDBObject("$cond",
													new BasicDBObject("if",
															new BasicDBObject("$eq",
																	Arrays.asList("$modeOfPayment",
																			ModeOfPayment.ONLINE.name())))
															.append("then", "$amountPaid").append("else", 0)))
											.append("wallet", new BasicDBObject("$cond",
													new BasicDBObject("if",
															new BasicDBObject("$eq",
																	Arrays.asList("$modeOfPayment",
																			ModeOfPayment.WALLET.name())))
															.append("then", "$amountPaid").append("else", 0)))
											.append("total", "$amountPaid").append("doctorId", "$doctorId")
											.append("date", "$receivedDate").append("title", "$user.title")
											.append("doctorName", "$user.firstName"))),
							aggregationOperation, Aggregation.sort(Direction.DESC, "receivedDate"),
							Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
							new CustomAggregationOperation(new Document("$project",
									new BasicDBObject("cash", new BasicDBObject("$cond",
											new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name())))
													.append("then", "$amountPaid").append("else", 0)))
											.append("card", new BasicDBObject("$cond", new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$modeOfPayment", ModeOfPayment.CARD.name())))
													.append("then", "$amountPaid").append("else", 0)))
											.append("online", new BasicDBObject("$cond",
													new BasicDBObject("if",
															new BasicDBObject("$eq",
																	Arrays.asList("$modeOfPayment",
																			ModeOfPayment.ONLINE.name())))
															.append("then", "$amountPaid").append("else", 0)))
											.append("wallet", new BasicDBObject("$cond",
													new BasicDBObject("if",
															new BasicDBObject("$eq",
																	Arrays.asList("$modeOfPayment",
																			ModeOfPayment.WALLET.name())))
															.append("then", "$amountPaid").append("else", 0)))
											.append("total", "$amountPaid").append("doctorId", "$doctorId")
											.append("receivedDate", "$receivedDate").append("title", "$user.title")
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
					Aggregation.skip((long) page * size), Aggregation.limit(size));
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
			aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria),
							new CustomAggregationOperation(new Document("$project",
									new BasicDBObject("cash", new BasicDBObject("$cond",
											new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name())))
													.append("then", "$amountPaid").append("else", 0)))
											.append("card", new BasicDBObject("$cond", new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$modeOfPayment", ModeOfPayment.CARD.name())))
													.append("then", "$amountPaid").append("else", 0)))
											.append("online", new BasicDBObject("$cond",
													new BasicDBObject("if",
															new BasicDBObject("$eq",
																	Arrays.asList("$modeOfPayment",
																			ModeOfPayment.ONLINE.name())))
															.append("then", "$amountPaid").append("else", 0)))
											.append("wallet", new BasicDBObject("$cond",
													new BasicDBObject("if",
															new BasicDBObject("$eq",
																	Arrays.asList("$modeOfPayment",
																			ModeOfPayment.WALLET.name())))
															.append("then", "$amountPaid").append("else", 0)))
											.append("day", "$receivedDate")
											.append("day", new BasicDBObject("$dayOfMonth", "$receivedDate"))
											.append("month", new BasicDBObject("$month", "$receivedDate"))
											.append("year", new BasicDBObject("$year", "$receivedDate"))
											.append("week", new BasicDBObject("$week", "$receivedDate")))),
							aggregationOperation, Aggregation.sort(Direction.DESC, "receivedDate"),
							Aggregation.skip((long) page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria),
							new CustomAggregationOperation(new Document("$project",
									new BasicDBObject("cash", new BasicDBObject("$cond",
											new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name())))
													.append("then", "$amountPaid").append("else", 0)))
											.append("card", new BasicDBObject("$cond", new BasicDBObject("if",
													new BasicDBObject("$eq",
															Arrays.asList("$modeOfPayment", ModeOfPayment.CARD.name())))
													.append("then", "$amountPaid").append("else", 0)))
											.append("online", new BasicDBObject("$cond",
													new BasicDBObject("if",
															new BasicDBObject("$eq",
																	Arrays.asList("$modeOfPayment",
																			ModeOfPayment.ONLINE.name())))
															.append("then", "$amountPaid").append("else", 0)))
											.append("wallet", new BasicDBObject("$cond",
													new BasicDBObject("if",
															new BasicDBObject("$eq",
																	Arrays.asList("$modeOfPayment",
																			ModeOfPayment.WALLET.name())))
															.append("then", "$amountPaid").append("else", 0)))
											.append("receivedDate", "$receivedDate")
											.append("day", new BasicDBObject("$dayOfMonth", "$receivedDate"))
											.append("month", new BasicDBObject("$month", "$receivedDate"))
											.append("year", new BasicDBObject("$year", "$receivedDate"))
											.append("week", new BasicDBObject("$week", "$receivedDate")))),
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

			applyDateCriteria(criteria4, "receipt.receivedDate", fromDate, toDate);
			applyDateCriteria(criteria3, "invoice.invoiceDate", fromDate, toDate);

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
																.append("isCghsInvoice",
																		new BasicDBObject("$max",
																				"$invoice.isCghsInvoice"))
																.append("cghsDue", new BasicDBObject("$sum",
																		new BasicDBObject("$cond",
																				Arrays.asList("$invoice.isCghsInvoice",
																						"$invoice.balanceAmount", 0))))
																.append("amountDue", new BasicDBObject("$sum",
																		new BasicDBObject("$cond",
																				Arrays.asList("$invoice.isCghsInvoice",
																						0, "$invoice.balanceAmount"))))
																.append("patientName",
																		new BasicDBObject("$first", "$patientName"))
																.append("pid", new BasicDBObject("$first", "$pid"))
																.append("patientId",
																		new BasicDBObject("$first", "$patientId"))
																.append("doctorId",
																		new BasicDBObject("$first", "$doctorId"))
																.append("dueAmount", new BasicDBObject("$first",
																		"$dueAmount")))),
										Aggregation.lookup(
												"doctor_patient_receipt_cl", "patientId", "patientId", "receipt"),
										Aggregation.unwind("receipt"), Aggregation.match(criteria4),

										new CustomAggregationOperation(new Document("$group",
												new BasicDBObject("_id", "$patientId")
														.append("invoiced", new BasicDBObject("$first", "$invoiced"))
														.append("received",
																new BasicDBObject("$sum", "$receipt.amountPaid"))
														.append("amountDue", new BasicDBObject("$first", "$amountDue"))
														.append("isCghsInvoice",
																new BasicDBObject("$first", "$isCghsInvoice"))
														.append("cghsDue", new BasicDBObject("$first", "$cghsDue"))
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
																.append("isCghsInvoice",
																		new BasicDBObject("$max",
																				"$invoice.isCghsInvoice"))
																.append("cghsDue", new BasicDBObject("$sum",
																		new BasicDBObject("$cond",
																				Arrays.asList("$invoice.isCghsInvoice",
																						"$invoice.balanceAmount", 0))))
																.append("amountDue", new BasicDBObject("$sum",
																		new BasicDBObject("$cond",
																				Arrays.asList("$invoice.isCghsInvoice",
																						0, "$invoice.balanceAmount"))))
																.append("patientName",
																		new BasicDBObject("$first", "$patientName"))
																.append("pid", new BasicDBObject("$first", "$pid"))
																.append("patientId",
																		new BasicDBObject("$first", "$patientId"))
																.append("doctorId",
																		new BasicDBObject("$first", "$doctorId"))
																.append("dueAmount", new BasicDBObject("$first",
																		"$dueAmount")))),
										Aggregation.lookup(
												"doctor_patient_receipt_cl", "patientId", "patientId", "receipt"),
										Aggregation.unwind("receipt"), Aggregation.match(criteria4),

										new CustomAggregationOperation(new Document("$group",
												new BasicDBObject("_id", "$patientId")
														.append("invoiced", new BasicDBObject("$first", "$invoiced"))
														.append("received",
																new BasicDBObject("$sum", "$receipt.amountPaid"))
														.append("amountDue", new BasicDBObject("$first", "$amountDue"))
														.append("isCghsInvoice",
																new BasicDBObject("$first", "$isCghsInvoice"))
														.append("cghsDue", new BasicDBObject("$first", "$cghsDue"))
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
																.append("totalDueAmount",
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
//																.append("amountDue",
//																		new BasicDBObject("$sum",
//																				"$invoice.balanceAmount"))
																.append("cghsDue", new BasicDBObject("$sum",
																		new BasicDBObject("$cond",
																				Arrays.asList("$invoice.isCghsInvoice",
																						"$invoice.balanceAmount", 0))))
																.append("amountDue", new BasicDBObject("$sum",
																		new BasicDBObject("$cond",
																				Arrays.asList("$invoice.isCghsInvoice",
																						0, "$invoice.balanceAmount"))))
																.append("totalDueAmount",
																		new BasicDBObject("$first",
																				"$totalDueAmount")))),
										Aggregation
												.lookup("doctor_patient_receipt_cl", "doctorId", "doctorId", "receipt"),
										Aggregation.unwind("receipt"), Aggregation.match(criteria4),
										new CustomAggregationOperation(new Document("$group",
												new BasicDBObject("_id", "$doctorId")
														.append("invoiced", new BasicDBObject("$first", "$invoiced"))
														.append("received",
																new BasicDBObject("$sum", "$receipt.amountPaid"))
														.append("amountDue", new BasicDBObject("$first", "$amountDue"))
														.append("cghsDue", new BasicDBObject("$first", "$cghsDue"))
														.append("totalDueAmount",
																new BasicDBObject("$first", "$totalDueAmount"))
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
																.append("totalDueAmount",
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
//																.append("amountDue",
//																		new BasicDBObject("$sum",
//																				"$invoice.balanceAmount"))
																.append("cghsDue", new BasicDBObject("$sum",
																		new BasicDBObject("$cond",
																				Arrays.asList("$invoice.isCghsInvoice",
																						"$invoice.balanceAmount", 0))))
																.append("amountDue", new BasicDBObject("$sum",
																		new BasicDBObject("$cond",
																				Arrays.asList("$invoice.isCghsInvoice",
																						0, "$invoice.balanceAmount"))))
																.append("totalDueAmount",
																		new BasicDBObject("$first",
																				"$totalDueAmount")))),

										Aggregation
												.lookup("doctor_patient_receipt_cl", "doctorId", "doctorId", "receipt"),
										Aggregation.unwind("receipt"), Aggregation.match(criteria4),
										new CustomAggregationOperation(new Document("$group",
												new BasicDBObject("_id", "$doctorId")
														.append("invoiced", new BasicDBObject("$first", "$invoiced"))
														.append("received",
																new BasicDBObject("$sum", "$receipt.amountPaid"))
														.append("amountDue", new BasicDBObject("$first", "$amountDue"))
														.append("cghsDue", new BasicDBObject("$first", "$cghsDue"))
														.append("totalDueAmount",
																new BasicDBObject("$first", "$totalDueAmount"))
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
	public ExpenseCountResponse getDoctorExpenseAnalytic(String doctorId, String locationId, String hospitalId,
			Boolean discarded, String fromDate, String toDate, String expenseType, String paymentMode) {
		ExpenseCountResponse response = null;
		try {
			Criteria criteria = new Criteria();
			applyDateCriteria(criteria, "toDate", fromDate, toDate);

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				criteria.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			if (!discarded)
				criteria.and("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(expenseType)) {
				criteria.and("expenseType").is(expenseType);
			}

			if (!DPDoctorUtils.anyStringEmpty(paymentMode)) {
				criteria.and("modeOfPayment").is(paymentMode.toUpperCase());
			}
			// Project required fields
			ProjectionOperation projectFields = Aggregation.project().andExpression("cost").as("cost")
					.andExpression("toDate").as("toDate").andExpression("expenseType").as("expenseType")
					.andExpression("vendor.vendorName").as("vendor"); // ✅ Extract vendor name

			// Group by `null` (i.e., accumulate all records into one list)
			GroupOperation groupExpenses = Aggregation.group().sum("cost").as("cost") // ✅ Aggregate total cost
					.push(new BasicDBObject("expenseType", "$expenseType").append("cost", "$cost")
							.append("toDate", "$toDate").append("vendor", "$vendor"))
					.as("analyticsDataResponse"); // ✅ Convert to list

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria), projectFields,
					groupExpenses, Aggregation.sort(Sort.Direction.DESC, "analyticsDataResponse.toDate"));

			response = mongoTemplate.aggregate(aggregation, DoctorExpenseCollection.class, ExpenseCountResponse.class)
					.getUniqueMappedResult();

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

			applyDateCriteria(criteria, "receivedDate", fromDate, toDate);

			CustomAggregationOperation aggregationOperation = null;
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("count", "$patientId"), Fields.field("receivedDate", "$receivedDate")));

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
											"$year"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("week", new BasicDBObject("$first", "$week"))
									.append("date", new BasicDBObject("$first", "$receivedDate"))
									.append("receivedDate", new BasicDBObject("$first", "$receivedDate"))
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
			applyDateCriteria(criteria, "createdTime", fromDate, toDate);

			CustomAggregationOperation aggregationOperation = null;
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("count", "$patientId"), Fields.field("invoiceDate", "$invoiceDate")));

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
											"$year"))
									.append("month", new BasicDBObject("$first", "$month"))
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

			applyDateCriteria(criteria4, "receipt.receivedDate", fromDate, toDate);
			applyDateCriteria(criteria3, "invoice.invoiceDate", fromDate, toDate);

			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(queryType)) {
				if (queryType.equalsIgnoreCase("PATIENT")) {

					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							Aggregation.unwind("patient"), Aggregation.match(criteria2),

							new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$patientId")

									.append("patientName", new BasicDBObject("$first", "$patient.localPatientName"))
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
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While counting amount due analytics data");
		}
		return response;
	}

	@Override
	public AllAnalyticResponse getAllAnalyticData(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate) {
		AllAnalyticResponse response = new AllAnalyticResponse();
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria = criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			applyDateCriteria(criteria, "createdTime", fromDate, toDate);

			criteria.and("discarded").is(false);
			Aggregation aggregation = null;
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria));

			//
			Integer totalPatientCount = mongoTemplate.aggregate(aggregation, "patient_cl", PatientCollection.class)
					.getMappedResults().size();
			response.setTotalNewPatient(totalPatientCount);

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria));
			//
			Integer totalPatientVisitCount = mongoTemplate
					.aggregate(aggregation, "patient_visit_cl", PatientVisitCollection.class).getMappedResults().size();
			response.setTotalVisitedPatient(totalPatientVisitCount);

			//
			Criteria criteriaAppointment = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteriaAppointment = criteriaAppointment.and("doctorId").is(new ObjectId(doctorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteriaAppointment = criteriaAppointment.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteriaAppointment = criteriaAppointment.and("hospitalId").is(new ObjectId(hospitalId));
			}
			applyDateCriteria(criteriaAppointment, "fromDate", fromDate, toDate);

			criteriaAppointment = criteriaAppointment.and("type").is(AppointmentType.APPOINTMENT);
			aggregation = Aggregation.newAggregation(Aggregation.match(criteriaAppointment));
			Integer totalAppointments = mongoTemplate
					.aggregate(aggregation, "appointment_cl", AppointmentCollection.class).getMappedResults().size();
			response.setTotalAppointments(totalAppointments);

			//
			criteriaAppointment.and("state").is(AppointmentState.CANCEL);
			aggregation = Aggregation.newAggregation(Aggregation.match(criteriaAppointment));
			Integer totaCancelledAppointments = mongoTemplate
					.aggregate(aggregation, "appointment_cl", AppointmentCollection.class).getMappedResults().size();
			response.setCancelledAppointments(totaCancelledAppointments);

			//
			Criteria criteriaPrescription = new Criteria();
			criteriaPrescription.and("discarded").is(false);
			applyDateCriteria(criteriaPrescription, "createdTime", fromDate, toDate);

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteriaPrescription = criteriaPrescription.and("doctorId").is(new ObjectId(doctorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteriaPrescription = criteriaPrescription.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteriaPrescription = criteriaPrescription.and("hospitalId").is(new ObjectId(hospitalId));
			}
			aggregation = Aggregation.newAggregation(Aggregation.match(criteriaPrescription));
			response.setTotalPrescription(
					(int) mongoTemplate.count(new Query(criteriaPrescription), PrescriptionCollection.class));

			Criteria criteria1 = new Criteria();
			Criteria criteria2 = new Criteria();
			Criteria criteria3 = new Criteria();
			Criteria criteria4 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria1.and("doctorId").is(new ObjectId(doctorId));
				criteria3.and("invoice.doctorId").is(new ObjectId(doctorId));
				criteria4.and("receipt.doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria1.and("locationId").is(new ObjectId(locationId));
				criteria2.and("patient.locationId").is(new ObjectId(locationId)).and("patient.discarded").is(false);
				criteria3.and("invoice.locationId").is(new ObjectId(locationId)).and("invoice.discarded").is(false);
				criteria4.and("receipt.locationId").is(new ObjectId(locationId)).and("receipt.discarded").is(false);
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria1.and("hospitalId").is(new ObjectId(hospitalId));
				criteria2.and("patient.hospitalId").is(new ObjectId(hospitalId));
				criteria3.and("invoice.hospitalId").is(new ObjectId(hospitalId));
				criteria4.and("receipt.hospitalId").is(new ObjectId(hospitalId));
			}

			applyDateCriteria(criteria4, "receipt.receivedDate", fromDate, toDate);
			applyDateCriteria(criteria3, "invoice.invoiceDate", fromDate, toDate);

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria1),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$doctorId")
									.append("doctorName", new BasicDBObject("$first", "$doctor.firstName"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("totalDueAmount", new BasicDBObject("$sum", "$dueAmount")))),
					Aggregation.lookup("doctor_patient_invoice_cl", "doctorId", "doctorId", "invoice"),
					Aggregation.unwind("invoice"), Aggregation.match(criteria3),
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$doctorId")
									.append("invoiced", new BasicDBObject("$sum", "$invoice.grandTotal"))
									.append("doctorName", new BasicDBObject("$first", "$doctorName"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("amountDue", new BasicDBObject("$sum", "$invoice.balanceAmount"))
									.append("totalDiscount", new BasicDBObject("$sum", "$invoice.totalDiscount"))
									.append("totalDueAmount", new BasicDBObject("$first", "$totalDueAmount")))),

					Aggregation.lookup("doctor_patient_receipt_cl", "doctorId", "doctorId", "receipt"),
					Aggregation.unwind("receipt"), Aggregation.match(criteria4),
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$doctorId")
									.append("invoiced", new BasicDBObject("$first", "$invoiced"))
									.append("received", new BasicDBObject("$sum", "$receipt.amountPaid"))
									.append("amountDue", new BasicDBObject("$first", "$amountDue"))
									.append("totalDueAmount", new BasicDBObject("$first", "$totalDueAmount"))
									.append("totalDiscount", new BasicDBObject("$sum", "$invoice.totalDiscount"))
									.append("doctorName", new BasicDBObject("$first", "$doctorName")))),
					Aggregation.sort(Direction.DESC, "dueAmount"));

			List<AmountDueAnalyticsDataResponse> amountDueAnalyticsDataResponses = mongoTemplate.aggregate(aggregation,
					DoctorPatientDueAmountCollection.class, AmountDueAnalyticsDataResponse.class).getMappedResults();
			AmountDueAnalyticsDataResponse amountDueAnalyticsDataResponse = null;
			if (!DPDoctorUtils.isNullOrEmptyList(amountDueAnalyticsDataResponses))
				amountDueAnalyticsDataResponse = amountDueAnalyticsDataResponses.get(0);
			if (amountDueAnalyticsDataResponse != null) {

				response.setTotalPayment(amountDueAnalyticsDataResponse.getInvoiced());
				response.setTotalAmountDue(amountDueAnalyticsDataResponse.getAmountDue());
				response.setTotalDiscount(amountDueAnalyticsDataResponse.getTotalDiscount());
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting all analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting all analytic");
		}

		return response;

	}

	@Override
	public DailyReportAnalyticResponse getDailyReportAnalytics(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate) {
		DailyReportAnalyticResponse response = new DailyReportAnalyticResponse();
		try {
			Criteria criteria = new Criteria();
			DateTime from = null;
			DateTime to = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate))
				from = new DateTime(DateUtil.getStartOfDay(new Date(Long.parseLong(fromDate))));
			if (!DPDoctorUtils.anyStringEmpty(toDate))
				to = new DateTime(DateUtil.getEndOfDay(new Date(Long.parseLong(toDate))));
			//
			criteria.and("discarded").is(false);
			applyDateCriteria(criteria, "createdTime", fromDate, toDate);
			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.lookup("patient_cl", "patientId", "userId", "patient"), Aggregation.unwind("patient"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					Aggregation.lookup("doctor_patient_invoice_cl", "invoiceId", "_id", "invoice"),
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject().append("_id", "$_id").append("date", "$receivedDate")
									.append("patientId", "$patient.PID").append("invoiceId", "$invoiceId")
									.append("patientName", "$patient.localPatientName")
									.append("locationName", "$location.locationName")
									.append("serviceFees", new BasicDBObject("$sum", "$invoice.totalCost"))
									.append("discount", new BasicDBObject("$sum", new BasicDBObject("$map",
											new BasicDBObject("input", "$invoice.totalDiscount")
													.append("as", "discount").append("in", "$$discount.value"))))
									// Add new patient/returning patient logic
									.append("isNewPatient", new BasicDBObject("$cond",
											Arrays.asList(new BasicDBObject("$gte",
													Arrays.asList("$patient.createdTime", from)), true, false)))
									.append("isReturningPatient", new BasicDBObject("$cond",
											Arrays.asList(new BasicDBObject("$lt",
													Arrays.asList("$patient.createdTime", from)), true, false)))
									.append("totalAmountPaid", "$amountPaid")
									.append("totalAmountPending", "$balanceAmount")
									.append("advancedAmount", "$amountPaid").append("paymentMode", "$modeOfPayment")
									.append("consultingDentist",
											new BasicDBObject("$concat",
													Arrays.asList("$doctor.title", " ", "$doctor.firstName")))
									.append("serviceName", "$invoice.invoiceItems.name"))),

					Aggregation.sort(Direction.DESC, "createdTime"));
			List<DailyReportAnalyticItem> dailyReportAnalyticItems = mongoTemplate
					.aggregate(aggregation, DoctorPatientReceiptCollection.class, DailyReportAnalyticItem.class)
					.getMappedResults();

			double finalTotalAmount = 0.0;
			double finalTotalAmountPending = 0.0;
			double finalTotalDiscount = 0.0;
			double finalTotalServiceFees = 0.0;
			double finalTotalAmountByCash = 0.0;
			double finalTotalAmountByCard = 0.0;

			if (dailyReportAnalyticItems != null) {
				response.setDailyReportAnalyticItem(dailyReportAnalyticItems);
				// Use a Map to track already processed patients
				Map<String, Double> patientTotalAmountPaidMap = new HashMap<>();
				Map<String, Double> patientTotalDiscountMap = new HashMap<>();
				Map<String, Double> patientTotalAmountPendingMap = new HashMap<>();

				for (DailyReportAnalyticItem dailyReportAnalyticItem : dailyReportAnalyticItems) {
					String invoiceId = dailyReportAnalyticItem.getInvoiceId();

					// If the patientId hasn't been processed yet, add the totalAmountPaid to the
					// map
					if (!patientTotalAmountPaidMap.containsKey(invoiceId)) {
						patientTotalAmountPaidMap.put(invoiceId, dailyReportAnalyticItem.getServiceFees());
						patientTotalDiscountMap.put(invoiceId, dailyReportAnalyticItem.getDiscount());
						patientTotalAmountPendingMap.put(invoiceId, dailyReportAnalyticItem.getTotalAmountPending());
					}

					finalTotalAmount += dailyReportAnalyticItem.getTotalAmountPaid();

					// Payment Status
					dailyReportAnalyticItem.setPaymentStatus(PaymentStatusType.PAID);

					// Accumulate totals based on payment mode
					if (dailyReportAnalyticItem.getPaymentMode()
							.equals(dailyReportAnalyticItem.getPaymentMode().CASH)) {
						finalTotalAmountByCash += dailyReportAnalyticItem.getTotalAmountPaid();
					}

					if (dailyReportAnalyticItem.getPaymentMode().equals(dailyReportAnalyticItem.getPaymentMode().CARD)
							|| dailyReportAnalyticItem.getPaymentMode()
									.equals(dailyReportAnalyticItem.getPaymentMode().ONLINE)
							|| dailyReportAnalyticItem.getPaymentMode()
									.equals(dailyReportAnalyticItem.getPaymentMode().CHEQUE)
							|| dailyReportAnalyticItem.getPaymentMode()
									.equals(dailyReportAnalyticItem.getPaymentMode().UPI)
							|| dailyReportAnalyticItem.getPaymentMode()
									.equals(dailyReportAnalyticItem.getPaymentMode().WALLET)) {
						finalTotalAmountByCard += dailyReportAnalyticItem.getTotalAmountPaid();
					}
				}

				// Calculate the final total amount for unique patients
				finalTotalServiceFees = patientTotalAmountPaidMap.values().stream().mapToDouble(Double::doubleValue)
						.sum();
				finalTotalAmountPending = patientTotalAmountPendingMap.values().stream()
						.mapToDouble(Double::doubleValue).sum();
				finalTotalDiscount = patientTotalDiscountMap.values().stream().mapToDouble(Double::doubleValue).sum();
				// Set the final totals in the response
				response.setTotalAmountByCash(finalTotalAmountByCash);
				response.setTotalAmountByCard(finalTotalAmountByCard);
				response.setTotalAmountPaid(finalTotalAmount);
				response.setTotalAmountPending(finalTotalAmountPending);
				response.setTotalDiscount(finalTotalDiscount);
				response.setTotalServiceFees(finalTotalServiceFees);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting all analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting all analytic");
		}

		return response;

	}

	@Override
	public List<DischargeSummaryAnalyticsDataResponse> getDischargeSummaryAnalyticsData(String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, int page, int size) {
		List<DischargeSummaryAnalyticsDataResponse> dailyReportAnalyticItems = null;
		try {
			Criteria criteria = new Criteria();

			criteria.and("discarded").is(false);
			applyDateCriteria(criteria, "createdTime", fromDate, toDate);

			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("patient_cl", "patientId", "userId", "patient"), Aggregation.unwind("patient"),
					Aggregation.lookup("user_cl", "patientId", "_id", "user"), Aggregation.unwind("user"),
					new CustomAggregationOperation(new Document("$project", new BasicDBObject()
							.append("id", "$_id").append("patientId",
									"$patient._id")
							.append("patientName", "$patient.localPatientName")
							.append("numberOfDaysAdmitted",
									new Document("$divide",
											Arrays.asList(
													new Document("$subtract",
															Arrays.asList("$dischargeDate", "$admissionDate")),
													1000 * 60 * 60 * 24)))
							.append("mobileNumber", "$user.mobileNumber").append("createdTime", "$createdTime")
							.append("dateOfDischarge", "$dischargeDate"))),
					Aggregation.group("id").first("id").as("id").first("patientId").as("patientId").first("patientName")
							.as("patientName").first("numberOfDaysAdmitted").as("numberOfDaysAdmitted")
							.first("mobileNumber").as("mobileNumber").first("dateOfDischarge").as("dateOfDischarge")
							.first("createdTime").as("createdTime"),
					Aggregation.sort(Direction.DESC, "createdTime"));
			dailyReportAnalyticItems = mongoTemplate
					.aggregate(aggregation, "discharge_summary_cl", DischargeSummaryAnalyticsDataResponse.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting all analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting all analytic");
		}

		return dailyReportAnalyticItems;
	}

	@Override
	public List<ExpenseAnalyticsTypeDataResponse> getExpenseAnalyticsTypeData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate) {
		List<ExpenseAnalyticsTypeDataResponse> expenseAnalytics = null;
		try {
			Criteria criteria = new Criteria();

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
			applyDateCriteria(criteria, "createdTime", fromDate, toDate);

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.group("expenseType").first("expenseType").as("expenseType").sum("cost").as("cost"),
					Aggregation.sort(Sort.Direction.DESC, "cost"));

			expenseAnalytics = mongoTemplate
					.aggregate(aggregation, "doctor_expense_cl", ExpenseAnalyticsTypeDataResponse.class)
					.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting all analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting all analytic");
		}

		return expenseAnalytics;
	}

	@Scheduled(cron = "0 0 22 * * ?", zone = "IST")
	@Override
	public Boolean getDailyReportAnalyticstoDoctor() {
		System.out.println("Scheduled method triggered");
		Boolean response = false;
		try {
			Date todayDate;
			todayDate = new Date();
			ZoneId defaultZoneId = ZoneId.systemDefault();
			Instant instant = todayDate.toInstant();
			LocalDate localDate = instant.atZone(defaultZoneId).toLocalDate();
			LocalDateTime startOfDay = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);
			LocalDateTime endOfDay = LocalDateTime.of(localDate, LocalTime.MAX);

			// Fetch payments for today
			Criteria paymentCriteria = Criteria.where("receivedDate").gte(startOfDay).lte(endOfDay).and("discarded")
					.is(false);
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(paymentCriteria),
					Aggregation.lookup("patient_cl", "patientId", "userId", "patient"), Aggregation.unwind("patient"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					Aggregation.lookup("user_cl", "patientId", "_id", "user"), Aggregation.unwind("user"),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.sort(Sort.Direction.DESC, "receivedDate"),
					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
							.append("date", new BasicDBObject("$first", "$receivedDate"))
							.append("patientName", new BasicDBObject("$first", "$patient.localPatientName"))
							.append("uniqueReceiptId", new BasicDBObject("$first", "$uniqueReceiptId"))
							.append("uniqueInvoiceId", new BasicDBObject("$first", "$uniqueInvoiceId"))
							.append("locationId", new BasicDBObject("$first", "$location._id"))
							.append("amountPaid", new BasicDBObject("$first", "$amountPaid"))
							.append("modeOfPayment", new BasicDBObject("$first", "$modeOfPayment"))
							.append("receiptType", new BasicDBObject("$first", "$receiptType"))
							.append("localPatientName", new BasicDBObject("$first", "$patient.localPatientName"))
							.append("firstName", new BasicDBObject("$first", "$user.firstName"))
							.append("mobileNumber", new BasicDBObject("$first", "$user.mobileNumber"))
							.append("doctorName", new BasicDBObject("$first", "$doctor.firstName"))
							.append("usedAdvanceAmount", new BasicDBObject("$first", "$usedAdvanceAmount")))));

			List<PaymentDetailsAnalyticsDataResponse> receiptCollections = Optional
					.ofNullable(mongoTemplate.aggregate(aggregation, DoctorPatientReceiptCollection.class,
							PaymentDetailsAnalyticsDataResponse.class).getMappedResults())
					.orElse(Collections.emptyList());

			// Group by locationId for quick access
			Map<String, List<PaymentDetailsAnalyticsDataResponse>> locationPayments = receiptCollections != null
					? receiptCollections.stream()
							.collect(Collectors.groupingBy(PaymentDetailsAnalyticsDataResponse::getLocationId))
					: new HashMap<>();

			// Loop through all locations
			List<LocationCollection> allLocations = locationRepository.findAll();

			for (LocationCollection locationCollection : allLocations) {

				String locationId = locationCollection.getId().toString();

				List<PaymentDetailsAnalyticsDataResponse> locationSpecificReceipts = locationPayments
						.getOrDefault(locationId, new ArrayList<>());

				if (locationSpecificReceipts == null || locationSpecificReceipts.isEmpty()) {

					// No payments for this location – add blank row
					locationSpecificReceipts = new ArrayList<>();
					PaymentDetailsAnalyticsDataResponse blankEntry = new PaymentDetailsAnalyticsDataResponse();
					blankEntry.setLocalPatientName("No Patient for Today");
					blankEntry.setDate(null);
					blankEntry.setModeOfPayment(null);
					blankEntry.setUsedAdvanceAmount(0.0);
					blankEntry.setAmountPaid(0.0);
					locationSpecificReceipts.add(blankEntry);
				}

				response = createPdfAndSendEmail(locationSpecificReceipts, locationCollection);
			}
		} catch (Exception e) {
			logger.error("Error in getDailyReportAnalyticstoDoctor(): ", e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private boolean createPdfAndSendEmail(List<PaymentDetailsAnalyticsDataResponse> locationSpecificReceipts,
			LocationCollection locationCollection) {
		boolean response = false;
		try {

			ByteArrayOutputStream byteArrayOutputStream = createPdf(locationSpecificReceipts,
					"Daily Payments Report : " + locationCollection.getLocationName(), false, null, null);

			// Send Email
			EmailListCollection emailListCollection = emailListRepository.findByLocationId(locationCollection.getId());
			if (emailListCollection != null && !emailListCollection.getEmails().isEmpty()) {
				List<String> emails = emailListCollection.getEmails();
				byte[] pdfBytes = byteArrayOutputStream.toByteArray();

				String subject = locationCollection.getLocationName() + " - Daily Payment Collection Report";

				String body = "Hello,\n\n" + "Please find attached the payments report for the last 24 hours.\n\n"
						+ "Thank you for your attention.\n\n\n\n" + "--- Auto-generated Report\n"
						+ "Do not reply to this email";
				// Replace newline characters with HTML line breaks
				String htmlBody = body.replace("\n", "<br/>");
				mailService.sendEmailWithPdf(emails, subject, htmlBody, pdfBytes);
				response = true;
			}
		} catch (Exception e) {
			logger.error("Error generating/sending PDF: ", e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private ByteArrayOutputStream createPdf(List<PaymentDetailsAnalyticsDataResponse> payments, String title,
			Boolean isWeekly, LocalDate startOfWeek, LocalDate endOfWeek) {

		// Create PDF
		com.lowagie.text.Document document = new com.lowagie.text.Document();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {
			DateTime now = DateTime.now();
			PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

			// Add page event handler to show footer on every page
			writer.setPageEvent(new PdfPageEventHelper() {
				@Override
				public void onEndPage(PdfWriter writer, com.lowagie.text.Document document) {
					PdfContentByte cb = writer.getDirectContent();
					Font footerFont = new Font(Font.HELVETICA, 8, Font.ITALIC);
					Phrase footer = new Phrase("Auto-generated PDF | Powered by Healthcoco", footerFont);

					// Precise bottom right corner positioning
					float x = document.right() - 10; // 10 points from right margin
					float y = document.bottom() + 10; // 10 points above bottom margin

					ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer, x, y, 0);
				}
			});
			document.open();

			Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
			Paragraph titleParagraph = new Paragraph(title, titleFont);
			document.add(titleParagraph);
			Paragraph dateParagraph;
			Font dateFont = new Font(Font.HELVETICA, 12, Font.BOLD);
			if (isWeekly) {
				dateParagraph = new Paragraph("Date: " + new DateTime(startOfWeek.toString()).toString("dd-MM-yyyy")
						+ " to " + new DateTime(endOfWeek.toString()).toString("dd-MM-yyyy"), dateFont);
			} else {
				dateParagraph = new Paragraph("Date: " + now.toString("dd-MM-yyyy"), dateFont);
			}
			document.add(dateParagraph);
			document.add(new Paragraph(" "));

			float[] columnWidths = { 2f, 1.5f, 1.5f, 1.5f };
			PdfPTable table = new PdfPTable(columnWidths);
			table.setWidthPercentage(100);

			String[] headers = { "Patient Name", "Receipt Date", "Payment Method", "Paid Amount" };
			Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);

			for (String header : headers) {
				PdfPCell cell = new PdfPCell(new Paragraph(header, headerFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setPadding(5);
				table.addCell(cell);
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			double totalAmount = 0.0;
			double[] totals = new double[6];

			for (PaymentDetailsAnalyticsDataResponse payment : payments) {

				String patientName = payment.getLocalPatientName() != null ? payment.getLocalPatientName() : "";
				String formattedDate = payment.getDate() != null ? sdf.format(payment.getDate()) : "--";
				String modeOfPayment = payment.getModeOfPayment() != null ? payment.getModeOfPayment().name() : "";
				Double usedAdvanceAmount = payment.getUsedAdvanceAmount() != null ? payment.getUsedAdvanceAmount()
						: 0.0;
//				System.out.println("payment.getAmountPaid()" + payment.getAmountPaid());
				Double amountPaid = payment.getAmountPaid() != null ? payment.getAmountPaid() : 0.0;
				table.addCell(createCenteredCell(patientName));
				table.addCell(createCenteredCell(formattedDate));
				table.addCell(createCenteredCell(modeOfPayment));
//				table.addCell(createCenteredCell(String.valueOf(usedAdvanceAmount)));
				table.addCell(createCenteredCell(String.valueOf(formatLargeNumber(amountPaid))));

				totalAmount += amountPaid;
				String mode = payment.getModeOfPayment() != null ? payment.getModeOfPayment().name().toUpperCase()
						: "UNKNOWN";
				switch (mode) {
				case "CASH":
					totals[0] += amountPaid;
					break;
				case "ONLINE":
					totals[1] += amountPaid;
					break;
				case "WALLET":
					totals[2] += amountPaid;
					break;
				case "CARD":
					totals[3] += amountPaid;
					break;
				case "UPI":
					totals[4] += amountPaid;
					break;
				case "CHEQUE":
					totals[5] += amountPaid;
					break;
				default:
					// optionally log or track unknown payment type
					break;
				}
			}

			document.add(table);

			// Summary Table with Dynamic Columns
			document.add(new Paragraph(" "));

			// Determine non-zero columns
			List<String> activeSummaryHeaders = new ArrayList<>();
			List<Double> activeTotals = new ArrayList<>();

			String[] summaryHeaders = { "Cash Amount", "Card Amount", "Online Amount", "Wallet Amount", "UPI Amount",
					"Cheque Amount" };

			// Collect non-zero totals and corresponding headers
			for (int i = 0; i < totals.length; i++) {
				if (totals[i] > 0) {
					activeSummaryHeaders.add(summaryHeaders[i]);
					activeTotals.add(totals[i]);
				}
			}

			// Add Total Amount
			activeSummaryHeaders.add("Total Amount");
			activeTotals.add(totalAmount);

			// Create dynamic width array
			float[] summaryColumnWidths = new float[activeSummaryHeaders.size()];
			Arrays.fill(summaryColumnWidths, 1f);

			PdfPTable summaryTable = new PdfPTable(summaryColumnWidths);
			summaryTable.setWidthPercentage(100);
			Font headerFont1 = new Font(Font.HELVETICA, 12, Font.BOLD); // Increased font size to 14

			// Add headers
			for (String header : activeSummaryHeaders) {
				PdfPCell cell = new PdfPCell(new Paragraph(header, headerFont1));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setPadding(5);
				summaryTable.addCell(cell);
			}

			for (Double total : activeTotals) {
				summaryTable.addCell(createCenteredCell(formatLargeNumber(total)));
			}

			document.add(summaryTable);

			document.close();

		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("Error generating/sending PDF: ", e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return byteArrayOutputStream;
	}

	// Formatting method to handle large numbers
	private String formatLargeNumber(double number) {
		// Use DecimalFormat to avoid scientific notation
		DecimalFormat df = new DecimalFormat("#,##0.00");
		return df.format(number);
	}

	// Helper method to create a centered cell with a paragraph
	private PdfPCell createCenteredCell(String text) {
		PdfPCell cell = new PdfPCell(new Paragraph(text));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(5);
		return cell;
	}

	@Scheduled(cron = "0 0 22 * * 6", zone = "IST")
	@Override
	public Boolean getWeeklyReportAnalyticstoDoctor() {
		System.out.println("Scheduled method triggered");
		Boolean response = false;
		try {
			LocalDate today = LocalDate.now();
			LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
			LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

			LocalDateTime startOfWeekDateTime = startOfWeek.atStartOfDay();
			LocalDateTime endOfWeekDateTime = endOfWeek.atTime(LocalTime.MAX);

			Criteria paymentCriteria = Criteria.where("receivedDate").gte(startOfWeekDateTime).lte(endOfWeekDateTime)
					.and("discarded").is(false);

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(paymentCriteria),
					Aggregation.lookup("patient_cl", "patientId", "userId", "patient"), Aggregation.unwind("patient"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					Aggregation.lookup("user_cl", "patientId", "_id", "user"), Aggregation.unwind("user"),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.sort(Sort.Direction.DESC, "receivedDate"),
					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
							.append("date", new BasicDBObject("$first", "$receivedDate"))
							.append("patientName", new BasicDBObject("$first", "$patient.localPatientName"))
							.append("uniqueReceiptId", new BasicDBObject("$first", "$uniqueReceiptId"))
							.append("uniqueInvoiceId", new BasicDBObject("$first", "$uniqueInvoiceId"))
							.append("locationId", new BasicDBObject("$first", "$location._id"))
							.append("amountPaid", new BasicDBObject("$first", "$amountPaid"))
							.append("modeOfPayment", new BasicDBObject("$first", "$modeOfPayment"))
							.append("receiptType", new BasicDBObject("$first", "$receiptType"))
							.append("localPatientName", new BasicDBObject("$first", "$patient.localPatientName"))
							.append("firstName", new BasicDBObject("$first", "$user.firstName"))
							.append("mobileNumber", new BasicDBObject("$first", "$user.mobileNumber"))
							.append("doctorName", new BasicDBObject("$first", "$doctor.firstName"))
							.append("usedAdvanceAmount", new BasicDBObject("$first", "$usedAdvanceAmount")))));
			List<PaymentDetailsAnalyticsDataResponse> receiptCollections = mongoTemplate.aggregate(aggregation,
					DoctorPatientReceiptCollection.class, PaymentDetailsAnalyticsDataResponse.class).getMappedResults();

			// Group by locationId for quick access
			Map<String, List<PaymentDetailsAnalyticsDataResponse>> locationPayments = (receiptCollections != null)
					? receiptCollections.stream()
							.collect(Collectors.groupingBy(PaymentDetailsAnalyticsDataResponse::getLocationId))
					: new HashMap<>();

			// Loop through all locations
			List<LocationCollection> allLocations = locationRepository.findAll();
			for (LocationCollection locationCollection : allLocations) {
				String locationId = locationCollection.getId().toHexString();
				List<PaymentDetailsAnalyticsDataResponse> locationSpecificReceipts = locationPayments.get(locationId);

				if (locationSpecificReceipts == null || locationSpecificReceipts.isEmpty()) {
					// No payments for this location – add blank row
					locationSpecificReceipts = new ArrayList<>();
					PaymentDetailsAnalyticsDataResponse blankEntry = new PaymentDetailsAnalyticsDataResponse();
					blankEntry.setLocalPatientName("No Patient for Today");
					blankEntry.setDate(null);
					blankEntry.setModeOfPayment(null);
					blankEntry.setUsedAdvanceAmount(0.0);
					blankEntry.setAmountPaid(0.0);
					locationSpecificReceipts.add(blankEntry);
				}

				response = createPdfAndSendEmailforWeeklyData(locationSpecificReceipts, locationCollection);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private Boolean createPdfAndSendEmailforWeeklyData(
			List<PaymentDetailsAnalyticsDataResponse> locationSpecificReceipts, LocationCollection locationCollection) {
		boolean response = false;
		LocalDate today = LocalDate.now();
		LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
		LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

		try {
			ByteArrayOutputStream byteArrayOutputStream = createPdf(locationSpecificReceipts,
					"Weekly Payments Report : " + locationCollection.getLocationName(), true, startOfWeek, endOfWeek);

			// Send Email
			EmailListCollection emailListCollection = emailListRepository.findByLocationId(locationCollection.getId());
			if (emailListCollection != null && !emailListCollection.getEmails().isEmpty()) {
				List<String> emails = emailListCollection.getEmails();
				byte[] pdfBytes = byteArrayOutputStream.toByteArray();

				String subject = locationCollection.getLocationName() + " - Weekly Payment Collection Report";
				String body = "Hello,\n\n" + "Please find attached the payments report for the last week.\n\n"
						+ "Thank you for your attention.\n\n\n\n" + "--- Auto-generated Report\n"
						+ "Do not reply to this email";
				// Replace newline characters with HTML line breaks
				String htmlBody = body.replace("\n", "<br/>");
				mailService.sendEmailWithPdf(emails, subject, htmlBody, pdfBytes);
				response = true;
			}
		} catch (Exception e) {
			logger.error("Error generating/sending PDF: ", e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
}