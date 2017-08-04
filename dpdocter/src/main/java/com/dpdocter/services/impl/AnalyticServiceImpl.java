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
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.IncomeAnalyticType;
import com.dpdocter.enums.PatientAnalyticType;
import com.dpdocter.enums.SearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.PatientAnalyticResponse;
import com.dpdocter.services.AnalyticService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
@Transactional
public class AnalyticServiceImpl implements AnalyticService {

	@Autowired
	private MongoTemplate mongoTemplate;

	Logger logger = Logger.getLogger(AnalyticServiceImpl.class);

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

	public List<PatientAnalyticResponse> getinvoiceIncome(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String queryType, String searchType, String searchTerm) {
		List<PatientAnalyticResponse> response = null;
		Criteria criteria = new Criteria();
		if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
			criteria = criteria.and("createdTime").gte(new Date(Long.parseLong(fromDate)));
		}
		if (!DPDoctorUtils.anyStringEmpty(toDate)) {
			criteria = criteria.and("createdTime").lte(new Date(Long.parseLong(toDate)));
		}
		Aggregation aggregation = null;
		CustomAggregationOperation aggregationOperation = null;
		switch (IncomeAnalyticType.valueOf(queryType.toUpperCase())) {

		}

		return null;
	}

}
