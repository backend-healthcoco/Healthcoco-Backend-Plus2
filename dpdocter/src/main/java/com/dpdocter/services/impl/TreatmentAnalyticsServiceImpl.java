package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.beans.TretmentAnalyticMongoResponse;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.enums.SearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.DoctorTreatmentAnalyticResponse;
import com.dpdocter.services.TreatmentAnalyticsService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
@Transactional
public class TreatmentAnalyticsServiceImpl implements TreatmentAnalyticsService {

	@Autowired
	private MongoTemplate mongoTemplate;

	Logger logger = Logger.getLogger(TreatmentAnalyticsServiceImpl.class);

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
	public List<AnalyticResponse> getTreatmentAnalyticData(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String searchType, String searchTerm) {
		List<AnalyticResponse> response = null;
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
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(0l);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(0l);
				to = new Date();
			}
			fromTime = new DateTime(from);
			toTime = new DateTime(to);

			CustomAggregationOperation aggregationOperation = null;
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("count", "$patientId"),
					Fields.field("date", "$createdTime"), Fields.field("createdTime", "$createdTime")));

			criteria.and("createdTime").gte(fromTime).lte(toTime);

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
					.newAggregation(Aggregation.match(criteria), 
							
							projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime")
									.extractMonth().as("month").and("createdTime").extractYear().as("year")
									.and("createdTime").extractWeek().as("week"),
							aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.ASC, "date")))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

			AggregationResults<AnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientTreatmentCollection.class, AnalyticResponse.class);
			response = aggregationResults.getMappedResults();

		} catch (

		Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting Treatment Graph analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting Treatment Graph analytic");
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

			criteria = criteria.and("createdTime").gte(fromTime).lte(toTime);
			criteria.and("discarded").is(false);

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria), Aggregation.unwind("$treatments"),
								Aggregation.group("$treatments.treatmentServiceId").count().as("count"),
								Aggregation.lookup("treatment_services_cl", "_id", "_id", "treatmentServices"),
								Aggregation.unwind("$treatmentServices"),
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
								Aggregation.unwind("$treatmentServices"),
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
			logger.error(e + " Error Occurred While getting most Treatments");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting most Treatment ");
		}
		return response;
	}
	
	@Override
	public List<DoctorTreatmentAnalyticResponse> getTreatmentAnalytic(int page, int size, String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String searchTerm) {
		List<DoctorTreatmentAnalyticResponse> response = null;
		try {
			Criteria criteria = null;
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
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
			criteria = getCriteria(doctorId, locationId, hospitalId).and("createdTime").gte(fromTime).lte(toTime)
					.and("discarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.and("totalTreatmentService.name").regex(searchTerm, "i");
			}
			Aggregation aggregation = null;
			if (size > 0) {

				aggregation = Aggregation
						.newAggregation(Aggregation.unwind("treatments"),
								Aggregation.lookup("treatment_services_cl", "treatments.treatmentServiceId", "_id",
										"totalTreatmentService"),

								Aggregation.unwind("totalTreatmentService"), Aggregation.match(criteria),
								new CustomAggregationOperation(
										new BasicDBObject("$group",
												new BasicDBObject("_id", "$treatments.treatmentServiceId")
														.append("treatmentServiceId",
																new BasicDBObject("$first",
																		"$treatments.treatmentServiceId"))
														.append("treatmentServiceName",
																new BasicDBObject("$first",
																		"$totalTreatmentService.name"))
														.append("totalTreatmentServiceNotStarted",
																new BasicDBObject("$push", "$treatments.status"))
														.append("totalTreatmentServiceProgress",
																new BasicDBObject("$push", "$treatments.status"))
														.append("totalTreatmentServiceCompleted",
																new BasicDBObject("$push", "$treatments.status"))
														.append("totalTreatmentService",
																new BasicDBObject("$sum", 1)))),
								Aggregation.sort(new Sort(Sort.Direction.DESC, "totalTreatmentService")),
								Aggregation.skip((page) * size), Aggregation.limit(size));
			} else {
				size = 10;

				aggregation = Aggregation
						.newAggregation(Aggregation.unwind("treatments"),
								Aggregation.lookup("treatment_services_cl", "treatments.treatmentServiceId", "_id",
										"totalTreatmentService"),

								Aggregation.unwind("totalTreatmentService"), Aggregation.match(criteria),
								new CustomAggregationOperation(
										new BasicDBObject("$group",
												new BasicDBObject("_id", "$treatments.treatmentServiceId")
														.append("treatmentServiceId",
																new BasicDBObject("$first",
																		"$treatments.treatmentServiceId"))
														.append("treatmentServiceName",
																new BasicDBObject("$first",
																		"$totalTreatmentService.name"))
														.append("totalTreatmentServiceNotStarted",
																new BasicDBObject("$push", "$treatments.status"))
														.append("totalTreatmentServiceProgress",
																new BasicDBObject("$push", "$treatments.status"))
														.append("totalTreatmentServiceCompleted",
																new BasicDBObject("$push", "$treatments.status"))
														.append("totalTreatmentService",
																new BasicDBObject("$sum", 1)))),
								Aggregation.sort(new Sort(Sort.Direction.DESC, "totalTreatmentService")),
								Aggregation.skip((page) * size), Aggregation.limit(size));

			}
			AggregationResults<TretmentAnalyticMongoResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientTreatmentCollection.class, TretmentAnalyticMongoResponse.class);
			List<TretmentAnalyticMongoResponse> analyticMongoResponses = aggregationResults.getMappedResults();
			if (analyticMongoResponses != null && !analyticMongoResponses.isEmpty()) {
				response = new ArrayList<DoctorTreatmentAnalyticResponse>();
				DoctorTreatmentAnalyticResponse analyticResponse = new DoctorTreatmentAnalyticResponse();
				for (TretmentAnalyticMongoResponse data : analyticMongoResponses) {
					analyticResponse.setTotalTreatmentService(data.getTotalTreatmentService());
					analyticResponse.setTreatmentServiceName(data.getTreatmentServiceName());
					if (data.getTotalTreatmentServiceCompleted() != null
							&& !data.getTotalTreatmentServiceCompleted().isEmpty()) {

						for (String str : data.getTotalTreatmentServiceCompleted()) {
							int i = 0;
							if (str.equals("COMPLETED")) {
								i++;

							}
							analyticResponse.setTotalTreatmentServiceCompleted(i);
						}
						if (data.getTotalTreatmentServiceProgress() != null
								&& !data.getTotalTreatmentServiceProgress().isEmpty()) {
							for (String str : data.getTotalTreatmentServiceProgress()) {
								int i = 0;
								if (str.equals("IN_PROGRESS")) {
									i++;

								}
								analyticResponse.setTotalTreatmentServiceProgress(i);
							}
						}
						if (data.getTotalTreatmentServiceNotStarted() != null
								&& !data.getTotalTreatmentServiceNotStarted().isEmpty()) {
							for (String str : data.getTotalTreatmentServiceNotStarted()) {
								int i = 0;
								if (str.equals("NOT_STARTED")) {
									i++;

								}
								analyticResponse.setTotalTreatmentServiceNotStarted(i);
							}

						}
					}
				}
				response.add(analyticResponse);
			}
		} catch (Exception e) {

			e.printStackTrace();
			logger.error(e + " Error Occurred While getting Treatment analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting Treatment analytic");
		}

		return response;
	}

}
