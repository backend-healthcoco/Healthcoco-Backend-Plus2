package com.dpdocter.services.impl;

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
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.GroupCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.PatientQueueCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.PatientAnalyticType;
import com.dpdocter.enums.PrescriptionItems;
import com.dpdocter.enums.SearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.AppointmentAnalyticResponse;
import com.dpdocter.response.AppointmentAverageTimeAnalyticResponse;
import com.dpdocter.response.AppointmentCountAnalyticResponse;
import com.dpdocter.response.AppointmentDeatilAnalyticResponse;
import com.dpdocter.response.PatientAnalyticResponse;
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
	public List<PatientAnalyticResponse> getPatientCount(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String queryType, String searchType, String searchTerm) {
		List<PatientAnalyticResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria = criteria.and("createdTime").gte(new Date(Long.parseLong(fromDate)));
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				criteria = criteria.and("createdTime").lte(new Date(Long.parseLong(toDate)));
			}
			Aggregation aggregation = null;
			CustomAggregationOperation aggregationOperation = null;
			switch (PatientAnalyticType.valueOf(queryType.toUpperCase())) {
			case NEW_PATIENT: {
				ProjectionOperation projectList = new ProjectionOperation(
						Fields.from(Fields.field("patients._id", "$userId"),
								Fields.field("patients.localPatientName", "$localPatientName"),
								Fields.field("patients.pid", "$PID"), Fields.field("patients.firstName", "$firstName"),
								Fields.field("patients.registrationDate", "$registrationDate"),
								Fields.field("patients.createdTime", "$createdTime"),
								Fields.field("createdTime", "$createdTime")));

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
															.append("date", new BasicDBObject("$first", "$createdTime"))
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
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("week", new BasicDBObject("$first", "$week"))
													.append("date", new BasicDBObject("$first", "$createdTime"))
													.append("patients", new BasicDBObject("$push", "$patient"))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("week", new BasicDBObject("$first", "$week"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$createdTime"))
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
						Fields.field("patient.createdTime", "$createdTime"), Fields.field("city", "$address.city")));
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
													.append("date", new BasicDBObject("$first", "$createdTime"))
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
													.append("date", new BasicDBObject("$first", "$createdTime"))
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
													.append("date", new BasicDBObject("$first", "$createdTime"))
													.append("patients", new BasicDBObject("$push", "$patient"))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year").append("city", "$city"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$createdTime"))
									.append("city", new BasicDBObject("$first", "$city"))
									.append("patients", new BasicDBObject("$push", "$patient"))));

					break;

				}
				default:
					break;
				}
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
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
						Fields.field("patient.createdTime", "$createdTime"), Fields.field("city", "$address.city")));
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
															.append("date", new BasicDBObject("$first", "$createdTime"))
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
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("city", new BasicDBObject("$first", "$city"))
													.append("date", new BasicDBObject("$first", "$createdTime"))
													.append("patients", new BasicDBObject("$push", "$patient"))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$createdTime"))
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
				ProjectionOperation projectList = new ProjectionOperation(Fields.from(
						Fields.field("patient.id", "$patient.userId"),
						Fields.field("patient.localPatientName", "$patient.localPatientName"),
						Fields.field("patient.PID", "$patient.PID"),
						Fields.field("patient.firstName", "$user.firstName"),
						Fields.field("patient.registrationDate", "$patient.registrationDate"),
						Fields.field("patient.createdTime", "$createdTime"), Fields.field("groupName", "$group.name"),
						Fields.field("createdTime", "$group.createdTime"), Fields.field("groupId", "$group._id")));
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
													.append("date", new BasicDBObject("$first", "$createdTime"))
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
													.append("date", new BasicDBObject("$first", "$createdTime"))
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
													.append("date", new BasicDBObject("$first", "$createdTime"))
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
													.append("date", new BasicDBObject("$first", "$createdTime"))
													.append("patients", new BasicDBObject("$push", "$patient"))));

					break;

				}
				default:
					break;
				}
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "patientId", "_id", "user"),
						Aggregation.unwind("user"),
						Aggregation.lookup("patient_cl", "patientId", "patientId", "patient"),
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
						Fields.field("createdTime", "$visit.createdTime")));
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
															.append("date",
																	new BasicDBObject("$first", "$createdTime"))
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

