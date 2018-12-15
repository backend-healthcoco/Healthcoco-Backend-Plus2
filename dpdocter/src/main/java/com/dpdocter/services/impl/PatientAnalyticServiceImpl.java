package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.PatientAnalyticData;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.enums.PatientAnalyticType;
import com.dpdocter.enums.SearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.PatientAnalyticResponse;
import com.dpdocter.services.PatientAnalyticService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class PatientAnalyticServiceImpl implements PatientAnalyticService {

	@Autowired
	private MongoTemplate mongoTemplate;

	Logger logger = Logger.getLogger(PatientAnalyticServiceImpl.class);

	@Override
	public List<PatientAnalyticResponse> getPatientAnalytic(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String queryType, String searchType, String searchTerm) {
		List<PatientAnalyticResponse> response = null;

		try {
			Criteria criteria = new Criteria();

			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(fromDate));
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(Long.parseLong(toDate));
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date();
				to = new Date();
			}
			fromTime = DPDoctorUtils.getStartTime(from);
			toTime = DPDoctorUtils.getEndTime(to);

			switch (PatientAnalyticType.valueOf(queryType.toUpperCase())) {
			case NEW_PATIENT: {

				response = getNewPatientAnalyticData(fromTime, toTime, criteria, searchType, doctorId, locationId,
						hospitalId, searchTerm);
				break;
			}

			case CITY_WISE: {
				response = getPatientCountCitiWise(fromTime, toTime, criteria, searchType, doctorId, locationId,
						hospitalId, searchTerm);
				break;
			}

			case VISITED_PATIENT: {
				response = getVisitedPatientcount(fromTime, toTime, criteria, searchType, doctorId, locationId,
						hospitalId, searchTerm);
				break;
			}

			default:
				break;

			}

		} catch (

		Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting patient analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting patient analytic");
		}

		return response;

	}

	private List<PatientAnalyticResponse> getNewPatientAnalyticData(DateTime fromTime, DateTime toTime,
			Criteria criteria, String searchType, String doctorId, String locationId, String hospitalId,
			String searchTerm) {
		Criteria secondCriteria = new Criteria();
		CustomAggregationOperation aggregationOperation = null;
		ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("count", "$userId"),
				Fields.field("date", "$createdTime"), Fields.field("createdTime", "$createdTime")));
		if (toTime != null && fromTime != null) {

			criteria.and("createdTime").gte(new Date(fromTime.getMillis())).lte(new Date(toTime.getMillis()));

		} else if (toTime != null) {
			criteria.and("createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria.and("createdTime").gte(fromTime);
		}

		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			criteria = criteria.and("locationId").is(new ObjectId(locationId));
		}
		if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
			criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
		}

		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			secondCriteria.orOperator(new Criteria("localPatientName").regex(searchTerm, "i"),
					new Criteria("user.firstName").regex(searchTerm, "i"),
					new Criteria("user.mobileNumber").regex(searchTerm, "i"),
					new Criteria("PID").regex(searchTerm, "i"));
		}
		switch (SearchType.valueOf(searchType.toUpperCase())) {
		case DAILY: {

			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("week", new BasicDBObject("$first", "$week"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("count", new BasicDBObject("$sum", 1))));

			break;
		}

		case WEEKLY: {

			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("week", new BasicDBObject("$first", "$week"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("count", new BasicDBObject("$sum", 1))));

			break;
		}

		case MONTHLY: {

			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
							.append("month", new BasicDBObject("$first", "$month"))
							.append("year", new BasicDBObject("$first", "$year"))
							.append("date", new BasicDBObject("$first", "$date"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("count", new BasicDBObject("$sum", 1))));

			break;
		}
		case YEARLY: {

			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("year", "$year"))
							.append("year", new BasicDBObject("$first", "$year"))
							.append("date", new BasicDBObject("$first", "$date"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("count", new BasicDBObject("$sum", 1))));

			break;

		}
		default:
			break;
		}

		Aggregation aggregation = Aggregation
				.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "userId", "_id", "user"),
						Aggregation.unwind("user"), Aggregation.match(secondCriteria),
						projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime").extractMonth()
								.as("month").and("createdTime").extractYear().as("year").and("createdTime")
								.extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "createdTime")))
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
				"patient_cl", PatientAnalyticResponse.class);

		return aggregationResults.getMappedResults();

	}

	private List<PatientAnalyticResponse> getPatientCountCitiWise(DateTime fromTime, DateTime toTime, Criteria criteria,
			String searchType, String doctorId, String locationId, String hospitalId, String searchTerm) {
		CustomAggregationOperation aggregationOperation = null;

		ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("date", "$createdTime"),
				Fields.field("city", "$address.city"), Fields.field("count", "$userId")));
		if (toTime != null && fromTime != null) {

			criteria.and("createdTime").gte(new Date(fromTime.getMillis())).lte(new Date(toTime.getMillis()));
		} else if (toTime != null) {
			criteria.and("createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria.and("createdTime").gte(fromTime);
		}
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			criteria.and("address.city").is(searchTerm);
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
											.append("week", new BasicDBObject("$first", "$week"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("date", new BasicDBObject("$first", "$date"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("count", new BasicDBObject("$sum", 1))));

			break;
		}
		case WEEKLY: {

			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year")
									.append("city", "$city")).append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("week", new BasicDBObject("$first", "$week"))
											.append("city", new BasicDBObject("$first", "$city"))
											.append("date", new BasicDBObject("$first", "$date"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("count", new BasicDBObject("$sum", 1))));

			break;
		}

		case MONTHLY: {

			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("month", "$month").append("year", "$year").append("city", "$city"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("city", new BasicDBObject("$first", "$city"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("count", new BasicDBObject("$sum", 1))));

			break;
		}
		case YEARLY: {

			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("year", "$year").append("city", "$city"))
							.append("year", new BasicDBObject("$first", "$year"))
							.append("city", new BasicDBObject("$first", "$city"))
							.append("date", new BasicDBObject("$first", "$date"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("count", new BasicDBObject("$sum", 1))));

			break;

		}
		default:
			break;
		}
		Aggregation aggregation = null;

		aggregation = Aggregation
				.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "userId", "_id", "user"),
						Aggregation.unwind("user"),
						projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime").extractMonth()
								.as("month").and("createdTime").extractYear().as("year").and("createdTime")
								.extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "createdTime")))
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
				"patient_cl", PatientAnalyticResponse.class);

		return aggregationResults.getMappedResults();

	}

	private List<PatientAnalyticResponse> getPatientAnalyticInGroup(DateTime fromTime, DateTime toTime,
			Criteria criteria, String searchType, String doctorId, String locationId, String hospitalId,
			String searchTerm) {
		CustomAggregationOperation aggregationOperation = null;
		CustomAggregationOperation aggregationOperation2 = null;
		ProjectionOperation projectList = new ProjectionOperation(Fields.from(
				Fields.field("groups.groupCreatedDate", "$group.createdTime"), Fields.field("count", "$patientId"),
				Fields.field("groupName", "$group.name"), Fields.field("date", "$createdTime"),
				Fields.field("groups.patientCount", "$group._id"), Fields.field("groupId", "$group._id")));
		if (toTime != null && fromTime != null) {

			criteria.and("createdTime").gte(new Date(fromTime.getMillis())).lte(new Date(toTime.getMillis()));

		} else if (toTime != null) {
			criteria.and("createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria.and("createdTime").gte(fromTime);
		}
		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			criteria = criteria.and("group.doctorId").is(new ObjectId(doctorId));
		}
		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			criteria = criteria.and("patient.locationId").is(new ObjectId(locationId)).and("group.locationId")
					.is(new ObjectId(locationId));
		}
		if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
			criteria = criteria.and("patient.hospitalId").is(new ObjectId(hospitalId)).and("group.hospitalId")
					.is(new ObjectId(hospitalId));
		}

		criteria = criteria.and("discarded").is(false);

		switch (SearchType.valueOf(searchType.toUpperCase())) {
		case DAILY: {

			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year")
									.append("groupId", "$groupId")).append("day", new BasicDBObject("$first", "$day"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("groups.groupName",
													new BasicDBObject("$first", "$groups.groupName"))
											.append("groups.groupId", new BasicDBObject("$first", "$groups.groupName"))
											.append("groups.patientCount", new BasicDBObject("$sum", 1))
											.append("groups.groupCreatedDate",
													new BasicDBObject("$first", "$groups.groupCreatedDate"))
											.append("date", new BasicDBObject("$first", "$date"))
											.append("week", new BasicDBObject("$first", "$week"))
											.append("count", new BasicDBObject("$sum", 1))));

			aggregationOperation2 = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("groups", new BasicDBObject("$push", "$groups"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("week", new BasicDBObject("$first", "$week"))
									.append("count", new BasicDBObject("$sum", 1))));

			break;
		}

		case WEEKLY: {

			aggregationOperation2 = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("groups", new BasicDBObject("$push", "$groups"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("week", new BasicDBObject("$first", "$week"))
									.append("count", new BasicDBObject("$sum", 1))));

			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year")
									.append("groupId", "$groupId")).append("day", new BasicDBObject("$first", "$day"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("groups.groupName",
													new BasicDBObject("$first", "$groups.groupName"))
											.append("groups.groupId", new BasicDBObject("$first", "$groups.groupName"))
											.append("groups.patientCount", new BasicDBObject("$sum", 1))
											.append("groups.groupCreatedDate",
													new BasicDBObject("$first", "$groups.groupCreatedDate"))
											.append("date", new BasicDBObject("$first", "$date"))
											.append("week", new BasicDBObject("$first", "$week"))
											.append("count", new BasicDBObject("$sum", 1))));

			break;
		}

		case MONTHLY: {
			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("month", "$month").append("year", "$year").append("groupId", "$groupId"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("groups.groupName", new BasicDBObject("$first", "$groups.groupName"))
									.append("groups.groupId", new BasicDBObject("$first", "$groups.groupId"))
									.append("groups.patientCount", new BasicDBObject("$sum", 1))
									.append("groups.groupCreatedDate",
											new BasicDBObject("$first", "$groups.groupCreatedDate"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("week", new BasicDBObject("$first", "$week"))
									.append("count", new BasicDBObject("$sum", 1))));

			aggregationOperation2 = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
							.append("day", new BasicDBObject("$first", "$day"))
							.append("month", new BasicDBObject("$first", "$month"))
							.append("year", new BasicDBObject("$first", "$year"))
							.append("groups", new BasicDBObject("$push", "$groups"))
							.append("date", new BasicDBObject("$first", "$date"))
							.append("week", new BasicDBObject("$first", "$week"))
							.append("count", new BasicDBObject("$sum", 1))));

			break;
		}
		case YEARLY: {

			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("year", "$year").append("groupId", "$groupId"))
							.append("day", new BasicDBObject("$first", "$day"))
							.append("month", new BasicDBObject("$first", "$month"))
							.append("year", new BasicDBObject("$first", "$year"))
							.append("groups.groupName", new BasicDBObject("$first", "$groups.groupName"))
							.append("groups.groupId", new BasicDBObject("$first", "$groups.groupName"))
							.append("groups.patientCount", new BasicDBObject("$sum", 1))
							.append("groups.groupCreatedDate", new BasicDBObject("$first", "$groups.groupCreatedDate"))
							.append("date", new BasicDBObject("$first", "$date"))
							.append("week", new BasicDBObject("$first", "$week"))
							.append("count", new BasicDBObject("$sum", 1))));

			aggregationOperation2 = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("year", "$year"))
							.append("day", new BasicDBObject("$first", "$day"))
							.append("month", new BasicDBObject("$first", "$month"))
							.append("year", new BasicDBObject("$first", "$year"))
							.append("groups", new BasicDBObject("$push", "$groups"))
							.append("date", new BasicDBObject("$first", "$date"))
							.append("week", new BasicDBObject("$first", "$week"))
							.append("count", new BasicDBObject("$sum", 1))));

			break;

		}
		default:
			break;
		}
		Aggregation aggregation = null;

		aggregation = Aggregation
				.newAggregation(Aggregation.lookup("user_cl", "patientId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
						Aggregation.unwind("patient"), Aggregation.lookup("group_cl", "groupId", "_id", "group"),
						Aggregation.unwind("group"), Aggregation.match(criteria),
						projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime").extractMonth()
								.as("month").and("createdTime").extractYear().as("year").and("createdTime")
								.extractWeek().as("week"),
						aggregationOperation, aggregationOperation2,
						Aggregation.sort(new Sort(Sort.Direction.ASC, "date")))
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
				PatientGroupCollection.class, PatientAnalyticResponse.class);

		return aggregationResults.getMappedResults();
	}

	private List<PatientAnalyticResponse> getPatientAnalyticLocalityWise(DateTime fromTime, DateTime toTime,
			Criteria criteria, String searchType, String doctorId, String locationId, String hospitalId,
			String searchTerm) {
		CustomAggregationOperation aggregationOperation = null;
		ProjectionOperation projectList = new ProjectionOperation(
				Fields.from(Fields.field("date", "$createdTime"), Fields.field("date", "$createdTime"),
						Fields.field("city", "$address.city"), Fields.field("count", "$userId")));
		if (toTime != null && fromTime != null) {

			criteria.and("createdTime").gte(new Date(fromTime.getMillis())).lte(new Date(toTime.getMillis()));
		} else if (toTime != null) {
			criteria.and("createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria.and("createdTime").gte(fromTime);
		}
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
			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year")
									.append("city", "$city")).append("day", new BasicDBObject("$first", "$day"))
											.append("city", new BasicDBObject("$first", "$city"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("date", new BasicDBObject("$first", "$date"))
											.append("count", new BasicDBObject("$sum", 1))));
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
											.append("count", new BasicDBObject("$sum", 1))));
			break;
		}

		case MONTHLY: {
			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("month", "$month").append("year", "$year").append("city", "$city"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("city", new BasicDBObject("$first", "$city"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("count", new BasicDBObject("$sum", 1))));
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
							.append("count", new BasicDBObject("$sum", 1))));
			break;

		}
		default:
			break;
		}

		Aggregation aggregation = null;

		aggregation = Aggregation
				.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "userId", "_id", "user"),
						Aggregation.unwind("user"),
						projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime").extractMonth()
								.as("month").and("createdTime").extractYear().as("year").and("createdTime")
								.extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "date")))
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
				"patient_cl", PatientAnalyticResponse.class);
		return aggregationResults.getMappedResults();

	}

	private List<PatientAnalyticResponse> getTopTenVisitedPatientData(DateTime fromTime, DateTime toTime,
			Criteria criteria, String searchType, String doctorId, String locationId, String hospitalId,
			String searchTerm) {
		CustomAggregationOperation aggregationOperation = null;
		ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("patient.id", "$userId"),
				Fields.field("patient.localPatientName", "$localPatientName"),
				Fields.field("patient.mobileNumber", "$user.mobileNumber"), Fields.field("patient.PID", "$PID"),
				Fields.field("patient.firstName", "$user.firstName"),
				Fields.field("patient.registrationDate", "$registrationDate"),
				Fields.field("patient.createdTime", "$createdTime"), Fields.field("patient.visitedTime", "$visit.time"),
				Fields.field("date", "$visit.createdTime"), Fields.field("count", "$userId")));
		if (toTime != null && fromTime != null) {

			criteria.and("visit.createdTime").gte(new Date(fromTime.getMillis())).lte(new Date(toTime.getMillis()));
		} else if (toTime != null) {
			criteria.and("visit.createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria.and("visit.createdTime").gte(fromTime);
		}
		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			criteria = criteria.and("visit.doctorId").is(new ObjectId(doctorId));
		}
		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			criteria = criteria.and("locationId").is(new ObjectId(locationId)).and("visit.locationId")
					.is(new ObjectId(locationId));
		}
		if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
			criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId)).and("visit.hospitalId")
					.is(new ObjectId(hospitalId));
		}

		switch (SearchType.valueOf(searchType.toUpperCase())) {
		case DAILY: {
			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("week", new BasicDBObject("$first", "$week"))
									.append("count", new BasicDBObject("$sum", 1))
									.append("patients", new BasicDBObject("$push", "$patient"))));
			break;
		}

		case WEEKLY: {
			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("week", new BasicDBObject("$first", "$week"))
									.append("count", new BasicDBObject("$sum", 1))
									.append("patients", new BasicDBObject("$push", "$patient"))));

			break;
		}

		case MONTHLY: {
			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
							.append("day", new BasicDBObject("$first", "$day"))
							.append("month", new BasicDBObject("$first", "$month"))
							.append("year", new BasicDBObject("$first", "$year"))
							.append("date", new BasicDBObject("$first", "$date"))
							.append("week", new BasicDBObject("$first", "$week"))
							.append("count", new BasicDBObject("$sum", 1))
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
							.append("week", new BasicDBObject("$first", "$week"))
							.append("count", new BasicDBObject("$sum", 1))
							.append("patients", new BasicDBObject("$push", "$patient"))));
			break;

		}

		default:
			break;
		}
		Aggregation aggregation = Aggregation
				.newAggregation(Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.lookup("patient_visit_cl", "userId", "patientId", "visit"),
						Aggregation.unwind("visit"), Aggregation.match(criteria),
						projectList.and("visit.createdTime").extractDayOfMonth().as("day").and("visit.createdTime")
								.extractMonth().as("month").and("visit.createdTime").extractYear().as("year")
								.and("visit.createdTime").extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "day")),
						Aggregation.skip((0) * 10), Aggregation.limit(10))
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
				"patient_cl", PatientAnalyticResponse.class);
		return aggregationResults.getMappedResults();

	}

	private List<PatientAnalyticResponse> getVisitedPatientcount(DateTime fromTime, DateTime toTime, Criteria criteria,
			String searchType, String doctorId, String locationId, String hospitalId, String searchTerm) {
		CustomAggregationOperation firstAggregationOperation = null;
		CustomAggregationOperation secondAggregationOperation = null;
		ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("count", "$patient.userId"),
				Fields.field("date", "$createdTime"), Fields.field("createdTime", "$createdTime")));
		if (toTime != null && fromTime != null) {

			criteria.and("createdTime").gte(new Date(fromTime.getMillis())).lte(new Date(toTime.getMillis()));
		} else if (toTime != null) {
			criteria.and("createdTime").lte(new Date(toTime.getMillis()));
		} else if (fromTime != null) {
			criteria.and("createdTime").gte(new Date(fromTime.getMillis()));
		}
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			criteria.orOperator(new Criteria("patient.localPatientName").regex(searchTerm, "i"),
					new Criteria("user.firstName").regex(searchTerm, "i"),
					new Criteria("user.mobileNumber").regex(searchTerm, "i"),
					new Criteria("patient.PID").regex(searchTerm, "i"));
		}
		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
		}
		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			criteria = criteria.and("locationId").is(new ObjectId(locationId)).and("patient.locationId")
					.is(new ObjectId(locationId));
		}
		if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
			criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId)).and("patient.hospitalId")
					.is(new ObjectId(hospitalId));
		}
		switch (SearchType.valueOf(searchType.toUpperCase())) {
		case DAILY: {
			firstAggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year")
									.append("userId", "$count")).append("day", new BasicDBObject("$first", "$day"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("week", new BasicDBObject("$first", "$week"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("date", new BasicDBObject("$first", "$date"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("count", new BasicDBObject("$sum", 1))));

			secondAggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("week", new BasicDBObject("$first", "$week"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("count", new BasicDBObject("$sum", 1))));

			break;
		}

		case WEEKLY: {

			firstAggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year")
									.append("userId", "$count")).append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("week", new BasicDBObject("$first", "$week"))
											.append("date", new BasicDBObject("$first", "$date"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("count", new BasicDBObject("$sum", 1))));

			secondAggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("week", new BasicDBObject("$first", "$week"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("count", new BasicDBObject("$sum", 1))));

			break;
		}

		case MONTHLY: {
			firstAggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("month", "$month").append("year", "$year").append("userId", "$count"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("count", new BasicDBObject("$sum", 1))));

			secondAggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
							.append("month", new BasicDBObject("$first", "$month"))
							.append("year", new BasicDBObject("$first", "$year"))
							.append("date", new BasicDBObject("$first", "$date"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("count", new BasicDBObject("$sum", 1))));

			break;
		}
		case YEARLY: {

			firstAggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("year", "$year").append("userId", "$count"))
							.append("year", new BasicDBObject("$first", "$year"))
							.append("date", new BasicDBObject("$first", "$date"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("count", new BasicDBObject("$sum", 1))));
			secondAggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("year", "$year"))
							.append("year", new BasicDBObject("$first", "$year"))
							.append("date", new BasicDBObject("$first", "$date"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("count", new BasicDBObject("$sum", 1))));

			break;

		}

		default:
			break;
		}

		Aggregation aggregation = null;

		aggregation = Aggregation
				.newAggregation(Aggregation.lookup("user_cl", "patientId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
						Aggregation.unwind("patient"), Aggregation.match(criteria),
						projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime").extractMonth()
								.as("month").and("createdTime").extractYear().as("year").and("createdTime")
								.extractWeek().as("week"),
						firstAggregationOperation, secondAggregationOperation,
						Aggregation.sort(new Sort(Sort.Direction.ASC, "createdTime")))
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<PatientAnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
				"patient_visit_cl", PatientAnalyticResponse.class);
		return aggregationResults.getMappedResults();

	}

	@Override
	public List<PatientAnalyticData> getPatientData(int page, int size, String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchTerm, String city) {
		List<PatientAnalyticData> response = null;

		try {
			Criteria criteria = new Criteria();

			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(fromDate));
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(Long.parseLong(toDate));
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date();
				to = new Date();
			}
			fromTime = DPDoctorUtils.getStartTime(from);
			toTime = DPDoctorUtils.getEndTime(to);

			switch (PatientAnalyticType.valueOf(queryType.toUpperCase())) {
			case NEW_PATIENT: {

				response = getPatientDetail(page, size, fromTime, toTime, criteria, null, doctorId, locationId,
						hospitalId, searchTerm);
				break;
			}

			case CITY_WISE: {
				response = getPatientDetail(page, size, fromTime, toTime, criteria, city, doctorId, locationId,
						hospitalId, searchTerm);
				break;
			}

			case VISITED_PATIENT: {
				response = getVisitedPatientData(page, size, fromTime, toTime, criteria, doctorId, locationId,
						hospitalId, searchTerm);
				break;
			}

			default:
				break;

			}

		} catch (

		Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting patient analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting patient analytic");
		}

		return response;

	}

	private List<PatientAnalyticData> getPatientDetail(int page, int size, DateTime fromTime, DateTime toTime,
			Criteria criteria, String city, String doctorId, String locationId, String hospitalId, String searchTerm) {

		if (!DPDoctorUtils.anyStringEmpty(city)) {
			criteria.and("address.city").is(city);
		}
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			criteria.orOperator(new Criteria("localPatientName").regex(searchTerm, "i"),
					new Criteria("user.firstName").regex(searchTerm, "i"),
					new Criteria("user.mobileNumber").regex(searchTerm, "i"),
					new Criteria("PID").regex(searchTerm, "i"));
		}
		ProjectionOperation projectList = new ProjectionOperation(
				Fields.from(Fields.field("id", "$userId"), Fields.field("mobileNumber", "$user.mobileNumber"),
						Fields.field("localPatientName", "$localPatientName"),
						Fields.field("firstName", "$user.firstName"), Fields.field("PID", "$PID"),
						Fields.field("registrationDate", "$registrationDate"), Fields.field("address", "$address"),
						Fields.field("gender", "$gender"), Fields.field("createdTime", "$createdTime")));
		if (toTime != null && fromTime != null) {

			criteria.and("createdTime").gte(new Date(fromTime.getMillis())).lte(new Date(toTime.getMillis()));
		} else if (toTime != null) {
			criteria.and("createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria.and("createdTime").gte(fromTime);
		}
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			criteria.and("address.city").is(searchTerm);
		}

		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			criteria = criteria.and("locationId").is(new ObjectId(locationId));
		}
		if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
			criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
		}

		Aggregation aggregation = null;
		if (size > 0)
			aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "userId", "_id", "user"),
							Aggregation.unwind("user"), projectList,
							Aggregation.sort(new Sort(Sort.Direction.ASC, "createdTime")),
							Aggregation.skip((page) * size), Aggregation.limit(size))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
		else
			aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "userId", "_id", "user"),
							Aggregation.unwind("user"), projectList,
							Aggregation.sort(new Sort(Sort.Direction.ASC, "createdTime")))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<PatientAnalyticData> aggregationResults = mongoTemplate.aggregate(aggregation, "patient_cl",
				PatientAnalyticData.class);

		return aggregationResults.getMappedResults();

	}

	private List<PatientAnalyticData> getVisitedPatientData(int page, int size, DateTime fromTime, DateTime toTime,
			Criteria criteria, String doctorId, String locationId, String hospitalId, String searchTerm) {
		CustomAggregationOperation aggregationOperation = null;

		if (toTime != null && fromTime != null) {

			criteria.and("createdTime").gte(new Date(fromTime.getMillis())).lte(new Date(toTime.getMillis()));
		} else if (toTime != null) {
			criteria.and("createdTime").lte(new Date(toTime.getMillis()));
		} else if (fromTime != null) {
			criteria.and("createdTime").gte(new Date(fromTime.getMillis()));
		}
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			criteria.orOperator(new Criteria("patient.localPatientName").regex(searchTerm, "i"),
					new Criteria("user.firstName").regex(searchTerm, "i"),
					new Criteria("user.mobileNumber").regex(searchTerm, "i"),
					new Criteria("patient.PID").regex(searchTerm, "i"));
		}
		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
		}
		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			criteria = criteria.and("locationId").is(new ObjectId(locationId)).and("patient.locationId")
					.is(new ObjectId(locationId));
		}
		if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
			criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId)).and("patient.hospitalId")
					.is(new ObjectId(hospitalId));
		}
		ProjectionOperation projectList = new ProjectionOperation(
				Fields.from(Fields.field("id", "$patient.userId"), Fields.field("mobileNumber", "$user.mobileNumber"),
						Fields.field("localPatientName", "$patient.localPatientName"),
						Fields.field("firstName", "$user.firstName"), Fields.field("PID", "$patient.PID"),
						Fields.field("registrationDate", "$patient.registrationDate"),
						Fields.field("address", "$patient.address"), Fields.field("gender", "$patient.gender"),
						Fields.field("createdTime", "$patient.createdTime"),
						Fields.field("visitedTime", "$visitedTime"), Fields.field("count", "$patient,userId")));

		aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
				new BasicDBObject("_id", "$id").append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
						.append("localPatientName", new BasicDBObject("$first", "$localPatientName"))
						.append("firstName", new BasicDBObject("$first", "$firstName"))
						.append("PID", new BasicDBObject("$first", "$PID"))
						.append("registrationDate", new BasicDBObject("$first", "$registrationDate"))
						.append("address", new BasicDBObject("$first", "$address"))
						.append("gender", new BasicDBObject("$first", "$gender"))
						.append("createdTime", new BasicDBObject("$first", "$createdTime"))
						.append("visitedTime", new BasicDBObject("$push", "$visitedTime"))
						.append("count", new BasicDBObject("$sum", 1))));

		Aggregation aggregation = null;
		if (size > 0)
			aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "patientId", "_id", "user"),
					Aggregation.unwind("user"), Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
					Aggregation.unwind("patient"), Aggregation.match(criteria), projectList, aggregationOperation,
					Aggregation.sort(new Sort(Sort.Direction.ASC, "count")), Aggregation.skip((page) * size),
					Aggregation.limit(size))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
		else
			aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "patientId", "_id", "user"),
					Aggregation.unwind("user"), Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
					Aggregation.unwind("patient"), Aggregation.match(criteria), projectList, aggregationOperation,
					Aggregation.sort(new Sort(Sort.Direction.ASC, "count")))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<PatientAnalyticData> aggregationResults = mongoTemplate.aggregate(aggregation,
				"patient_visit_cl", PatientAnalyticData.class);
		return aggregationResults.getMappedResults();

	}

	@Override
	public Integer getPatientCount(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String queryType, String searchTerm, String city) {
		Integer response = 0;

		try {
			Criteria criteria = new Criteria();

			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(fromDate));
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(Long.parseLong(toDate));
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date();
				to = new Date();
			}
			fromTime = DPDoctorUtils.getStartTime(from);
			toTime = DPDoctorUtils.getEndTime(to);

			switch (PatientAnalyticType.valueOf(queryType.toUpperCase())) {
			case NEW_PATIENT: {

				response = getPatientDetailCount(fromTime, toTime, criteria, city, doctorId, locationId, hospitalId,
						searchTerm);
				break;
			}

			case CITY_WISE: {
				response = getPatientDetailCount(fromTime, toTime, criteria, city, doctorId, locationId, hospitalId,
						searchTerm);
				break;
			}

			case VISITED_PATIENT: {
				response = getVisitedPatientCount(fromTime, toTime, criteria, doctorId, locationId, hospitalId,
						searchTerm);
				break;
			}

			default:
				break;

			}

		} catch (

		Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting patient analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting patient analytic");
		}

		return response;

	}

	private Integer getPatientDetailCount(DateTime fromTime, DateTime toTime, Criteria criteria, String city,
			String doctorId, String locationId, String hospitalId, String searchTerm) {

		if (!DPDoctorUtils.anyStringEmpty(city)) {
			criteria.and("address.city").is(city);
		}

		if (toTime != null && fromTime != null) {

			criteria.and("createdTime").gte(new Date(fromTime.getMillis())).lte(new Date(toTime.getMillis()));
		} else if (toTime != null) {
			criteria.and("createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria.and("createdTime").gte(fromTime);
		}
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			criteria.orOperator(new Criteria("localPatientName").regex(searchTerm, "i"),
					new Criteria("user.firstName").regex(searchTerm, "i"),
					new Criteria("user.mobileNumber").regex(searchTerm, "i"),
					new Criteria("PID").regex(searchTerm, "i"));
		}

		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			criteria = criteria.and("locationId").is(new ObjectId(locationId));
		}
		if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
			criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
		}

		Aggregation aggregation = null;

		aggregation = Aggregation
				.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "userId", "_id", "user"),
						Aggregation.unwind("user"))
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<PatientAnalyticData> aggregationResults = mongoTemplate.aggregate(aggregation, "patient_cl",
				PatientAnalyticData.class);

		return aggregationResults.getMappedResults().size();

	}

	private Integer getVisitedPatientCount(DateTime fromTime, DateTime toTime, Criteria criteria, String doctorId,
			String locationId, String hospitalId, String searchTerm) {
		CustomAggregationOperation aggregationOperation = null;

		if (toTime != null && fromTime != null) {

			criteria.and("createdTime").gte(new Date(fromTime.getMillis())).lte(new Date(toTime.getMillis()));
		} else if (toTime != null) {
			criteria.and("createdTime").lte(new Date(toTime.getMillis()));
		} else if (fromTime != null) {
			criteria.and("createdTime").gte(new Date(fromTime.getMillis()));
		}
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			criteria.orOperator(new Criteria("patient.localPatientName").regex(searchTerm, "i"),
					new Criteria("user.firstName").regex(searchTerm, "i"),
					new Criteria("user.mobileNumber").regex(searchTerm, "i"),
					new Criteria("patient.PID").regex(searchTerm, "i"));
		}
		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
		}
		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			criteria = criteria.and("locationId").is(new ObjectId(locationId)).and("patient.locationId")
					.is(new ObjectId(locationId));
		}
		if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
			criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId)).and("patient.hospitalId")
					.is(new ObjectId(hospitalId));
		}
		ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$patient.userId")));

		aggregationOperation = new CustomAggregationOperation(
				new BasicDBObject("$group", new BasicDBObject("_id", "$id")));

		Aggregation aggregation = Aggregation
				.newAggregation(Aggregation.lookup("user_cl", "patientId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
						Aggregation.unwind("patient"), Aggregation.match(criteria), projectList, aggregationOperation)
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<PatientAnalyticData> aggregationResults = mongoTemplate.aggregate(aggregation,
				"patient_visit_cl", PatientAnalyticData.class);
		return aggregationResults.getMappedResults().size();

	}
}
