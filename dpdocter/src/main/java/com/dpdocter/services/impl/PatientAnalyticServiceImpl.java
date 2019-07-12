package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.PatientAnalyticData;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.enums.PatientAnalyticType;
import com.dpdocter.enums.SearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.AnalyticCountResponse;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.DoctorPatientAnalyticResponse;
import com.dpdocter.services.PatientAnalyticService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class PatientAnalyticServiceImpl implements PatientAnalyticService {

	@Autowired
	private MongoTemplate mongoTemplate;

	Logger logger = Logger.getLogger(PatientAnalyticServiceImpl.class);

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
	public List<AnalyticResponse> getPatientAnalytic(String doctorId, String locationId, String hospitalId,
			String groupId, String fromDate, String toDate, String queryType, String searchType, String searchTerm) {
		List<AnalyticResponse> response = null;

		try {
			Criteria criteria = new Criteria();
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
			switch (PatientAnalyticType.valueOf(queryType.toUpperCase())) {
			case NEW_PATIENT: {

				response = getNewPatientAnalyticData(from, to, criteria, searchType, doctorId, locationId, hospitalId,
						searchTerm);
				break;
			}

			case CITY_WISE: {
				response = getPatientCountCitiWise(from, to, criteria, searchType, doctorId, locationId, hospitalId,
						searchTerm);
				break;
			}

			case IN_GROUP: {
				if (DPDoctorUtils.allStringsEmpty(groupId)) {
					throw new BusinessException(ServiceError.InvalidInput, "groupId should not be empty");
				}
				response = getPatientCountGroupWise(from, to, criteria, searchType, doctorId, locationId, hospitalId,
						groupId, searchTerm);
				break;
			}

			case VISITED_PATIENT: {
				response = getVisitedPatientcount(from, to, criteria, searchType, doctorId, locationId, hospitalId,
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

	private List<AnalyticResponse> getNewPatientAnalyticData(Date fromTime, Date toTime, Criteria criteria,
			String searchType, String doctorId, String locationId, String hospitalId, String searchTerm) {
		Criteria secondCriteria = new Criteria();
		CustomAggregationOperation aggregationOperation = null;
		ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("count", "$userId"),
				Fields.field("date", "$createdTime"), Fields.field("createdTime", "$createdTime")));
		if (toTime != null && fromTime != null) {
			criteria.and("createdTime").gte(fromTime).lte(toTime);
		} else if (toTime != null) {
			criteria.and("createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria.and("createdTime").gte(fromTime);
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

		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			secondCriteria.orOperator(new Criteria("localPatientName").regex(searchTerm, "i"),
					new Criteria("user.firstName").regex(searchTerm, "i"),
					new Criteria("user.mobileNumber").regex(searchTerm, "i"),
					new Criteria("PID").regex(searchTerm, "i"));
		}
		switch (SearchType.valueOf(searchType.toUpperCase())) {
		case DAILY: {

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
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

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
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

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
							.append("month", new BasicDBObject("$first", "$month"))
							.append("year", new BasicDBObject("$first", "$year"))
							.append("date", new BasicDBObject("$first", "$date"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("count", new BasicDBObject("$sum", 1))));

			break;
		}
		case YEARLY: {

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
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
						aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "date")))
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<AnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation, "patient_cl",
				AnalyticResponse.class);

		return aggregationResults.getMappedResults();

	}

	private List<AnalyticResponse> getPatientCountCitiWise(Date fromTime, Date toTime, Criteria criteria,
			String searchType, String doctorId, String locationId, String hospitalId, String searchTerm) {
		CustomAggregationOperation aggregationOperation = null;

		ProjectionOperation projectList = new ProjectionOperation(
				Fields.from(Fields.field("date", "$createdTime"), Fields.field("createdTime", "$createdTime"),
						Fields.field("city", "$address.city"), Fields.field("count", "$userId")));
		if (toTime != null && fromTime != null) {
			criteria.and("createdTime").gte(fromTime).lte(toTime);
		} else if (toTime != null) {
			criteria.and("createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria.and("createdTime").gte(fromTime);
		}
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

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
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

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
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

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
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

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
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

		AggregationResults<AnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation, "patient_cl",
				AnalyticResponse.class);

		return aggregationResults.getMappedResults();

	}

	private List<AnalyticResponse> getPatientCountGroupWise(Date fromTime, Date toTime, Criteria criteria,
			String searchType, String doctorId, String locationId, String hospitalId, String groupId,
			String searchTerm) {
		CustomAggregationOperation aggregationOperation = null;
		Criteria criteria2 = new Criteria();
		ProjectionOperation projectList = new ProjectionOperation(
				Fields.from(Fields.field("date", "$patientGroup.createdTime"), Fields.field("city", "$address.city"),
						Fields.field("groupName", "$group.name"), Fields.field("count", "$userId")));
		if (toTime != null && fromTime != null) {

			criteria2.and("patientGroup.createdTime").gte(fromTime).lte(toTime);
		} else if (toTime != null) {
			criteria2.and("createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria2.and("createdTime").gte(fromTime);
		}
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			criteria.and("address.city").is(searchTerm);
		}
		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			criteria2 = criteria2.and("group.doctorId").is(new ObjectId(doctorId));

		}
		if (!DPDoctorUtils.anyStringEmpty(groupId)) {
			criteria2 = criteria2.and("patientGroup.groupId").is(new ObjectId(groupId));
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
			}
		}

		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			criteria = criteria.and("locationId").is(new ObjectId(locationId));
			criteria2 = criteria2.and("group.locationId").is(new ObjectId(locationId));

		}
		if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
			criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
			criteria2 = criteria2.and("group.hospitalId").is(new ObjectId(hospitalId));
		}
		criteria2.and("group.discarded").is(false);
		switch (SearchType.valueOf(searchType.toUpperCase())) {
		case DAILY: {

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year")
									.append("city", "$city")).append("day", new BasicDBObject("$first", "$day"))
											.append("city", new BasicDBObject("$first", "$city"))
											.append("week", new BasicDBObject("$first", "$week"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("groupName", new BasicDBObject("$first", "$groupName"))
											.append("date", new BasicDBObject("$first", "$date"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("count", new BasicDBObject("$sum", 1))));

			break;
		}
		case WEEKLY: {

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id",
							new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year")
									.append("city", "$city")).append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("week", new BasicDBObject("$first", "$week"))
											.append("city", new BasicDBObject("$first", "$city"))
											.append("date", new BasicDBObject("$first", "$date"))
											.append("groupName", new BasicDBObject("$first", "$groupName"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("count", new BasicDBObject("$sum", 1))));

			break;
		}

		case MONTHLY: {

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id",
							new BasicDBObject("month", "$month").append("year", "$year").append("city", "$city"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("city", new BasicDBObject("$first", "$city"))
									.append("date", new BasicDBObject("$first", "$date"))
									.append("groupName", new BasicDBObject("$first", "$groupName"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("count", new BasicDBObject("$sum", 1))));

			break;
		}
		case YEARLY: {

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", new BasicDBObject("year", "$year").append("city", "$city"))
							.append("year", new BasicDBObject("$first", "$year"))
							.append("city", new BasicDBObject("$first", "$city"))
							.append("date", new BasicDBObject("$first", "$date"))
							.append("groupName", new BasicDBObject("$first", "$groupName"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("count", new BasicDBObject("$sum", 1))));

			break;

		}
		default:
			break;
		}
		Aggregation aggregation = null;

		aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
				Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
				Aggregation.lookup("patient_group_cl", "userId", "patientId", "patientGroup"),
				Aggregation.unwind("patientGroup"),
				Aggregation.lookup("group_cl", "patientGroup.groupId", "_id", "group"), Aggregation.unwind("group"),
				Aggregation.match(criteria2),
				projectList.and("patientGroup.createdTime").extractDayOfMonth().as("day")
						.and("patientGroup.createdTime").extractMonth().as("month").and("patientGroup.createdTime")
						.extractYear().as("year").and("patientGroup.createdTime").extractWeek().as("week"),
				aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "date")))
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<AnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation, "patient_cl",
				AnalyticResponse.class);

		return aggregationResults.getMappedResults();

	}

	private List<AnalyticResponse> getVisitedPatientcount(Date fromTime, Date toTime, Criteria criteria,
			String searchType, String doctorId, String locationId, String hospitalId, String searchTerm) {
		CustomAggregationOperation firstAggregationOperation = null;
		ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("count", "$patient.userId"),
				Fields.field("date", "$createdTime"), Fields.field("createdTime", "$createdTime")));
		if (toTime != null && fromTime != null) {

			criteria.and("createdTime").gte(fromTime).lte(toTime);
		} else if (toTime != null) {
			criteria.and("createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria.and("createdTime").gte(fromTime);
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
		criteria.and("discarded").is(false);
		switch (SearchType.valueOf(searchType.toUpperCase())) {
		case DAILY: {
			firstAggregationOperation = new CustomAggregationOperation(new Document("$group",
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

			firstAggregationOperation = new CustomAggregationOperation(new Document("$group",
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
			firstAggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
							.append("month", new BasicDBObject("$first", "$month"))
							.append("year", new BasicDBObject("$first", "$year"))
							.append("date", new BasicDBObject("$first", "$date"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("count", new BasicDBObject("$sum", 1))));

			break;
		}
		case YEARLY: {

			firstAggregationOperation = new CustomAggregationOperation(new Document("$group",
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
						firstAggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "date")))
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<AnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
				"patient_visit_cl", AnalyticResponse.class);
		return aggregationResults.getMappedResults();

	}

	@Override
	public List<PatientAnalyticData> getPatientData(int page, int size, String doctorId, String locationId,
			String hospitalId, String groupId, String fromDate, String toDate, String queryType, String searchTerm,
			String city) {
		List<PatientAnalyticData> response = null;

		try {
			Criteria criteria = new Criteria();
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

			switch (PatientAnalyticType.valueOf(queryType.toUpperCase())) {
			case NEW_PATIENT: {

				response = getPatientDetail(page, size, from, to, criteria, null, doctorId, locationId, hospitalId,
						searchTerm);
				break;
			}

			case CITY_WISE: {
				response = getPatientDetail(page, size, from, to, criteria, city, doctorId, locationId, hospitalId,
						searchTerm);
				break;
			}

			case IN_GROUP: {
				if (DPDoctorUtils.allStringsEmpty(groupId)) {
					throw new BusinessException(ServiceError.InvalidInput, "groupId should not be empty");
				}
				response = getGroupPatientDetail(page, size, from, to, criteria, city, doctorId, locationId, hospitalId,
						groupId, searchTerm);
				break;
			}

			case VISITED_PATIENT: {
				response = getVisitedPatientData(page, size, from, to, criteria, doctorId, locationId, hospitalId,
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

	private List<PatientAnalyticData> getPatientDetail(int page, int size, Date fromTime, Date toTime,
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
		ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$userId"),
				Fields.field("mobileNumber", "$user.mobileNumber"),
				Fields.field("localPatientName", "$localPatientName"), Fields.field("firstName", "$user.firstName"),
				Fields.field("PID", "$PID"), Fields.field("dob", "$dob"),
				Fields.field("registrationDate", "$registrationDate"), Fields.field("address", "$address"),
				Fields.field("gender", "$gender"), Fields.field("createdTime", "$createdTime")));
		if (toTime != null && fromTime != null) {

			criteria.and("createdTime").gte(fromTime).lte(toTime);
		} else if (toTime != null) {
			criteria.and("createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria.and("createdTime").gte(fromTime);
		}
		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
		}
		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			criteria = criteria.and("locationId").is(new ObjectId(locationId));
		}
		if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
			criteria.and("hospitalId").is(new ObjectId(hospitalId));
		}

		Aggregation aggregation = null;
		if (size > 0)
			aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "userId", "_id", "user"),
							Aggregation.unwind("user"), projectList,
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
							Aggregation.skip((long)(page) * size), Aggregation.limit(size))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
		else
			aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "userId", "_id", "user"),
							Aggregation.unwind("user"), projectList,
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<PatientAnalyticData> aggregationResults = mongoTemplate.aggregate(aggregation, "patient_cl",
				PatientAnalyticData.class);

		return aggregationResults.getMappedResults();

	}

	private List<PatientAnalyticData> getGroupPatientDetail(int page, int size, Date fromTime, Date toTime,
			Criteria criteria, String city, String doctorId, String locationId, String hospitalId, String groupId,
			String searchTerm) {

		if (!DPDoctorUtils.anyStringEmpty(city)) {
			criteria.and("address.city").is(city);
		}
		Criteria criteria2 = new Criteria();
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			criteria.orOperator(new Criteria("localPatientName").regex(searchTerm, "i"),
					new Criteria("user.firstName").regex(searchTerm, "i"),
					new Criteria("user.mobileNumber").regex(searchTerm, "i"),
					new Criteria("PID").regex(searchTerm, "i"));
		}
		ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$userId"),
				Fields.field("mobileNumber", "$user.mobileNumber"),
				Fields.field("localPatientName", "$localPatientName"), Fields.field("firstName", "$user.firstName"),
				Fields.field("PID", "$PID"), Fields.field("dob", "$dob"),
				Fields.field("registrationDate", "$registrationDate"), Fields.field("address", "$address"),
				Fields.field("gender", "$gender"), Fields.field("createdTime", "$patientGroup.createdTime")));
		if (toTime != null && fromTime != null) {
			criteria.and("patientGroup.createdTime").gte(fromTime).lte(toTime);
		} else if (toTime != null) {
			criteria.and("patientGroup.createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria.and("patientGroup.createdTime").gte(fromTime);
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
		if (!DPDoctorUtils.anyStringEmpty(groupId)) {
			criteria2 = criteria2.and("patientGroup.groupId").is(new ObjectId(groupId));
		}

		criteria2 = criteria2.and("patientGroup.discarded").is(false);

		Aggregation aggregation = null;
		if (size > 0)
			aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "userId", "_id", "user"),
							Aggregation.unwind("user"),
							Aggregation.lookup("patient_group_cl", "userId", "patientId", "patientGroup"),
							Aggregation.unwind("patientGroup"), Aggregation.match(criteria2), projectList,
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
							Aggregation.skip((long)(page) * size), Aggregation.limit(size))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
		else
			aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "userId", "_id", "user"),
							Aggregation.unwind("user"),
							Aggregation.lookup("patient_group_cl", "userId", "patientId", "patientGroup"),
							Aggregation.unwind("patientGroup"), Aggregation.match(criteria2), projectList,
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<PatientAnalyticData> aggregationResults = mongoTemplate.aggregate(aggregation, "patient_cl",
				PatientAnalyticData.class);

		return aggregationResults.getMappedResults();

	}

	private List<PatientAnalyticData> getVisitedPatientData(int page, int size, Date fromTime, Date toTime,
			Criteria criteria, String doctorId, String locationId, String hospitalId, String searchTerm) {
		CustomAggregationOperation aggregationOperation = null;

		if (toTime != null && fromTime != null) {

			criteria.and("createdTime").gte(fromTime).lte(toTime);
		} else if (toTime != null) {
			criteria.and("createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria.and("createdTime").gte(fromTime);
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
		criteria.and("discarded").is(false);
		ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$patient.userId"),
				Fields.field("mobileNumber", "$user.mobileNumber"),
				Fields.field("localPatientName", "$patient.localPatientName"),
				Fields.field("firstName", "$user.firstName"), Fields.field("PID", "$patient.PID"),
				Fields.field("dob", "$patient.dob"), Fields.field("registrationDate", "$patient.registrationDate"),
				Fields.field("address", "$patient.address"), Fields.field("gender", "$patient.gender"),
				Fields.field("createdTime", "$patient.createdTime"), Fields.field("visitedTime", "$visitedTime"),
				Fields.field("count", "$patient.userId")));

		aggregationOperation = new CustomAggregationOperation(new Document("$group",
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
					Aggregation.sort(new Sort(Sort.Direction.ASC, "count")), Aggregation.skip((long)(page) * size),
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
	public Integer getPatientCount(String doctorId, String locationId, String hospitalId, String groupId,
			String fromDate, String toDate, String queryType, String searchTerm, String city) {
		Integer response = 0;

		try {
			Criteria criteria = new Criteria();
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

			switch (PatientAnalyticType.valueOf(queryType.toUpperCase())) {
			case NEW_PATIENT: {

				response = getPatientDetailCount(from, to, criteria, null, doctorId, locationId, hospitalId,
						searchTerm);
				break;
			}

			case CITY_WISE: {
				response = getPatientDetailCount(from, to, criteria, searchTerm, doctorId, locationId, hospitalId,
						searchTerm);
				break;
			}

			case IN_GROUP: {
				if (DPDoctorUtils.allStringsEmpty(groupId)) {
					throw new BusinessException(ServiceError.InvalidInput, "groupId should not be empty");
				}
				response = getGroupPatientDetailCount(from, to, criteria, searchTerm, doctorId, locationId, hospitalId,
						groupId, searchTerm);
				break;
			}

			case VISITED_PATIENT: {
				response = getVisitedPatientCount(from, to, criteria, doctorId, locationId, hospitalId, searchTerm);
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

	private Integer getPatientDetailCount(Date fromTime, Date toTime, Criteria criteria, String city, String doctorId,
			String locationId, String hospitalId, String searchTerm) {

		if (!DPDoctorUtils.anyStringEmpty(city)) {
			criteria.and("address.city").is(city);
		}

		if (toTime != null && fromTime != null) {
			criteria.and("createdTime").gte(fromTime).lte(toTime);
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

		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
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

	private Integer getGroupPatientDetailCount(Date fromTime, Date toTime, Criteria criteria, String city,
			String doctorId, String locationId, String hospitalId, String groupId, String searchTerm) {
		Criteria criteria2 = new Criteria();
		if (!DPDoctorUtils.anyStringEmpty(city)) {
			criteria.and("address.city").is(city);
		}

		if (toTime != null && fromTime != null) {
			criteria.and("patientGroup.createdTime").gte(fromTime).lte(toTime);
		} else if (toTime != null) {
			criteria.and("patientGroup.createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria.and("patientGroup.createdTime").gte(fromTime);
		}
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			criteria.orOperator(new Criteria("localPatientName").regex(searchTerm, "i"),
					new Criteria("user.firstName").regex(searchTerm, "i"),
					new Criteria("user.mobileNumber").regex(searchTerm, "i"),
					new Criteria("PID").regex(searchTerm, "i"));
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
		if (!DPDoctorUtils.anyStringEmpty(groupId)) {
			criteria2 = criteria2.and("patientGroup.groupId").is(new ObjectId(groupId));
		}
		criteria2.and("patientGroup.discarded").is(false);
		Aggregation aggregation = null;

		aggregation = Aggregation
				.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "userId", "_id", "user"),
						Aggregation.unwind("user"),
						Aggregation.lookup("patient_group_cl", "userId", "patientId", "patientGroup"),
						Aggregation.unwind("patientGroup"), Aggregation.match(criteria2))
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<PatientAnalyticData> aggregationResults = mongoTemplate.aggregate(aggregation, "patient_cl",
				PatientAnalyticData.class);

		return aggregationResults.getMappedResults().size();

	}

	private Integer getVisitedPatientCount(Date fromTime, Date toTime, Criteria criteria, String doctorId,
			String locationId, String hospitalId, String searchTerm) {
		CustomAggregationOperation aggregationOperation = null;

		if (toTime != null && fromTime != null) {

			criteria.and("createdTime").gte(fromTime).lte(toTime);
		} else if (toTime != null) {
			criteria.and("createdTime").lte(toTime);
		} else if (fromTime != null) {
			criteria.and("createdTime").gte(fromTime);
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
				new Document("$group", new BasicDBObject("_id", "$id")));

		Aggregation aggregation = Aggregation
				.newAggregation(Aggregation.lookup("user_cl", "patientId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
						Aggregation.unwind("patient"), Aggregation.match(criteria), projectList, aggregationOperation)
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		AggregationResults<PatientAnalyticData> aggregationResults = mongoTemplate.aggregate(aggregation,
				"patient_visit_cl", PatientAnalyticData.class);
		return aggregationResults.getMappedResults().size();

	}

	@Override
	public DoctorPatientAnalyticResponse getPatientAnalytic(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate) {
		DoctorPatientAnalyticResponse data = new DoctorPatientAnalyticResponse();
		try {
			Criteria criteria = null;
			Date from = null;
			Date to = null;
			Date lastdate = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(0);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(0);
				to = new Date();
			}
			long diff = to.getTime() - from.getTime();
			long diffDays = (diff / (24 * 60 * 60 * 1000));
			lastdate = new Date(from.getTime() - ((diffDays + 1) * (24 * 60 * 60 * 1000)));
			criteria = getCriteria(doctorId, locationId, hospitalId);
			data.setTotalPatient((int) mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							Aggregation.sort(Direction.DESC, "createdTime")),
					PatientCollection.class, PatientCollection.class).getMappedResults().size());

			criteria = getCriteria(doctorId, locationId, hospitalId).and("createdTime").gte(from).lte(to);
			data.setTotalNewPatient((int) mongoTemplate.count(new Query(criteria), PatientCollection.class));

			// hike in patient
			int total = 0;

			if (data.getTotalNewPatient() > 0) {
				// hike in patient

				criteria = getCriteria(doctorId, locationId, hospitalId).and("createdTime").gte(lastdate).lte(from);
				total = (int) mongoTemplate.count(new Query(criteria), PatientCollection.class);
				data.setChangeInTotalPatientInPercent(total);

			}
			// visited patient
			criteria = getCriteria(null, locationId, hospitalId).and("visit.adminCreatedTime").gte(from).lte(to)
					.and("visit.locationId").is(new ObjectId(locationId)).and("visit.discarded").is(false)
					.and("visit.hospitalId").is(new ObjectId(hospitalId));

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("visit.doctorId").is(new ObjectId(doctorId));
			}

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("patient_visit_cl", "userId", "patientId", "visit"), Aggregation.unwind("visit"),
					Aggregation.match(criteria),
					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id"))));
			total = mongoTemplate.aggregate(aggregation, PatientCollection.class, PatientCollection.class)
					.getMappedResults().size();
			data.setTotalVisitedPatient(total);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting Patient analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting Patient analytic");
		}
		return data;
	}

	private List<AnalyticCountResponse> getPatientCountByCitiWise(int size, int page, Date fromTime, Date toTime,
			String doctorId, String locationId, String hospitalId, String searchTerm, boolean isVisited) {
		Criteria criteria = new Criteria();
		Criteria criteria2 = new Criteria();

		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			criteria.orOperator(new Criteria("address.city").regex(searchTerm, "i"));
		}
		if (!isVisited) {
			if (toTime != null && fromTime != null) {

				criteria.and("createdTime").gte(fromTime).lte(toTime);
			} else if (toTime != null) {
				criteria.and("createdTime").lte(toTime);
			} else if (fromTime != null) {
				criteria.and("createdTime").gte(fromTime);
			}
		} else {

			if (toTime != null && fromTime != null) {

				criteria2.and("visit.createdTime").gte(fromTime).lte(toTime);
			} else if (toTime != null) {
				criteria2.and("visit.createdTime").lte(toTime);
			} else if (fromTime != null) {
				criteria2.and("visit.createdTime").gte(fromTime);
			}

		}
		ProjectionOperation projectList = new ProjectionOperation(
				Fields.from(Fields.field("name", "$address.city"), Fields.field("count", "$userId")));

		CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", "$name").append("name", new BasicDBObject("$first", "$name")).append("count",
						new BasicDBObject("$sum", 1))));

		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			criteria2.and("visit.doctorId").is(new ObjectId(doctorId));

		}
		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			criteria = criteria.and("locationId").is(new ObjectId(locationId));
			criteria2.and("visit.locationId").is(new ObjectId(locationId));

		}
		if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
			criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
			criteria2.and("visit.hospitalId").is(new ObjectId(hospitalId));
		}
		criteria.and("discarded").is(false);
		criteria2.and("visit.discarded").is(false);
		Aggregation aggregation = null;
		if (!isVisited) {
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (size > 0)
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria), projectList, aggregationOperation,
								Aggregation.sort(new Sort(Sort.Direction.ASC, "name")), Aggregation.skip((long)(page) * size),
								Aggregation.limit(size))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
			else
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria), projectList, aggregationOperation,
								Aggregation.sort(new Sort(Sort.Direction.ASC, "name")))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
		} else {
			if (size > 0)
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("patient_visit_cl", "userId", "patientId", "visit"),
								Aggregation.unwind("visit"), Aggregation.match(criteria2),
								new ProjectionOperation(Fields.from(
										Fields.field("name", "$address.city"), Fields.field("count", "$userId"),
										Fields.field("userId", "$userId"))),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id", "$userId")
												.append("name", new BasicDBObject("$first", "$name"))
												.append("count", new BasicDBObject("$first", "$count")))),
								aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "name")),
								Aggregation.skip((long)(page) * size), Aggregation.limit(size))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
			else
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("patient_visit_cl", "userId", "patientId", "visit"),
								Aggregation.unwind("visit"), Aggregation.match(criteria2),
								new ProjectionOperation(Fields.from(
										Fields.field("name", "$address.city"), Fields.field("count", "$userId"),
										Fields.field("userId", "$userId"))),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id", "$userId")
												.append("name", new BasicDBObject("$first", "$name"))
												.append("count", new BasicDBObject("$first", "$count")))),
								aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "name")))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		}
		AggregationResults<AnalyticCountResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
				PatientCollection.class, AnalyticCountResponse.class);
		return aggregationResults.getMappedResults();
	}

	private List<AnalyticCountResponse> getPatientCountByReference(int size, int page, Date fromTime, Date toTime,
			String doctorId, String locationId, String hospitalId, String searchTerm, boolean isVisited) {
		Criteria criteria = new Criteria();
		Criteria criteria2 = new Criteria();
		Criteria criteria3 = new Criteria();

		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			criteria2.orOperator(new Criteria("group.name").regex(searchTerm, "i"));
		}
		if (!isVisited) {
			if (toTime != null && fromTime != null) {
				criteria.and("createdTime").gte(fromTime).lte(toTime);
			} else if (toTime != null) {
				criteria.and("createdTime").lte(toTime);
			} else if (fromTime != null) {
				criteria.and("createdTime").gte(fromTime);
			}
		} else {

			if (toTime != null && fromTime != null) {

				criteria2.and("visit.createdTime").gte(fromTime).lte(toTime);
			} else if (toTime != null) {
				criteria2.and("visit.createdTime").lte(toTime);
			} else if (fromTime != null) {
				criteria2.and("visit.createdTime").gte(fromTime);
			}

		}
		ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$refer.reference"),
				Fields.field("name", "$refer.reference"), Fields.field("count", "$userId")));

		CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", "$name").append("name", new BasicDBObject("$first", "$name")).append("count",
						new BasicDBObject("$sum", 1))));

		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			if (!isVisited) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			criteria2.and("visit.doctorId").is(new ObjectId(doctorId));

		}
		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			criteria = criteria.and("locationId").is(new ObjectId(locationId));
			criteria2.and("visit.locationId").is(new ObjectId(locationId));

		}
		if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
			criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
			criteria2.and("visit.hospitalId").is(new ObjectId(hospitalId));
		}
		criteria.and("discarded").is(false);
		criteria2.and("visit.discarded").is(false);
		Aggregation aggregation = null;
		if (!isVisited) {

			if (size > 0)
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("referrences_cl", "referredBy", "_id", "refer"),
								Aggregation.unwind("refer"), Aggregation.match(criteria3), projectList,
								aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "name")),
								Aggregation.skip((long)(page) * size), Aggregation.limit(size))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
			else
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("referrences_cl", "referredBy", "_id", "refer"),
								Aggregation.unwind("refer"), Aggregation.match(criteria3), projectList,
								aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "name")))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
		} else {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("referrences_cl", "referredBy", "_id", "refer"), Aggregation.unwind("refer"),
						Aggregation.lookup("patient_visit_cl", "userId", "patientId", "visit"),
						Aggregation.unwind("visit"), Aggregation.match(criteria2),

						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$_id").append("refer", new BasicDBObject("$first", "$refer"))
										.append("userId", new BasicDBObject("$first", "$userId")))),

						new ProjectionOperation(Fields.from(Fields.field("id", "$refer.reference"),
								Fields.field("name", "$refer.reference"), Fields.field("count", "$userId"))),

						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$id").append("name", new BasicDBObject("$first", "$name"))
										.append("count", new BasicDBObject("$sum", 1)))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "name")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("referrences_cl", "referredBy", "_id", "refer"), Aggregation.unwind("refer"),
						Aggregation.lookup("patient_visit_cl", "userId", "patientId", "visit"),
						Aggregation.unwind("visit"), Aggregation.match(criteria2),

						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$_id").append("refer", new BasicDBObject("$first", "$refer"))
										.append("userId", new BasicDBObject("$first", "$userId")))),

						new ProjectionOperation(Fields.from(Fields.field("id", "$refer.reference"),
								Fields.field("name", "$refer.reference"), Fields.field("count", "$userId"))),

						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$name").append("name", new BasicDBObject("$first", "$name"))
										.append("count", new BasicDBObject("$sum", 1)))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "name")))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		}
		AggregationResults<AnalyticCountResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
				PatientCollection.class, AnalyticCountResponse.class);
		return aggregationResults.getMappedResults();
	}

	private List<AnalyticCountResponse> getPatientCountByGroup(int size, int page, Date fromTime, Date toTime,
			String doctorId, String locationId, String hospitalId, String searchTerm, boolean isVisited) {
		Criteria criteria = new Criteria();
		Criteria criteria2 = new Criteria();
		Criteria criteria3 = new Criteria();

		if (!isVisited) {
			if (toTime != null && fromTime != null) {

				criteria.and("createdTime").gte(fromTime).lte(toTime);
			} else if (toTime != null) {
				criteria.and("createdTime").lte(toTime);
			} else if (fromTime != null) {
				criteria.and("createdTime").gte(fromTime);
			}
		} else {

			if (toTime != null && fromTime != null) {

				criteria3.and("visit.createdTime").gte(fromTime).lte(toTime);
			} else if (toTime != null) {
				criteria3.and("visit.createdTime").lte(toTime);
			} else if (fromTime != null) {
				criteria3.and("visit.createdTime").gte(fromTime);
			}

		}

		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			criteria2.orOperator(new Criteria("refer.reference").regex(searchTerm, "i"));
		}
		ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$group._id"),
				Fields.field("name", "$group.name"), Fields.field("count", "$userId")));

		CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", "$id").append("name", new BasicDBObject("$first", "$name")).append("count",
						new BasicDBObject("$sum", 1))));

		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			criteria3.and("visit.doctorId").is(new ObjectId(doctorId));
			criteria2.and("group.doctorId").is(new ObjectId(doctorId));

		}
		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			criteria = criteria.and("locationId").is(new ObjectId(locationId));
			criteria3.and("visit.locationId").is(new ObjectId(locationId));
			criteria2.and("group.locationId").is(new ObjectId(locationId));

		}
		if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
			criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
			criteria3.and("visit.hospitalId").is(new ObjectId(hospitalId));
			criteria2.and("group.hospitalId").is(new ObjectId(hospitalId));
		}
		criteria.and("discarded").is(false);
		criteria3.and("visit.discarded").is(false);
		Aggregation aggregation = null;
		if (!isVisited) {
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria2.and("doctorId").is(new ObjectId(doctorId));

			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("patient_group_cl", "userId", "patientId", "patientGroup"),
						Aggregation.unwind("patientGroup"),
						Aggregation.match(new Criteria("patientGroup.discarded").is(false)),
						Aggregation.lookup("group_cl", "patientGroup.groupId", "_id", "group"),
						Aggregation.unwind("group"), Aggregation.match(criteria2.and("group.discarded").is(false)),
						projectList, aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "name")),
						Aggregation.skip((long)(page) * size), Aggregation.limit(size))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("patient_group_cl", "userId", "patientId", "patientGroup"),
						Aggregation.unwind("patientGroup"),
						Aggregation.match(new Criteria("patientGroup.discarded").is(false)),
						Aggregation.lookup("group_cl", "patientGroup.groupId", "_id", "group"),
						Aggregation.unwind("group"), Aggregation.match(criteria2.and("group.discarded").is(false)),
						projectList, aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "name")))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
		} else {
			if (size > 0)
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("patient_group_cl", "userId", "patientId", "patientGroup"),
								Aggregation.unwind("patientGroup"),
								Aggregation.match(new Criteria("patientGroup.discarded").is(false)),
								Aggregation.lookup("group_cl", "patientGroup.groupId", "_id", "group"),
								Aggregation.unwind("group"),
								Aggregation.match(criteria2.and("group.discarded").is(false)),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id", "$patientGroup._id")
												.append("group", new BasicDBObject("$first", "$group"))
												.append("userId", new BasicDBObject("$first", "$userId")))),
								Aggregation.lookup("patient_visit_cl", "userId", "patientId", "visit"),
								Aggregation.unwind("visit"), Aggregation.match(criteria3),
								new ProjectionOperation(
										Fields.from(Fields.field("name", "$group.name"),
												Fields.field("count", "$group._id"), Fields.field("groupId",
														"$group._id"))),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id", "$_id")
												.append("name", new BasicDBObject("$first", "$name"))
												.append("groupId", new BasicDBObject("$first", "$groupId"))
												.append("count", new BasicDBObject("$first", "$count")))),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id", "$groupId")
												.append("name", new BasicDBObject("$first", "$name"))
												.append("count", new BasicDBObject("$sum", 1)))),
								Aggregation.sort(new Sort(Sort.Direction.ASC, "name")), Aggregation.skip((long)(page) * size),
								Aggregation.limit(size))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
			else
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("patient_group_cl", "userId", "patientId", "patientGroup"),
								Aggregation.unwind("patientGroup"),
								Aggregation.match(new Criteria("patientGroup.discarded").is(false)),
								Aggregation.lookup("group_cl", "patientGroup.groupId", "_id", "group"),
								Aggregation.unwind("group"),
								Aggregation.match(criteria2.and("group.discarded").is(false)),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id", "$patientGroup._id")
												.append("group", new BasicDBObject("$first", "$group"))
												.append("userId", new BasicDBObject("$first", "$userId")))),
								Aggregation.lookup("patient_visit_cl", "userId", "patientId", "visit"),
								Aggregation.unwind("visit"), Aggregation.match(criteria3),
								new ProjectionOperation(
										Fields.from(Fields.field("name", "$group.name"),
												Fields.field("count", "$group._id"), Fields.field("groupId",
														"$group._id"))),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id", "$_id")
												.append("name", new BasicDBObject("$first", "$name"))
												.append("groupId", new BasicDBObject("$first", "$groupId"))
												.append("count", new BasicDBObject("$first", "$count")))),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id", "$groupId")
												.append("name", new BasicDBObject("$first", "$name"))
												.append("count", new BasicDBObject("$sum", 1)))),
								Aggregation.sort(new Sort(Sort.Direction.ASC, "name")))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		}
		AggregationResults<AnalyticCountResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
				PatientCollection.class, AnalyticCountResponse.class);
		return aggregationResults.getMappedResults();
	}

	@Override
	public List<AnalyticCountResponse> getPatientCountAnalytic(int size, int page, String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchTerm, boolean isVisited) {
		List<AnalyticCountResponse> response = null;

		try {
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

			if (queryType.equalsIgnoreCase("CITY_WISE")) {

				response = getPatientCountByCitiWise(size, page, from, to, doctorId, locationId, hospitalId, searchTerm,
						isVisited);

			}

			else if (queryType.equalsIgnoreCase("GROUP_WISE")) {

				response = getPatientCountByGroup(size, page, from, to, doctorId, locationId, hospitalId, searchTerm,
						isVisited);

			} else if (queryType.equalsIgnoreCase("REFERRED_BY")) {

				response = getPatientCountByReference(size, page, from, to, doctorId, locationId, hospitalId,
						searchTerm, isVisited);

			}

		} catch (

		Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting patient analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting patient analytic");
		}

		return response;

	}
}
