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
import com.dpdocter.collections.GroupCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
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
				criteria=criteria.and("createdTime").gte(new Date(Long.parseLong(fromDate)));
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				criteria=criteria.and("createdTime").lte(new Date(Long.parseLong(toDate)));
			}
			Aggregation aggregation = null;
			switch (PatientAnalyticType.valueOf(queryType.toUpperCase())) {
			case NEW_PATIENT: {
				ProjectionOperation projectList = new ProjectionOperation(Fields.from(
						Fields.field("patient.id", "$userId"),
						Fields.field("patient.localPatientName", "$localPatientName"),
						Fields.field("patient.PID", "$PID"), Fields.field("patient.firstName", "$user.firstName"),
						Fields.field("patient.registrationDate", "$registrationDate"),
						Fields.field("patient.createdTime", "$createdTime")));
				
				criteria=criteria.and("doctorId").is(new ObjectId(doctorId)).and("locationId").is(new ObjectId(locationId))
						.and("hospitalId").is(new ObjectId(hospitalId));
				switch (SearchType.valueOf(searchType.toUpperCase())) {
				case DAILY: {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear().as("year").and("week")
									.extractWeek().as("week"),
							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("day", "$day").append("month", "$month").append("year",
													"$year")).append("day", new BasicDBObject("$first", "$day"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("createdTime",
																	new BasicDBObject("$first", "$createdTime"))
															.append("data", new BasicDBObject("$push", "$patient"))
															.append("count", new BasicDBObject("$size", "$patient")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
					break;
				}

				case WEEKLY: {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear().as("year"),
							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("week", "$week").append("month", "$month").append("year",
													"$year")).append("day", new BasicDBObject("$first", "$day"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("week", new BasicDBObject("$first", "$week"))
															.append("createdTime",
																	new BasicDBObject("$first", "$createdTime"))
															.append("data", new BasicDBObject("$push", "$patient"))
															.append("count", new BasicDBObject("$size", "$patient")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
					break;
				}

				case MONTHLY: {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear()
									.as("year"),
							new CustomAggregationOperation(
									new BasicDBObject("$group",
											new BasicDBObject("_id",
													new BasicDBObject("month", "$month").append("year", "$year"))
															.append("day", new BasicDBObject("$first", "$day"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("createdTime",
																	new BasicDBObject("$first", "$createdTime"))
															.append("data", new BasicDBObject("$push", "$patient"))
															.append("count", new BasicDBObject("$size", "$patient")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

					break;
				}
				case YEARLY: {

					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear().as("year"),

							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id", new BasicDBObject("year", "$year"))
											.append("day", new BasicDBObject("$first", "$day"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("data", new BasicDBObject("$push", "$patient"))
											.append("count", new BasicDBObject("$size", "$data")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

					break;

				}
				default:
					break;
				}
				AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
						PatientCollection.class, PatientAnalyticResponse.class);
				response = aggregationResults.getMappedResults();
				break;

			}

			case CITY_WISE: {
				ProjectionOperation projectList = new ProjectionOperation(Fields.from(
						Fields.field("patient.id", "$userId"),
						Fields.field("patient.localPatientName", "$localPatientName"),
						Fields.field("patient.PID", "$PID"), Fields.field("patient.firstName", "$user.firstName"),
						Fields.field("patient.registrationDate", "$registrationDate"),
						Fields.field("patient.createdTime", "$createdTime"), Fields.field("city", "$address.city")));
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					criteria.and("$address.city").is(searchTerm);
				}
				criteria.and("doctorId").is(new ObjectId(doctorId)).and("locationId").is(new ObjectId(locationId))
						.and("hospitalId").is(new ObjectId(hospitalId));
				switch (SearchType.valueOf(searchType.toUpperCase())) {
				case DAILY: {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear().as("year").and("week")
									.extractWeek().as("week"),
							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("day", "$day").append("month", "$month").append("year",
													"$year")).append("day", new BasicDBObject("$first", "$day"))
															.append("city", new BasicDBObject("$first", "$city"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("createdTime",
																	new BasicDBObject("$first", "$createdTime"))
															.append("data", new BasicDBObject("$push", "$patient"))
															.append("count", new BasicDBObject("$size", "$patient")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
					break;
				}
				case WEEKLY: {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear().as("year"),
							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("week", "$week").append("month", "$month").append("year",
													"$year")).append("day", new BasicDBObject("$first", "$day"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("week", new BasicDBObject("$first", "$week"))
															.append("city", new BasicDBObject("$first", "$city"))
															.append("createdTime",
																	new BasicDBObject("$first", "$createdTime"))
															.append("data", new BasicDBObject("$push", "$patient"))
															.append("count", new BasicDBObject("$size", "$patient")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
					break;
				}

				case MONTHLY: {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear()
									.as("year"),
							new CustomAggregationOperation(
									new BasicDBObject("$group",
											new BasicDBObject("_id",
													new BasicDBObject("month", "$month").append("year", "$year"))
															.append("day", new BasicDBObject("$first", "$day"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("city", new BasicDBObject("$first", "$city"))
															.append("createdTime",
																	new BasicDBObject("$first", "$createdTime"))
															.append("data", new BasicDBObject("$push", "$patient"))
															.append("count", new BasicDBObject("$size", "$patient")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

					break;
				}
				case YEARLY: {

					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear().as("year"),

							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id", new BasicDBObject("year", "$year"))
											.append("day", new BasicDBObject("$first", "$day"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("city", new BasicDBObject("$first", "$city"))
											.append("data", new BasicDBObject("$push", "$patient"))
											.append("count", new BasicDBObject("$size", "$data")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

					break;

				}
				default:
					break;
				}
				AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
						PatientCollection.class, PatientAnalyticResponse.class);
				response = aggregationResults.getMappedResults();
				break;
			}
			case LOCALITY_WISE: {
				ProjectionOperation projectList = new ProjectionOperation(Fields.from(
						Fields.field("patient.id", "$userId"),
						Fields.field("patient.localPatientName", "$localPatientName"),
						Fields.field("patient.PID", "$PID"), Fields.field("patient.firstName", "$user.firstName"),
						Fields.field("patient.registrationDate", "$registrationDate"),
						Fields.field("patient.createdTime", "$createdTime"), Fields.field("city", "$address.city")));
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					criteria.and("address.locality").regex("^" + searchTerm, "i");
				}

				criteria.and("doctorId").is(new ObjectId(doctorId)).and("locationId").is(new ObjectId(locationId))
						.and("hospitalId").is(new ObjectId(hospitalId));
				switch (SearchType.valueOf(searchType.toUpperCase())) {
				case DAILY: {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear().as("year").and("week")
									.extractWeek().as("week"),
							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("day", "$day").append("month", "$month").append("year",
													"$year")).append("day", new BasicDBObject("$first", "$day"))
															.append("city", new BasicDBObject("$first", "$city"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("createdTime",
																	new BasicDBObject("$first", "$createdTime"))
															.append("data", new BasicDBObject("$push", "$patient"))
															.append("count", new BasicDBObject("$size", "$patient")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
					break;
				}

				case WEEKLY: {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear().as("year"),
							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("week", "$week").append("month", "$month").append("year",
													"$year")).append("day", new BasicDBObject("$first", "$day"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("week", new BasicDBObject("$first", "$week"))
															.append("city", new BasicDBObject("$first", "$city"))
															.append("createdTime",
																	new BasicDBObject("$first", "$createdTime"))
															.append("data", new BasicDBObject("$push", "$patient"))
															.append("count", new BasicDBObject("$size", "$patient")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
					break;
				}

				case MONTHLY: {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear()
									.as("year"),
							new CustomAggregationOperation(
									new BasicDBObject("$group",
											new BasicDBObject("_id",
													new BasicDBObject("month", "$month").append("year", "$year"))
															.append("day", new BasicDBObject("$first", "$day"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("city", new BasicDBObject("$first", "$city"))
															.append("createdTime",
																	new BasicDBObject("$first", "$createdTime"))
															.append("data", new BasicDBObject("$push", "$patient"))
															.append("count", new BasicDBObject("$size", "$patient")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

					break;
				}
				case YEARLY: {

					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear().as("year"),

							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id", new BasicDBObject("year", "$year"))
											.append("day", new BasicDBObject("$first", "$day"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("city", new BasicDBObject("$first", "$city"))
											.append("data", new BasicDBObject("$push", "$patient"))
											.append("count", new BasicDBObject("$size", "$data")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

					break;

				}
				default:
					break;
				}
				AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
						PatientCollection.class, PatientAnalyticResponse.class);
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

				criteria.and("patient.doctorId").is(new ObjectId(doctorId)).and("patient.locationId")
						.is(new ObjectId(locationId)).and("patient.hospitalId").is(new ObjectId(hospitalId));
				criteria.and("group.doctorId").is(new ObjectId(doctorId)).and("group.locationId")
						.is(new ObjectId(locationId)).and("group.hospitalId").is(new ObjectId(hospitalId));
				switch (SearchType.valueOf(searchType.toUpperCase())) {
				case DAILY: {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							Aggregation.unwind("patient"), Aggregation.lookup("group_cl", "groupId", "_id", "group"),
							Aggregation.unwind("group"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear().as("year").and("week")
									.extractWeek().as("week"),

							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("day", "$day").append("month", "$month")
													.append("year", "$year").append("groupId", "$groupId"))
															.append("day", new BasicDBObject("$first", "$day"))
															.append("city", new BasicDBObject("$first", "$city"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("groupName",
																	new BasicDBObject("$first", "$groupName"))
															.append("groupId", new BasicDBObject("$first", "$groupId"))
															.append("createdTime",
																	new BasicDBObject("$first", "$createdTime"))
															.append("data", new BasicDBObject("$push", "$patient"))
															.append("count", new BasicDBObject("$size", "$patient")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
					break;
				}

				case WEEKLY: {

					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							Aggregation.unwind("patient"), Aggregation.lookup("group_cl", "groupId", "_id", "group"),
							Aggregation.unwind("group"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear().as("year").and("week")
									.extractWeek().as("week"),

							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("week", "$week").append("month", "$month")
													.append("year", "$year").append("groupId", "$groupId"))
															.append("day", new BasicDBObject("$first", "$day"))
															.append("city", new BasicDBObject("$first", "$city"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("groupName",
																	new BasicDBObject("$first", "$groupName"))
															.append("groupId", new BasicDBObject("$first", "$groupId"))
															.append("createdTime",
																	new BasicDBObject("$first", "$createdTime"))
															.append("data", new BasicDBObject("$push", "$patient"))
															.append("count", new BasicDBObject("$size", "$patient")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
					break;
				}

				case MONTHLY: {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							Aggregation.unwind("patient"), Aggregation.lookup("group_cl", "groupId", "_id", "group"),
							Aggregation.unwind("group"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear().as("year").and("week")
									.extractWeek().as("week"),
							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("month", "$month").append("year", "$year")
													.append("groupId", "$groupId"))
															.append("day", new BasicDBObject("$first", "$day"))
															.append("city", new BasicDBObject("$first", "$city"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("groupName",
																	new BasicDBObject("$first", "$groupName"))
															.append("groupId", new BasicDBObject("$first", "$groupId"))
															.append("createdTime",
																	new BasicDBObject("$first", "$createdTime"))
															.append("data", new BasicDBObject("$push", "$patient"))
															.append("count", new BasicDBObject("$size", "$patient")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

					break;
				}
				case YEARLY: {

					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							Aggregation.unwind("patient"), Aggregation.lookup("group_cl", "groupId", "_id", "group"),
							Aggregation.unwind("group"),
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear().as("year").and("week")
									.extractWeek()
									.as("week"),
							new CustomAggregationOperation(
									new BasicDBObject("$group",
											new BasicDBObject("_id",
													new BasicDBObject("year", "$year").append("groupId", "$groupId"))
															.append("day", new BasicDBObject("$first", "$day"))
															.append("city", new BasicDBObject("$first", "$city"))
															.append("month", new BasicDBObject("$first", "$month"))
															.append("year", new BasicDBObject("$first", "$year"))
															.append("groupName",
																	new BasicDBObject("$first", "$groupName"))
															.append("groupId", new BasicDBObject("$first", "$groupId"))
															.append("createdTime",
																	new BasicDBObject("$first", "$createdTime"))
															.append("data", new BasicDBObject("$push", "$patient"))
															.append("count", new BasicDBObject("$size", "$patient")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

					break;

				}
				default:
					break;
				}

				AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
						PatientGroupCollection.class, PatientAnalyticResponse.class);
				response = aggregationResults.getMappedResults();
				break;
			}
			case TOP_10_VISITED:{
				
				
			}
				
			case VISITED_PATIENT:
			default:
				break;

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting patient analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting patient analytic");
		}

		return response;

	}

}
