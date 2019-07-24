package com.dpdocter.services.impl;

import java.util.ArrayList;
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
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.TreatmentAnalyticDetail;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.enums.SearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.DoctorAnalyticPieChartResponse;
import com.dpdocter.response.DoctorTreatmentAnalyticResponse;
import com.dpdocter.response.TreatmentServiceAnalyticResponse;
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
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("count", "$patientId"), Fields.field("createdTime", "$createdTime")));

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
			if (!DPDoctorUtils.anyStringEmpty(searchType))
				switch (SearchType.valueOf(searchType.toUpperCase())) {
				case DAILY: {

					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
											.append("day", new BasicDBObject("$first", "$day"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("week", new BasicDBObject("$first", "$week"))
											.append("date", new BasicDBObject("$first", "$createdTime"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
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
													.append("date", new BasicDBObject("$first", "$createdTime"))
													.append("createdTime", new BasicDBObject("$first", "$createdTime"))
													.append("count", new BasicDBObject("$sum", 1))));

					break;
				}

				case MONTHLY: {

					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$createdTime"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("count", new BasicDBObject("$sum", 1))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$createdTime"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("count", new BasicDBObject("$sum", 1))));

					break;

				}
				default:
					break;
				}

			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.unwind("treatments"),
							Aggregation.lookup(
									"treatment_services_cl", "$treatments.treatmentServiceId", "_id", "treatments"),
							Aggregation.unwind("treatments"),

							new CustomAggregationOperation(new Document("$group",
									new BasicDBObject("_id", "$_id")
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("patientId", new BasicDBObject("$first", "$patientId")))),

							projectList.and("createdTime").as("date").and("createdTime").extractDayOfMonth().as("day")
									.and("createdTime").extractMonth().as("month").and("createdTime").extractYear()
									.as("year").and("createdTime").extractWeek().as("week"),
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
	public List<TreatmentService> getTreatmentServiceAnalytics(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, int page, int size, String searchTerm) {
		List<TreatmentService> response = null;
		try {
			Criteria criteria = new Criteria();
			Criteria criteriaSecond = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteriaSecond.and("treatmentServices.name").regex(searchTerm, "i");
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
						.newAggregation(Aggregation.match(criteria), Aggregation.unwind("treatments"),
								Aggregation.group("$treatments.treatmentServiceId").count().as("count"),
								Aggregation.lookup("treatment_services_cl", "_id", "_id", "treatmentServices"),
								Aggregation.unwind("treatmentServices"), Aggregation.match(criteriaSecond),
								new CustomAggregationOperation(
										new Document("$group",
												new BasicDBObject("_id", "$_id")
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
						.newAggregation(Aggregation.match(criteria), Aggregation.unwind("treatments"),
								Aggregation.group("$treatments.treatmentServiceId").count().as("count"),
								Aggregation.lookup("treatment_services_cl", "_id", "_id", "treatmentServices"),
								Aggregation.unwind("treatmentServices"), Aggregation.match(criteriaSecond),
								new CustomAggregationOperation(
										new Document("$group",
												new BasicDBObject("_id", "$_id")
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
	public List<DoctorTreatmentAnalyticResponse> getTreatmentServiceAnalyticWithStatus(int page, int size,
			String doctorId, String locationId, String hospitalId, String fromDate, String toDate, String searchTerm) {
		List<DoctorTreatmentAnalyticResponse> response = null;
		try {
			Criteria criteria = null;
			Criteria criteriaSecond = new Criteria();
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
				criteriaSecond.and("totalTreatmentService.name").regex(searchTerm, "i");
			}
			Aggregation aggregation = null;
			if (size > 0) {

				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria), Aggregation.unwind("treatments"),
								Aggregation.lookup("treatment_services_cl", "$treatments.treatmentServiceId", "_id",
										"totalTreatmentService"),

								Aggregation.unwind("totalTreatmentService"), Aggregation.match(criteriaSecond),
								new CustomAggregationOperation(
										new Document("$group",
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
								Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			} else {
				size = 10;

				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria),
								Aggregation.unwind("treatments"),
								Aggregation.lookup("treatment_services_cl", "$treatments.treatmentServiceId", "_id",
										"totalTreatmentService"),

								Aggregation.unwind("totalTreatmentService"), Aggregation.match(criteriaSecond),
								new CustomAggregationOperation(
										new Document("$group",
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
								Aggregation.skip((long)(page) * size), Aggregation.limit(size));

			}
			AggregationResults<TreatmentServiceAnalyticResponse> aggregationResults = mongoTemplate
					.aggregate(aggregation, PatientTreatmentCollection.class, TreatmentServiceAnalyticResponse.class);
			List<TreatmentServiceAnalyticResponse> analyticMongoResponses = aggregationResults.getMappedResults();
			if (analyticMongoResponses != null && !analyticMongoResponses.isEmpty()) {
				response = new ArrayList<DoctorTreatmentAnalyticResponse>();
				DoctorTreatmentAnalyticResponse analyticResponse = new DoctorTreatmentAnalyticResponse();
				for (TreatmentServiceAnalyticResponse data : analyticMongoResponses) {
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

	@Override
	public List<TreatmentAnalyticDetail> getTreatmentAnalyticDetail(int page, int size, String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String searchTerm, String status) {
		List<TreatmentAnalyticDetail> response = null;
		try {
			Criteria criteria = null;
			Criteria criteriaSecond = new Criteria();
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
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteriaSecond.and("patient.locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteriaSecond.and("patient.hospitalId").is(new ObjectId(hospitalId));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteriaSecond.orOperator(new Criteria("patient.firstName").regex(searchTerm, "i"),
						new Criteria("patient.localPatientName").regex(searchTerm, "i"));
			}
			if (!DPDoctorUtils.anyStringEmpty(status)) {
				criteriaSecond.and("treatments.status").is(status.toUpperCase());
			}
			Aggregation aggregation = null;
			if (size > 0) {

				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("treatments"),
						Aggregation.lookup("treatment_services_cl", "$treatments.treatmentServiceId", "_id",
								"services"),
						Aggregation.unwind("services"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
						Aggregation.unwind("patient"), Aggregation.match(criteriaSecond),
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
								.append("services", new BasicDBObject("$push", "$services"))
								.append("status", new BasicDBObject("$push", "$treatments.status"))
								.append("localPatientName", new BasicDBObject("$first", "$patient.localPatientName"))
								.append("firstName", new BasicDBObject("$first", "$patient.firstName"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("doctorName", new BasicDBObject("$first", "$doctor.firstName")))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			} else {

				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("treatments"),
						Aggregation.lookup("treatment_services_cl", "$treatments.treatmentServiceId", "_id",
								"services"),
						Aggregation.unwind("services"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
						Aggregation.unwind("patient"), Aggregation.match(criteriaSecond),
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
								.append("services", new BasicDBObject("$push", "$services"))
								.append("status", new BasicDBObject("$push", "$treatments.status"))
								.append("localPatientName", new BasicDBObject("$first", "$patient.localPatientName"))
								.append("firstName", new BasicDBObject("$first", "$patient.firstName"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("doctorName", new BasicDBObject("$first", "$doctor.firstName")))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			AggregationResults<TreatmentAnalyticDetail> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientTreatmentCollection.class, TreatmentAnalyticDetail.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting Treatment analytic Detail");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting Treatment analytic Detail");
		}

		return response;
	}

	@Override
	public List<DoctorAnalyticPieChartResponse> getTreatmentAnalyticForPieChart(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate) {
		List<DoctorAnalyticPieChartResponse> response = null;
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

			criteria = criteria.and("createdTime").gte(fromTime).lte(toTime);
			criteria.and("discarded").is(false);
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("date", new BasicDBObject("$first", "$fromDate"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("count", new BasicDBObject("$first", "$doctorId"))
							.append("firstName", new BasicDBObject("$first", "$doctor.firstName"))));

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.unwind("treatments"),
					Aggregation.lookup("treatment_services_cl", "$treatments.treatmentServiceId", "_id", "services"),
					Aggregation.unwind("services"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					Aggregation.unwind("doctor"), group,
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$doctorId").append("count", new BasicDBObject("$sum", 1))
									.append("firstName", new BasicDBObject("$first", "$firstName")))));

			AggregationResults<DoctorAnalyticPieChartResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientTreatmentCollection.class, DoctorAnalyticPieChartResponse.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting Treatment analytic Detail");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting Treatment analytic Detail");
		}
		return response;
	}

	@Override
	public Integer countTreatments(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String searchTerm, String status) {
		Integer response = 0;
		try {
			Criteria criteria = new Criteria();
			Criteria criteriaSecond = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {

				criteriaSecond.orOperator(new Criteria("patient.firstName").regex(searchTerm, "i"),
						new Criteria("patient.localPatientName").regex(searchTerm, "i"));
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
			if (!DPDoctorUtils.anyStringEmpty(status)) {
				criteriaSecond.and("treatments.status").is(status.toUpperCase());
			}
			criteria.and("discarded").is(false);

			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("treatments"),
					Aggregation.lookup("treatment_services_cl", "$treatments.treatmentServiceId", "_id", "services"),
					Aggregation.unwind("services"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					Aggregation.unwind("doctor"), Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
					Aggregation.unwind("patient"), Aggregation.match(criteriaSecond),
					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id"))));

			response = mongoTemplate.aggregate(aggregation, PatientTreatmentCollection.class, TreatmentService.class)
					.getMappedResults().size();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting most Treatments count");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting most Treatment count ");
		}
		return response;
	}

	@Override
	public Integer countTreatmentService(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String searchTerm) {
		Integer response = 0;
		try {
			Criteria criteria = null;
			Criteria criteriaSecond = new Criteria();
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
				criteriaSecond.and("totalTreatmentService.name").regex(searchTerm, "i");
			}
			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("treatments"),
					Aggregation.lookup("treatment_services_cl", "treatments.treatmentServiceId", "_id",
							"totalTreatmentService"),
					Aggregation.unwind("totalTreatmentService"), Aggregation.match(criteriaSecond),
					new CustomAggregationOperation(
							new Document("$group", new BasicDBObject("_id", "$treatments.treatmentServiceId"))));

			response = mongoTemplate
					.aggregate(aggregation, PatientTreatmentCollection.class, DoctorTreatmentAnalyticResponse.class)
					.getMappedResults().size();

		} catch (Exception e) {

			e.printStackTrace();
			logger.error(e + " Error Occurred While count Treatment Service");
			throw new BusinessException(ServiceError.Unknown, " Error Occurred While count Treatment Service");
		}

		return response;
	}
}
