package com.dpdocter.services.impl;

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
import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.DoctorPatientInvoiceCollection;
import com.dpdocter.collections.DoctorPatientLedgerCollection;
import com.dpdocter.collections.DoctorPatientReceiptCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.PatientQueueCollection;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.enums.AppointmentCreatedBy;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.ModeOfPayment;
import com.dpdocter.enums.PatientAnalyticType;
import com.dpdocter.enums.PrescriptionItems;
import com.dpdocter.enums.SearchType;
import com.dpdocter.enums.UnitType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.AmountDueAnalyticsDataResponse;
import com.dpdocter.response.AppointmentAnalyticResponse;
import com.dpdocter.response.AppointmentAverageTimeAnalyticResponse;
import com.dpdocter.response.AppointmentCountAnalyticResponse;
import com.dpdocter.response.AppointmentDeatilAnalyticResponse;
import com.dpdocter.response.DiagnosticTestsAnalyticsData;
import com.dpdocter.response.DoctorAppointmentAnalyticResponse;
import com.dpdocter.response.DoctorPatientAnalyticResponse;
import com.dpdocter.response.DoctorTreatmentAnalyticResponse;
import com.dpdocter.response.DrugsAnalyticsData;
import com.dpdocter.response.IncomeAnalyticsDataResponse;
import com.dpdocter.response.InvoiceAnalyticsDataDetailResponse;
import com.dpdocter.response.PatientAnalyticResponse;
import com.dpdocter.response.PaymentAnalyticsDataResponse;
import com.dpdocter.response.PaymentDetailsAnalyticsDataResponse;
import com.dpdocter.services.AnalyticsService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
@Transactional
public class AnalyticsServiceImpl implements AnalyticsService {

	@Autowired
	private MongoTemplate mongoTemplate;

	Logger logger = Logger.getLogger(AnalyticsServiceImpl.class);

	@Override
	public DoctorAppointmentAnalyticResponse getAppointmentAnalytic(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate) {
		DoctorAppointmentAnalyticResponse data = new DoctorAppointmentAnalyticResponse();
		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		Criteria criteria = new Criteria("locationId").is(new ObjectId(locationId)).and("hospitalId")
				.is(new ObjectId(hospitalId)).and("isPatientDiscarded").is(false);
		DateTime fromTime = null;
		DateTime toTime = null;
		if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
			localCalendar.setTime(new Date(Long.parseLong(fromDate)));
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);
			fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			criteria.and("updatedTime").gte(fromTime);