//	public List<PatientAnalyticResponse> getinvoiceIncome(String doctorId, String locationId, String hospitalId,
//			String fromDate, String toDate, String queryType, String searchType, String searchTerm) {
//		List<PatientAnalyticResponse> response = null;
//		Criteria criteria = new Criteria();
//		if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
//			criteria = criteria.and("createdTime").gte(new Date(Long.parseLong(fromDate)));
//		}
//		if (!DPDoctorUtils.anyStringEmpty(toDate)) {
//			criteria = criteria.and("createdTime").lte(new Date(Long.parseLong(toDate)));
//		}
//		Aggregation aggregation = null;
//		CustomAggregationOperation aggregationOperation = null;
//		switch (IncomeAnalyticType.valueOf(queryType.toUpperCase())) {
//
//		}
//
//		return null;
//	}

	@Override
	public List<?> getMostPrescribedPrescriptionItems(String type, String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String searchType, int page, int size) {
		List<?> response = null;
		try {
			switch (PrescriptionItems.valueOf(type.toUpperCase())) {

				case DRUGS: {
					response = getMostPrescribedDrugs(doctorId, locationId, hospitalId, fromDate, toDate, searchType, page, size); break;
				}
				case DIAGNOSTICTEST: {
					response = getMostPrescribedLabTests(locationId, hospitalId, fromDate, toDate, searchType, page, size); break;
				}
				default : break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting most prescribed prescription items");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting most prescribed prescription items");
		}
		return response;
	}

	private List<?> getMostPrescribedLabTests(String locationId, String hospitalId, String fromDate, String toDate,
			String searchType, int page, int size) {
		List<DiagnosticTest> response = null;
		try {
			Criteria criteria = new Criteria("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria = criteria.and("createdTime").gte(new Date(Long.parseLong(fromDate)));
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				criteria = criteria.and("createdTime").lte(new Date(Long.parseLong(toDate)));
			}
			int skip=0, limit = 10;
			if(size > 0) {skip = (page) * size;limit = size;}
			
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("$diagnosticTests"), Aggregation.group("$diagnosticTests.testId").count().as("count"), 
				Aggregation.lookup("diagnostic_test_cl", "_id", "_id", "test"), Aggregation.unwind("test"), 
				
				new CustomAggregationOperation(
						new BasicDBObject("$group",
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
				
				
				Aggregation.sort(Direction.DESC, "count"),Aggregation.skip(skip), Aggregation.limit(limit));
		
		response = mongoTemplate.aggregate(aggregation, PrescriptionCollection.class, DiagnosticTest.class).getMappedResults();
		}catch (Exception e) {
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
			int skip=0, limit = 10;
			if(size > 0) {skip = (page) * size;limit = size;}
			
			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria = criteria.and("createdTime").gte(new Date(Long.parseLong(fromDate)));
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				criteria = criteria.and("createdTime").lte(new Date(Long.parseLong(toDate)));
			}
			
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("$items"), Aggregation.group("$items.drugId").count().as("count"), 
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
											.append("genericNames", new BasicDBObject("$first", "$drug.genericNames"))
											.append("drugCode", new BasicDBObject("$first", "$drug.drugCode"))
											.append("createdTime", new BasicDBObject("$first", "$drug.createdTime"))
											.append("updatedTime", new BasicDBObject("$first", "$drug.updatedTime"))
											.append("createdBy", new BasicDBObject("$first", "$drug.createdBy"))
											.append("count", new BasicDBObject("$first", "$count")))),
					
					
					Aggregation.sort(Direction.DESC, "count"),Aggregation.skip(skip), Aggregation.limit(limit));
			
			response = mongoTemplate.aggregate(aggregation, PrescriptionCollection.class, Drug.class).getMappedResults();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting most prescribed drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting most prescribed drugs");
		}
		return response;
	}

	@Override
	public AppointmentAnalyticResponse getAppointmentAnalyticnData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, String searchTerm, int page, int size) {
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
			if(count > 0) {
				response = new AppointmentAnalyticResponse();
				int skip=0, limit=10;
				if(size > 0) {skip=page*size; limit= size;}
				
				Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria), 
						Aggregation.lookup("patient_queue_cl", "appointmentId", "appointmentId", "patientQueue"),
						new CustomAggregationOperation(new BasicDBObject("$unwind", new BasicDBObject("path", "$patientQueue").append("preserveNullAndEmptyArrays", true))),
//						Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
//						new CustomAggregationOperation(new BasicDBObject("$unwind", new BasicDBObject("path", "$patient").append("preserveNullAndEmptyArrays", true))),
//						Aggregation.match(new Criteria("patient.locationId").is(new ObjectId(locationId))),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						new CustomAggregationOperation(new BasicDBObject("$unwind", new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(
								new BasicDBObject("$group",
										new BasicDBObject("id","$_id")
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
						Aggregation.skip(skip), Aggregation.limit(limit),
						Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.fromTime")));
				
				List<AppointmentDeatilAnalyticResponse> analyticResponses = mongoTemplate.aggregate(aggregation, AppointmentCollection.class, AppointmentDeatilAnalyticResponse.class).getMappedResults();
				response.setTotalAppointments(count);
				
				for(AppointmentDeatilAnalyticResponse appointmentDeatilAnalyticResponse : analyticResponses) {
					List<PatientCard> patientCards = mongoTemplate
							.aggregate(
									Aggregation.newAggregation(
											Aggregation.match(new Criteria("userId")
													.is(new ObjectId(appointmentDeatilAnalyticResponse.getPatientId())).and("locationId")
													.is(new ObjectId(appointmentDeatilAnalyticResponse.getLocationId())).and("hospitalId")
													.is(new ObjectId(appointmentDeatilAnalyticResponse.getHospitalId())))), PatientCollection.class, PatientCard.class).getMappedResults();
					if (patientCards != null && !patientCards.isEmpty()) {
						appointmentDeatilAnalyticResponse.setPatientName(patientCards.get(0).getLocalPatientName());
					}
				}
				response.setAppointments(analyticResponses);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment analytics data");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting appointment analytics data");
		}
		return response;
	}

	@Override
	public List<AppointmentAverageTimeAnalyticResponse> getAppointmentAverageTimeAnalyticnData(String doctorId,
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
			if(!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("date").gt(start);
			}
			if(!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("date").lte(end);
			}

			AggregationOperation aggregationOperation = null;
			if(!DPDoctorUtils.anyStringEmpty(searchType))
			switch (SearchType.valueOf(searchType.toUpperCase())) {
				
				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("day", "$day").append("month", "$month").append("year",
													"$year")).append("averageWaitingTime", new BasicDBObject("$avg", "$waitedFor"))
																.append("averageEngagedTime", new BasicDBObject("$avg", "$engagedFor"))
																.append("date", new BasicDBObject("$first", "$date"))));
	
					break;
				}

				case WEEKLY: {
	
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("averageWaitingTime", new BasicDBObject("$avg", "$waitedFor"))
													.append("averageEngagedTime", new BasicDBObject("$avg", "$engagedFor"))
													.append("date", new BasicDBObject("$first", "$date"))));
	
					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("month", "$month").append("year", "$year"))
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
				aggregationOperation = new CustomAggregationOperation(
						new BasicDBObject("$group",
								new BasicDBObject("_id",
										new BasicDBObject("day", "$day").append("month", "$month").append("year",
												"$year")).append("averageWaitingTime", new BasicDBObject("$avg", "$waitedFor"))
															.append("averageEngagedTime", new BasicDBObject("$avg", "$engagedFor"))
															.append("date", new BasicDBObject("$first", "$date"))));
			}
		
			Aggregation	aggregation = null;
			if(size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new ProjectionOperation(
								Fields.from(Fields.field("date", "$date"), Fields.field("waitedFor", "$waitedFor"), Fields.field("engagedFor", "$engagedFor"))).and("date").extractDayOfMonth().as("day").and("date").extractMonth()
									.as("month").and("date").extractYear().as("year").and("date")
									.extractWeek().as("week"),
							aggregationOperation, Aggregation.sort(Direction.DESC, "date"), Aggregation.skip(page * size), Aggregation.limit(size));
			}else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new ProjectionOperation(
								Fields.from(Fields.field("date", "$date"), Fields.field("waitedFor", "$waitedFor"), Fields.field("engagedFor", "$engagedFor"))).and("date").extractDayOfMonth().as("day").and("date").extractMonth()
									.as("month").and("date").extractYear().as("year").and("date")
									.extractWeek().as("week"),
							aggregationOperation, Aggregation.sort(Direction.DESC, "date"));
			}
				AggregationResults<AppointmentAverageTimeAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
						PatientQueueCollection.class, AppointmentAverageTimeAnalyticResponse.class);
				response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment average time analytics data");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting appointment average time analytics data");
		}
		return response;
	}

	@Override
	public List<AppointmentCountAnalyticResponse> getAppointmentCountAnalyticnData(String doctorId, String locationId,
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

			if(!DPDoctorUtils.anyStringEmpty(queryType)) {
				if(queryType.equalsIgnoreCase("CANCEL"))criteria.and("state").is(AppointmentState.CANCEL.getState());
				else if(queryType.equalsIgnoreCase("PATIENTGROUP")){
					response = getAppointmentCountAnalyticnDataByPatientGroup(doctorId, locationId,
							hospitalId, fromDate, toDate, page, size);
					return response;
				}
			}
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			if(!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("fromDate").gt(start);
			}
			if(!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("fromDate").lte(end);
			}

			AggregationOperation aggregationOperation = null;
			if(!DPDoctorUtils.anyStringEmpty(searchType))
			switch (SearchType.valueOf(searchType.toUpperCase())) {
				
				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("day", "$day").append("month", "$month").append("year",
													"$year"))
									.append("count", new BasicDBObject("$sum", 1))
									.append("date", new BasicDBObject("$first", "$fromDate"))));
	
					break;
				}

				case WEEKLY: {
	
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("count", new BasicDBObject("$sum", 1))
								.append("date", new BasicDBObject("$first", "$fromDate"))));
	
					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(
							new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("month", "$month").append("year", "$year"))
									.append("count", new BasicDBObject("$sum", 1))
									.append("date", new BasicDBObject("$first", "$fromDate"))));
	
					break;
				}
				case YEARLY: {
	
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
							.append("count", new BasicDBObject("$sum", 1))
							.append("date", new BasicDBObject("$first", "$fromDate"))));
	
					break;
	
				}
				default:
					break;
				}
			else {
				aggregationOperation = new CustomAggregationOperation(
						new BasicDBObject("$group",
								new BasicDBObject("_id",
										new BasicDBObject("day", "$day").append("month", "$month").append("year","$year"))
								.append("count", new BasicDBObject("$sum", 1))
								.append("date", new BasicDBObject("$first", "$fromDate"))));
			}
		
			Aggregation	aggregation = null;
			if(size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new ProjectionOperation(
								Fields.from(Fields.field("fromDate", "$fromDate"))).and("fromDate").extractDayOfMonth().as("day").and("fromDate").extractMonth()
									.as("month").and("fromDate").extractYear().as("year").and("fromDate")
									.extractWeek().as("week"),
							aggregationOperation, Aggregation.sort(Direction.DESC, "fromDate"), Aggregation.skip(page * size), Aggregation.limit(size));
			}else {
				aggregation = Aggregation.newAggregation(
						Aggregation.match(criteria),
						new ProjectionOperation(
								Fields.from(Fields.field("fromDate", "$fromDate"))).and("fromDate").extractDayOfMonth().as("day").and("fromDate").extractMonth()
									.as("month").and("fromDate").extractYear().as("year").and("fromDate")
									.extractWeek().as("week"),
							aggregationOperation, Aggregation.sort(Direction.DESC, "fromDate"));
			}
				AggregationResults<AppointmentCountAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
						AppointmentCollection.class, AppointmentCountAnalyticResponse.class);
				response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment count analytics data");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting appointment count analytics data");
		}
		return response;
	}

	private List<AppointmentCountAnalyticResponse> getAppointmentCountAnalyticnDataByPatientGroup(String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, int page, int size) {
		List<AppointmentCountAnalyticResponse> response = null;
		try {
				Criteria criteria = new Criteria();
				
				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria.and("doctorId").in(new ObjectId(doctorId));
				}
				if (!DPDoctorUtils.anyStringEmpty(locationId)) {
					criteria.and("locationId").is(new ObjectId(locationId));
				}
	
				Criteria criteria2 = new Criteria();
				Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
				if(!DPDoctorUtils.anyStringEmpty(fromDate)) {
					localCalendar.setTime(new Date(Long.parseLong(fromDate)));
					int currentDay = localCalendar.get(Calendar.DATE);
					int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
					int currentYear = localCalendar.get(Calendar.YEAR);
	
					DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
							DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
					criteria2.and("appointment.fromDate").gt(start);
				}
				if(!DPDoctorUtils.anyStringEmpty(toDate)) {
					localCalendar.setTime(new Date(Long.parseLong(toDate)));
					int currentDay = localCalendar.get(Calendar.DATE);
					int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
					int currentYear = localCalendar.get(Calendar.YEAR);
	
					DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
							DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
					criteria2.and("appointment.fromDate").lte(end);
				}
				
				Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria), 
						Aggregation.lookup("patient_group_cl", "groupId", "id", "patientGroupCollection"),
						Aggregation.unwind("patientGroupCollection"),
						Aggregation.lookup("appointment_cl", "patientId", "patientGroupCollection.patientId", "appointment"),
						Aggregation.unwind("appointment"),
						Aggregation.match(criteria2), 
						new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("_id","id")
								.append("groupName", new BasicDBObject("$first", "$name"))
								.append("count", new BasicDBObject("$sum", 1)))));
				
	
				response = mongoTemplate.aggregate(aggregation, GroupCollection.class, AppointmentCountAnalyticResponse.class).getMappedResults();

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment count analytics data by patient group");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting appointment count analytics data by patient group");
		}
		return response;
	}

}