			localCalendar.setTime(new Date(Long.parseLong(toDate)));
			currentDay = localCalendar.get(Calendar.DATE);
			currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			currentYear = localCalendar.get(Calendar.YEAR);
			toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			criteria.and("updatedTime").lte(toTime);

		} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
			localCalendar.setTime(new Date(Long.parseLong(fromDate)));
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);
			fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			criteria.and("updatedTime").gte(fromTime);

			localCalendar.setTime(new Date(Long.parseLong(fromDate)));
			toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			criteria.and("updatedTime").lte(toTime);

		} else {
			localCalendar.setTime(new Date());
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);
			fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			criteria.and("updatedTime").gte(fromTime);
			toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			criteria.and("updatedTime").lte(toTime);

		}
		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			criteria.and("doctorId").is(new ObjectId(doctorId));
		}

		data.setTotalNoOfAppointment((int) mongoTemplate.count(new Query(criteria), AppointmentCollection.class));
		data.setCancelBydoctor((int) mongoTemplate.count(
				new Query(criteria.and("cancelledBy").is(AppointmentCreatedBy.DOCTOR.getType())),
				AppointmentCollection.class));
		data.setCancelByPatient((int) mongoTemplate.count(
				new Query(criteria.and("cancelledBy").is(AppointmentCreatedBy.PATIENT.getType())),
				AppointmentCollection.class));
		int appointmentCount = (int) mongoTemplate.count(new Query(criteria.and("state").is("CONFIRM")),
				AppointmentCollection.class);
		data.setBookedAppointmentInPercent(
				((100 * (double) appointmentCount) / (double) data.getTotalNoOfAppointment()));
		appointmentCount = (int) mongoTemplate.count(new Query(criteria.and("status").is("SCHEDULED")),
				AppointmentCollection.class);
		data.setBookedAppointmentInPercent((100 * (double) appointmentCount) / (double) data.getTotalNoOfAppointment());
		return data;
	}

	@Override
	public DoctorPatientAnalyticResponse getPatientAnalytic(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate) {
		DoctorPatientAnalyticResponse data = new DoctorPatientAnalyticResponse();
		Date date = new Date();
		return data;
	}

	@Override
	public List<DoctorTreatmentAnalyticResponse> getTreatmentAnalytic(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String searchTerm) {
		List<DoctorTreatmentAnalyticResponse> data = new ArrayList<DoctorTreatmentAnalyticResponse>();
		Date date = new Date();
		return data;
	}

	@Override
	public List<PatientAnalyticResponse> getPatientCount(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String queryType, String searchType, String searchTerm) {
		List<PatientAnalyticResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("fromDate").gte(fromTime);
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("toDate").lte(toTime);
			}
			Aggregation aggregation = null;
			CustomAggregationOperation aggregationOperation = null;
			switch (PatientAnalyticType.valueOf(queryType.toUpperCase())) {
			case NEW_PATIENT: {
				ProjectionOperation projectList = new ProjectionOperation(Fields.from(
						Fields.field("patients._id", "$userId"),
						Fields.field("patients.localPatientName", "$localPatientName"),
						Fields.field("patients.pid", "$PID"), Fields.field("patients.firstName", "$firstName"),
						Fields.field("patients.registrationDate", "$registrationDate"),
						Fields.field("patients.createdTime", "$createdTime"), Fields.field("date", "$createdTime")));

				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
				}
				if (!DPDoctorUtils.anyStringEmpty(locationId)) {
					criteria = criteria.and("locationId").is(new ObjectId(locationId));
				}
				if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
					criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
				}
				switch (SearchType.valueOf(searchType.toUpperCase())) {
				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("day", "$day").append("month", "$month").append("year",
													"$year")).append("day", new BasicDBObject("$first", "$day"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("week", new BasicDBObject("$first", "$week"))
															.append("date", new BasicDBObject("$first", "$date"))
															.append("patients",
																	new BasicDBObject("$push", "$patients"))));

					break;
				}

				case WEEKLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("day", new BasicDBObject("$first", "$day"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("week", new BasicDBObject("$first", "$week"))
													.append("date", new BasicDBObject("$first", "$date"))
													.append("patients", new BasicDBObject("$push", "$patients"))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("month", "$month").append("year", "$year"))
													.append("day", new BasicDBObject("$first", "$day"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("week", new BasicDBObject("$first", "$week"))
													.append("date", new BasicDBObject("$first", "$date"))
													.append("patients", new BasicDBObject("$push", "$patients"))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("week", new BasicDBObject("$first", "$week"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("patients", new BasicDBObject("$push", "$patients"))));

					break;

				}
				default:
					break;
				}
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime").extractMonth()
								.as("month").and("createdTime").extractYear().as("year").and("createdTime")
								.extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "date")));
				AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
						"patient_cl", PatientAnalyticResponse.class);
				response = aggregationResults.getMappedResults();
				break;
			}

			case CITY_WISE: {
				ProjectionOperation projectList = new ProjectionOperation(Fields.from(
						Fields.field("patient._id", "$userId"),
						Fields.field("patient.localPatientName", "$localPatientName"),
						Fields.field("patient.pid", "$PID"), Fields.field("patient.firstName", "$user.firstName"),
						Fields.field("patient.registrationDate", "$registrationDate"),
						Fields.field("patient.createdTime", "$createdTime"), Fields.field("date", "$createdTime"),
						Fields.field("city", "$address.city")));
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					criteria.and("address.city").is(searchTerm);
				}
				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
				}
				if (!DPDoctorUtils.anyStringEmpty(locationId)) {
					criteria = criteria.and("locationId").is(new ObjectId(locationId));
				}
				if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
					criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
				}
				switch (SearchType.valueOf(searchType.toUpperCase())) {
				case DAILY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year")
											.append("city", "$city")).append("day", new BasicDBObject("$first", "$day"))
													.append("city", new BasicDBObject("$first", "$city"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("date", new BasicDBObject("$first", "$date"))
													.append("patients", new BasicDBObject("$push", "$patient"))));

					break;
				}
				case WEEKLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year")
											.append("city", "$city")).append("day", new BasicDBObject("$first", "$day"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("week", new BasicDBObject("$first", "$week"))
													.append("city", new BasicDBObject("$first", "$city"))
													.append("date", new BasicDBObject("$first", "$date"))
													.append("patients", new BasicDBObject("$push", "$patient"))));
					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("month", "$month").append("year", "$year").append("city",
											"$city")).append("day", new BasicDBObject("$first", "$day"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("city", new BasicDBObject("$first", "$city"))
													.append("date", new BasicDBObject("$first", "$date"))
													.append("patients", new BasicDBObject("$push", "$patient"))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year").append("city", "$city"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("city", new BasicDBObject("$first", "$city"))
									.append("patients", new BasicDBObject("$push", "$patient"))));

					break;

				}
				default:
					break;
				}
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime").extractMonth()
								.as("month").and("createdTime").extractYear().as("year").and("createdTime")
								.extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.DESC, "date")));
				AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
						"patient_cl", PatientAnalyticResponse.class);
				response = aggregationResults.getMappedResults();
				break;
			}
			case LOCALITY_WISE: {
				ProjectionOperation projectList = new ProjectionOperation(Fields.from(
						Fields.field("patient._id", "$userId"),
						Fields.field("patient.localPatientName", "$localPatientName"),
						Fields.field("patient.pid", "$PID"), Fields.field("patient.firstName", "$user.firstName"),
						Fields.field("patient.registrationDate", "$registrationDate"),
						Fields.field("patient.createdTime", "$createdTime"), Fields.field("date", "$createdTime"),
						Fields.field("city", "$address.city")));
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					criteria.and("address.locality").is(searchTerm);
				}

				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
				}
				if (!DPDoctorUtils.anyStringEmpty(locationId)) {
					criteria = criteria.and("locationId").is(new ObjectId(locationId));
				}
				if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
					criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
				}
				switch (SearchType.valueOf(searchType.toUpperCase())) {
				case DAILY: {

					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("day", "$day").append("month", "$month").append("year",
													"$year")).append("day", new BasicDBObject("$first", "$day"))
															.append("city", new BasicDBObject("$first", "$city"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("date", new BasicDBObject("$first", "$date"))
															.append("patients",
																	new BasicDBObject("$push", "$patient"))));
					break;
				}

				case WEEKLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("day", new BasicDBObject("$first", "$day"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("week", new BasicDBObject("$first", "$week"))
													.append("city", new BasicDBObject("$first", "$city"))
													.append("date", new BasicDBObject("$first", "$date"))
													.append("patients", new BasicDBObject("$push", "$patient"))));
					break;
				}

				case MONTHLY: {

					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("month", "$month").append("year", "$year"))
													.append("day", new BasicDBObject("$first", "$day"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("city", new BasicDBObject("$first", "$city"))
													.append("date", new BasicDBObject("$first", "$date"))
													.append("patients", new BasicDBObject("$push", "$patient"))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("city", new BasicDBObject("$first", "$city"))
									.append("patients", new BasicDBObject("$push", "$patient"))));

					break;

				}
				default:
					break;
				}
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
						projectList.and("createdTime").extractDayOfMonth().as("day").and("date").extractMonth()
								.as("month").and("createdTime").extractYear().as("year"),
						aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
				AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
						"patient_cl", PatientAnalyticResponse.class);
				response = aggregationResults.getMappedResults();
				break;
			}
			case IN_GROUP: {
				ProjectionOperation projectList = new ProjectionOperation(
						Fields.from(Fields.field("patient.id", "$patient.userId"),
								Fields.field("patient.localPatientName", "$patient.localPatientName"),
								Fields.field("patient.PID", "$patient.PID"),
								Fields.field("patient.firstName", "$patient.firstName"),
								Fields.field("patient.registrationDate", "$patient.registrationDate"),
								Fields.field("patient.createdTime", "$patient.createdTime"),
								Fields.field("groupName", "$group.name"), Fields.field("date", "$createdTime"),
								Fields.field("groupId", "$group._id")));
				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria = criteria.and("patient.doctorId").is(new ObjectId(doctorId)).and("group.doctorId")
							.is(new ObjectId(doctorId));
				}
				if (!DPDoctorUtils.anyStringEmpty(locationId)) {
					criteria = criteria.and("patient.locationId").is(new ObjectId(locationId)).and("group.locationId")
							.is(new ObjectId(locationId));
				}
				if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
					criteria = criteria.and("patient.hospitalId").is(new ObjectId(hospitalId)).and("group.hospitalId")
							.is(new ObjectId(hospitalId));
				}

				switch (SearchType.valueOf(searchType.toUpperCase())) {
				case DAILY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year")
											.append("groupId", "$groupId"))
													.append("day", new BasicDBObject("$first", "$day"))
													.append("city", new BasicDBObject("$first", "$city"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("groupName", new BasicDBObject("$first", "$groupName"))
													.append("groupId", new BasicDBObject("$first", "$groupId"))
													.append("date", new BasicDBObject("$first", "$date"))
													.append("patients", new BasicDBObject("$push", "$patient"))));
					break;
				}

				case WEEKLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year")
											.append("groupId", "$groupId"))
													.append("day", new BasicDBObject("$first", "$day"))
													.append("city", new BasicDBObject("$first", "$city"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("groupName", new BasicDBObject("$first", "$groupName"))
													.append("groupId", new BasicDBObject("$first", "$groupId"))
													.append("date", new BasicDBObject("$first", "$date"))
													.append("patients", new BasicDBObject("$push", "$patient"))));
					break;
				}

				case MONTHLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("month", "$month").append("year", "$year").append("groupId",
											"$groupId")).append("day", new BasicDBObject("$first", "$day"))
													.append("city", new BasicDBObject("$first", "$city"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("groupName", new BasicDBObject("$first", "$groupName"))
													.append("groupId", new BasicDBObject("$first", "$groupId"))
													.append("date", new BasicDBObject("$first", "$date"))
													.append("patients", new BasicDBObject("$push", "$patient"))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("year", "$year").append("groupId", "$groupId"))
													.append("day", new BasicDBObject("$first", "$day"))
													.append("city", new BasicDBObject("$first", "$city"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("groupName", new BasicDBObject("$first", "$groupName"))
													.append("groupId", new BasicDBObject("$first", "$groupId"))
													.append("date", new BasicDBObject("$first", "$date"))
													.append("patients", new BasicDBObject("$push", "$patient"))));

					break;

				}
				default:
					break;
				}
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
						Aggregation.unwind("patient"), Aggregation.lookup("group_cl", "groupId", "_id", "group"),
						Aggregation.unwind("group"), Aggregation.match(criteria),
						projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime").extractMonth()
								.as("month").and("createdTime").extractYear().as("year").and("createdTime")
								.extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.DESC, "date")));

				AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
						PatientGroupCollection.class, PatientAnalyticResponse.class);
				response = aggregationResults.getMappedResults();
				break;
			}
			case TOP_10_VISITED: {
				ProjectionOperation projectList = new ProjectionOperation(Fields.from(
						Fields.field("patient.id", "$userId"),
						Fields.field("patient.localPatientName", "$localPatientName"),
						Fields.field("patient.PID", "$PID"), Fields.field("patient.firstName", "$user.firstName"),
						Fields.field("patient.registrationDate", "$registrationDate"),
						Fields.field("patient.createdTime", "$visit.createdTime"),
						Fields.field("patient.visitedTime", "$visit.time"),
						Fields.field("date", "$visit.createdTime")));
				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria = criteria.and("patient.doctorId").is(new ObjectId(doctorId)).and("visit.doctorId")
							.is(new ObjectId(doctorId));
				}
				if (!DPDoctorUtils.anyStringEmpty(locationId)) {
					criteria = criteria.and("patient.locationId").is(new ObjectId(locationId)).and("visit.locationId")
							.is(new ObjectId(locationId));
				}
				if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
					criteria = criteria.and("patient.hospitalId").is(new ObjectId(hospitalId)).and("visit.hospitalId")
							.is(new ObjectId(hospitalId));
				}

				switch (SearchType.valueOf(searchType.toUpperCase())) {
				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("day", "$day").append("month", "$month").append("year",
													"$year")).append("day", new BasicDBObject("$first", "$day"))
															.append("city", new BasicDBObject("$first", "$city"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))

															.append("createdTime",
																	new BasicDBObject("$first", "$createdTime"))
															.append("data", new BasicDBObject("$push", "$patient"))
															.append("count", new BasicDBObject("$size", "$data"))));
					break;
				}

				case WEEKLY: {

					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("week", "$week").append("month", "$month").append("year",
													"$year")).append("day", new BasicDBObject("$first", "$day"))
															.append("city", new BasicDBObject("$first", "$city"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("createdTime",
																	new BasicDBObject("$first", "$createdTime"))
															.append("data", new BasicDBObject("$push", "$patient"))
															.append("count", new BasicDBObject("$size", "$data"))));
					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("month", "$month").append("year", "$year"))
													.append("day", new BasicDBObject("$first", "$day"))
													.append("city", new BasicDBObject("$first", "$city"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("createdTime", new BasicDBObject("$first", "$createdTime"))
													.append("data", new BasicDBObject("$push", "$patient"))
													.append("count", new BasicDBObject("$size", "$data"))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("city", new BasicDBObject("$first", "$city"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("data", new BasicDBObject("$push", "$patient"))));

					break;

				}

				default:
					break;
				}
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "userId", "_id", "user"),
						Aggregation.unwind("user"),
						Aggregation.lookup("patient_visit_cl", "patientId", "patientId", "visit"),
						Aggregation.unwind("visit"), Aggregation.match(criteria),
						projectList.and("visit.createdTime").extractDayOfMonth().as("day").and("visit.createdTime")
								.extractMonth().as("month").and("visit.createdTime").extractYear().as("year")
								.and("visit.createdTime").extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.DESC, "count")));

				AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
						"patient_cl", PatientAnalyticResponse.class);
				response = aggregationResults.getMappedResults();
				break;

			}

			case VISITED_PATIENT: {
				ProjectionOperation projectList = new ProjectionOperation(Fields.from(
						Fields.field("patient.id", "$patient.userId"),
						Fields.field("patient.localPatientName", "$patient.localPatientName"),
						Fields.field("patient.PID", "$patient.PID"),
						Fields.field("patient.firstName", "$user.firstName"),
						Fields.field("patient.registrationDate", "$patient.registrationDate"),
						Fields.field("patient.createdTime", "$createdTime"),
						Fields.field("patient.visitedTime", "$time"), Fields.field("createdTime", "$createdTime")));
				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria = criteria.and("patient.doctorId").is(new ObjectId(doctorId)).and("patient.doctorId")
							.is(new ObjectId(doctorId));
				}
				if (!DPDoctorUtils.anyStringEmpty(locationId)) {
					criteria = criteria.and("patient.locationId").is(new ObjectId(locationId)).and("patient.locationId")
							.is(new ObjectId(locationId));
				}
				if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
					criteria = criteria.and("patient.hospitalId").is(new ObjectId(hospitalId)).and("patient.hospitalId")
							.is(new ObjectId(hospitalId));
				}
				switch (SearchType.valueOf(searchType.toUpperCase())) {
				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("day", "$day").append("month", "$month").append("year",
													"$year")).append("day", new BasicDBObject("$first", "$day"))
															.append("city", new BasicDBObject("$first", "$city"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("date", new BasicDBObject("$first", "$createdTime"))
															.append("patients",
																	new BasicDBObject("$push", "$patient"))));
					break;
				}

				case WEEKLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year")
											.append("groupId", "$groupId"))
													.append("day", new BasicDBObject("$first", "$day"))
													.append("city", new BasicDBObject("$first", "$city"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("date", new BasicDBObject("$first", "$createdTime"))
													.append("patients", new BasicDBObject("$push", "$patient"))));
					break;
				}

				case MONTHLY: {

					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("month", "$month").append("year", "$year"))
													.append("day", new BasicDBObject("$first", "$day"))
													.append("city", new BasicDBObject("$first", "$city"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("date", new BasicDBObject("$first", "$createdTime"))
													.append("patients", new BasicDBObject("$push", "$patient"))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("city", new BasicDBObject("$first", "$city"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$createdTime"))
									.append("patients", new BasicDBObject("$push", "$patient"))));

					break;

				}

				default:
					break;
				}
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "patientId", "_id", "user"),
						Aggregation.unwind("user"), Aggregation.lookup("patient_cl", "userId", "patientId", "patient"),
						Aggregation.unwind("patient"), Aggregation.match(criteria),
						projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime").extractMonth()
								.as("month").and("createdTime").extractYear().as("year").and("createdTime")
								.extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.DESC, "date")));
				AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
						"patient_visit_cl", PatientAnalyticResponse.class);
				response = aggregationResults.getMappedResults();

				break;

			}

			default:
				break;

			}
			for (PatientAnalyticResponse patientAnalyticResponse : response) {
				patientAnalyticResponse.setCount(patientAnalyticResponse.getPatients().size());

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting patient analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting patient analytic");
		}

		return response;

	}

	@Override
	public List<?> getMostPrescribedPrescriptionItems(String type, String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page,
			int size) {
		List<?> response = null;
		try {
			switch (PrescriptionItems.valueOf(type.toUpperCase())) {

			case DRUGS: {
				if (!DPDoctorUtils.anyStringEmpty(queryType) && queryType.equalsIgnoreCase("TOP")) {
					response = getMostPrescribedDrugs(doctorId, locationId, hospitalId, fromDate, toDate, searchType,
							page, size);
					break;
				} else
					response = getMostPrescribedDrugsByDate(doctorId, locationId, hospitalId, fromDate, toDate,
							searchType, page, size);
				break;
			}
			case DIAGNOSTICTEST: {
				if (!DPDoctorUtils.anyStringEmpty(queryType) && queryType.equalsIgnoreCase("TOP")) {
					response = getMostPrescribedLabTests(locationId, hospitalId, fromDate, toDate, searchType, page,
							size);
					break;
				} else
					response = getMostPrescribedLabTestsByDate(locationId, hospitalId, fromDate, toDate, searchType,
							page, size);
				break;
			}
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting most prescribed prescription items");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting most prescribed prescription items");
		}
		return response;
	}

	private List<?> getMostPrescribedLabTestsByDate(String locationId, String hospitalId, String fromDate,
			String toDate, String searchType, int page, int size) {
		List<DiagnosticTestsAnalyticsData> response = null;
		try {
			Criteria criteria = new Criteria("locationId").is(new ObjectId(locationId)).and("hospitalId")
					.is(new ObjectId(hospitalId));
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria = criteria.and("createdTime").gte(new Date(Long.parseLong(fromDate)));
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				criteria = criteria.and("createdTime").lte(new Date(Long.parseLong(toDate)));
			}
			AggregationOperation aggregationOperation = null;
			if (!DPDoctorUtils.anyStringEmpty(searchType))
				switch (SearchType.valueOf(searchType.toUpperCase())) {

				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("day", "$day").append("month", "$month").append("year",
													"$year")).append("day", new BasicDBObject("$first", "$day"))
															.append("tests", new BasicDBObject("$push", "$tests"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}

				case WEEKLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("day", new BasicDBObject("$first", "$day"))
													.append("tests", new BasicDBObject("$push", "$tests"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("month", "$month").append("year", "$year"))
													.append("day", new BasicDBObject("$first", "$day"))
													.append("tests", new BasicDBObject("$push", "$tests"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("date", new BasicDBObject("$first", "$date"))));
					break;
				}
				case YEARLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("tests", new BasicDBObject("$push", "$tests"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))));

					break;

				}
				default:
					break;
				}
			else {
				aggregationOperation = new CustomAggregationOperation(
						new BasicDBObject("$group",
								new BasicDBObject("_id",
										new BasicDBObject("day", "$day").append("month", "$month").append("year",
												"$year")).append("day", new BasicDBObject("$first", "$day"))
														.append("tests", new BasicDBObject("$push", "$tests"))
														.append("month", new BasicDBObject("$first", "$month"))
														.append("year", new BasicDBObject("$first", "$year"))
														.append("date", new BasicDBObject("$first", "$date"))));
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.unwind("$diagnosticTests"),
						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("_id", "$diagnosticTests.testId")
										.append("count", new BasicDBObject("$sum", 1)).append("createdTime",
												new BasicDBObject("$first", "$createdTime")))),
						Aggregation.lookup("diagnostic_test_cl", "_id", "_id", "test"), Aggregation.unwind("test"),
						new CustomAggregationOperation(new BasicDBObject("$project",
								new BasicDBObject("tests.locationId", "$test.locationId")
										.append("tests.hospitalId", "$test.hospitalId")
										.append("tests.testName", "$test.testName")
										.append("tests.explanation", "$test.explanation")
										.append("tests.discarded", "$test.discarded")
										.append("tests.specimen", "$test.specimen").append("tests.code", "$test.code")
										.append("tests.createdTime", "$test.createdTime")
										.append("tests.updatedTime", "$test.updatedTime")
										.append("tests.createdBy", "$test.createdBy").append("tests..count", "$count")
										.append("date", "$createdTime")
										.append("day", new BasicDBObject("$dayOfMonth", "$createdTime"))
										.append("month", new BasicDBObject("$month", "$createdTime"))
										.append("year", new BasicDBObject("$year", "$createdTime"))
										.append("week", new BasicDBObject("$week", "$createdTime")))),
						aggregationOperation, Aggregation.sort(Direction.DESC, "createdTime"),
						Aggregation.skip(page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.unwind("$diagnosticTests"),
						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("_id", "$diagnosticTests.testId")
										.append("count", new BasicDBObject("$sum", 1)).append("createdTime",
												new BasicDBObject("$first", "$createdTime")))),
						Aggregation.lookup("diagnostic_test_cl", "_id", "_id", "test"), Aggregation.unwind("test"),
						new CustomAggregationOperation(new BasicDBObject("$project",
								new BasicDBObject("tests.locationId", "$test.locationId")
										.append("tests.hospitalId", "$test.hospitalId")
										.append("tests.testName", "$test.testName")
										.append("tests.explanation", "$test.explanation")
										.append("tests.discarded", "$test.discarded")
										.append("tests.specimen", "$test.specimen").append("tests.code", "$test.code")
										.append("tests.createdTime", "$test.createdTime")
										.append("tests.updatedTime", "$test.updatedTime")
										.append("tests.createdBy", "$test.createdBy").append("tests.count", "$count")
										.append("date", "$createdTime")
										.append("day", new BasicDBObject("$dayOfMonth", "$createdTime"))
										.append("month", new BasicDBObject("$month", "$createdTime"))
										.append("year", new BasicDBObject("$year", "$createdTime"))
										.append("week", new BasicDBObject("$week", "$createdTime")))),
						aggregationOperation, Aggregation.sort(Direction.DESC, "createdTime"));
			}
			response = mongoTemplate
					.aggregate(aggregation, PrescriptionCollection.class, DiagnosticTestsAnalyticsData.class)
					.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting most prescribed lab tests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting most prescribed lab tests");
		}
		return response;
	}

	private List<?> getMostPrescribedDrugsByDate(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String searchType, int page, int size) {
		List<DrugsAnalyticsData> response = null;
		try {
			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId")
					.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));

			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria = criteria.and("createdTime").gte(new Date(Long.parseLong(fromDate)));
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				criteria = criteria.and("createdTime").lte(new Date(Long.parseLong(toDate)));
			}

			AggregationOperation aggregationOperation = null;
			if (!DPDoctorUtils.anyStringEmpty(searchType))
				switch (SearchType.valueOf(searchType.toUpperCase())) {

				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("day", "$day").append("month", "$month").append("year",
													"$year")).append("day", new BasicDBObject("$first", "$day"))
															.append("drugs", new BasicDBObject("$push", "$drugs"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}

				case WEEKLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("day", new BasicDBObject("$first", "$day"))
													.append("drugs", new BasicDBObject("$push", "$drugs"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("month", "$month").append("year", "$year"))
													.append("day", new BasicDBObject("$first", "$day"))
													.append("drugs", new BasicDBObject("$push", "$drugs"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("date", new BasicDBObject("$first", "$date"))));
					break;
				}
				case YEARLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("drugs", new BasicDBObject("$push", "$drugs"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))));

					break;

				}
				default:
					break;
				}
			else {
				aggregationOperation = new CustomAggregationOperation(
						new BasicDBObject("$group",
								new BasicDBObject("_id",
										new BasicDBObject("day", "$day").append("month", "$month").append("year",
												"$year")).append("day", new BasicDBObject("$first", "$day"))
														.append("drugs", new BasicDBObject("$push", "$drugs"))
														.append("month", new BasicDBObject("$first", "$month"))
														.append("year", new BasicDBObject("$first", "$year"))
														.append("date", new BasicDBObject("$first", "$date"))));
			}
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("$items"),
						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("_id", "$items.drugId").append("count", new BasicDBObject("$sum", 1))
										.append("createdTime", new BasicDBObject("$first", "$createdTime")))),
						Aggregation.lookup("drug_cl", "_id", "_id", "drug"), Aggregation.unwind("drug"),
						new CustomAggregationOperation(new BasicDBObject("$project",
								new BasicDBObject("drugs.locationId", "$drug.locationId")
										.append("drugs.hospitalId", "$drug.hospitalId")
										.append("drugs.doctorId", "$drug.doctorId")
										.append("drugs.drugName", "$drug.drugName")
										.append("drugs.drugType", "$drug.drugType")
										.append("drugs.explanation", "$drug.explanation")
										.append("drugs.discarded", "$drug.discarded")
										.append("drugs.duration", "$drug.duration")
										.append("drugs.dosage", "$drug.dosage")
										.append("drugs.dosageTime", "$drug.dosageTime")
										.append("drugs.direction", "$drug.direction")
										.append("drugs.categories", "$drug.categories")
										.append("drugs.rankingCount", "$drug.rankingCount")
										.append("drugs.genericNames", "$drug.genericNames")
										.append("drugs.drugCode", "$drug.drugCode")
										.append("drugs.createdTime", "$drug.createdTime")
										.append("drugs.updatedTime", "$drug.updatedTime")
										.append("drugs.createdBy", "$drug.createdBy").append("drugs.count", "$count")
										.append("date", "$createdTime")
										.append("day", new BasicDBObject("$dayOfMonth", "$createdTime"))
										.append("month", new BasicDBObject("$month", "$createdTime"))
										.append("year", new BasicDBObject("$year", "$createdTime"))
										.append("week", new BasicDBObject("$week", "$createdTime")))),
						aggregationOperation, Aggregation.sort(Direction.DESC, "createdTime"),
						Aggregation.skip(page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("$items"),
						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("_id", "$items.drugId").append("count", new BasicDBObject("$sum", 1))
										.append("createdTime", new BasicDBObject("$first", "$createdTime")))),
						Aggregation.lookup("drug_cl", "_id", "_id", "drug"), Aggregation.unwind("drug"),
						new CustomAggregationOperation(new BasicDBObject("$project",
								new BasicDBObject("drugs.locationId", "$drug.locationId")
										.append("drugs.hospitalId", "$drug.hospitalId")
										.append("drugs.doctorId", "$drug.doctorId")
										.append("drugs.drugName", "$drug.drugName")
										.append("drugs.drugType", "$drug.drugType")
										.append("drugs.explanation", "$drug.explanation")
										.append("drugs.discarded", "$drug.discarded")
										.append("drugs.duration", "$drug.duration")
										.append("drugs.dosage", "$drug.dosage")
										.append("drugs.dosageTime", "$drug.dosageTime")
										.append("drugs.direction", "$drug.direction")
										.append("drugs.categories", "$drug.categories")
										.append("drugs.rankingCount", "$drug.rankingCount")
										.append("drugs.genericNames", "$drug.genericNames")
										.append("drugs.drugCode", "$drug.drugCode")
										.append("drugs.createdTime", "$drug.createdTime")
										.append("drugs.updatedTime", "$drug.updatedTime")
										.append("drugs.createdBy", "$drug.createdBy").append("drugs.count", "$count")
										.append("date", "$createdTime")
										.append("day", new BasicDBObject("$dayOfMonth", "$createdTime"))
										.append("month", new BasicDBObject("$month", "$createdTime"))
										.append("year", new BasicDBObject("$year", "$createdTime"))
										.append("week", new BasicDBObject("$week", "$createdTime")))),
						aggregationOperation, Aggregation.sort(Direction.DESC, "createdTime"));
			}

			response = mongoTemplate.aggregate(aggregation, PrescriptionCollection.class, DrugsAnalyticsData.class)
					.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting most prescribed drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting most prescribed drugs");
		}
		return response;
	}

	private List<?> getMostPrescribedLabTests(String locationId, String hospitalId, String fromDate, String toDate,
			String searchType, int page, int size) {
		List<DiagnosticTest> response = null;
		try {
			Criteria criteria = new Criteria("locationId").is(new ObjectId(locationId)).and("hospitalId")
					.is(new ObjectId(hospitalId));
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria = criteria.and("createdTime").gte(new Date(Long.parseLong(fromDate)));
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				criteria = criteria.and("createdTime").lte(new Date(Long.parseLong(toDate)));
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.unwind("$diagnosticTests"),
						Aggregation.group("$diagnosticTests.testId").count().as("count"),
						Aggregation.lookup("diagnostic_test_cl", "_id", "_id", "test"), Aggregation.unwind("test"),

						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("id", "$_id")
										.append("locationId", new BasicDBObject("$first", "$test.locationId"))
										.append("hospitalId", new BasicDBObject("$first", "$test.hospitalId"))
										.append("testName", new BasicDBObject("$first", "$test.testName"))
										.append("explanation", new BasicDBObject("$first", "$test.explanation"))
										.append("discarded", new BasicDBObject("$first", "$test.discarded"))
										.append("specimen", new BasicDBObject("$first", "$test.specimen"))
										.append("code", new BasicDBObject("$first", "$test.code"))
										.append("createdTime", new BasicDBObject("$first", "$test.createdTime"))
										.append("updatedTime", new BasicDBObject("$first", "$test.updatedTime"))
										.append("createdBy", new BasicDBObject("$first", "$test.createdBy"))
										.append("count", new BasicDBObject("$first", "$count")))),

						Aggregation.sort(Direction.DESC, "count"), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.unwind("$diagnosticTests"),
						Aggregation.group("$diagnosticTests.testId").count().as("count"),
						Aggregation.lookup("diagnostic_test_cl", "_id", "_id", "test"), Aggregation.unwind("test"),

						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("id", "$_id")
										.append("locationId", new BasicDBObject("$first", "$test.locationId"))
										.append("hospitalId", new BasicDBObject("$first", "$test.hospitalId"))
										.append("testName", new BasicDBObject("$first", "$test.testName"))
										.append("explanation", new BasicDBObject("$first", "$test.explanation"))
										.append("discarded", new BasicDBObject("$first", "$test.discarded"))
										.append("specimen", new BasicDBObject("$first", "$test.specimen"))
										.append("code", new BasicDBObject("$first", "$test.code"))
										.append("createdTime", new BasicDBObject("$first", "$test.createdTime"))
										.append("updatedTime", new BasicDBObject("$first", "$test.updatedTime"))
										.append("createdBy", new BasicDBObject("$first", "$test.createdBy"))
										.append("count", new BasicDBObject("$first", "$count")))),

						Aggregation.sort(Direction.DESC, "count"));
			}
			response = mongoTemplate.aggregate(aggregation, PrescriptionCollection.class, DiagnosticTest.class)
					.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting most prescribed lab tests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting most prescribed lab tests");
		}
		return response;

	}

	private List<Drug> getMostPrescribedDrugs(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String searchType, int page, int size) {
		List<Drug> response = null;
		try {
			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId")
					.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));

			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria = criteria.and("createdTime").gte(new Date(Long.parseLong(fromDate)));
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				criteria = criteria.and("createdTime").lte(new Date(Long.parseLong(toDate)));
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("$items"),
						Aggregation.group("$items.drugId").count().as("count"),
						Aggregation.lookup("drug_cl", "_id", "_id", "drug"), Aggregation.unwind("drug"),

						new CustomAggregationOperation(
								new BasicDBObject("$group",
										new BasicDBObject("id", "$_id")
												.append("locationId", new BasicDBObject("$first", "$drug.locationId"))
												.append("hospitalId", new BasicDBObject("$first", "$drug.hospitalId"))
												.append("doctorId", new BasicDBObject("$first", "$drug.doctorId"))
												.append("drugName", new BasicDBObject("$first", "$drug.drugName"))
												.append("drugType", new BasicDBObject("$first", "$drug.drugType"))
												.append("explanation", new BasicDBObject("$first", "$drug.explanation"))
												.append("discarded", new BasicDBObject("$first", "$drug.discarded"))
												.append("duration", new BasicDBObject("$first", "$drug.duration"))
												.append("dosage", new BasicDBObject("$first", "$drug.dosage"))
												.append("dosageTime", new BasicDBObject("$first", "$drug.dosageTime"))
												.append("direction", new BasicDBObject("$first", "$drug.direction"))
												.append("categories", new BasicDBObject("$first", "$drug.categories"))
												.append("rankingCount", new BasicDBObject("$max", "$drug.rankingCount"))
												.append("genericNames",
														new BasicDBObject("$first", "$drug.genericNames"))
												.append("drugCode", new BasicDBObject("$first", "$drug.drugCode"))
												.append("createdTime", new BasicDBObject("$first", "$drug.createdTime"))
												.append("updatedTime", new BasicDBObject("$first", "$drug.updatedTime"))
												.append("createdBy", new BasicDBObject("$first", "$drug.createdBy"))
												.append("count", new BasicDBObject("$first", "$count")))),

						Aggregation.sort(Direction.DESC, "count"), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("$items"),
						Aggregation.group("$items.drugId").count().as("count"),
						Aggregation.lookup("drug_cl", "_id", "_id", "drug"), Aggregation.unwind("drug"),

						new CustomAggregationOperation(
								new BasicDBObject("$group",
										new BasicDBObject("id", "$_id")
												.append("locationId", new BasicDBObject("$first", "$drug.locationId"))
												.append("hospitalId", new BasicDBObject("$first", "$drug.hospitalId"))
												.append("doctorId", new BasicDBObject("$first", "$drug.doctorId"))
												.append("drugName", new BasicDBObject("$first", "$drug.drugName"))
												.append("drugType", new BasicDBObject("$first", "$drug.drugType"))
												.append("explanation", new BasicDBObject("$first", "$drug.explanation"))
												.append("discarded", new BasicDBObject("$first", "$drug.discarded"))
												.append("duration", new BasicDBObject("$first", "$drug.duration"))
												.append("dosage", new BasicDBObject("$first", "$drug.dosage"))
												.append("dosageTime", new BasicDBObject("$first", "$drug.dosageTime"))
												.append("direction", new BasicDBObject("$first", "$drug.direction"))
												.append("categories", new BasicDBObject("$first", "$drug.categories"))
												.append("rankingCount", new BasicDBObject("$max", "$drug.rankingCount"))
												.append("genericNames",
														new BasicDBObject("$first", "$drug.genericNames"))
												.append("drugCode", new BasicDBObject("$first", "$drug.drugCode"))
												.append("createdTime", new BasicDBObject("$first", "$drug.createdTime"))
												.append("updatedTime", new BasicDBObject("$first", "$drug.updatedTime"))
												.append("createdBy", new BasicDBObject("$first", "$drug.createdBy"))
												.append("count", new BasicDBObject("$first", "$count")))),

						Aggregation.sort(Direction.DESC, "count"));
			}

			response = mongoTemplate.aggregate(aggregation, PrescriptionCollection.class, Drug.class)
					.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting most prescribed drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting most prescribed drugs");
		}
		return response;
	}

	@Override
	public AppointmentAnalyticResponse getAppointmentAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, String searchTerm,
			int page, int size) {
		AppointmentAnalyticResponse response = null;
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

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("fromDate").gte(fromTime);
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("toDate").lte(toTime);
			}

			long count = mongoTemplate.count(new Query(criteria), AppointmentCollection.class);
			if (count > 0) {
				response = new AppointmentAnalyticResponse();
				Aggregation aggregation = null;
				if (size > 0) {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("patient_queue_cl", "appointmentId", "appointmentId", "patientQueue"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$patientQueue").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.lookup("user_cl", "doctorId", "_id",
									"doctor"),
							new CustomAggregationOperation(
									new BasicDBObject("$unwind",
											new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays",
													true))),
							new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("id", "$_id")
									.append("date", new BasicDBObject("$first", "$fromDate"))
									.append("fromTime", new BasicDBObject("$first", "$time.fromTime"))
									.append("waitedFor", new BasicDBObject("$max", "$patientQueue.waitedFor"))
									.append("engagedAt", new BasicDBObject("$max", "$patientQueue.engagedAt"))
									.append("checkedInAt", new BasicDBObject("$max", "$patientQueue.checkedInAt"))
									.append("checkedOutAt", new BasicDBObject("$max", "$patientQueue.checkedOutAt"))
									.append("patientName", new BasicDBObject("$first", "$patient.localPatientName"))
									.append("patientId", new BasicDBObject("$first", "$patientId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorName", new BasicDBObject("$first", "$doctor.firstName")))),
							Aggregation.skip(page * size), Aggregation.limit(size),
							Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.fromTime")));
				} else {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("patient_queue_cl", "appointmentId", "appointmentId", "patientQueue"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$patientQueue").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.lookup("user_cl", "doctorId", "_id",
									"doctor"),
							new CustomAggregationOperation(
									new BasicDBObject("$unwind",
											new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays",
													true))),
							new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("id", "$_id")
									.append("date", new BasicDBObject("$first", "$fromDate"))
									.append("fromTime", new BasicDBObject("$first", "$time.fromTime"))
									.append("waitedFor", new BasicDBObject("$max", "$patientQueue.waitedFor"))
									.append("engagedAt", new BasicDBObject("$max", "$patientQueue.engagedAt"))
									.append("checkedInAt", new BasicDBObject("$max", "$patientQueue.checkedInAt"))
									.append("checkedOutAt", new BasicDBObject("$max", "$patientQueue.checkedOutAt"))
									.append("patientName", new BasicDBObject("$first", "$patient.localPatientName"))
									.append("patientId", new BasicDBObject("$first", "$patientId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorName", new BasicDBObject("$first", "$doctor.firstName")))),
							Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.fromTime")));
				}
				List<AppointmentDeatilAnalyticResponse> analyticResponses = mongoTemplate
						.aggregate(aggregation, AppointmentCollection.class, AppointmentDeatilAnalyticResponse.class)
						.getMappedResults();
				response.setTotalAppointments(count);

				for (AppointmentDeatilAnalyticResponse appointmentDeatilAnalyticResponse : analyticResponses) {
					List<PatientCard> patientCards = mongoTemplate
							.aggregate(
									Aggregation
											.newAggregation(
													Aggregation.match(
															new Criteria("userId")
																	.is(new ObjectId(appointmentDeatilAnalyticResponse
																			.getPatientId()))
																	.and("locationId")
																	.is(new ObjectId(appointmentDeatilAnalyticResponse
																			.getLocationId()))
																	.and("hospitalId")
																	.is(new ObjectId(appointmentDeatilAnalyticResponse
																			.getHospitalId())))),
									PatientCollection.class, PatientCard.class)
							.getMappedResults();
					if (patientCards != null && !patientCards.isEmpty()) {
						appointmentDeatilAnalyticResponse.setPatientName(patientCards.get(0).getLocalPatientName());
					}
				}
				response.setAppointments(analyticResponses);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment analytics data");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting appointment analytics data");
		}
		return response;
	}

	@Override
	public List<AppointmentAverageTimeAnalyticResponse> getAppointmentAverageTimeAnalyticsData(String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String queryType, String searchType,
			String searchTerm, int page, int size) {
		List<AppointmentAverageTimeAnalyticResponse> response = null;
		try {

			Criteria criteria = new Criteria("discarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("date").gt(start);
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("date").lte(end);
			}

			AggregationOperation aggregationOperation = null;
			if (!DPDoctorUtils.anyStringEmpty(searchType))
				switch (SearchType.valueOf(searchType.toUpperCase())) {

				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
											.append("averageWaitingTime", new BasicDBObject("$avg", "$waitedFor"))
											.append("averageEngagedTime", new BasicDBObject("$avg", "$engagedFor"))
											.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}

				case WEEKLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year"))
													.append("averageWaitingTime",
															new BasicDBObject("$avg", "$waitedFor"))
													.append("averageEngagedTime",
															new BasicDBObject("$avg", "$engagedFor"))
													.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("averageWaitingTime", new BasicDBObject("$avg", "$waitedFor"))
									.append("averageEngagedTime", new BasicDBObject("$avg", "$engagedFor"))
									.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("averageWaitingTime", new BasicDBObject("$avg", "$waitedFor"))
									.append("averageEngagedTime", new BasicDBObject("$avg", "$engagedFor"))
									.append("date", new BasicDBObject("$first", "$date"))));

					break;

				}
				default:
					break;
				}
			else {
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
						new BasicDBObject("_id",
								new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
										.append("averageWaitingTime", new BasicDBObject("$avg", "$waitedFor"))
										.append("averageEngagedTime", new BasicDBObject("$avg", "$engagedFor"))
										.append("date", new BasicDBObject("$first", "$date"))));
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new ProjectionOperation(Fields.from(Fields.field("date", "$date"),
								Fields.field("waitedFor", "$waitedFor"), Fields.field("engagedFor", "$engagedFor")))
										.and("date").extractDayOfMonth().as("day").and("date").extractMonth()
										.as("month").and("date").extractYear().as("year").and("date").extractWeek()
										.as("week"),
						aggregationOperation, Aggregation.sort(Direction.DESC, "date"), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new ProjectionOperation(Fields.from(Fields.field("date", "$date"),
								Fields.field("waitedFor", "$waitedFor"), Fields.field("engagedFor", "$engagedFor")))
										.and("date").extractDayOfMonth().as("day").and("date").extractMonth()
										.as("month").and("date").extractYear().as("year").and("date").extractWeek()
										.as("week"),
						aggregationOperation, Aggregation.sort(Direction.DESC, "date"));
			}
			AggregationResults<AppointmentAverageTimeAnalyticResponse> aggregationResults = mongoTemplate
					.aggregate(aggregation, PatientQueueCollection.class, AppointmentAverageTimeAnalyticResponse.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment average time analytics data");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting appointment average time analytics data");
		}
		return response;
	}

	@Override
	public List<AppointmentCountAnalyticResponse> getAppointmentCountAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, String searchTerm,
			int page, int size) {
		List<AppointmentCountAnalyticResponse> response = null;
		try {

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(queryType)) {
				if (queryType.equalsIgnoreCase("CANCEL"))
					criteria.and("state").is(AppointmentState.CANCEL.getState());
				else if (queryType.equalsIgnoreCase("PATIENTGROUP")) {
					response = getAppointmentCountAnalyticnDataByPatientGroup(doctorId, locationId, hospitalId,
							fromDate, toDate, page, size);
					return response;
				}
			}
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("fromDate").gt(start);
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("fromDate").lte(end);
			}

			AggregationOperation aggregationOperation = null;
			if (!DPDoctorUtils.anyStringEmpty(searchType))
				switch (SearchType.valueOf(searchType.toUpperCase())) {

				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("day", "$day").append("month", "$month").append("year",
													"$year")).append("count", new BasicDBObject("$sum", 1))
															.append("date", new BasicDBObject("$first", "$fromDate"))));

					break;
				}

				case WEEKLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("count", new BasicDBObject("$sum", 1)).append("date",
													new BasicDBObject("$first", "$fromDate"))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("count", new BasicDBObject("$sum", 1)).append("date",
											new BasicDBObject("$first", "$fromDate"))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("count", new BasicDBObject("$sum", 1)).append("date",
											new BasicDBObject("$first", "$fromDate"))));

					break;

				}
				default:
					break;
				}
			else {
				aggregationOperation = new CustomAggregationOperation(
						new BasicDBObject("$group",
								new BasicDBObject("_id",
										new BasicDBObject("day", "$day").append("month", "$month").append("year",
												"$year")).append("count", new BasicDBObject("$sum", 1)).append("date",
														new BasicDBObject("$first", "$fromDate"))));
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new ProjectionOperation(Fields.from(Fields.field("fromDate", "$fromDate"))).and("fromDate")
								.extractDayOfMonth().as("day").and("fromDate").extractMonth().as("month")
								.and("fromDate").extractYear().as("year").and("fromDate").extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(Direction.DESC, "fromDate"),
						Aggregation.skip(page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new ProjectionOperation(Fields.from(Fields.field("fromDate", "$fromDate"))).and("fromDate")
								.extractDayOfMonth().as("day").and("fromDate").extractMonth().as("month")
								.and("fromDate").extractYear().as("year").and("fromDate").extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(Direction.DESC, "fromDate"));
			}
			AggregationResults<AppointmentCountAnalyticResponse> aggregationResults = mongoTemplate
					.aggregate(aggregation, AppointmentCollection.class, AppointmentCountAnalyticResponse.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment count analytics data");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting appointment count analytics data");
		}
		return response;
	}

	private List<AppointmentCountAnalyticResponse> getAppointmentCountAnalyticnDataByPatientGroup(String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, int page, int size) {
		List<AppointmentCountAnalyticResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("group.doctorId").in(new ObjectId(doctorId));
				criteria2.and("appointment.doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("group.locationId").is(new ObjectId(locationId));
				criteria2.and("appointment.locationId").is(new ObjectId(locationId));
			}

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria2.and("appointment.fromDate").gt(start);
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria2.and("appointment.fromDate").lte(end);
			}

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("group_cl", "groupId", "_id", "group"), Aggregation.unwind("group"),
					Aggregation.match(criteria),
					Aggregation.lookup("appointment_cl", "patientId", "patientId", "appointment"),
					Aggregation.unwind("appointment"), Aggregation.match(criteria2),
					new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("groupName", "$group.name")).append("count",
									new BasicDBObject("$sum", 1)))));

			response = mongoTemplate
					.aggregate(aggregation, PatientGroupCollection.class, AppointmentCountAnalyticResponse.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment count analytics data by patient group");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting appointment count analytics data by patient group");
		}
		return response;
	}

	@Override
	public List<InvoiceAnalyticsDataDetailResponse> getIncomeDetailsAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page,
			int size) {
		List<InvoiceAnalyticsDataDetailResponse> response = null;
		try {
			Criteria criteria = new Criteria();

			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				criteria2.and("patient.locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				criteria2.and("patient.hospitalId").is(new ObjectId(hospitalId));
			}

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("invoiceDate").gt(start);
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("invoiceDate").lte(end);
			}
			AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("uniqueInvoiceId", "$uniqueInvoiceId"))
							.append("uniqueInvoiceId", new BasicDBObject("$first", "$uniqueInvoiceId"))
							.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
							.append("cost", new BasicDBObject("$sum", "$resultantCost"))
							.append("tax", new BasicDBObject("$sum", "$resultantTax"))
							.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
							.append("patientName", new BasicDBObject("$first", "$patientName"))
							.append("date", new BasicDBObject("$first", "$invoiceDate"))));

			if (size > 0) {
				response = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteria),
										Aggregation.lookup("patient_cl", "userId", "patientId", "patient"),
										Aggregation.unwind("patient"), Aggregation.match(criteria2),
										new CustomAggregationOperation(new BasicDBObject("$project", new BasicDBObject(
												"resultantDiscount", new BasicDBObject("$cond", new BasicDBObject("if",
														new BasicDBObject("$eq", Arrays.asList("$totalDiscount.unit",
																UnitType.PERCENT.name())))
																		.append("then", new BasicDBObject("$multiply",
																				Arrays.asList(
																						new BasicDBObject("$divide",
																								Arrays.asList(
																										"$totalDiscount.value",
																										100)),
																						"$totalCost")))
																		.append("else", "$totalDiscount.value")))
																				.append("resultantCost", "$totalCost")
																				.append("invoiceDate", "$invoiceDate")
																				.append("resultantTax",
																						new BasicDBObject("$cond",
																								new BasicDBObject("if",
																										new BasicDBObject(
																												"$eq",
																												Arrays.asList(
																														"$totalTax.unit", UnitType.PERCENT
																																.name())))
																																		.append("then",
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
																				.append("patientName",
																						"$patient.localPatientName")
																				.append("uniqueInvoiceId",
																						"$uniqueInvoiceId")
																				.append("resultantInvoiceAmount",
																						"$grandTotal"))),
										aggregationOperation,
										Aggregation.sort(new Sort(Sort.Direction.DESC, "invoiceDate")),
										Aggregation.skip((page) * size), Aggregation.limit(size)),
								DoctorPatientInvoiceCollection.class, InvoiceAnalyticsDataDetailResponse.class)
						.getMappedResults();
			} else {
				response = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteria),
										Aggregation.lookup("patient_cl", "userId", "patientId", "patient"),
										Aggregation.unwind("patient"), Aggregation.match(criteria2),
										new CustomAggregationOperation(new BasicDBObject("$project", new BasicDBObject(
												"resultantDiscount", new BasicDBObject("$cond", new BasicDBObject("if",
														new BasicDBObject("$eq", Arrays.asList("$totalDiscount.unit",
																UnitType.PERCENT.name())))
																		.append("then", new BasicDBObject("$multiply",
																				Arrays.asList(
																						new BasicDBObject("$divide",
																								Arrays.asList(
																										"$totalDiscount.value",
																										100)),
																						"$totalCost")))
																		.append("else", "$totalDiscount.value")))
																				.append("resultantCost", "$totalCost")
																				.append("invoiceDate", "$invoiceDate")
																				.append("resultantTax",
																						new BasicDBObject("$cond",
																								new BasicDBObject("if",
																										new BasicDBObject(
																												"$eq",
																												Arrays.asList(
																														"$totalTax.unit", UnitType.PERCENT
																																.name())))
																																		.append("then",
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
																				.append("patientName",
																						"$patient.localPatientName")
																				.append("uniqueInvoiceId",
																						"$uniqueInvoiceId")
																				.append("resultantInvoiceAmount",
																						"$grandTotal"))),
										aggregationOperation,
										Aggregation.sort(new Sort(Sort.Direction.DESC, "invoiceDate"))),
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

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			DateTime start = null, end = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("invoiceDate").gt(start);
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("invoiceDate").lte(end);
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
					response = getInvoiceIncomeDataByPatientGroup(searchType, page, size, start, end, doctorId,
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
			AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
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
				criteria.and("group.doctorId").in(new ObjectId(doctorId));
				criteria2.and("invoice.doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("group.locationId").is(new ObjectId(locationId));
				criteria2.and("invoice.locationId").is(new ObjectId(locationId));
			}

			if (start != null) {
				criteria2.and("invoice.invoiceDate").gt(start);
			}
			if (end != null) {
				criteria2.and("invoice.invoiceDate").lte(end);
			}

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("group_cl", "groupId", "_id", "group"), Aggregation.unwind("group"),
					Aggregation.match(criteria),
					Aggregation.lookup("doctor_patient_invoice_cl", "patientId", "patientId", "invoice"),
					Aggregation.unwind("invoice"), Aggregation.match(criteria2),
					Aggregation.unwind("invoice.invoiceItems"),
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("resultantDiscount", new BasicDBObject("$cond",
									new BasicDBObject("if",
											new BasicDBObject("$eq",
													Arrays.asList("$invoice.totalDiscount.unit",
															UnitType.PERCENT.name())))
																	.append("then",
																			new BasicDBObject("$multiply",
																					Arrays.asList(
																							new BasicDBObject("$divide",
																									Arrays.asList(
																											"$invoice.totalDiscount.value",
																											100)),
																							"$invoice.totalCost")))
																	.append("else", "$invoice.totalDiscount.value")))
																			.append("resultantCost",
																					"$invoice.totalCost")
																			.append("invoiceDate",
																					"$invoice.invoiceDate")
																			.append("resultantTax", new BasicDBObject(
																					"$cond",
																					new BasicDBObject("if",
																							new BasicDBObject("$eq",
																									Arrays.asList(
																											"$invoice.totalTax.unit",
																											UnitType.PERCENT
																													.name())))
																															.append("then",
																																	new BasicDBObject(
																																			"$multiply",
																																			Arrays.asList(
																																					new BasicDBObject(
																																							"$divide",
																																							Arrays.asList(
																																									"$invoice.totalTax.value",
																																									100)),
																																					"$invoice.totalCost")))
																															.append("else",
																																	"$invoice.totalTax.value")))
																			.append("resultantInvoiceAmount",
																					"$invoice.grandTotal")
																			.append("groupId", "$group._id")
																			.append("groupName", "$group.name"))),
					aggregationOperation);
			// , Aggregation.skip(page * size), Aggregation.limit(size)
			// new CustomAggregationOperation(new BasicDBObject("$group", new
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

	private List<IncomeAnalyticsDataResponse> getInvoiceIncomeDataByServices(String searchType, int page, int size,
			Criteria criteria) {
		List<IncomeAnalyticsDataResponse> response = null;

		AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
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
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("resultantDiscount", new BasicDBObject("$cond",
									new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then",
															new BasicDBObject(
																	"$multiply", Arrays.asList(
																			new BasicDBObject("$divide",
																					Arrays.asList(
																							"$totalDiscount.value",
																							100)),
																			"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax",
																	new BasicDBObject("$cond", new BasicDBObject("if",
																			new BasicDBObject("$eq",
																					Arrays.asList("$totalTax.unit",
																							UnitType.PERCENT.name())))
																									.append("then",
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
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("resultantDiscount", new BasicDBObject("$cond",
									new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then",
															new BasicDBObject(
																	"$multiply", Arrays.asList(
																			new BasicDBObject("$divide",
																					Arrays.asList(
																							"$totalDiscount.value",
																							100)),
																			"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax",
																	new BasicDBObject("$cond", new BasicDBObject("if",
																			new BasicDBObject("$eq",
																					Arrays.asList("$totalTax.unit",
																							UnitType.PERCENT.name())))
																									.append("then",
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

	private List<IncomeAnalyticsDataResponse> getInvoiceIncomeDataByDoctors(String searchType, int page, int size,
			Criteria criteria) {
		List<IncomeAnalyticsDataResponse> response = null;

		AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
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
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("resultantDiscount", new BasicDBObject("$cond",
									new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then",
															new BasicDBObject("$multiply",
																	Arrays.asList(new BasicDBObject("$divide",
																			Arrays.asList("$totalDiscount.value", 100)),
																			"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax",
																	new BasicDBObject("$cond", new BasicDBObject("if",
																			new BasicDBObject("$eq",
																					Arrays.asList("$totalTax.unit",
																							UnitType.PERCENT.name())))
																									.append("then",
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
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("resultantDiscount", new BasicDBObject("$cond",
									new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then",
															new BasicDBObject("$multiply",
																	Arrays.asList(new BasicDBObject("$divide",
																			Arrays.asList("$totalDiscount.value", 100)),
																			"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax",
																	new BasicDBObject("$cond", new BasicDBObject("if",
																			new BasicDBObject("$eq",
																					Arrays.asList("$totalTax.unit",
																							UnitType.PERCENT.name())))
																									.append("then",
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

	private List<IncomeAnalyticsDataResponse> getInvoiceIncomeDataByDate(String searchType, int page, int size,
			Criteria criteria) {
		List<IncomeAnalyticsDataResponse> response = null;
		AggregationOperation aggregationOperation = null;
		if (!DPDoctorUtils.anyStringEmpty(searchType))
			switch (SearchType.valueOf(searchType.toUpperCase())) {

			case DAILY: {
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
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
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
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
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
						new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
								.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
								.append("cost", new BasicDBObject("$sum", "$resultantCost"))
								.append("tax", new BasicDBObject("$sum", "$resultantTax"))
								.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
								.append("date", new BasicDBObject("$first", "$invoiceDate"))));
				break;
			}
			case YEARLY: {
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
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
			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
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
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), new CustomAggregationOperation(
					new BasicDBObject("$project", new BasicDBObject("resultantDiscount", new BasicDBObject("$cond",
							new BasicDBObject("if",
									new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then",
															new BasicDBObject("$multiply",
																	Arrays.asList(new BasicDBObject("$divide",
																			Arrays.asList("$totalDiscount.value", 100)),
																			"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax",
																	new BasicDBObject("$cond", new BasicDBObject("if",
																			new BasicDBObject("$eq",
																					Arrays.asList("$totalTax.unit",
																							UnitType.PERCENT.name())))
																									.append("then",
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
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), new CustomAggregationOperation(
					new BasicDBObject("$project", new BasicDBObject("resultantDiscount", new BasicDBObject("$cond",
							new BasicDBObject("if",
									new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then",
															new BasicDBObject("$multiply",
																	Arrays.asList(new BasicDBObject("$divide",
																			Arrays.asList("$totalDiscount.value", 100)),
																			"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax",
																	new BasicDBObject("$cond", new BasicDBObject("if",
																			new BasicDBObject("$eq",
																					Arrays.asList("$totalTax.unit",
																							UnitType.PERCENT.name())))
																									.append("then",
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
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page,
			int size) {
		List<PaymentDetailsAnalyticsDataResponse> response = null;
		try {
			Criteria criteria = new Criteria();

			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				criteria2.and("patient.locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				criteria2.and("patient.hospitalId").is(new ObjectId(hospitalId));
			}

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("receivedDate").gt(start);
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("receivedDate").lte(end);
			}

			if (size > 0) {
				response = mongoTemplate
						.aggregate(
								Aggregation
										.newAggregation(Aggregation.match(criteria),
												Aggregation.lookup("patient_cl", "userId", "patientId", "patient"),
												Aggregation.unwind("patient"),
												Aggregation
														.match(criteria2),
												new CustomAggregationOperation(
														new BasicDBObject(
																"$group", new BasicDBObject("_id", "$uniqueReceiptId")
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
																						"$receiptType")))),
												Aggregation.sort(new Sort(Sort.Direction.DESC, "receivedDate")),
												Aggregation.skip((page) * size), Aggregation.limit(size)),
								DoctorPatientReceiptCollection.class, PaymentDetailsAnalyticsDataResponse.class)
						.getMappedResults();
			} else {
				response = mongoTemplate
						.aggregate(
								Aggregation
										.newAggregation(Aggregation.match(criteria),
												Aggregation.lookup("patient_cl", "userId", "patientId", "patient"),
												Aggregation.unwind("patient"),
												Aggregation
														.match(criteria2),
												new CustomAggregationOperation(
														new BasicDBObject(
																"$group", new BasicDBObject("_id", "$uniqueReceiptId")
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
																						"$receiptType")))),
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
	public List<PaymentAnalyticsDataResponse> getPaymentAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page,
			int size) {
		List<PaymentAnalyticsDataResponse> response = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("receivedDate").gt(start);
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("receivedDate").lte(end);
			}

			switch (queryType) {
			case "DOCTORS": {
				response = getPaymentDataByDoctors(searchType, page, size, criteria);
				break;
			}
			case "PAYMENTMODES": {
				response = getPaymentDataByPaymentModes(searchType, page, size, criteria);
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

	private List<PaymentAnalyticsDataResponse> getPaymentDataByDoctors(String searchType, int page, int size,
			Criteria criteria) {
		List<PaymentAnalyticsDataResponse> response = null;
		AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
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
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("cash", new BasicDBObject("$cond",
									new BasicDBObject("if",
											new BasicDBObject("$eq",
													Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name())))
															.append("then", "$amountPaid").append("else", 0))).append(
																	"card",
																	new BasicDBObject("$cond", new BasicDBObject("if",
																			new BasicDBObject("$eq",
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
																									.name())))
																											.append("then",
																													"$amountPaid")
																											.append("else",
																													0)))
																	.append("wallet", new BasicDBObject("$cond",
																			new BasicDBObject("if", new BasicDBObject(
																					"$eq",
																					Arrays.asList("$modeOfPayment",
																							ModeOfPayment.WALLET
																									.name())))
																											.append("then",
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
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("cash", new BasicDBObject("$cond",
									new BasicDBObject("if",
											new BasicDBObject("$eq",
													Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name())))
															.append("then", "$amountPaid").append("else", 0))).append(
																	"card",
																	new BasicDBObject("$cond", new BasicDBObject("if",
																			new BasicDBObject("$eq",
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
																									.name())))
																											.append("then",
																													"$amountPaid")
																											.append("else",
																													0)))
																	.append("wallet", new BasicDBObject("$cond",
																			new BasicDBObject("if", new BasicDBObject(
																					"$eq",
																					Arrays.asList("$modeOfPayment",
																							ModeOfPayment.WALLET
																									.name())))
																											.append("then",
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

	private List<PaymentAnalyticsDataResponse> getPaymentDataByPaymentModes(String searchType, int page, int size,
			Criteria criteria) {
		List<PaymentAnalyticsDataResponse> response = null;
		AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
				new BasicDBObject("_id", new BasicDBObject("modeOfPayment", "$modeOfPayment"))
						.append("modeOfPayment", new BasicDBObject("$first", "$modeOfPayment")).append("total",
								new BasicDBObject("$sum", "$total"))));

		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria),
							new CustomAggregationOperation(new BasicDBObject("$project",
									new BasicDBObject("total", "$amountPaid").append("modeOfPayment",
											"$modeOfPayment"))),
							aggregationOperation, Aggregation.sort(Direction.ASC, "modeOfPayment"),
							Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria),
							new CustomAggregationOperation(new BasicDBObject("$project",
									new BasicDBObject("total", "$amountPaid").append("modeOfPayment",
											"$modeOfPayment"))),
							aggregationOperation, Aggregation.sort(Direction.ASC, "modeOfPayment"));
		}
		response = mongoTemplate
				.aggregate(aggregation, DoctorPatientReceiptCollection.class, PaymentAnalyticsDataResponse.class)
				.getMappedResults();
		return response;

	}

	private List<PaymentAnalyticsDataResponse> getPaymentByDate(String searchType, int page, int size,
			Criteria criteria) {
		List<PaymentAnalyticsDataResponse> response = null;
		AggregationOperation aggregationOperation = null;
		if (!DPDoctorUtils.anyStringEmpty(searchType))
			switch (SearchType.valueOf(searchType.toUpperCase())) {

			case DAILY: {
				aggregationOperation = new CustomAggregationOperation(
						new BasicDBObject("$group",
								new BasicDBObject("_id",
										new BasicDBObject("day", "$day").append("month", "$month").append("year",
												"$year")).append("cash", new BasicDBObject("$sum", "$cash"))
														.append("card", new BasicDBObject("$sum", "$card"))
														.append("online", new BasicDBObject("$sum", "$online"))
														.append("wallet", new BasicDBObject("$sum", "$wallet"))
														.append("total", new BasicDBObject("$sum", "$total"))
														.append("date", new BasicDBObject("$first", "$receivedDate"))));

				break;
			}

			case WEEKLY: {
				aggregationOperation = new CustomAggregationOperation(
						new BasicDBObject("$group",
								new BasicDBObject("_id",
										new BasicDBObject("week", "$week").append("month", "$month").append("year",
												"$year")).append("cash", new BasicDBObject("$sum", "$cash"))
														.append("card", new BasicDBObject("$sum", "$card"))
														.append("online", new BasicDBObject("$sum", "$online"))
														.append("wallet", new BasicDBObject("$sum", "$wallet"))
														.append("total", new BasicDBObject("$sum", "$total"))
														.append("date", new BasicDBObject("$first", "$receivedDate"))));

				break;
			}

			case MONTHLY: {
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
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
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
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
			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
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
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("cash", new BasicDBObject("$cond",
									new BasicDBObject("if",
											new BasicDBObject("$eq",
													Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name())))
															.append("then", "$amountPaid").append("else", 0))).append(
																	"card",
																	new BasicDBObject("$cond", new BasicDBObject("if",
																			new BasicDBObject("$eq",
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
																									.name())))
																											.append("then",
																													"$amountPaid")
																											.append("else",
																													0)))
																	.append("wallet", new BasicDBObject("$cond",
																			new BasicDBObject("if", new BasicDBObject(
																					"$eq",
																					Arrays.asList("$modeOfPayment",
																							ModeOfPayment.WALLET
																									.name())))
																											.append("then",
																													"$amountPaid")
																											.append("else",
																													0)))
																	.append("day", "$receivedDate")
																	.append("day",
																			new BasicDBObject("$dayOfMonth",
																					"$receivedDate"))
																	.append("month",
																			new BasicDBObject("$month",
																					"$receivedDate"))
																	.append("year",
																			new BasicDBObject("$year", "$receivedDate"))
																	.append("week",
																			new BasicDBObject("$week",
																					"$receivedDate")))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "receivedDate"),
					Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("cash", new BasicDBObject("$cond",
									new BasicDBObject("if",
											new BasicDBObject("$eq",
													Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name())))
															.append("then", "$amountPaid").append("else", 0))).append(
																	"card",
																	new BasicDBObject("$cond", new BasicDBObject("if",
																			new BasicDBObject("$eq",
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
																									.name())))
																											.append("then",
																													"$amountPaid")
																											.append("else",
																													0)))
																	.append("wallet", new BasicDBObject("$cond",
																			new BasicDBObject("if", new BasicDBObject(
																					"$eq",
																					Arrays.asList("$modeOfPayment",
																							ModeOfPayment.WALLET
																									.name())))
																											.append("then",
																													"$amountPaid")
																											.append("else",
																													0)))
																	.append("receivedDate", "$receivedDate")
																	.append("day",
																			new BasicDBObject("$dayOfMonth",
																					"$receivedDate"))
																	.append("month",
																			new BasicDBObject("$month",
																					"$receivedDate"))
																	.append("year",
																			new BasicDBObject("$year", "$receivedDate"))
																	.append("week",
																			new BasicDBObject("$week",
																					"$receivedDate")))),
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

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				criteria2.and("patient.locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				criteria2.and("patient.hospitalId").is(new ObjectId(hospitalId));
			}

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("receivedDate").gt(start);
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("receivedDate").lte(end);
			}
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(queryType)) {
				if (queryType.equalsIgnoreCase("PATIENT")) {
					if (size > 0) {
						aggregation = Aggregation
								.newAggregation(Aggregation.match(criteria),
										Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
										Aggregation.unwind("patient"), Aggregation.match(criteria2),
										new CustomAggregationOperation(
												new BasicDBObject("$project",
														new BasicDBObject("debitAmount", "$debitAmount")
																.append("creditAmount", "$creditAmount")
																.append("patientId", "$patientId")
																.append("total", new BasicDBObject("$subtract",
																		Arrays.asList("$debitAmount",
																				"$creditAmount")))
																.append("patientName", "$patient.localPatientName")
																.append("pid", "$patient.PID"))),
										new CustomAggregationOperation(new BasicDBObject("$group",
												new BasicDBObject("_id", "$patientId")
														.append("invoiced", new BasicDBObject("$sum", "$debitAmount"))
														.append("received", new BasicDBObject("$sum", "$creditAmount"))
														.append("amountDue", new BasicDBObject("$sum", "$total"))
														.append("patientName",
																new BasicDBObject("$first", "$patientName"))
														.append("pid", new BasicDBObject("$first", "$pid")))),
										Aggregation.skip(page * size), Aggregation.limit(size));
					} else {
						aggregation = Aggregation
								.newAggregation(Aggregation.match(criteria),
										Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
										Aggregation.unwind("patient"), Aggregation.match(criteria2),
										new CustomAggregationOperation(
												new BasicDBObject("$project",
														new BasicDBObject("debitAmount", "$debitAmount")
																.append("creditAmount", "$creditAmount")
																.append("patientId", "$patientId")
																.append("total", new BasicDBObject("$subtract",
																		Arrays.asList("$debitAmount",
																				"$creditAmount")))
																.append("patientName", "$patient.localPatientName")
																.append("pid", "$patient.PID"))),
										new CustomAggregationOperation(new BasicDBObject("$group",
												new BasicDBObject("_id", "$patientId")
														.append("invoiced", new BasicDBObject("$sum", "$debitAmount"))
														.append("received", new BasicDBObject("$sum", "$creditAmount"))
														.append("amountDue", new BasicDBObject("$sum", "$total"))
														.append("patientName",
																new BasicDBObject("$first", "$patientName"))
														.append("pid", new BasicDBObject("$first", "$pid")))));
					}
				} else if (queryType.equalsIgnoreCase("DOCTORS")) {
					if (size > 0) {
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								Aggregation.unwind("doctor"),
								new CustomAggregationOperation(new BasicDBObject("$project",
										new BasicDBObject("debitAmount", "$debitAmount")
												.append("creditAmount", "$creditAmount").append("doctorId", "$doctorId")
												.append("total",
														new BasicDBObject("$subtract",
																Arrays.asList("$debitAmount", "$creditAmount")))
												.append("doctorName",
														"$doctor.firstName"))),
								new CustomAggregationOperation(new BasicDBObject("$group",
										new BasicDBObject("_id", "$doctorId")
												.append("invoiced", new BasicDBObject("$sum", "$debitAmount"))
												.append("received", new BasicDBObject("$sum", "$creditAmount"))
												.append("amountDue", new BasicDBObject("$sum", "$total"))
												.append("doctorName", new BasicDBObject("$first", "$doctorName")))),
								Aggregation.skip(page * size), Aggregation.limit(size));
					} else {
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								Aggregation.unwind("doctor"),
								new CustomAggregationOperation(new BasicDBObject("$project",
										new BasicDBObject("debitAmount", "$debitAmount")
												.append("creditAmount", "$creditAmount").append("doctorId", "$doctorId")
												.append("total",
														new BasicDBObject("$subtract",
																Arrays.asList("$debitAmount", "$creditAmount")))
												.append("doctorName",
														"$doctor.firstName"))),
								new CustomAggregationOperation(new BasicDBObject("$group",
										new BasicDBObject("_id", "$doctorId")
												.append("invoiced", new BasicDBObject("$sum", "$debitAmount"))
												.append("received", new BasicDBObject("$sum", "$creditAmount"))
												.append("amountDue", new BasicDBObject("$sum", "$total"))
												.append("doctorName", new BasicDBObject("$first", "$doctorName")))));
					}
				}
			} else {
				throw new BusinessException(ServiceError.Unknown, "Query Type cannot be null");
			}

			response = mongoTemplate
					.aggregate(aggregation, DoctorPatientLedgerCollection.class, AmountDueAnalyticsDataResponse.class)
					.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting amount due analytics data");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting amount due analytics data");
		}
		return response;
	}

	@Override
	public List<TreatmentService> getTreatmentsAnalyticsData(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String searchType, int page, int size) {
		List<TreatmentService> response = null;
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
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria = criteria.and("createdTime").gte(new Date(Long.parseLong(fromDate)));
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				criteria = criteria.and("createdTime").lte(new Date(Long.parseLong(toDate)));
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria), Aggregation.unwind("$treatments"),
								Aggregation.group("$treatments.treatmentServiceId").count().as("count"),
								Aggregation.lookup("treatment_services_cl", "_id", "_id", "treatmentServices"),
								Aggregation
										.unwind("$treatmentServices"),
								new CustomAggregationOperation(
										new BasicDBObject("$group",
												new BasicDBObject("id", "$_id")
														.append("locationId",
																new BasicDBObject("$first",
																		"$treatmentServices.locationId"))
														.append("hospitalId",
																new BasicDBObject("$first",
																		"$treatmentServices.hospitalId"))
														.append("doctorId",
																new BasicDBObject("$first",
																		"$treatmentServices.doctorId"))
														.append("name",
																new BasicDBObject("$first", "$treatmentServices.name"))
														.append("cost",
																new BasicDBObject("$first", "$treatmentServices.cost"))
														.append("treatmentCode",
																new BasicDBObject("$first",
																		"$treatmentServices.treatmentCode"))
														.append("discarded",
																new BasicDBObject("$first",
																		"$treatmentServices.discarded"))
														.append("rankingCount",
																new BasicDBObject("$first",
																		"$treatmentServices.rankingCount"))
														.append("category",
																new BasicDBObject("$first",
																		"$treatmentServices.category"))
														.append("fieldsRequired",
																new BasicDBObject("$first",
																		"$treatmentServices.fieldsRequired"))
														.append("createdTime",
																new BasicDBObject("$first",
																		"$treatmentServices.createdTime"))
														.append("updatedTime",
																new BasicDBObject("$first",
																		"$treatmentServices.updatedTime"))
														.append("createdBy",
																new BasicDBObject("$first",
																		"$treatmentServices.createdBy"))
														.append("count", new BasicDBObject("$first", "$count")))),
								Aggregation.sort(Direction.DESC, "count"), Aggregation.skip(page * size),
								Aggregation.limit(size));
			} else {
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria), Aggregation.unwind("$treatments"),
								Aggregation.group("$treatments.treatmentServiceId").count().as("count"),
								Aggregation.lookup("treatment_services_cl", "_id", "_id", "treatmentServices"),
								Aggregation
										.unwind("$treatmentServices"),
								new CustomAggregationOperation(
										new BasicDBObject("$group",
												new BasicDBObject("id", "$_id")
														.append("locationId",
																new BasicDBObject("$first",
																		"$treatmentServices.locationId"))
														.append("hospitalId",
																new BasicDBObject("$first",
																		"$treatmentServices.hospitalId"))
														.append("doctorId",
																new BasicDBObject("$first",
																		"$treatmentServices.doctorId"))
														.append("name",
																new BasicDBObject("$first", "$treatmentServices.name"))
														.append("cost",
																new BasicDBObject("$first", "$treatmentServices.cost"))
														.append("treatmentCode",
																new BasicDBObject("$first",
																		"$treatmentServices.treatmentCode"))
														.append("discarded",
																new BasicDBObject("$first",
																		"$treatmentServices.discarded"))
														.append("rankingCount",
																new BasicDBObject("$first",
																		"$treatmentServices.rankingCount"))
														.append("category",
																new BasicDBObject("$first",
																		"$treatmentServices.category"))
														.append("fieldsRequired",
																new BasicDBObject("$first",
																		"$treatmentServices.fieldsRequired"))
														.append("createdTime",
																new BasicDBObject("$first",
																		"$treatmentServices.createdTime"))
														.append("updatedTime",
																new BasicDBObject("$first",
																		"$treatmentServices.updatedTime"))
														.append("createdBy",
																new BasicDBObject("$first",
																		"$treatmentServices.createdBy"))
														.append("count", new BasicDBObject("$first", "$count")))),
								Aggregation.sort(Direction.DESC, "count"));
			}

			response = mongoTemplate.aggregate(aggregation, PatientTreatmentCollection.class, TreatmentService.class)
					.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting most prescribed drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting most prescribed drugs");
		}
		return response;
	}

}
